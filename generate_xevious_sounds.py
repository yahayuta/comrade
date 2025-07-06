import numpy as np
from scipy.io.wavfile import write
import os

# Ensure the resources directory exists
os.makedirs("resources", exist_ok=True)

def generate_tone(filename, freq, duration, volume=0.5, sr=44100, fade_out=True):
    """Generate a pure tone with optional fade out"""
    t = np.linspace(0, duration, int(sr * duration), False)
    tone = np.sin(freq * 2 * np.pi * t) * volume
    
    if fade_out:
        # Add a slight fade out for more authentic arcade sound
        fade_samples = int(sr * 0.02)  # 20ms fade
        if fade_samples < len(tone):
            tone[-fade_samples:] *= np.linspace(1, 0, fade_samples)
    
    audio = np.int16(tone * 32767)
    write(f"resources/{filename}", sr, audio)

def generate_chord(filename, freqs, duration, volume=0.5, sr=44100):
    """Generate a chord with multiple frequencies"""
    t = np.linspace(0, duration, int(sr * duration), False)
    tone = np.zeros_like(t)
    
    for freq in freqs:
        tone += np.sin(freq * 2 * np.pi * t) * (volume / len(freqs))
    
    # Add slight fade out
    fade_samples = int(sr * 0.02)
    if fade_samples < len(tone):
        tone[-fade_samples:] *= np.linspace(1, 0, fade_samples)
    
    audio = np.int16(tone * 32767)
    write(f"resources/{filename}", sr, audio)

def generate_descending_tone(filename, start_freq, end_freq, duration, volume=0.5, sr=44100):
    """Generate a tone that descends in frequency"""
    t = np.linspace(0, duration, int(sr * duration), False)
    freqs = np.linspace(start_freq, end_freq, t.size)
    tone = np.sin(2 * np.pi * freqs * t) * volume
    
    # Add fade out
    fade_samples = int(sr * 0.03)
    if fade_samples < len(tone):
        tone[-fade_samples:] *= np.linspace(1, 0, fade_samples)
    
    audio = np.int16(tone * 32767)
    write(f"resources/{filename}", sr, audio)

def generate_noise(filename, duration, volume=0.7, sr=44100, low_pass=True):
    """Generate noise with optional low-pass filtering"""
    t = np.linspace(0, duration, int(sr * duration), False)
    noise = np.random.uniform(-1, 1, t.size) * volume
    
    if low_pass:
        # Simple low-pass filter to make it less harsh
        from scipy.signal import butter, filtfilt
        nyquist = sr / 2
        cutoff = 2000  # 2kHz cutoff
        normal_cutoff = cutoff / nyquist
        b, a = butter(4, normal_cutoff, btype='low', analog=False)
        noise = filtfilt(b, a, noise)
    
    # Fade out
    fade_samples = int(sr * 0.05)
    if fade_samples < len(noise):
        noise[-fade_samples:] *= np.linspace(1, 0, fade_samples)
    
    audio = np.int16(noise * 32767)
    write(f"resources/{filename}", sr, audio)

# Xevious Main Shooting Sound (Player's Zapper)
# Characteristic high-pitched beep around 1.2kHz
generate_tone("shoot.wav", freq=1200, duration=0.08, volume=0.6)

# Xevious Enemy Destruction Sounds
# Different enemies have different destruction sounds
# Small enemies: higher pitch
generate_tone("enemy_hit.wav", freq=800, duration=0.06, volume=0.7)

# Medium enemies: medium pitch
generate_tone("enemy_hit_medium.wav", freq=600, duration=0.08, volume=0.7)

# Large enemies: lower pitch
generate_tone("enemy_hit_large.wav", freq=400, duration=0.1, volume=0.7)

# Boss Hit Sound (characteristic descending tone)
generate_descending_tone("boss_hit.wav", start_freq=600, end_freq=200, duration=0.15, volume=0.7)

# Boss Defeat Sound (more dramatic descending tone)
generate_descending_tone("boss_defeat.wav", start_freq=800, end_freq=150, duration=0.4, volume=0.8)

# Player Hit Sound (harsh noise)
generate_noise("player_hit.wav", duration=0.12, volume=0.9, low_pass=True)

# Explosion Sound (filtered noise)
generate_noise("explosion.wav", duration=0.3, volume=0.8, low_pass=True)

# Additional Xevious-style sounds

# Power-up sound (ascending tone)
def generate_ascending_tone(filename, start_freq, end_freq, duration, volume=0.5, sr=44100):
    """Generate a tone that ascends in frequency"""
    t = np.linspace(0, duration, int(sr * duration), False)
    freqs = np.linspace(start_freq, end_freq, t.size)
    tone = np.sin(2 * np.pi * freqs * t) * volume
    
    # Add fade out
    fade_samples = int(sr * 0.02)
    if fade_samples < len(tone):
        tone[-fade_samples:] *= np.linspace(1, 0, fade_samples)
    
    audio = np.int16(tone * 32767)
    write(f"resources/{filename}", sr, audio)

# Power-up sound
generate_ascending_tone("powerup.wav", start_freq=400, end_freq=800, duration=0.2, volume=0.6)

# Bonus sound (chord)
generate_chord("bonus.wav", freqs=[523, 659, 784], duration=0.3, volume=0.6)  # C-E-G chord

# Game over sound (descending chord)
def generate_descending_chord(filename, start_freqs, end_freqs, duration, volume=0.5, sr=44100):
    """Generate a chord that descends in frequency"""
    t = np.linspace(0, duration, int(sr * duration), False)
    tone = np.zeros_like(t)
    
    for i, (start_freq, end_freq) in enumerate(zip(start_freqs, end_freqs)):
        freqs = np.linspace(start_freq, end_freq, t.size)
        tone += np.sin(2 * np.pi * freqs * t) * (volume / len(start_freqs))
    
    # Add fade out
    fade_samples = int(sr * 0.05)
    if fade_samples < len(tone):
        tone[-fade_samples:] *= np.linspace(1, 0, fade_samples)
    
    audio = np.int16(tone * 32767)
    write(f"resources/{filename}", sr, audio)

# Game over sound
generate_descending_chord("game_over.wav", 
                         start_freqs=[523, 659, 784], 
                         end_freqs=[262, 330, 392], 
                         duration=0.5, volume=0.7)

# Level complete sound (ascending chord)
def generate_ascending_chord(filename, start_freqs, end_freqs, duration, volume=0.5, sr=44100):
    """Generate a chord that ascends in frequency"""
    t = np.linspace(0, duration, int(sr * duration), False)
    tone = np.zeros_like(t)
    
    for i, (start_freq, end_freq) in enumerate(zip(start_freqs, end_freqs)):
        freqs = np.linspace(start_freq, end_freq, t.size)
        tone += np.sin(2 * np.pi * freqs * t) * (volume / len(start_freqs))
    
    # Add fade out
    fade_samples = int(sr * 0.03)
    if fade_samples < len(tone):
        tone[-fade_samples:] *= np.linspace(1, 0, fade_samples)
    
    audio = np.int16(tone * 32767)
    write(f"resources/{filename}", sr, audio)

# Level complete sound
generate_ascending_chord("level_complete.wav", 
                        start_freqs=[262, 330, 392], 
                        end_freqs=[523, 659, 784], 
                        duration=0.4, volume=0.6)

print("Xevious-style sound files generated in the resources/ directory.")
print("Generated sounds:")
print("- shoot.wav (main weapon)")
print("- enemy_hit.wav (small enemy destruction)")
print("- enemy_hit_medium.wav (medium enemy destruction)")
print("- enemy_hit_large.wav (large enemy destruction)")
print("- boss_hit.wav (boss hit)")
print("- boss_defeat.wav (boss destruction)")
print("- player_hit.wav (player hit)")
print("- explosion.wav (explosion)")
print("- powerup.wav (power-up)")
print("- bonus.wav (bonus points)")
print("- game_over.wav (game over)")
print("- level_complete.wav (level complete)") 