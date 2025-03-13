package app.tuxguitar.player.impl.midiport.vst.command;

import java.io.IOException;

import app.tuxguitar.midi.synth.remote.TGAbstractCommand;
import app.tuxguitar.midi.synth.remote.TGConnection;

public class VSTIsUpdatedCommand extends TGAbstractCommand<Boolean> {

	public static final Integer COMMAND_ID = 4;

	public VSTIsUpdatedCommand(TGConnection connection) {
		super(connection);
	}

	public Boolean process() throws IOException {
		this.writeInteger(COMMAND_ID);

		return this.readBoolean();
	}
}
