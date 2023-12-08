package org.herac.tuxguitar.player.impl.midiport.lv2.remote.command;

import java.io.IOException;

import org.herac.tuxguitar.midi.synth.remote.TGAbstractCommand;
import org.herac.tuxguitar.midi.synth.remote.TGConnection;

public class LV2ProcessSetStateCommand extends TGAbstractCommand<Void> {
	
	public static final Integer COMMAND_ID = 2;
	
	private String state;
	
	public LV2ProcessSetStateCommand(TGConnection connection, String state) {
		super(connection);
		
		this.state = state;
	}

	public Void process() throws IOException {
		this.writeInteger(COMMAND_ID);
		this.writeString(this.state);
		
		return null;
	}
}
