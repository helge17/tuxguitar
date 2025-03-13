package org.herac.tuxguitar.android.midi.port;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.activity.TGActivityController;
import org.herac.tuxguitar.gm.port.GMOutputPort;
import org.herac.tuxguitar.gm.port.GMReceiver;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.util.TGContext;

@SuppressLint("NewApi")
public class MidiOutputPortImpl extends GMOutputPort {

	private final static long CONNECTION_TIMEOUT = 15000;

	private TGContext context;
	private MidiDeviceInfo info;
	private MidiDeviceInfo.PortInfo portInfo;
	private MidiOutputPortConection connection;
	private MidiReceiverImpl receiver;
	private String key;
	private boolean connecting;

	public MidiOutputPortImpl(TGContext context, MidiDeviceInfo info, MidiDeviceInfo.PortInfo portInfo){
		this.context = context;
		this.info = info;
		this.portInfo = portInfo;
		this.connection = new MidiOutputPortConection();
		this.receiver = new MidiReceiverImpl(this.connection);
	}

	public String getKey(){
		if( this.key == null ) {
			Bundle properties = this.info.getProperties();
			String deviceName = properties.getString(MidiDeviceInfo.PROPERTY_PRODUCT);
			if( deviceName == null ) {
				deviceName = properties.getString(MidiDeviceInfo.PROPERTY_NAME);
			}

			String portName = this.portInfo.getName();
			if( portName == null ) {
				portName = ("#" + this.portInfo.getPortNumber());
			}

			StringBuilder sb = new StringBuilder();
			if( deviceName != null ) {
				sb.append(deviceName);
			}

			if( sb.length() > 0 ) {
				sb.append(": ");
			}
			sb.append(portName);

			this.key = sb.toString();
		}
		return this.key;
	}

	public String getName(){
		return this.getKey();
	}

	public GMReceiver getReceiver() throws MidiPlayerException {
		this.open();
		this.tryWaitForConnection();

		return this.receiver;
	}

	public void check() throws MidiPlayerException {
		this.tryWaitForConnection();
	}

	public void open() throws MidiPlayerException {
		if(!this.connection.isConnected() && !this.connecting) {
			this.updateConnectingStatus(true);
			this.openInNewThread();
			this.tryWaitForConnection();
		}
	}

	public void close() throws MidiPlayerException {
		if( this.connection.isConnected() ) {
			this.connection.disconnect();
		}
	}

	private void tryWaitForConnection() throws MidiPlayerException {
		// never lock the ui-thread
		if( Looper.myLooper() != Looper.getMainLooper() ) {
			long time = System.currentTimeMillis();
			while( this.connecting ) {
				Thread.yield();

				if( System.currentTimeMillis() > (time + CONNECTION_TIMEOUT)) {
					updateConnectingStatus(false);

					throw new MidiPlayerException("Connection timeout");
				}
			}
		}
	}

	private void updateConnectingStatus(boolean connecting) {
		this.connecting = connecting;
	}

	private void openInNewThread() {
		new Thread(new Runnable() {
			public void run() {
				try {
					openInCurrentThread();
				} catch(Throwable throwable) {
					updateConnectingStatus(false);
				}
			}
		}).start();
	}

	private void openInCurrentThread() {
		TGActivity activity = TGActivityController.getInstance(this.context).getActivity();

		MidiManager midiManager = (MidiManager) activity.getSystemService(Context.MIDI_SERVICE);
		midiManager.openDevice(this.info, new MidiManager.OnDeviceOpenedListener() {
			public void onDeviceOpened(MidiDevice device) {
				try {
					if (device != null) {
						openInputPort(device);
					}
				} finally {
					updateConnectingStatus(false);
				}
			}
		}, new Handler(Looper.getMainLooper()));
	}

	private void openInputPort(MidiDevice device) {
		MidiInputPort port = device.openInputPort(this.portInfo.getPortNumber());
		if( port != null ) {
			this.connection.connect(device, port);
		}
	}
}