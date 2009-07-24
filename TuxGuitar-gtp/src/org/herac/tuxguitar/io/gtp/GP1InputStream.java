package org.herac.tuxguitar.io.gtp;

import java.io.IOException;
import java.util.Iterator;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGColor;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGNoteEffect;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
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
public class GP1InputStream extends GTPInputStream {
	
	private static final String SUPPORTED_VERSIONS[] = new String[]{
		"FICHIER GUITARE PRO v1", "FICHIER GUITARE PRO v1.01","FICHIER GUITARE PRO v1.02", "FICHIER GUITARE PRO v1.03","FICHIER GUITARE PRO v1.04"
	};
	
	private static final short TRACK_CHANNELS[][] = new short[][]{
		new short[]{0,1},
		new short[]{2,3},
		new short[]{4,5},
		new short[]{6,7},
		new short[]{8,10},
		new short[]{11,12},
		new short[]{13,14},
		new short[]{9,9},
	};
	
	private int trackCount;
	
	public GP1InputStream(GTPSettings settings){
		super(settings, SUPPORTED_VERSIONS);
	}
	
	public TGFileFormat getFileFormat(){
		return new TGFileFormat("Guitar Pro","*.gtp");
	}
	
	public TGSong readSong() throws GTPFormatException, IOException {
		readVersion();
		if (!isSupportedVersion(getVersion())) {
			this.close();
			throw new GTPFormatException("Unsupported Version");
		}
		this.trackCount = ((getVersionIndex() > 2)?8:1);
		
		TGSong song = getFactory().newSong();
		
		readInfo(song);
		
		int tempo = readInt();
		int tripletFeel = ((readInt() == 1)?TGMeasureHeader.TRIPLET_FEEL_EIGHTH:TGMeasureHeader.TRIPLET_FEEL_NONE);
		
		if(getVersionIndex() > 2){
			readInt(); //key
		}
		
		for (int i = 0; i < this.trackCount; i++) {
			TGTrack track = getFactory().newTrack();
			track.setNumber( (i + 1) );
			track.getChannel().setChannel(TRACK_CHANNELS[ i ][0]);
			track.getChannel().setEffectChannel(TRACK_CHANNELS[ i ][1]);
			TGColor.RED.copy(track.getColor());
			
			int strings = ((getVersionIndex() > 1)?readInt():6);
			for (int j = 0; j < strings; j++) {
				TGString string = getFactory().newString();
				string.setNumber( j + 1 );
				string.setValue( readInt() );
				track.getStrings().add( string  );
			}
			song.addTrack(track);
		}
		
		int measureCount = readInt();
		
		for (int i = 0; i < this.trackCount; i++) {
			readTrack(song.getTrack(i));
		}
		
		if(getVersionIndex() > 2){
			skip(10);
		}
		
		TGMeasureHeader previous = null;
		long[] lastReadedStarts = new long[this.trackCount];
		for (int i = 0; i < measureCount; i++) {
			TGMeasureHeader header = getFactory().newHeader();
			header.setStart( (previous == null)?TGDuration.QUARTER_TIME:(previous.getStart() + previous.getLength()) );
			header.setNumber( (previous == null)?1:previous.getNumber() + 1 );
			header.getTempo().setValue( (previous == null)?tempo:previous.getTempo().getValue() );
			header.setTripletFeel(tripletFeel);
			readTrackMeasures(song,header,lastReadedStarts);
			previous = header;
		}
		
		TGSongManager manager = new TGSongManager(getFactory());
		manager.setSong(song);
		manager.autoCompleteSilences();
		
		this.close();
		
		return song;
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
		
		int[] beats = new int[this.trackCount];
		for (int i = 0; i < this.trackCount; i++) {
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
		for (int i = 0; i < this.trackCount; i++) {
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
		
		int flags = readUnsignedByte();
		
		duration.setDotted( ((flags & 0x10) != 0) );
		if ((flags & 0x20) != 0) {
			duration.getDivision().setEnters(3);
			duration.getDivision().setTimes(2);
			skip(1);
		}
		
		// beat effects
		if ((flags & 0x04) != 0) {
			readBeatEffects(effect);
		}
		
		// chord diagram
		if ((flags & 0x02) != 0) {
			readChord(track.stringCount(), beat);
		}
		
		// text
		if ((flags & 0x01) != 0) {
			readText(beat);
		}
		
		if((flags & 0x40) != 0){
			if(lastReadedStart < start){
				TGBeat previousBeat = getBeat(track, measure, lastReadedStart);
				if(previousBeat != null){
					TGVoice previousVoice = previousBeat.getVoice(0);
					Iterator it = previousVoice.getNotes().iterator();
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
		else if ((flags & 0x08) == 0) {
			int stringsFlags = readUnsignedByte();
			int effectsFlags = readUnsignedByte();
			
			for (int i = 5; i >= 0; i--) {
				if ((stringsFlags & (1 << i)) != 0) {
					TGNote note = getFactory().newNote();
					
					int fret = readUnsignedByte();
					if ((effectsFlags & (1 << i)) != 0) {
						readNoteEffects(effect);
					}
					note.setValue( (fret >= 0 && fret < 100)?fret:0);
					note.setVelocity(TGVelocities.DEFAULT); 
					note.setString( track.stringCount() - i );
					note.setEffect(effect.clone(getFactory()));
					note.getEffect().setDeadNote(  (fret < 0 || fret >= 100)  );
					
					voice.addNote(note);
				}
			}
		}
		
		beat.setStart(start);
		voice.setEmpty(false);
		duration.copy(voice.getDuration());
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
		bend.addPoint(Math.round(TGEffectBend.MAX_POSITION_LENGTH), Math.round(value * TGEffectBend.SEMITONE_LENGTH));
		effect.setBend(bend);
		skip(1);
	}
	
	private void readTrack(TGTrack track) throws IOException {
		track.setName("Track 1");
		track.getChannel().setInstrument((short)readInt());
		if (getVersionIndex() > 2) {
			readInt(); // Number of frets
			track.setName(readStringByteSizeOfByte());
			track.setSolo(readBoolean());
			track.getChannel().setVolume((short)readInt());
			track.getChannel().setBalance((short)readInt());
			track.getChannel().setChorus((short)readInt());
			track.getChannel().setReverb((short)readInt());
			track.setOffset(readInt());
		}
	}
	
	private void readChord(int strings, TGBeat beat) throws IOException {
		if(getVersionIndex() > 3){
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
		}else{
			readStringByteSizeOfByte();
		}
	}
	
	private int parseRepeatAlternative(TGSong song,int measure,int value){
		int repeatAlternative = 0;
		int existentAlternatives = 0;
		Iterator it = song.getMeasureHeaders();
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
		if( !track.isPercussionTrack() ){
			Iterator it = track.getStrings().iterator();
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
			Iterator beats = measure.getBeats().iterator();
			while(beats.hasNext()){
				TGBeat beat = (TGBeat)beats.next();
				if(beat.getStart() == start){
					return beat;
				}
			}
		}
		return null;
	}
}