package md5;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.Random;

/**
 * @author mamad
 * @since 23/11/14.
 */
public class TestMD5 {
    @Test
    public void testPutBytesStream() throws Exception {
        int sizeKb = 128;
        byte[] data = generateRandomStream(sizeKb);
        HashFunction md5 = Hashing.md5();
        String h1 = md5.newHasher().putBytes(data).hash().toString();
        Hasher hasher = md5.newHasher();
        for (int i = 0; i < sizeKb; i++) {
            hasher.putBytes(data, i * 1024, 1024);
        }
        String h2 = hasher.hash().toString();
        Assert.assertEquals(h1, h2);
    }

    private byte[] generateRandomStream(int sizeKb) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(sizeKb * 1024);
        byte[] buff = new byte[1024];
        Random rnd = new Random(System.nanoTime());
        for (int i = 0; i < sizeKb; i++) {
            rnd.nextBytes(buff);
            out.write(buff, 0, buff.length);
        }
        return out.toByteArray();
    }
}
