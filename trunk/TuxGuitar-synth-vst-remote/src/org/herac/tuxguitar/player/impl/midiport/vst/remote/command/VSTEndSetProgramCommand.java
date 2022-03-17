package org.herac.tuxguitar.player.impl.midiport.vst.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.player.impl.midiport.vst.remote.VSTConnection;

public class VSTEndSetProgramCommand extends VSTAbstractCommand<Void> {
	
	public static final Integer COMMAND_ID = 21;
	
	public VSTEndSetProgramCommand(VSTConnection connection) {
		super(connection);
	}

	public Void process() throws IOException {
		this.writeInteger(COMMAND_ID);
		
		return null;
	}
}
