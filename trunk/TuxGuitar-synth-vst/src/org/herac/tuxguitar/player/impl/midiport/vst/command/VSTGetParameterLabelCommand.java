package org.herac.tuxguitar.player.impl.midiport.vst.command;

import java.io.IOException;

import org.herac.tuxguitar.midi.synth.remote.TGAbstractCommand;
import org.herac.tuxguitar.midi.synth.remote.TGConnection;

public class VSTGetParameterLabelCommand extends TGAbstractCommand<String> {
	
	public static final Integer COMMAND_ID = 11;
	
	private Integer index;
	
	public VSTGetParameterLabelCommand(TGConnection connection, Integer index) {
		super(connection);
		
		this.index = index;
	}

	public String process() throws IOException {
		this.writeInteger(COMMAND_ID);
		this.writeInteger(this.index);
		
		return this.readString();
	}
}
