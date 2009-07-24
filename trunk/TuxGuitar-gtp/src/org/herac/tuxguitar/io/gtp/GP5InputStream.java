package org.herac.tuxguitar.io.gtp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGColor;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGLyric;
import org.herac.tuxguitar.song.models.TGMarker;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGNoteEffect;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGStroke;
import org.herac.tuxguitar.song.models.TGTempo;
import org.herac.tuxguitar.song.models.TGText;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVelocities;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.song.models.effects.TGEffectBend;
import org.herac.tuxguitar.song.models.effects.TGEffectGrace;
import org.herac.tuxguitar.song.models.effects.TGEffectHarmonic;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloBar;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloPicking;
import org.herac.tuxguitar.song.models.effects.TGEffectTrill;

public class GP5InputStream extends GTPInputStream {
	private static final String supportedVersions[] = { "FICHIER GUITAR PRO v5.00","FICHIER GUITAR PRO v5.10"};
	private static final float GP_BEND_SEMITONE = 25f;
	private static final float GP_BEND_POSITION = 60f;
	
	public GP5InputStream(GTPSettings settings) {
		super(settings, supportedVersions);
	}
	
	public TGFileFormat getFileFormat(){
		return new TGFileFormat("Guitar Pro 5","*.gp5");
	}
	
	public TGSong readSong() throws IOException, GTPFormatException {
		readVersion();
		if (!isSupportedVersion(getVersion())) {
			this.close();
			throw new GTPFormatException("Unsupported Version");
		}
		TGSong song = getFactory().newSong();
		
		readInfo(song);
		
		//lyrics
		int lyricTrack = readInt();
		TGLyric lyric = readLyrics();
		
		readPageSetup();
		
		int tempoValue = readInt();
		
		if(getVersionIndex() > 0){
			skip(1);
		}
		
		readInt(); //key
		
		readByte(); //octave
		
		List channels = readChannels();
		
		skip(42);
		
		int measures = readInt();
		int tracks = readInt();
		
		readMeasureHeaders(song, measures);
		readTracks(song, tracks, channels, lyric, lyricTrack);
		readMeasures(song, measures, tracks, tempoValue);
		
		this.close();
		
		return song;
	}
	
	private void readInfo(TGSong song) throws IOException{
		song.setName(readStringByteSizeOfInteger());
		readStringByteSizeOfInteger();
		song.setArtist(readStringByteSizeOfInteger());
		song.setAlbum(readStringByteSizeOfInteger());
		song.setAuthor(readStringByteSizeOfInteger());
		readStringByteSizeOfInteger();
		song.setCopyright(readStringByteSizeOfInteger());
		song.setWriter(readStringByteSizeOfInteger());
		readStringByteSizeOfInteger();
		int comments = readInt();
		for (int i = 0; i < comments; i++) {
			song.setComments( song.getComments() + readStringByteSizeOfInteger() );
		}
	}
	
	private void readPageSetup() throws IOException{
		skip( (getVersionIndex() > 0 ?49 : 30 ) );
		for (int i = 0; i < 11; i++) {
			skip(4);
			readStringByte(0);
		}
	}
	
	private void readMeasureHeaders(TGSong song, int count) throws IOException{
		TGTimeSignature timeSignature = getFactory().newTimeSignature();
		for (int i = 0; i < count; i++) {
			if(i > 0 ){
				skip(1);
			}
			song.addMeasureHeader(readMeasureHeader(i,timeSignature));
		}
	}
	
	private void readTracks(TGSong song, int count, List channels,TGLyric lyric, int lyricTrack) throws IOException{
		for (int number = 1; number <= count; number++) {
			song.addTrack(readTrack(number, channels,(number == lyricTrack)?lyric:getFactory().newLyric()));
		}
		skip( (getVersionIndex() == 0 ? 2 : 1) );
	}
	
	private void readMeasures(TGSong song, int measures, int tracks, int tempoValue) throws IOException{
		TGTempo tempo = getFactory().newTempo();
		tempo.setValue(tempoValue);
		long start = TGDuration.QUARTER_TIME;
		for (int i = 0; i < measures; i++) {
			TGMeasureHeader header = song.getMeasureHeader(i);
			header.setStart(start);
			for (int j = 0; j < tracks; j++) {
				TGTrack track = song.getTrack(j);
				TGMeasure measure = getFactory().newMeasure(header);
				track.addMeasure(measure);
				readMeasure(measure, track, tempo);
				skip(1);
			}
			tempo.copy(header.getTempo());
			start += header.getLength();
		}
	}
	
	private TGLyric readLyrics() throws IOException{
		TGLyric lyric = getFactory().newLyric();
		lyric.setFrom(readInt());
		lyric.setLyrics(readStringInteger());
		for (int i = 0; i < 4; i++) {
			readInt();
			readStringInteger();
		}
		return lyric;
	}
	
	private long readBeat(long start, TGMeasure measure, TGTrack track, TGTempo tempo, int voiceIndex) throws IOException{
		int flags = readUnsignedByte();
		
		TGBeat beat = getBeat(measure, start);
		TGVoice voice = beat.getVoice(voiceIndex);
		if((flags & 0x40) != 0){
			int beatType = readUnsignedByte();
			voice.setEmpty( ( beatType & 0x02 ) == 0 );
		}
		TGDuration duration = readDuration(flags);
		TGNoteEffect effect = getFactory().newEffect();
		if ((flags & 0x02) != 0) {
			readChord(track.stringCount(), beat);
		}
		if ((flags & 0x04) != 0) {
			readText(beat);
		}
		if ((flags & 0x08) != 0) {
			readBeatEffects(beat, effect);
		}
		if ((flags & 0x10) != 0) {
			readMixChange(tempo);
		}
		int stringFlags = readUnsignedByte();
		for (int i = 6; i >= 0; i--) {
			if ((stringFlags & (1 << i)) != 0 && (6 - i) < track.stringCount()) {
				TGString string = track.getString( (6 - i) + 1 ).clone(getFactory());
				TGNote note = readNote(string,track,effect.clone(getFactory()));
				voice.addNote(note);
			}
			duration.copy(voice.getDuration());
		}
		
		skip(1);
		
		int read = readByte();
		//if (read == 8 || read == 10 || read == 24 ) {
		if( (read & 0x08) != 0 ){
			skip(1);
		}
		
		return (!voice.isEmpty() ? duration.getTime() : 0 );
	}
	
	private List readChannels() throws IOException{
		List channels = new ArrayList();
		for (int i = 0; i < 64; i++) {
			TGChannel channel = getFactory().newChannel();
			channel.setChannel((short)i);
			channel.setEffectChannel((short)i);
			channel.setInstrument((short)readInt());
			channel.setVolume(toChannelShort(readByte()));
			channel.setBalance(toChannelShort(readByte()));
			channel.setChorus(toChannelShort(readByte()));
			channel.setReverb(toChannelShort(readByte()));
			channel.setPhaser(toChannelShort(readByte()));
			channel.setTremolo(toChannelShort(readByte()));
			channels.add(channel);
			skip(2);
		}
		return channels;
	}
	
	private void readText(TGBeat beat) throws IOException{
		TGText text = getFactory().newText();
		text.setValue(readStringByteSizeOfInteger());
		beat.setText(text);
	}
	
	private TGDuration readDuration(int flags) throws IOException {
		TGDuration duration = getFactory().newDuration();
		duration.setValue( (int) (Math.pow( 2 , (readByte() + 4) ) / 4 ) );
		duration.setDotted(((flags & 0x01) != 0));
		if ((flags & 0x20) != 0) {
			int divisionType = readInt();
			switch (divisionType) {
			case 3:
				duration.getDivision().setEnters(3);
				duration.getDivision().setTimes(2);
				break;
			case 5:
				duration.getDivision().setEnters(5);
				duration.getDivision().setTimes(4);
				break;
			case 6:
				duration.getDivision().setEnters(6);
				duration.getDivision().setTimes(4);
				break;
			case 7:
				duration.getDivision().setEnters(7);
				duration.getDivision().setTimes(4);
				break;
			case 9:
				duration.getDivision().setEnters(9);
				duration.getDivision().setTimes(8);
				break;
			case 10:
				duration.getDivision().setEnters(10);
				duration.getDivision().setTimes(8);
				break;
			case 11:
				duration.getDivision().setEnters(11);
				duration.getDivision().setTimes(8);
				break;
			case 12:
				duration.getDivision().setEnters(12);
				duration.getDivision().setTimes(8);
				break;
			}
		}
		return duration;
	}
	
	private int getTiedNoteValue(int string, TGTrack track) {
		int measureCount = track.countMeasures();
		if (measureCount > 0) {
			for (int m = measureCount - 1; m >= 0; m--) {
				TGMeasure measure = track.getMeasure( m );
				for (int b = measure.countBeats() - 1; b >= 0; b--) {
					TGBeat beat = measure.getBeat( b );
					for (int v = 0; v < beat.countVoices(); v ++) {
						TGVoice voice = beat.getVoice(v);
						if(!voice.isEmpty()){
							for (int n = 0; n < voice.countNotes(); n ++) {
								TGNote note = voice.getNote( n );
								if (note.getString() == string) {
									return note.getValue();
								}
							}
						}
					}
				}
			}
		}
		return -1;
	}
	
	private void readColor(TGColor color) throws IOException {
		color.setR(readUnsignedByte());
		color.setG(readUnsignedByte());
		color.setB(readUnsignedByte());
		skip(1);
	}
	
	private TGMarker readMarker(int measure) throws IOException {
		TGMarker marker = getFactory().newMarker();
		marker.setMeasure(measure);
		marker.setTitle(readStringByteSizeOfInteger());
		readColor(marker.getColor());
		return marker;
	}
	
	private TGMeasureHeader readMeasureHeader(int index,TGTimeSignature timeSignature) throws IOException {
		int flags = readUnsignedByte();
		TGMeasureHeader header = getFactory().newHeader();
		header.setNumber( (index + 1) );
		header.setStart(0);
		header.getTempo().setValue(120);
		header.setRepeatOpen( ((flags & 0x04) != 0) );
		if ((flags & 0x01) != 0) {
			timeSignature.setNumerator(readByte());
		}
		if ((flags & 0x02) != 0) {
			timeSignature.getDenominator().setValue(readByte());
		}
		timeSignature.copy(header.getTimeSignature());
		if ((flags & 0x08) != 0) {
			header.setRepeatClose( ( (readByte() & 0xff) - 1) );
		}
		if ((flags & 0x20) != 0) {
			header.setMarker(readMarker(header.getNumber()));
		}
		if ((flags & 0x10) != 0) {
			header.setRepeatAlternative(readUnsignedByte());
		}
		if ((flags & 0x40) != 0) {
			readByte();
			readByte();
		}
		if ((flags & 0x01) != 0) {
			skip(4);
		}
		if ((flags & 0x10) == 0) {
			skip(1);
		}
		int tripletFeel = readByte();
		if(tripletFeel == 1){
			header.setTripletFeel(TGMeasureHeader.TRIPLET_FEEL_EIGHTH);
		}else if(tripletFeel == 2){
			header.setTripletFeel(TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH);
		}else{
			header.setTripletFeel(TGMeasureHeader.TRIPLET_FEEL_NONE);
		}
		return header;
	}
	
	private void readMeasure(TGMeasure measure, TGTrack track, TGTempo tempo) throws IOException {
		for( int voice = 0 ; voice < 2 ; voice ++ ){
			long start = measure.getStart();
			int beats = readInt();
			for (int i = 0; i < beats; i++) {
				start += readBeat(start, measure, track, tempo, voice);
			}
		}
		
		List emptyBeats = new ArrayList();
		for( int i = 0 ; i < measure.countBeats() ; i ++ ){
			TGBeat beat = measure.getBeat( i );
			boolean empty = true;
			for( int v = 0 ; v < beat.countVoices() ; v ++ ){
				if( !beat.getVoice( v ).isEmpty() ){
					empty = false;
				}
			}
			if( empty ){
				emptyBeats.add( beat );
			}
		}
		Iterator it = emptyBeats.iterator();
		while( it.hasNext() ){
			TGBeat beat = (TGBeat)it.next();
			measure.removeBeat( beat );
		}
		measure.setClef( getClef(track) );
	}
	
	private TGNote readNote(TGString string,TGTrack track,TGNoteEffect effect)throws IOException {
		int flags = readUnsignedByte();
		TGNote note = getFactory().newNote();
		note.setString(string.getNumber());
		note.setEffect(effect);
		note.getEffect().setAccentuatedNote(((flags & 0x40) != 0));
		note.getEffect().setHeavyAccentuatedNote(((flags & 0x02) != 0));
		note.getEffect().setGhostNote(((flags & 0x04) != 0));
		if ((flags & 0x20) != 0) {
			int noteType = readUnsignedByte();
			note.setTiedNote( (noteType == 0x02) );
			note.getEffect().setDeadNote((noteType == 0x03));
		}
		if ((flags & 0x10) != 0) {
			note.setVelocity((TGVelocities.MIN_VELOCITY + (TGVelocities.VELOCITY_INCREMENT * readByte())) - TGVelocities.VELOCITY_INCREMENT);
		}
		if ((flags & 0x20) != 0) {
			int fret = readByte();
			int value = ( note.isTiedNote() ? getTiedNoteValue(string.getNumber(), track) : fret );
			note.setValue( value >= 0 && value < 100 ? value : 0 );
		}
		if ((flags & 0x80) != 0) {
			skip(2);
		}
		if ((flags & 0x01) != 0) {
			skip(8);
		}
		skip(1);
		if ((flags & 0x08) != 0) {
			readNoteEffects(note.getEffect());
		}
		return note;
	}
	
	private TGTrack readTrack(int number, List channels,TGLyric lyrics) throws IOException {
		readUnsignedByte();
		if(number ==  1 || getVersionIndex() == 0){
			skip(1);
		}
		TGTrack track = getFactory().newTrack();
		track.setNumber(number);
		track.setLyrics(lyrics);
		track.setName(readStringByte(40));
		int stringCount = readInt();
		for (int i = 0; i < 7; i++) {
			int tuning = readInt();
			if (stringCount > i) {
				TGString string = getFactory().newString();
				string.setNumber(i + 1);
				string.setValue(tuning);
				track.getStrings().add(string);
			}
		}
		readInt();
		readChannel(track.getChannel(), channels);
		readInt();
		track.setOffset(readInt());
		readColor(track.getColor());
		skip( (getVersionIndex() > 0)? 49 : 44);
		if(getVersionIndex() > 0){
			readStringByteSizeOfInteger();
			readStringByteSizeOfInteger();
		}
		return track;
	}
	
	private void readChannel(TGChannel channel,List channels) throws IOException {
		int index = (readInt() - 1);
		int effectChannel = (readInt() - 1);
		if(index >= 0 && index < channels.size()){
			((TGChannel) channels.get(index)).copy(channel);
			if (channel.getInstrument() < 0) {
				channel.setInstrument((short)0);
			}
			if(!channel.isPercussionChannel()){
				channel.setEffectChannel((short)effectChannel);
			}
		}
	}
	
	private void readChord(int strings,TGBeat beat) throws IOException{
		TGChord chord = getFactory().newChord(strings);
		this.skip(17);
		chord.setName(readStringByte(21));
		this.skip(4);
		chord.setFirstFret(readInt());
		for (int i = 0; i < 7; i++) {
			int fret = readInt();
			if(i < chord.countStrings()){
				chord.addFretValue(i,fret);
			}
		}
		this.skip(32);
		if(chord.countNotes() > 0){
			beat.setChord(chord);
		}
	}
	
	private void readBeatEffects(TGBeat beat, TGNoteEffect noteEffect) throws IOException {
		int flags1 = readUnsignedByte();
		int flags2 = readUnsignedByte();
		noteEffect.setFadeIn(((flags1 & 0x10) != 0));
		noteEffect.setVibrato(((flags1  & 0x02) != 0));
		if ((flags1 & 0x20) != 0) {
			int effect = readUnsignedByte();
			noteEffect.setTapping(effect == 1);
			noteEffect.setSlapping(effect == 2);
			noteEffect.setPopping(effect == 3);
		}
		if ((flags2 & 0x04) != 0) {
			readTremoloBar(noteEffect);
		}
		if ((flags1 & 0x40) != 0) {
			int strokeUp = readByte();
			int strokeDown = readByte();
			if( strokeUp > 0 ){
				beat.getStroke().setDirection( TGStroke.STROKE_UP );
				beat.getStroke().setValue( toStrokeValue(strokeUp) );
			}else if( strokeDown > 0 ){
				beat.getStroke().setDirection( TGStroke.STROKE_DOWN );
				beat.getStroke().setValue( toStrokeValue(strokeDown) );
			} 
		}
		if ((flags2 & 0x02) != 0) {
			readByte();
		}
	}
	
	private void readNoteEffects(TGNoteEffect noteEffect) throws IOException {
		int flags1 = readUnsignedByte();
		int flags2 = readUnsignedByte();
		if ((flags1 & 0x01) != 0) {
			readBend(noteEffect);
		}
		if ((flags1 & 0x10) != 0) {
			readGrace(noteEffect);
		}
		if ((flags2 & 0x04) != 0) {
			readTremoloPicking(noteEffect);
		}
		if ((flags2 & 0x08) != 0) {
			noteEffect.setSlide(true);
			readByte();
		}
		if ((flags2 & 0x10) != 0) {
			readArtificialHarmonic(noteEffect);
		}
		if ((flags2 & 0x20) != 0) {
			readTrill(noteEffect);
		}
		noteEffect.setHammer(((flags1 & 0x02) != 0));
		noteEffect.setVibrato(((flags2 & 0x40) != 0) || noteEffect.isVibrato());
		noteEffect.setPalmMute(((flags2 & 0x02) != 0));
		noteEffect.setStaccato(((flags2 & 0x01) != 0));
	}
	
	private void readGrace(TGNoteEffect effect) throws IOException {
		int fret = readUnsignedByte();
		int dynamic = readUnsignedByte();
		int transition = readByte();
		int duration = readUnsignedByte();
		int flags = readUnsignedByte();
		TGEffectGrace grace = getFactory().newEffectGrace();
		grace.setFret( fret );
		grace.setDynamic( (TGVelocities.MIN_VELOCITY + (TGVelocities.VELOCITY_INCREMENT * dynamic)) - TGVelocities.VELOCITY_INCREMENT );
		grace.setDuration(duration);
		grace.setDead( (flags & 0x01) != 0 );
		grace.setOnBeat( (flags & 0x02) != 0 );
		if(transition == 0){
			grace.setTransition(TGEffectGrace.TRANSITION_NONE);
		}
		else if(transition == 1){
			grace.setTransition(TGEffectGrace.TRANSITION_SLIDE);
		}
		else if(transition == 2){
			grace.setTransition(TGEffectGrace.TRANSITION_BEND);
		}
		else if(transition == 3){
			grace.setTransition(TGEffectGrace.TRANSITION_HAMMER);
		}
		effect.setGrace(grace);
	}
	
	private void readBend(TGNoteEffect effect) throws IOException {
		skip(5);
		TGEffectBend bend = getFactory().newEffectBend();
		int numPoints = readInt();
		for (int i = 0; i < numPoints; i++) {
			int bendPosition = readInt();
			int bendValue = readInt();
			readByte();
			
			int pointPosition = Math.round(bendPosition * TGEffectBend.MAX_POSITION_LENGTH / GP_BEND_POSITION);
			int pointValue = Math.round(bendValue * TGEffectBend.SEMITONE_LENGTH / GP_BEND_SEMITONE);
			bend.addPoint(pointPosition,pointValue);
		}
		if(!bend.getPoints().isEmpty()){
			effect.setBend(bend);
		}
	}
	
	private void readTremoloBar(TGNoteEffect effect) throws IOException {
		skip(5);
		TGEffectTremoloBar tremoloBar = getFactory().newEffectTremoloBar();
		int numPoints = readInt();
		for (int i = 0; i < numPoints; i++) {
			int position = readInt();
			int value = readInt();
			readByte();
			
			int pointPosition = Math.round(position * TGEffectTremoloBar.MAX_POSITION_LENGTH / GP_BEND_POSITION);
			int pointValue = Math.round(value / (GP_BEND_SEMITONE * 2f));
			tremoloBar.addPoint(pointPosition,pointValue);
		}
		if(!tremoloBar.getPoints().isEmpty()){
			effect.setTremoloBar(tremoloBar);
		}
	}
	
	private void readTrill(TGNoteEffect effect) throws IOException{
		byte fret = readByte();
		byte period = readByte();
		TGEffectTrill trill = getFactory().newEffectTrill();
		trill.setFret(fret);
		if(period == 1){
			trill.getDuration().setValue(TGDuration.SIXTEENTH);
			effect.setTrill(trill);
		}else if(period == 2){
			trill.getDuration().setValue(TGDuration.THIRTY_SECOND);
			effect.setTrill(trill);
		}else if(period == 3){
			trill.getDuration().setValue(TGDuration.SIXTY_FOURTH);
			effect.setTrill(trill);
		}
	}
	
	private void readArtificialHarmonic(TGNoteEffect effect) throws IOException{
		int type = readByte();
		TGEffectHarmonic harmonic = getFactory().newEffectHarmonic();
		harmonic.setData(0);
		if(type == 1){
			harmonic.setType(TGEffectHarmonic.TYPE_NATURAL);
			effect.setHarmonic(harmonic);
		}else if(type == 2){
			skip(3);
			harmonic.setType(TGEffectHarmonic.TYPE_ARTIFICIAL);
			effect.setHarmonic(harmonic);
		}else if(type == 3){
			skip(1);
			harmonic.setType(TGEffectHarmonic.TYPE_TAPPED);
			effect.setHarmonic(harmonic);
		}else if(type == 4){
			harmonic.setType(TGEffectHarmonic.TYPE_PINCH);
			effect.setHarmonic(harmonic);
		}else if(type == 5){
			harmonic.setType(TGEffectHarmonic.TYPE_SEMI);
			effect.setHarmonic(harmonic);
		}
	}
	
	public void readTremoloPicking(TGNoteEffect effect) throws IOException{
		int value = readUnsignedByte();
		TGEffectTremoloPicking tp = getFactory().newEffectTremoloPicking();
		if(value == 1){
			tp.getDuration().setValue(TGDuration.EIGHTH);
			effect.setTremoloPicking(tp);
		}else if(value == 2){
			tp.getDuration().setValue(TGDuration.SIXTEENTH);
			effect.setTremoloPicking(tp);
		}else if(value == 3){
			tp.getDuration().setValue(TGDuration.THIRTY_SECOND);
			effect.setTremoloPicking(tp);
		}
	}
	
	private void readMixChange(TGTempo tempo) throws IOException {
		readByte(); //instrument
		
		skip(16);
		int volume = readByte();
		int pan = readByte();
		int chorus = readByte();
		int reverb = readByte();
		int phaser = readByte();
		int tremolo = readByte();
		readStringByteSizeOfInteger(); //tempoName
		int tempoValue = readInt();
		if(volume >= 0){
			readByte();
		}
		if(pan >= 0){
			readByte();
		}
		if(chorus >= 0){
			readByte();
		}
		if(reverb >= 0){
			readByte();
		}
		if(phaser >= 0){
			readByte();
		}
		if(tremolo >= 0){
			readByte();
		}
		if(tempoValue >= 0){
			tempo.setValue(tempoValue);
			skip(1);
			if(getVersionIndex() > 0){
				skip(1);
			}
		}
		readByte();
		skip(1);
		if(getVersionIndex() > 0){
			readStringByteSizeOfInteger();
			readStringByteSizeOfInteger();
		}
	}
	
	private int toStrokeValue( int value ){
		if( value == 1 || value == 2){
			return TGDuration.SIXTY_FOURTH;
		}
		if( value == 3){
			return TGDuration.THIRTY_SECOND;
		}
		if( value == 4){
			return TGDuration.SIXTEENTH;
		}
		if( value == 5){
			return TGDuration.EIGHTH;
		}
		if( value == 6){
			return TGDuration.QUARTER;
		}
		return TGDuration.SIXTY_FOURTH;
	}
	
	private short toChannelShort(byte b){
		short value = (short)(( b * 8 ) - 1);
		return (short)Math.max(value,0);
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
	
	private TGBeat getBeat(TGMeasure measure, long start){
		int count = measure.countBeats();
		for(int i = 0 ; i < count ; i ++ ){
			TGBeat beat = measure.getBeat( i );
			if( beat.getStart() == start ){
				return beat;
			}
		}
		TGBeat beat = getFactory().newBeat();
		beat.setStart(start);
		measure.addBeat(beat);
		return beat;
	}
}
