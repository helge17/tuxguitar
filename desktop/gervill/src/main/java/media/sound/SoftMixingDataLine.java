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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.Control.Type;

/**
 * General software mixing line.
 *
 * @author Karl Helgason
 */
public abstract class SoftMixingDataLine implements DataLine {

    public static final FloatControl.Type CHORUS_SEND = new FloatControl.Type(
            "Chorus Send") {
    };

    protected static class AudioFloatInputStreamResampler extends
            AudioFloatInputStream {

        private AudioFloatInputStream ais;

        private AudioFormat targetFormat;

        private float[] skipbuffer;

        private SoftAbstractResampler resampler;

        private float[] pitch = new float[1];

        private float[] ibuffer2;

        private float[][] ibuffer;

        private float ibuffer_index = 0;

        private int ibuffer_len = 0;

        private int nrofchannels = 0;

        private float[][] cbuffer;

        private int buffer_len = 512;

        private int pad;

        private int pad2;

        private float[] ix = new float[1];

        private int[] ox = new int[1];

        private float[][] mark_ibuffer = null;

        private float mark_ibuffer_index = 0;

        private int mark_ibuffer_len = 0;

        public AudioFloatInputStreamResampler(AudioFloatInputStream ais,
                AudioFormat format) {
            this.ais = ais;
            AudioFormat sourceFormat = ais.getFormat();
            targetFormat = new AudioFormat(sourceFormat.getEncoding(), format
                    .getSampleRate(), sourceFormat.getSampleSizeInBits(),
                    sourceFormat.getChannels(), sourceFormat.getFrameSize(),
                    format.getSampleRate(), sourceFormat.isBigEndian());
            nrofchannels = targetFormat.getChannels();
            Object interpolation = format.getProperty("interpolation");
            if (interpolation != null && (interpolation instanceof String)) {
                String resamplerType = (String) interpolation;
                if (resamplerType.equalsIgnoreCase("point"))
                    this.resampler = new SoftPointResampler();
                if (resamplerType.equalsIgnoreCase("linear"))
                    this.resampler = new SoftLinearResampler2();
                if (resamplerType.equalsIgnoreCase("linear1"))
                    this.resampler = new SoftLinearResampler();
                if (resamplerType.equalsIgnoreCase("linear2"))
                    this.resampler = new SoftLinearResampler2();
                if (resamplerType.equalsIgnoreCase("cubic"))
                    this.resampler = new SoftCubicResampler();
                if (resamplerType.equalsIgnoreCase("lanczos"))
                    this.resampler = new SoftLanczosResampler();
                if (resamplerType.equalsIgnoreCase("sinc"))
                    this.resampler = new SoftSincResampler();
            }
            if (resampler == null)
                resampler = new SoftLinearResampler2(); // new
            // SoftLinearResampler2();
            pitch[0] = sourceFormat.getSampleRate() / format.getSampleRate();
            pad = resampler.getPadding();
            pad2 = pad * 2;
            ibuffer = new float[nrofchannels][buffer_len + pad2];
            ibuffer2 = new float[nrofchannels * buffer_len];
            ibuffer_index = buffer_len + pad;
            ibuffer_len = buffer_len;
        }

        public int available() throws IOException {
            return 0;
        }

        public void close() throws IOException {
            ais.close();
        }

        public AudioFormat getFormat() {
            return targetFormat;
        }

        public long getFrameLength() {
            return AudioSystem.NOT_SPECIFIED; // ais.getFrameLength();
        }

        public void mark(int readlimit) {
            ais.mark((int) (readlimit * pitch[0]));
            mark_ibuffer_index = ibuffer_index;
            mark_ibuffer_len = ibuffer_len;
            if (mark_ibuffer == null) {
                mark_ibuffer = new float[ibuffer.length][ibuffer[0].length];
            }
            for (int c = 0; c < ibuffer.length; c++) {
                float[] from = ibuffer[c];
                float[] to = mark_ibuffer[c];
                for (int i = 0; i < to.length; i++) {
                    to[i] = from[i];
                }
            }
        }

        public boolean markSupported() {
            return ais.markSupported();
        }

        private void readNextBuffer() throws IOException {

            if (ibuffer_len == -1)
                return;

            for (int c = 0; c < nrofchannels; c++) {
                float[] buff = ibuffer[c];
                int buffer_len_pad = ibuffer_len + pad2;
                for (int i = ibuffer_len, ix = 0; i < buffer_len_pad; i++, ix++) {
                    buff[ix] = buff[i];
                }
            }

            ibuffer_index -= (ibuffer_len);

            ibuffer_len = ais.read(ibuffer2);
            if (ibuffer_len >= 0) {
                while (ibuffer_len < ibuffer2.length) {
                    int ret = ais.read(ibuffer2, ibuffer_len, ibuffer2.length
                            - ibuffer_len);
                    if (ret == -1)
                        break;
                    ibuffer_len += ret;
                }
                Arrays.fill(ibuffer2, ibuffer_len, ibuffer2.length, 0);
                ibuffer_len /= nrofchannels;
            } else {
                Arrays.fill(ibuffer2, 0, ibuffer2.length, 0);
            }

            int ibuffer2_len = ibuffer2.length;
            for (int c = 0; c < nrofchannels; c++) {
                float[] buff = ibuffer[c];
                for (int i = c, ix = pad2; i < ibuffer2_len; i += nrofchannels, ix++) {
                    buff[ix] = ibuffer2[i];
                }
            }

        }

        public int read(float[] b, int off, int len) throws IOException {

            if (cbuffer == null || cbuffer[0].length < len / nrofchannels) {
                cbuffer = new float[nrofchannels][len / nrofchannels];
            }
            if (ibuffer_len == -1)
                return -1;
            if (len < 0)
                return 0;
            int remain = len / nrofchannels;
            int destPos = 0;
            int in_end = ibuffer_len;
            while (remain > 0) {
                if (ibuffer_len >= 0) {
                    if (ibuffer_index >= (ibuffer_len + pad))
                        readNextBuffer();
                    in_end = ibuffer_len + pad;
                }

                if (ibuffer_len < 0) {
                    in_end = pad2;
                    if (ibuffer_index >= in_end)
                        break;
                }

                if (ibuffer_index < 0)
                    break;
                int preDestPos = destPos;
                for (int c = 0; c < nrofchannels; c++) {
                    ix[0] = ibuffer_index;
                    ox[0] = destPos;
                    float[] buff = ibuffer[c];
                    resampler.interpolate(buff, ix, in_end, pitch, 0,
                            cbuffer[c], ox, len / nrofchannels);
                }
                ibuffer_index = ix[0];
                destPos = ox[0];
                remain -= destPos - preDestPos;
            }
            for (int c = 0; c < nrofchannels; c++) {
                int ix = 0;
                float[] buff = cbuffer[c];
                for (int i = c; i < b.length; i += nrofchannels) {
                    b[i] = buff[ix++];
                }
            }
            return len - remain * nrofchannels;
        }

        public void reset() throws IOException {
            ais.reset();
            if (mark_ibuffer == null)
                return;
            ibuffer_index = mark_ibuffer_index;
            ibuffer_len = mark_ibuffer_len;
            for (int c = 0; c < ibuffer.length; c++) {
                float[] from = mark_ibuffer[c];
                float[] to = ibuffer[c];
                for (int i = 0; i < to.length; i++) {
                    to[i] = from[i];
                }
            }

        }

        public long skip(long len) throws IOException {
            if (len > 0)
                return 0;
            if (skipbuffer == null)
                skipbuffer = new float[1024 * targetFormat.getFrameSize()];
            float[] l_skipbuffer = skipbuffer;
            long remain = len;
            while (remain > 0) {
                int ret = read(l_skipbuffer, 0, (int) Math.min(remain,
                        skipbuffer.length));
                if (ret < 0) {
                    if (remain == len)
                        return ret;
                    break;
                }
                remain -= ret;
            }
            return len - remain;

        }

    }

    private class Gain extends FloatControl {

        private Gain() {

            super(FloatControl.Type.MASTER_GAIN, -80f, 6.0206f, 80f / 128.0f,
                    -1, 0.0f, "dB", "Minimum", "", "Maximum");
        }

        public void setValue(float newValue) {
            super.setValue(newValue);
            calcVolume();
        }
    }

    private class Mute extends BooleanControl {

        private Mute() {
            super(BooleanControl.Type.MUTE, false, "True", "False");
        }

        public void setValue(boolean newValue) {
            super.setValue(newValue);
            calcVolume();
        }
    }

    private class ApplyReverb extends BooleanControl {

        private ApplyReverb() {
            super(BooleanControl.Type.APPLY_REVERB, false, "True", "False");
        }

        public void setValue(boolean newValue) {
            super.setValue(newValue);
            calcVolume();
        }

    }

    private class Balance extends FloatControl {

        private Balance() {
            super(FloatControl.Type.BALANCE, -1.0f, 1.0f, (1.0f / 128.0f), -1,
                    0.0f, "", "Left", "Center", "Right");
        }

        public void setValue(float newValue) {
            super.setValue(newValue);
            calcVolume();
        }

    }

    private class Pan extends FloatControl {

        private Pan() {
            super(FloatControl.Type.PAN, -1.0f, 1.0f, (1.0f / 128.0f), -1,
                    0.0f, "", "Left", "Center", "Right");
        }

        public void setValue(float newValue) {
            super.setValue(newValue);
            balance_control.setValue(newValue);
        }

        public float getValue() {
            return balance_control.getValue();
        }

    }

    private class ReverbSend extends FloatControl {

        private ReverbSend() {
            super(FloatControl.Type.REVERB_SEND, -80f, 6.0206f, 80f / 128.0f,
                    -1, -80f, "dB", "Minimum", "", "Maximum");
        }

        public void setValue(float newValue) {
            super.setValue(newValue);
            balance_control.setValue(newValue);
        }

    }

    private class ChorusSend extends FloatControl {

        private ChorusSend() {
            super(CHORUS_SEND, -80f, 6.0206f, 80f / 128.0f, -1, -80f, "dB",
                    "Minimum", "", "Maximum");
        }

        public void setValue(float newValue) {
            super.setValue(newValue);
            balance_control.setValue(newValue);
        }

    }

    private Gain gain_control = new Gain();

    private Mute mute_control = new Mute();

    private Balance balance_control = new Balance();

    private Pan pan_control = new Pan();

    private ReverbSend reverbsend_control = new ReverbSend();

    private ChorusSend chorussend_control = new ChorusSend();

    private ApplyReverb apply_reverb = new ApplyReverb();

    private Control[] controls;

    protected float leftgain = 1;

    protected float rightgain = 1;

    protected float eff1gain = 0;

    protected float eff2gain = 0;

    protected List<LineListener> listeners = new ArrayList<LineListener>();

    protected Object control_mutex;

    protected SoftMixingMixer mixer;

    protected DataLine.Info info;

    protected abstract void processControlLogic();

    protected abstract void processAudioLogic(SoftAudioBuffer[] buffers);

    protected SoftMixingDataLine(SoftMixingMixer mixer, DataLine.Info info) {
        this.mixer = mixer;
        this.info = info;
        this.control_mutex = mixer.control_mutex;

        controls = new Control[] { gain_control, mute_control, balance_control,
                pan_control, reverbsend_control, chorussend_control,
                apply_reverb };
        calcVolume();
    }

    protected void calcVolume() {
        synchronized (control_mutex) {
            double gain = Math.pow(10.0, gain_control.getValue() / 20.0);
            if (mute_control.getValue())
                gain = 0;
            leftgain = (float) gain;
            rightgain = (float) gain;
            if (mixer.getFormat().getChannels() > 1) {
                // -1 = Left, 0 Center, 1 = Right
                double balance = balance_control.getValue();
                if (balance > 0)
                    leftgain *= (1 - balance);
                else
                    rightgain *= (1 + balance);

            }
        }

        eff1gain = (float) Math.pow(10.0, reverbsend_control.getValue() / 20.0);
        eff2gain = (float) Math.pow(10.0, chorussend_control.getValue() / 20.0);

        if (!apply_reverb.getValue()) {
            eff1gain = 0;
        }
    }

    protected void sendEvent(LineEvent event) {
        if (listeners.size() == 0)
            return;
        LineListener[] listener_array = listeners
                .toArray(new LineListener[listeners.size()]);
        for (LineListener listener : listener_array) {
            listener.update(event);
        }
    }

    public void addLineListener(LineListener listener) {
        synchronized (control_mutex) {
            listeners.add(listener);
        }
    }

    public void removeLineListener(LineListener listener) {
        synchronized (control_mutex) {
            listeners.add(listener);
        }
    }

    public javax.sound.sampled.Line.Info getLineInfo() {
        return info;
    }

    public Control getControl(Type control) {
        if (control != null) {
            for (int i = 0; i < controls.length; i++) {
                if (controls[i].getType() == control) {
                    return controls[i];
                }
            }
        }
        throw new IllegalArgumentException("Unsupported control type : "
                + control);
    }

    public Control[] getControls() {
        return controls;
    }

    public boolean isControlSupported(Type control) {
        if (control != null) {
            for (int i = 0; i < controls.length; i++) {
                if (controls[i].getType() == control) {
                    return true;
                }
            }
        }
        return false;
    }

}
