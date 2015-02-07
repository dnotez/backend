package com.dz.duplicate;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import java.io.IOException;
import java.io.InputStream;

import static com.dz.stream.StreamHelper.toMarkSupported;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Compare two streams by generating md5 of the content and comparing them.
 * Generating md5 is a time consuming operation and consume both streams.
 *
 * @author mamad
 * @since 22/11/14.
 */
public class MD5DuplicateStreamDetector implements DuplicateStreamDetector {

    public static final HashFunction MD5_HASH_FUNCTION = Hashing.md5();

    @Override
    public boolean isDuplicate(InputStream newStream, InputStream oldStream) throws IOException {
        InputStream s1 = toMarkSupported(checkNotNull(newStream, "Input stream can not be null."));
        InputStream s2 = toMarkSupported(checkNotNull(newStream, "Comparing stream can not be null."));
        HashCode h1 = hashStream(s1);
        HashCode h2 = hashStream(s2);
        return h1.equals(h2);
    }

    @Override
    public String hash(InputStream stream) throws IOException {
        return hashStream(toMarkSupported(checkNotNull(stream, "Stream can not be null."))).toString();
    }

    public HashCode hashStream(InputStream stream) throws IOException {
        byte[] buffer = new byte[1024];
        try {
            stream.mark(Integer.MAX_VALUE);
            Hasher hasher = MD5_HASH_FUNCTION.newHasher();
            int nRead;
            while ((nRead = stream.read(buffer, 0, 1024)) > 0) {
                hasher.putBytes(buffer, 0, nRead);
            }
            return hasher.hash();
        } finally {
            stream.reset();
        }
    }
}
