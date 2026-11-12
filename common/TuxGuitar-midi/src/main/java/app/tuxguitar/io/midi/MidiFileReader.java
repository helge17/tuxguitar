package app.tuxguitar.io.midi;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import app.tuxguitar.io.midi.base.MidiEvent;
import app.tuxguitar.io.midi.base.MidiMessage;
import app.tuxguitar.io.midi.base.MidiSequence;
import app.tuxguitar.io.midi.base.MidiTrack;

public class MidiFileReader implements MidiFileHeader{

	public static boolean CANCEL_RUNNING_STATUS_ON_META_AND_SYSEX = true;

	private static final int STATUS_NONE = 0;
	private static final int STATUS_ONE_BYTE = 1;
	private static final int STATUS_TWO_BYTES = 2;
	private static final int STATUS_SYSEX = 3;
	private static final int STATUS_META = 4;

	public MidiSequence getSequence(InputStream stream)throws MidiFileException, IOException{
		DataInputStream	in = new DataInputStream(stream);
		int headerLength = readHeaderLength(in);
		int type = readType(in);
		int trackCount = readTrackCount(in, type);
		int resolution = readResolution(in);

		in.skip(headerLength - HEADER_LENGTH);

		MidiSequence sequence = new MidiSequence(MidiSequence.PPQ,resolution);
		for (int i = 0; i < trackCount; i++){
			MidiTrack track = new MidiTrack();
			sequence.addTrack(track);
			readTrack(in, track);
		}

		in.close();

		return sequence;
	}

	private static int readHeaderLength(DataInputStream in)throws MidiFileException, IOException{
		if (in.readInt() != HEADER_MAGIC){
			throw new MidiFileException("not a MIDI file: wrong header magic");
		}
		int headerLength = in.readInt();
		if (headerLength < HEADER_LENGTH){
			throw new MidiFileException("corrupt MIDI file: wrong header length");
		}
		return headerLength;
	}

	private static int readType(DataInputStream in)throws MidiFileException, IOException{
		int type = in.readShort();
		if (type < 0 || type > 2){
			throw new MidiFileException("corrupt MIDI file: illegal type");
		}
		if (type == 2){
			throw new MidiFileException("this implementation doesn't support type 2 MIDI files");
		}
		return type;
	}

	private static int readTrackCount(DataInputStream in, int type)throws MidiFileException, IOException{
		int trackCount = in.readShort();
		if (trackCount <= 0){
			throw new MidiFileException("corrupt MIDI file: number of tracks must be positive");
		}
		if (type == 0 && trackCount != 1){
			throw new MidiFileException("corrupt MIDI file: type 0 files must contain exactly one track");
		}
		return trackCount;
	}

	// only PPQ (ticks per quarter note) time division is read.
	// SMPTE time division (high bit set) was always rejected: its frame type was decoded as -(high byte & 0xFF),
	// which is never 24, 25, 29 or 30, so the SMPTE frame rate branches could not be reached.
	private static int readResolution(DataInputStream in)throws MidiFileException, IOException{
		int division = in.readUnsignedShort();
		if ((division & 0x8000) != 0){
			throw new MidiFileException("corrupt MIDI file: illegal frame division type");
		}
		return division & 0x7fff;
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

	// a byte below 0x80 is the first data byte of a message repeating the running status
	private static MidiEvent readEvent(DataInputStream in, MidiTrackReaderHelper helper)throws MidiFileException, IOException{
		int statusByte = readUnsignedByte(in, helper);
		if (statusByte < 0x80){
			return readRunningStatusEvent(in, helper, statusByte);
		}
		int type = getType(statusByte);
		if (type == STATUS_SYSEX){
			readSysexData(in, helper);
			return null;
		}
		if (type == STATUS_META){
			return readMetaEvent(in, helper);
		}
		if (type == STATUS_NONE){
			return null;
		}
		helper.runningStatusByte = statusByte;
		return readChannelEvent(in, helper, statusByte, readUnsignedByte(in, helper));
	}

	private static MidiEvent readRunningStatusEvent(DataInputStream in, MidiTrackReaderHelper helper, int data1)throws MidiFileException, IOException{
		if (helper.runningStatusByte == -1){
			throw new MidiFileException("corrupt MIDI file: status byte missing");
		}
		return readChannelEvent(in, helper, helper.runningStatusByte, data1);
	}

	// the status byte is a channel voice status: its command takes one or two data bytes
	private static MidiEvent readChannelEvent(DataInputStream in, MidiTrackReaderHelper helper, int statusByte, int data1)throws IOException{
		int command = (statusByte & 0xF0);
		int channel = (statusByte & 0x0F);
		if (getType(statusByte) == STATUS_ONE_BYTE){
			return new MidiEvent(MidiMessage.shortMessage(command, channel, data1), helper.ticks);
		}
		return new MidiEvent(MidiMessage.shortMessage(command, channel, data1, readUnsignedByte(in, helper)), helper.ticks);
	}

	// system exclusive data is skipped
	private static void readSysexData(DataInputStream in, MidiTrackReaderHelper helper)throws MidiFileException, IOException{
		cancelRunningStatus(helper);
		readData(in, helper);
	}

	private static MidiEvent readMetaEvent(DataInputStream in, MidiTrackReaderHelper helper)throws MidiFileException, IOException{
		cancelRunningStatus(helper);
		int typeByte = readUnsignedByte(in, helper);
		return new MidiEvent(MidiMessage.metaMessage(typeByte, readData(in, helper)), helper.ticks);
	}

	private static void cancelRunningStatus(MidiTrackReaderHelper helper){
		if (CANCEL_RUNNING_STATUS_ON_META_AND_SYSEX){
			helper.runningStatusByte = -1;
		}
	}

	// a variable-length quantity giving the data length, then the data
	private static byte[] readData(DataInputStream in, MidiTrackReaderHelper helper)throws MidiFileException, IOException{
		int dataLength = (int) readVariableLengthQuantity(in, helper);
		byte[] data = new byte[dataLength];
		for (int i = 0; i < dataLength; i++){
			data[i] = (byte) readUnsignedByte(in, helper);
		}
		return data;
	}

	// only called with a status byte (0x80 and above): below 0xf0 every command is a channel voice message
	private static int getType(int statusByte){
		if (statusByte >= 0xf0){
			return getSystemType(statusByte);
		}
		int command = statusByte & 0xf0;
		return (command == 0xc0 || command == 0xd0) ? STATUS_ONE_BYTE : STATUS_TWO_BYTES;
	}

	private static int getSystemType(int statusByte){
		if (statusByte == 0xf0 || statusByte == 0xf7){
			return STATUS_SYSEX;
		}
		return (statusByte == 0xff) ? STATUS_META : STATUS_NONE;
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
