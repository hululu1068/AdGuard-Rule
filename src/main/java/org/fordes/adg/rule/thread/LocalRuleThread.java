package org.fordes.adg.rule.thread;

import cn.hutool.core.io.FileUtil;

import java.io.InputStream;

/**
 * 本地规则处理
 *
 * @author ChengFengsheng on 2022/7/7
 */
public class LocalRuleThread extends AbstractRuleThread {

    public LocalRuleThread(String ruleUrl) {
        super(ruleUrl);
    }

    @Override
    protected InputStream getContentStream() {
        return FileUtil.getInputStream(getRuleUrl());
    }

    @Override
    protected boolean isRemote() {
        return false;
    }
}
