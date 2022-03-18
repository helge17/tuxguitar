package org.herac.tuxguitar.player.impl.midiport.vst.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.player.impl.midiport.vst.remote.VSTConnection;

public class VSTGetVersionCommand extends VSTAbstractCommand<Integer> {
	
	public static final Integer COMMAND_ID = 24;
	
	public VSTGetVersionCommand(VSTConnection connection) {
		super(connection);
	}

	public Integer process() throws IOException {
		this.writeInteger(COMMAND_ID);
		
		return this.readInteger();
	}
}
