package org.herac.tuxguitar.player.impl.midiport.vst.command;

import java.io.IOException;

import org.herac.tuxguitar.midi.synth.remote.TGAbstractCommand;
import org.herac.tuxguitar.midi.synth.remote.TGConnection;

public class VSTSetActiveCommand extends TGAbstractCommand<Void> {
	
	public static final Integer COMMAND_ID = 1;
	
	private Boolean value;
	
	public VSTSetActiveCommand(TGConnection connection, Boolean value) {
		super(connection);
		
		this.value = value;
	}

	public Void process() throws IOException {
		this.writeInteger(COMMAND_ID);
		this.writeInteger(Boolean.TRUE.equals(this.value) ? 1 : 0);
		
		return null;
	}
}
