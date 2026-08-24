import json
from pathlib import Path


def rule_body(path: str):
    lines = Path(path).read_text(encoding="utf-8").splitlines()
    body = [line for line in lines if line and not line.startswith(("!", "#", "["))]
    if not body:
        raise SystemExit(f"empty output: {path}")
    if len(body) != len(set(body)):
        raise SystemExit(f"duplicate rules: {path}")
    if any(line.lower().startswith(("<!doctype html", "<html")) for line in body):
        raise SystemExit(f"HTML payload: {path}")
    if any("! url: http" in line for line in body):
        raise SystemExit(f"source marker joined to rule: {path}")
    return set(body)


lite = rule_body("adblock_lite.txt")
normal = rule_body("adblock.txt")
plus = rule_body("adblock_plus.txt")
if not lite < normal:
    raise SystemExit("Normal content rules must be a strict superset of Lite")
if not normal < plus:
    raise SystemExit("Plus content rules must be a strict superset of Normal")

dns_lines = Path("dns.txt").read_text(encoding="utf-8").splitlines()
dns_domains = {
    line[2:-1]
    for line in dns_lines
    if line.startswith("||") and line.endswith("^") and "*" not in line
}
if not dns_domains:
    raise SystemExit("dns.txt has no canonical domain rules")

derived = {
    "adrules_domainset.txt": {
        line[2:] for line in Path("adrules_domainset.txt").read_text(encoding="utf-8").splitlines()
        if line.startswith("+.")
    },
    "smart-dns.conf": {
        line[len("address /"):-2]
        for line in Path("smart-dns.conf").read_text(encoding="utf-8").splitlines()
        if line.startswith("address /") and line.endswith("/#")
    },
    "mosdns_adrules.txt": {
        line[len("domain:"):]
        for line in Path("mosdns_adrules.txt").read_text(encoding="utf-8").splitlines()
        if line.startswith("domain:")
    },
    "qx.conf": {
        line.split(",", 2)[1]
        for line in Path("qx.conf").read_text(encoding="utf-8").splitlines()
        if line.startswith("host-suffix,") and line.endswith(",reject")
    },
    "adrules.list": {
        line.split(",", 1)[1]
        for line in Path("adrules.list").read_text(encoding="utf-8").splitlines()
        if line.startswith("DOMAIN-SUFFIX,")
    },
}
for path, domains in derived.items():
    if domains != dns_domains:
        raise SystemExit(
            f"domain mismatch in {path}: missing={len(dns_domains - domains)}, "
            f"extra={len(domains - dns_domains)}"
        )

for path in ("adrules-singbox.json", "adrules.list.json"):
    with Path(path).open(encoding="utf-8") as stream:
        json.load(stream)

for path in ("adrules-singbox.srs", "adrules-mihomo.mrs"):
    if Path(path).stat().st_size == 0:
        raise SystemExit(f"empty binary ruleset: {path}")

print(
    f"validated content tiers: lite={len(lite)}, normal={len(normal)}, plus={len(plus)}; "
    f"dns domains={len(dns_domains)}"
)
