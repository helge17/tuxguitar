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

/**
 * ModelAbstractChannelMixer is ready for use class to implement
 * ModelChannelMixer interface.
 *
 * @author Karl Helgason
 */
public abstract class ModelAbstractChannelMixer implements ModelChannelMixer {

    @Override
    public abstract boolean process(float[][] buffer, int offset, int len);

    @Override
    public abstract void stop();

    @Override
    public void allNotesOff() {
    }

    @Override
    public void allSoundOff() {
    }

    @Override
    public void controlChange(int controller, int value) {
    }

    @Override
    public int getChannelPressure() {
        return 0;
    }

    @Override
    public int getController(int controller) {
        return 0;
    }

    @Override
    public boolean getMono() {
        return false;
    }

    @Override
    public boolean getMute() {
        return false;
    }

    @Override
    public boolean getOmni() {
        return false;
    }

    @Override
    public int getPitchBend() {
        return 0;
    }

    @Override
    public int getPolyPressure(int noteNumber) {
        return 0;
    }

    @Override
    public int getProgram() {
        return 0;
    }

    @Override
    public boolean getSolo() {
        return false;
    }

    @Override
    public boolean localControl(boolean on) {
        return false;
    }

    @Override
    public void noteOff(int noteNumber) {
    }

    @Override
    public void noteOff(int noteNumber, int velocity) {
    }

    @Override
    public void noteOn(int noteNumber, int velocity) {
    }

    @Override
    public void programChange(int program) {
    }

    @Override
    public void programChange(int bank, int program) {
    }

    @Override
    public void resetAllControllers() {
    }

    @Override
    public void setChannelPressure(int pressure) {
    }

    @Override
    public void setMono(boolean on) {
    }

    @Override
    public void setMute(boolean mute) {
    }

    @Override
    public void setOmni(boolean on) {
    }

    @Override
    public void setPitchBend(int bend) {
    }

    @Override
    public void setPolyPressure(int noteNumber, int pressure) {
    }

    @Override
    public void setSolo(boolean soloState) {
    }
}
