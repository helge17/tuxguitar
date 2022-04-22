package org.herac.tuxguitar.player.impl.midiport.lv2.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.midi.synth.remote.TGAbstractCommand;
import org.herac.tuxguitar.midi.synth.remote.TGConnection;

public class LV2ProcessFocusUICommand extends TGAbstractCommand<Void> {
	
	public static final Integer COMMAND_ID = 9;
	
	public LV2ProcessFocusUICommand(TGConnection connection) {
		super(connection);
	}

	public Void process() throws IOException {
		this.writeInteger(COMMAND_ID);
		
		return null;
	}
}
