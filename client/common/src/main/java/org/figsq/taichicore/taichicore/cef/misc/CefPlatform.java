/*
 * 来自 MCEF
 */

package org.figsq.taichicore.taichicore.cef.misc;

import java.util.Locale;

public enum CefPlatform {
    LINUX_AMD64,
    LINUX_ARM64,
    WINDOWS_AMD64,
    WINDOWS_ARM64,
    MACOS_AMD64,
    MACOS_ARM64;

    public String getNormalizedName() {
        return name().toLowerCase(Locale.US);
    }

    public boolean isLinux() {
        return (this == LINUX_AMD64 || this == LINUX_ARM64);
    }

    public boolean isWindows() {
        return (this == WINDOWS_AMD64 || this == WINDOWS_ARM64);
    }

    public boolean isMacOS() {
        return (this == MACOS_AMD64 || this == MACOS_ARM64);
    }

    public static CefPlatform getPlatform() {
        String os = System.getProperty("os.name").toLowerCase(Locale.US);
        String arch = System.getProperty("os.arch").toLowerCase(Locale.US);

        if (os.startsWith("linux")) {
            if (arch.equals("amd64")) {
                return LINUX_AMD64;
            } else if (arch.equals("aarch64")) {
                return LINUX_ARM64;
            }
        } else if (os.startsWith("windows")) {
            if (arch.equals("amd64")) {
                return WINDOWS_AMD64;
            } else if (arch.equals("aarch64")) {
                return WINDOWS_ARM64;
            }
        } else if (os.startsWith("mac os x")) {
            if (arch.equals("x86_64")) {
                return MACOS_AMD64;
            } else if (arch.equals("aarch64")) {
                return MACOS_ARM64;
            }
        }

        throw new RuntimeException("Unsupported platform: " + os + " " + arch);
    }
}
