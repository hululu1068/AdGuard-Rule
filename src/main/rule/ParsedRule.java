package org.fordes.adg.rule;

import org.fordes.adg.rule.enums.RuleType;

public final class ParsedRule {

    private final RuleType type;
    private final String output;
    private final String deduplicationKey;

    private ParsedRule(RuleType type, String output, String deduplicationKey) {
        this.type = type;
        this.output = output;
        this.deduplicationKey = deduplicationKey;
    }

    public static ParsedRule valid(RuleType type, String output, String deduplicationKey) {
        return new ParsedRule(type, output, deduplicationKey);
    }

    public static ParsedRule invalid() {
        return new ParsedRule(RuleType.INVALID, null, null);
    }

    public RuleType getType() {
        return type;
    }

    public String getOutput() {
        return output;
    }

    public String getDeduplicationKey() {
        return deduplicationKey;
    }

    public boolean isValid() {
        return type != RuleType.INVALID;
    }
}