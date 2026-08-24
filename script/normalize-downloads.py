import sys
from pathlib import Path


root = Path(sys.argv[1])
for path in root.rglob("*"):
    if not path.is_file() or path.suffix == ".tmp":
        continue
    content = path.read_bytes().replace(b"\r\n", b"\n").replace(b"\r", b"\n")
    path.write_bytes(content.rstrip(b"\n") + b"\n")
