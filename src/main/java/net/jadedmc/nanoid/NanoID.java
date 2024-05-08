/*
 * This file is part of NanoID, licensed under the MIT License.
 *
 *  Copyright (c) 2017 The JNanoID Authors
 *  Copyright (c) 2017 Aventrix LLC
 *  Copyright (c) 2017 Andrey Sitnik
 *  Copyright (c) 2024 JadedMC
 *  Copyright (c) 2024 contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.jadedmc.nanoid;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

public class NanoID {
    private static final byte DEFAULT_SIZE = 21;
    private static final String DEFAULT_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz-";
    public static final SecureRandom DEFAULT_RANDOM = new SecureRandom();
    private final byte[] bytes;

    /**
     * Creates a NanoID with all default values. Generates a similar number of possible Ids as UUID v4.
     * Generator: SecureRandom
     * Size: 21
     * Alphabet: "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz-"
     */
    public NanoID() {
        this(DEFAULT_RANDOM, DEFAULT_SIZE, DEFAULT_ALPHABET);
    }

    /**
     * Creates a NanoID with the default generator and alphabet, but with a given size.
     * @param size Size of the NanoID.
     */
    public NanoID(final int size) {
        this(DEFAULT_RANDOM, size, DEFAULT_ALPHABET);
    }

    /**
     * Creates a NanoID with the default generator, with a given size and alphabet.
     * @param size Size of the NanoID.
     * @param alphabet Alphabet the NanoID uses.
     */
    public NanoID(final int size, final String alphabet) {
        this(DEFAULT_RANDOM, size, alphabet);
    }

    /**
     * Creates a NanoID with a given generator, size, and alphabet.
     * @param random Random Number Generator to create the NanoID with.
     * @param size Size of the NanoID.
     * @param alphabet Alphabet the NanoID uses.
     */
    public NanoID(final Random random, final int size, final String alphabet) {
        this(generateNanoIDString(random, size, alphabet).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Creates a NanoID with a given byte array.
     * @param bytes Byte array that makes up the NanoID.
     */
    public NanoID(final byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * Converts the NanoID into it's byte form.
     * @return Byte representation of the NanoID.
     */
    public final byte[] asByteArray() {
        return bytes;
    }


    /**
     * Compares an object to this NanoID.
     * Only returns true if the object is a NanoID and has the same byte array
     * @param object Object to be compared.
     * @return true if the objects are the same; false otherwise.
     */
    @Override
    public boolean equals(final Object object) {
        // Null-objects cannot be equal to the NanoID.
        if(object == null) {
            return false;
        }

        // Non-NanoID objects cannot be equal to the NanoID.
        if(!(object instanceof NanoID)) {
            return false;
        }

        // Check if the bytes match.
        final NanoID otherID = (NanoID) object;
        return Arrays.equals(otherID.asByteArray(), this.bytes);
    }

    /**
     * Gets the length of the ID (in number of bytes).
     * @return Number of bytes that make up the ID.
     */
    public final int size() {
        return this.bytes.length;
    }

    /**
     * Converts the NanoID into its String form.
     * @return A String representation of the NanoID.
     */
    @Override
    public String toString() {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Get an NanoID object from its representative String.
     * @param string String representing the NanoID.
     * @return NanoID that is represented by the given String.
     */
    public static NanoID fromString(final String string) {
        return new NanoID(string.getBytes(StandardCharsets.UTF_8));
    }

    public static String generateNanoIDString(final Random random, final int size, final String alphabet) {
        final int mask = (2 << (int) Math.floor(Math.log(alphabet.length() - 1) / Math.log(2))) - 1;
        final int step = (int) Math.ceil(1.6 * mask * size / alphabet.length());

        final StringBuilder idBuilder = new StringBuilder();

        while (true) {
            final byte[] bytes = new byte[step];
            random.nextBytes(bytes);

            for (int i = 0; i < step; i++) {
                final int alphabetIndex = bytes[i] & mask;

                if (alphabetIndex < alphabet.length()) {
                    idBuilder.append(alphabet.charAt(alphabetIndex));
                    if (idBuilder.length() == size) {
                        return idBuilder.toString();
                    }
                }
            }
        }
    }
}