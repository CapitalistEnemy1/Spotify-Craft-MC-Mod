/*
 * Decompiled with CFR 0.152.
 */
package dev.neonlyrics.config;

import dev.neonlyrics.hud.Gfx;
import dev.neonlyrics.sky.SkyPreset;
import dev.neonlyrics.sky.WorldSky;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Config {
    public int accent = 0;
    public boolean rgbAccent = false;
    public boolean blur = true;
    public boolean particles = false;
    public boolean ripples = false;
    public boolean sounds = true;
    public int uiFont = 0;
    public int lyricsFont = 0;
    public boolean fxMenuAnim = true;
    public int menuMode = 0;
    public String licenseCode = "";
    public Map<String, String> usedCodes = new HashMap<String, String>();
    public int menuModeVer = 0;
    public boolean menuCustom = true;
    public int menuTime = 0;
    public int menuCam = 0;
    public boolean menuOwnBg = false;
    public boolean customSkyboxEnabled = true;
    public int sky = 0;
    public int lastSky = 0;
    public float skyStrength = 0.8f;
    public float weatherAmount = 1.0f;
    public boolean realWeather = true;
    public boolean hideClouds = true;
    public String cloudsBefore = "";
    public boolean skyDay = true;
    public boolean fxDayNight = false;
    public boolean fxConstellations = true;
    public boolean fxSkyMusic = true;
    public float skySpeed = 1.0f;
    public int skyPalette = -1;
    public int hat = 0;
    public boolean hatGlow = true;
    public int crosshair = 0;
    public int trail = 0;
    public boolean aura = false;
    public int deathFx = 1;
    public boolean deathOthers = false;
    public boolean beatGlow = false;
    public boolean skyShuffle = false;
    public float skyPattern = 1.0f;
    public float skyLively = 0.6f;
    public float skyBreath = 1.0f;
    public int customTop = 656920;
    public int customHorizon = 2758216;
    public int customAurora1 = 10719743;
    public int customAurora2 = 0xFFFFFF;
    public int customWeather = 2;
    public boolean customAurora = true;
    public boolean customStars = true;
    public boolean customMoon = false;
    public boolean customShooting = true;
    public boolean lyricsSky = true;
    public boolean lyricsIsland = true;
    public boolean lyricsFollow = true;
    public int lyricsStyle = 0;
    public boolean lyricsViewLock = false;
    public float lyricsSize = 1.6f;
    public float lyricsOpacity = 0.9f;
    public boolean lyricsGlow = true;
    public float lyricsGlowStrength = 1.0f;
    public int lyricsGlowColor = -1;
    public int lyricsColor = -1;
    public int lyricsAnim = 0;
    public boolean lyricsUnderground = false;
    public int lyricsOffsetMs = 0;
    public boolean fxBeatPulse = true;
    public boolean fxSongIntro = true;
    public boolean translate = false;
    public String translateTo = "de";
    public boolean spotifyHudEnabled = true;
    // Retained for backwards compatibility with existing config.json files.
    @Deprecated
    public boolean islandEnabled = true;
    public boolean showClock = true;
    public boolean showPing = true;
    public boolean showFps = false;
    public boolean albumCover = true;
    public boolean glassHotbar = true;
    public boolean fxStartup = true;
    public int quality = 1;
    public boolean fpsBoost = false;
    public String boostBefore = "";
    public boolean autoPerf = true;
    public int targetFps = 0;
    public boolean fxWeather = true;
    public boolean fxAurora = true;
    public boolean fxStars = true;
    public boolean fxBeams = true;
    public boolean fxSkyColor = true;
    public int profile = 0;
    public boolean demoLyrics = false;
    public String spotifyClientId = "";
    public int spotifyMode = 0;
    public boolean spotifyTutorialSeen = false;
    public int styleVersion = 0;

    public void clamp() {
        this.deathFx = Math.max(0, Math.min(3, this.deathFx));
        if (this.styleVersion < 2 && this.spotifyClientId != null && !this.spotifyClientId.isBlank()) {
            this.spotifyMode = 1;
        }
        if (this.styleVersion < 3 && System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win")) {
            this.spotifyMode = 0;
        }
        if (this.styleVersion < 7) {
            this.fxDayNight = false;
            this.skyDay = true;
        }
        if (this.styleVersion < 8) {
            this.styleVersion = 8;
        }
        if (this.styleVersion < 9) {
            this.autoPerf = true;
            this.targetFps = 0;
            this.styleVersion = 9;
        }
        if (this.styleVersion < 10) {
            this.spotifyHudEnabled = this.islandEnabled;
            this.styleVersion = 10;
        }
        this.islandEnabled = this.spotifyHudEnabled;
        this.spotifyMode = Math.max(0, Math.min(1, this.spotifyMode));
        this.rgbAccent = false;
        int fonts = Gfx.FONT_IDS.length;
        this.uiFont = Math.max(0, Math.min(fonts, this.uiFont));
        this.lyricsFont = Math.max(0, Math.min(fonts, this.lyricsFont));
        this.sky = Math.max(-1, Math.min(SkyPreset.values().length - 1, this.sky));
        this.skyStrength = Math.max(0.1f, Math.min(1.0f, this.skyStrength));
        this.weatherAmount = Math.max(0.0f, Math.min(2.0f, this.weatherAmount));
        this.lyricsSize = Math.max(0.6f, Math.min(4.0f, this.lyricsSize));
        this.lyricsOpacity = Math.max(0.2f, Math.min(1.0f, this.lyricsOpacity));
        this.lyricsGlowStrength = Math.max(0.2f, Math.min(2.5f, this.lyricsGlowStrength));
        this.lyricsAnim = Math.floorMod(this.lyricsAnim, 3);
        this.quality = Math.max(0, Math.min(2, this.quality));
        if (this.targetFps != 0) {
            this.targetFps = Math.max(30, Math.min(240, this.targetFps));
        }
        this.skySpeed = Math.max(0.25f, Math.min(2.0f, this.skySpeed));
        this.skyPattern = Math.max(0.2f, Math.min(1.5f, this.skyPattern));
        this.skyLively = Math.max(0.0f, Math.min(1.0f, this.skyLively));
        this.skyBreath = Math.max(0.0f, Math.min(2.0f, this.skyBreath));
        this.skyPalette = Math.max(-1, Math.min(WorldSky.PALETTES.length - 1, this.skyPalette));
        this.lyricsOffsetMs = Math.max(-5000, Math.min(5000, this.lyricsOffsetMs));
        this.lyricsStyle = Math.floorMod(this.lyricsStyle, 2);
        if (this.spotifyClientId == null) {
            this.spotifyClientId = "";
        }
        if (this.cloudsBefore == null) {
            this.cloudsBefore = "";
        }
        if (this.boostBefore == null) {
            this.boostBefore = "";
        }
        if (this.translateTo == null) {
            this.translateTo = "de";
        }
        if (this.licenseCode == null) {
            this.licenseCode = "";
        }
        if (this.usedCodes == null) {
            this.usedCodes = new HashMap<String, String>();
        }
        if (this.sky >= 20) {
            this.sky = SkyPreset.INFERNO.ordinal();
        }
        if (this.menuModeVer < 2) {
            this.menuMode = 0;
            this.menuModeVer = 2;
        }
        if (this.sky >= 0) {
            this.lastSky = this.sky;
        }
    }
}

