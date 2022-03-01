// LZ.OutWindow

package compression.lz;

import java.io.IOException;
import java.io.OutputStream;

public class OutWindow {
    byte[] buffer;
    int pos;
    int windowSize = 0;
    int streamPos;
    OutputStream _stream;

    public void create(int windowSize) {
        if (buffer == null || this.windowSize != windowSize)
            buffer = new byte[windowSize];
        this.windowSize = windowSize;
        pos = 0;
        streamPos = 0;
    }

    public void setStream(OutputStream stream) throws IOException {
        releaseStream();
        _stream = stream;
    }

    public void releaseStream() throws IOException {
        flush();
        _stream = null;
    }

    public void init(boolean solid) {
        if (!solid) {
            streamPos = 0;
            pos = 0;
        }
    }

    public void flush() throws IOException {
        int size = pos - streamPos;
        if (size == 0)
            return;
        _stream.write(buffer, streamPos, size);
        if (pos >= windowSize)
            pos = 0;
        streamPos = pos;
    }

    public void copyBlock(int distance, int len) throws IOException {
        int pos = this.pos - distance - 1;
        if (pos < 0)
            pos += windowSize;
        for (; len != 0; len--) {
            if (pos >= windowSize)
                pos = 0;
            buffer[this.pos++] = buffer[pos++];
            if (this.pos >= windowSize)
                flush();
        }
    }

    public void putByte(byte b) throws IOException {
        buffer[pos++] = b;
        if (pos >= windowSize)
            flush();
    }

    public byte getByte(int distance) {
        int pos = this.pos - distance - 1;
        if (pos < 0)
            pos += windowSize;
        return buffer[pos];
    }
}
