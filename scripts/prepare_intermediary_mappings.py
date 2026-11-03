#!/usr/bin/env python3
"""Create a Loom Tiny v2 mapping file whose named namespace mirrors intermediary.

The checked-in Java sources were decompiled in the intermediary namespace.
Loom expects a named source namespace, so this adds an identity alias without
rewriting the decompiled Java sources.
"""

from __future__ import annotations

import io
import urllib.request
from pathlib import Path
from zipfile import ZipFile

MAPPINGS_URL = (
    "https://maven.fabricmc.net/net/fabricmc/intermediary/1.21.11/"
    "intermediary-1.21.11-v2.jar"
)
MAPPINGS_ENTRY = "mappings/mappings.tiny"
EXPECTED_HEADER = ["tiny", "2", "0", "official", "intermediary"]
OUTPUT = Path("build/mappings/intermediary-as-named.tiny")


def main() -> None:
    request = urllib.request.Request(
        MAPPINGS_URL,
        headers={"User-Agent": "SpotifyCraft-build/1.0"},
    )
    with urllib.request.urlopen(request, timeout=60) as response:
        archive_bytes = response.read()

    with ZipFile(io.BytesIO(archive_bytes)) as archive:
        try:
            raw = archive.read(MAPPINGS_ENTRY).decode("utf-8")
        except KeyError as exc:
            raise RuntimeError(
                f"Fabric Intermediary artifact does not contain {MAPPINGS_ENTRY}"
            ) from exc

    lines = raw.splitlines()
    if not lines:
        raise RuntimeError("Fabric Intermediary mapping file is empty.")

    header = lines[0].split("\t")
    if header != EXPECTED_HEADER:
        raise RuntimeError(
            "Unexpected Intermediary mapping namespaces: "
            f"expected {EXPECTED_HEADER!r}, got {header!r}"
        )

    converted = ["\t".join(header + ["named"])]
    class_count = 0
    member_count = 0

    for line_number, line in enumerate(lines[1:], start=2):
        if not line:
            continue

        # Tiny v2 class rows contain one name per namespace.
        if line.startswith("c\t"):
            parts = line.split("\t")
            if len(parts) != 3:
                raise RuntimeError(
                    f"Malformed class mapping on line {line_number}: {line!r}"
                )
            # The named class is deliberately identical to its intermediary name.
            converted.append(line + "\t" + parts[2])
            class_count += 1
            continue

        # Field and method rows are indented one level and also contain
        # one name per namespace. Add the intermediary name as named.
        if line.startswith("\t"):
            parts = line.split("\t")
            if len(parts) != 5 or parts[1] not in {"f", "m"}:
                raise RuntimeError(
                    f"Unsupported Tiny v2 member mapping on line {line_number}: "
                    f"{line!r}"
                )
            converted.append(line + "\t" + parts[4])
            member_count += 1
            continue

        raise RuntimeError(
            f"Unsupported Tiny v2 record on line {line_number}: {line!r}"
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
