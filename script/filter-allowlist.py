import re
from pathlib import Path


def load_allowlist(path: Path):
    exact = set()
    patterns = []
    for raw_line in path.read_text(encoding="utf-8").splitlines():
        line = raw_line.strip()
        if not line or line.startswith("#"):
            continue
        if line.startswith("regex:"):
            patterns.append(re.compile(line.removeprefix("regex:")))
        else:
            exact.add(line.lower())
    return exact, patterns


def domain_from_rule(rule: str):
    if rule.startswith("||") and rule.endswith("^"):
        return rule[2:-1].lower()
    return None


def filter_rules(rules, exact_allowlist, regex_allowlist):
    filtered = []
    for rule in rules:
        domain = domain_from_rule(rule)
        if domain is None:
            filtered.append(rule)
            continue
        if domain in exact_allowlist:
            continue
        if any(pattern.fullmatch(domain) for pattern in regex_allowlist):
            continue
        filtered.append(rule)
    return filtered


def main():
    dns_path = Path("dns.txt")
    rules = [line.strip() for line in dns_path.read_text(encoding="utf-8").splitlines() if line.strip()]
    exact_allowlist, regex_allowlist = load_allowlist(Path("script/allowlist.txt"))
    filtered = filter_rules(rules, exact_allowlist, regex_allowlist)
    dns_path.write_text("\n".join(filtered) + "\n", encoding="utf-8")


if __name__ == "__main__":
    main()
