package compression.lzma;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import compression.lz.OutWindow;
import compression.range_coder.BitTreeDecoder;

public class LzmaDecoder {
    OutWindow mOutWindow = new OutWindow();
    compression.range_coder.Decoder mRangeDecoder = new compression.range_coder.Decoder();
    short[] mIsMatchDecoders = new short[Base.kNumStates << Base.kNumPosStatesBitsMax];
    short[] mIsRepDecoders = new short[Base.kNumStates];
    short[] mIsRepG0Decoders = new short[Base.kNumStates];
    short[] mIsRepG1Decoders = new short[Base.kNumStates];
    short[] mIsRepG2Decoders = new short[Base.kNumStates];
    short[] mIsRep0LongDecoders = new short[Base.kNumStates << Base.kNumPosStatesBitsMax];
    BitTreeDecoder[] mPosSlotDecoder = new BitTreeDecoder[Base.kNumLenToPosStates];
    short[] mPosDecoders = new short[Base.kNumFullDistances - Base.kEndPosModelIndex];
    BitTreeDecoder mPosAlignDecoder = new BitTreeDecoder(Base.kNumAlignBits);
    LenDecoder mLenDecoder = new LenDecoder();
    LenDecoder mRepLenDecoder = new LenDecoder();
    LiteralDecoder mLiteralDecoder = new LiteralDecoder();
    int mDictionarySize = -1;
    int mDictionarySizeCheck = -1;
    int mPosStateMask;
    public LzmaDecoder() {
        for (int i = 0; i < Base.kNumLenToPosStates; i++)
            mPosSlotDecoder[i] = new BitTreeDecoder(Base.kNumPosSlotBits);
    }

    boolean setDictionarySize(int dictionarySize) {
        if (dictionarySize < 0)
            return false;
        if (mDictionarySize != dictionarySize) {
            mDictionarySize = dictionarySize;
            mDictionarySizeCheck = Math.max(mDictionarySize, 1);
            mOutWindow.create(Math.max(mDictionarySizeCheck, (1 << 12)));
        }
        return true;
    }

    boolean setLcLpPb(int lc, int lp, int pb) {
        if (lc > Base.kNumLitContextBitsMax || lp > 4 || pb > Base.kNumPosStatesBitsMax)
            return false;
        mLiteralDecoder.create(lp, lc);
        int numPosStates = 1 << pb;
        mLenDecoder.create(numPosStates);
        mRepLenDecoder.create(numPosStates);
        mPosStateMask = numPosStates - 1;
        return true;
    }

    void init() throws IOException {
        mOutWindow.init(false);

        compression.range_coder.Decoder.initBitModels(mIsMatchDecoders);
        compression.range_coder.Decoder.initBitModels(mIsRep0LongDecoders);
        compression.range_coder.Decoder.initBitModels(mIsRepDecoders);
        compression.range_coder.Decoder.initBitModels(mIsRepG0Decoders);
        compression.range_coder.Decoder.initBitModels(mIsRepG1Decoders);
        compression.range_coder.Decoder.initBitModels(mIsRepG2Decoders);
        compression.range_coder.Decoder.initBitModels(mPosDecoders);

        mLiteralDecoder.init();
        int i;
        for (i = 0; i < Base.kNumLenToPosStates; i++)
            mPosSlotDecoder[i].init();
        mLenDecoder.init();
        mRepLenDecoder.init();
        mPosAlignDecoder.init();
        mRangeDecoder.init();
    }

    public boolean decode(InputStream inStream, OutputStream outStream,
                          long outSize) throws IOException {
        mRangeDecoder.setStream(inStream);
        mOutWindow.setStream(outStream);
        init();

        int state = Base.stateInit();
        int rep0 = 0, rep1 = 0, rep2 = 0, rep3 = 0;

        long nowPos64 = 0;
        byte prevByte = 0;
        while (outSize < 0 || nowPos64 < outSize) {
            int posState = (int) nowPos64 & mPosStateMask;
            if (mRangeDecoder.decodeBit(mIsMatchDecoders, (state << Base.kNumPosStatesBitsMax) + posState) == 0) {
                LiteralDecoder.Decoder2 decoder2 = mLiteralDecoder.getDecoder((int) nowPos64, prevByte);
                if (!Base.stateIsCharState(state))
                    prevByte = decoder2.decodeWithMatchByte(mRangeDecoder, mOutWindow.getByte(rep0));
                else
                    prevByte = decoder2.decodeNormal(mRangeDecoder);
                mOutWindow.putByte(prevByte);
                state = Base.stateUpdateChar(state);
                nowPos64++;
            } else {
                int len;
                if (mRangeDecoder.decodeBit(mIsRepDecoders, state) == 1) {
                    len = 0;
                    if (mRangeDecoder.decodeBit(mIsRepG0Decoders, state) == 0) {
                        if (mRangeDecoder.decodeBit(mIsRep0LongDecoders, (state << Base.kNumPosStatesBitsMax) + posState) == 0) {
                            state = Base.stateUpdateShortRep(state);
                            len = 1;
                        }
                    } else {
                        int distance;
                        if (mRangeDecoder.decodeBit(mIsRepG1Decoders, state) == 0)
                            distance = rep1;
                        else {
                            if (mRangeDecoder.decodeBit(mIsRepG2Decoders, state) == 0)
                                distance = rep2;
                            else {
                                distance = rep3;
                                rep3 = rep2;
                            }
                            rep2 = rep1;
                        }
                        rep1 = rep0;
                        rep0 = distance;
                    }
                    if (len == 0) {
                        len = mRepLenDecoder.decode(mRangeDecoder, posState) + Base.kMatchMinLen;
                        state = Base.stateUpdateRep(state);
                    }
                } else {
                    rep3 = rep2;
                    rep2 = rep1;
                    rep1 = rep0;
                    len = Base.kMatchMinLen + mLenDecoder.decode(mRangeDecoder, posState);
                    state = Base.stateUpdateMatch(state);
                    int posSlot = mPosSlotDecoder[Base.getLenToPosState(len)].decode(mRangeDecoder);
                    if (posSlot >= Base.kStartPosModelIndex) {
                        int numDirectBits = (posSlot >> 1) - 1;
                        rep0 = ((2 | (posSlot & 1)) << numDirectBits);
                        if (posSlot < Base.kEndPosModelIndex)
                            rep0 += BitTreeDecoder.reverseDecode(mPosDecoders,
                                    rep0 - posSlot - 1, mRangeDecoder, numDirectBits);
                        else {
                            rep0 += (mRangeDecoder.decodeDirectBits(
                                    numDirectBits - Base.kNumAlignBits) << Base.kNumAlignBits);
                            rep0 += mPosAlignDecoder.reverseDecode(mRangeDecoder);
                            if (rep0 < 0) {
                                if (rep0 == -1)
                                    break;
                                return false;
                            }
                        }
                    } else
                        rep0 = posSlot;
                }
                if (rep0 >= nowPos64 || rep0 >= mDictionarySizeCheck) {
                    // m_OutWindow.Flush();
                    return false;
                }
                mOutWindow.copyBlock(rep0, len);
                nowPos64 += len;
                prevByte = mOutWindow.getByte(0);
            }
        }
        mOutWindow.flush();
        mOutWindow.releaseStream();
        mRangeDecoder.releaseStream();
        return true;
    }

    public boolean setDecoderProperties(byte[] properties) {
        if (properties.length < 5)
            return false;
        int val = properties[0] & 0xFF;
        int lc = val % 9;
        int remainder = val / 9;
        int lp = remainder % 5;
        int pb = remainder / 5;
        int dictionarySize = 0;
        for (int i = 0; i < 4; i++)
            dictionarySize += ((int) (properties[1 + i]) & 0xFF) << (i * 8);
        if (!setLcLpPb(lc, lp, pb))
            return false;
        return setDictionarySize(dictionarySize);
    }

    class LenDecoder {
        short[] mChoice = new short[2];
        BitTreeDecoder[] mLowCoder = new BitTreeDecoder[Base.kNumPosStatesMax];
        BitTreeDecoder[] mMidCoder = new BitTreeDecoder[Base.kNumPosStatesMax];
        BitTreeDecoder mHighCoder = new BitTreeDecoder(Base.kNumHighLenBits);
        int mNumPosStates = 0;

        public void create(int numPosStates) {
            for (; mNumPosStates < numPosStates; mNumPosStates++) {
                mLowCoder[mNumPosStates] = new BitTreeDecoder(Base.kNumLowLenBits);
                mMidCoder[mNumPosStates] = new BitTreeDecoder(Base.kNumMidLenBits);
            }
        }

        public void init() {
            compression.range_coder.Decoder.initBitModels(mChoice);
            for (int posState = 0; posState < mNumPosStates; posState++) {
                mLowCoder[posState].init();
                mMidCoder[posState].init();
            }
            mHighCoder.init();
        }

        public int decode(compression.range_coder.Decoder rangeDecoder, int posState) throws IOException {
            if (rangeDecoder.decodeBit(mChoice, 0) == 0)
                return mLowCoder[posState].decode(rangeDecoder);
            int symbol = Base.kNumLowLenSymbols;
            if (rangeDecoder.decodeBit(mChoice, 1) == 0)
                symbol += mMidCoder[posState].decode(rangeDecoder);
            else
                symbol += Base.kNumMidLenSymbols + mHighCoder.decode(rangeDecoder);
            return symbol;
        }
    }

    class LiteralDecoder {
        Decoder2[] mCoders;
        int mNumPrevBits;
        int mNumPosBits;
        int mPosMask;

        public void create(int numPosBits, int numPrevBits) {
            if (mCoders != null && mNumPrevBits == numPrevBits && mNumPosBits == numPosBits)
                return;
            mNumPosBits = numPosBits;
            mPosMask = (1 << numPosBits) - 1;
            mNumPrevBits = numPrevBits;
            int numStates = 1 << (mNumPrevBits + mNumPosBits);
            mCoders = new Decoder2[numStates];
            for (int i = 0; i < numStates; i++)
                mCoders[i] = new Decoder2();
        }

        public void init() {
            int numStates = 1 << (mNumPrevBits + mNumPosBits);
            for (int i = 0; i < numStates; i++)
                mCoders[i].init();
        }

        Decoder2 getDecoder(int pos, byte prevByte) {
            return mCoders[((pos & mPosMask) << mNumPrevBits) + ((prevByte & 0xFF) >>> (8 - mNumPrevBits))];
        }

        class Decoder2 {
            short[] mDecoders = new short[0x300];

            public void init() {
                compression.range_coder.Decoder.initBitModels(mDecoders);
            }

            public byte decodeNormal(compression.range_coder.Decoder rangeDecoder) throws IOException {
                int symbol = 1;
                do
                    symbol = (symbol << 1) | rangeDecoder.decodeBit(mDecoders, symbol);
                while (symbol < 0x100);
                return (byte) symbol;
            }

            public byte decodeWithMatchByte(compression.range_coder.Decoder rangeDecoder, byte matchByte) throws IOException {
                int symbol = 1;
                do {
                    int matchBit = (matchByte >> 7) & 1;
                    matchByte <<= 1;
                    int bit = rangeDecoder.decodeBit(mDecoders, ((1 + matchBit) << 8) + symbol);
                    symbol = (symbol << 1) | bit;
                    if (matchBit != bit) {
                        while (symbol < 0x100)
                            symbol = (symbol << 1) | rangeDecoder.decodeBit(mDecoders, symbol);
                        break;
                    }
                }
                while (symbol < 0x100);
                return (byte) symbol;
            }
        }
    }
}
