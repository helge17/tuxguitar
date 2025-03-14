package app.tuxguitar.player.impl.midiport.vst.command;

import java.io.IOException;

import app.tuxguitar.midi.synth.remote.TGAbstractCommand;
import app.tuxguitar.midi.synth.remote.TGConnection;

public class VSTGetNumInputsCommand extends TGAbstractCommand<Integer> {

	public static final Integer COMMAND_ID = 7;

	public VSTGetNumInputsCommand(TGConnection connection) {
		super(connection);
	}

	public Integer process() throws IOException {
		this.writeInteger(COMMAND_ID);

		return this.readInteger();
	}
}
