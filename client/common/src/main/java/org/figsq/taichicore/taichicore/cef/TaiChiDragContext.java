/*
 * 来自 MCEF
 */

package org.figsq.taichicore.taichicore.cef;

import lombok.Getter;
import org.cef.callback.CefDragData;
import org.figsq.taichicore.taichicore.cef.misc.CefCursorType;

@Getter
public class TaiChiDragContext {
    private CefDragData dragData = null;
    private int dragMask = 0;
    private int cursorOverride = -1;
    private int actualCursor = -1;

    public int getVirtualModifiers(int btnMask) {
        return dragData != null ? 0 : btnMask;
    }

    public int getVirtualCursor(int cursorType) {
        actualCursor = cursorType;
        if (cursorOverride != -1) cursorType = cursorOverride;
        return cursorType;
    }

    public int getMask() {
        return dragMask;
    }

    public void startDragging(CefDragData dragData, int mask) {
        this.dragData = dragData;
        this.dragMask = mask;
    }

    public void stopDragging() {
        dragData.dispose();
        dragData = null;
        dragMask = 0;
        cursorOverride = -1;
    }

    public boolean isDragging() {
        return dragData != null;
    }

    public boolean updateCursor(int operation) {
        if (dragData == null) return false;

        int currentOverride = cursorOverride;

        switch (operation) {
            case 0:
                cursorOverride = CefCursorType.NO_DROP.ordinal();
                break;
            case 1:
                cursorOverride = CefCursorType.COPY.ordinal();
                break;
            case 16:
                cursorOverride = CefCursorType.MOVE.ordinal();
                break;
            default:
                cursorOverride = -1;
        }

        return currentOverride != cursorOverride && cursorOverride != -1;
    }
}
