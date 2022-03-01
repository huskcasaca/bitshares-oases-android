package compression.tools;// SevenZip/Compression.Tools.CRC.java

public class CRC {
    static public int[] table = new int[256];

    static {
        for (int i = 0; i < 256; i++) {
            int r = i;
            for (int j = 0; j < 8; j++)
                if ((r & 1) != 0)
                    r = (r >>> 1) ^ 0xEDB88320;
                else
                    r >>>= 1;
            table[i] = r;
        }
    }

    int value = -1;

    public void init() {
        value = -1;
    }

    public void update(byte[] data, int offset, int size) {
        for (int i = 0; i < size; i++)
            value = table[(value ^ data[offset + i]) & 0xFF] ^ (value >>> 8);
    }

    public void update(byte[] data) {
        int size = data.length;
        for (byte datum : data) value = table[(value ^ datum) & 0xFF] ^ (value >>> 8);
    }

    public void updateByte(int b) {
        value = table[(value ^ b) & 0xFF] ^ (value >>> 8);
    }

    public int getDigest() {
        return ~value;
    }
}
