package org.fordes.adg.rule;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fordes.adg.rule.config.OutputConfig;
import org.fordes.adg.rule.config.RuleConfig;
import org.fordes.adg.rule.thread.AbstractRuleThread;
import org.fordes.adg.rule.thread.LocalRuleThread;
import org.fordes.adg.rule.thread.RemoteRuleThread;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
public class AdgRuleApplication implements ApplicationRunner {

    private final static int N = Runtime.getRuntime().availableProcessors();
    private static final long TASK_TIMEOUT_SECONDS = 60;

    private final RuleConfig ruleConfig;

    private final OutputConfig outputConfig;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        TimeInterval interval = DateUtil.timer();
        List<AbstractRuleThread> tasks = createTasks();
        if (tasks.isEmpty()) {
            throw new IllegalStateException("没有配置任何规则来源");
        }

        if (outputConfig.getFiles() == null || outputConfig.getFiles().isEmpty()) {
            throw new IllegalStateException("没有配置任何输出文件");
        }
        if (StrUtil.isBlank(outputConfig.getPath())) {
            throw new IllegalStateException("没有配置输出目录");
        }
        Path outputPath = Util.resolvePath(outputConfig.getPath());
        validateLocalInputs(tasks, outputPath);

        int threadCount = Math.max(1, Math.min(tasks.size(), 2 * N));
        AtomicInteger threadNumber = new AtomicInteger();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount, runnable -> {
            Thread thread = new Thread(runnable,
                    "rule-source-" + threadNumber.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        });
        RuleAggregator aggregator = new RuleAggregator();

        try {
            List<Future<SourceResult>> futures = executor.invokeAll(
                    tasks, TASK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            executor.shutdown();

            for (int index = 0; index < futures.size(); index++) {
                Future<SourceResult> future = futures.get(index);
                AbstractRuleThread task = tasks.get(index);
                if (future.isCancelled()) {
                    if (task instanceof RemoteRuleThread) {
                        log.warn("跳过处理超时的远程规则源: {}", task.getRuleUrl());
                        continue;
                    }
                    throw new IllegalStateException("本地规则处理超时: " + task.getRuleUrl());
                }
                SourceResult result = future.get();
                if (result.isSuccessful()) {
                    aggregator.add(result);
                } else if (result.isRemote()) {
                    log.warn("跳过失败的远程规则源: {} ({})",
                            result.getSource(), result.getErrorMessage());
                } else {
                    throw new IllegalStateException("本地规则读取失败: "
                            + result.getSource() + " (" + result.getErrorMessage() + ")");
                }
            }

            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                log.warn("部分已取消的规则任务仍未结束，将忽略其结果");
            }
        } finally {
            executor.shutdownNow();
        }

        RuleOutputWriter.write(outputPath, outputConfig.getFiles(), aggregator);
        log.info("Done! {} ms", interval.intervalMs());
    }

    private List<AbstractRuleThread> createTasks() {
        List<AbstractRuleThread> tasks = new ArrayList<>();

        for (String remote : safeList(ruleConfig.getRemote())) {
            if (StrUtil.isBlank(remote)) {
                continue;
            }
            String normalized = remote.trim();
            URI uri = URI.create(normalized);
            if (!"http".equalsIgnoreCase(uri.getScheme())
                    && !"https".equalsIgnoreCase(uri.getScheme())) {
                throw new IllegalArgumentException("远程规则仅支持 HTTP/HTTPS: " + remote);
            }
            tasks.add(new RemoteRuleThread(normalized));
        }

        for (String local : safeList(ruleConfig.getLocal())) {
            if (StrUtil.isBlank(local)) {
                continue;
            }
            String normalized = FileUtil.normalize(local);
            Path localPath = FileUtil.isAbsolutePath(normalized)
                    ? Util.resolvePath(normalized)
                    : Util.resolvePath(Constant.LOCAL_RULE_SUFFIX + File.separator + normalized);
            tasks.add(new LocalRuleThread(localPath.toString()));
        }

        return tasks;
    }

    private static List<String> safeList(List<String> values) {
        return values == null ? Collections.emptyList() : values;
    }

    private void validateLocalInputs(List<AbstractRuleThread> tasks, Path outputPath) throws IOException {
        for (AbstractRuleThread task : tasks) {
            if (!(task instanceof LocalRuleThread)) {
                continue;
            }
            Path input = realPathIfExists(Util.resolvePath(task.getRuleUrl()));
            for (String outputName : outputConfig.getFiles().keySet()) {
                Path output = realPathIfExists(
                        RuleOutputWriter.resolveOutputFile(outputPath, outputName));
                if (input.equals(output)) {
                    throw new IllegalStateException("本地输入不能同时作为输出文件: " + input);
                }
            }
        }
    }

    private static Path realPathIfExists(Path path) throws IOException {
        Path normalized = path.toAbsolutePath().normalize();
        return Files.exists(normalized) ? normalized.toRealPath() : normalized;
    }

    public static void main(String[] args) {
        SpringApplication.run(AdgRuleApplication.class, args);
    }
}
