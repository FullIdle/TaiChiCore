package org.figsq.taichicore.taichicore.cef;

import lombok.Getter;
import lombok.val;
import net.minecraft.client.Minecraft;
import org.cef.*;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefKeyboardHandler;
import org.cef.misc.BoolRef;
import org.figsq.taichicore.taichicore.TaiChiCore;

import java.awt.*;
import java.io.File;
import java.util.HashSet;

public class TaiChiCefUtil {
    public static final String LIB_PATH_KEY = "jcef.path";

    public static final Component AWT_TAICHI_COMPONENT = new Component() {
        @Override
        public String getName() {
            return TaiChiCore.MOD_NAME;
        }
    };

    @Getter
    private static boolean initialized = false;

    @Getter
    private static CefApp cefApp;
    @Getter
    private static CefClient cefClient;

    private static HashSet<TaiChiCefBrowser> browserSet = new HashSet<>();

    public static void addBrowser(TaiChiCefBrowser browser) {
        browserSet.add(browser);
    }

    public static void removeBrowser(TaiChiCefBrowser browser) {
        browserSet.remove(browser);
    }

    public static boolean init() {
        if (initialized) return true;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            cefClient.dispose();
            cefApp.dispose();
            new HashSet<>(browserSet).forEach(b -> b.close(true));
        }));

        //TODO
        val folderPath = "C:\\Users\\COLORFUL\\Downloads\\windows-amd64\\bin\\lib";
        val separator = File.separator;
        val libPath = folderPath + separator + getOSLibName();

        System.setProperty(LIB_PATH_KEY, libPath);

        SystemBootstrap.setLoader(libname -> {
            try {
                System.load(libPath + separator + libname + ".dll");
            } catch (UnsatisfiedLinkError e) {
                System.loadLibrary(libname);
            }
        });

        //解除注释掉的参数，可以让cef彻底无限制渲染但消耗似乎有点大
        val args = new String[]{
                "--autoplay-policy=no-user-gesture-required",
                "--disable-web-security",
                "--enable-widevine-cdm",

                "--enable-html5-video",
                "--enable-hardware-video-decoding",
                "--enable-media-stream",
                "--enable-plugins",
                "--no-sandbox",
                "--disable-gpu-vsync",
//                "--disable-frame-rate-limit",             完全不限制了      不建议用
                "--enable-begin-frame-scheduling",
                "--enable-gpu-rasterization",
                "--enable-zero-copy",
                "--enable-gpu-compositing",
                "--disable-background-timer-throttling",
                "--disable-renderer-backgrounding",
        };

        val settings = new CefSettings();

        cefApp = CefApp.getInstance(args, settings);

        cefClient = cefApp.createClient();

        cefClient.addKeyboardHandler(new CefKeyboardHandler() {
            @Override
            public boolean onPreKeyEvent(CefBrowser browser, CefKeyEvent event, BoolRef is_keyboard_shortcut) {
                return false;
            }

            @Override
            public boolean onKeyEvent(CefBrowser browser, CefKeyEvent event) {
                System.out.println("[KeyEvent] " + event.toString() +
                        " character_int=" + (int) event.character +
                        " unmodified_int=" + (int) event.unmodified_character);
                return false;
            }
        });

        return initialized = true;
    }

    public static TaiChiCefBrowser createBrowser(String url, boolean isTransparent) {
        //获取MC设置的限制帧率
        return createBrowser(url, isTransparent, Math.max(Minecraft.getInstance().options.framerateLimit().get(), 0));
    }

    public static TaiChiCefBrowser createBrowser(String url, boolean isTransparent, int frameRate) {
        val settings = new CefBrowserSettings();
        settings.windowless_frame_rate = frameRate;
        val browser = new TaiChiCefBrowser(
                getCefClient(),
                url,
                isTransparent,
                null,
                null,
                null,
                settings
        );
        browser.setCloseAllowed();
        browser.createImmediately();
        return browser;
    }

    public static String getOSLibName() {
        if (OS.isWindows()) return "win64";
        throw new UnsupportedOperationException("Unsupported OS: " + OS.getOSType().name());
    }
}
