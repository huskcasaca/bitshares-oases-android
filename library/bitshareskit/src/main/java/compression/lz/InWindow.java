// LZ.InWindow

package compression.lz;

import java.io.IOException;

public class InWindow {
    public byte[] bufferBase; // pointer to buffer with data
    public int bufferOffset;
    public int blockSize;  // Size of Allocated memory block
    public int pos;             // offset (from _buffer) of curent byte
    public int streamPos;   // offset (from _buffer) of first not read byte from Stream
    java.io.InputStream stream;
    int posLimit;  // offset (from _buffer) of first byte when new block reading must be done
    boolean streamEndWasReached; // if (true) then _streamPos shows real end of stream
    int pointerToLastSafePosition;
    int keepSizeBefore;  // how many BYTEs must be kept in buffer before _pos
    int keepSizeAfter;   // how many BYTEs must be kept buffer after _pos

    public void moveBlock() {
        int offset = bufferOffset + pos - keepSizeBefore;
        // we need one additional byte, since MovePos moves on 1 byte.
        if (offset > 0)
            offset--;

        int numBytes = bufferOffset + streamPos - offset;

        // check negative offset ????
        if (numBytes >= 0) System.arraycopy(bufferBase, offset, bufferBase, 0, numBytes);
        bufferOffset -= offset;
    }

    public void readBlock() throws IOException {
        if (streamEndWasReached)
            return;
        while (true) {
            int size = -bufferOffset + blockSize - streamPos;
            if (size == 0)
                return;
            int numReadBytes = stream.read(bufferBase, bufferOffset + streamPos, size);
            if (numReadBytes == -1) {
                posLimit = streamPos;
                int pointerToPostion = bufferOffset + posLimit;
                if (pointerToPostion > pointerToLastSafePosition)
                    posLimit = pointerToLastSafePosition - bufferOffset;

                streamEndWasReached = true;
                return;
            }
            streamPos += numReadBytes;
            if (streamPos >= pos + keepSizeAfter)
                posLimit = streamPos - keepSizeAfter;
        }
    }

    void free() {
        bufferBase = null;
    }

    public void create(int keepSizeBefore, int keepSizeAfter, int keepSizeReserv) {
        this.keepSizeBefore = keepSizeBefore;
        this.keepSizeAfter = keepSizeAfter;
        int blockSize = keepSizeBefore + keepSizeAfter + keepSizeReserv;
        if (bufferBase == null || this.blockSize != blockSize) {
            free();
            this.blockSize = blockSize;
            bufferBase = new byte[this.blockSize];
        }
        pointerToLastSafePosition = this.blockSize - keepSizeAfter;
    }

    public void setStream(java.io.InputStream stream) {
        this.stream = stream;
    }

    public void releaseStream() {
        stream = null;
    }

    public void init() throws IOException {
        bufferOffset = 0;
        pos = 0;
        streamPos = 0;
        streamEndWasReached = false;
        readBlock();
    }

    public void movePos() throws IOException {
        pos++;
        if (pos > posLimit) {
            int pointerToPostion = bufferOffset + pos;
            if (pointerToPostion > pointerToLastSafePosition)
                moveBlock();
            readBlock();
        }
    }

    public byte getIndexByte(int index) {
        return bufferBase[bufferOffset + pos + index];
    }

    // index + limit have not to exceed _keepSizeAfter;
    public int getMatchLen(int index, int distance, int limit) {
        if (streamEndWasReached)
            if ((pos + index) + limit > streamPos)
                limit = streamPos - (pos + index);
        distance++;
        // Byte *pby = _buffer + (size_t)_pos + index;
        int pby = bufferOffset + pos + index;

        int i;
        for (i = 0; i < limit && bufferBase[pby + i] == bufferBase[pby + i - distance]; i++) ;
        return i;
    }

    public int getNumAvailableBytes() {
        return streamPos - pos;
    }

    public void reduceOffsets(int subValue) {
        bufferOffset += subValue;
        posLimit -= subValue;
        pos -= subValue;
        streamPos -= subValue;
    }
}
