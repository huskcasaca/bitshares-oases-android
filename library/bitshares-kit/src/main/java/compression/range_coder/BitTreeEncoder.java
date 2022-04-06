package compression.range_coder;

import java.io.IOException;

public class BitTreeEncoder {
    short[] models;
    int numBitLevels;

    public BitTreeEncoder(int numBitLevels) {
        this.numBitLevels = numBitLevels;
        models = new short[1 << numBitLevels];
    }

    public static int reverseGetPrice(short[] Models, int startIndex,
                                      int NumBitLevels, int symbol) {
        int price = 0;
        int m = 1;
        for (int i = NumBitLevels; i != 0; i--) {
            int bit = symbol & 1;
            symbol >>>= 1;
            price += Encoder.GetPrice(Models[startIndex + m], bit);
            m = (m << 1) | bit;
        }
        return price;
    }

    public static void reverseEncode(short[] Models, int startIndex,
                                     Encoder rangeEncoder, int NumBitLevels, int symbol) throws IOException {
        int m = 1;
        for (int i = 0; i < NumBitLevels; i++) {
            int bit = symbol & 1;
            rangeEncoder.Encode(Models, startIndex + m, bit);
            m = (m << 1) | bit;
            symbol >>= 1;
        }
    }

    public void init() {
        compression.range_coder.Decoder.initBitModels(models);
    }

    public void encode(Encoder rangeEncoder, int symbol) throws IOException {
        int m = 1;
        for (int bitIndex = numBitLevels; bitIndex != 0; ) {
            bitIndex--;
            int bit = (symbol >>> bitIndex) & 1;
            rangeEncoder.Encode(models, m, bit);
            m = (m << 1) | bit;
        }
    }

    public void reverseEncode(Encoder rangeEncoder, int symbol) throws IOException {
        int m = 1;
        for (int i = 0; i < numBitLevels; i++) {
            int bit = symbol & 1;
            rangeEncoder.Encode(models, m, bit);
            m = (m << 1) | bit;
            symbol >>= 1;
        }
    }

    public int getPrice(int symbol) {
        int price = 0;
        int m = 1;
        for (int bitIndex = numBitLevels; bitIndex != 0; ) {
            bitIndex--;
            int bit = (symbol >>> bitIndex) & 1;
            price += Encoder.GetPrice(models[m], bit);
            m = (m << 1) + bit;
        }
        return price;
    }

    public int reverseGetPrice(int symbol) {
        int price = 0;
        int m = 1;
        for (int i = numBitLevels; i != 0; i--) {
            int bit = symbol & 1;
            symbol >>>= 1;
            price += Encoder.GetPrice(models[m], bit);
            m = (m << 1) | bit;
        }
        return price;
    }
}
