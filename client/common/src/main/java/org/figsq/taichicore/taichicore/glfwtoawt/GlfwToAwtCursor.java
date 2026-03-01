package org.figsq.taichicore.taichicore.glfwtoawt;

import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GlfwToAwtCursor {
    public static final Map<Integer, Long> cache = new HashMap<>();

    //只能搞出这么多了 AWT NS的都没有... CEF传递的 NS类型的光标也会直接值为0
    public static int toGlfwCursorType(int awtCursorType) {
        return switch (awtCursorType) {
            case Cursor.CROSSHAIR_CURSOR -> GLFW.GLFW_CROSSHAIR_CURSOR;
            case Cursor.TEXT_CURSOR -> GLFW.GLFW_IBEAM_CURSOR;
            case Cursor.SW_RESIZE_CURSOR, Cursor.NE_RESIZE_CURSOR -> GLFW.GLFW_RESIZE_NESW_CURSOR;
            case Cursor.SE_RESIZE_CURSOR, Cursor.NW_RESIZE_CURSOR -> GLFW.GLFW_RESIZE_NWSE_CURSOR;
            case Cursor.N_RESIZE_CURSOR, Cursor.S_RESIZE_CURSOR -> GLFW.GLFW_RESIZE_NS_CURSOR;
            case Cursor.W_RESIZE_CURSOR, Cursor.E_RESIZE_CURSOR -> GLFW.GLFW_RESIZE_EW_CURSOR;
            case Cursor.HAND_CURSOR -> GLFW.GLFW_POINTING_HAND_CURSOR;
            case Cursor.MOVE_CURSOR -> GLFW.GLFW_RESIZE_ALL_CURSOR;
            default -> GLFW.GLFW_ARROW_CURSOR;
        };
    }

    public static long glfwCursor(int awtCursorType) {
        return cache.computeIfAbsent(toGlfwCursorType(awtCursorType), GLFW::glfwCreateStandardCursor);
    }
}
