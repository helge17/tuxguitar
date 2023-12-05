package org.herac.tuxguitar.player.impl.midiport.lv2.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.midi.synth.remote.TGAbstractCommand;
import org.herac.tuxguitar.midi.synth.remote.TGConnection;

public class LV2ProcessGetControlPortValueCommand extends TGAbstractCommand<Float> {
	
	public static final Integer COMMAND_ID = 3;
	
	private Integer index;
	
	public LV2ProcessGetControlPortValueCommand(TGConnection connection, Integer index) {
		super(connection);
		
		this.index = index;
	}

	public Float process() throws IOException {
		this.writeInteger(COMMAND_ID);
		this.writeInteger(this.index);
		
		return this.readFloat();
	}
}
