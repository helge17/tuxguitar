package app.tuxguitar.player.impl.midiport.vst.command;

import java.io.IOException;

import app.tuxguitar.midi.synth.remote.TGAbstractCommand;
import app.tuxguitar.midi.synth.remote.TGConnection;

public class VSTGetNumParamsCommand extends TGAbstractCommand<Integer> {

	public static final Integer COMMAND_ID = 6;

	public VSTGetNumParamsCommand(TGConnection connection) {
		super(connection);
	}

	public Integer process() throws IOException {
		this.writeInteger(COMMAND_ID);

		return this.readInteger();
	}
}
