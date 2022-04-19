package org.herac.tuxguitar.player.impl.midiport.lv2.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.player.impl.midiport.lv2.remote.LV2Connection;

public class LV2ProcessSetStateCommand extends LV2AbstractCommand<Void> {
	
	public static final Integer COMMAND_ID = 20;
	
	private String state;
	
	public LV2ProcessSetStateCommand(LV2Connection connection, String state) {
		super(connection);
		
		this.state = state;
	}

	public Void process() throws IOException {
		this.writeInteger(COMMAND_ID);
		this.writeString(this.state);
		
		return null;
	}
}
