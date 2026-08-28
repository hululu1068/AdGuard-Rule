package org.fordes.adg.rule.thread;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class RemoteRuleThread extends AbstractRuleThread {

    public RemoteRuleThread(String ruleUrl) {
        super(ruleUrl);
    }

    @Override
    protected InputStream getContentStream() throws IOException {
        try (HttpResponse response = HttpRequest.get(getRuleUrl())
                    .setFollowRedirects(true)
                    .timeout(20000)
                    .execute()) {
            if (!response.isOk()) {
                throw new IOException("HTTP " + response.getStatus());
            }
            String charset = response.charset();
            if (charset != null && !charset.trim().isEmpty()) {
                setCharset(Charset.forName(charset));
            }
            return new ByteArrayInputStream(response.bodyBytes());
        }
    }

    @Override
    protected boolean isRemote() {
        return true;
    }
}
