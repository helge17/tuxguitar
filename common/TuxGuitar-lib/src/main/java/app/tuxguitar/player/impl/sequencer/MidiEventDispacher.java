package app.tuxguitar.player.impl.sequencer;

import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiReceiver;

public class MidiEventDispacher{
	private MidiSequencerImpl sequencer;

	public MidiEventDispacher(MidiSequencerImpl sequencer){
		this.sequencer = sequencer;
	}

	public void dispatch(MidiEvent event) throws MidiPlayerException{
		this.dispatch(event, this.sequencer.getTransmitter());
	}

	// a channel event goes to the receiver; a tempo event sets the sequencer's tempo
	public void dispatch(MidiEvent event, MidiReceiver receiver) throws MidiPlayerException{
		if(event.getType() == MidiEvent.MIDI_EVENT_NOTEON){
			receiver.sendNoteOn(event.getData()[0],event.getData()[1],event.getData()[2],event.getData()[3],(event.getData()[4] == 1));
		}
		else if(event.getType() == MidiEvent.MIDI_EVENT_NOTEOFF){
			receiver.sendNoteOff(event.getData()[0],event.getData()[1],event.getData()[2],event.getData()[3],(event.getData()[4] == 1));
		}
		else if(event.getType() == MidiEvent.MIDI_EVENT_PITCH_BEND){
			receiver.sendPitchBend(event.getData()[0],event.getData()[1],event.getSemitones(),event.getData()[2],(event.getData()[3] == 1));
		}
		else if(event.getType() == MidiEvent.MIDI_EVENT_CONTROL_CHANGE){
			receiver.sendControlChange(event.getData()[0],event.getData()[1],event.getData()[2]);
		}
		else if(event.getType() == MidiEvent.MIDI_EVENT_PROGRAM_CHANGE){
			receiver.sendProgramChange(event.getData()[0],event.getData()[1]);
		}
		else if(event.isTempo()){
			this.sequencer.setTempoInUSQ(event.getTempoInUSQ());
		}
	}
}
