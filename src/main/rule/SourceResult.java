package org.fordes.adg.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SourceResult {

    private final String source;
    private final boolean remote;
    private final boolean successful;
    private final List<ParsedRule> rules;
    private final int invalidCount;
    private final String errorMessage;

    private SourceResult(String source, boolean remote, boolean successful, List<ParsedRule> rules,
                         int invalidCount, String errorMessage) {
        this.source = source;
        this.remote = remote;
        this.successful = successful;
        this.rules = rules;
        this.invalidCount = invalidCount;
        this.errorMessage = errorMessage;
    }

    public static SourceResult success(String source, boolean remote, List<ParsedRule> rules,
                                       int invalidCount) {
        return new SourceResult(source, remote, true,
            Collections.unmodifiableList(new ArrayList<>(rules)), invalidCount, null);
    }

    public static SourceResult failure(String source, boolean remote, String errorMessage) {
        return new SourceResult(source, remote, false,
                Collections.emptyList(), 0, errorMessage);
    }

    public String getSource() {
        return source;
    }

    public boolean isRemote() {
        return remote;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public List<ParsedRule> getRules() {
        return rules;
    }

    public int getInvalidCount() {
        return invalidCount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}