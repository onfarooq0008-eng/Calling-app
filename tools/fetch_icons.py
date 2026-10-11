#!/usr/bin/env python3
"""Fetch Material Symbols SVGs (Apache-2.0) from google/material-design-icons and
convert them into Android VectorDrawable XML files (24dp viewport)."""
import base64
import json
import os
import re
import sys
import urllib.request
import xml.etree.ElementTree as ET

PROJECT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
# Default output: the app's drawable folder (pass a different folder as argument 1 if you like).
OUT_DIR = sys.argv[1] if len(sys.argv) > 1 else os.path.join(
    PROJECT, "app", "src", "main", "res", "drawable")
API = "https://api.github.com/repos/google/material-design-icons/contents/symbols/web/{name}/{style}/{file}?ref=master"

# (icon name, style, output android drawable name)
ICONS = [
    # name,             style,                       output
    ("casino",          "materialsymbolsrounded",    "ic_dice_filled"),
    ("casino",          "materialsymbolsoutlined",   "ic_dice_outlined"),
    ("history",         "materialsymbolsrounded",    "ic_history_filled"),
    ("history",         "materialsymbolsoutlined",   "ic_history_outlined"),
    ("settings",        "materialsymbolsrounded",    "ic_settings_filled"),
    ("settings",        "materialsymbolsoutlined",   "ic_settings_outlined"),
    ("info",            "materialsymbolsrounded",    "ic_info"),
    ("delete",          "materialsymbolsrounded",    "ic_delete"),
    ("volume_up",       "materialsymbolsrounded",    "ic_sound_on"),
    ("volume_off",      "materialsymbolsrounded",    "ic_sound_off"),
    ("vibration",       "materialsymbolsrounded",    "ic_vibration"),
    ("brightness_auto", "materialsymbolsrounded",    "ic_theme_system"),
    ("light_mode",      "materialsymbolsrounded",    "ic_theme_light"),
    ("dark_mode",       "materialsymbolsrounded",    "ic_theme_dark"),
    ("bar_chart",       "materialsymbolsrounded",    "ic_bar_chart"),
    ("check",           "materialsymbolsrounded",    "ic_check"),
]


def fetch(name: str, style: str, filled: bool) -> str:
    suffix = "_fill1_24px.svg" if filled else "_24px.svg"
    url = API.format(name=name, style=style, file=name + suffix)
    req = urllib.request.Request(url, headers={
        "Accept": "application/vnd.github.raw",
        "User-Agent": "diceroll-build",
    })
    with urllib.request.urlopen(req, timeout=60) as r:
        return r.read().decode("utf-8")


def svg_to_vector(svg: str, drawable_name: str) -> str:
    root = ET.fromstring(svg)
    paths = []
    for el in root.iter():
        tag = el.tag.split("}")[-1]
        if tag != "path":
            continue
        d = el.get("d")
        if not d:
            continue
        fill_type = el.get("fill-rule") or el.get("fillRule") or ""
        opacity = el.get("opacity") or el.get("fill-opacity")
        extra = ""
        if fill_type.lower() == "evenodd":
            extra += ' android:fillType="evenOdd"'
        if opacity and float(opacity) < 1.0:
            extra += ' android:fillAlpha="%s"' % opacity
        cleaned = re.sub(r"\s+", " ", d).strip()
        paths.append('    <path\n        android:fillColor="#FFFFFFFF"%s\n        android:pathData="%s" />' % (extra, cleaned))
    if not paths:
        raise ValueError("no <path> found in " + drawable_name)
    return ('<?xml version="1.0" encoding="utf-8"?>\n'
            '<!-- Material Symbols (Apache License 2.0) - recoloured/tinted by the app. -->\n'
            '<vector xmlns:android="http://schemas.android.com/apk/res/android"\n'
            '    android:width="24dp"\n'
            '    android:height="24dp"\n'
            '    android:viewportWidth="24"\n'
            '    android:viewportHeight="24">\n'
            + "\n".join(paths) + "\n</vector>\n")


def main() -> None:
    os.makedirs(OUT_DIR, exist_ok=True)
    for name, style, out in ICONS:
        filled = not out.endswith("outlined")
        try:
            svg = fetch(name, style, filled)
            xml = svg_to_vector(svg, out)
        except Exception as exc:  # noqa: BLE001
            print("FAILED %-18s %s" % (out, exc))
            continue
        path = os.path.join(OUT_DIR, out + ".xml")
        with open(path, "w", encoding="utf-8") as fh:
            fh.write(xml)
        print("ok  %-22s %6d bytes" % (out, len(xml)))


if __name__ == "__main__":
    main()
