package org.herac.tuxguitar.player.impl.midiport.vst.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.player.impl.midiport.vst.remote.VSTConnection;
import org.herac.tuxguitar.player.impl.midiport.vst.remote.VSTSession;

public class VSTStartSessionCommand extends VSTAbstractCommand<VSTSession> {
	
	public VSTStartSessionCommand(VSTConnection connection) {
		super(connection);
	}

	public VSTSession process() throws IOException {
		return new VSTSession(this.readInteger(), this.getConnection());
	}
}
