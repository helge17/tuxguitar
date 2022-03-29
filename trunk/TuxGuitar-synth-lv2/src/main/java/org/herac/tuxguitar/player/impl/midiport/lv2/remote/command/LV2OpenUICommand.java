package org.herac.tuxguitar.player.impl.midiport.lv2.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.player.impl.midiport.lv2.remote.LV2Connection;

public class LV2OpenUICommand extends LV2AbstractCommand<Void> {
	
	public static final Integer COMMAND_ID = 5;
	
	public LV2OpenUICommand(LV2Connection connection) {
		super(connection);
	}

	public Void process() throws IOException {
		this.writeInteger(COMMAND_ID);
		
		return null;
	}
}
