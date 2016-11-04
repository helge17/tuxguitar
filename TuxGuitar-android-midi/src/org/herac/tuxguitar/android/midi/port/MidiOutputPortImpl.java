package org.herac.tuxguitar.android.midi.port;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.os.Handler;
import android.os.Looper;

import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.activity.TGActivityController;
import org.herac.tuxguitar.gm.port.GMOutputPort;
import org.herac.tuxguitar.gm.port.GMReceiver;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.util.TGContext;

@SuppressLint("NewApi")
public class MidiOutputPortImpl extends GMOutputPort{

	private TGContext context;
	private MidiDeviceInfo info;
	private MidiDeviceInfo.PortInfo portInfo;
	private MidiOutputPortConection connection;
	private MidiReceiverImpl receiver;
	
	public MidiOutputPortImpl(TGContext context, MidiDeviceInfo info, MidiDeviceInfo.PortInfo portInfo){
		this.context = context;
		this.info = info;
		this.portInfo = portInfo;
		this.connection = new MidiOutputPortConection();
		this.receiver = new MidiReceiverImpl(this.connection);
	}
	
	public void open() {
		if(!this.connection.isConnected()) {
			TGActivity activity = TGActivityController.getInstance(this.context).getActivity();

			MidiManager midiManager = (MidiManager) activity.getSystemService(Context.MIDI_SERVICE);
			midiManager.openDevice(this.info, new MidiManager.OnDeviceOpenedListener() {
				public void onDeviceOpened(MidiDevice device) {
					if (device != null) {
						MidiInputPort port = device.openInputPort(portInfo.getPortNumber());
						MidiOutputPortImpl.this.connection.connect(device, port);
					}
				}
			}, new Handler(Looper.getMainLooper()));
		}
	}

	public void close() throws MidiPlayerException {
		if( this.connection.isConnected() ) {
			this.connection.disconnect();
		}
	}
	
	public GMReceiver getReceiver(){
		this.open();

		return this.receiver;
	}
	
	public void check(){
		// Not implemented
	}

	public String getKey(){
		return this.portInfo.getName();
	}
	
	public String getName(){
		return this.portInfo.getName();
	}
}