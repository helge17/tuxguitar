package org.herac.tuxguitar.player.impl.midiport.vst.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.player.impl.midiport.vst.remote.VSTConnection;

public class VSTSetParameterCommand extends VSTAbstractCommand<Void> {
	
	public static final Integer COMMAND_ID = 10;
	
	private Integer index;
	private Float value;
	
	public VSTSetParameterCommand(VSTConnection connection, Integer index, Float value) {
		super(connection);
		
		this.index = index;
		this.value = value;
	}

	public Void process() throws IOException {
		this.writeInteger(COMMAND_ID);
		this.writeInteger(this.index);
		this.writeFloat(this.value);
		
		return null;
	}
}
