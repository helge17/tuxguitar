package org.herac.tuxguitar.player.impl.midiport.vst.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.midi.synth.remote.TGAbstractCommand;
import org.herac.tuxguitar.midi.synth.remote.TGConnection;

public class VSTCloseEffectUICommand extends TGAbstractCommand<Void> {
	
	public static final Integer COMMAND_ID = 15;
	
	public VSTCloseEffectUICommand(TGConnection connection) {
		super(connection);
	}

	public Void process() throws IOException {
		this.writeInteger(COMMAND_ID);
		
		return null;
	}
}
