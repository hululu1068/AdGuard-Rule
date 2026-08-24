import json

with open('domain.txt', 'r', encoding='utf-8') as file:
    domains = sorted({line.strip() for line in file if line.strip()})

rules = {
    "version": "1",
    "rules": [
        {
            "domain_suffix": domains
        }
    ]
}

with open('adrules-singbox.json', 'w', encoding='utf-8') as file:
    json.dump(rules, file, indent=4)
