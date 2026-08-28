package org.fordes.adg.rule;

import org.fordes.adg.rule.enums.RuleType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class RuleOutputWriter {

    private static final DateTimeFormatter UPDATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private RuleOutputWriter() {
    }

    public static synchronized void write(Path outputDirectory, Map<String, List<RuleType>> files,
                                          RuleAggregator aggregator) throws IOException {
        Path outputRoot = outputDirectory.toAbsolutePath().normalize();
        Files.createDirectories(outputRoot);

        Path lockFile = outputRoot.resolve(".adg-rule.lock");
        try (FileChannel lockChannel = FileChannel.open(lockFile,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
             FileLock ignored = lockChannel.lock()) {
            writeLocked(outputRoot, files, aggregator);
        }
    }

    private static void writeLocked(Path outputRoot, Map<String, List<RuleType>> files,
                                    RuleAggregator aggregator) throws IOException {

        Path parent = outputRoot.getParent();
        if (parent == null) {
            throw new IOException("输出目录缺少父目录: " + outputRoot);
        }

        Path stagingDirectory = Files.createTempDirectory(parent, ".adg-rule-staging-");
        Path backupDirectory = Files.createTempDirectory(parent, ".adg-rule-backup-");
        Map<Path, Path> stagedTargets = new LinkedHashMap<>();
        Map<Path, Path> backups = new LinkedHashMap<>();
        List<Path> replacedTargets = new ArrayList<>();

        try {
            for (Map.Entry<String, List<RuleType>> entry : files.entrySet()) {
                if (entry.getValue() == null || entry.getValue().isEmpty()
                        || entry.getValue().contains(RuleType.INVALID)) {
                    throw new IOException("输出文件缺少合法规则类型: " + entry.getKey());
                }
                Path target = resolveOutputFile(outputRoot, entry.getKey());
                Path relative = outputRoot.relativize(target);
                Path stagedFile = stagingDirectory.resolve(relative);
                Files.createDirectories(stagedFile.getParent());
                Files.write(stagedFile, render(entry.getKey(), aggregator.getSources(),
                    aggregator.getRules(entry.getValue())));
                stagedTargets.put(target, stagedFile);

                if (Files.exists(target)) {
                    Path backup = backupDirectory.resolve(relative);
                    Files.createDirectories(backup.getParent());
                    Files.copy(target, backup, StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.COPY_ATTRIBUTES);
                    backups.put(target, backup);
                }
            }

            for (Map.Entry<Path, Path> entry : stagedTargets.entrySet()) {
                Files.createDirectories(entry.getKey().getParent());
                replacedTargets.add(entry.getKey());
                moveReplacing(entry.getValue(), entry.getKey());
            }
        } catch (IOException exception) {
            IOException rollbackFailure = rollback(replacedTargets, backups);
            if (rollbackFailure != null) {
                exception.addSuppressed(rollbackFailure);
            }
            throw exception;
        } finally {
            deleteRecursively(stagingDirectory);
            deleteRecursively(backupDirectory);
        }
    }

    static Path resolveOutputFile(Path outputRoot, String configuredName) throws IOException {
        if (configuredName == null || configuredName.trim().isEmpty()) {
            throw new IOException("输出文件名不能为空");
        }
        if (".adg-rule.lock".equals(configuredName)) {
            throw new IOException("输出文件名为保留名称: " + configuredName);
        }
        Path configuredPath = Path.of(configuredName);
        if (configuredPath.isAbsolute() || configuredPath.getNameCount() != 1) {
            throw new IOException("非法输出文件路径: " + configuredName);
        }
        Path target = outputRoot.resolve(configuredName).normalize();
        if (!target.startsWith(outputRoot) || target.equals(outputRoot)) {
            throw new IOException("非法输出文件路径: " + configuredName);
        }
        return target;
    }

    private static byte[] render(String fileName, List<String> sources, List<String> rules) {
        String title = Constant.TITLE.replace("{}", fileName);
        String update = Constant.UPDATE.replace("{}", LocalDateTime.now().format(UPDATE_TIME));
        StringBuilder content = new StringBuilder(title)
                .append(update)
                .append(Constant.EXPIRES)
                .append(Constant.REPO_URL)
                .append(Constant.OUTPUT_HEADER);
        for (String source : sources) {
            content.append("# - '").append(source).append("'\r\n");
        }
        content.append(Constant.OUTPUT_FOOTER);
        for (String rule : rules) {
            content.append(rule).append("\r\n");
        }
        return content.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static void moveReplacing(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static IOException rollback(List<Path> replacedTargets, Map<Path, Path> backups) {
        IOException failure = null;
        for (int index = replacedTargets.size() - 1; index >= 0; index--) {
            Path target = replacedTargets.get(index);
            Path backup = backups.get(target);
            try {
                if (backup == null) {
                    Files.deleteIfExists(target);
                } else {
                    Files.copy(backup, target, StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.COPY_ATTRIBUTES);
                }
            } catch (IOException exception) {
                if (failure == null) {
                    failure = new IOException("输出文件回滚失败");
                }
                failure.addSuppressed(exception);
            }
        }
        return failure;
    }

    private static void deleteRecursively(Path directory) {
        if (directory == null || !Files.exists(directory)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(directory)) {
            paths
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                        }
                    });
        } catch (IOException ignored) {
        }
    }
}
