package org.herac.tuxguitar.player.impl.midiport.lv2.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.player.impl.midiport.lv2.remote.LV2Connection;

public class LV2ProcessIsUIAvailableCommand extends LV2AbstractCommand<Boolean> {
	
	public static final Integer COMMAND_ID = 8;
	
	public LV2ProcessIsUIAvailableCommand(LV2Connection connection) {
		super(connection);
	}

	public Boolean process() throws IOException {
		this.writeInteger(COMMAND_ID);
		
		return this.readBoolean();
	}
}
