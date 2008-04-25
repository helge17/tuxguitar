package org.herac.tuxguitar.io.midi;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.herac.tuxguitar.io.midi.base.MidiEvent;
import org.herac.tuxguitar.io.midi.base.MidiMessage;
import org.herac.tuxguitar.io.midi.base.MidiSequence;
import org.herac.tuxguitar.io.midi.base.MidiTrack;

public class MidiFileReader implements MidiFileHeader{
	
	public static boolean CANCEL_RUNNING_STATUS_ON_META_AND_SYSEX = true;
	
	private static final int STATUS_NONE = 0;
	private static final int STATUS_ONE_BYTE = 1;
	private static final int STATUS_TWO_BYTES = 2;
	private static final int STATUS_SYSEX = 3;
	private static final int STATUS_META = 4;
	
	public MidiSequence getSequence(InputStream stream)throws MidiFileException, IOException{
		DataInputStream	in = new DataInputStream(stream);
		if (in.readInt() != HEADER_MAGIC){
			throw new MidiFileException("not a MIDI file: wrong header magic");
		}
		int headerLength = in.readInt();
		if (headerLength < HEADER_LENGTH){
			throw new MidiFileException("corrupt MIDI file: wrong header length");
		}
		int type = in.readShort();
		if (type < 0 || type > 2){
			throw new MidiFileException("corrupt MIDI file: illegal type");
		}
		if (type == 2){
			throw new MidiFileException("this implementation doesn't support type 2 MIDI files");
		}
		int trackCount = in.readShort();
		if (trackCount <= 0){
			throw new MidiFileException("corrupt MIDI file: number of tracks must be positive");
		}
		if (type == 0 && trackCount != 1){
			throw new MidiFileException("corrupt MIDI file:  type 0 files must contain exactely one track");
		}
		float divisionType = -1.0F;
		int resolution = -1;
		int division = in.readUnsignedShort();
		if ((division & 0x8000) != 0){
			int frameType = -((division >>> 8) & 0xFF);
			if(frameType == 24){
				divisionType = MidiSequence.SMPTE_24;
			}else if(frameType == 25){
				divisionType = MidiSequence.SMPTE_25;
			}else if(frameType == 29){
				divisionType = MidiSequence.SMPTE_30DROP;
			}else if(frameType == 30){
				divisionType = MidiSequence.SMPTE_30;
			}else{
				throw new MidiFileException("corrupt MIDI file: illegal frame division type");
			}
			resolution = division & 0xff;
		}else{
			divisionType = MidiSequence.PPQ;
			resolution = division & 0x7fff;
		}
		
		in.skip(headerLength - HEADER_LENGTH);
		
		MidiSequence sequence = new MidiSequence(divisionType,resolution);
		for (int i = 0; i < trackCount; i++){
			MidiTrack track = new MidiTrack();
			sequence.addTrack(track);
			readTrack(in, track);
		}
		
		in.close();
		
		return sequence;
	}
	
	private void readTrack(DataInputStream in, MidiTrack track)throws MidiFileException, IOException{
		while (true){
			if (in.readInt() == TRACK_MAGIC){
				break;
			}
			int chunkLength = in.readInt();
			if (chunkLength % 2 != 0){
				chunkLength++;
			}
			in.skip(chunkLength);
		}
		
		MidiTrackReaderHelper helper = new MidiTrackReaderHelper(0,in.readInt(),-1);
		while (helper.remainingBytes > 0){
			helper.ticks += readVariableLengthQuantity(in, helper);
			MidiEvent event = readEvent(in, helper);
			if(event != null){
				track.add(event);
			}
		}
	}
	
	private static MidiEvent readEvent(DataInputStream in, MidiTrackReaderHelper helper)throws MidiFileException, IOException{
		int statusByte = readUnsignedByte(in, helper);
		int savedByte = 0;
		boolean runningStatusApplies = false;
		
		if (statusByte < 0x80){
			if (helper.runningStatusByte != -1){
				runningStatusApplies = true;
				savedByte = statusByte;
				statusByte = helper.runningStatusByte;
			}else{
				throw new MidiFileException("corrupt MIDI file: status byte missing");
			}
		}
		
		int type = getType(statusByte);
		if(type == STATUS_ONE_BYTE){
			int data = 0;
			if (runningStatusApplies){
				data = savedByte;
			}else{
				data = readUnsignedByte(in, helper);
				helper.runningStatusByte = statusByte;
			}
			
			return new MidiEvent(MidiMessage.shortMessage((statusByte & 0xF0),(statusByte & 0x0F) , data), helper.ticks);
		}else if(type == STATUS_TWO_BYTES){
			int	data1 = 0;
			if (runningStatusApplies){
				data1 = savedByte;
			}else{
				data1 = readUnsignedByte(in, helper);
				helper.runningStatusByte = statusByte;
			}
			
			return new MidiEvent(MidiMessage.shortMessage((statusByte & 0xF0),(statusByte & 0x0F) , data1, readUnsignedByte(in, helper)), helper.ticks);
		}else if(type == STATUS_SYSEX){
			if (CANCEL_RUNNING_STATUS_ON_META_AND_SYSEX){
				helper.runningStatusByte = -1;
			}
			int dataLength = (int) readVariableLengthQuantity(in, helper);
			byte[] data = new byte[dataLength];
			for (int i = 0; i < dataLength; i++){
				data[i] = (byte) readUnsignedByte(in, helper);
			}
		}else if(type == STATUS_META){
			if (CANCEL_RUNNING_STATUS_ON_META_AND_SYSEX){
				helper.runningStatusByte = -1;
			}
			int typeByte = readUnsignedByte(in, helper);
			int dataLength = (int) readVariableLengthQuantity(in, helper);
			byte[] data = new byte[dataLength];
			for (int i = 0; i < dataLength; i++){
				data[i] = (byte) readUnsignedByte(in, helper);
			}
			
			return new MidiEvent(MidiMessage.metaMessage(typeByte, data), helper.ticks);
		}
		
		return null;
	}
	
	private static int getType(int statusByte){
		if (statusByte < 0xf0) {
			int command = statusByte & 0xf0;
			if(command == 0x80 || command == 0x90 || command == 0xa0 || command == 0xb0 || command == 0xe0){
				return STATUS_TWO_BYTES;
			}
			else if(command == 0xc0 || command == 0xd0){
				return STATUS_ONE_BYTE;
			}
			return STATUS_NONE;
		}
		else if (statusByte == 0xf0 || statusByte == 0xf7){
			return STATUS_SYSEX;
		}
		else if (statusByte == 0xff){
			return STATUS_META;
		}
		else{
			return STATUS_NONE;
		}
	}
	
	public static long readVariableLengthQuantity(DataInputStream in, MidiTrackReaderHelper helper)throws MidiFileException, IOException{
		int	count = 0;
		long value = 0;
		while (count < 4){
			int	data = readUnsignedByte(in, helper);
			count++;
			value <<= 7;
			value |= (data & 0x7f);
			if (data < 128){
				return value;
			}
		}
		throw new MidiFileException("not a MIDI file: unterminated variable-length quantity");
	}
	
	public static int readUnsignedByte(DataInputStream dataInputStream, MidiTrackReaderHelper helper)throws IOException{
		helper.remainingBytes--;
		return dataInputStream.readUnsignedByte();
	}
	
	private class MidiTrackReaderHelper{
		protected long ticks = 0;
		protected long remainingBytes;
		protected int runningStatusByte;
		
		protected MidiTrackReaderHelper(long ticks,long remainingBytes,int runningStatusByte){
			this.ticks = ticks;
			this.remainingBytes = remainingBytes;
			this.runningStatusByte = runningStatusByte;
		}
	}
}