/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraft.class_156
 *  net.minecraft.class_310
 */
package dev.neonlyrics.lyrics;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.neonlyrics.NeonLyrics;
import dev.neonlyrics.config.Config;
import dev.neonlyrics.config.Storage;
import dev.neonlyrics.hud.Cover;
import dev.neonlyrics.hud.Island;
import dev.neonlyrics.lyrics.Http;
import dev.neonlyrics.lyrics.LocalMedia;
import dev.neonlyrics.lyrics.LyricsProvider;
import dev.neonlyrics.lyrics.NowPlaying;
import dev.neonlyrics.sky.SkyPreset;
import dev.neonlyrics.util.Fx;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.class_156;
import net.minecraft.class_310;

public final class SpotifyClient {
    public static final int PORT = 8789;
    public static final String REDIRECT = "http://127.0.0.1:8789/callback";
    public static final String DASHBOARD = "https://developer.spotify.com/dashboard";
    private static final String SCOPES = "user-read-currently-playing user-read-playback-state user-modify-playback-state";
    private static Tokens tokens = new Tokens();
    private static volatile Status status = Status.DISCONNECTED;
    private static volatile String lastError = "";
    private static volatile NowPlaying now = NowPlaying.EMPTY;
    private static final ArrayDeque<Played> HISTORY = new ArrayDeque();
    private static volatile long backoffUntil = 0L;
    private static String coverKey = "";
    private static ScheduledExecutorService exec;
    private static volatile ServerSocket loginServer;
    private static long demoStart;
    private static List<NowPlaying.Line> demoLines;

    private SpotifyClient() {
    }

    private static void remember(String title, String artist) {
        if (title == null || title.isBlank()) {
            return;
        }
        Played first = HISTORY.peekFirst();
        if (first != null && first.title().equals(title) && first.artist().equals(String.valueOf(artist))) {
            return;
        }
        HISTORY.addFirst(new Played(title, String.valueOf(artist), System.currentTimeMillis()));
        while (HISTORY.size() > 12) {
            HISTORY.removeLast();
        }
    }

    private static void coverFallback(String title, String artist) {
        long songAt = System.currentTimeMillis();
        if (exec == null) {
            return;
        }
        exec.schedule(() -> {
            try {
                if (Cover.loadedAt() >= songAt) {
                    return;
                }
                NowPlaying cur = now;
                if (cur.title == null || !cur.title.equals(title)) {
                    return;
                }
                String clean = title.replaceAll("\\s*[\\(\\[](feat|ft|with)[^\\)\\]]*[\\)\\]]", "").trim();
                String term = URLEncoder.encode(artist + " " + clean, StandardCharsets.UTF_8);
                Http.Response r = Http.get("https://itunes.apple.com/search?media=music&entity=song&limit=1&term=" + term, Map.of());
                if (r.code() != 200) {
                    return;
                }
                JsonObject o = JsonParser.parseString((String)r.body()).getAsJsonObject();
                JsonArray res = o.getAsJsonArray("results");
                if (res == null || res.isEmpty()) {
                    return;
                }
                JsonObject first = res.get(0).getAsJsonObject();
                if (!first.has("artworkUrl100")) {
                    return;
                }
                String url = first.get("artworkUrl100").getAsString().replace("100x100bb", "600x600bb");
                if (Cover.loadedAt() >= songAt) {
                    return;
                }
                Cover.load(Http.getBytes(url));
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }, 2500L, TimeUnit.MILLISECONDS);
    }

    public static List<Played> history() {
        return new ArrayList<Played>(HISTORY);
    }

    public static void init() {
        tokens = Storage.load("spotify.json", Tokens.class, Tokens::new);
        status = SpotifyClient.tokens.refresh != null && !SpotifyClient.tokens.refresh.isBlank() ? Status.CONNECTED : Status.DISCONNECTED;
        exec = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "NeonLyrics-Spotify");
            t.setDaemon(true);
            return t;
        });
        exec.scheduleWithFixedDelay(SpotifyClient::poll, 2L, 2L, TimeUnit.SECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(LocalMedia::stop, "NeonLyrics-MediaStop"));
    }

    public static Status status() {
        if (NeonLyrics.cfg().spotifyClientId.isBlank() && status != Status.CONNECTED) {
            return Status.NOT_CONFIGURED;
        }
        return status;
    }

    public static String lastError() {
        return lastError;
    }

    public static NowPlaying now() {
        if (NeonLyrics.cfg().demoLyrics) {
            return SpotifyClient.demo();
        }
        return now;
    }

    private static NowPlaying demo() {
        if (demoLines == null) {
            demoLines = LyricsProvider.demo();
        }
        long len = demoLines.get(demoLines.size() - 1).timeMs() + 1500L;
        if (demoStart == 0L) {
            demoStart = System.currentTimeMillis();
        }
        long p = (System.currentTimeMillis() - demoStart) % len;
        return new NowPlaying("demo", "Neon Nights (Demo)", "SpotifyCraft", len, true, p, System.currentTimeMillis(), demoLines, NowPlaying.LyricsState.SYNCED);
    }

    public static void restartDemo() {
        demoStart = System.currentTimeMillis();
    }

    public static void login() {
        String clientId = NeonLyrics.cfg().spotifyClientId.trim();
        if (clientId.isEmpty()) {
            Fx.error(Fx.t("neonlyrics.spotify.needid", new Object[0]));
            return;
        }
        SpotifyClient.closeLoginServer();
        try {
            byte[] rnd = new byte[48];
            new SecureRandom().nextBytes(rnd);
            String verifier = Base64.getUrlEncoder().withoutPadding().encodeToString(rnd);
            String challenge = Base64.getUrlEncoder().withoutPadding().encodeToString(MessageDigest.getInstance("SHA-256").digest(verifier.getBytes(StandardCharsets.US_ASCII)));
            byte[] st = new byte[12];
            new SecureRandom().nextBytes(st);
            String state = Base64.getUrlEncoder().withoutPadding().encodeToString(st);
            ServerSocket ss = new ServerSocket(8789, 4, InetAddress.getByName("127.0.0.1"));
            ss.setSoTimeout(180000);
            loginServer = ss;
            status = Status.WAITING_LOGIN;
            String url = "https://accounts.spotify.com/authorize?response_type=code&client_id=" + LyricsProvider.enc(clientId) + "&scope=" + LyricsProvider.enc(SCOPES) + "&redirect_uri=" + LyricsProvider.enc(REDIRECT) + "&code_challenge_method=S256&code_challenge=" + challenge + "&state=" + state;
            class_156.method_668().method_670(url);
            Thread t = new Thread(() -> SpotifyClient.waitForCallback(ss, clientId, verifier, state), "NeonLyrics-SpotifyLogin");
            t.setDaemon(true);
            t.start();
        }
        catch (Exception e) {
            SpotifyClient.fail(e);
        }
    }

    /*
     * Exception decompiling
     */
    private static void waitForCallback(ServerSocket ss, String clientId, String verifier, String state) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 3 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private static void exchange(String clientId, String verifier, String code) throws Exception {
        String form = "grant_type=authorization_code&code=" + LyricsProvider.enc(code) + "&redirect_uri=" + LyricsProvider.enc(REDIRECT) + "&client_id=" + LyricsProvider.enc(clientId) + "&code_verifier=" + LyricsProvider.enc(verifier);
        Http.Response r = Http.postForm("https://accounts.spotify.com/api/token", Map.of(), form);
        if (r.code() != 200) {
            throw new IllegalStateException("Token " + r.code() + ": " + r.body());
        }
        SpotifyClient.readTokens(r.body());
        status = Status.CONNECTED;
        lastError = "";
        class_310.method_1551().execute(() -> {
            Island.notify(Fx.t("neonlyrics.spotify.connected", new Object[0]), true);
            Fx.sound(Fx.Sfx.WIN);
        });
    }

    private static void readTokens(String json) {
        JsonObject o = JsonParser.parseString((String)json).getAsJsonObject();
        SpotifyClient.tokens.access = o.get("access_token").getAsString();
        if (o.has("refresh_token") && !o.get("refresh_token").isJsonNull()) {
            SpotifyClient.tokens.refresh = o.get("refresh_token").getAsString();
        }
        SpotifyClient.tokens.expiresAt = System.currentTimeMillis() + (o.has("expires_in") ? o.get("expires_in").getAsLong() : 3600L) * 1000L - 30000L;
        Storage.save("spotify.json", tokens);
    }

    private static boolean refresh() {
        String clientId = NeonLyrics.cfg().spotifyClientId.trim();
        if (SpotifyClient.tokens.refresh == null || SpotifyClient.tokens.refresh.isBlank() || clientId.isEmpty()) {
            return false;
        }
        try {
            String form = "grant_type=refresh_token&refresh_token=" + LyricsProvider.enc(SpotifyClient.tokens.refresh) + "&client_id=" + LyricsProvider.enc(clientId);
            Http.Response r = Http.postForm("https://accounts.spotify.com/api/token", Map.of(), form);
            if (r.code() != 200) {
                if (r.code() == 400) {
                    SpotifyClient.logout();
                }
                lastError = "Refresh " + r.code();
                return false;
            }
            SpotifyClient.readTokens(r.body());
            return true;
        }
        catch (Exception e) {
            lastError = String.valueOf(e.getMessage());
            return false;
        }
    }

    public static void logout() {
        tokens = new Tokens();
        Storage.save("spotify.json", tokens);
        now = NowPlaying.EMPTY;
        status = Status.DISCONNECTED;
    }

    private static void closeLoginServer() {
        ServerSocket s = loginServer;
        if (s != null) {
            try {
                s.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private static void poll() {
        try {
            if (NeonLyrics.cfg().demoLyrics) {
                return;
            }
            if (SpotifyClient.autoMode()) {
                LocalMedia.ensureRunning();
                return;
            }
            LocalMedia.stop();
            if (status != Status.CONNECTED) {
                return;
            }
            if (System.currentTimeMillis() < backoffUntil) {
                return;
            }
            if (class_310.method_1551().field_1687 == null) {
                return;
            }
            if (System.currentTimeMillis() > SpotifyClient.tokens.expiresAt && !SpotifyClient.refresh()) {
                return;
            }
            Http.Response r = Http.get("https://api.spotify.com/v1/me/player/currently-playing", Map.of("Authorization", "Bearer " + SpotifyClient.tokens.access));
            if (r.code() == 401) {
                if (SpotifyClient.refresh()) {
                    SpotifyClient.poll();
                }
                return;
            }
            if (r.code() == 429) {
                backoffUntil = System.currentTimeMillis() + 15000L;
                return;
            }
            if (r.code() == 204 || r.body().isBlank()) {
                now = NowPlaying.EMPTY;
                return;
            }
            if (r.code() != 200) {
                lastError = "Spotify " + r.code();
                return;
            }
            JsonObject o = JsonParser.parseString((String)r.body()).getAsJsonObject();
            if (!o.has("item") || o.get("item").isJsonNull()) {
                now = NowPlaying.EMPTY;
                return;
            }
            JsonObject item = o.getAsJsonObject("item");
            String id = item.has("id") && !item.get("id").isJsonNull() ? item.get("id").getAsString() : item.get("name").getAsString();
            String title = item.get("name").getAsString();
            StringBuilder artists = new StringBuilder();
            if (item.has("artists")) {
                JsonArray arr = item.getAsJsonArray("artists");
                for (int i = 0; i < arr.size(); ++i) {
                    if (i > 0) {
                        artists.append(", ");
                    }
                    artists.append(arr.get(i).getAsJsonObject().get("name").getAsString());
                }
            }
            String firstArtist = artists.toString().split(", ")[0];
            String album = item.has("album") ? item.getAsJsonObject("album").get("name").getAsString() : "";
            long duration = item.has("duration_ms") ? item.get("duration_ms").getAsLong() : 0L;
            long progress = o.has("progress_ms") && !o.get("progress_ms").isJsonNull() ? o.get("progress_ms").getAsLong() : 0L;
            boolean playing = o.has("is_playing") && o.get("is_playing").getAsBoolean();
            SpotifyClient.publish(id, title, artists.toString(), firstArtist, album, duration, progress, playing);
            if (!id.equals(coverKey)) {
                JsonArray imgs;
                coverKey = id;
                String url = null;
                if (item.has("album") && item.getAsJsonObject("album").has("images") && !(imgs = item.getAsJsonObject("album").getAsJsonArray("images")).isEmpty()) {
                    url = imgs.get(imgs.size() - 1).getAsJsonObject().get("url").getAsString();
                }
                if (url == null) {
                    Cover.clear();
                } else {
                    String u = url;
                    exec.execute(() -> {
                        try {
                            Cover.load(Http.getBytes(u));
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                    });
                }
            }
        }
        catch (Exception e) {
            lastError = String.valueOf(e.getMessage());
        }
    }

    static void publish(String id, String title, String artists, String firstArtist, String album, long duration, long progress, boolean playing) {
        List<NowPlaying.Line> lines;
        long predicted;
        long diff;
        boolean changed;
        NowPlaying prev = now;
        boolean bl = changed = !id.equals(prev.trackId);
        if (!changed && playing && prev.playing && Math.abs(diff = progress - (predicted = prev.progressNow(0))) < 1500L) {
            progress = predicted + diff / 4L;
        }
        List<NowPlaying.Line> list = lines = changed ? LyricsProvider.cached(id) : prev.lines;
        NowPlaying.LyricsState ls = changed ? (lines == null ? NowPlaying.LyricsState.LOADING : (lines.isEmpty() ? NowPlaying.LyricsState.NOT_FOUND : NowPlaying.LyricsState.SYNCED)) : prev.lyricsState;
        now = new NowPlaying(id, title, artists, duration, playing, progress, System.currentTimeMillis(), lines, ls);
        if (!changed) {
            return;
        }
        SpotifyClient.coverFallback(title, firstArtist);
        class_310.method_1551().execute(() -> {
            Island.notify("\u266a " + title, true);
            SpotifyClient.remember(title, artists);
            Config cf = NeonLyrics.cfg();
            if (cf.skyShuffle && cf.sky >= 0) {
                SkyPreset.shuffle();
            }
        });
        if (ls != NowPlaying.LyricsState.LOADING) {
            return;
        }
        exec.execute(() -> {
            block3: {
                try {
                    List<NowPlaying.Line> fetched = LyricsProvider.fetch(id, title, firstArtist, album, duration);
                    NowPlaying cur = now;
                    if (id.equals(cur.trackId)) {
                        now = cur.withLyrics(fetched, fetched == null ? NowPlaying.LyricsState.NOT_FOUND : NowPlaying.LyricsState.SYNCED);
                    }
                }
                catch (Exception e) {
                    NowPlaying cur = now;
                    if (!id.equals(cur.trackId)) break block3;
                    now = cur.withLyrics(null, NowPlaying.LyricsState.ERROR);
                }
            }
        });
    }

    static void publishNothing() {
        now = NowPlaying.EMPTY;
    }

    public static void syncHere() {
        NowPlaying np = SpotifyClient.now();
        Config c = NeonLyrics.cfg();
        if (!np.hasTrack() || np.lines.isEmpty()) {
            Island.notify(Fx.t("neonlyrics.media.nolyrics", new Object[0]), false);
            return;
        }
        long raw = np.progressNow(0);
        long cur = raw + (long)c.lyricsOffsetMs;
        long best = Long.MAX_VALUE;
        long bestT = 0L;
        for (NowPlaying.Line l : np.lines) {
            long d;
            if (l.text().isBlank() || (d = Math.abs(l.timeMs() - cur)) >= best) continue;
            best = d;
            bestT = l.timeMs();
        }
        c.lyricsOffsetMs = (int)Math.max(-5000L, Math.min(5000L, bestT - raw));
        NeonLyrics.saveConfig();
        Island.notify(Fx.t("neonlyrics.media.synced", (c.lyricsOffsetMs > 0 ? "+" : "") + c.lyricsOffsetMs), true);
    }

    public static void control(Cmd cmd) {
        if (NeonLyrics.cfg().demoLyrics) {
            SpotifyClient.restartDemo();
            return;
        }
        String label = switch (cmd.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> Fx.t("neonlyrics.media.next", new Object[0]);
            case 1 -> Fx.t("neonlyrics.media.prev", new Object[0]);
            case 2 -> SpotifyClient.now.playing ? Fx.t("neonlyrics.media.pause", new Object[0]) : Fx.t("neonlyrics.media.play", new Object[0]);
        };
        Island.notify(label, true);
        if (SpotifyClient.autoMode()) {
            LocalMedia.command(cmd.name().toLowerCase(Locale.ROOT));
            return;
        }
        exec.execute(() -> {
            try {
                Http.Response r;
                if (status != Status.CONNECTED) {
                    return;
                }
                if (System.currentTimeMillis() > SpotifyClient.tokens.expiresAt && !SpotifyClient.refresh()) {
                    return;
                }
                Map<String, String> h = Map.of("Authorization", "Bearer " + SpotifyClient.tokens.access);
                switch (cmd.ordinal()) {
                    default: {
                        throw new MatchException(null, null);
                    }
                    case 0: {
                        r = Http.empty("POST", "https://api.spotify.com/v1/me/player/next", h);
                        break;
                    }
                    case 1: {
                        r = Http.empty("POST", "https://api.spotify.com/v1/me/player/previous", h);
                        break;
                    }
                    case 2: {
                        r = Http.empty("PUT", "https://api.spotify.com/v1/me/player/" + (SpotifyClient.now.playing ? "pause" : "play"), h);
                    }
                }
                if (r.code() == 401 || r.code() == 403) {
                    class_310.method_1551().execute(() -> Island.notify(Fx.t("neonlyrics.media.noperm", new Object[0]), false));
                }
                backoffUntil = 0L;
                Thread.sleep(400L);
                SpotifyClient.poll();
            }
            catch (Exception e) {
                lastError = String.valueOf(e.getMessage());
            }
        });
    }

    public static boolean autoMode() {
        return NeonLyrics.cfg().spotifyMode == 0;
    }

    public static void setMode(int mode) {
        if (NeonLyrics.cfg().spotifyMode == mode) {
            return;
        }
        NeonLyrics.cfg().spotifyMode = mode;
        NeonLyrics.saveConfig();
        now = NowPlaying.EMPTY;
        if (mode != 0) {
            LocalMedia.stop();
        }
    }

    private static void fail(Exception e) {
        status = Status.ERROR;
        lastError = String.valueOf(e.getMessage());
        NeonLyrics.LOG.warn("Spotify-Login fehlgeschlagen", (Throwable)e);
        class_310.method_1551().execute(() -> Fx.error(Fx.t("neonlyrics.spotify.failed", lastError)));
    }

    private static Map<String, String> query(String q) {
        HashMap<String, String> m = new HashMap<String, String>();
        for (String p : q.split("&")) {
            int i = p.indexOf(61);
            if (i <= 0) continue;
            m.put(URLDecoder.decode(p.substring(0, i), StandardCharsets.UTF_8), URLDecoder.decode(p.substring(i + 1), StandardCharsets.UTF_8));
        }
        return m;
    }

    private static void respond(Socket s, int code, String html) throws Exception {
        byte[] b = html.getBytes(StandardCharsets.UTF_8);
        OutputStream out = s.getOutputStream();
        out.write(("HTTP/1.1 " + code + " OK\r\nContent-Type: text/html; charset=utf-8\r\nContent-Length: " + b.length + "\r\nConnection: close\r\n\r\n").getBytes(StandardCharsets.US_ASCII));
        out.write(b);
        out.flush();
    }

    private static String page(boolean ok) {
        return "<!doctype html><meta charset=utf-8><title>SpotifyCraft</title><body style=\"margin:0;height:100vh;display:grid;place-items:center;background:#08120e;color:#e3f6ec;font-family:system-ui,sans-serif\"><div style=\"text-align:center\"><div style=\"font-size:20px;font-weight:600\">" + (ok ? "Spotify ist verbunden" : "Verbindung fehlgeschlagen") + "</div><div style=\"margin-top:8px;color:#86ac9b\">" + (ok ? "Du kannst dieses Fenster schliessen und zurueck ins Spiel." : "Versuche es im Spiel noch einmal.") + "</div></div></body>";
    }

    static {
        demoStart = 0L;
    }

    public record Played(String title, String artist, long at) {
    }

    private static final class Tokens {
        String access = "";
        String refresh = "";
        long expiresAt;

        private Tokens() {
        }
    }

    public static enum Status {
        NOT_CONFIGURED,
        DISCONNECTED,
        WAITING_LOGIN,
        CONNECTED,
        ERROR;

    }

    public static enum Cmd {
        NEXT,
        PREVIOUS,
        PLAYPAUSE;

    }
}

