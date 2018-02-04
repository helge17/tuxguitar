package org.herac.tuxguitar.player.impl.midiport.vst.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.player.impl.midiport.vst.remote.VSTConnection;

public class VSTGetNumOutputsCommand extends VSTAbstractCommand<Integer> {
	
	public static final Integer COMMAND_ID = 5;
	
	public VSTGetNumOutputsCommand(VSTConnection connection) {
		super(connection);
	}

	public Integer process() throws IOException {
		this.writeInteger(COMMAND_ID);
		
		return this.readInteger();
	}
}
