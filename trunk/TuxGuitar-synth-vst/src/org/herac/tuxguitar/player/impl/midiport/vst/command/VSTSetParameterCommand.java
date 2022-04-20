package org.herac.tuxguitar.player.impl.midiport.vst.command;

import java.io.IOException;

import org.herac.tuxguitar.midi.synth.remote.TGAbstractCommand;
import org.herac.tuxguitar.midi.synth.remote.TGConnection;

public class VSTSetParameterCommand extends TGAbstractCommand<Void> {
	
	public static final Integer COMMAND_ID = 8;
	
	private Integer index;
	private Float value;
	
	public VSTSetParameterCommand(TGConnection connection, Integer index, Float value) {
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
