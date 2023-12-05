package org.herac.tuxguitar.player.impl.midiport.lv2.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.midi.synth.remote.TGAbstractCommand;
import org.herac.tuxguitar.midi.synth.remote.TGConnection;

public class LV2ProcessOpenUICommand extends TGAbstractCommand<Void> {
	
	public static final Integer COMMAND_ID = 7;
	
	public LV2ProcessOpenUICommand(TGConnection connection) {
		super(connection);
	}

	public Void process() throws IOException {
		this.writeInteger(COMMAND_ID);
		
		return null;
	}
}
