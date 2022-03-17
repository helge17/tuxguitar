package org.herac.tuxguitar.player.impl.midiport.vst.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.player.impl.midiport.vst.remote.VSTConnection;

public class VSTSetChunkCommand extends VSTAbstractCommand<Void> {
	
	public static final Integer COMMAND_ID = 19;
	
	private byte[] chunk;
	
	public VSTSetChunkCommand(VSTConnection connection, byte[] chunk) {
		super(connection);
		
		this.chunk = chunk;
	}

	public Void process() throws IOException {
		this.writeInteger(COMMAND_ID);
		this.writeInteger(this.chunk.length);
		this.writeBytes(this.chunk);
		
		return null;
	}
}
