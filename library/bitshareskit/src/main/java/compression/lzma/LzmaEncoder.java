package compression.lzma;

import java.io.IOException;

import compression.range_coder.BitTreeEncoder;
import compression.tools.Progress;

public class LzmaEncoder {
    public static final int EMatchFinderTypeBT2 = 0;
    public static final int EMatchFinderTypeBT4 = 1;
    public static final int kNumLenSpecSymbols = Base.kNumLowLenSymbols + Base.kNumMidLenSymbols;
    public static final int kPropSize = 5;
    static final int kInfinityPrice = 0xFFFFFFF;
    static final int kDefaultDictionaryLogSize = 22;
    static final int kNumFastBytesDefault = 0x20;
    static final int kNumOpts = 1 << 12;
    static byte[] g_FastPos = new byte[1 << 11];

    static {
        int kFastSlots = 22;
        int c = 2;
        g_FastPos[0] = 0;
        g_FastPos[1] = 1;
        for (int slotFast = 2; slotFast < kFastSlots; slotFast++) {
            int k = (1 << ((slotFast >> 1) - 1));
            for (int j = 0; j < k; j++, c++)
                g_FastPos[c] = (byte) slotFast;
        }
    }

    int mState = Base.stateInit();
    byte previousByte;
    int[] repDistances = new int[Base.kNumRepDistances];
    Optimal[] optimum = new Optimal[kNumOpts];
    compression.lz.BinTree _matchFinder = null;

    ;
    compression.range_coder.Encoder _rangeEncoder = new compression.range_coder.Encoder();
    short[] isMatch = new short[Base.kNumStates << Base.kNumPosStatesBitsMax];
    short[] isRep = new short[Base.kNumStates];
    short[] isRepG0 = new short[Base.kNumStates];

    ;
    short[] isRepG1 = new short[Base.kNumStates];
    short[] isRepG2 = new short[Base.kNumStates];
    short[] isRep0Long = new short[Base.kNumStates << Base.kNumPosStatesBitsMax];
    BitTreeEncoder[] mPosSlotEncoder = new BitTreeEncoder[Base.kNumLenToPosStates]; // kNumPosSlotBits
    short[] mPosEncoders = new short[Base.kNumFullDistances - Base.kEndPosModelIndex];
    BitTreeEncoder mPosAlignEncoder = new BitTreeEncoder(Base.kNumAlignBits);
    LenPriceTableEncoder mLenEncoder = new LenPriceTableEncoder();
    LenPriceTableEncoder mRepMatchLenEncoder = new LenPriceTableEncoder();
    LiteralEncoder mLiteralEncoder = new LiteralEncoder();
    int[] mMatchDistances = new int[Base.kMatchMaxLen * 2 + 2];
    int numFastBytes = kNumFastBytesDefault;
    int mLongestMatchLength;
    int numDistancePairs;
    int mAdditionalOffset;
    int mOptimumEndIndex;
    int mOptimumCurrentIndex;
    boolean mLongestMatchWasFound;
    int[] mPosSlotPrices = new int[1 << (Base.kNumPosSlotBits + Base.kNumLenToPosStatesBits)];
    int[] mDistancesPrices = new int[Base.kNumFullDistances << Base.kNumLenToPosStatesBits];
    int[] mAlignPrices = new int[Base.kAlignTableSize];
    int mAlignPriceCount;
    int mDistTableSize = (kDefaultDictionaryLogSize * 2);
    int mPosStateBits = 2;
    int mPosStateMask = (4 - 1);
    int numLiteralPosStateBits = 0;
    int numLiteralContextBits = 3;
    int mDictionarySize = (1 << kDefaultDictionaryLogSize);
    int mDictionarySizePrev = -1;
    int numFastBytesPrev = -1;
    long nowPos64;
    boolean mFinished;
    java.io.InputStream inStream;
    int mMatchFinderType = EMatchFinderTypeBT4;
    boolean mWriteEndMark = false;
    boolean mNeedReleaseMfstream = false;
    int[] reps = new int[Base.kNumRepDistances];
    int[] repLens = new int[Base.kNumRepDistances];
    int backRes;
    long[] processedInSize = new long[1];
    long[] processedOutSize = new long[1];
    boolean[] finished = new boolean[1];
    byte[] properties = new byte[kPropSize];
    int[] tempPrices = new int[Base.kNumFullDistances];
    int mMatchPriceCount;

    public LzmaEncoder() {
        for (int i = 0; i < kNumOpts; i++)
            optimum[i] = new Optimal();
        for (int i = 0; i < Base.kNumLenToPosStates; i++)
            mPosSlotEncoder[i] = new BitTreeEncoder(Base.kNumPosSlotBits);
    }

    static int getPosSlot(int pos) {
        if (pos < (1 << 11))
            return g_FastPos[pos];
        if (pos < (1 << 21))
            return (g_FastPos[pos >> 10] + 20);
        return (g_FastPos[pos >> 20] + 40);
    }

    static int getPosSlot2(int pos) {
        if (pos < (1 << 17))
            return (g_FastPos[pos >> 6] + 12);
        if (pos < (1 << 27))
            return (g_FastPos[pos >> 16] + 32);
        return (g_FastPos[pos >> 26] + 52);
    }

    void baseInit() {
        mState = Base.stateInit();
        previousByte = 0;
        for (int i = 0; i < Base.kNumRepDistances; i++)
            repDistances[i] = 0;
    }

    void create() {
        if (_matchFinder == null) {
            compression.lz.BinTree bt = new compression.lz.BinTree();
            int numHashBytes = 4;
            if (mMatchFinderType == EMatchFinderTypeBT2)
                numHashBytes = 2;
            bt.setType(numHashBytes);
            _matchFinder = bt;
        }
        mLiteralEncoder.create(numLiteralPosStateBits, numLiteralContextBits);

        if (mDictionarySize == mDictionarySizePrev && numFastBytesPrev == numFastBytes)
            return;
        _matchFinder.create(mDictionarySize, kNumOpts, numFastBytes, Base.kMatchMaxLen + 1);
        mDictionarySizePrev = mDictionarySize;
        numFastBytesPrev = numFastBytes;
    }

    void setWriteEndMarkerMode(boolean writeEndMarker) {
        mWriteEndMark = writeEndMarker;
    }

    void init() {
        baseInit();
        _rangeEncoder.Init();

        compression.range_coder.Encoder.InitBitModels(isMatch);
        compression.range_coder.Encoder.InitBitModels(isRep0Long);
        compression.range_coder.Encoder.InitBitModels(isRep);
        compression.range_coder.Encoder.InitBitModels(isRepG0);
        compression.range_coder.Encoder.InitBitModels(isRepG1);
        compression.range_coder.Encoder.InitBitModels(isRepG2);
        compression.range_coder.Encoder.InitBitModels(mPosEncoders);


        mLiteralEncoder.init();
        for (int i = 0; i < Base.kNumLenToPosStates; i++)
            mPosSlotEncoder[i].init();


        mLenEncoder.init(1 << mPosStateBits);
        mRepMatchLenEncoder.init(1 << mPosStateBits);

        mPosAlignEncoder.init();

        mLongestMatchWasFound = false;
        mOptimumEndIndex = 0;
        mOptimumCurrentIndex = 0;
        mAdditionalOffset = 0;
    }

    int readMatchDistances() throws java.io.IOException {
        int lenRes = 0;
        numDistancePairs = _matchFinder.getMatches(mMatchDistances);
        if (numDistancePairs > 0) {
            lenRes = mMatchDistances[numDistancePairs - 2];
            if (lenRes == numFastBytes)
                lenRes += _matchFinder.getMatchLen((int) lenRes - 1, mMatchDistances[numDistancePairs - 1],
                        Base.kMatchMaxLen - lenRes);
        }
        mAdditionalOffset++;
        return lenRes;
    }

    void movePos(int num) throws java.io.IOException {
        if (num > 0) {
            _matchFinder.skip(num);
            mAdditionalOffset += num;
        }
    }

    int getRepLen1Price(int state, int posState) {
        return compression.range_coder.Encoder.GetPrice0(isRepG0[state]) +
                compression.range_coder.Encoder.GetPrice0(isRep0Long[(state << Base.kNumPosStatesBitsMax) + posState]);
    }

    int getPureRepPrice(int repIndex, int state, int posState) {
        int price;
        if (repIndex == 0) {
            price = compression.range_coder.Encoder.GetPrice0(isRepG0[state]);
            price += compression.range_coder.Encoder.GetPrice1(isRep0Long[(state << Base.kNumPosStatesBitsMax) + posState]);
        } else {
            price = compression.range_coder.Encoder.GetPrice1(isRepG0[state]);
            if (repIndex == 1)
                price += compression.range_coder.Encoder.GetPrice0(isRepG1[state]);
            else {
                price += compression.range_coder.Encoder.GetPrice1(isRepG1[state]);
                price += compression.range_coder.Encoder.GetPrice(isRepG2[state], repIndex - 2);
            }
        }
        return price;
    }

    int getRepPrice(int repIndex, int len, int state, int posState) {
        int price = mRepMatchLenEncoder.getPrice(len - Base.kMatchMinLen, posState);
        return price + getPureRepPrice(repIndex, state, posState);
    }

    int getPosLenPrice(int pos, int len, int posState) {
        int price;
        int lenToPosState = Base.getLenToPosState(len);
        if (pos < Base.kNumFullDistances)
            price = mDistancesPrices[(lenToPosState * Base.kNumFullDistances) + pos];
        else
            price = mPosSlotPrices[(lenToPosState << Base.kNumPosSlotBits) + getPosSlot2(pos)] +
                    mAlignPrices[pos & Base.kAlignMask];
        return price + mLenEncoder.getPrice(len - Base.kMatchMinLen, posState);
    }

    int backward(int cur) {
        mOptimumEndIndex = cur;
        int posMem = optimum[cur].PosPrev;
        int backMem = optimum[cur].BackPrev;
        do {
            if (optimum[cur].Prev1IsChar) {
                optimum[posMem].makeAsChar();
                optimum[posMem].PosPrev = posMem - 1;
                if (optimum[cur].Prev2) {
                    optimum[posMem - 1].Prev1IsChar = false;
                    optimum[posMem - 1].PosPrev = optimum[cur].PosPrev2;
                    optimum[posMem - 1].BackPrev = optimum[cur].BackPrev2;
                }
            }
            int posPrev = posMem;
            int backCur = backMem;

            backMem = optimum[posPrev].BackPrev;
            posMem = optimum[posPrev].PosPrev;

            optimum[posPrev].BackPrev = backCur;
            optimum[posPrev].PosPrev = cur;
            cur = posPrev;
        }
        while (cur > 0);
        backRes = optimum[0].BackPrev;
        mOptimumCurrentIndex = optimum[0].PosPrev;
        return mOptimumCurrentIndex;
    }

    int getOptimum(int position) throws IOException {
        if (mOptimumEndIndex != mOptimumCurrentIndex) {
            int lenRes = optimum[mOptimumCurrentIndex].PosPrev - mOptimumCurrentIndex;
            backRes = optimum[mOptimumCurrentIndex].BackPrev;
            mOptimumCurrentIndex = optimum[mOptimumCurrentIndex].PosPrev;
            return lenRes;
        }
        mOptimumCurrentIndex = mOptimumEndIndex = 0;

        int lenMain, numDistancePairs;
        if (!mLongestMatchWasFound) {
            lenMain = readMatchDistances();
        } else {
            lenMain = mLongestMatchLength;
            mLongestMatchWasFound = false;
        }
        numDistancePairs = this.numDistancePairs;

        int numAvailableBytes = _matchFinder.getNumAvailableBytes() + 1;
        if (numAvailableBytes < 2) {
            backRes = -1;
            return 1;
        }
        if (numAvailableBytes > Base.kMatchMaxLen)
            numAvailableBytes = Base.kMatchMaxLen;

        int repMaxIndex = 0;
        int i;
        for (i = 0; i < Base.kNumRepDistances; i++) {
            reps[i] = repDistances[i];
            repLens[i] = _matchFinder.getMatchLen(0 - 1, reps[i], Base.kMatchMaxLen);
            if (repLens[i] > repLens[repMaxIndex])
                repMaxIndex = i;
        }
        if (repLens[repMaxIndex] >= numFastBytes) {
            backRes = repMaxIndex;
            int lenRes = repLens[repMaxIndex];
            movePos(lenRes - 1);
            return lenRes;
        }

        if (lenMain >= numFastBytes) {
            backRes = mMatchDistances[numDistancePairs - 1] + Base.kNumRepDistances;
            movePos(lenMain - 1);
            return lenMain;
        }

        byte currentByte = _matchFinder.getIndexByte(0 - 1);
        byte matchByte = _matchFinder.getIndexByte(0 - repDistances[0] - 1 - 1);

        if (lenMain < 2 && currentByte != matchByte && repLens[repMaxIndex] < 2) {
            backRes = -1;
            return 1;
        }

        optimum[0].State = mState;

        int posState = (position & mPosStateMask);

        optimum[1].Price = compression.range_coder.Encoder.GetPrice0(isMatch[(mState << Base.kNumPosStatesBitsMax) + posState]) +
                mLiteralEncoder.getSubCoder(position, previousByte).getPrice(!Base.stateIsCharState(mState), matchByte, currentByte);
        optimum[1].makeAsChar();

        int matchPrice = compression.range_coder.Encoder.GetPrice1(isMatch[(mState << Base.kNumPosStatesBitsMax) + posState]);
        int repMatchPrice = matchPrice + compression.range_coder.Encoder.GetPrice1(isRep[mState]);

        if (matchByte == currentByte) {
            int shortRepPrice = repMatchPrice + getRepLen1Price(mState, posState);
            if (shortRepPrice < optimum[1].Price) {
                optimum[1].Price = shortRepPrice;
                optimum[1].makeAsShortRep();
            }
        }

        int lenEnd = ((lenMain >= repLens[repMaxIndex]) ? lenMain : repLens[repMaxIndex]);

        if (lenEnd < 2) {
            backRes = optimum[1].BackPrev;
            return 1;
        }

        optimum[1].PosPrev = 0;

        optimum[0].Backs0 = reps[0];
        optimum[0].Backs1 = reps[1];
        optimum[0].Backs2 = reps[2];
        optimum[0].Backs3 = reps[3];

        int len = lenEnd;
        do
            optimum[len--].Price = kInfinityPrice;
        while (len >= 2);

        for (i = 0; i < Base.kNumRepDistances; i++) {
            int repLen = repLens[i];
            if (repLen < 2)
                continue;
            int price = repMatchPrice + getPureRepPrice(i, mState, posState);
            do {
                int curAndLenPrice = price + mRepMatchLenEncoder.getPrice(repLen - 2, posState);
                Optimal optimum = this.optimum[repLen];
                if (curAndLenPrice < optimum.Price) {
                    optimum.Price = curAndLenPrice;
                    optimum.PosPrev = 0;
                    optimum.BackPrev = i;
                    optimum.Prev1IsChar = false;
                }
            }
            while (--repLen >= 2);
        }

        int normalMatchPrice = matchPrice + compression.range_coder.Encoder.GetPrice0(isRep[mState]);

        len = ((repLens[0] >= 2) ? repLens[0] + 1 : 2);
        if (len <= lenMain) {
            int offs = 0;
            while (len > mMatchDistances[offs])
                offs += 2;
            for (; ; len++) {
                int distance = mMatchDistances[offs + 1];
                int curAndLenPrice = normalMatchPrice + getPosLenPrice(distance, len, posState);
                Optimal optimum = this.optimum[len];
                if (curAndLenPrice < optimum.Price) {
                    optimum.Price = curAndLenPrice;
                    optimum.PosPrev = 0;
                    optimum.BackPrev = distance + Base.kNumRepDistances;
                    optimum.Prev1IsChar = false;
                }
                if (len == mMatchDistances[offs]) {
                    offs += 2;
                    if (offs == numDistancePairs)
                        break;
                }
            }
        }

        int cur = 0;

        while (true) {
            cur++;
            if (cur == lenEnd)
                return backward(cur);
            int newLen = readMatchDistances();
            numDistancePairs = this.numDistancePairs;
            if (newLen >= numFastBytes) {

                mLongestMatchLength = newLen;
                mLongestMatchWasFound = true;
                return backward(cur);
            }
            position++;
            int posPrev = optimum[cur].PosPrev;
            int state;
            if (optimum[cur].Prev1IsChar) {
                posPrev--;
                if (optimum[cur].Prev2) {
                    state = optimum[optimum[cur].PosPrev2].State;
                    if (optimum[cur].BackPrev2 < Base.kNumRepDistances)
                        state = Base.stateUpdateRep(state);
                    else
                        state = Base.stateUpdateMatch(state);
                } else
                    state = optimum[posPrev].State;
                state = Base.stateUpdateChar(state);
            } else
                state = optimum[posPrev].State;
            if (posPrev == cur - 1) {
                if (optimum[cur].isShortRep())
                    state = Base.stateUpdateShortRep(state);
                else
                    state = Base.stateUpdateChar(state);
            } else {
                int pos;
                if (optimum[cur].Prev1IsChar && optimum[cur].Prev2) {
                    posPrev = optimum[cur].PosPrev2;
                    pos = optimum[cur].BackPrev2;
                    state = Base.stateUpdateRep(state);
                } else {
                    pos = optimum[cur].BackPrev;
                    if (pos < Base.kNumRepDistances)
                        state = Base.stateUpdateRep(state);
                    else
                        state = Base.stateUpdateMatch(state);
                }
                Optimal opt = optimum[posPrev];
                if (pos < Base.kNumRepDistances) {
                    if (pos == 0) {
                        reps[0] = opt.Backs0;
                        reps[1] = opt.Backs1;
                        reps[2] = opt.Backs2;
                        reps[3] = opt.Backs3;
                    } else if (pos == 1) {
                        reps[0] = opt.Backs1;
                        reps[1] = opt.Backs0;
                        reps[2] = opt.Backs2;
                        reps[3] = opt.Backs3;
                    } else if (pos == 2) {
                        reps[0] = opt.Backs2;
                        reps[1] = opt.Backs0;
                        reps[2] = opt.Backs1;
                        reps[3] = opt.Backs3;
                    } else {
                        reps[0] = opt.Backs3;
                        reps[1] = opt.Backs0;
                        reps[2] = opt.Backs1;
                        reps[3] = opt.Backs2;
                    }
                } else {
                    reps[0] = (pos - Base.kNumRepDistances);
                    reps[1] = opt.Backs0;
                    reps[2] = opt.Backs1;
                    reps[3] = opt.Backs2;
                }
            }
            optimum[cur].State = state;
            optimum[cur].Backs0 = reps[0];
            optimum[cur].Backs1 = reps[1];
            optimum[cur].Backs2 = reps[2];
            optimum[cur].Backs3 = reps[3];
            int curPrice = optimum[cur].Price;

            currentByte = _matchFinder.getIndexByte(0 - 1);
            matchByte = _matchFinder.getIndexByte(0 - reps[0] - 1 - 1);

            posState = (position & mPosStateMask);

            int curAnd1Price = curPrice +
                    compression.range_coder.Encoder.GetPrice0(isMatch[(state << Base.kNumPosStatesBitsMax) + posState]) +
                    mLiteralEncoder.getSubCoder(position, _matchFinder.getIndexByte(0 - 2)).
                            getPrice(!Base.stateIsCharState(state), matchByte, currentByte);

            Optimal nextOptimum = optimum[cur + 1];

            boolean nextIsChar = false;
            if (curAnd1Price < nextOptimum.Price) {
                nextOptimum.Price = curAnd1Price;
                nextOptimum.PosPrev = cur;
                nextOptimum.makeAsChar();
                nextIsChar = true;
            }

            matchPrice = curPrice + compression.range_coder.Encoder.GetPrice1(isMatch[(state << Base.kNumPosStatesBitsMax) + posState]);
            repMatchPrice = matchPrice + compression.range_coder.Encoder.GetPrice1(isRep[state]);

            if (matchByte == currentByte &&
                    !(nextOptimum.PosPrev < cur && nextOptimum.BackPrev == 0)) {
                int shortRepPrice = repMatchPrice + getRepLen1Price(state, posState);
                if (shortRepPrice <= nextOptimum.Price) {
                    nextOptimum.Price = shortRepPrice;
                    nextOptimum.PosPrev = cur;
                    nextOptimum.makeAsShortRep();
                    nextIsChar = true;
                }
            }

            int numAvailableBytesFull = _matchFinder.getNumAvailableBytes() + 1;
            numAvailableBytesFull = Math.min(kNumOpts - 1 - cur, numAvailableBytesFull);
            numAvailableBytes = numAvailableBytesFull;

            if (numAvailableBytes < 2)
                continue;
            if (numAvailableBytes > numFastBytes)
                numAvailableBytes = numFastBytes;
            if (!nextIsChar && matchByte != currentByte) {
                // try Literal + rep0
                int t = Math.min(numAvailableBytesFull - 1, numFastBytes);
                int lenTest2 = _matchFinder.getMatchLen(0, reps[0], t);
                if (lenTest2 >= 2) {
                    int state2 = Base.stateUpdateChar(state);

                    int posStateNext = (position + 1) & mPosStateMask;
                    int nextRepMatchPrice = curAnd1Price +
                            compression.range_coder.Encoder.GetPrice1(isMatch[(state2 << Base.kNumPosStatesBitsMax) + posStateNext]) +
                            compression.range_coder.Encoder.GetPrice1(isRep[state2]);
                    {
                        int offset = cur + 1 + lenTest2;
                        while (lenEnd < offset)
                            optimum[++lenEnd].Price = kInfinityPrice;
                        int curAndLenPrice = nextRepMatchPrice + getRepPrice(
                                0, lenTest2, state2, posStateNext);
                        Optimal optimum = this.optimum[offset];
                        if (curAndLenPrice < optimum.Price) {
                            optimum.Price = curAndLenPrice;
                            optimum.PosPrev = cur + 1;
                            optimum.BackPrev = 0;
                            optimum.Prev1IsChar = true;
                            optimum.Prev2 = false;
                        }
                    }
                }
            }

            int startLen = 2; // speed optimization

            for (int repIndex = 0; repIndex < Base.kNumRepDistances; repIndex++) {
                int lenTest = _matchFinder.getMatchLen(0 - 1, reps[repIndex], numAvailableBytes);
                if (lenTest < 2)
                    continue;
                int lenTestTemp = lenTest;
                do {
                    while (lenEnd < cur + lenTest)
                        optimum[++lenEnd].Price = kInfinityPrice;
                    int curAndLenPrice = repMatchPrice + getRepPrice(repIndex, lenTest, state, posState);
                    Optimal optimum = this.optimum[cur + lenTest];
                    if (curAndLenPrice < optimum.Price) {
                        optimum.Price = curAndLenPrice;
                        optimum.PosPrev = cur;
                        optimum.BackPrev = repIndex;
                        optimum.Prev1IsChar = false;
                    }
                }
                while (--lenTest >= 2);
                lenTest = lenTestTemp;

                if (repIndex == 0)
                    startLen = lenTest + 1;

                // if (_maxMode)
                if (lenTest < numAvailableBytesFull) {
                    int t = Math.min(numAvailableBytesFull - 1 - lenTest, numFastBytes);
                    int lenTest2 = _matchFinder.getMatchLen(lenTest, reps[repIndex], t);
                    if (lenTest2 >= 2) {
                        int state2 = Base.stateUpdateRep(state);

                        int posStateNext = (position + lenTest) & mPosStateMask;
                        int curAndLenCharPrice =
                                repMatchPrice + getRepPrice(repIndex, lenTest, state, posState) +
                                        compression.range_coder.Encoder.GetPrice0(isMatch[(state2 << Base.kNumPosStatesBitsMax) + posStateNext]) +
                                        mLiteralEncoder.getSubCoder(position + lenTest,
                                                _matchFinder.getIndexByte(lenTest - 1 - 1)).getPrice(true,
                                                _matchFinder.getIndexByte(lenTest - 1 - (reps[repIndex] + 1)),
                                                _matchFinder.getIndexByte(lenTest - 1));
                        state2 = Base.stateUpdateChar(state2);
                        posStateNext = (position + lenTest + 1) & mPosStateMask;
                        int nextMatchPrice = curAndLenCharPrice + compression.range_coder.Encoder.GetPrice1(isMatch[(state2 << Base.kNumPosStatesBitsMax) + posStateNext]);
                        int nextRepMatchPrice = nextMatchPrice + compression.range_coder.Encoder.GetPrice1(isRep[state2]);

                        // for(; lenTest2 >= 2; lenTest2--)
                        {
                            int offset = lenTest + 1 + lenTest2;
                            while (lenEnd < cur + offset)
                                optimum[++lenEnd].Price = kInfinityPrice;
                            int curAndLenPrice = nextRepMatchPrice + getRepPrice(0, lenTest2, state2, posStateNext);
                            Optimal optimum = this.optimum[cur + offset];
                            if (curAndLenPrice < optimum.Price) {
                                optimum.Price = curAndLenPrice;
                                optimum.PosPrev = cur + lenTest + 1;
                                optimum.BackPrev = 0;
                                optimum.Prev1IsChar = true;
                                optimum.Prev2 = true;
                                optimum.PosPrev2 = cur;
                                optimum.BackPrev2 = repIndex;
                            }
                        }
                    }
                }
            }

            if (newLen > numAvailableBytes) {
                newLen = numAvailableBytes;
                for (numDistancePairs = 0; newLen > mMatchDistances[numDistancePairs]; numDistancePairs += 2) ;
                mMatchDistances[numDistancePairs] = newLen;
                numDistancePairs += 2;
            }
            if (newLen >= startLen) {
                normalMatchPrice = matchPrice + compression.range_coder.Encoder.GetPrice0(isRep[state]);
                while (lenEnd < cur + newLen)
                    optimum[++lenEnd].Price = kInfinityPrice;

                int offs = 0;
                while (startLen > mMatchDistances[offs])
                    offs += 2;

                for (int lenTest = startLen; ; lenTest++) {
                    int curBack = mMatchDistances[offs + 1];
                    int curAndLenPrice = normalMatchPrice + getPosLenPrice(curBack, lenTest, posState);
                    Optimal optimum = this.optimum[cur + lenTest];
                    if (curAndLenPrice < optimum.Price) {
                        optimum.Price = curAndLenPrice;
                        optimum.PosPrev = cur;
                        optimum.BackPrev = curBack + Base.kNumRepDistances;
                        optimum.Prev1IsChar = false;
                    }

                    if (lenTest == mMatchDistances[offs]) {
                        if (lenTest < numAvailableBytesFull) {
                            int t = Math.min(numAvailableBytesFull - 1 - lenTest, numFastBytes);
                            int lenTest2 = _matchFinder.getMatchLen(lenTest, curBack, t);
                            if (lenTest2 >= 2) {
                                int state2 = Base.stateUpdateMatch(state);

                                int posStateNext = (position + lenTest) & mPosStateMask;
                                int curAndLenCharPrice = curAndLenPrice +
                                        compression.range_coder.Encoder.GetPrice0(isMatch[(state2 << Base.kNumPosStatesBitsMax) + posStateNext]) +
                                        mLiteralEncoder.getSubCoder(position + lenTest,
                                                _matchFinder.getIndexByte(lenTest - 1 - 1)).
                                                getPrice(true,
                                                        _matchFinder.getIndexByte(lenTest - (curBack + 1) - 1),
                                                        _matchFinder.getIndexByte(lenTest - 1));
                                state2 = Base.stateUpdateChar(state2);
                                posStateNext = (position + lenTest + 1) & mPosStateMask;
                                int nextMatchPrice = curAndLenCharPrice + compression.range_coder.Encoder.GetPrice1(isMatch[(state2 << Base.kNumPosStatesBitsMax) + posStateNext]);
                                int nextRepMatchPrice = nextMatchPrice + compression.range_coder.Encoder.GetPrice1(isRep[state2]);

                                int offset = lenTest + 1 + lenTest2;
                                while (lenEnd < cur + offset)
                                    this.optimum[++lenEnd].Price = kInfinityPrice;
                                curAndLenPrice = nextRepMatchPrice + getRepPrice(0, lenTest2, state2, posStateNext);
                                optimum = this.optimum[cur + offset];
                                if (curAndLenPrice < optimum.Price) {
                                    optimum.Price = curAndLenPrice;
                                    optimum.PosPrev = cur + lenTest + 1;
                                    optimum.BackPrev = 0;
                                    optimum.Prev1IsChar = true;
                                    optimum.Prev2 = true;
                                    optimum.PosPrev2 = cur;
                                    optimum.BackPrev2 = curBack + Base.kNumRepDistances;
                                }
                            }
                        }
                        offs += 2;
                        if (offs == numDistancePairs)
                            break;
                    }
                }
            }
        }
    }

    boolean changePair(int smallDist, int bigDist) {
        int kDif = 7;
        return (smallDist < (1 << (32 - kDif)) && bigDist >= (smallDist << kDif));
    }

    void writeEndMarker(int posState) throws IOException {
        if (!mWriteEndMark)
            return;

        _rangeEncoder.Encode(isMatch, (mState << Base.kNumPosStatesBitsMax) + posState, 1);
        _rangeEncoder.Encode(isRep, mState, 0);
        mState = Base.stateUpdateMatch(mState);
        int len = Base.kMatchMinLen;
        mLenEncoder.encode(_rangeEncoder, len - Base.kMatchMinLen, posState);
        int posSlot = (1 << Base.kNumPosSlotBits) - 1;
        int lenToPosState = Base.getLenToPosState(len);
        mPosSlotEncoder[lenToPosState].encode(_rangeEncoder, posSlot);
        int footerBits = 30;
        int posReduced = (1 << footerBits) - 1;
        _rangeEncoder.EncodeDirectBits(posReduced >> Base.kNumAlignBits, footerBits - Base.kNumAlignBits);
        mPosAlignEncoder.reverseEncode(_rangeEncoder, posReduced & Base.kAlignMask);
    }

    void flush(int nowPos) throws IOException {
        releaseMFStream();
        writeEndMarker(nowPos & mPosStateMask);
        _rangeEncoder.FlushData();
        _rangeEncoder.FlushStream();
    }

    public void codeOneBlock(long[] inSize, long[] outSize, boolean[] finished) throws IOException {
        inSize[0] = 0;
        outSize[0] = 0;
        finished[0] = true;

        if (inStream != null) {
            _matchFinder.setStream(inStream);
            _matchFinder.init();
            mNeedReleaseMfstream = true;
            inStream = null;
        }

        if (mFinished)
            return;
        mFinished = true;


        long progressPosValuePrev = nowPos64;
        if (nowPos64 == 0) {
            if (_matchFinder.getNumAvailableBytes() == 0) {
                flush((int) nowPos64);
                return;
            }

            readMatchDistances();
            int posState = (int) (nowPos64) & mPosStateMask;
            _rangeEncoder.Encode(isMatch, (mState << Base.kNumPosStatesBitsMax) + posState, 0);
            mState = Base.stateUpdateChar(mState);
            byte curByte = _matchFinder.getIndexByte(0 - mAdditionalOffset);
            mLiteralEncoder.getSubCoder((int) (nowPos64), previousByte).encode(_rangeEncoder, curByte);
            previousByte = curByte;
            mAdditionalOffset--;
            nowPos64++;
        }
        if (_matchFinder.getNumAvailableBytes() == 0) {
            flush((int) nowPos64);
            return;
        }
        while (true) {

            int len = getOptimum((int) nowPos64);
            int pos = backRes;
            int posState = ((int) nowPos64) & mPosStateMask;
            int complexState = (mState << Base.kNumPosStatesBitsMax) + posState;
            if (len == 1 && pos == -1) {
                _rangeEncoder.Encode(isMatch, complexState, 0);
                byte curByte = _matchFinder.getIndexByte((int) (0 - mAdditionalOffset));
                LiteralEncoder.Encoder2 subCoder = mLiteralEncoder.getSubCoder((int) nowPos64, previousByte);
                if (!Base.stateIsCharState(mState)) {
                    byte matchByte = _matchFinder.getIndexByte((int) (0 - repDistances[0] - 1 - mAdditionalOffset));
                    subCoder.encodeMatched(_rangeEncoder, matchByte, curByte);
                } else
                    subCoder.encode(_rangeEncoder, curByte);
                previousByte = curByte;
                mState = Base.stateUpdateChar(mState);
            } else {
                _rangeEncoder.Encode(isMatch, complexState, 1);
                if (pos < Base.kNumRepDistances) {
                    _rangeEncoder.Encode(isRep, mState, 1);
                    if (pos == 0) {
                        _rangeEncoder.Encode(isRepG0, mState, 0);
                        if (len == 1)
                            _rangeEncoder.Encode(isRep0Long, complexState, 0);
                        else
                            _rangeEncoder.Encode(isRep0Long, complexState, 1);
                    } else {
                        _rangeEncoder.Encode(isRepG0, mState, 1);
                        if (pos == 1)
                            _rangeEncoder.Encode(isRepG1, mState, 0);
                        else {
                            _rangeEncoder.Encode(isRepG1, mState, 1);
                            _rangeEncoder.Encode(isRepG2, mState, pos - 2);
                        }
                    }
                    if (len == 1)
                        mState = Base.stateUpdateShortRep(mState);
                    else {
                        mRepMatchLenEncoder.encode(_rangeEncoder, len - Base.kMatchMinLen, posState);
                        mState = Base.stateUpdateRep(mState);
                    }
                    int distance = repDistances[pos];
                    if (pos != 0) {
                        for (int i = pos; i >= 1; i--)
                            repDistances[i] = repDistances[i - 1];
                        repDistances[0] = distance;
                    }
                } else {
                    _rangeEncoder.Encode(isRep, mState, 0);
                    mState = Base.stateUpdateMatch(mState);
                    mLenEncoder.encode(_rangeEncoder, len - Base.kMatchMinLen, posState);
                    pos -= Base.kNumRepDistances;
                    int posSlot = getPosSlot(pos);
                    int lenToPosState = Base.getLenToPosState(len);
                    mPosSlotEncoder[lenToPosState].encode(_rangeEncoder, posSlot);

                    if (posSlot >= Base.kStartPosModelIndex) {
                        int footerBits = (int) ((posSlot >> 1) - 1);
                        int baseVal = ((2 | (posSlot & 1)) << footerBits);
                        int posReduced = pos - baseVal;

                        if (posSlot < Base.kEndPosModelIndex)
                            BitTreeEncoder.reverseEncode(mPosEncoders,
                                    baseVal - posSlot - 1, _rangeEncoder, footerBits, posReduced);
                        else {
                            _rangeEncoder.EncodeDirectBits(posReduced >> Base.kNumAlignBits, footerBits - Base.kNumAlignBits);
                            mPosAlignEncoder.reverseEncode(_rangeEncoder, posReduced & Base.kAlignMask);
                            mAlignPriceCount++;
                        }
                    }
                    int distance = pos;
                    for (int i = Base.kNumRepDistances - 1; i >= 1; i--)
                        repDistances[i] = repDistances[i - 1];
                    repDistances[0] = distance;
                    mMatchPriceCount++;
                }
                previousByte = _matchFinder.getIndexByte(len - 1 - mAdditionalOffset);
            }
            mAdditionalOffset -= len;
            nowPos64 += len;
            if (mAdditionalOffset == 0) {
                // if (!_fastMode)
                if (mMatchPriceCount >= (1 << 7))
                    fillDistancesPrices();
                if (mAlignPriceCount >= Base.kAlignTableSize)
                    fillAlignPrices();
                inSize[0] = nowPos64;
                outSize[0] = _rangeEncoder.GetProcessedSizeAdd();
                if (_matchFinder.getNumAvailableBytes() == 0) {
                    flush((int) nowPos64);
                    return;
                }

                if (nowPos64 - progressPosValuePrev >= (1 << 12)) {
                    mFinished = false;
                    finished[0] = false;
                    return;
                }
            }
        }
    }

    void releaseMFStream() {
        if (_matchFinder != null && mNeedReleaseMfstream) {
            _matchFinder.releaseStream();
            mNeedReleaseMfstream = false;
        }
    }

    void setOutStream(java.io.OutputStream outStream) {
        _rangeEncoder.SetStream(outStream);
    }

    void releaseOutStream() {
        _rangeEncoder.ReleaseStream();
    }

    void releaseStreams() {
        releaseMFStream();
        releaseOutStream();
    }

    void setStreams(java.io.InputStream inStream, java.io.OutputStream outStream,
                    long inSize, long outSize) {
        this.inStream = inStream;
        mFinished = false;
        create();
        setOutStream(outStream);
        init();

        // if (!_fastMode)
        {
            fillDistancesPrices();
            fillAlignPrices();
        }

        mLenEncoder.setTableSize(numFastBytes + 1 - Base.kMatchMinLen);
        mLenEncoder.updateTables(1 << mPosStateBits);
        mRepMatchLenEncoder.setTableSize(numFastBytes + 1 - Base.kMatchMinLen);
        mRepMatchLenEncoder.updateTables(1 << mPosStateBits);

        nowPos64 = 0;
    }

    public void encode(java.io.InputStream inStream, java.io.OutputStream outStream,
                       long inSize, long outSize, Progress progress) throws IOException {
        mNeedReleaseMfstream = false;
        try {
            setStreams(inStream, outStream, inSize, outSize);
            while (true) {
                codeOneBlock(processedInSize, processedOutSize, finished);
                if (finished[0])
                    return;
                if (progress != null) {
                    progress.setProgress(processedInSize[0], processedOutSize[0]);
                }
            }
        } finally {
            releaseStreams();
        }
    }

    public void writeCoderProperties(java.io.OutputStream outStream) throws IOException {
        properties[0] = (byte) ((mPosStateBits * 5 + numLiteralPosStateBits) * 9 + numLiteralContextBits);
        for (int i = 0; i < 4; i++)
            properties[1 + i] = (byte) (mDictionarySize >> (8 * i));
        outStream.write(properties, 0, kPropSize);
    }

    void fillDistancesPrices() {
        for (int i = Base.kStartPosModelIndex; i < Base.kNumFullDistances; i++) {
            int posSlot = getPosSlot(i);
            int footerBits = (int) ((posSlot >> 1) - 1);
            int baseVal = ((2 | (posSlot & 1)) << footerBits);
            tempPrices[i] = BitTreeEncoder.reverseGetPrice(mPosEncoders,
                    baseVal - posSlot - 1, footerBits, i - baseVal);
        }

        for (int lenToPosState = 0; lenToPosState < Base.kNumLenToPosStates; lenToPosState++) {
            int posSlot;
            BitTreeEncoder encoder = mPosSlotEncoder[lenToPosState];

            int st = (lenToPosState << Base.kNumPosSlotBits);
            for (posSlot = 0; posSlot < mDistTableSize; posSlot++)
                mPosSlotPrices[st + posSlot] = encoder.getPrice(posSlot);
            for (posSlot = Base.kEndPosModelIndex; posSlot < mDistTableSize; posSlot++)
                mPosSlotPrices[st + posSlot] += ((((posSlot >> 1) - 1) - Base.kNumAlignBits) << compression.range_coder.Encoder.kNumBitPriceShiftBits);

            int st2 = lenToPosState * Base.kNumFullDistances;
            int i;
            for (i = 0; i < Base.kStartPosModelIndex; i++)
                mDistancesPrices[st2 + i] = mPosSlotPrices[st + i];
            for (; i < Base.kNumFullDistances; i++)
                mDistancesPrices[st2 + i] = mPosSlotPrices[st + getPosSlot(i)] + tempPrices[i];
        }
        mMatchPriceCount = 0;
    }

    void fillAlignPrices() {
        for (int i = 0; i < Base.kAlignTableSize; i++)
            mAlignPrices[i] = mPosAlignEncoder.reverseGetPrice(i);
        mAlignPriceCount = 0;
    }

    public boolean setAlgorithm(int algorithm) {
		/*
		_fastMode = (algorithm == 0);
		_maxMode = (algorithm >= 2);
		*/
        return true;
    }

    public int getDictionarySize() {
        return mDictionarySize;
    }

    public boolean setDictionarySize(int dictionarySize) {
        int kDicLogSizeMaxCompress = 29;
        if (dictionarySize < (1 << Base.kDicLogSizeMin) || dictionarySize > (1 << kDicLogSizeMaxCompress))
            return false;
        mDictionarySize = dictionarySize;
        int dicLogSize;
        for (dicLogSize = 0; dictionarySize > (1 << dicLogSize); dicLogSize++) ;
        mDistTableSize = dicLogSize * 2;
        return true;
    }

    public int getNumFastBytes() {
        return numFastBytes;
    }

    public boolean setNumFastBytes(int numFastBytes) {
        if (numFastBytes < 5 || numFastBytes > Base.kMatchMaxLen)
            return false;
        this.numFastBytes = numFastBytes;
        return true;
    }


    public boolean setMatchFinder(int matchFinderIndex) {
        if (matchFinderIndex < 0 || matchFinderIndex > 2)
            return false;
        int matchFinderIndexPrev = mMatchFinderType;
        mMatchFinderType = matchFinderIndex;
        if (_matchFinder != null && matchFinderIndexPrev != mMatchFinderType) {
            mDictionarySizePrev = -1;
            _matchFinder = null;
        }
        return true;
    }

    public boolean setLcLpPb(int lc, int lp, int pb) {
        if (
                lp < 0 || lp > Base.kNumLitPosStatesBitsEncodingMax ||
                        lc < 0 || lc > Base.kNumLitContextBitsMax ||
                        pb < 0 || pb > Base.kNumPosStatesBitsEncodingMax)
            return false;
        numLiteralPosStateBits = lp;
        numLiteralContextBits = lc;
        mPosStateBits = pb;
        mPosStateMask = ((1) << mPosStateBits) - 1;
        return true;
    }

    public void setEndMarkerMode(boolean endMarkerMode) {
        mWriteEndMark = endMarkerMode;
    }

    class LiteralEncoder {
        Encoder2[] m_Coders;
        int m_NumPrevBits;
        int m_NumPosBits;
        int m_PosMask;

        public void create(int numPosBits, int numPrevBits) {
            if (m_Coders != null && m_NumPrevBits == numPrevBits && m_NumPosBits == numPosBits)
                return;
            m_NumPosBits = numPosBits;
            m_PosMask = (1 << numPosBits) - 1;
            m_NumPrevBits = numPrevBits;
            int numStates = 1 << (m_NumPrevBits + m_NumPosBits);
            m_Coders = new Encoder2[numStates];
            for (int i = 0; i < numStates; i++)
                m_Coders[i] = new Encoder2();
        }

        public void init() {
            int numStates = 1 << (m_NumPrevBits + m_NumPosBits);
            for (int i = 0; i < numStates; i++)
                m_Coders[i].init();
        }

        public Encoder2 getSubCoder(int pos, byte prevByte) {
            return m_Coders[((pos & m_PosMask) << m_NumPrevBits) + ((prevByte & 0xFF) >>> (8 - m_NumPrevBits))];
        }

        class Encoder2 {
            short[] m_Encoders = new short[0x300];

            public void init() {
                compression.range_coder.Encoder.InitBitModels(m_Encoders);
            }


            public void encode(compression.range_coder.Encoder rangeEncoder, byte symbol) throws IOException {
                int context = 1;
                for (int i = 7; i >= 0; i--) {
                    int bit = ((symbol >> i) & 1);
                    rangeEncoder.Encode(m_Encoders, context, bit);
                    context = (context << 1) | bit;
                }
            }

            public void encodeMatched(compression.range_coder.Encoder rangeEncoder, byte matchByte, byte symbol) throws IOException {
                int context = 1;
                boolean same = true;
                for (int i = 7; i >= 0; i--) {
                    int bit = ((symbol >> i) & 1);
                    int state = context;
                    if (same) {
                        int matchBit = ((matchByte >> i) & 1);
                        state += ((1 + matchBit) << 8);
                        same = (matchBit == bit);
                    }
                    rangeEncoder.Encode(m_Encoders, state, bit);
                    context = (context << 1) | bit;
                }
            }

            public int getPrice(boolean matchMode, byte matchByte, byte symbol) {
                int price = 0;
                int context = 1;
                int i = 7;
                if (matchMode) {
                    for (; i >= 0; i--) {
                        int matchBit = (matchByte >> i) & 1;
                        int bit = (symbol >> i) & 1;
                        price += compression.range_coder.Encoder.GetPrice(m_Encoders[((1 + matchBit) << 8) + context], bit);
                        context = (context << 1) | bit;
                        if (matchBit != bit) {
                            i--;
                            break;
                        }
                    }
                }
                for (; i >= 0; i--) {
                    int bit = (symbol >> i) & 1;
                    price += compression.range_coder.Encoder.GetPrice(m_Encoders[context], bit);
                    context = (context << 1) | bit;
                }
                return price;
            }
        }
    }

    class LenEncoder {
        short[] _choice = new short[2];
        BitTreeEncoder[] _lowCoder = new BitTreeEncoder[Base.kNumPosStatesEncodingMax];
        BitTreeEncoder[] _midCoder = new BitTreeEncoder[Base.kNumPosStatesEncodingMax];
        BitTreeEncoder _highCoder = new BitTreeEncoder(Base.kNumHighLenBits);


        public LenEncoder() {
            for (int posState = 0; posState < Base.kNumPosStatesEncodingMax; posState++) {
                _lowCoder[posState] = new BitTreeEncoder(Base.kNumLowLenBits);
                _midCoder[posState] = new BitTreeEncoder(Base.kNumMidLenBits);
            }
        }

        public void init(int numPosStates) {
            compression.range_coder.Encoder.InitBitModels(_choice);

            for (int posState = 0; posState < numPosStates; posState++) {
                _lowCoder[posState].init();
                _midCoder[posState].init();
            }
            _highCoder.init();
        }

        public void encode(compression.range_coder.Encoder rangeEncoder, int symbol, int posState) throws IOException {
            if (symbol < Base.kNumLowLenSymbols) {
                rangeEncoder.Encode(_choice, 0, 0);
                _lowCoder[posState].encode(rangeEncoder, symbol);
            } else {
                symbol -= Base.kNumLowLenSymbols;
                rangeEncoder.Encode(_choice, 0, 1);
                if (symbol < Base.kNumMidLenSymbols) {
                    rangeEncoder.Encode(_choice, 1, 0);
                    _midCoder[posState].encode(rangeEncoder, symbol);
                } else {
                    rangeEncoder.Encode(_choice, 1, 1);
                    _highCoder.encode(rangeEncoder, symbol - Base.kNumMidLenSymbols);
                }
            }
        }

        public void SetPrices(int posState, int numSymbols, int[] prices, int st) {
            int a0 = compression.range_coder.Encoder.GetPrice0(_choice[0]);
            int a1 = compression.range_coder.Encoder.GetPrice1(_choice[0]);
            int b0 = a1 + compression.range_coder.Encoder.GetPrice0(_choice[1]);
            int b1 = a1 + compression.range_coder.Encoder.GetPrice1(_choice[1]);
            int i = 0;
            for (i = 0; i < Base.kNumLowLenSymbols; i++) {
                if (i >= numSymbols)
                    return;
                prices[st + i] = a0 + _lowCoder[posState].getPrice(i);
            }
            for (; i < Base.kNumLowLenSymbols + Base.kNumMidLenSymbols; i++) {
                if (i >= numSymbols)
                    return;
                prices[st + i] = b0 + _midCoder[posState].getPrice(i - Base.kNumLowLenSymbols);
            }
            for (; i < numSymbols; i++)
                prices[st + i] = b1 + _highCoder.getPrice(i - Base.kNumLowLenSymbols - Base.kNumMidLenSymbols);
        }
    }

    class LenPriceTableEncoder extends LenEncoder {
        int[] _prices = new int[Base.kNumLenSymbols << Base.kNumPosStatesBitsEncodingMax];
        int _tableSize;
        int[] _counters = new int[Base.kNumPosStatesEncodingMax];

        public void setTableSize(int tableSize) {
            _tableSize = tableSize;
        }

        public int getPrice(int symbol, int posState) {
            return _prices[posState * Base.kNumLenSymbols + symbol];
        }

        void updateTable(int posState) {
            SetPrices(posState, _tableSize, _prices, posState * Base.kNumLenSymbols);
            _counters[posState] = _tableSize;
        }

        public void updateTables(int numPosStates) {
            for (int posState = 0; posState < numPosStates; posState++)
                updateTable(posState);
        }

        public void encode(compression.range_coder.Encoder rangeEncoder, int symbol, int posState) throws IOException {
            super.encode(rangeEncoder, symbol, posState);
            if (--_counters[posState] == 0)
                updateTable(posState);
        }
    }

    class Optimal {
        public int State;

        public boolean Prev1IsChar;
        public boolean Prev2;

        public int PosPrev2;
        public int BackPrev2;

        public int Price;
        public int PosPrev;
        public int BackPrev;

        public int Backs0;
        public int Backs1;
        public int Backs2;
        public int Backs3;

        public void makeAsChar() {
            BackPrev = -1;
            Prev1IsChar = false;
        }

        public void makeAsShortRep() {
            BackPrev = 0;
            ;
            Prev1IsChar = false;
        }

        public boolean isShortRep() {
            return (BackPrev == 0);
        }
    }
}

