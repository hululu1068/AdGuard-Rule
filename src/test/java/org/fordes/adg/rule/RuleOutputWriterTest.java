package org.fordes.adg.rule;

import org.fordes.adg.rule.enums.RuleType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuleOutputWriterTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void writesConfiguredOutputsWithoutReplacingLocalInput() throws Exception {
        Path outputDirectory = temporaryDirectory.resolve("rule");
        Files.createDirectories(outputDirectory);
        Path localRules = outputDirectory.resolve("mylist.txt");
        Files.write(localRules, "local marker".getBytes(StandardCharsets.UTF_8));

        RuleAggregator aggregator = new RuleAggregator();
        aggregator.add(SourceResult.success("test", false, Arrays.asList(
                RuleParser.parse("plain-domain.test"),
                RuleParser.parse("@@||allowed-exception.test^$important"),
                RuleParser.parse("||blocked-filter.test^"),
                RuleParser.parse("127.0.0.1 hosts-entry.test"),
                RuleParser.parse("/^regex-target\\.test$/"),
                RuleParser.parse("cosmetic-target.test##.advert")), 0));

        Map<String, java.util.List<RuleType>> outputs = new LinkedHashMap<>();
        outputs.put("domain.txt", Arrays.asList(RuleType.DOMAIN));
        outputs.put("adgh.txt", Arrays.asList(RuleType.DNS_EXCEPTION, RuleType.DOMAIN,
                RuleType.DNS_FILTER, RuleType.REGEX));
        outputs.put("all.txt", Arrays.asList(RuleType.DNS_EXCEPTION, RuleType.DOMAIN,
                RuleType.DNS_FILTER, RuleType.HOSTS, RuleType.REGEX, RuleType.MODIFY));

        RuleOutputWriter.write(outputDirectory, outputs, aggregator);

        String domain = new String(Files.readAllBytes(outputDirectory.resolve("domain.txt")),
                StandardCharsets.UTF_8);
        String adgh = new String(Files.readAllBytes(outputDirectory.resolve("adgh.txt")),
                StandardCharsets.UTF_8);
        String all = new String(Files.readAllBytes(outputDirectory.resolve("all.txt")),
                StandardCharsets.UTF_8);

        assertTrue(domain.contains("\r\nplain-domain.test\r\n"));
        assertFalse(domain.contains("@@||"));
        assertTrue(adgh.contains("@@||allowed-exception.test^$important"));
        assertOrdered(adgh,
                "@@||allowed-exception.test^$important",
                "plain-domain.test",
                "||blocked-filter.test^",
                "/^regex-target\\.test$/");
        assertOrdered(all,
                "@@||allowed-exception.test^$important",
                "plain-domain.test",
                "||blocked-filter.test^",
                "127.0.0.1 hosts-entry.test",
                "/^regex-target\\.test$/",
                "cosmetic-target.test##.advert");
        assertEquals("local marker", new String(Files.readAllBytes(localRules),
                StandardCharsets.UTF_8));
    }

    @Test
    void rejectsOutputOutsideConfiguredDirectory() throws Exception {
        Path outputDirectory = temporaryDirectory.resolve("rule");
        RuleAggregator aggregator = new RuleAggregator();
        Map<String, java.util.List<RuleType>> outputs = new LinkedHashMap<>();
        outputs.put("../outside.txt", Arrays.asList(RuleType.DOMAIN));

        assertThrows(java.io.IOException.class,
                () -> RuleOutputWriter.write(outputDirectory, outputs, aggregator));
        assertFalse(Files.exists(temporaryDirectory.resolve("outside.txt")));
    }

    @Test
    void keepsConcurrentOutputBatchesConsistent() throws Exception {
        Path outputDirectory = temporaryDirectory.resolve("rule");
        Map<String, java.util.List<RuleType>> outputs = new LinkedHashMap<>();
        outputs.put("first.txt", Arrays.asList(RuleType.DOMAIN));
        outputs.put("second.txt", Arrays.asList(RuleType.DOMAIN));

        RuleAggregator firstBatch = new RuleAggregator();
        firstBatch.add(SourceResult.success("first", true,
                Arrays.asList(RuleParser.parse("first.example.com")), 0));
        RuleAggregator secondBatch = new RuleAggregator();
        secondBatch.add(SourceResult.success("second", true,
                Arrays.asList(RuleParser.parse("second.example.com")), 0));

        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
                        Future<?> first = executor.submit(() -> {
                                RuleOutputWriter.write(outputDirectory, outputs, firstBatch);
                                return null;
                        });
                        Future<?> second = executor.submit(() -> {
                                RuleOutputWriter.write(outputDirectory, outputs, secondBatch);
                                return null;
                        });
            first.get();
            second.get();
        } finally {
            executor.shutdownNow();
        }

        String firstFile = new String(Files.readAllBytes(outputDirectory.resolve("first.txt")),
                StandardCharsets.UTF_8);
        String secondFile = new String(Files.readAllBytes(outputDirectory.resolve("second.txt")),
                StandardCharsets.UTF_8);
        assertEquals(firstFile.contains("first.example.com"),
                secondFile.contains("first.example.com"));
        assertEquals(firstFile.contains("second.example.com"),
                secondFile.contains("second.example.com"));
    }

        private static void assertOrdered(String content, String... rules) {
                int previous = -1;
                for (String rule : rules) {
                        int current = content.indexOf(rule);
                        assertTrue(current > previous, "规则顺序错误: " + rule);
                        previous = current;
                }
        }
}
