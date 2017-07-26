package estool;

import org.apache.lucene.util.StringHelper;

/**
 * Created by lanpay on 2017/7/24.
 */
public final class Murmur3HashFunction {
    private Murmur3HashFunction() {
        //no instance
    }

    public static int hash(String routing) {
        final byte[] bytesToHash = new byte[routing.length() * 2];
        for (int i = 0; i < routing.length(); ++i) {
            final char c = routing.charAt(i);
            final byte b1 = (byte) c, b2 = (byte) (c >>> 8);
            assert ((b1 & 0xFF) | ((b2 & 0xFF) << 8)) == c; // no information loss
            bytesToHash[i * 2] = b1;
            bytesToHash[i * 2 + 1] = b2;
        }
        return hash(bytesToHash, 0, bytesToHash.length);
    }

    public static int hash(byte[] bytes, int offset, int length) {
        return StringHelper.murmurhash3_x86_32(bytes, offset, length, 0);
    }

    public static void main(String[] args) {
        String key = "3";
        int v = 0;
        //v = Math.floorMod(hash(key), 5);
        System.out.println("hash="+v);
    }
}
