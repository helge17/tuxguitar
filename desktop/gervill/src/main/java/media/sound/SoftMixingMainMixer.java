/*
 * Copyright 2008 Sun Microsystems, Inc.  All Rights Reserved.
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * Main mixer for SoftMixingMixer.
 *
 * @author Karl Helgason
 */
public class SoftMixingMainMixer {

    public final static int CHANNEL_LEFT = 0;

    public final static int CHANNEL_RIGHT = 1;

    public final static int CHANNEL_EFFECT1 = 2;

    public final static int CHANNEL_EFFECT2 = 3;

    public final static int CHANNEL_EFFECT3 = 4;

    public final static int CHANNEL_EFFECT4 = 5;

    public final static int CHANNEL_LEFT_DRY = 10;

    public final static int CHANNEL_RIGHT_DRY = 11;

    public final static int CHANNEL_SCRATCH1 = 12;

    public final static int CHANNEL_SCRATCH2 = 13;

    public final static int CHANNEL_CHANNELMIXER_LEFT = 14;

    public final static int CHANNEL_CHANNELMIXER_RIGHT = 15;

    private SoftMixingMixer mixer;

    private AudioInputStream ais;

    private SoftAudioBuffer[] buffers;

    private SoftAudioProcessor reverb;

    private SoftAudioProcessor chorus;

    private SoftAudioProcessor agc;

    private int nrofchannels;

    private Object control_mutex;

    private List<SoftMixingDataLine> openLinesList = new ArrayList<SoftMixingDataLine>();

    private SoftMixingDataLine[] openLines = new SoftMixingDataLine[0];

    public AudioInputStream getInputStream() {
        return ais;
    }

    protected void processAudioBuffers() {
        for (int i = 0; i < buffers.length; i++) {
            buffers[i].clear();
        }

        SoftMixingDataLine[] openLines;
        synchronized (control_mutex) {
            openLines = this.openLines;
            for (int i = 0; i < openLines.length; i++) {
                openLines[i].processControlLogic();
            }
            chorus.processControlLogic();
            reverb.processControlLogic();
            agc.processControlLogic();
        }
        for (int i = 0; i < openLines.length; i++) {
            openLines[i].processAudioLogic(buffers);
        }

        chorus.processAudio();
        reverb.processAudio();

        agc.processAudio();

    }

    public SoftMixingMainMixer(SoftMixingMixer mixer) {
        this.mixer = mixer;

        nrofchannels = mixer.getFormat().getChannels();

        int buffersize = (int) (mixer.getFormat().getSampleRate() / mixer
                .getControlRate());

        control_mutex = mixer.control_mutex;
        buffers = new SoftAudioBuffer[16];
        for (int i = 0; i < buffers.length; i++) {
            buffers[i] = new SoftAudioBuffer(buffersize, mixer.getFormat());

        }

        reverb = new SoftReverb();
        chorus = new SoftChorus();
        agc = new SoftLimiter();

        float samplerate = mixer.getFormat().getSampleRate();
        float controlrate = mixer.getControlRate();
        reverb.init(samplerate, controlrate);
        chorus.init(samplerate, controlrate);
        agc.init(samplerate, controlrate);

        reverb.setMixMode(true);
        chorus.setMixMode(true);
        agc.setMixMode(false);

        chorus.setInput(0, buffers[CHANNEL_EFFECT2]);
        chorus.setOutput(0, buffers[CHANNEL_LEFT]);
        if (nrofchannels != 1)
            chorus.setOutput(1, buffers[CHANNEL_RIGHT]);
        chorus.setOutput(2, buffers[CHANNEL_EFFECT1]);

        reverb.setInput(0, buffers[CHANNEL_EFFECT1]);
        reverb.setOutput(0, buffers[CHANNEL_LEFT]);
        if (nrofchannels != 1)
            reverb.setOutput(1, buffers[CHANNEL_RIGHT]);

        agc.setInput(0, buffers[CHANNEL_LEFT]);
        if (nrofchannels != 1)
            agc.setInput(1, buffers[CHANNEL_RIGHT]);
        agc.setOutput(0, buffers[CHANNEL_LEFT]);
        if (nrofchannels != 1)
            agc.setOutput(1, buffers[CHANNEL_RIGHT]);

        InputStream in = new InputStream() {

            private SoftAudioBuffer[] buffers = SoftMixingMainMixer.this.buffers;

            private int nrofchannels = SoftMixingMainMixer.this.mixer
                    .getFormat().getChannels();

            private int buffersize = buffers[0].getSize();

            private byte[] bbuffer = new byte[buffersize
                    * (SoftMixingMainMixer.this.mixer.getFormat()
                            .getSampleSizeInBits() / 8) * nrofchannels];

            private int bbuffer_pos = 0;

            private byte[] single = new byte[1];

            public void fillBuffer() {
                processAudioBuffers();
                for (int i = 0; i < nrofchannels; i++)
                    buffers[i].get(bbuffer, i);
                bbuffer_pos = 0;
            }

            public int read(byte[] b, int off, int len) {
                int bbuffer_len = bbuffer.length;
                int offlen = off + len;
                byte[] bbuffer = this.bbuffer;
                while (off < offlen)
                    if (available() == 0)
                        fillBuffer();
                    else {
                        int bbuffer_pos = this.bbuffer_pos;
                        while (off < offlen && bbuffer_pos < bbuffer_len)
                            b[off++] = bbuffer[bbuffer_pos++];
                        this.bbuffer_pos = bbuffer_pos;
                    }
                return len;
            }

            public int read() throws IOException {
                int ret = read(single);
                if (ret == -1)
                    return -1;
                return single[0] & 0xFF;
            }

            public int available() {
                return bbuffer.length - bbuffer_pos;
            }

            public void close() {
                SoftMixingMainMixer.this.mixer.close();
            }

        };

        ais = new AudioInputStream(in, mixer.getFormat(),
                AudioSystem.NOT_SPECIFIED);

    }

    public void openLine(SoftMixingDataLine line) {
        synchronized (control_mutex) {
            openLinesList.add(line);
            openLines = openLinesList
                    .toArray(new SoftMixingDataLine[openLinesList.size()]);
        }
    }

    public void closeLine(SoftMixingDataLine line) {
        synchronized (control_mutex) {
            openLinesList.remove(line);
            openLines = openLinesList
                    .toArray(new SoftMixingDataLine[openLinesList.size()]);
            if (openLines.length == 0)
                if (mixer.implicitOpen)
                    mixer.close();
        }

    }

    public SoftMixingDataLine[] getOpenLines() {
        synchronized (control_mutex) {
            return openLines;
        }

    }

    public void close() {
        SoftMixingDataLine[] openLines = this.openLines;
        for (int i = 0; i < openLines.length; i++) {
            openLines[i].close();
        }
    }

}
