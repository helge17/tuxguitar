package org.herac.tuxguitar.player.impl.midiport.lv2.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.player.impl.midiport.lv2.remote.LV2Connection;

public class LV2ProcessGetControlPortValueCommand extends LV2AbstractCommand<Float> {
	
	public static final Integer COMMAND_ID = 1;
	
	private Integer index;
	
	public LV2ProcessGetControlPortValueCommand(LV2Connection connection, Integer index) {
		super(connection);
		
		this.index = index;
	}

	public Float process() throws IOException {
		this.writeInteger(COMMAND_ID);
		this.writeInteger(this.index);
		
		return this.readFloat();
	}
}
