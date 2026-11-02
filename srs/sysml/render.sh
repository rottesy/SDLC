#!/bin/sh
# Рендер SVG-диаграмм в PNG (масштаб 2x) через headless Chrome
cd "$(dirname "$0")"
python3 diagrams.py
for f in out/*.svg; do
  n=$(basename "$f" .svg)
  w=$(grep -o 'width="[0-9]*"' "$f" | head -1 | tr -dc 0-9)
  h=$(grep -o 'height="[0-9]*"' "$f" | head -1 | tr -dc 0-9)
  printf '<html><body style="margin:0">%s</body></html>' "$(cat "$f")" > "out/$n.html"
  google-chrome --headless=new --disable-gpu --hide-scrollbars --force-device-scale-factor=2 \
    --window-size=$w,$h --screenshot="../img/$n.png" "file://$PWD/out/$n.html" >/dev/null 2>&1
done
