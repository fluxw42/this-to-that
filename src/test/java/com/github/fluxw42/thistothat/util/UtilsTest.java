package com.github.fluxw42.thistothat.util;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Date: 2/6/16 - 3:44 PM
 *
 * @author Jeroen Meulemeester
 */
public class UtilsTest {

    @Test
    public void testSha256() throws Exception {
        assertArrayEquals(
                Utils.fromHex("E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855"),
                Utils.sha256(new ByteArrayInputStream(new byte[0]))
        );
        assertArrayEquals(
                Utils.fromHex("C0535E4BE2B79FFD93291305436BF889314E4A3FAEC05ECFFCBB7DF31AD9E51A"),
                Utils.sha256(new ByteArrayInputStream("Hello world!".getBytes(StandardCharsets.US_ASCII)))
        );
    }

    @Test
    public void testAsHex() throws Exception {
        assertEquals("", Utils.asHex(null));
        assertEquals("", Utils.asHex(new byte[0]));
        assertEquals("000102", Utils.asHex(new byte[]{0, 1, 2}));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromHexNull() throws Exception {
        Utils.fromHex(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromHexUnevenNibbles() throws Exception {
        Utils.fromHex("123");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromHexIllegalCharacter() throws Exception {
        Utils.fromHex("123-56");
    }

    @Test
    public void testFromHex() throws Exception {
        assertArrayEquals(new byte[0], Utils.fromHex(""));
        assertArrayEquals(new byte[]{0, 1, 2}, Utils.fromHex("000102"));
    }

}