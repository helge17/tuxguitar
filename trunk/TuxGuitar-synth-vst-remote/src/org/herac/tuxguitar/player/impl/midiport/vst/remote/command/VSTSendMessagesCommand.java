package org.herac.tuxguitar.player.impl.midiport.vst.remote.command;

import java.io.IOException;
import java.util.List;

import org.herac.tuxguitar.player.impl.midiport.vst.remote.VSTConnection;

public class VSTSendMessagesCommand extends VSTAbstractCommand<Void> {
	
	public static final Integer COMMAND_ID = 12;
	
	private List<byte[]> messages;
	
	public VSTSendMessagesCommand(VSTConnection connection, List<byte[]> messages) {
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
