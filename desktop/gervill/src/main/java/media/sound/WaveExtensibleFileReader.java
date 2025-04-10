/*
 * Copyright 2007 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */
package media.sound;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.spi.AudioFileReader;

/**
 * WAVE file reader for files using format WAVE_FORMAT_EXTENSIBLE (0xFFFE).
 *
 * @author Karl Helgason
 */
public class WaveExtensibleFileReader extends AudioFileReader {

    static private class GUID {
        long i1;

        int s1;

        int s2;

        int x1;

        int x2;

        int x3;

        int x4;

        int x5;

        int x6;

        int x7;

        int x8;

        private GUID() {
        }

        public GUID(long i1, int s1, int s2, int x1, int x2, int x3, int x4,
                int x5, int x6, int x7, int x8) {
            this.i1 = i1;
            this.s1 = s1;
            this.s2 = s2;
            this.x1 = x1;
            this.x2 = x2;
            this.x3 = x3;
            this.x4 = x4;
            this.x5 = x5;
            this.x6 = x6;
            this.x7 = x7;
            this.x8 = x8;
        }

        public static GUID read(RIFFReader riff) throws IOException {
            GUID d = new GUID();
            d.i1 = riff.readUnsignedInt();
            d.s1 = riff.readUnsignedShort();
            d.s2 = riff.readUnsignedShort();
            d.x1 = riff.readUnsignedByte();
            d.x2 = riff.readUnsignedByte();
            d.x3 = riff.readUnsignedByte();
            d.x4 = riff.readUnsignedByte();
            d.x5 = riff.readUnsignedByte();
            d.x6 = riff.readUnsignedByte();
            d.x7 = riff.readUnsignedByte();
            d.x8 = riff.readUnsignedByte();
            return d;
        }

        public int hashCode() {
            return (int) i1;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof GUID))
                return false;
            GUID t = (GUID) obj;
            if (i1 != t.i1)
                return false;
            if (s1 != t.s1)
                return false;
            if (s2 != t.s2)
                return false;
            if (x1 != t.x1)
                return false;
            if (x2 != t.x2)
                return false;
            if (x3 != t.x3)
                return false;
            if (x4 != t.x4)
                return false;
            if (x5 != t.x5)
                return false;
            if (x6 != t.x6)
                return false;
            if (x7 != t.x7)
                return false;
            if (x8 != t.x8)
                return false;
            return true;
        }

    }

    private static String[] channelnames = { "FL", "FR", "FC", "LF",
            "BL",
            "BR", // 5.1
            "FLC", "FLR", "BC", "SL", "SR", "TC", "TFL", "TFC", "TFR", "TBL",
            "TBC", "TBR" };

    private static String[] allchannelnames = { "w1", "w2", "w3", "w4", "w5",
            "w6", "w7", "w8", "w9", "w10", "w11", "w12", "w13", "w14", "w15",
            "w16", "w17", "w18", "w19", "w20", "w21", "w22", "w23", "w24",
            "w25", "w26", "w27", "w28", "w29", "w30", "w31", "w32", "w33",
            "w34", "w35", "w36", "w37", "w38", "w39", "w40", "w41", "w42",
            "w43", "w44", "w45", "w46", "w47", "w48", "w49", "w50", "w51",
            "w52", "w53", "w54", "w55", "w56", "w57", "w58", "w59", "w60",
            "w61", "w62", "w63", "w64" };

    private static GUID SUBTYPE_PCM = new GUID(0x00000001, 0x0000, 0x0010,
            0x80, 0x00, 0x00, 0xaa, 0x00, 0x38, 0x9b, 0x71);

    private static GUID SUBTYPE_IEEE_FLOAT = new GUID(0x00000003, 0x0000,
            0x0010, 0x80, 0x00, 0x00, 0xaa, 0x00, 0x38, 0x9b, 0x71);

    private String decodeChannelMask(long channelmask) {
        StringBuffer sb = new StringBuffer();
        long m = 1;
        for (int i = 0; i < allchannelnames.length; i++) {
            if ((channelmask & m) != 0L) {
                if (i < channelnames.length) {
                    sb.append(channelnames[i] + " ");
                } else {
                    sb.append(allchannelnames[i] + " ");
                }
            }
            m *= 2L;
        }
        if (sb.length() == 0)
            return null;
        return sb.substring(0, sb.length() - 1);

    }

    public AudioFileFormat getAudioFileFormat(InputStream stream)
            throws UnsupportedAudioFileException, IOException {

        stream.mark(200);
        AudioFileFormat format;
        try {
            format = internal_getAudioFileFormat(stream);
        } finally {
            stream.reset();
        }
        return format;
    }

    private AudioFileFormat internal_getAudioFileFormat(InputStream stream)
            throws UnsupportedAudioFileException, IOException {

        RIFFReader riffiterator = new RIFFReader(stream);
        if (!riffiterator.getFormat().equals("RIFF"))
            throw new UnsupportedAudioFileException();
        if (!riffiterator.getType().equals("WAVE"))
            throw new UnsupportedAudioFileException();

        boolean fmt_found = false;
        boolean data_found = false;

        int channels = 1;
        long samplerate = 1;
        // long framerate = 1;
        int framesize = 1;
        int bits = 1;
        int validBitsPerSample = 1;
        long channelMask = 0;
        GUID subFormat = null;

        while (riffiterator.hasNextChunk()) {
            RIFFReader chunk = riffiterator.nextChunk();

            if (chunk.getFormat().equals("fmt ")) {
                fmt_found = true;

                int format = chunk.readUnsignedShort();
                if (format != 0xFFFE)
                    throw new UnsupportedAudioFileException(); // WAVE_FORMAT_EXTENSIBLE
                // only
                channels = chunk.readUnsignedShort();
                samplerate = chunk.readUnsignedInt();
                /* framerate = */chunk.readUnsignedInt();
                framesize = chunk.readUnsignedShort();
                bits = chunk.readUnsignedShort();
                int cbSize = chunk.readUnsignedShort();
                if (cbSize != 22)
                    throw new UnsupportedAudioFileException();
                validBitsPerSample = chunk.readUnsignedShort();
                if (validBitsPerSample > bits)
                    throw new UnsupportedAudioFileException();
                channelMask = chunk.readUnsignedInt();
                subFormat = GUID.read(chunk);

            }
            if (chunk.getFormat().equals("data")) {
                data_found = true;
                break;
            }
        }

        if (!fmt_found)
            throw new UnsupportedAudioFileException();
        if (!data_found)
            throw new UnsupportedAudioFileException();

        Map<String, Object> p = new HashMap<String, Object>();
        String s_channelmask = decodeChannelMask(channelMask);
        if (s_channelmask != null)
            p.put("channelOrder", s_channelmask);
        if (channelMask != 0)
            p.put("channelMask", channelMask);
        // validBitsPerSample is only informational for PCM data,
        // data is still encode according to SampleSizeInBits.
        p.put("validBitsPerSample", validBitsPerSample);

        AudioFormat audioformat = null;
        if (subFormat.equals(SUBTYPE_PCM)) {
            if (bits == 8) {
                audioformat = new AudioFormat(Encoding.PCM_UNSIGNED,
                        samplerate, bits, channels, framesize, samplerate,
                        false, p);
            } else {
                audioformat = new AudioFormat(Encoding.PCM_SIGNED, samplerate,
                        bits, channels, framesize, samplerate, false, p);
            }
        } else if (subFormat.equals(SUBTYPE_IEEE_FLOAT)) {
            audioformat = new AudioFormat(AudioFloatConverter.PCM_FLOAT,
                    samplerate, bits, channels, framesize, samplerate, false, p);
        } else
            throw new UnsupportedAudioFileException();

        AudioFileFormat fileformat = new AudioFileFormat(
                AudioFileFormat.Type.WAVE, audioformat,
                AudioSystem.NOT_SPECIFIED);
        return fileformat;
    }

    public AudioInputStream getAudioInputStream(InputStream stream)
            throws UnsupportedAudioFileException, IOException {

        AudioFileFormat format = getAudioFileFormat(stream);
        RIFFReader riffiterator = new RIFFReader(stream);
        if (!riffiterator.getFormat().equals("RIFF"))
            throw new UnsupportedAudioFileException();
        if (!riffiterator.getType().equals("WAVE"))
            throw new UnsupportedAudioFileException();
        while (riffiterator.hasNextChunk()) {
            RIFFReader chunk = riffiterator.nextChunk();
            if (chunk.getFormat().equals("data")) {
                return new AudioInputStream(chunk, format.getFormat(), chunk
                        .getSize());
            }
        }
        throw new UnsupportedAudioFileException();
    }

    public AudioFileFormat getAudioFileFormat(URL url)
            throws UnsupportedAudioFileException, IOException {
        InputStream stream = url.openStream();
        AudioFileFormat format;
        try {
            format = getAudioFileFormat(new BufferedInputStream(stream));
        } finally {
            stream.close();
        }
        return format;
    }

    public AudioFileFormat getAudioFileFormat(File file)
            throws UnsupportedAudioFileException, IOException {
        InputStream stream = new FileInputStream(file);
        AudioFileFormat format;
        try {
            format = getAudioFileFormat(new BufferedInputStream(stream));
        } finally {
            stream.close();
        }
        return format;
    }

    public AudioInputStream getAudioInputStream(URL url)
            throws UnsupportedAudioFileException, IOException {
        return getAudioInputStream(new BufferedInputStream(url.openStream()));
    }

    public AudioInputStream getAudioInputStream(File file)
            throws UnsupportedAudioFileException, IOException {
        return getAudioInputStream(new BufferedInputStream(new FileInputStream(
                file)));
    }

}
