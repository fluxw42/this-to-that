package com.github.fluxw42.thistothat.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Date: 2/9/16 - 1:58 PM
 *
 * @author Jeroen Meulemeester
 */
public class OSTypeTest {

    @Test
    public void testGetOSType() throws Exception {
        assertNotNull(OSType.getOSType());
    }

}