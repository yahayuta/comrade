import numpy as np
from scipy.io.wavfile import write
import os

# Ensure the resources directory exists
os.makedirs("resources", exist_ok=True)

def beep(filename, freq=440, duration=0.1, volume=0.5, sr=44100):
    t = np.linspace(0, duration, int(sr * duration), False)
    tone = np.sin(freq * 2 * np.pi * t) * volume
    audio = np.int16(tone * 32767)
    write(f"resources/{filename}", sr, audio)

def explosion(filename, duration=0.3, volume=0.7, sr=44100):
    t = np.linspace(0, duration, int(sr * duration), False)
    noise = np.random.uniform(-1, 1, t.shape) * volume
    # Fade out
    noise *= np.linspace(1, 0, t.size)
    audio = np.int16(noise * 32767)
    write(f"resources/{filename}", sr, audio)

# Shooting sound
beep("shoot.wav", freq=900, duration=0.07, volume=0.6)

# Explosion sound
explosion("explosion.wav", duration=0.25, volume=0.8)

# Enemy hit (short beep)
beep("enemy_hit.wav", freq=600, duration=0.05, volume=0.7)

# Boss hit (lower beep)
beep("boss_hit.wav", freq=300, duration=0.08, volume=0.7)

# Boss defeat (descending tone)
def boss_defeat(filename, sr=44100):
    duration = 0.4
    t = np.linspace(0, duration, int(sr * duration), False)
    freqs = np.linspace(800, 200, t.size)
    tone = np.sin(2 * np.pi * freqs * t) * 0.7
    audio = np.int16(tone * 32767)
    write(f"resources/{filename}", sr, audio)
boss_defeat("boss_defeat.wav")

# Player hit (short harsh noise)
explosion("player_hit.wav", duration=0.08, volume=1.0)

print("Sound files generated in the resources/ directory.")
