package org.herac.tuxguitar.android.midi.port;

import android.annotation.SuppressLint;
import android.media.midi.MidiDevice;
import android.media.midi.MidiInputPort;

import org.herac.tuxguitar.player.base.MidiPlayerException;

import java.io.IOException;

@SuppressLint("NewApi")
public class MidiOutputPortConection {

	private MidiDevice midiDevice;
	private MidiInputPort midiInputPort;

	public MidiOutputPortConection() {
		super();
	}

	public boolean isConnected() {
		return (this.midiDevice != null && this.midiInputPort != null);
	}

	public void connect(MidiDevice midiDevice, MidiInputPort midiInputPort) {
		this.midiDevice = midiDevice;
		this.midiInputPort = midiInputPort;
	}

	public void disconnect() throws MidiPlayerException {
		try {
			if (this.isConnected()) {
				this.midiInputPort.close();
				this.midiInputPort = null;

				this.midiDevice.close();
				this.midiDevice = null;
			}
		} catch(IOException e) {
			throw new MidiPlayerException(e);
		}
	}

	public MidiDevice getMidiDevice() {
		return this.midiDevice;
	}

	public MidiInputPort getMidiInputPort() {
		return this.midiInputPort;
	}
}
