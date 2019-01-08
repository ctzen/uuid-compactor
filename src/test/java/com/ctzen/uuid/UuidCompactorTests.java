package com.ctzen.uuid;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UuidCompactor unit tests.
 *
 * @author cchang
 */
public class UuidCompactorTests {

    private final UuidCompactor compactor = new UuidCompactor();

    @DataProvider(name = "edgeCasesData")
    public Object[][] edgeCasesData() {
        return new Object[][] {
            { 0, 0 },
            { Long.MIN_VALUE, 0 },
            { 0, Long.MIN_VALUE },
            { Long.MIN_VALUE, Long.MIN_VALUE },
            { Long.MIN_VALUE, Long.MAX_VALUE },
            { Long.MAX_VALUE, 0 },
            { 0, Long.MAX_VALUE },
            { Long.MAX_VALUE, Long.MIN_VALUE },
            { Long.MAX_VALUE, Long.MAX_VALUE },
        };
    }

    @Test(dataProvider = "edgeCasesData")
    public void edgeCases64(final long msb, final long lsb) {
        final String compactUuid = compactor.compact64(msb, lsb);
        assert64(new UUID(msb, lsb), compactUuid);
    }

    @Test(dataProvider = "edgeCasesData")
    public void edgeCases32(final long msb, final long lsb) {
        final String compactUuid = compactor.compact32(msb, lsb);
        assert32(new UUID(msb, lsb), compactUuid);
    }

    @Test
    public void randomUuids() {
        for (int i = 0; i < 10000; ++i) {
            final UUID uuid = UUID.randomUUID();
            assert64(uuid, compactor.compact64(uuid));
            assert32(uuid, compactor.compact32(uuid));
        }
    }

    @Test
    public void randomUuidStrings() {
        for (int i = 0; i < 10000; ++i) {
            final UUID uuid = UUID.randomUUID();
            assert64(uuid, compactor.compact64(uuid.toString()));
            assert32(uuid, compactor.compact32(uuid.toString()));
        }
    }

    private void assert64(final UUID uuid, final String compact64Uuid) {
        assertThat(compact64Uuid)
            .isNotNull()
            .hasSize(UuidCompactor.COMPACT64_LEN);
        assertThat(compactor.expand64(compact64Uuid))
            .isEqualTo(uuid);
        assertThat(compactor.expand(compact64Uuid))
            .isEqualTo(uuid);
    }

    private void assert32(final UUID uuid, final String compact32Uuid) {
        assertThat(compact32Uuid)
            .isNotNull()
            .hasSize(UuidCompactor.COMPACT32_LEN);
        assertThat(compactor.expand32(compact32Uuid))
            .isEqualTo(uuid);
        assertThat(compactor.expand(compact32Uuid))
            .isEqualTo(uuid);
    }

    private static String repeat(final char c, final int times) {
        final StringBuilder sb = new StringBuilder(times);
        for (int i = 0; i < times; ++i) {
            sb.append(c);
        }
        return sb.toString();
    }

    // NOT in Base32 and Base64 alphabets.
    private static final char BAD_CHAR = '%';

    @DataProvider(name = "expandBadCharData")
    public Object[][] expandBadCharData() {
        return new Object[][] {
            { repeat('A', UuidCompactor.COMPACT64_LEN - 1) + BAD_CHAR },
            { repeat('A', UuidCompactor.COMPACT32_LEN - 1) + BAD_CHAR },
        };
    }

    @Test(dataProvider = "expandBadCharData"
        , expectedExceptions = IllegalArgumentException.class
        , expectedExceptionsMessageRegExp = "Not a compact uuid string:.*")
    public void expandBadChar(final String compactUuid) {
        compactor.expand(compactUuid);
    }

    private static final Object[][] COMMON_BAD_LENGTH_DATA =
        new Object[][] {
            { "" },
            { "A" },
            { repeat('A', UuidCompactor.COMPACT64_LEN - 1) },   // COMPACT64_LEN - 1
            { repeat('A', UuidCompactor.COMPACT64_LEN + 1) },   // COMPACT64_LEN + 1
            { repeat('A', UuidCompactor.COMPACT32_LEN - 1) },   // COMPACT32_LEN - 1
            { repeat('A', UuidCompactor.COMPACT32_LEN + 1) },   // COMPACT32_LEN + 1
        };

    @DataProvider(name = "expandBadLengthData")
    public Object[][] expandBadLengthData() {
        return COMMON_BAD_LENGTH_DATA;
    }

    @Test(dataProvider = "expandBadLengthData"
        , expectedExceptions = IllegalArgumentException.class
        , expectedExceptionsMessageRegExp = "Not a compact uuid string:.*")
    public void expandBadLength(final String compactUuid) {
        compactor.expand(compactUuid);
    }

    @DataProvider(name = "expand64BadLengthData")
    public Object[][] expand64BadLengthData() {
        final Object[][] ret = Arrays.copyOf(COMMON_BAD_LENGTH_DATA, COMMON_BAD_LENGTH_DATA.length + 1);
        // Add COMPACT32_LEN
        ret[ret.length - 1] = new Object[] { repeat('A', UuidCompactor.COMPACT32_LEN) };
        return ret;
    }

    @Test(dataProvider = "expand64BadLengthData"
        , expectedExceptions = IllegalArgumentException.class
        , expectedExceptionsMessageRegExp = "Expecting a compact uuid string of length " + UuidCompactor.COMPACT64_LEN)
    public void expand64BadLength(final String compactUuid) {
        compactor.expand64(compactUuid);
    }

    @DataProvider(name = "expand32BadLengthData")
    public Object[][] expand32BadLengthData() {
        final Object[][] ret = Arrays.copyOf(COMMON_BAD_LENGTH_DATA, COMMON_BAD_LENGTH_DATA.length + 1);
        // Add COMPACT64_LEN
        ret[ret.length - 1] = new Object[] { repeat('A', UuidCompactor.COMPACT64_LEN) };
        return ret;
    }

    @Test(dataProvider = "expand32BadLengthData"
        , expectedExceptions = IllegalArgumentException.class
        , expectedExceptionsMessageRegExp = "Expecting a compact uuid string of length " + UuidCompactor.COMPACT32_LEN)
    public void expand32BadLength(final String compactUuid) {
        compactor.expand32(compactUuid);
    }

}
