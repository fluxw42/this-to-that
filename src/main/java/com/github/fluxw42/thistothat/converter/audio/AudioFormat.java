package com.github.fluxw42.thistothat.converter.audio;

/**
 * Date: 2/6/16 - 2:16 AM
 *
 * @author Jeroen Meulemeester
 */
public enum AudioFormat {

    MP3("mp3");

    /**
     * The audio format name
     */
    private final String name;

    /**
     * Create a new {@link AudioFormat} instance with the given name
     *
     * @param name The name of the audio format
     */
    AudioFormat(final String name) {
        this.name = name;
    }

    /**
     * Get the name of the {@link AudioFormat}
     *
     * @return The name of the format
     */
    public final String getName() {
        return this.name;
    }

}
