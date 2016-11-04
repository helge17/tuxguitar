////////////////////////////////////////////////////////////////////////////////
//
//  MidiDriver - An Android Midi Driver.
//
//  Copyright (C) 2013	Bill Farmer
//
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Bill Farmer	 william j farmer [at] yahoo [dot] co [dot] uk.
//
///////////////////////////////////////////////////////////////////////////////

package org.billthefarmer.mididriver;

import java.util.ArrayList;
import java.util.List;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class MidiDriver implements Runnable {
	
	private static final int SAMPLE_RATE = 22050;
	private static final int BUFFER_SIZE = 64;
	
	private Object mutex;
	private Thread thread;
	private AudioTrack audioTrack;
	private List<byte[]> queuedEvents;
	
	public MidiDriver() {
		this.mutex = new Object();
		this.queuedEvents = new ArrayList<byte[]>();
	}

	// Start midi
	public void start() {
		this.thread = new Thread(this, "MidiDriver");
		this.thread.start();
	}
	
	// Stop
	public void stop() {
		Thread t = this.thread;
		this.thread = null;

		// Wait for the thread to exit
		while (t != null && t.isAlive()) {
			Thread.yield();
		}
	}

	// Runnable implementation
	public void run() {
		processMidi();
	}
	
	// Process MidiDriver
	private void processMidi() {
		int status = 0;
		int size = 0;

		// Init midi
		if ((size = this.init()) == 0) {
			return;
		}

		short[] buffer = new short[size];

		// Create audio track
		this.audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE, AudioTrack.MODE_STREAM);
		
		// Check audiotrack
		if( audioTrack == null ) {
			this.shutdown();
			return;
		}

		// Check state
		int state = this.audioTrack.getState();

		if (state != AudioTrack.STATE_INITIALIZED) {
			this.audioTrack.release();
			this.shutdown();
			return;
		}

		// Play track
		this.audioTrack.play();

		// Keep running until stopped
		while( this.thread != null ) {
			
			// Write the midi events
			synchronized (this.mutex) {
				for(byte[] queuedEvent : this.queuedEvents) {
					this.write(queuedEvent);
				}
				this.queuedEvents.clear();
			}
			
			// Render the audio
			if (this.render(buffer) == 0) {
				break;
			}
			// Write audio to audiotrack
			status = this.audioTrack.write(buffer, 0, buffer.length);

			if (status < 0) {
				break;
			}
		}

		// Render and write the last bit of audio
		if( status > 0 ) {
			if (this.render(buffer) > 0) {
				this.audioTrack.write(buffer, 0, buffer.length);
			}
		}
		// Shut down audio
		this.shutdown();
		this.audioTrack.release();
	}

	public void queueEvent(byte[] event) {
		synchronized (this.mutex) {
			this.queuedEvents.add(event);
		}
	}
	
	// Native midi methods
	private native int init();

	private native int[] config();

	private native int render(short a[]);

	private native boolean write(byte a[]);

	private native boolean shutdown();

	// Load midi library
	static {
		System.loadLibrary("midi");
	}
}
