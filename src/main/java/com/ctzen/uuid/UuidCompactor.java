package com.ctzen.uuid;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.BaseNCodec;

import java.util.UUID;

/**
 * Converts UUID to URL-safe compact string, and vice versa.
 * <p>
 * UUID strings are 36 characters, e.g. {@code be177dbe-5639-4ee1-90b1-09e108ffdddc}.
 * </p>
 * <p>
 * {@code compact64()} and {@code compact32()} compact UUIDs down to strings of 22 and 26 characters respectively. <br />
 * The compacted strings are URL-safe.
 * </p>
 * <p>
 * Uses apache commons codec's {@link Base64} and {@link Base32}.
 * </p>
 * <p>
 * {@code compact32()} produces a longer string but the character set used is unambiguous, only upper-case alphabets, and no
 * '0','1','8' to confuse with 'O','I','B'.<br />
 * Base32 character set: {@code A B C D E F G H I J K L M N O P Q R S T U V W X Y Z 2 3 4 5 6 7}
 * </p>
 * <p>
 * This class is thread safe.
 * </p>
 *
 * @author cchang
 */
public class UuidCompactor {

    /**
     * Length of result string from {@code compact64()} functions.
     */
    public static final int COMPACT64_LEN = 22;

    /**
     * Length of result string from {@code compact32()} functions.
     */
    public static final int COMPACT32_LEN = 26;

    /**
     * Compacts a {@link UUID} using {@link Base64}.
     *
     * @param uuid
     *            UUID to compact
     * @return compact UUID string
     */
    public String compact64(final UUID uuid) {
        return compact64(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
    }

    /**
     * Compacts a {@link UUID} using {@link Base32}.
     *
     * @param uuid
     *            UUID to compact
     * @return compact UUID string
     */
    public String compact32(final UUID uuid) {
        return compact32(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
    }

    /**
     * Compacts a {@link UUID} using {@link Base64}.
     *
     * @param uuidString
     *            UUID string in the standard {@code xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx} format
     * @return compact UUID string
     */
    public String compact64(final String uuidString) {
        return compact64(UUID.fromString(uuidString));
    }

    /**
     * Compacts a {@link UUID} using {@link Base32}.
     *
     * @param uuidString
     *            UUID string in the standard {@code xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx} format
     * @return compact UUID string
     */
    public String compact32(final String uuidString) {
        return compact32(UUID.fromString(uuidString));
    }

    /**
     * Compacts a {@link UUID} using {@link Base64}.
     * <p>
     * For when the source is neither {@code java.util.UUID} nor a UUID string e.g. {#code com.eaio.uuid.UUID}.
     * </p>
     *
     * @param msb
     *            most significant bits
     * @param lsb
     *            least significant bits
     * @return compact UUID string
     */
    public String compact64(final long msb, final long lsb) {
        return compact(msb, lsb, base64, COMPACT64_LEN);
    }

    /**
     * Compacts a {@link UUID} using {@link Base32}.
     * <p>
     * For when the source is neither {@code java.util.UUID} nor a UUID string e.g. {#code com.eaio.uuid.UUID}.
     * </p>
     *
     * @param msb
     *            most significant bits
     * @param lsb
     *            least significant bits
     * @return compact UUID string
     */
    public String compact32(final long msb, final long lsb) {
        return compact(msb, lsb, base32, COMPACT32_LEN);
    }

    /**
     * Decodes a compact uuid string back to {@link UUID}.
     *
     * @param compactUuid
     *            compact UUID string from any of the {@code compact64()} or {@code compact32()} functions
     * @return decoded {@link UUID}
     */
    public UUID expand(final String compactUuid) {
        switch (compactUuid.length()) {
            case COMPACT64_LEN:
                return doExpand64(compactUuid);
            case COMPACT32_LEN:
                return doExpand32(compactUuid);
            default:
                throw new IllegalArgumentException("Not a compact uuid string: " + compactUuid);
        }
    }

    /**
     * Decodes a compact64 uuid string back to {@link UUID}.
     *
     * @param compact64Uuid
     *            compact UUID string from any of the {@code compact64()} functions
     * @return decoded {@link UUID}
     */
    public UUID expand64(final String compact64Uuid) {
        if (compact64Uuid.length() != COMPACT64_LEN) {
            throw new IllegalArgumentException("Expecting a compact uuid string of length " + COMPACT64_LEN);
        }
        return doExpand64(compact64Uuid);
    }

    /**
     * Decodes a compact32 uuid string back to {@link UUID}.
     *
     * @param compact32Uuid
     *            compact UUID string from any of the {@code compact32()} functions
     * @return decoded {@link UUID}
     */
    public UUID expand32(final String compact32Uuid) {
        if (compact32Uuid.length() != COMPACT32_LEN) {
            throw new IllegalArgumentException("Expecting a compact uuid string of length " + COMPACT32_LEN);
        }
        return doExpand32(compact32Uuid);
    }

    /**
     * No length check.
     */
    private UUID doExpand64(final String compact64Uuid) {
        final long[] msblsb = expand(compact64Uuid, base64);
        return new UUID(msblsb[0], msblsb[1]);
    }

    /**
     * No length check.
     */
    private UUID doExpand32(final String compact32Uuid) {
        final long[] msblsb = expand(compact32Uuid, base32);
        return new UUID(msblsb[0], msblsb[1]);
    }

    /**
     * Base64 is thread-safe
     */
    private final Base64 base64 = new Base64(true);

    /**
     * Base32 is thread-safe
     */
    private final Base32 base32 = new Base32();

    /**
     * Number of bytes in a long (8).
     */
    private static final int BYTES_PER_LONG = Long.SIZE / Byte.SIZE;

    /**
     * Number of bytes in 2 longs (16).
     */
    private static final int BYTES_PER_2_LONGS = BYTES_PER_LONG * 2;

    /**
     * The real compactor.
     */
    private String compact(final long msb, final long lsb, final BaseNCodec baseN, final int compactLen) {
        final byte[] bytes = new byte[BYTES_PER_2_LONGS];
        int i = BYTES_PER_2_LONGS;
        long x = lsb;
        while (i > BYTES_PER_LONG) {
            bytes[--i] = (byte)x;
            x >>>= Byte.SIZE;
        }
        x = msb;
        while (i > 0) {
            bytes[--i] = (byte)x;
            x >>>= Byte.SIZE;
        }
        // strips any the trailing ======
        return baseN.encodeAsString(bytes).substring(0, compactLen);
    }

    /**
     * Mask to prevent prefix of 'f's when casting byte to long.
     */
    private static final long LONG_BYTE_MASK = -1 ^ (-1 << Byte.SIZE);

    /**
     * The real expander.
     */
    private long[] expand(final String compactUuid, final BaseNCodec baseN) {
        final byte[] bytes = baseN.decode(compactUuid);
        if (bytes.length != BYTES_PER_2_LONGS) {
            throw new IllegalArgumentException("Not a compact uuid string: " + compactUuid);
        }
        int i = 0;
        long msb = bytes[i++];
        while (i < BYTES_PER_LONG) {
            msb = msb << Byte.SIZE | (bytes[i++] & LONG_BYTE_MASK);
        }
        long lsb = bytes[i++];
        while (i < BYTES_PER_2_LONGS) {
            lsb = lsb << Byte.SIZE | (bytes[i++] & LONG_BYTE_MASK);
        }
        return new long[] { msb, lsb };
    }

}
