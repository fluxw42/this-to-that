package com.github.fluxw42.thistothat;

import com.github.fluxw42.thistothat.converter.audio.AudioConverter;
import com.github.fluxw42.thistothat.converter.audio.AudioFormat;
import com.github.fluxw42.thistothat.converter.Converter;

import java.io.File;

/**
 * Date: 2/2/16 - 11:51 PM
 *
 * @author Jeroen Meulemeester
 */
public class Main {

    public static void main(String[] args) throws Exception {
        final Converter converter = new AudioConverter(
                new File(args[0]),
                new File(args[1]),
                AudioFormat.MP3
        );
        converter.convert(System.out::println);
    }

}
