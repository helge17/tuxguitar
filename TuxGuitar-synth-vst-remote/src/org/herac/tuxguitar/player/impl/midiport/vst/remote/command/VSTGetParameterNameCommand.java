package org.herac.tuxguitar.player.impl.midiport.vst.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.player.impl.midiport.vst.remote.VSTConnection;

public class VSTGetParameterNameCommand extends VSTAbstractCommand<String> {
	
	public static final Integer COMMAND_ID = 12;
	
	private Integer index;
	
	public VSTGetParameterNameCommand(VSTConnection connection, Integer index) {
		super(connection);
		
		this.index = index;
	}

	public String process() throws IOException {
		this.writeInteger(COMMAND_ID);
		this.writeInteger(this.index);
		
		return this.readString();
	}
}
