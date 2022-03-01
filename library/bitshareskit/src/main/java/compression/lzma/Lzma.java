package compression.lzma;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Lzma {

    /**
     * 压缩函数
     *
     * @param inputStream:  要压缩的数据，如果源数据是byte[]，那么在外层要ByteArrayInputStream ins = new ByteArrayInputStream(yourbytes[], offset, effective_length)
     * @param outputStream: 压缩好的数据，可以通过outputs_bytes.toBytes变成byte数组
     * @param len:          inputs_bytes中的有效数据长度，用于写入压缩好的数据开头，解压时会先读出来这段大小，以便知道要解压的数据大小。注意数据类型
     */
    public static void encode(ByteArrayInputStream inputStream, ByteArrayOutputStream outputStream, int len) throws Exception {
        encode(inputStream, outputStream, (long) len);
    }

    public static void encode(ByteArrayInputStream inputStream, ByteArrayOutputStream outputStream, long len) throws Exception {
        LzmaEncoder encoder = new LzmaEncoder();
        //设置压缩参数。默认即可
        if (!encoder.setAlgorithm(1))
            throw new Exception("Incorrect compression level");
        if (!encoder.setDictionarySize(1 << 24))
            throw new Exception("Incorrect dictionary size");
        if (!encoder.setNumFastBytes(32))
            throw new Exception("Incorrect -fb value");
        if (!encoder.setMatchFinder(1))
            throw new Exception("Incorrect -mf value");
        if (!encoder.setLcLpPb(3, 0, 2))
            throw new Exception("Incorrect -lc or -lp or -pb value");
        encoder.setEndMarkerMode(false);
        //首先会有5bytes的参数信息被写入
        encoder.writeCoderProperties(outputStream);
        //接下来8bytes是要压缩的数据的长度，在解压时将被读取。注意这里len是long类型，如果是int，则最大可表示2GB的数据，因此采用long，但是里面每个byte在存储的时候，使用int即可。
        for (int j = 0; j < 8; j++)
            //无符号右移
            outputStream.write((int) (len >>> (8 * j)) & 0xFF);
        // inSize、outSize以及progress参数可以这样设置不用理会
        encoder.encode(inputStream, outputStream, -1, -1, null);
    }

    /**
     * 解压函数
     *
     * @param inputStream:  要解压的数据。要求同encode。
     * @param outputStream: 解压获得的数据。
     */
    public static void decode(ByteArrayInputStream inputStream, ByteArrayOutputStream outputStream) throws Exception {
        LzmaDecoder decoder = new LzmaDecoder();
        //先读取5bytes设置
        int propertiesSize = 5;
        byte[] properties = new byte[propertiesSize];
        if (inputStream.read(properties, 0, propertiesSize) != propertiesSize)
            throw new Exception("Incorrect size");
        if (!decoder.setDecoderProperties(properties))
            throw new Exception("Incorrect properties");
        long outSize = 0;
        // 读取8bytes的要解压出来的文件长度（单位bytes）
        for (int j = 0; j < 8; j++) {
            int v = inputStream.read();
            if (v < 0)
                throw new Exception("Cannot read input stream size");
            outSize |= ((long) v) << (8 * j);
        }
        if (!decoder.decode(inputStream, outputStream, outSize)) {
            throw new Exception("Cannot decode data stream");
        }
        if (outputStream.size() != outSize) {
            throw new IllegalArgumentException(String.format("Size different: outputStream [%d], outsize [%d]", outputStream.size(), outSize));
        }
    }

}
