#!/usr/bin/env python3
"""Generate the dice vector drawables used by the launcher icon and splash screen."""
import os
import sys

PROJECT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
# Default output: the app's drawable folder (pass a different folder as argument 1 if you like).
OUT = sys.argv[1] if len(sys.argv) > 1 else os.path.join(
    PROJECT, "app", "src", "main", "res", "drawable")

FACE = (255, 255, 255)
PIP = (22, 24, 31)
SIDE = (159, 176, 216)

# Pip positions for a "5" face, in unit coordinates inside the dice square.
FACE_FIVE = [(0.27, 0.27), (0.73, 0.27), (0.5, 0.5), (0.27, 0.73), (0.73, 0.73)]


def hexc(rgb, alpha=255):
    return "#%02X%02X%02X%02X" % (alpha, rgb[0], rgb[1], rgb[2])


def rounded_rect(x0, y0, x1, y1, r):
    return (
        "M%.2f,%.2f L%.2f,%.2f A%.2f,%.2f 0 0 1 %.2f,%.2f L%.2f,%.2f "
        "A%.2f,%.2f 0 0 1 %.2f,%.2f L%.2f,%.2f A%.2f,%.2f 0 0 1 %.2f,%.2f "
        "L%.2f,%.2f A%.2f,%.2f 0 0 1 %.2f,%.2f Z"
        % (x0 + r, y0, x1 - r, y0, r, r, x1, y0 + r, x1, y1 - r,
           r, r, x1 - r, y1, x0 + r, y1, r, r, x0, y1 - r, x0, y0 + r,
           r, r, x0 + r, y0)
    )


def circle(cx, cy, r):
    return ("M%.2f,%.2f a%.2f,%.2f 0 1 0 %.2f,0 a%.2f,%.2f 0 1 0 %.2f,0 Z"
            % (cx - r, cy, r, r, 2 * r, r, r, -2 * r))


def dice_vector(size, box, radius_ratio, pip_ratio, comment):
    """box = (x0, y0, x1, y1) of the dice face inside a `size` x `size` viewport."""
    x0, y0, x1, y1 = box
    side = x1 - x0
    r = side * radius_ratio
    depth = side * 0.05
    pip_r = side * pip_ratio

    paths = [
        (SIDE, rounded_rect(x0 + depth, y0 + depth, x1 + depth, y1 + depth, r)),
        (FACE, rounded_rect(x0, y0, x1, y1, r)),
    ]
    for fx, fy in FACE_FIVE:
        paths.append((PIP, circle(x0 + fx * side, y0 + fy * side, pip_r)))

    body = "\n".join(
        '    <path\n        android:fillColor="%s"\n        android:pathData="%s" />' % (hexc(c), d)
        for c, d in paths
    )
    return (
        '<?xml version="1.0" encoding="utf-8"?>\n'
        "<!-- %s -->\n"
        '<vector xmlns:android="http://schemas.android.com/apk/res/android"\n'
        '    android:width="%ddp"\n'
        '    android:height="%ddp"\n'
        '    android:viewportWidth="%d"\n'
        '    android:viewportHeight="%d">\n%s\n</vector>\n'
        % (comment, size, size, size, size, body)
    )


def main():
    os.makedirs(OUT, exist_ok=True)

    # Adaptive launcher icon foreground: content must stay inside the middle 66dp of 108dp.
    launcher = dice_vector(
        108, (26.0, 26.0, 82.0, 82.0), radius_ratio=0.20, pip_ratio=0.083,
        comment="Launcher icon foreground: white dice, drawn for the 66dp adaptive icon safe zone.")
    # Splash screen icon.
    splash = dice_vector(
        108, (24.0, 24.0, 84.0, 84.0), radius_ratio=0.20, pip_ratio=0.083,
        comment="Splash screen logo: white dice on the dark navy splash background.")

    for name, data in (("ic_launcher_foreground.xml", launcher), ("ic_splash_logo.xml", splash)):
        path = os.path.join(OUT, name)
        with open(path, "w", encoding="utf-8") as fh:
            fh.write(data)
        print("wrote %s (%d bytes)" % (name, len(data)))


if __name__ == "__main__":
    main()
