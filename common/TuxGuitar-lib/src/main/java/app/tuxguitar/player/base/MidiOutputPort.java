package app.tuxguitar.player.base;

public interface MidiOutputPort extends MidiDevice {

	public MidiSynthesizer getSynthesizer() throws MidiPlayerException;

}