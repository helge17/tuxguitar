package org.herac.tuxguitar.player.impl.midiport.lv2.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.player.impl.midiport.lv2.remote.LV2Connection;

public class LV2ProcessSetControlPortValueCommand extends LV2AbstractCommand<Void> {
	
	public static final Integer COMMAND_ID = 2;
	
	private Integer index;
	private Float value;
	
	public LV2ProcessSetControlPortValueCommand(LV2Connection connection, Integer index, Float value) {
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
