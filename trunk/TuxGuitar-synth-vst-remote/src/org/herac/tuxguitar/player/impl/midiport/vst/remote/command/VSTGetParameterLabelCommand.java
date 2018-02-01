package org.herac.tuxguitar.player.impl.midiport.vst.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.player.impl.midiport.vst.remote.VSTConnection;

public class VSTGetParameterLabelCommand extends VSTAbstractCommand<String> {
	
	public static final Integer COMMAND_ID = 13;
	
	private Integer index;
	
	public VSTGetParameterLabelCommand(VSTConnection connection, Integer index) {
		super(connection);
		
		this.index = index;
	}

	public String process() throws IOException {
		this.writeInteger(COMMAND_ID);
		this.writeInteger(this.index);
		
		return this.readString();
	}
}
