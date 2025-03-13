package app.tuxguitar.player.impl.midiport.vst.command;

import java.io.IOException;

import app.tuxguitar.midi.synth.remote.TGAbstractCommand;
import app.tuxguitar.midi.synth.remote.TGConnection;

public class VSTIsEffectUIAvailableCommand extends TGAbstractCommand<Boolean> {

	public static final Integer COMMAND_ID = 21;

	public VSTIsEffectUIAvailableCommand(TGConnection connection) {
		super(connection);
	}

	public Boolean process() throws IOException {
		this.writeInteger(COMMAND_ID);

		return this.readBoolean();
	}
}
