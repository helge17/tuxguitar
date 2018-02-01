package org.herac.tuxguitar.player.impl.midiport.vst.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.player.impl.midiport.vst.remote.VSTConnection;

public class VSTGetParameterCommand extends VSTAbstractCommand<Float> {
	
	public static final Integer COMMAND_ID = 11;
	
	private Integer index;
	
	public VSTGetParameterCommand(VSTConnection connection, Integer index) {
		super(connection);
		
		this.index = index;
	}

	public Float process() throws IOException {
		this.writeInteger(COMMAND_ID);
		this.writeInteger(this.index);
		
		return this.readFloat();
	}
}
