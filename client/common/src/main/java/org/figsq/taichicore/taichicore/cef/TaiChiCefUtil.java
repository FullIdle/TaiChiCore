package org.figsq.taichicore.taichicore.cef;

import lombok.Getter;
import lombok.val;
import net.minecraft.client.Minecraft;
import org.cef.*;
import org.cef.callback.CefSchemeRegistrar;
import org.cef.handler.CefAppHandlerAdapter;
import org.figsq.taichicore.taichicore.TaiChiCore;
import org.figsq.taichicore.taichicore.cef.handler.TaiChiCefDisplayHandler;
import org.figsq.taichicore.taichicore.cef.scheme.TaiChiResourceHandler;
import org.figsq.taichicore.taichicore.cef.handler.query.TaiChiCefQueryHandler;

import java.awt.*;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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

    private static final HashSet<TaiChiCefBrowser> browserSet = new HashSet<>();

    public static void addBrowser(TaiChiCefBrowser browser) {
        browserSet.add(browser);
    }

    public static void removeBrowser(TaiChiCefBrowser browser) {
        browserSet.remove(browser);
    }

    public static Set<TaiChiCefBrowser> getBrowserSet() {
        return Collections.unmodifiableSet(browserSet);
    }

    public static boolean init() {
        if (initialized) return true;

        //TODO
        val gamePath = Minecraft.getInstance().gameDirectory.toPath().toAbsolutePath();
        val dataPath = gamePath.resolve("mods").resolve("taichi-data").normalize();
        val libPath = dataPath.resolve("library");

        System.setProperty(LIB_PATH_KEY, libPath.toString());

        SystemBootstrap.setLoader(libname -> {
            try {
                System.load(libPath.resolve(libname + ".dll").toString());
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

        val rootCachePath = dataPath.resolve("root-cache");
        settings.root_cache_path = rootCachePath.toString();
        settings.cache_path = rootCachePath.resolve("cache").toString();

        CefApp.addAppHandler(new CefAppHandlerAdapter(null) {
            @Override
            public void onRegisterCustomSchemes(CefSchemeRegistrar registrar) {
                registrar.addCustomScheme(
                        "taichi",
                        true,
                        false,
                        false,
                        true,
                        true,
                        false,
                        true
                );
                super.onRegisterCustomSchemes(registrar);
            }

            @Override
            public void stateHasChanged(CefApp.CefAppState state) {
                if (state == CefApp.CefAppState.INITIALIZED)
                    cefApp.registerSchemeHandlerFactory("taichi", "", TaiChiResourceHandler::new);
            }
        });
        cefApp = CefApp.getInstance(args, settings);

        val version = cefApp.getVersion();
        assert version != null;
        TaiChiCore.LOGGER.info("cef-version: {}", version.getCefVersion());
        TaiChiCore.LOGGER.info("chrome-version: {}", version.getChromeVersion());
        TaiChiCore.LOGGER.info("jcef-version: {}", version.getJcefVersion());

        cefClient = cefApp.createClient();

        cefClient.addMessageRouter(TaiChiCefQueryHandler.ROUTER);
        cefClient.addDisplayHandler(TaiChiCefDisplayHandler.INSTANCE);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            cefClient.dispose();
            cefApp.dispose();
            new HashSet<>(browserSet).forEach(b -> b.close(true));
        }));

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

    public static void updateAllFrameRateLimit(int newFrameRateLimit) {
        browserSet.forEach(b -> b.setWindowlessFrameRate(newFrameRateLimit));
    }
}
