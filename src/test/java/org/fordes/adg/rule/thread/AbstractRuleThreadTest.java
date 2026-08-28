package org.fordes.adg.rule.thread;

import org.fordes.adg.rule.SourceResult;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractRuleThreadTest {

    @Test
    void parsesSourceAndCountsInvalidLines() {
        AbstractRuleThread task = new TestRuleThread(
                "example.com\n! comment\n@@||allowed.example.com^$important\n", false);

        SourceResult result = task.call();

        assertTrue(result.isSuccessful());
        assertEquals(2, result.getRules().size());
        assertEquals(1, result.getInvalidCount());
    }

    @Test
    void convertsReadFailureToFailedResult() {
        AbstractRuleThread task = new FailingRuleThread();

        SourceResult result = task.call();

        assertFalse(result.isSuccessful());
        assertTrue(result.isRemote());
    }

    private static final class TestRuleThread extends AbstractRuleThread {

        private final byte[] content;
        private final boolean remote;

        private TestRuleThread(String content, boolean remote) {
            super("test");
            this.content = content.getBytes(StandardCharsets.UTF_8);
            this.remote = remote;
        }

        @Override
        protected InputStream getContentStream() {
            return new ByteArrayInputStream(content);
        }

        @Override
        protected boolean isRemote() {
            return remote;
        }
    }

    private static final class FailingRuleThread extends AbstractRuleThread {

        private FailingRuleThread() {
            super("failed");
        }

        @Override
        protected InputStream getContentStream() throws IOException {
            throw new IOException("failed");
        }

        @Override
        protected boolean isRemote() {
            return true;
        }
    }
}
