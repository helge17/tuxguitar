package org.herac.tuxguitar.player.impl.midiport.vst.command;

import java.io.IOException;
import java.util.List;

import org.herac.tuxguitar.midi.synth.remote.TGAbstractCommand;
import org.herac.tuxguitar.midi.synth.remote.TGConnection;

public class VSTSendMessagesCommand extends TGAbstractCommand<Void> {
	
	public static final Integer COMMAND_ID = 19;
	
	private List<byte[]> messages;
	
	public VSTSendMessagesCommand(TGConnection connection, List<byte[]> messages) {
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
