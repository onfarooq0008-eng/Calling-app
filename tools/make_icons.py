#!/usr/bin/env python3
"""Generate the Dice Roll launcher icons: a white dice on a dark navy / deep
purple gradient background, with a soft glow and a subtle 3D extrusion."""
import math
import os
import sys

from PIL import Image, ImageDraw, ImageFilter

PROJECT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
RES = os.path.join(PROJECT, "app", "src", "main", "res")

# Default output: the app's mipmap folders. Pass a folder as argument 1 to write elsewhere.
BASE = sys.argv[1] if len(sys.argv) > 1 else RES
# The two preview pictures used by README.md.
PREVIEW_DIR = sys.argv[2] if len(sys.argv) > 2 else os.path.join(PROJECT, "docs")

NAVY = (26, 38, 86)
PURPLE_DEEP = (18, 20, 56)
PURPLE = (124, 86, 232)
BLUE = (56, 140, 255)
PIP = (20, 22, 34)
SIDE = (176, 190, 224)

SIZES = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192,
}

FACE_FIVE = [(0.26, 0.26), (0.74, 0.26), (0.5, 0.5), (0.26, 0.74), (0.74, 0.74)]


def gradient(size, top, bottom):
    img = Image.new("RGB", (1, 2), top)
    img.putpixel((0, 0), top)
    img.putpixel((0, 1), bottom)
    return img.resize((size, size), Image.BILINEAR).convert("RGBA")


def soft_radial(size, color, cx, cy, radius, strength):
    """A blurred radial glow of the given colour and strength."""
    layer = Image.new("L", (size, size), 0)
    dr = ImageDraw.Draw(layer)
    steps = 40
    for i in range(steps, 0, -1):
        r = radius * size * (i / steps)
        a = int(255 * strength * (1 - i / steps) ** 1.6)
        dr.ellipse([cx * size - r, cy * size - r, cx * size + r, cy * size + r], fill=a)
    layer = layer.filter(ImageFilter.GaussianBlur(size * 0.06))
    out = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    out.paste(Image.new("RGB", (size, size), color), (0, 0), layer)
    return out


def dice_layer(size, scale=0.62, angle=-8.0):
    """Render the dice on its own layer so it can be rotated / shadowed."""
    side = int(size * 1.6)
    layer = Image.new("RGBA", (side, side), (0, 0, 0, 0))
    dr = ImageDraw.Draw(layer, "RGBA")

    cx = cy = side / 2
    half = size * scale / 2
    x0, y0, x1, y1 = cx - half, cy - half, cx + half, cy + half
    radius = (x1 - x0) * 0.2
    depth = (x1 - x0) * 0.055

    # Extruded base: the dice seen slightly from above-left.
    dr.rounded_rectangle([x0 + depth, y0 + depth, x1 + depth, y1 + depth],
                         radius=radius, fill=SIDE + (255,))
    # Face.
    dr.rounded_rectangle([x0, y0, x1, y1], radius=radius, fill=(255, 255, 255, 255))

    # Very gentle shading so the face is not perfectly flat (kept subtle).
    shade = Image.new("RGBA", (side, side), (0, 0, 0, 0))
    ImageDraw.Draw(shade).rounded_rectangle(
        [x0, y0 + (y1 - y0) * 0.62, x1, y1], radius=radius, fill=(184, 196, 226, 52))
    shade = shade.filter(ImageFilter.GaussianBlur(side * 0.015))
    layer.alpha_composite(shade)

    # Pips with a soft inner shadow ring for an engraved look.
    pip_r = (x1 - x0) * 0.083
    for fx, fy in FACE_FIVE:
        px = x0 + fx * (x1 - x0)
        py = y0 + fy * (y1 - y0)
        dr.ellipse([px - pip_r, py - pip_r, px + pip_r, py + pip_r], fill=PIP + (255,))

    # Specular highlight on the top-left corner.
    gloss = Image.new("RGBA", (side, side), (0, 0, 0, 0))
    ImageDraw.Draw(gloss).ellipse(
        [x0 + (x1 - x0) * 0.06, y0 + (y1 - y0) * 0.03,
         x0 + (x1 - x0) * 0.62, y0 + (y1 - y0) * 0.40], fill=(255, 255, 255, 60))
    gloss = gloss.filter(ImageFilter.GaussianBlur(side * 0.05))
    layer.alpha_composite(gloss)

    layer = layer.rotate(angle, resample=Image.BICUBIC, center=(side / 2, side / 2))
    # Crop back to the canvas, centred.
    off = int((side - size) / 2)
    return layer.crop((off, off, off + size, off + size))


def shadow_layer(size, dice, offset=0.035, blur=0.05, alpha=150):
    alpha_ch = dice.split()[3].point(lambda v: min(255, int(v * alpha / 255)))
    sh = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    sh.putalpha(alpha_ch)
    sh = sh.filter(ImageFilter.GaussianBlur(size * blur))
    shifted = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    shifted.alpha_composite(sh, (int(size * offset), int(size * offset)))
    return shifted


def make_icon(size, round_icon=False):
    img = gradient(size, NAVY, PURPLE_DEEP)
    # Purple / blue glow, kept subtle so the navy stays dominant.
    img.alpha_composite(soft_radial(size, PURPLE, 0.5, 0.55, 0.40, 0.34))
    img.alpha_composite(soft_radial(size, BLUE, 0.72, 0.2, 0.28, 0.20))

    dice = dice_layer(size)
    img.alpha_composite(shadow_layer(size, dice))
    img.alpha_composite(dice)

    mask = Image.new("L", (size, size), 0)
    mdr = ImageDraw.Draw(mask)
    if round_icon:
        mdr.ellipse([0, 0, size - 1, size - 1], fill=255)
    else:
        mdr.rounded_rectangle([0, 0, size - 1, size - 1], radius=int(size * 0.22), fill=255)
    img.putalpha(mask)
    return img


def main():
    for folder, size in SIZES.items():
        out_dir = os.path.join(BASE, folder)
        os.makedirs(out_dir, exist_ok=True)
        make_icon(size, round_icon=False).save(os.path.join(out_dir, "ic_launcher.png"))
        make_icon(size, round_icon=True).save(os.path.join(out_dir, "ic_launcher_round.png"))
        print("wrote %s (%dx%d)" % (folder, size, size))
    os.makedirs(PREVIEW_DIR, exist_ok=True)
    make_icon(512, round_icon=False).save(os.path.join(PREVIEW_DIR, "app_icon.png"))
    make_icon(192, round_icon=True).save(os.path.join(PREVIEW_DIR, "app_icon_round.png"))
    print("wrote previews to %s" % PREVIEW_DIR)


if __name__ == "__main__":
    main()
