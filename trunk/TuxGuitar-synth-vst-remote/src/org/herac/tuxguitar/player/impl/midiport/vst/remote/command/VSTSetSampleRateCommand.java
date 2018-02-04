package org.herac.tuxguitar.player.impl.midiport.vst.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.player.impl.midiport.vst.remote.VSTConnection;

public class VSTSetSampleRateCommand extends VSTAbstractCommand<Void> {
	
	public static final Integer COMMAND_ID = 7;
	
	private Float value;
	
	public VSTSetSampleRateCommand(VSTConnection connection, Float value) {
		super(connection);
		
		this.value = value;
	}

	public Void process() throws IOException {
		this.writeInteger(COMMAND_ID);
		this.writeFloat(this.value);
		
		return null;
	}
}
