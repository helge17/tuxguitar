package org.herac.tuxguitar.io.ptb;

import java.io.IOException;
import java.io.InputStream;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGInputStreamBase;
import org.herac.tuxguitar.io.ptb.base.PTBar;
import org.herac.tuxguitar.io.ptb.base.PTBeat;
import org.herac.tuxguitar.io.ptb.base.PTDirection;
import org.herac.tuxguitar.io.ptb.base.PTGuitarIn;
import org.herac.tuxguitar.io.ptb.base.PTNote;
import org.herac.tuxguitar.io.ptb.base.PTSection;
import org.herac.tuxguitar.io.ptb.base.PTSong;
import org.herac.tuxguitar.io.ptb.base.PTSymbol;
import org.herac.tuxguitar.io.ptb.base.PTTempo;
import org.herac.tuxguitar.io.ptb.base.PTTrack;
import org.herac.tuxguitar.io.ptb.base.PTTrackInfo;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;

public class PTInputStream implements TGInputStreamBase{
	
	private static final String PTB_VERSION = "ptab-4";
	
	private InputStream stream;
	private String version;
	private PTSong song;
	private PTSongParser parser;
	
	public PTInputStream(){
		super();
	}
	
	public void init(TGFactory factory,InputStream stream){
		this.version = null;
		this.stream = stream;
		this.parser = new PTSongParser(factory);
	}
	
	public TGFileFormat getFileFormat(){
		return new TGFileFormat("PowerTab","*.ptb");
	}
	
	public boolean isSupportedVersion(String version){
		return (version.equals(PTB_VERSION));
	}
	
	public boolean isSupportedVersion(){
		try{
			readVersion();
			return isSupportedVersion(this.version);
		}catch(Exception e){
			return false;
		}catch(Error e){
			return false;
		}
	}
	
	private void readVersion(){
		if(this.version == null){
			this.version = (readString(4) + "-" + readShort());
		}
	}
	
	public TGSong readSong() throws IOException{
		this.readVersion();
		if (!isSupportedVersion(this.version)) {
			throw new IOException("Unsupported Version");
		}
		this.song = new PTSong();
		this.readSongInfo();
		this.readDataInstruments(this.song.getTrack1());
		this.readDataInstruments(this.song.getTrack2());
		this.close();
		
		return this.parser.parseSong(this.song);
	}
	
	private void readSongInfo(){
		this.song.getInfo().setClassification(readByte());
		if(this.song.getInfo().getClassification() == 0) {
			skip(1);
			this.song.getInfo().setName(readString());
			this.song.getInfo().setInterpret(readString());
			this.song.getInfo().setReleaseType(readByte());
			if (this.song.getInfo().getReleaseType() == 0){
				this.song.getInfo().setAlbumType(readByte());
				this.song.getInfo().setAlbum(readString());
				this.song.getInfo().setYear(readShort());
				this.song.getInfo().setLiveRecording(readBoolean());
			}else if(this.song.getInfo().getReleaseType() == 1){
				this.song.getInfo().setAlbum(readString());
				this.song.getInfo().setLiveRecording(readBoolean());
			}else if(this.song.getInfo().getReleaseType() == 2){
				this.song.getInfo().setAlbum(readString());
				this.song.getInfo().setDay(readShort());
				this.song.getInfo().setMonth(readShort());
				this.song.getInfo().setYear(readShort());
			}
			if (readByte() == 0) {
				this.song.getInfo().setAuthor(readString());
				this.song.getInfo().setLyricist(readString());
			}
			this.song.getInfo().setArrenger(readString());
			this.song.getInfo().setGuitarTranscriber(readString());
			this.song.getInfo().setBassTranscriber(readString());
			this.song.getInfo().setCopyright(readString());
			this.song.getInfo().setLyrics(readString());
			this.song.getInfo().setGuitarInstructions(readString());
			this.song.getInfo().setBassInstructions(readString());
		}else if(this.song.getInfo().getClassification() == 1){
			this.song.getInfo().setName(readString());
			this.song.getInfo().setAlbum(readString());
			this.song.getInfo().setStyle(readShort());
			this.song.getInfo().setLevel(readByte());
			this.song.getInfo().setAuthor(readString());
			this.song.getInfo().setInstructions(readString());
			this.song.getInfo().setCopyright(readString());
		}
	}
	
	private void readDataInstruments(PTTrack track){
		// Guitar section
		int itemCount = readHeaderItems();
		for (int j = 0; j < itemCount; j++) {
			readTrackInfo(track);
			if (j < itemCount - 1){
				readShort();
			}
		}
		// ChordDiagram section
		itemCount = readHeaderItems();
		for (int j = 0; j < itemCount; j++) {
			readChord();
			if (j < itemCount - 1){
				readShort();
			}
		}
		// FloatingText section
		itemCount = readHeaderItems();
		for (int j = 0; j < itemCount; j++) {
			readFloattingText();
			if (j < itemCount - 1){
				readShort();
			}
		}
		// GuitarIn section
		itemCount = readHeaderItems();
		for (int j = 0; j < itemCount; j++) {
			readGuitarIn(track);
			if (j < itemCount - 1){
				readShort();
			}
		}
		// TempoMarker
		itemCount = readHeaderItems();
		for (int j = 0; j < itemCount; j++) {
			readTempoMarker(track);
			if (j < itemCount - 1){
				readShort();
			}
		}
		// Dynamic section
		itemCount = readHeaderItems();
		for (int j = 0; j < itemCount; j++) {
			readDynamic();
			
			if (j < itemCount - 1){
				readShort();
			}
		}
		// SectionSymbol section
		itemCount = readHeaderItems();
		for (int j = 0; j < itemCount; j++) {
			readSectionSymbol(track);
			if (j < itemCount - 1){
				readShort();
			}
		}
		// Section section
		itemCount = readHeaderItems();
		for (int j = 0; j < itemCount; j++) {
			readSection(track.getSection(j));
			if (j < itemCount - 1){
				readShort();
			}
		}
	}
	
	private void readTrackInfo(PTTrack track){
		PTTrackInfo info = new PTTrackInfo();
		info.setNumber(readByte());
		info.setName(readString());
		info.setInstrument((short)readByte());
		info.setVolume((short)readByte());
		info.setBalance((short)readByte());
		info.setReverb((short)readByte());
		info.setChorus((short)readByte());
		info.setTremolo((short)readByte());
		info.setPhaser((short)readByte());
		
		readByte();//capo
		
		// Tuning
		readString();//tunningName
		
		//bit 7 = Music notation offset sign, bits 6 to 1 = Music notation offset value, bit 0 = display sharps or flats;
		readByte();  //offset
		
		int[] strings = new int[ (readByte() & 0xff) ];
		for (int i = 0; i < strings.length; i++) {
			strings[i] = readByte();
		}
		info.setStrings(strings);
		
		track.getInfos().add(info);
	}
	
	private void readSection(PTSection section){
		readInt();//left
		readInt();//top
		readInt();//right
		readInt();//bottom
		
		int lastBarData = readByte();
		
		readByte();
		readByte();
		readByte();
		readByte();
		
		// BarLine
		readBarLine(section);
		
		// Direction section
		int itemCount = readHeaderItems();
		for (int j = 0; j < itemCount; j++) {
			readDirection(section);
			if (j < itemCount - 1){
				readShort();
			}
		}
		// ChordText section
		itemCount = readHeaderItems();
		for (int j = 0; j < itemCount; j++) {
			readChordText();
			if (j < itemCount - 1){
				readShort();
			}
		}
		// RhythmSlash section
		itemCount = readHeaderItems();
		for (int j = 0; j < itemCount; j++) {
			readRhythmSlash();
			if (j < itemCount - 1){
				readShort();
			}
		}
		// Staff
		section.setStaffs(readHeaderItems());
		for (int staff = 0; staff < section.getStaffs(); staff++) {
			readStaff(staff,section);
			if (staff < section.getStaffs() - 1){
				readShort();
			}
		}
		// MusicBar section
		itemCount = readHeaderItems();
		for (int j = 0; j < itemCount; j++) {
			readBarLine(section);
			if (j < itemCount - 1){
				readShort();
			}
		}
		PTBar bar = new PTBar();
		bar.setRepeatClose(((lastBarData >>> 5) == 4)?(lastBarData - 128):0);
		section.getPosition(section.getNextPositionNumber()).addComponent(bar);
	}
	
	private void readStaff(int staff,PTSection section){
		readByte();
		readByte();
		readByte();
		readByte();
		readByte();
		
		for( int voice = 0 ; voice < 2 ; voice ++ ){
			int itemCount = readHeaderItems();
			for (int j = 0; j < itemCount; j++) {
				readPosition(staff,voice,section);
				if (j < itemCount - 1){
					readShort();
				}
			}
		}
	}
	
	private void readPosition(int staff,int voice,PTSection section){
		PTBeat beat = new PTBeat(staff,voice);
		
		int position = readByte();
		int beaming = readByte();
		beaming = ((beaming - 128 < 0)?beaming:beaming - 128);
		
		readByte();
		
		int data1 = readByte();
		readByte();
		int data3 = readByte();
		int durationValue = readByte();
		
		int multiBarRest = 1;
		int complexCount = readByte();
		for (int i = 0; i < complexCount; i++) {
			int count = readShort();
			readByte();
			
			int type = readByte();
			if((type & 0x08) != 0){
				multiBarRest = count;
			}
		}
		
		int itemCount = readHeaderItems();
		for (int j = 0; j < itemCount; j++) {
			readNote(beat);
			if (j < itemCount - 1){
				readShort();
			}
		}
		beat.setMultiBarRest((itemCount == 0)?multiBarRest:1);
		beat.setVibrato(((data1 & 0x08) != 0) || ((data1 & 0x10) != 0));
		beat.setGrace((data3 & 0x01) != 0);
		
		// Set the duration
		beat.setDuration(durationValue);
		beat.setDotted((data1 & 0x01) != 0);
		beat.setDoubleDotted((data1 & 0x02) != 0);
		beat.setArpeggioUp((data1 & 0x20) != 0);
		beat.setArpeggioDown((data1 & 0x40) != 0);
		beat.setEnters(((beaming - (beaming % 8)) / 8) + 1);
		beat.setTimes((beaming % 8) + 1);
		
		section.getPosition(position).addComponent(beat);
	}
	
	private void readNote(PTBeat beat){
		PTNote note = new PTNote();
		int position = readByte();
		int simpleData = readShort();
		int symbolCount = readByte();
		for (int i = 0; i < symbolCount; i++) {
			readByte();
			readByte();
			int data3 = readByte();
			int data4 = readByte();
			note.setBend((data4 == 101)?((data3 / 16) + 1):0);
			note.setSlide((data4 == 100));
		}
		note.setValue(position & 0x1f);
		note.setString(((position & 0xe0) >> 5) + 1);
		note.setTied((simpleData & 0x01) != 0);
		note.setDead((simpleData & 0x02) != 0);
		beat.addNote(note);
	}
	
	private void readTimeSignature(PTBar bar){
		int data = readInt();
		readByte(); //measurePulses
		
		bar.setNumerator(((((data >> 24) - ((data >> 24) % 8)) / 8) + 1));
		bar.setDenominator((int)Math.pow(2,(data >> 24) % 8));
	}
	
	private void readKeySignature(){
		readByte();
	}
	
	private void readBarLine(PTSection section){
		PTBar bar = new PTBar();
		int position = readByte();
		int type = readByte();
		
		//repeat start
		bar.setRepeatStart(((type >>> 5) == 3));
		
		//repeat end
		bar.setRepeatClose((((type >>> 5) == 4)?(type - 128):0));
		
		readKeySignature();
		readTimeSignature(bar);
		readRehearsalSign();
		section.getPosition(position).addComponent(bar);
	}
	
	private void readChord(){
		readShort(); //chordKey
		readByte();
		readShort(); //chordModification
		readByte();
		readByte();
		int stringCount = readByte();
		for (int j = 0; j < stringCount; j++) {
			readByte(); //fret
		}
	}
	
	private void readFloattingText(){
		// Floating text
		readString();
		// Read mfc rect
		readInt();//left
		readInt();//top
		readInt();//right
		readInt();//bottom
		
		readByte();
		readFontSetting();
	}
	
	private void readFontSetting(){
		readString();//fontName
		readInt();//pointSize	
		readInt();//weight
		readBoolean();//italic
		readBoolean();//underline
		readBoolean();//strikeout
		readInt();//color
	}
	
	private void readGuitarIn(PTTrack track){
		int section = readShort();
		int staff = readByte();
		int position = readByte();
		skip(1);
		int info = (readByte() & 0xff);
		track.getSection(section).getPosition(position).addComponent(new PTGuitarIn(staff,info));
	}
	
	private void readTempoMarker(PTTrack track){
		int section = readShort();
		int position = readByte();
		int tempo = readShort();
		int data = readShort();
		readString();//description
		int tripletFeel = TGMeasureHeader.TRIPLET_FEEL_NONE;
		if((data & 0x01) != 0){
			tripletFeel = TGMeasureHeader.TRIPLET_FEEL_EIGHTH;
		}else if((data & 0x02) != 0){
			tripletFeel = TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH;
		}
		if(tempo > 0){
			track.getSection(section).getPosition(position).addComponent(new PTTempo(tempo,tripletFeel));
		}
	}
	
	private void readSectionSymbol(PTTrack track){
		int section = readShort();
		int position = readByte();
		int data = readInt();
		PTSymbol symbol = new PTSymbol();
		symbol.setEndNumber( (data >> 16) );
		track.getSection(section).getPosition(position).addComponent(symbol);
	}
	
	private void readDynamic(){
		readShort();
		readByte();
		readByte();
		readShort();
	}
	
	private void readRehearsalSign(){
		readByte();
		readString();
	}
	
	private void readDirection(PTSection section){
		int position = readByte();
		int symboleCount = readByte();
		for (int i = 0; i < symboleCount; i++) {
			int data = readShort();
			section.getPosition(position).addComponent(new PTDirection( ( data >> 8 ) , ((data & 0xc0) >> 6), (data & 0x1f) ) );
		}
	}
	
	private void readChordText(){
		readByte();
		readShort();
		readByte();
		readShort();
		readByte();
	}
	
	private void readRhythmSlash(){
		readByte();
		readByte();
		readInt();
	}
	
	private int readHeaderItems(){
		int nbItems = readShort();
		if (nbItems != 0){
			int header = readShort();
			if (header == 0xffff) {
				if (readShort() != 1) {
					return -1;
				}
				readString(readShort());
			}
		}
		return nbItems;
	}
	
	private String readString(){
		try {
			int length = (this.stream.read() & 0xff);
			return this.readString(((length < 0xff)?length:readShort()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String readString(int length){
		try {
			byte[] bytes = new byte[length];
			this.stream.read(bytes);
			return new String(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private int readInt(){
		try {
			byte[] b = new byte[4];
			this.stream.read(b);
			return ((b[3] & 0xff) << 24) | ((b[2] & 0xff) << 16) | ((b[1] & 0xff) << 8) | (b[0] & 0xff);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private int readShort(){
		try {
			byte[] b = {0, 0};
			this.stream.read(b);
			return ((b[1] & 0xff) << 8) | (b[0] & 0xff);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private boolean readBoolean(){
		try {
			return (this.stream.read() > 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private int readByte(){
		try {
			return this.stream.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private void skip(int bytes){
		try {
			this.stream.read(new byte[bytes]);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void close(){
		try {
			this.stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
