package org.herac.tuxguitar.io.midi;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.herac.tuxguitar.io.midi.base.MidiEvent;
import org.herac.tuxguitar.io.midi.base.MidiMessage;
import org.herac.tuxguitar.io.midi.base.MidiSequence;
import org.herac.tuxguitar.io.midi.base.MidiTrack;

public class MidiFileWriter implements MidiFileHeader{
	
	public void write(MidiSequence sequence,int type,OutputStream stream)throws IOException{
		DataOutputStream out = new DataOutputStream(stream);
		out.writeInt(HEADER_MAGIC);
		out.writeInt(HEADER_LENGTH);
		out.writeShort(type);
		out.writeShort(sequence.countTracks());
		out.writeShort(  (sequence.getDivisionType() == MidiSequence.PPQ)?(sequence.getResolution() & 0x7fff):0   );
		for (int i = 0; i < sequence.countTracks(); i++){
			writeTrack(sequence.getTrack(i),out);
		}
		out.flush();
		out.close();
	}
	
	private static int writeTrack(MidiTrack track,DataOutputStream out)throws IOException{
		int length = 0;
		if (out != null){
			out.writeInt(TRACK_MAGIC);
		}
		if (out != null){
			out.writeInt( writeTrack(track, null) );
		}
		MidiEvent previous = null;
		for (int i = 0; i < track.size(); i++){
			MidiEvent event = track.get(i);
			length += writeEvent(event,previous,out);
			previous = event;
		}
		return length;
	}
	
	private static int writeEvent(MidiEvent event,MidiEvent previous,OutputStream out)throws IOException{
		int length = writeVariableLengthQuantity(((previous != null)?(event.getTick() - previous.getTick()):0), out);
		MidiMessage	message = event.getMessage();
		if(message.getType() == MidiMessage.TYPE_SHORT){
			length += writeShortMessage(message,out);
		}
		else if(message.getType() == MidiMessage.TYPE_META){
			length += writeMetaMessage(message,out);
		}
		
		return length;
	}
	
	private static int writeShortMessage(MidiMessage message,OutputStream out)throws IOException{
		byte[] data = message.getData();
		int	length = data.length;
		if (out != null){
			out.write(message.getData(),0,length);
		}
		return length;
	}
	
	private static int writeMetaMessage(MidiMessage message,OutputStream out)throws IOException{
		int	length = 0;
		byte[] data = message.getData();
		if (out != null){
			out.write(0xFF);
			out.write(message.getCommand());
		}
		length += 2;
		length += writeVariableLengthQuantity(data.length,out);
		if (out != null){
			out.write(data);
		}
		length += data.length;
		return length;
	}
	
	private static int writeVariableLengthQuantity(long value, OutputStream out)throws IOException{
		boolean started = false;
		int length = 0;
		int data = (int) ((value >> 21) & 0x7f);
		if (data != 0){
			if (out != null){
				out.write(data | 0x80);
			}
			length++;
			started = true;
		}
		data = (int) ((value >> 14) & 0x7f);
		if (data != 0 || started){
			if (out != null){
				out.write(data | 0x80);
			}
			length++;
			started = true;
		}
		data = (int) ((value >> 7) & 0x7f);
		if (data != 0 || started){
			if (out != null){
				out.write(data | 0x80);
			}
			length++;
		}
		data = (int) (value & 0x7f);
		if (out != null){
			out.write(data);
		}
		length++;
		return length;
	}
}
