package org.figsq.taichicore.taichicore;

import lombok.val;
import org.cef.OS;
import org.lwjgl.glfw.GLFW;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class GlfwToAwtKeyMapper {
    private static final Map<Integer, Integer> GLFW_TO_AWT = new HashMap<>();
    private static final Map<Integer, Integer> GLFW_TO_LOCATION = new HashMap<>();

    static {
        for (int i = GLFW.GLFW_KEY_A; i <= GLFW.GLFW_KEY_Z; i++) {
            GLFW_TO_AWT.put(i, KeyEvent.VK_A + (i - GLFW.GLFW_KEY_A));
            GLFW_TO_LOCATION.put(i, KeyEvent.KEY_LOCATION_STANDARD);
        }

        for (int i = GLFW.GLFW_KEY_0; i <= GLFW.GLFW_KEY_9; i++) {
            GLFW_TO_AWT.put(i, KeyEvent.VK_0 + (i - GLFW.GLFW_KEY_0));
            GLFW_TO_LOCATION.put(i, KeyEvent.KEY_LOCATION_STANDARD);
        }

        GLFW_TO_AWT.put(GLFW.GLFW_KEY_SPACE, KeyEvent.VK_SPACE);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_APOSTROPHE, KeyEvent.VK_QUOTE);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_COMMA, KeyEvent.VK_COMMA);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_MINUS, KeyEvent.VK_MINUS);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_PERIOD, KeyEvent.VK_PERIOD);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_SLASH, KeyEvent.VK_SLASH);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_SEMICOLON, KeyEvent.VK_SEMICOLON);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_EQUAL, KeyEvent.VK_EQUALS);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_LEFT_BRACKET, KeyEvent.VK_OPEN_BRACKET);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_RIGHT_BRACKET, KeyEvent.VK_CLOSE_BRACKET);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_BACKSLASH, KeyEvent.VK_BACK_SLASH);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_GRAVE_ACCENT, KeyEvent.VK_BACK_QUOTE);

        GLFW_TO_AWT.put(GLFW.GLFW_KEY_ESCAPE, KeyEvent.VK_ESCAPE);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_ENTER, KeyEvent.VK_ENTER);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_TAB, KeyEvent.VK_TAB);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_BACKSPACE, KeyEvent.VK_BACK_SPACE);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_INSERT, KeyEvent.VK_INSERT);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_DELETE, KeyEvent.VK_DELETE);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_HOME, KeyEvent.VK_HOME);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_END, KeyEvent.VK_END);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_PAGE_UP, KeyEvent.VK_PAGE_UP);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_PAGE_DOWN, KeyEvent.VK_PAGE_DOWN);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_CAPS_LOCK, KeyEvent.VK_CAPS_LOCK);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_NUM_LOCK, KeyEvent.VK_NUM_LOCK);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_SCROLL_LOCK, KeyEvent.VK_SCROLL_LOCK);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_PAUSE, KeyEvent.VK_PAUSE);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_PRINT_SCREEN, KeyEvent.VK_PRINTSCREEN);

        GLFW_TO_AWT.put(GLFW.GLFW_KEY_RIGHT, KeyEvent.VK_RIGHT);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_LEFT, KeyEvent.VK_LEFT);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_DOWN, KeyEvent.VK_DOWN);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_UP, KeyEvent.VK_UP);

        // 修饰键，区分左右
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_LEFT_SHIFT, KeyEvent.VK_SHIFT);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_RIGHT_SHIFT, KeyEvent.VK_SHIFT);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_LEFT_CONTROL, KeyEvent.VK_CONTROL);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_RIGHT_CONTROL, KeyEvent.VK_CONTROL);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_LEFT_ALT, KeyEvent.VK_ALT);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_RIGHT_ALT, KeyEvent.VK_ALT);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_LEFT_SUPER, KeyEvent.VK_META);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_RIGHT_SUPER, KeyEvent.VK_META);

        GLFW_TO_LOCATION.put(GLFW.GLFW_KEY_LEFT_SHIFT, KeyEvent.KEY_LOCATION_LEFT);
        GLFW_TO_LOCATION.put(GLFW.GLFW_KEY_RIGHT_SHIFT, KeyEvent.KEY_LOCATION_RIGHT);
        GLFW_TO_LOCATION.put(GLFW.GLFW_KEY_LEFT_CONTROL, KeyEvent.KEY_LOCATION_LEFT);
        GLFW_TO_LOCATION.put(GLFW.GLFW_KEY_RIGHT_CONTROL, KeyEvent.KEY_LOCATION_RIGHT);
        GLFW_TO_LOCATION.put(GLFW.GLFW_KEY_LEFT_ALT, KeyEvent.KEY_LOCATION_LEFT);
        GLFW_TO_LOCATION.put(GLFW.GLFW_KEY_RIGHT_ALT, KeyEvent.KEY_LOCATION_RIGHT);
        GLFW_TO_LOCATION.put(GLFW.GLFW_KEY_LEFT_SUPER, KeyEvent.KEY_LOCATION_LEFT);
        GLFW_TO_LOCATION.put(GLFW.GLFW_KEY_RIGHT_SUPER, KeyEvent.KEY_LOCATION_RIGHT);

        // F1~F12
        for (int i = 0; i < 12; i++) {
            GLFW_TO_AWT.put(GLFW.GLFW_KEY_F1 + i, KeyEvent.VK_F1 + i);
            GLFW_TO_LOCATION.put(GLFW.GLFW_KEY_F1 + i, KeyEvent.KEY_LOCATION_STANDARD);
        }
        // F13~F24（AWT 只安全支持到 F24）
        for (int i = 0; i < 12; i++) {
            GLFW_TO_AWT.put(GLFW.GLFW_KEY_F13 + i, KeyEvent.VK_F13 + i);
            GLFW_TO_LOCATION.put(GLFW.GLFW_KEY_F13 + i, KeyEvent.KEY_LOCATION_STANDARD);
        }

        // 小键盘数字
        for (int i = 0; i <= 9; i++) {
            int glfwKey = GLFW.GLFW_KEY_KP_0 + i;
            GLFW_TO_AWT.put(glfwKey, KeyEvent.VK_NUMPAD0 + i);
            GLFW_TO_LOCATION.put(glfwKey, KeyEvent.KEY_LOCATION_NUMPAD);
        }

        GLFW_TO_AWT.put(GLFW.GLFW_KEY_KP_DIVIDE, KeyEvent.VK_DIVIDE);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_KP_MULTIPLY, KeyEvent.VK_MULTIPLY);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_KP_SUBTRACT, KeyEvent.VK_SUBTRACT);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_KP_ADD, KeyEvent.VK_ADD);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_KP_DECIMAL, KeyEvent.VK_DECIMAL);
        GLFW_TO_AWT.put(GLFW.GLFW_KEY_KP_ENTER, KeyEvent.VK_ENTER);

        for (int key : new int[]{
                GLFW.GLFW_KEY_KP_DIVIDE, GLFW.GLFW_KEY_KP_MULTIPLY,
                GLFW.GLFW_KEY_KP_SUBTRACT, GLFW.GLFW_KEY_KP_ADD,
                GLFW.GLFW_KEY_KP_DECIMAL, GLFW.GLFW_KEY_KP_ENTER
        }) {
            GLFW_TO_LOCATION.put(key, KeyEvent.KEY_LOCATION_NUMPAD);
        }

        // 没有显式设置 location 的键，补全为 STANDARD
        for (Integer key : GLFW_TO_AWT.keySet()) {
            GLFW_TO_LOCATION.putIfAbsent(key, KeyEvent.KEY_LOCATION_STANDARD);
        }
    }

    public static int toAwtKeyCode(int glfwKey) {
        return GLFW_TO_AWT.getOrDefault(glfwKey, KeyEvent.VK_UNDEFINED);
    }

    public static int getKeyLocation(int glfwKey) {
        return GLFW_TO_LOCATION.getOrDefault(glfwKey, KeyEvent.KEY_LOCATION_UNKNOWN);
    }

    public static int toAwtModifiers(int glfwModifiers) {
        int result = 0;
        if ((glfwModifiers & GLFW.GLFW_MOD_SHIFT) != 0)   result |= InputEvent.SHIFT_DOWN_MASK;
        if ((glfwModifiers & GLFW.GLFW_MOD_CONTROL) != 0) result |= InputEvent.CTRL_DOWN_MASK;
        if ((glfwModifiers & GLFW.GLFW_MOD_ALT) != 0)     result |= InputEvent.ALT_DOWN_MASK;
        if ((glfwModifiers & GLFW.GLFW_MOD_SUPER) != 0)   result |= InputEvent.META_DOWN_MASK;
        return result;
    }

    /**
     * 创建 KEY_PRESSED / KEY_RELEASED 事件
     */
    public static KeyEvent createKeyEvent(Component source, int glfwKey, int glfwModifiers, int scanCode, boolean onKeyRelease) {
        int awtKeyCode = toAwtKeyCode(glfwKey);
        int awtModifiers = toAwtModifiers(glfwModifiers);
        int location = getKeyLocation(glfwKey);

        val keyStroke = KeyStroke.getKeyStroke(
                awtKeyCode,
                awtModifiers,
                onKeyRelease
        );

        val event = new KeyEvent(
                source,
                keyStroke.getKeyEventType(),
                System.currentTimeMillis(),
                keyStroke.getModifiers(),
                keyStroke.getKeyCode(),
                keyStroke.getKeyChar(),
                location
        );

        if (OS.isWindows()) setScanCode(event, scanCode & 0xFF);

        return event;
    }

    /**
     * 创建 KEY_TYPED 事件
     */
    public static KeyEvent createCharTypedEvent(Component source, char codePoint) {
        val keyStroke = KeyStroke.getKeyStroke(codePoint);
        return new KeyEvent(
                source,
                keyStroke.getKeyEventType(),
                System.currentTimeMillis(),
                keyStroke.getModifiers(),
                keyStroke.getKeyCode(),
                keyStroke.getKeyChar(),
                KeyEvent.KEY_LOCATION_UNKNOWN
        );
    }

    private static void setScanCode(KeyEvent event, long scanCode) {
        try {
            java.lang.reflect.Field unsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            sun.misc.Unsafe unsafe = (sun.misc.Unsafe) unsafeField.get(null);

            java.lang.reflect.Field scancodeField = KeyEvent.class.getDeclaredField("scancode");
            long offset = unsafe.objectFieldOffset(scancodeField);
            unsafe.putLong(event, offset, scanCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}