#!/usr/bin/env python3
"""Create a Loom Tiny v2 mapping file with named identifiers equal to intermediary.

The checked-in Java sources were decompiled in the intermediary namespace.
Loom's remapping build expects a named namespace, so this script creates one
that is an identity copy of intermediary. No Java source identifiers change.
"""

from __future__ import annotations

import io
import urllib.request
from pathlib import Path
from zipfile import ZipFile

MAPPINGS_URL = (
    "https://raw.githubusercontent.com/FabricMC/intermediary/"
    "c5e588231ea76aee3cae10e5a56febe46d892709/mappings/1.21.11.tiny"
)
EXPECTED_HEADER = "v1\tofficial\tintermediary"
OUTPUT = Path("build/mappings/intermediary-as-named.tiny")


def main() -> None:
    request = urllib.request.Request(
        MAPPINGS_URL,
        headers={"User-Agent": "SpotifyCraft-build/1.0"},
    )
    with urllib.request.urlopen(request, timeout=60) as response:
        raw = response.read().decode("utf-8")

    lines = raw.splitlines()
    if not lines or lines[0] != EXPECTED_HEADER:
        raise RuntimeError(
            "Unexpected Intermediary mapping format; refusing to generate "
            "an invalid Loom mapping file."
        )

    converted = ["tiny\t2\t0\tofficial\tintermediary\tnamed"]
    current_class = None
    class_count = 0
    member_count = 0

    for line_number, line in enumerate(lines[1:], start=2):
        if not line:
            continue

        parts = line.split("\t")
        kind = parts[0]

        if kind == "CLASS":
            if len(parts) != 3:
                raise RuntimeError(f"Malformed CLASS mapping on line {line_number}")
            official_name, intermediary_name = parts[1], parts[2]
            current_class = official_name
            converted.append(
                f"c\t{official_name}\t{intermediary_name}\t{intermediary_name}"
            )
            class_count += 1
            continue

        if kind in {"FIELD", "METHOD"}:
            if len(parts) != 5:
                raise RuntimeError(
                    f"Malformed {kind} mapping on line {line_number}"
                )

            owner, descriptor, official_name, intermediary_name = parts[1:]
            if owner != current_class:
                raise RuntimeError(
                    f"Unexpected owner on line {line_number}: "
                    f"{owner!r} (current class: {current_class!r})"
                )

            tiny_kind = "f" if kind == "FIELD" else "m"
            converted.append(
                f"\t{tiny_kind}\t{descriptor}\t{official_name}\t"
                f"{intermediary_name}\t{intermediary_name}"
            )
            member_count += 1
            continue

        raise RuntimeError(
            f"Unsupported Intermediary record {kind!r} on line {line_number}"
        )

    if class_count == 0 or member_count == 0:
        raise RuntimeError("The Intermediary mapping file was unexpectedly empty.")

    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    OUTPUT.write_text("\n".join(converted) + "\n", encoding="utf-8")
    print(
        f"Wrote {OUTPUT}: {class_count:,} classes and "
        f"{member_count:,} fields/methods; named mirrors intermediary."
    )


if __name__ == "__main__":
    main()
