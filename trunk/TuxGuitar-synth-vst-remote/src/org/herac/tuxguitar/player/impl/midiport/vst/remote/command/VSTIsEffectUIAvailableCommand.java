package org.herac.tuxguitar.player.impl.midiport.vst.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.player.impl.midiport.vst.remote.VSTConnection;

public class VSTIsEffectUIAvailableCommand extends VSTAbstractCommand<Boolean> {
	
	public static final Integer COMMAND_ID = 19;
	
	public VSTIsEffectUIAvailableCommand(VSTConnection connection) {
		super(connection);
	}

	public Boolean process() throws IOException {
		this.writeInteger(COMMAND_ID);
		
		return this.readBoolean();
	}
}
