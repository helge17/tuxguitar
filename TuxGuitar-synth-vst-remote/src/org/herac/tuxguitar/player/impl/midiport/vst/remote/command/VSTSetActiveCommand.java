package org.herac.tuxguitar.player.impl.midiport.vst.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.player.impl.midiport.vst.remote.VSTConnection;

public class VSTSetActiveCommand extends VSTAbstractCommand<Void> {
	
	public static final Integer COMMAND_ID = 1;
	
	private Boolean value;
	
	public VSTSetActiveCommand(VSTConnection connection, Boolean value) {
		super(connection);
		
		this.value = value;
	}

	public Void process() throws IOException {
		this.writeInteger(COMMAND_ID);
		this.writeInteger(Boolean.TRUE.equals(this.value) ? 1 : 0);
		
		return null;
	}
}
