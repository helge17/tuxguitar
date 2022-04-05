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

import java.util.TreeMap;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

/**
 * Software synthesizer MIDI receiver class.
 *
 * @author Karl Helgason
 */
public class SoftReceiver implements MidiDeviceReceiver {

    protected boolean open = true;
    private Object control_mutex;
    private SoftSynthesizer synth;
    protected TreeMap<Long, Object> midimessages;
    protected SoftMainMixer mainmixer;

    public SoftReceiver(SoftSynthesizer synth) {
        this.control_mutex = synth.control_mutex;
        this.synth = synth;
        this.mainmixer = synth.getMainMixer();
        if (mainmixer != null)
            this.midimessages = mainmixer.midimessages;
    }

    public MidiDevice getMidiDevice() {
        return synth;
    }    

    public void send(MidiMessage message, long timeStamp) {

        synchronized (control_mutex) {
            if (!open)
                throw new IllegalStateException("Receiver is not open");
        }

        if (timeStamp != -1) {
            synchronized (control_mutex) {
                mainmixer.activity();
                while (midimessages.get(timeStamp) != null)
                    timeStamp++;
                if (message instanceof ShortMessage
                        && (((ShortMessage)message).getChannel() > 0xF)) {
                    midimessages.put(timeStamp, message.clone());
                } else {
                    midimessages.put(timeStamp, message.getMessage());
                }
            }
        } else {
            mainmixer.processMessage(message);
        }
    }

    public void close() {
        synchronized (control_mutex) {
            open = false;
        }
        synth.removeReceiver(this);
    }
}
