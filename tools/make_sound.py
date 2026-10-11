#!/usr/bin/env python3
"""Synthesise an original 'dice roll' sound effect (rattle + settle) as a WAV file.

The sound is 100% generated here, so the app ships with an original, licence-free
audio asset - no downloaded files, no attribution needed.
"""
import math
import os
import random
import struct
import sys
import wave

RATE = 22050
PROJECT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
# Default output: the app's raw folder, so re-running this script refreshes the shipped sound.
OUT = sys.argv[1] if len(sys.argv) > 1 else os.path.join(
    PROJECT, "app", "src", "main", "res", "raw", "dice_roll.wav")
random.seed(20240815)


def one_pole_lowpass(samples, cutoff):
    alpha = 1.0 - math.exp(-2.0 * math.pi * cutoff / RATE)
    out = []
    prev = 0.0
    for s in samples:
        prev += alpha * (s - prev)
        out.append(prev)
    return out


def one_pole_highpass(samples, cutoff):
    alpha = 1.0 - math.exp(-2.0 * math.pi * cutoff / RATE)
    out = []
    prev_in = 0.0
    prev_out = 0.0
    for s in samples:
        prev_out = alpha * (prev_out + s - prev_in)
        prev_in = s
        out.append(prev_out)
    return out


def noise_burst(length, decay, low, high, gain):
    """A short 'clack' of plastic hitting the table."""
    raw = [random.uniform(-1.0, 1.0) for _ in range(length)]
    raw = one_pole_highpass(raw, low)
    raw = one_pole_lowpass(raw, high)
    out = []
    for i, s in enumerate(raw):
        t = i / RATE
        attack = min(1.0, t / 0.0008)
        env = attack * math.exp(-t / decay)
        out.append(s * env * gain)
    return out


def tone(length, freq, decay, gain):
    out = []
    for i in range(length):
        t = i / RATE
        env = math.exp(-t / decay) * min(1.0, t / 0.002)
        out.append(math.sin(2 * math.pi * freq * t) * env * gain)
    return out


def write_wav(path, samples, rate=RATE):
    os.makedirs(os.path.dirname(os.path.abspath(path)), exist_ok=True)
    with wave.open(path, "wb") as wf:
        wf.setnchannels(1)
        wf.setsampwidth(2)
        wf.setframerate(rate)
        frames = b"".join(struct.pack("<h", int(max(-1.0, min(1.0, s)) * 32000)) for s in samples)
        wf.writeframes(frames)
    print("wrote %s (%.2fs, %d bytes)" % (path, len(samples) / rate, os.path.getsize(path)))


def make_land(out_path):
    """A short, soft 'click' for the moment the dice settle."""
    total = int(RATE * 0.30)
    buf = [0.0] * total

    def add(clip, start):
        i0 = int(start * RATE)
        for i, s in enumerate(clip):
            idx = i0 + i
            if 0 <= idx < total:
                buf[idx] += s

    add(noise_burst(int(RATE * 0.07), decay=0.012, low=700, high=6000, gain=0.42), 0.0)
    add(tone(int(RATE * 0.16), 196.0, decay=0.035, gain=0.30), 0.0)
    add(noise_burst(int(RATE * 0.03), decay=0.005, low=2600, high=9000, gain=0.16), 0.02)

    peak = max(1e-9, max(abs(s) for s in buf))
    buf = [math.tanh((s / peak) * 1.1) / math.tanh(1.1) * 0.85 for s in buf]
    fade = int(RATE * 0.05)
    for i in range(fade):
        buf[total - fade + i] *= 1.0 - i / fade
    write_wav(out_path, buf)


def main():
    duration = 0.9
    total = int(RATE * duration)
    buf = [0.0] * total

    def add(clip, start):
        i0 = int(start * RATE)
        for i, s in enumerate(clip):
            idx = i0 + i
            if 0 <= idx < total:
                buf[idx] += s

    # Six rattling clacks of a dice tumbling over the table.
    positions = [0.02, 0.11, 0.19, 0.27, 0.34, 0.42]
    for k, pos in enumerate(positions):
        length = int(RATE * (0.05 + 0.01 * random.random()))
        gain = 0.55 - 0.05 * k
        add(noise_burst(length, decay=0.012 + 0.004 * random.random(),
                        low=900 + 200 * random.random(), high=5200, gain=gain), pos)
        # a bright 'crack' transient on top of each clack
        length2 = int(RATE * 0.02)
        add(noise_burst(length2, decay=0.003, low=2600, high=9000, gain=0.3), pos)

    # The dice settles: a lower, softer thud plus a small final tick.
    add(tone(int(RATE * 0.25), 138.0, decay=0.05, gain=0.42), 0.60)
    add(noise_burst(int(RATE * 0.09), decay=0.02, low=180, high=1800, gain=0.40), 0.60)
    add(noise_burst(int(RATE * 0.05), decay=0.008, low=1500, high=7000, gain=0.22), 0.74)

    # Normalise, soft-clip and taper the tail so there is no click at the end.
    peak = max(1e-9, max(abs(s) for s in buf))
    buf = [math.tanh((s / peak) * 1.25) / math.tanh(1.25) for s in buf]
    fade_len = int(RATE * 0.06)
    for i in range(fade_len):
        buf[total - fade_len + i] *= 1.0 - i / fade_len
    for i in range(int(RATE * 0.004)):
        buf[i] *= i / (RATE * 0.004)

    write_wav(OUT, buf)
    make_land(os.path.join(os.path.dirname(OUT), "dice_land.wav"))


if __name__ == "__main__":
    main()
