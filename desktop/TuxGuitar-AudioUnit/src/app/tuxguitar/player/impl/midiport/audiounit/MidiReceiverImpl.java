package app.tuxguitar.player.impl.midiport.audiounit;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.util.TGMessageDialogUtil;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.gm.port.GMReceiver;
import app.tuxguitar.player.base.MidiControllers;
import app.tuxguitar.player.base.MidiOutputPort;
import app.tuxguitar.player.impl.midiport.audiounit.utils.MidiConfigUtils;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGExpressionResolver;

public class MidiReceiverImpl extends MidiReceiverJNI implements GMReceiver {
	private boolean open; // unnecessary
	private boolean connected;
	private List<MidiOutputPort> ports;
	private TGContext context;

	public MidiReceiverImpl(TGContext context) {
		this.ports = new ArrayList<MidiOutputPort>();
		this.connected = false;
		this.context = context;
	}

	public void open(){
		super.open();
		this.open = true;
	}

	public void close(){
		if(this.isOpen()){
			this.disconnect();
			super.close();
			this.open = false;
		}
	}

	public boolean isOpen(){
		return (this.open);
	}

	public boolean isConnected(){
		return (this.isOpen() && this.connected);
	}

	public void connect() {
		if (isOpen()) {
			if (!isConnected()) {
				this.connected = true;
				this.openDevice();
				this.changeSoundbank();
			}
		}
	}

	public void disconnect() {
		if (isConnected()) {
			this.closeDevice();
			this.connected = false;
		}
	}

	private void changeSoundbank() {
		String soundBankPath = MidiConfigUtils.getSoundbankPath(context);
		String soundBank = TGExpressionResolver.getInstance(context).resolve(soundBankPath);
		if (soundBank != null && !soundBank.isEmpty()) {
			if (this.changeSoundBank(soundBank) != 0) {
				TGMessageDialogUtil.errorMessage(context, TGWindow.getInstance(context).getWindow(),
						TuxGuitar.getProperty("audiounit.settings.soundbank-error", new String[] { soundBankPath }));
			}
		}
	}

	public List<MidiOutputPort> listPorts(){
		if(isOpen()){
			this.ports.clear();
			this.ports.add(new MidiPortImpl(this, "AudioUnit graph midi playback" , "audiounit" ));
			return this.ports;
		}
		return new ArrayList<MidiOutputPort>();
	}

	public void sendSystemReset() {
		if(isOpen()){
			//not implemented
		}
	}

	public void sendAllNotesOff() {
		for(int i = 0; i < 16; i ++){
			sendControlChange(i,MidiControllers.ALL_NOTES_OFF,0);
		}
	}

	public void sendControlChange(int channel, int controller, int value) {
		if(isOpen()){
			super.controlChange(channel, controller, value);
		}
	}

	public void sendNoteOff(int channel, int key, int velocity) {
		if(isOpen()){
			super.noteOff(channel, key, velocity);
		}
	}

	public void sendNoteOn(int channel, int key, int velocity) {
		if(isOpen()){
			super.noteOn(channel, key, velocity);
		}
	}

	public void sendPitchBend(int channel, int value) {
		if(isOpen()){
			super.pitchBend(channel, value);
		}
	}

	public void sendProgramChange(int channel, int value) {
		if(isOpen()){
			super.programChange(channel, value);
		}
	}
}
