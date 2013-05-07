# uuid-compactor

Encodes UUID into URL-safe string of 22 or 26 characters, and vice versa.

## Motivation

A UUID string is 36 characters, e.g. `be177dbe-5639-4ee1-90b1-09e108ffdddc`.

There are various reasons to want to represent a UUID in a shorter string.

A UUID can be Base64 encoded to a URL-safe string of 22 characters.<br />
Or for an extra 4 characters, Base32 encoded to 26 characters.

The advantage of using Base32 is that the encoding alphabets are unambiguous;<br />
they are all the upper case letters `A` to `Z`, and the digits `2` to `7`,<br />
no `0`, `1`, `8` to confuse with `O`, `I`, `l`, `B`.

## Usage

    // Create compactor, or inject as singleton, it is thread-safe.
    static final UuidCompactor compactor = new UuidCompactor();
    UUID uuid = UUID.randomUUID();
    // compact
    String compactUuid = compactor.compact64(uuid);
    // expand
    UUID uuid2 = compactor.expand64(compactUuid);
    assert uuid2.equals(uuid);

## build

    gradlew build javadoc
