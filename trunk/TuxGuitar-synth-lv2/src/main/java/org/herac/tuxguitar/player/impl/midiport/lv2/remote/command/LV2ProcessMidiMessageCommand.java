package org.herac.tuxguitar.player.impl.midiport.lv2.remote.command;

import java.io.IOException;
import java.util.List;

import org.herac.tuxguitar.player.impl.midiport.lv2.remote.LV2Connection;

public class LV2ProcessMidiMessageCommand extends LV2AbstractCommand<Void> {
	
	public static final Integer COMMAND_ID = 3;
	
	private List<byte[]> messages;
	
	public LV2ProcessMidiMessageCommand(LV2Connection connection, List<byte[]> messages) {
		super(connection);
		
		this.messages = messages;
	}

	public Void process() throws IOException {
		this.writeInteger(COMMAND_ID);
		this.writeInteger(this.messages.size());
		for(byte[] bytes : this.messages) {
			this.writeBytes(bytes);
		}
		return null;
	}
}
