"""Generate optimised README screenshots under docs/screenshots/.

The originals live in assets/image/ (kept unchanged).  This script only creates
lighter, English-named copies so the README renders fast on GitHub.
"""

from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parent.parent
SRC = ROOT / "assets" / "image"
OUT = ROOT / "docs" / "screenshots"

# (source file, output file, target width or None, use 256-colour palette)
JOBS = [
    ("概念图.png", "main-menu.png", 1500, False),
    ("游戏中概念-1.png", "gameplay.png", None, True),
    ("游戏中概念-2.png", "gameplay-pause.png", None, True),
    ("结束概念图.png", "game-over.png", None, True),
]


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    for src_name, dst_name, width, quantize in JOBS:
        src_path = SRC / src_name
        image = Image.open(src_path)
        original_size = src_path.stat().st_size

        if width is not None and image.width > width:
            height = round(image.height * width / image.width)
            image = image.resize((width, height), Image.LANCZOS)

        if quantize:
            image = image.convert("RGB").quantize(colors=256, method=Image.MEDIANCUT)

        dst_path = OUT / dst_name
        image.save(dst_path, format="PNG", optimize=True)

        print(
            f"{dst_name:<20} {image.width}x{image.height}  "
            f"{original_size / 1024:8.1f} KB -> {dst_path.stat().st_size / 1024:7.1f} KB"
        )


if __name__ == "__main__":
    main()
