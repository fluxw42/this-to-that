package com.github.fluxw42.thistothat.converter.audio;

import com.github.fluxw42.thistothat.converter.ConversionException;
import com.github.fluxw42.thistothat.converter.ConversionProgressListener;
import com.github.fluxw42.thistothat.converter.Converter;
import com.github.fluxw42.thistothat.jave.AudioAttributes;
import com.github.fluxw42.thistothat.jave.Encoder;
import com.github.fluxw42.thistothat.jave.EncoderException;
import com.github.fluxw42.thistothat.jave.EncoderProgressListener;
import com.github.fluxw42.thistothat.jave.EncodingAttributes;
import com.github.fluxw42.thistothat.jave.MultimediaInfo;

import java.io.File;

/**
 * Date: 2/5/16 - 3:51 PM
 *
 * @author Jeroen Meulemeester
 */
public class AudioConverter extends Converter {

    /**
     * The expected audio format
     */
    private final AudioFormat format;

    /**
     * Create a new audio converter to convert the input file to the given {@link AudioFormat}
     *
     * @param inputFile  The input audio file
     * @param outputFile The output audio file
     * @param format     The expected output format
     */
    public AudioConverter(final File inputFile, final File outputFile, final AudioFormat format) {
        super(inputFile, outputFile);
        this.format = format;
    }

    /**
     * {@inheritDoc}
     */
    public void convert(final ConversionProgressListener listener) throws ConversionException {
        try {
            final EncodingAttributes attributes = new EncodingAttributes();
            attributes.setAudioAttributes(new AudioAttributes());
            attributes.setFormat(this.format.getName());

            final Encoder encoder = new Encoder();
            encoder.encode(
                    getInputFile(),
                    getOutputFile(),
                    attributes,
                    mapListener(listener)
            );
        } catch (EncoderException e) {
            throw new ConversionException(getInputFile(), getOutputFile(), e);
        }
    }

    /**
     * Map the given {@link ConversionProgressListener} to a JAVE {@link EncoderProgressListener}
     *
     * @param listener The {@link ConversionProgressListener}
     * @return The {@link EncoderProgressListener}
     */
    private EncoderProgressListener mapListener(final ConversionProgressListener listener) {
        return new EncoderProgressListener() {
            public void sourceInfo(MultimediaInfo multimediaInfo) {

            }

            public void progress(int i) {
                if (listener != null) {
                    listener.state(i);
                }
            }

            public void message(String s) {

            }
        };
    }

}
