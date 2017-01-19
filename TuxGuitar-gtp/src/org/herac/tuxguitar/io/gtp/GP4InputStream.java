package org.herac.tuxguitar.io.gtp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

public class GP4InputStream extends GTPInputStream {
	
	public static final TGFileFormat FILE_FORMAT = new TGFileFormat("Guitar Pro 4", "audio/x-gtp", new String[]{"gp4"});
	
	public static final GTPFileFormatVersion[] SUPPORTED_VERSIONS = new GTPFileFormatVersion[] {
		new GTPFileFormatVersion(FILE_FORMAT, "FICHIER GUITAR PRO v4.00", 0),
		new GTPFileFormatVersion(FILE_FORMAT, "FICHIER GUITAR PRO v4.06", 1),
		new GTPFileFormatVersion(FILE_FORMAT, "FICHIER GUITAR PRO L4.06", 2)
	};
	
	private static final float GP_BEND_SEMITONE = 25f;
	private static final float GP_BEND_POSITION = 60f;
	
	private int tripletFeel;
	private int keySignature;
	
	public GP4InputStream(GTPSettings settings){
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
			
			this.tripletFeel = ((readBoolean())?TGMeasureHeader.TRIPLET_FEEL_EIGHTH:TGMeasureHeader.TRIPLET_FEEL_NONE);
			
			int lyricTrack = readInt();
			TGLyric lyric = readLyrics();
			
			int tempoValue = readInt();
	
			this.keySignature = readKeySignature();
			this.skip(3);
			
			readByte(); //octave
			
			List<TGChannel> channels = readChannels();
			
			int measures = readInt();
			int tracks = readInt();
			
			readMeasureHeaders(song, measures);
			readTracks(song, tracks, channels, lyric, lyricTrack);
			readMeasures(song, measures, tracks, tempoValue);
			
			this.close();
			
			return song;
		} catch (GTPFormatException gtpFormatException) {
			throw gtpFormatException;
		} catch (Throwable throwable) {
			throw new TGFileFormatException(throwable);
		}
	}
	
	private void readInfo(TGSong song) throws IOException{
		song.setName(readStringByteSizeOfInteger());
		readStringByteSizeOfInteger();
		song.setArtist(readStringByteSizeOfInteger());
		song.setAlbum(readStringByteSizeOfInteger());
		song.setAuthor(readStringByteSizeOfInteger());
		song.setCopyright(readStringByteSizeOfInteger());
		song.setWriter(readStringByteSizeOfInteger());
		readStringByteSizeOfInteger();
		int comments = readInt();
		for (int i = 0; i < comments; i++) {
			song.setComments( song.getComments() + readStringByteSizeOfInteger() );
		}
	}
	
	private void readMeasureHeaders(TGSong song, int count) throws IOException{
		TGTimeSignature timeSignature = getFactory().newTimeSignature();
		for (int i = 0; i < count; i++) {
			song.addMeasureHeader(readMeasureHeader((i + 1),song,timeSignature));
		}
	}
	
	private void readTracks(TGSong song, int count, List<TGChannel> channels,TGLyric lyric, int lyricTrack) throws IOException{
		for (int number = 1; number <= count; number++) {
			song.addTrack(readTrack(song, number, channels,(number == lyricTrack)?lyric:getFactory().newLyric()));
		}
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
			}
			header.getTempo().copyFrom(tempo);
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
	
	private List<TGChannel> readChannels() throws IOException{
		List<TGChannel> channels = new ArrayList<TGChannel>();
		for (int i = 0; i < 64; i++) {
			TGChannel channel = getFactory().newChannel();
			channel.setProgram((short)readInt());
			channel.setVolume(toChannelShort(readByte()));
			channel.setBalance(toChannelShort(readByte()));
			channel.setChorus(toChannelShort(readByte()));
			channel.setReverb(toChannelShort(readByte()));
			channel.setPhaser(toChannelShort(readByte()));
			channel.setTremolo(toChannelShort(readByte()));
			channel.setBank( i == 9 ? TGChannel.DEFAULT_PERCUSSION_BANK : TGChannel.DEFAULT_BANK);
			if (channel.getProgram() < 0) {
				channel.setProgram((short)0);
			}
			channels.add(channel);
			skip(2);
		}
		return channels;
	}
	
	private long readBeat(long start, TGMeasure measure,TGTrack track, TGTempo tempo) throws IOException {
		int flags = readUnsignedByte();
		if((flags & 0x40) != 0){
			readUnsignedByte();
		}
		
		TGBeat beat = getFactory().newBeat();
		TGVoice voice = beat.getVoice(0);
		TGDuration duration = readDuration(flags);
		TGNoteEffect effect = getFactory().newEffect();
		if ((flags & 0x02) != 0) {
			readChord(track.stringCount(), beat);
		}
		if ((flags & 0x04) != 0) {
			readText(beat);
		}
		if ((flags & 0x08) != 0) {
			readBeatEffects(beat,effect);
		}
		if ((flags & 0x10) != 0) {
			readMixChange(tempo);
		}
		int stringFlags = readUnsignedByte();
		for (int i = 6; i >= 0; i--) {
			if ((stringFlags & (1 << i)) != 0 && (6 - i) < track.stringCount()) {
				TGString string = track.getString( (6 - i) + 1 ).clone(getFactory());
				TGNote note = readNote(string, track,effect.clone(getFactory()));
				voice.addNote(note);
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
					TGVoice voice = beat.getVoice(0);  
					for (int n = 0; n < voice.countNotes(); n ++) {
						TGNote note = voice.getNote( n );
						if (note.getString() == string) {
							return note.getValue();
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
		read();
	}
	
	private TGMarker readMarker(int measure) throws IOException {
		TGMarker marker = getFactory().newMarker();
		marker.setMeasure(measure);
		marker.setTitle(readStringByteSizeOfInteger());
		readColor(marker.getColor());
		return marker;
	}
	
	private TGMeasureHeader readMeasureHeader(int number,TGSong song,TGTimeSignature timeSignature) throws IOException {
		int flags = readUnsignedByte();
		TGMeasureHeader header = getFactory().newHeader();
		header.setNumber(number);
		header.setStart(0);
		header.getTempo().setValue(120);
		header.setTripletFeel(this.tripletFeel);
		header.setRepeatOpen( ((flags & 0x04) != 0) );
		if ((flags & 0x01) != 0) {
			timeSignature.setNumerator(readByte());
		}
		if ((flags & 0x02) != 0) {
			timeSignature.getDenominator().setValue(readByte());
		}
		header.getTimeSignature().copyFrom(timeSignature);
		if ((flags & 0x08) != 0) {
			header.setRepeatClose(readByte());
		}
		if ((flags & 0x10) != 0) {
			header.setRepeatAlternative(parseRepeatAlternative(song, number, readUnsignedByte()));
		}
		if ((flags & 0x20) != 0) {
			header.setMarker(readMarker(number));
		}
		if ((flags & 0x40) != 0) {
			this.keySignature = readKeySignature();
			this.skip(1);
		}
		return header;
	}
	
	private void readMeasure(TGMeasure measure,TGTrack track, TGTempo tempo) throws IOException{
		long nextNoteStart = measure.getStart();
		int numberOfBeats = readInt();
		for (int i = 0; i < numberOfBeats; i++) {
			nextNoteStart += readBeat(nextNoteStart, measure, track, tempo);
		}
		measure.setClef( getClef(track) );
		measure.setKeySignature(this.keySignature);
	}
	
	private TGNote readNote(TGString string, TGTrack track,TGNoteEffect effect)throws IOException {
		int flags = readUnsignedByte();
		TGNote note = getFactory().newNote();
		note.setString(string.getNumber());
		note.setEffect(effect);
		note.getEffect().setAccentuatedNote(((flags & 0x40) != 0));
		note.getEffect().setGhostNote(((flags & 0x04) != 0));
		if ((flags & 0x20) != 0) {
			int noteType = readUnsignedByte();
			note.setTiedNote( (noteType == 0x02) );
			note.getEffect().setDeadNote((noteType == 0x03));
		}
		if ((flags & 0x01) != 0) {
			skip(2);
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
		if ((flags & 0x08) != 0) {
			readNoteEffects(note.getEffect());
		}
		return note;
	}
	
	private TGTrack readTrack(TGSong song, int number, List<TGChannel> channels,TGLyric lyrics) throws IOException {
		TGTrack track = getFactory().newTrack();
		track.setNumber(number);
		track.setLyrics(lyrics);
		readUnsignedByte();
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
		readChannel(song, track, channels);
		readInt();
		track.setOffset(readInt());
		readColor(track.getColor());
		return track;
	}
	
	private void readChannel(TGSong song, TGTrack track, List<TGChannel> channels) throws IOException {
		int gmChannel1 = (readInt() - 1);
		int gmChannel2 = (readInt() - 1);
		if( gmChannel1 >= 0 && gmChannel1 < channels.size()){
			TGChannel channel = getFactory().newChannel();
			TGChannelParameter gmChannel1Param = getFactory().newChannelParameter();
			TGChannelParameter gmChannel2Param = getFactory().newChannelParameter();
			
			gmChannel1Param.setKey(GMChannelRoute.PARAMETER_GM_CHANNEL_1);
			gmChannel1Param.setValue(Integer.toString(gmChannel1));
			gmChannel2Param.setKey(GMChannelRoute.PARAMETER_GM_CHANNEL_2);
			gmChannel2Param.setValue(Integer.toString(gmChannel1 != 9 ? gmChannel2 : gmChannel1));
			
			channel.copyFrom(getFactory(), ((TGChannel) channels.get(gmChannel1)));
			
			//------------------------------------------//
			for( int i = 0 ; i < song.countChannels() ; i ++ ){
				TGChannel channelAux = song.getChannel(i);
				for( int n = 0 ; n < channelAux.countParameters() ; n ++ ){
					TGChannelParameter channelParameter = channelAux.getParameter( n );
					if( channelParameter.getKey().equals(GMChannelRoute.PARAMETER_GM_CHANNEL_1) ){
						if( Integer.toString(gmChannel1).equals(channelParameter.getValue()) ){
							channel.setChannelId(channelAux.getChannelId());
						}
					}
				}
			}
			if( channel.getChannelId() <= 0 ){
				channel.setChannelId( song.countChannels() + 1 );
				channel.setName(new TGSongManager(getFactory()).createChannelNameFromProgram(song, channel));
				channel.addParameter(gmChannel1Param);
				channel.addParameter(gmChannel2Param);
				song.addChannel(channel);
			}
			track.setChannelId(channel.getChannelId());
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
	
	private void readChord(int strings,TGBeat beat) throws IOException {
		TGChord chord = getFactory().newChord(strings);
		if ((readUnsignedByte() & 0x01) == 0) {
			chord.setName( readStringByteSizeOfInteger() );
			chord.setFirstFret(readInt());
			if(chord.getFirstFret() != 0){
				for (int i = 0; i < 6; i++) {
					int fret = readInt();
					if(i < chord.countStrings()){
						chord.addFretValue(i,fret);
					}
				}
			}
		}
		else{
			skip(16);
			chord.setName(readStringByte(21));
			skip(4);
			chord.setFirstFret(readInt());
			for (int i = 0; i < 7; i++) {
				int fret = readInt();
				if(i < chord.countStrings()){
					chord.addFretValue(i,fret);
				}
			}
			skip(32);
		}
		if(chord.countNotes() > 0){
			beat.setChord(chord);
		}
	}
	
	private void readGrace(TGNoteEffect effect) throws IOException {
		int fret = readUnsignedByte();
		TGEffectGrace grace = getFactory().newEffectGrace();
		grace.setOnBeat(false);
		grace.setDead( (fret == 255) );
		grace.setFret( ((!grace.isDead())?fret:0) );
		grace.setDynamic( (TGVelocities.MIN_VELOCITY + (TGVelocities.VELOCITY_INCREMENT * readUnsignedByte())) - TGVelocities.VELOCITY_INCREMENT );
		int transition = readUnsignedByte();
		if(transition == 0){
			grace.setTransition( TGEffectGrace.TRANSITION_NONE );
		}
		else if(transition == 1){
			grace.setTransition(  TGEffectGrace.TRANSITION_SLIDE );
		}
		else if(transition == 2){
			grace.setTransition(  TGEffectGrace.TRANSITION_BEND );
		}
		else if(transition == 3){
			grace.setTransition(  TGEffectGrace.TRANSITION_HAMMER );
		}
		grace.setDuration(readUnsignedByte());
		effect.setGrace(grace);
	}
	
	private void readBend(TGNoteEffect effect) throws IOException {
		TGEffectBend bend = getFactory().newEffectBend();
		skip(5);
		int points = readInt();
		for (int i = 0; i < points; i++) {
			int position = readInt();
			int value = readInt();
			readByte();
			
			int pointPosition = Math.round(position * TGEffectBend.MAX_POSITION_LENGTH / GP_BEND_POSITION);
			int pointValue = Math.round(value * TGEffectBend.SEMITONE_LENGTH / GP_BEND_SEMITONE);
			bend.addPoint(pointPosition,pointValue);
		}
		if(!bend.getPoints().isEmpty()){
			effect.setBend(bend);
		}
	}
	
	private void readTremoloBar(TGNoteEffect effect) throws IOException {
		TGEffectTremoloBar tremoloBar = getFactory().newEffectTremoloBar();
		skip(5);
		int points = readInt();
		for (int i = 0; i < points; i++) {
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
	
	private void readNoteEffects(TGNoteEffect noteEffect) throws IOException {
		int flags1 = readUnsignedByte();
		int flags2 = readUnsignedByte();
		noteEffect.setHammer(((flags1 & 0x02) != 0));
		noteEffect.setLetRing(((flags1 & 0x08) != 0));
		noteEffect.setVibrato(((flags2 & 0x40) != 0) || noteEffect.isVibrato());
		noteEffect.setPalmMute(((flags2 & 0x02) != 0));
		noteEffect.setStaccato(((flags2 & 0x01) != 0));
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
			TGEffectHarmonic harmonic = getFactory().newEffectHarmonic();
			int type = readByte();
			if(type == 1){
				harmonic.setType(TGEffectHarmonic.TYPE_NATURAL);
			}else if(type == 3){
				harmonic.setType(TGEffectHarmonic.TYPE_TAPPED);
			}else if(type == 4){
				harmonic.setType(TGEffectHarmonic.TYPE_PINCH);
			}else if(type == 5){
				harmonic.setType(TGEffectHarmonic.TYPE_SEMI);
			}else if(type == 15){
				harmonic.setType(TGEffectHarmonic.TYPE_ARTIFICIAL);
				harmonic.setData(2);
			}else if(type == 17){
				harmonic.setType(TGEffectHarmonic.TYPE_ARTIFICIAL);
				harmonic.setData(3);
			}else if(type == 22){
				harmonic.setType(TGEffectHarmonic.TYPE_ARTIFICIAL);
				harmonic.setData(0);
			}
			noteEffect.setHarmonic(harmonic);
		}
		if ((flags2 & 0x20) != 0) {
			byte fret = readByte();
			byte period = readByte();
			TGEffectTrill trill = getFactory().newEffectTrill();
			trill.setFret(fret);
			if(period == 1){
				trill.getDuration().setValue(TGDuration.SIXTEENTH);
				noteEffect.setTrill(trill);
			}else if(period == 2){
				trill.getDuration().setValue(TGDuration.THIRTY_SECOND);
				noteEffect.setTrill(trill);
			}else if(period == 3){
				trill.getDuration().setValue(TGDuration.SIXTY_FOURTH);
				noteEffect.setTrill(trill);
			}
		}
	}
	
	private void readBeatEffects(TGBeat beat,TGNoteEffect noteEffect) throws IOException {
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
			int strokeDown = readByte();
			int strokeUp = readByte();
			if( strokeDown > 0 ){
				beat.getStroke().setDirection( TGStroke.STROKE_DOWN );
				beat.getStroke().setValue( toStrokeValue(strokeDown) );
			}else if( strokeUp > 0 ){
				beat.getStroke().setDirection( TGStroke.STROKE_UP );
				beat.getStroke().setValue( toStrokeValue(strokeUp) );
			}
		}
		if ((flags2 & 0x02) != 0) {
			readByte();
		}
	}
	
	private void readMixChange(TGTempo tempo) throws IOException {
		readByte();
		int volume = readByte();
		int pan = readByte();
		int chorus = readByte();
		int reverb = readByte();
		int phaser = readByte();
		int tremolo = readByte();
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
			readByte();
		}
		readByte();
	}
	
	private int readKeySignature() throws IOException {
		// 0: C 1: G, -1: F		
		int keySignature = readByte();
		if (keySignature < 0){
			keySignature = 7 - keySignature; // translate -1 to 8, etc.
		}
		
		return keySignature;
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
