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

import java.io.InputStream;

import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

/**
 * Soundfont sample storage.
 *
 * @author Karl Helgason
 */
public class SF2Sample extends SoundbankResource {

    protected String name = "";
    protected long startLoop = 0;
    protected long endLoop = 0;
    protected long sampleRate = 44100;
    protected int originalPitch = 60;
    protected byte pitchCorrection = 0;
    protected int sampleLink = 0;
    protected int sampleType = 0;
    protected ModelByteBuffer data;
    protected ModelByteBuffer data24;

    public SF2Sample(Soundbank soundBank) {
        super(soundBank, null, AudioInputStream.class);
    }

    public SF2Sample() {
        super(null, null, AudioInputStream.class);
    }

    public Object getData() {

        AudioFormat format = getFormat();
        /*
        if (sampleFile != null) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(sampleFile);
                RIFFReader riff = new RIFFReader(fis);
                if (!riff.getFormat().equals("RIFF")) {
                    throw new RIFFInvalidDataException(
                        "Input stream is not a valid RIFF stream!");
                }
                if (!riff.getType().equals("sfbk")) {
                    throw new RIFFInvalidDataException(
                        "Input stream is not a valid SoundFont!");
                }
                while (riff.hasNextChunk()) {
                    RIFFReader chunk = riff.nextChunk();
                    if (chunk.getFormat().equals("LIST")) {
                        if (chunk.getType().equals("sdta")) {
                            while(chunk.hasNextChunk()) {
                                RIFFReader chunkchunk = chunk.nextChunk();
                                if(chunkchunk.getFormat().equals("smpl")) {
                                    chunkchunk.skip(sampleOffset);
                                    return new AudioInputStream(chunkchunk,
                                            format, sampleLen);
                                }
                            }
                        }
                    }
                }
                return null;
            } catch (Exception e) {
                return new Throwable(e.toString());
            }
        }
        */
        InputStream is = data.getInputStream();
        if (is == null)
            return null;
        return new AudioInputStream(is, format, data.capacity());
    }

    public ModelByteBuffer getDataBuffer() {
        return data;
    }

    public ModelByteBuffer getData24Buffer() {
        return data24;
    }

    public AudioFormat getFormat() {
        return new AudioFormat(sampleRate, 16, 1, true, false);
    }

    public void setData(ModelByteBuffer data) {
        this.data = data;
    }

    public void setData(byte[] data) {
        this.data = new ModelByteBuffer(data);
    }

    public void setData(byte[] data, int offset, int length) {
        this.data = new ModelByteBuffer(data, offset, length);
    }

    public void setData24(ModelByteBuffer data24) {
        this.data24 = data24;
    }

    public void setData24(byte[] data24) {
        this.data24 = new ModelByteBuffer(data24);
    }

    public void setData24(byte[] data24, int offset, int length) {
        this.data24 = new ModelByteBuffer(data24, offset, length);
    }

    /*
    public void setData(File file, int offset, int length) {
        this.data = null;
        this.sampleFile = file;
        this.sampleOffset = offset;
        this.sampleLen = length;
    }
    */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getEndLoop() {
        return endLoop;
    }

    public void setEndLoop(long endLoop) {
        this.endLoop = endLoop;
    }

    public int getOriginalPitch() {
        return originalPitch;
    }

    public void setOriginalPitch(int originalPitch) {
        this.originalPitch = originalPitch;
    }

    public byte getPitchCorrection() {
        return pitchCorrection;
    }

    public void setPitchCorrection(byte pitchCorrection) {
        this.pitchCorrection = pitchCorrection;
    }

    public int getSampleLink() {
        return sampleLink;
    }

    public void setSampleLink(int sampleLink) {
        this.sampleLink = sampleLink;
    }

    public long getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(long sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getSampleType() {
        return sampleType;
    }

    public void setSampleType(int sampleType) {
        this.sampleType = sampleType;
    }

    public long getStartLoop() {
        return startLoop;
    }

    public void setStartLoop(long startLoop) {
        this.startLoop = startLoop;
    }

    public String toString() {
        return "Sample: " + name;
    }
}
