package org.fordes.adg.rule.thread;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.fordes.adg.rule.ParsedRule;
import org.fordes.adg.rule.RuleParser;
import org.fordes.adg.rule.SourceResult;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 规则处理线程抽象
 *
 * @author ChengFengsheng on 2022/7/7
 */
@Slf4j
public abstract class AbstractRuleThread implements Callable<SourceResult> {

    private final String ruleUrl;

    protected AbstractRuleThread(String ruleUrl) {
        this.ruleUrl = ruleUrl;
    }

    private Charset charset = StandardCharsets.UTF_8;

    protected abstract InputStream getContentStream() throws Exception;

    protected abstract boolean isRemote();

    @Override
    public SourceResult call() {
        TimeInterval interval = DateUtil.timer();
        List<ParsedRule> rules = new ArrayList<>();
        int invalidCount = 0;

        try (InputStream inputStream = getContentStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ParsedRule rule = RuleParser.parse(line);
                if (rule.isValid()) {
                    rules.add(rule);
                } else {
                    invalidCount++;
                }
            }

            log.info("规则<{}> 耗时 => {} ms 有效数 => {} 无效数 => {}",
                    ruleUrl, interval.intervalMs(), rules.size(), invalidCount);
            return SourceResult.success(ruleUrl, isRemote(), rules, invalidCount);
        } catch (Exception e) {
            log.error(ExceptionUtil.stacktraceToString(e));
            return SourceResult.failure(ruleUrl, isRemote(), e.getMessage());
        }
    }

    public String getRuleUrl() {
        return ruleUrl;
    }

    protected void setCharset(Charset charset) {
        if (charset != null) {
            this.charset = charset;
        }
    }
}
