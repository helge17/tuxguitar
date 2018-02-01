package org.herac.tuxguitar.player.impl.midiport.vst.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.player.impl.midiport.vst.remote.VSTConnection;

public class VSTSetBlockSizeCommand extends VSTAbstractCommand<Void> {
	
	public static final Integer COMMAND_ID = 8;
	
	private Integer value;
	
	public VSTSetBlockSizeCommand(VSTConnection connection, Integer value) {
		super(connection);
		
		this.value = value;
	}

	public Void process() throws IOException {
		this.writeInteger(COMMAND_ID);
		this.writeInteger(this.value);
		
		return null;
	}
}
