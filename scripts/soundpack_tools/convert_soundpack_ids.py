import json
import sys

if len(sys.argv) != 2:
    sys.exit('''Usage: {} DATABASE_JSON_FILE

DATABASE_JSON_FILE will have references to numerical block IDs replaced with
block names.

WARNING: the replacement happens in-place! Make sure you have a backup first.
'''.format(sys.argv[0]))

jsonPath = sys.argv[1]

data = json.load(open(jsonPath, "r", encoding="utf8"))
ids = json.load(open("ids.json", "r"))

def to_block_name(id):
    if id in ids:
        return ids[id]
    else:
        raise Exception("Unknown ID: " + id)

found = 0

for k, v in data["condition"].items():
    if (v.get("sheet") or "").startswith("scan_") and (v.get("index") or "").isnumeric():
        v["index"] = to_block_name(v["index"])
        found += 1

print("Converted", found, "conditions.")

if found > 0:
    json.dump(data, open(jsonPath, "w", encoding="utf8"), indent=2)