package org.herac.tuxguitar.player.impl.midiport.vst.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.player.impl.midiport.vst.remote.VSTConnection;

public class VSTGetChunkCommand extends VSTAbstractCommand<byte[]> {
	
	public static final Integer COMMAND_ID = 18;
	
	public VSTGetChunkCommand(VSTConnection connection) {
		super(connection);
	}

	public byte[] process() throws IOException {
		this.writeInteger(COMMAND_ID);
		
		int length = this.readInteger();
		
		return (length > 0 ? this.readBytes(length) : null);
	}
}
