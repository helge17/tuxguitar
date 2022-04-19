package org.herac.tuxguitar.player.impl.midiport.vst.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.midi.synth.remote.TGAbstractCommand;
import org.herac.tuxguitar.midi.synth.remote.TGConnection;

public class VSTGetNumParamsCommand extends TGAbstractCommand<Integer> {
	
	public static final Integer COMMAND_ID = 3;
	
	public VSTGetNumParamsCommand(TGConnection connection) {
		super(connection);
	}

	public Integer process() throws IOException {
		this.writeInteger(COMMAND_ID);
		
		return this.readInteger();
	}
}
