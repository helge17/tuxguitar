package app.tuxguitar.midi.synth.remote;

public interface TGClientStarter {

	String getWorkingDir();

	String[] createClientCommand(Integer sessionId, Integer serverPort);
}
