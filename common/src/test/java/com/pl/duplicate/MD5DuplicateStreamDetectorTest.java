package com.pl.duplicate;

import com.google.common.hash.HashCode;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * @author mamad
 * @since 23/11/14.
 */
public class MD5DuplicateStreamDetectorTest {
    @Test
    public void testHashCode() throws Exception {
        MD5DuplicateStreamDetector detector = new MD5DuplicateStreamDetector();
        byte[] data = generateRandomStream(2053);
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        HashCode h1 = detector.hashStream(stream);
        assertNotNull(h1);
        HashCode h2 = detector.hashStream(stream);
        assertEquals(h1, h2);

        assertEquals(detector.hash(stream), detector.hash(new ByteArrayInputStream(data)));

        HashCode h3 = detector.hashStream(new ByteArrayInputStream(generateRandomStream(2053)));
        assertNotEquals(h1, h3);
    }

    private byte[] generateRandomStream(int size) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(size);
        byte[] buff = new byte[1];
        Random rnd = new Random(System.nanoTime());
        for (int i = 0; i < size; i++) {
            rnd.nextBytes(buff);
            out.write(buff, 0, buff.length);
        }
        return out.toByteArray();
    }

}
