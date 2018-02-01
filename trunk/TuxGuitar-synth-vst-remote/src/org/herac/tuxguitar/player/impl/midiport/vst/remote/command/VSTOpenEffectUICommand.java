package org.herac.tuxguitar.player.impl.midiport.vst.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.player.impl.midiport.vst.remote.VSTConnection;

public class VSTOpenEffectUICommand extends VSTAbstractCommand<Void> {
	
	public static final Integer COMMAND_ID = 16;
	
	public VSTOpenEffectUICommand(VSTConnection connection) {
		super(connection);
	}

	public Void process() throws IOException {
		this.writeInteger(COMMAND_ID);
		
		return null;
	}
}
