package org.fordes.adg.rule;

import org.fordes.adg.rule.enums.RuleType;

import java.net.IDN;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RuleParser {

    private static final Pattern HOSTS_PATTERN = Pattern.compile(
            "^(\\S+)\\s+(.+)$");
    private static final Pattern DOMAIN_PATTERN = Pattern.compile(
            "^(?=.{1,253}$)(?:[a-zA-Z0-9_](?:[a-zA-Z0-9_-]{0,61}[a-zA-Z0-9_])?\\.)+[a-zA-Z0-9_](?:[a-zA-Z0-9_-]{0,61}[a-zA-Z0-9_])?$");
    private static final Pattern STRICT_REGEX_PATTERN = Pattern.compile("^/(?:\\\\.|[^/\\\\])+/$");
    private static final Pattern TRAILING_COMMENT_PATTERN = Pattern.compile("\\s+#.*$");
    private static final Set<String> DNS_MODIFIERS = new HashSet<>(Arrays.asList(
            "app", "badfilter", "client", "ctag", "denyallow", "dnsrewrite", "dnstype",
            "important", "network"
    ));
    private static final String[] COSMETIC_MARKERS = {
            "##", "#@#", "#$#", "#%#", "#?#", "#@$#", "#@%#", "#@?#"
    };

    private RuleParser() {
    }

    public static ParsedRule parse(String line) {
        if (line == null) {
            return ParsedRule.invalid();
        }

        String rule = line.trim();
        if (rule.isEmpty() || rule.startsWith("!") || rule.startsWith("[")) {
            return ParsedRule.invalid();
        }

        if (isCosmeticRule(rule)) {
            return ParsedRule.valid(RuleType.MODIFY, rule, rule);
        }

        if (rule.startsWith("#")) {
            return ParsedRule.invalid();
        }

        ParsedRule hostsRule = parseHosts(rule);
        if (hostsRule != null) {
            return hostsRule;
        }

        ParsedRule domainRule = parseDomain(rule);
        if (domainRule != null) {
            return domainRule;
        }

        if (STRICT_REGEX_PATTERN.matcher(rule).matches()) {
            return ParsedRule.valid(RuleType.REGEX, rule, rule);
        }

        if (isDnsFilterRule(rule) && isCanonicalDomainFilter(rule)) {
            RuleType type = rule.startsWith("@@")
                    ? RuleType.DNS_EXCEPTION
                    : RuleType.DNS_FILTER;
            return ParsedRule.valid(type, rule, rule);
        }

        if (isAdGuardClientRule(rule)) {
            return ParsedRule.valid(RuleType.MODIFY, rule, rule);
        }

        return ParsedRule.invalid();
    }

    private static ParsedRule parseHosts(String rule) {
        Matcher matcher = HOSTS_PATTERN.matcher(rule);
        if (!matcher.matches() || !isValidIpAddress(matcher.group(1))) {
            return null;
        }

        String hostPart = removeTrailingComment(matcher.group(2));
        String[] hosts = hostPart.split("\\s+");
        StringBuilder normalizedHosts = new StringBuilder();
        for (String host : hosts) {
            String normalized = normalizeHost(host);
            if (normalized == null) {
                return null;
            }
            if (normalizedHosts.length() > 0) {
                normalizedHosts.append(' ');
            }
            normalizedHosts.append(normalized);
        }
        if (normalizedHosts.length() == 0) {
            return null;
        }

        String output = matcher.group(1).toLowerCase(Locale.ROOT) + " " + normalizedHosts;
        return ParsedRule.valid(RuleType.HOSTS, output, output);
    }

    private static ParsedRule parseDomain(String rule) {
        String candidate = removeTrailingComment(rule);
        String domain = normalizeDomain(candidate);
        if (domain == null) {
            return null;
        }
        String output = "0.0.0.0 " + domain;
        return ParsedRule.valid(RuleType.HOSTS, output, output);
    }

    private static String normalizeDomain(String candidate) {
        String host = normalizeHost(candidate);
        return host != null && host.contains(".") ? host : null;
    }

    private static String normalizeHost(String candidate) {
        try {
            String ascii = IDN.toASCII(candidate).toLowerCase(Locale.ROOT);
            if (!DOMAIN_PATTERN.matcher(ascii).matches()
                    && !ascii.matches("[a-zA-Z0-9_](?:[a-zA-Z0-9_-]{0,61}[a-zA-Z0-9_])?")) {
                return null;
            }
            return ascii;
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private static String removeTrailingComment(String rule) {
        return TRAILING_COMMENT_PATTERN.matcher(rule).replaceFirst("").trim();
    }

    private static boolean isDnsFilterRule(String rule) {
        String candidate = rule.startsWith("@@") ? rule.substring(2) : rule;
        int modifierIndex = candidate.indexOf('$');
        String pattern = modifierIndex >= 0 ? candidate.substring(0, modifierIndex) : candidate;
        String modifiers = modifierIndex >= 0 ? candidate.substring(modifierIndex + 1) : "";

        if (pattern.isEmpty() || pattern.contains("/") || pattern.contains(" ")
                || pattern.contains("://")) {
            return false;
        }

        String domainPattern = pattern;
        if (domainPattern.startsWith("||")) {
            domainPattern = domainPattern.substring(2);
        } else if (domainPattern.startsWith("|")) {
            domainPattern = domainPattern.substring(1);
        }
        if (domainPattern.endsWith("|")) {
            domainPattern = domainPattern.substring(0, domainPattern.length() - 1);
        }
        if (domainPattern.endsWith("^")) {
            domainPattern = domainPattern.substring(0, domainPattern.length() - 1);
        }
        if (domainPattern.isEmpty()) {
            return false;
        }
        if (!domainPattern.matches("[\\p{L}\\p{N}*._-]+")) {
            return false;
        }

        if (modifierIndex < 0) {
            return true;
        }
        if (modifiers.isEmpty()) {
            return false;
        }

        for (String modifier : modifiers.split(",")) {
            String name = modifier;
            if (name.startsWith("~")) {
                name = name.substring(1);
            }
            int valueIndex = name.indexOf('=');
            if (valueIndex >= 0) {
                name = name.substring(0, valueIndex);
            }
            if (!DNS_MODIFIERS.contains(name.toLowerCase(Locale.ROOT))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isCanonicalDomainFilter(String rule) {
        String candidate = rule.startsWith("@@") ? rule.substring(2) : rule;
        int modifierIndex = candidate.indexOf('$');
        String pattern = modifierIndex >= 0 ? candidate.substring(0, modifierIndex) : candidate;
        return pattern.startsWith("||");
    }

    private static boolean isAdGuardClientRule(String rule) {
        return !rule.matches(".*\\s+.*");
    }

    private static boolean isCosmeticRule(String value) {
        for (String marker : COSMETIC_MARKERS) {
            int markerIndex = value.indexOf(marker);
            if (markerIndex < 0) {
                continue;
            }
            String prefix = value.substring(0, markerIndex);
            if (!prefix.startsWith("#") && !prefix.contains("/")) {
                return true;
            }
        }
        return false;
    }

    private static boolean isValidIpAddress(String value) {
        if (value.contains(":")) {
            try {
                return InetAddress.getByName(value).getHostAddress().contains(":");
            } catch (UnknownHostException exception) {
                return false;
            }
        }
        String[] octets = value.split("\\.");
        if (octets.length != 4) {
            return false;
        }
        for (String octet : octets) {
            try {
                int number = Integer.parseInt(octet);
                if (number > 255 || number < 0) {
                    return false;
                }
            } catch (NumberFormatException exception) {
                return false;
            }
        }
        return true;
    }
}