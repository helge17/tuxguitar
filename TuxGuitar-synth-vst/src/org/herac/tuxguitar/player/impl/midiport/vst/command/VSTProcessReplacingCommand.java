package org.herac.tuxguitar.player.impl.midiport.vst.command;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.herac.tuxguitar.midi.synth.remote.TGAbstractCommand;
import org.herac.tuxguitar.midi.synth.remote.TGConnection;

public class VSTProcessReplacingCommand extends TGAbstractCommand<Float[][]> {
	
	public static final Integer COMMAND_ID = 13;
	
	private float[][] inputs;
	private float[][] outputs;
	private Integer blocksize;
	
	public VSTProcessReplacingCommand(TGConnection connection, float[][] inputs, float[][] outputs, Integer blocksize) {
		super(connection);
		
		this.inputs = inputs;
		this.outputs = outputs;
		this.blocksize = blocksize;
	}

	public Float[][] process() throws IOException {
		this.writeInteger(COMMAND_ID);
		this.writeInteger(this.blocksize);
		
		for(int c = 0; c < this.inputs.length; c ++) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			for(int i = 0; i < this.inputs[c].length; i ++) {
		        int value = Float.floatToIntBits(this.inputs[c][i]);
				out.write((value >>> 0) & 0xFF);
		        out.write((value >>> 8) & 0xFF);
		        out.write((value >>> 16) & 0xFF);
		        out.write((value >>> 24) & 0xFF);
		        
			}
			out.flush();
			out.close();
			
			this.writeBytes(out.toByteArray());
		}

		for(int c = 0; c < this.outputs.length; c ++) {
			
			byte[] buffer = new byte[/*this.outputs.length * */ this.blocksize * 4];
			
			this.readBytes(buffer);
			
			ByteArrayInputStream in = new ByteArrayInputStream(buffer);
			for(int i = 0; i < this.outputs[c].length; i ++) {
				int value = (in.read() << 0) + (in.read() << 8) + (in.read() << 16) + (in.read() << 24);
		        
				this.outputs[c][i] = Float.intBitsToFloat(value);
			}
		}
		return null;
	}
}
