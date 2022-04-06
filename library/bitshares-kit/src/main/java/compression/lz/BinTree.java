package compression.lz;

import java.io.IOException;

public class BinTree extends InWindow {
    static final int kHash2Size = 1 << 10;
    static final int kHash3Size = 1 << 16;
    static final int kBT2HashSize = 1 << 16;
    static final int kStartMaxLen = 1;
    static final int kHash3Offset = kHash2Size;
    static final int kEmptyHashValue = 0;
    static final int kMaxValForNormalize = (1 << 30) - 1;
    private static final int[] CrcTable = new int[256];

    static {
        for (int i = 0; i < 256; i++) {
            int r = i;
            for (int j = 0; j < 8; j++)
                if ((r & 1) != 0)
                    r = (r >>> 1) ^ 0xEDB88320;
                else
                    r >>>= 1;
            CrcTable[i] = r;
        }
    }

    int cyclicBufferPos;
    int cyclicBufferSize = 0;
    int matchMaxLen;
    int[] son;
    int[] hash;
    int cutValue = 0xFF;
    int hashMask;
    int hashSizeSum = 0;
    boolean hashArray = true;
    int kNumHashDirectBytes = 0;
    int kMinMatchCheck = 4;
    int kFixHashSize = kHash2Size + kHash3Size;

    public void setType(int numHashBytes) {
        hashArray = (numHashBytes > 2);
        if (hashArray) {
            kNumHashDirectBytes = 0;
            kMinMatchCheck = 4;
            kFixHashSize = kHash2Size + kHash3Size;
        } else {
            kNumHashDirectBytes = 2;
            kMinMatchCheck = 2 + 1;
            kFixHashSize = 0;
        }
    }

    public void init() throws IOException {
        super.init();
        for (int i = 0; i < hashSizeSum; i++)
            hash[i] = kEmptyHashValue;
        cyclicBufferPos = 0;
        reduceOffsets(-1);
    }

    public void movePos() throws IOException {
        if (++cyclicBufferPos >= cyclicBufferSize)
            cyclicBufferPos = 0;
        super.movePos();
        if (pos == kMaxValForNormalize)
            normalize();
    }

    public boolean create(int historySize, int keepAddBufferBefore,
                          int matchMaxLen, int keepAddBufferAfter) {
        if (historySize > kMaxValForNormalize - 256)
            return false;
        cutValue = 16 + (matchMaxLen >> 1);

        int windowReservSize = (historySize + keepAddBufferBefore +
                matchMaxLen + keepAddBufferAfter) / 2 + 256;

        super.create(historySize + keepAddBufferBefore, matchMaxLen + keepAddBufferAfter, windowReservSize);

        this.matchMaxLen = matchMaxLen;

        int cyclicBufferSize = historySize + 1;
        if (this.cyclicBufferSize != cyclicBufferSize)
            son = new int[(this.cyclicBufferSize = cyclicBufferSize) * 2];

        int hs = kBT2HashSize;

        if (hashArray) {
            hs = historySize - 1;
            hs |= (hs >> 1);
            hs |= (hs >> 2);
            hs |= (hs >> 4);
            hs |= (hs >> 8);
            hs >>= 1;
            hs |= 0xFFFF;
            if (hs > (1 << 24))
                hs >>= 1;
            hashMask = hs;
            hs++;
            hs += kFixHashSize;
        }
        if (hs != hashSizeSum)
            hash = new int[hashSizeSum = hs];
        return true;
    }

    public int getMatches(int[] distances) throws IOException {
        int lenLimit;
        if (pos + matchMaxLen <= streamPos)
            lenLimit = matchMaxLen;
        else {
            lenLimit = streamPos - pos;
            if (lenLimit < kMinMatchCheck) {
                movePos();
                return 0;
            }
        }

        int offset = 0;
        int matchMinPos = (pos > cyclicBufferSize) ? (pos - cyclicBufferSize) : 0;
        int cur = bufferOffset + pos;
        int maxLen = kStartMaxLen; // to avoid items for len < hashSize;
        int hashValue, hash2Value = 0, hash3Value = 0;

        if (hashArray) {
            int temp = CrcTable[bufferBase[cur] & 0xFF] ^ (bufferBase[cur + 1] & 0xFF);
            hash2Value = temp & (kHash2Size - 1);
            temp ^= ((int) (bufferBase[cur + 2] & 0xFF) << 8);
            hash3Value = temp & (kHash3Size - 1);
            hashValue = (temp ^ (CrcTable[bufferBase[cur + 3] & 0xFF] << 5)) & hashMask;
        } else
            hashValue = ((bufferBase[cur] & 0xFF) ^ ((int) (bufferBase[cur + 1] & 0xFF) << 8));

        int curMatch = hash[kFixHashSize + hashValue];
        if (hashArray) {
            int curMatch2 = hash[hash2Value];
            int curMatch3 = hash[kHash3Offset + hash3Value];
            hash[hash2Value] = pos;
            hash[kHash3Offset + hash3Value] = pos;
            if (curMatch2 > matchMinPos)
                if (bufferBase[bufferOffset + curMatch2] == bufferBase[cur]) {
                    distances[offset++] = maxLen = 2;
                    distances[offset++] = pos - curMatch2 - 1;
                }
            if (curMatch3 > matchMinPos)
                if (bufferBase[bufferOffset + curMatch3] == bufferBase[cur]) {
                    if (curMatch3 == curMatch2)
                        offset -= 2;
                    distances[offset++] = maxLen = 3;
                    distances[offset++] = pos - curMatch3 - 1;
                    curMatch2 = curMatch3;
                }
            if (offset != 0 && curMatch2 == curMatch) {
                offset -= 2;
                maxLen = kStartMaxLen;
            }
        }

        hash[kFixHashSize + hashValue] = pos;

        int ptr0 = (cyclicBufferPos << 1) + 1;
        int ptr1 = (cyclicBufferPos << 1);

        int len0, len1;
        len0 = len1 = kNumHashDirectBytes;

        if (kNumHashDirectBytes != 0) {
            if (curMatch > matchMinPos) {
                if (bufferBase[bufferOffset + curMatch + kNumHashDirectBytes] !=
                        bufferBase[cur + kNumHashDirectBytes]) {
                    distances[offset++] = maxLen = kNumHashDirectBytes;
                    distances[offset++] = pos - curMatch - 1;
                }
            }
        }

        int count = cutValue;

        while (true) {
            if (curMatch <= matchMinPos || count-- == 0) {
                son[ptr0] = son[ptr1] = kEmptyHashValue;
                break;
            }
            int delta = pos - curMatch;
            int cyclicPos = ((delta <= cyclicBufferPos) ?
                    (cyclicBufferPos - delta) :
                    (cyclicBufferPos - delta + cyclicBufferSize)) << 1;

            int pby1 = bufferOffset + curMatch;
            int len = Math.min(len0, len1);
            if (bufferBase[pby1 + len] == bufferBase[cur + len]) {
                while (++len != lenLimit)
                    if (bufferBase[pby1 + len] != bufferBase[cur + len])
                        break;
                if (maxLen < len) {
                    distances[offset++] = maxLen = len;
                    distances[offset++] = delta - 1;
                    if (len == lenLimit) {
                        son[ptr1] = son[cyclicPos];
                        son[ptr0] = son[cyclicPos + 1];
                        break;
                    }
                }
            }
            if ((bufferBase[pby1 + len] & 0xFF) < (bufferBase[cur + len] & 0xFF)) {
                son[ptr1] = curMatch;
                ptr1 = cyclicPos + 1;
                curMatch = son[ptr1];
                len1 = len;
            } else {
                son[ptr0] = curMatch;
                ptr0 = cyclicPos;
                curMatch = son[ptr0];
                len0 = len;
            }
        }
        movePos();
        return offset;
    }

    public void skip(int num) throws IOException {
        do {
            int lenLimit;
            if (pos + matchMaxLen <= streamPos)
                lenLimit = matchMaxLen;
            else {
                lenLimit = streamPos - pos;
                if (lenLimit < kMinMatchCheck) {
                    movePos();
                    continue;
                }
            }

            int matchMinPos = (pos > cyclicBufferSize) ? (pos - cyclicBufferSize) : 0;
            int cur = bufferOffset + pos;

            int hashValue;

            if (hashArray) {
                int temp = CrcTable[bufferBase[cur] & 0xFF] ^ (bufferBase[cur + 1] & 0xFF);
                int hash2Value = temp & (kHash2Size - 1);
                hash[hash2Value] = pos;
                temp ^= ((int) (bufferBase[cur + 2] & 0xFF) << 8);
                int hash3Value = temp & (kHash3Size - 1);
                hash[kHash3Offset + hash3Value] = pos;
                hashValue = (temp ^ (CrcTable[bufferBase[cur + 3] & 0xFF] << 5)) & hashMask;
            } else
                hashValue = ((bufferBase[cur] & 0xFF) ^ ((int) (bufferBase[cur + 1] & 0xFF) << 8));

            int curMatch = hash[kFixHashSize + hashValue];
            hash[kFixHashSize + hashValue] = pos;

            int ptr0 = (cyclicBufferPos << 1) + 1;
            int ptr1 = (cyclicBufferPos << 1);

            int len0, len1;
            len0 = len1 = kNumHashDirectBytes;

            int count = cutValue;
            while (true) {
                if (curMatch <= matchMinPos || count-- == 0) {
                    son[ptr0] = son[ptr1] = kEmptyHashValue;
                    break;
                }

                int delta = pos - curMatch;
                int cyclicPos = ((delta <= cyclicBufferPos) ?
                        (cyclicBufferPos - delta) :
                        (cyclicBufferPos - delta + cyclicBufferSize)) << 1;

                int pby1 = bufferOffset + curMatch;
                int len = Math.min(len0, len1);
                if (bufferBase[pby1 + len] == bufferBase[cur + len]) {
                    while (++len != lenLimit)
                        if (bufferBase[pby1 + len] != bufferBase[cur + len])
                            break;
                    if (len == lenLimit) {
                        son[ptr1] = son[cyclicPos];
                        son[ptr0] = son[cyclicPos + 1];
                        break;
                    }
                }
                if ((bufferBase[pby1 + len] & 0xFF) < (bufferBase[cur + len] & 0xFF)) {
                    son[ptr1] = curMatch;
                    ptr1 = cyclicPos + 1;
                    curMatch = son[ptr1];
                    len1 = len;
                } else {
                    son[ptr0] = curMatch;
                    ptr0 = cyclicPos;
                    curMatch = son[ptr0];
                    len0 = len;
                }
            }
            movePos();
        }
        while (--num != 0);
    }

    void normalizeLinks(int[] items, int numItems, int subValue) {
        for (int i = 0; i < numItems; i++) {
            int value = items[i];
            if (value <= subValue)
                value = kEmptyHashValue;
            else
                value -= subValue;
            items[i] = value;
        }
    }

    void normalize() {
        int subValue = pos - cyclicBufferSize;
        normalizeLinks(son, cyclicBufferSize * 2, subValue);
        normalizeLinks(hash, hashSizeSum, subValue);
        reduceOffsets(subValue);
    }

    public void SetCutValue(int cutValue) {
        this.cutValue = cutValue;
    }
}
