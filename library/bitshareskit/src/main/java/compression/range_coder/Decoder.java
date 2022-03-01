package compression.range_coder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class Decoder {
    static final int kTopMask = ~((1 << 24) - 1);

    static final int kNumBitModelTotalBits = 11;
    static final int kBitModelTotal = (1 << kNumBitModelTotalBits);
    static final int kNumMoveBits = 5;

    int range;
    int code;

    InputStream stream;

    public static void initBitModels(short[] probs) {
        Arrays.fill(probs, (short) (kBitModelTotal >>> 1));
    }

    public final void setStream(InputStream stream) {
        this.stream = stream;
    }

    public final void releaseStream() {
        stream = null;
    }

    public final void init() throws IOException {
        code = 0;
        range = -1;
        for (int i = 0; i < 5; i++)
            code = (code << 8) | stream.read();
    }

    public final int decodeDirectBits(int numTotalBits) throws IOException {
        int result = 0;
        for (int i = numTotalBits; i != 0; i--) {
            range >>>= 1;
            int t = ((code - range) >>> 31);
            code -= range & (t - 1);
            result = (result << 1) | (1 - t);

            if ((range & kTopMask) == 0) {
                code = (code << 8) | stream.read();
                range <<= 8;
            }
        }
        return result;
    }

    public int decodeBit(short[] probs, int index) throws IOException {
        int prob = probs[index];
        int newBound = (range >>> kNumBitModelTotalBits) * prob;
        if ((code ^ 0x80000000) < (newBound ^ 0x80000000)) {
            range = newBound;
            probs[index] = (short) (prob + ((kBitModelTotal - prob) >>> kNumMoveBits));
            if ((range & kTopMask) == 0) {
                code = (code << 8) | stream.read();
                range <<= 8;
            }
            return 0;
        } else {
            range -= newBound;
            code -= newBound;
            probs[index] = (short) (prob - ((prob) >>> kNumMoveBits));
            if ((range & kTopMask) == 0) {
                code = (code << 8) | stream.read();
                range <<= 8;
            }
            return 1;
        }
    }
}
