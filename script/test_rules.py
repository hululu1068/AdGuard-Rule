import importlib.util
import re
import unittest
from pathlib import Path


def load_filter_module():
    path = Path(__file__).with_name("filter-allowlist.py")
    spec = importlib.util.spec_from_file_location("filter_allowlist", path)
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


filter_allowlist = load_filter_module()


class AllowlistTests(unittest.TestCase):
    def test_exact_domains_do_not_expand_dots_as_regex(self):
        rules = ["||xp.apple.com^", "||xpXapple.com^", "/^adserver\\./"]
        filtered = filter_allowlist.filter_rules(rules, {"xp.apple.com"}, [])
        self.assertEqual(["||xpXapple.com^", "/^adserver\\./"], filtered)

    def test_regex_entries_use_full_domain_matching(self):
        rules = ["||is1-ssl.mzstatic.com^", "||isx-ssl.mzstatic.com^"]
        patterns = [re.compile(r"is[0-9]-ssl\.mzstatic\.com")]
        filtered = filter_allowlist.filter_rules(rules, set(), patterns)
        self.assertEqual(["||isx-ssl.mzstatic.com^"], filtered)


if __name__ == "__main__":
    unittest.main()
