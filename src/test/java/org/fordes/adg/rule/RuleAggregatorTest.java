package org.fordes.adg.rule;

import org.fordes.adg.rule.enums.RuleType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RuleAggregatorTest {

    @Test
    void deduplicatesNormalizedRulesAndPreservesSourceOrder() {
        RuleAggregator aggregator = new RuleAggregator();
        aggregator.add(SourceResult.success("first", true, Arrays.asList(
                RuleParser.parse("Example.com"),
                RuleParser.parse("||ads.example.com^")), 0));
        aggregator.add(SourceResult.success("second", true, Arrays.asList(
                RuleParser.parse("example.com"),
                RuleParser.parse("||tracker.example.com^")), 0));

        assertEquals(Collections.singletonList("example.com"),
                aggregator.getRules(Collections.singletonList(RuleType.DOMAIN)));
        assertEquals(Arrays.asList("||ads.example.com^", "||tracker.example.com^"),
                aggregator.getRules(Collections.singletonList(RuleType.DNS_FILTER)));
    }
}
