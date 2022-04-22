package org.herac.tuxguitar.player.impl.midiport.vst.command;

import java.io.IOException;

import org.herac.tuxguitar.midi.synth.remote.TGAbstractCommand;
import org.herac.tuxguitar.midi.synth.remote.TGConnection;

public class VSTSetChunkCommand extends TGAbstractCommand<Void> {
	
	public static final Integer COMMAND_ID = 16;
	
	private byte[] chunk;
	
	public VSTSetChunkCommand(TGConnection connection, byte[] chunk) {
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
