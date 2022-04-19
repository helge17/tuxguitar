package org.herac.tuxguitar.player.impl.midiport.lv2.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.player.impl.midiport.lv2.remote.LV2Connection;

public class LV2ProcessGetStateCommand extends LV2AbstractCommand<String> {
	
	public static final Integer COMMAND_ID = 10;
	
	public LV2ProcessGetStateCommand(LV2Connection connection) {
		super(connection);
	}

	public String process() throws IOException {
		this.writeInteger(COMMAND_ID);
		
		return this.readString();
	}
}
