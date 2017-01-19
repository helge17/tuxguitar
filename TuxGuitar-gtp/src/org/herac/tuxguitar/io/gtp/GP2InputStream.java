package org.herac.tuxguitar.io.gtp;

import java.io.IOException;
import java.util.Iterator;

import org.herac.tuxguitar.gm.GMChannelRoute;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGChannelParameter;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGColor;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGNoteEffect;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTempo;
import org.herac.tuxguitar.song.models.TGText;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVelocities;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.song.models.effects.TGEffectBend;
import org.herac.tuxguitar.song.models.effects.TGEffectHarmonic;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class GP2InputStream extends GTPInputStream {
	
	public static final TGFileFormat FILE_FORMAT = new TGFileFormat("Guitar Pro 2", "audio/x-gtp", new String[]{"gtp"});
	
	public static final GTPFileFormatVersion[] SUPPORTED_VERSIONS = new GTPFileFormatVersion[] {
		new GTPFileFormatVersion(FILE_FORMAT, "FICHIER GUITAR PRO v2.20", 0),
		new GTPFileFormatVersion(FILE_FORMAT, "FICHIER GUITAR PRO v2.21", 1)
	};
	
	private static final int TRACK_COUNT = 8;
	
	private static final short TRACK_CHANNELS[][] = new short[][]{
		new short[]{1,0,1},
		new short[]{2,2,3},
		new short[]{3,4,5},
		new short[]{4,6,7},
		new short[]{5,8,10},
		new short[]{6,11,12},
		new short[]{7,13,14},
		new short[]{8,9,9},
	};
	
	public GP2InputStream(GTPSettings settings){
		super(settings, SUPPORTED_VERSIONS);
	}
	
	public TGFileFormat getFileFormat(){
		return FILE_FORMAT;
	}
	
	public TGSong readSong() throws TGFileFormatException {
		try{
			readVersion();
			
			TGSong song = getFactory().newSong();
			
			readInfo(song);
			
			int tempo = readInt();
			int tripletFeel = ((readInt() == 1)?TGMeasureHeader.TRIPLET_FEEL_EIGHTH:TGMeasureHeader.TRIPLET_FEEL_NONE);
			
			readInt(); //key
			
			for (int i = 0; i < TRACK_COUNT; i++) {
				TGChannel channel = getFactory().newChannel();
				TGChannelParameter gmChannel1Param = getFactory().newChannelParameter();
				TGChannelParameter gmChannel2Param = getFactory().newChannelParameter();
				
				gmChannel1Param.setKey(GMChannelRoute.PARAMETER_GM_CHANNEL_1);
				gmChannel1Param.setValue(Integer.toString(TRACK_CHANNELS[ i ][1]));
				gmChannel2Param.setKey(GMChannelRoute.PARAMETER_GM_CHANNEL_2);
				gmChannel2Param.setValue(Integer.toString(TRACK_CHANNELS[ i ][2]));
				
				channel.setChannelId(TRACK_CHANNELS[ i ][0]);
				channel.addParameter(gmChannel1Param);
				channel.addParameter(gmChannel2Param);
				song.addChannel(channel);
			}
			for (int i = 0; i < TRACK_COUNT; i++) {
				TGTrack track = getFactory().newTrack();
				track.setNumber( (i + 1) );
				track.setChannelId(TRACK_CHANNELS[ i ][0]);
				track.getColor().copyFrom(TGColor.RED);
				
				int strings = readInt();
				for (int j = 0; j < strings; j++) {
					TGString string = getFactory().newString();
					string.setNumber( j + 1 );
					string.setValue( readInt() );
					track.getStrings().add( string  );
				}
				song.addTrack(track);
			}
			
			int measureCount = readInt();
			
			for (int i = 0; i < TRACK_COUNT; i++) {
				readTrack(song.getTrack(i), song.getChannel(i));
			}
			
			skip(10);
			
			TGMeasureHeader previous = null;
			long[] lastReadedStarts = new long[TRACK_COUNT];
			for (int i = 0; i < measureCount; i++) {
				TGMeasureHeader header = getFactory().newHeader();
				header.setStart( (previous == null)?TGDuration.QUARTER_TIME:(previous.getStart() + previous.getLength()) );
				header.setNumber( (previous == null)?1:previous.getNumber() + 1 );
				header.getTempo().setValue( (previous == null)?tempo:previous.getTempo().getValue() );
				header.setTripletFeel(tripletFeel);
				readTrackMeasures(song,header,lastReadedStarts);
				previous = header;
			}
			
			TGSongManager tgSongManager = new TGSongManager(getFactory());
			tgSongManager.autoCompleteSilences(song);
			
			this.updateChannelNames(tgSongManager, song);
			this.close();
			
			return song;
		} catch (GTPFormatException gtpFormatException) {
			throw gtpFormatException;
		} catch (Throwable throwable) {
			throw new TGFileFormatException(throwable);
		}
	}
	
	private void readInfo(TGSong song) throws IOException{
		song.setName(readStringByteSizeOfByte());
		song.setAuthor(readStringByteSizeOfByte());
		readStringByteSizeOfByte();
	}
	
	private TGDuration readDuration() throws IOException {
		TGDuration duration = getFactory().newDuration();
		duration.setValue( (int) (Math.pow( 2 , (readByte() + 4) ) / 4 ) );
		return duration;
	}
	
	private void readTrackMeasures(TGSong song,TGMeasureHeader header,long[] lastReadedStarts) throws IOException {
		readTimeSignature(header.getTimeSignature());
		
		skip(6);
		
		int[] beats = new int[TRACK_COUNT];
		for (int i = 0; i < TRACK_COUNT; i++) {
			readUnsignedByte();
			readUnsignedByte();
			beats[i] = readUnsignedByte();
			if( beats[i] > 127 ){
				beats[i] = 0;
			}
			skip(9);
		}
		skip(2);
		
		int flags = readUnsignedByte();
		
		header.setRepeatOpen( ((flags & 0x01) != 0) );
		if ((flags & 0x02) != 0) {
			header.setRepeatClose( readUnsignedByte() );
		}
		
		if ((flags & 0x04) != 0) {
			header.setRepeatAlternative( parseRepeatAlternative(song, header.getNumber(), readUnsignedByte()) );
		}
		
		song.addMeasureHeader(header);
		for (int i = 0; i < TRACK_COUNT; i++) {
			TGTrack track = song.getTrack(i);
			TGMeasure measure = getFactory().newMeasure(header);
			
			long start = measure.getStart();
			for (int j = 0; j < beats[i]; j++) {
				long length = readBeat(track, measure,start,lastReadedStarts[i]);
				lastReadedStarts[i] = start;
				start += length;
			}
			measure.setClef( getClef(track) );
			track.addMeasure(measure);
		}
	}
	
	private void readTimeSignature(TGTimeSignature timeSignature) throws IOException {
		timeSignature.setNumerator(readUnsignedByte());
		timeSignature.getDenominator().setValue(readUnsignedByte());
	}
	
	private long readBeat(TGTrack track, TGMeasure measure, long start, long lastReadedStart) throws IOException {
		readInt();
		
		TGBeat beat = getFactory().newBeat();
		TGVoice voice = beat.getVoice(0);
		TGDuration duration = readDuration();
		TGNoteEffect effect = getFactory().newEffect();
		
		int flags1 = readUnsignedByte();
		int flags2 = readUnsignedByte();
		
		if ((flags2 & 0x02) != 0) {
			readMixChange(measure.getTempo());
		}
		
		if ((flags2 & 0x01) != 0) {
			readUnsignedByte(); //strokeType
			readUnsignedByte(); //strokeDuration
		}
		
		duration.setDotted( ((flags1 & 0x10) != 0) );
		if ((flags1 & 0x20) != 0) {
			duration.getDivision().setEnters(3);
			duration.getDivision().setTimes(2);
			skip(1);
		}
		
		// beat effects
		if ((flags1 & 0x04) != 0) {
			readBeatEffects(effect);
		}
		
		// chord diagram
		if ((flags1 & 0x02) != 0) {
			readChord(track.stringCount(), beat);
		}
		
		// text
		if ((flags1 & 0x01) != 0) {
			readText(beat);
		}
		
		if((flags1 & 0x40) != 0){
			if(lastReadedStart < start){
				TGBeat previousBeat = getBeat(track, measure, lastReadedStart);
				if(previousBeat != null){
					TGVoice previousVoice = previousBeat.getVoice(0);
					Iterator<TGNote> it = previousVoice.getNotes().iterator();
					while(it.hasNext()){
						TGNote previous = (TGNote)it.next();
						TGNote note = getFactory().newNote();
						note.setValue(previous.getValue());
						note.setString(previous.getString());
						note.setVelocity(previous.getVelocity());
						note.setTiedNote(true);
						
						voice.addNote(note);
					}
				}
			}
		}
		else if ((flags1 & 0x08) == 0) {
			int stringsFlags = readUnsignedByte();
			int effectsFlags = readUnsignedByte();
			int graceFlags = readUnsignedByte();
			
			for (int i = 5; i >= 0; i--) {
				if ((stringsFlags & (1 << i)) != 0) {
					TGNote note = getFactory().newNote();
					
					int fret = readUnsignedByte();
					int dynamic = readUnsignedByte();
					if ((effectsFlags & (1 << i)) != 0) {
						readNoteEffects(effect);
					}
					note.setValue( (fret >= 0 && fret < 100)?fret:0);
					note.setVelocity( (TGVelocities.MIN_VELOCITY + (TGVelocities.VELOCITY_INCREMENT * dynamic)) - TGVelocities.VELOCITY_INCREMENT );
					note.setString( track.stringCount() - i );
					note.setEffect(effect.clone(getFactory()));
					note.getEffect().setDeadNote(  (fret < 0 || fret >= 100)  );
					
					voice.addNote(note);
				}
				
				// Grace note
				if ((graceFlags & (1 << i)) != 0) {
					readGraceNote();
				}
			}
		}
		
		beat.setStart(start);
		voice.setEmpty(false);
		voice.getDuration().copyFrom(duration);
		measure.addBeat(beat);
		
		return duration.getTime();
	}
	
	private void readText(TGBeat beat) throws IOException{
		TGText text = getFactory().newText();
		text.setValue(readStringByte(0));
		beat.setText(text);
	}
	
	private void readBeatEffects(TGNoteEffect effect) throws IOException {
		int flags = readUnsignedByte();
		effect.setVibrato( (flags == 1 || flags == 2) );
		effect.setFadeIn( (flags == 4) );
		effect.setTapping( (flags == 5) ) ;
		effect.setSlapping( (flags == 6) ) ;
		effect.setPopping( (flags == 7) ) ;
		if(flags == 3){
			readBend(effect);
		}
		else if(flags == 8 || flags == 9){
			TGEffectHarmonic harmonic = getFactory().newEffectHarmonic();
			harmonic.setType((flags == 8)?TGEffectHarmonic.TYPE_NATURAL:TGEffectHarmonic.TYPE_ARTIFICIAL);
			harmonic.setData(0);
			effect.setHarmonic(harmonic);
		}
	}
	
	private void readNoteEffects(TGNoteEffect effect) throws IOException {
		int flags = readUnsignedByte();
		effect.setHammer( (flags == 1 || flags == 2) );
		effect.setSlide( (flags == 3 || flags == 4) );
		if(flags == 5 || flags == 6){
			readBend(effect);
		}
	}
	
	private void readBend(TGNoteEffect effect) throws IOException {
		skip(6);
		float value = Math.max(  ((readUnsignedByte() / 8f) - 26f) , 1f);
		TGEffectBend bend = getFactory().newEffectBend();
		bend.addPoint(0,0);
		bend.addPoint(Math.round(TGEffectBend.MAX_POSITION_LENGTH / 2), Math.round(value * TGEffectBend.SEMITONE_LENGTH) );
		bend.addPoint(Math.round(TGEffectBend.MAX_POSITION_LENGTH),Math.round(value * TGEffectBend.SEMITONE_LENGTH));
		effect.setBend(bend);
		skip(1);
	}
	
	private void readGraceNote() throws IOException {
		byte bytes[] = new byte[3];
		read(bytes);
	}
	
	private void readTrack(TGTrack track, TGChannel channel) throws IOException {
		channel.setProgram((short)readInt());
		readInt(); // Number of frets
		track.setName(readStringByteSizeOfByte());
		track.setSolo(readBoolean());
		channel.setVolume((short)readInt());
		channel.setBalance((short)readInt());
		channel.setChorus((short)readInt());
		channel.setReverb((short)readInt());
		track.setOffset(readInt());
	}
	
	private void readChord(int strings, TGBeat beat) throws IOException {
		TGChord chord = getFactory().newChord(strings);
		chord.setName(readStringByte(0));
		
		this.skip(1);
		if ( readInt() < 12 ) {
			skip(32);
		}
		
		chord.setFirstFret(readInt());
		if (chord.getFirstFret() != 0) {
			for (int i = 0; i < 6; i++) {
				int fret = readInt();
				if(i < chord.countStrings()){
					chord.addFretValue(i,fret);
				}
			}
		}
		if(chord.countNotes() > 0){
			beat.setChord(chord);
		}
	}
	
	private void readMixChange(TGTempo tempo) throws IOException {
		int flags = readUnsignedByte();
		// Tempo
		if ((flags & 0x20) != 0) {
			tempo.setValue(readInt());
			readUnsignedByte();
		}
		// Reverb
		if ((flags & 0x10) != 0) {
			readUnsignedByte();
			readUnsignedByte();
		}
		// Chorus
		if ((flags & 0x08) != 0) {
			readUnsignedByte();
			readUnsignedByte();
		}
		// Balance
		if ((flags & 0x04) != 0) {
			readUnsignedByte();
			readUnsignedByte();
		}
		// Volume
		if ((flags & 0x02) != 0) {
			readUnsignedByte();
			readUnsignedByte();
		}
		// Instrument
		if ((flags & 0x01) != 0) {
			readUnsignedByte();
		}
	}
	
	private int parseRepeatAlternative(TGSong song,int measure,int value){
		int repeatAlternative = 0;
		int existentAlternatives = 0;
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = (TGMeasureHeader)it.next();
			if(header.getNumber() == measure){
				break;
			}
			if(header.isRepeatOpen()){
				existentAlternatives = 0;
			}
			existentAlternatives |= header.getRepeatAlternative();
		}
		
		for(int i = 0; i < 8; i ++){
			if(value > i && (existentAlternatives & (1 << i)) == 0){
				repeatAlternative |= (1 << i);
			}
		}
		return repeatAlternative;
	}
	
	private int getClef( TGTrack track ){
		if(!isPercussionChannel(track.getSong(),track.getChannelId())){
			Iterator<TGString> it = track.getStrings().iterator();
			while( it.hasNext() ){
				TGString string = (TGString) it.next();
				if( string.getValue() <= 34 ){
					return TGMeasure.CLEF_BASS;
				}
			}
		}
		return TGMeasure.CLEF_TREBLE;
	}
	
	private TGBeat getBeat(TGTrack track, TGMeasure measure,long start){
		TGBeat beat = getBeat(measure,start);
		if(beat == null){
			for(int i = (track.countMeasures() - 1);i >=0; i-- ){
				beat = getBeat(track.getMeasure(i),start);
				if(beat != null){
					break;
				}
			}
		}
		return beat;
	}
	
	private TGBeat getBeat(TGMeasure measure,long start){
		if(start >= measure.getStart() && start < (measure.getStart() + measure.getLength())){
			Iterator<TGBeat> beats = measure.getBeats().iterator();
			while(beats.hasNext()){
				TGBeat beat = (TGBeat)beats.next();
				if(beat.getStart() == start){
					return beat;
				}
			}
		}
		return null;
	}
	
	private void updateChannelNames(TGSongManager songManager, TGSong song) {
		Iterator<TGChannel> it = song.getChannels();
		while( it.hasNext() ) {
			TGChannel tgChannel = it.next();
			tgChannel.setName(songManager.createChannelNameFromProgram(song, tgChannel));
		}
	}
	
	private boolean isPercussionChannel( TGSong song, int channelId ){
		Iterator<TGChannel> it = song.getChannels();
		while( it.hasNext() ){
			TGChannel channel = (TGChannel)it.next();
			if( channel.getChannelId() == channelId ){
				return channel.isPercussionChannel();
			}
		}
		return false;
	}
}