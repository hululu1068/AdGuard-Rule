package org.fordes.adg.rule;

import org.fordes.adg.rule.enums.RuleType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RuleParserTest {

    @ParameterizedTest
    @MethodSource("rules")
    void classifiesRules(String input, RuleType expectedType, String expectedOutput) {
        ParsedRule result = RuleParser.parse(input);

        assertEquals(expectedType, result.getType());
        assertEquals(expectedOutput, result.getOutput());
    }

    private static Stream<Arguments> rules() {
        return Stream.of(
                Arguments.of("", RuleType.INVALID, null),
                Arguments.of("! comment", RuleType.INVALID, null),
                Arguments.of("# comment", RuleType.INVALID, null),
                Arguments.of("[Adblock Plus 2.0]", RuleType.INVALID, null),
                Arguments.of("Example.COM", RuleType.DOMAIN, "example.com"),
                Arguments.of("example.com # trailing comment", RuleType.DOMAIN, "example.com"),
                Arguments.of("0.0.0.0   example.com", RuleType.HOSTS, "0.0.0.0 example.com"),
                Arguments.of("127.0.0.1 example.com # comment", RuleType.HOSTS, "127.0.0.1 example.com"),
                Arguments.of("::1 ipv6.example.com", RuleType.HOSTS, "::1 ipv6.example.com"),
                Arguments.of("::1 localhost ip6-loopback", RuleType.HOSTS,
                        "::1 localhost ip6-loopback"),
                Arguments.of("999.0.0.1 example.com", RuleType.INVALID, null),
                Arguments.of("/^ads\\d+\\.example\\.com$/", RuleType.REGEX, "/^ads\\d+\\.example\\.com$/"),
                Arguments.of("||example.com^", RuleType.DNS_FILTER, "||example.com^"),
                Arguments.of("@@||example.com^$important", RuleType.DNS_EXCEPTION,
                        "@@||example.com^$important"),
                Arguments.of("||example.com^$dnstype=AAAA|HTTPS", RuleType.DNS_FILTER,
                        "||example.com^$dnstype=AAAA|HTTPS"),
                Arguments.of("||example.com^$", RuleType.MODIFY, "||example.com^$"),
                Arguments.of("example.com##.advert", RuleType.MODIFY, "example.com##.advert"),
                Arguments.of("##.global-advert", RuleType.MODIFY, "##.global-advert"),
                Arguments.of("||example.com^$popup", RuleType.MODIFY, "||example.com^$popup"),
                Arguments.of("https://example.com/advert.js", RuleType.MODIFY,
                        "https://example.com/advert.js"),
                Arguments.of("_ad&callback=", RuleType.MODIFY, "_ad&callback="),
                Arguments.of("MRelateFeedAd", RuleType.DNS_FILTER, "MRelateFeedAd"),
                Arguments.of("ad*", RuleType.DNS_FILTER, "ad*"),
                Arguments.of("not a rule", RuleType.INVALID, null)
        );
    }
}
