package org.figsq.taichicore.taichicore.cef.misc;

import lombok.Getter;
import lombok.val;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.SystemBootstrap;
import org.cef.browser.CefBrowser;
import org.figsq.taichicore.taichicore.TaiChiCore;
import org.figsq.taichicore.taichicore.cef.TaiChiBrowser;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

public class CefUtil {
    @Getter
    private static boolean initialized = false;

    @Getter
    private static CefApp cefAppInstance;

    @Getter
    private static CefClient cefClientInstance;

    public static boolean init() {
        try {
            val path = new File("C:\\Users\\COLORFUL\\Downloads\\windows-amd64\\bin\\lib\\win64\\").getCanonicalPath();
            System.setProperty("jcef.path", path);
            SystemBootstrap.setLoader(libname -> {
                try {
                    val filename = path + File.separator + libname + ".dll";
                    System.load(filename);
                } catch (UnsatisfiedLinkError e) {
                    System.loadLibrary(libname);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CefPlatform platform = CefPlatform.getPlatform();

        // Ensure binaries are executable
        if (platform.isLinux()) {
            File jcefHelperFile = new File(System.getProperty("mcef.libraries.path"), platform.getNormalizedName() + "/jcef_helper");
            setUnixExecutable(jcefHelperFile);
        } /*else if (platform.isMacOS()) {
            File mcefLibrariesPath = new File(System.getProperty("mcef.libraries.path"));
            File jcefHelperFile = new File(mcefLibrariesPath, platform.getNormalizedName() + "/jcef_app.app/Contents/Frameworks/jcef Helper.app/Contents/MacOS/jcef Helper");
            File jcefHelperGPUFile = new File(mcefLibrariesPath, platform.getNormalizedName() + "/jcef_app.app/Contents/Frameworks/jcef Helper (GPU).app/Contents/MacOS/jcef Helper (GPU)");
            File jcefHelperPluginFile = new File(mcefLibrariesPath, platform.getNormalizedName() + "/jcef_app.app/Contents/Frameworks/jcef Helper (Plugin).app/Contents/MacOS/jcef Helper (Plugin)");
            File jcefHelperRendererFile = new File(mcefLibrariesPath, platform.getNormalizedName() + "/jcef_app.app/Contents/Frameworks/jcef Helper (Renderer).app/Contents/MacOS/jcef Helper (Renderer)");
            setUnixExecutable(jcefHelperFile);
            setUnixExecutable(jcefHelperGPUFile);
            setUnixExecutable(jcefHelperPluginFile);
            setUnixExecutable(jcefHelperRendererFile);
        }*/

        String[] cefSwitches = new String[]{
                "--autoplay-policy=no-user-gesture-required",
                "--disable-web-security",
                "--enable-widevine-cdm",
                "--disable-gpu-vsync",
                "--disable-frame-rate-limit",
                "--off-screen-frame-rate=0",
                "--enable-begin-frame-scheduling",
                "--enable-gpu-rasterization",
                "--enable-zero-copy",
                "--enable-gpu-compositing",
                "--disable-background-timer-throttling",
                "--disable-renderer-backgrounding"
        };

        if (!CefApp.startup(cefSwitches)) {
            return false;
        }

        CefSettings cefSettings = new CefSettings();
        cefSettings.windowless_rendering_enabled = true;
        cefSettings.background_color = cefSettings.new ColorType(0, 255, 255, 255);
        cefSettings.user_agent_product = "TAICHI/1";

        cefAppInstance = CefApp.getInstance(cefSwitches, cefSettings);
        cefClientInstance = cefAppInstance.createClient();

        return initialized = true;
    }

    private static void setUnixExecutable(File file) {
        Set<PosixFilePermission> perms = new HashSet<>();
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);
        perms.add(PosixFilePermission.OWNER_EXECUTE);

        try {
            Files.setPosixFilePermissions(file.toPath(), perms);
        } catch (IOException e) {
            TaiChiCore.LOGGER.error("Failed to set {} as executable.", file, e);
        }
    }

    public static TaiChiBrowser createBrowser(String url, boolean transparent) {
        val browser = new TaiChiBrowser(getCefClientInstance(), url, transparent, true);
        browser.setCloseAllowed();
        browser.createImmediately();
        return browser;
    }
}
