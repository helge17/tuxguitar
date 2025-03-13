package app.tuxguitar.player.impl.midiport.lv2.remote.command;

import java.io.IOException;

import app.tuxguitar.midi.synth.remote.TGAbstractCommand;
import app.tuxguitar.midi.synth.remote.TGConnection;

public class LV2ProcessGetStateCommand extends TGAbstractCommand<String> {

	public static final Integer COMMAND_ID = 1;

	public LV2ProcessGetStateCommand(TGConnection connection) {
		super(connection);
	}

	public String process() throws IOException {
		this.writeInteger(COMMAND_ID);

		return this.readString();
	}
}
