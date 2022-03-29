package org.herac.tuxguitar.player.impl.midiport.lv2.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.player.impl.midiport.lv2.remote.LV2Connection;
import org.herac.tuxguitar.player.impl.midiport.lv2.remote.LV2Session;

public class LV2StartSessionCommand extends LV2AbstractCommand<LV2Session> {
	
	public LV2StartSessionCommand(LV2Connection connection) {
		super(connection);
	}

	public LV2Session process() throws IOException {
		return new LV2Session(this.readInteger(), this.getConnection());
	}
}
