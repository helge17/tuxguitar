package org.herac.tuxguitar.midi.synth.remote;

import java.io.IOException;

public class TGStartSessionCommand extends TGAbstractCommand<TGSession> {
	
	public TGStartSessionCommand(TGConnection connection) {
		super(connection);
	}

	public TGSession process() throws IOException {
		return new TGSession(this.readInteger(), this.getConnection());
	}
}
