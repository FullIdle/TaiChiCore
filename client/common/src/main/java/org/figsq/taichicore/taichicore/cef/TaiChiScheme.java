package org.figsq.taichicore.taichicore.cef;

import com.cinemamod.mcef.MIMEUtil;
import com.cinemamod.mcef.ModScheme;
import lombok.Getter;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandler;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;
import org.figsq.taichicore.taichicore.TaiChiScreen;

/**
 * 最终修复版：解决视口与读取坐标不匹配导致的透明图片问题
 * @see ModScheme
 */
@Getter
public class TaiChiScheme implements CefResourceHandler {
    private String contentType;
    private byte[] bytes;
    private final String url;
    private int readOffset = 0;

    public TaiChiScheme(String url) {
        this.url = url;
    }

    @Override
    public boolean processRequest(CefRequest cefRequest, CefCallback cefCallback) {
        val url = this.url.substring("taichi://".length());
        System.out.println("url = " + url);
        int pos;

        this.contentType = null;
        pos = url.lastIndexOf(46);
        if (pos >= 0 && pos < url.length() - 2) {
            this.contentType = MIMEUtil.mimeFromExtension(url.substring(pos + 1));
        }

        val instance = Minecraft.getInstance();
        instance.execute(() -> {
            val itemStack = new ItemStack(Items.GLASS);
            itemStack.setCount(64);
            if (!(instance.screen instanceof TaiChiScreen screen)) {
                cefCallback.cancel();
                return;
            }
            screen.schemeTasks.add((guiGraphics, mouseX, mouseY, f) -> {
                //TODO
                this.bytes = null;
                cefCallback.Continue();
            });
        });

        return true;
    }

    @Override
    public void getResponseHeaders(CefResponse cefResponse, IntRef contentLength, StringRef redir) {
        if (this.contentType != null) cefResponse.setMimeType(this.contentType);
        cefResponse.setStatus(200);
        cefResponse.setStatusText("OK");
        contentLength.set(bytes != null ? bytes.length : 0);
    }

    @Override
    public boolean readResponse(byte[] output, int bytesToRead, IntRef bytesRead, CefCallback cefCallback) {
        if (bytes == null || readOffset >= bytes.length) {
            bytesRead.set(0);
            return false;
        }

        int remainingBytes = bytes.length - readOffset;
        int bytesToCopy = Math.min(bytesToRead, remainingBytes);

        System.arraycopy(bytes, readOffset, output, 0, bytesToCopy);

        readOffset += bytesToCopy;
        bytesRead.set(bytesToCopy);

        return readOffset >= bytes.length;
    }

    @Override
    public void cancel() {
        this.bytes = null;
        this.readOffset = 0;
    }
}