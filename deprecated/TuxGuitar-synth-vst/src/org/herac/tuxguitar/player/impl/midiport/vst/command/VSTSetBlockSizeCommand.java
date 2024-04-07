package org.herac.tuxguitar.player.impl.midiport.vst.command;

import java.io.IOException;

import org.herac.tuxguitar.midi.synth.remote.TGAbstractCommand;
import org.herac.tuxguitar.midi.synth.remote.TGConnection;

public class VSTSetBlockSizeCommand extends TGAbstractCommand<Void> {
	
	public static final Integer COMMAND_ID = 9;
	
	private Integer value;
	
	public VSTSetBlockSizeCommand(TGConnection connection, Integer value) {
		super(connection);
		
		this.value = value;
	}

	public Void process() throws IOException {
		this.writeInteger(COMMAND_ID);
		this.writeInteger(this.value);
		
		return null;
	}
}
