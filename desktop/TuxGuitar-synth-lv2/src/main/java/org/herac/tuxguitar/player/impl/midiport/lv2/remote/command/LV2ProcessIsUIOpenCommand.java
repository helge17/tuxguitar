package org.herac.tuxguitar.player.impl.midiport.lv2.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.midi.synth.remote.TGAbstractCommand;
import org.herac.tuxguitar.midi.synth.remote.TGConnection;

public class LV2ProcessIsUIOpenCommand extends TGAbstractCommand<Boolean> {
	
	public static final Integer COMMAND_ID = 10;
	
	public LV2ProcessIsUIOpenCommand(TGConnection connection) {
		super(connection);
	}

	public Boolean process() throws IOException {
		this.writeInteger(COMMAND_ID);
		
		return this.readBoolean();
	}
}
