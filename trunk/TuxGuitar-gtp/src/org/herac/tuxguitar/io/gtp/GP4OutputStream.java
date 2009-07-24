/*
 * Created on 09-ene-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.io.gtp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGColor;
import org.herac.tuxguitar.song.models.TGDuration;
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
import org.herac.tuxguitar.song.models.TGDivisionType;
import org.herac.tuxguitar.song.models.TGVelocities;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.song.models.effects.TGEffectBend;
import org.herac.tuxguitar.song.models.effects.TGEffectGrace;
import org.herac.tuxguitar.song.models.effects.TGEffectHarmonic;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloBar;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloPicking;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class GP4OutputStream extends GTPOutputStream{
	private static final String GP4_FORMAT_EXTENSION = ".gp4";
	private static final String GP4_VERSION = "FICHIER GUITAR PRO v4.00";
	private static final int GP_BEND_SEMITONE = 25;
	private static final int GP_BEND_POSITION = 60;
	
	public GP4OutputStream(GTPSettings settings){
		super(settings);
	}
	
	public TGFileFormat getFileFormat(){
		return new TGFileFormat("Guitar Pro 4","*.gp4");
	}
	
	public boolean isSupportedExtension(String extension) {
		return (extension.toLowerCase().equals(GP4_FORMAT_EXTENSION)) ;
	}
	
	public void writeSong(TGSong song) {
		try {
			if(song.isEmpty()){
				throw new TGFileFormatException("Empty Song!!!");
			}
			TGMeasureHeader header = song.getMeasureHeader(0);
			writeStringByte(GP4_VERSION, 30, DEFAULT_VERSION_CHARSET);
			writeInfo(song);
			writeBoolean( (header.getTripletFeel() == TGMeasureHeader.TRIPLET_FEEL_EIGHTH) );
			writeLyrics(song);
			writeInt(header.getTempo().getValue());
			writeInt(0);
			writeByte((byte)0);
			writeChannels(song);
			writeInt(song.countMeasureHeaders());
			writeInt(song.countTracks());
			writeMeasures(song);
			writeTracks(song);
			writeMeasures(song, header.getTempo().clone(getFactory()));
			close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void writeInfo(TGSong song) throws IOException{
		List comments = toCommentLines(song.getComments());
		writeStringByteSizeOfInteger(song.getName());
		writeStringByteSizeOfInteger("");
		writeStringByteSizeOfInteger(song.getArtist());
		writeStringByteSizeOfInteger(song.getAlbum());
		writeStringByteSizeOfInteger(song.getAuthor());
		writeStringByteSizeOfInteger(song.getCopyright());
		writeStringByteSizeOfInteger(song.getWriter());
		writeStringByteSizeOfInteger("");
		writeInt( comments.size() );
		for (int i = 0; i < comments.size(); i++) {
			writeStringByteSizeOfInteger( (String)comments.get(i) );
		}
	}
	
	private void writeChannels(TGSong song) throws IOException{
		TGChannel[] channels = makeChannels(song);
		for (int i = 0; i < channels.length; i++) {
			writeInt(channels[i].getInstrument());
			writeByte(toChannelByte(channels[i].getVolume()));
			writeByte(toChannelByte(channels[i].getBalance()));
			writeByte(toChannelByte(channels[i].getChorus()));
			writeByte(toChannelByte(channels[i].getReverb()));
			writeByte(toChannelByte(channels[i].getPhaser()));
			writeByte(toChannelByte(channels[i].getTremolo()));
			writeBytes(new byte[]{0,0});
		}
	}
	
	private void writeMeasures(TGSong song, TGTempo tempo) throws IOException{
		for (int i = 0; i < song.countMeasureHeaders(); i++) {
			TGMeasureHeader header = song.getMeasureHeader(i);
			for (int j = 0; j < song.countTracks(); j++) {
				TGTrack track = song.getTrack(j);
				TGMeasure measure = track.getMeasure(i);
				writeMeasure(measure, (header.getTempo().getValue() != tempo.getValue()) );
			}
			header.getTempo().copy( tempo );
		}
	}
	
	private void writeLyrics(TGSong song) throws IOException{
		TGTrack lyricTrack = null;
		Iterator it = song.getTracks();
		while(it.hasNext()){
			TGTrack track = (TGTrack)it.next();
			if(!track.getLyrics().isEmpty()){
				lyricTrack = track;
				break;
			}
		}
		writeInt( (lyricTrack == null)?0:lyricTrack.getNumber() );
		writeInt((lyricTrack == null)?0:lyricTrack.getLyrics().getFrom());
		writeStringInteger((lyricTrack == null)?"":lyricTrack.getLyrics().getLyrics());
		for (int i = 0; i < 4; i++) {
			writeInt((lyricTrack == null)?0:1);
			writeStringInteger("");
		}
	}
	
	private void writeMeasures(TGSong song) throws IOException {
		TGTimeSignature timeSignature = getFactory().newTimeSignature();
		if (song.countMeasureHeaders() > 0) {
			for (int i = 0; i < song.countMeasureHeaders(); i++) {
				TGMeasureHeader measure = song.getMeasureHeader(i);
				writeMeasureHeader(measure, timeSignature);
				timeSignature.setNumerator(measure.getTimeSignature().getNumerator());
				timeSignature.getDenominator().setValue(measure.getTimeSignature().getDenominator().getValue());
			}
		}
	}
	
	private void writeMeasureHeader(TGMeasureHeader measure, TGTimeSignature timeSignature) throws IOException {
		int flags = 0;
		if (measure.getNumber() == 1 || measure.getTimeSignature().getNumerator() != timeSignature.getNumerator()) {
			flags |= 0x01;
		}
		if (measure.getNumber() == 1 || measure.getTimeSignature().getDenominator().getValue() != timeSignature.getDenominator().getValue()) {
			flags |= 0x02;
		}
		if (measure.isRepeatOpen()) {
			flags |= 0x04;
		}
		if (measure.getRepeatClose() > 0) {
			flags |= 0x08;
		}
		if (measure.hasMarker()) {
			flags |= 0x20;
		}
		writeUnsignedByte(flags);
		
		if ((flags & 0x01) != 0) {
			writeByte((byte) measure.getTimeSignature().getNumerator());
		}
		if ((flags & 0x02) != 0) {
			writeByte((byte) measure.getTimeSignature().getDenominator().getValue());
		}
		if ((flags & 0x08) != 0) {
			writeByte((byte) measure.getRepeatClose());
		}
		if ((flags & 0x20) != 0) {
			writeMarker(measure.getMarker());
		}
	}
	
	private void writeTracks(TGSong song) throws IOException {
		for (int i = 0; i < song.countTracks(); i++) {
			TGTrack track = song.getTrack(i);
			writeTrack(track);
		}
	}
	
	private void writeTrack(TGTrack track) throws IOException {
		int flags = 0;
		if (track.isPercussionTrack()) {
			flags |= 0x01;
		}
		writeUnsignedByte(flags);
		writeStringByte(track.getName(), 40);
		writeInt(track.getStrings().size());
		for (int i = 0; i < 7; i++) {
			int value = 0;
			if (track.getStrings().size() > i) {
				TGString string = (TGString) track.getStrings().get(i);
				value = string.getValue();
			}
			writeInt(value);
		}
		writeInt(1);
		writeInt(track.getChannel().getChannel() + 1);
		writeInt(track.getChannel().getEffectChannel() + 1);
		writeInt(24);
		writeInt(Math.min(Math.max(track.getOffset(),0),12));
		writeColor(track.getColor());
	}
	
	private void writeMeasure(TGMeasure srcMeasure, boolean changeTempo) throws IOException {
		TGMeasure measure = new GTPVoiceJoiner(getFactory(),srcMeasure).process();
		
		int beatCount = measure.countBeats();
		writeInt(beatCount);
		for (int i = 0; i < beatCount; i++) {
			TGBeat beat = measure.getBeat( i );
			writeBeat(beat, measure, ( changeTempo && i == 0 ));
		}
		
	}
	
	private void writeBeat(TGBeat beat, TGMeasure measure, boolean changeTempo) throws IOException {
		TGVoice voice = beat.getVoice(0);
		TGDuration duration = voice.getDuration();
		TGNoteEffect effect = getFactory().newEffect();
		for (int i = 0; i < voice.countNotes(); i++) {
			TGNote playedNote = voice.getNote(i);
			
			if(playedNote.getEffect().isFadeIn()){
				effect.setFadeIn(true);
			}
			if(playedNote.getEffect().isTremoloBar()){
				effect.setTremoloBar(playedNote.getEffect().getTremoloBar().clone(getFactory()));
			}
			if(playedNote.getEffect().isTapping()){
				effect.setTapping(true);
			}
			if(playedNote.getEffect().isSlapping()){
				effect.setSlapping(true);
			}
			if(playedNote.getEffect().isPopping()){
				effect.setPopping(true);
			}
		}
		
		int flags = 0;
		if (duration.isDotted() || duration.isDoubleDotted() ) {
			flags |= 0x01;
		}
		if(beat.isChordBeat()){
			flags |= 0x02;
		}
		if(beat.isTextBeat()){
			flags |= 0x04;
		}
		if ( beat.getStroke().getDirection() != TGStroke.STROKE_NONE ){
			flags |= 0x08;
		}
		else if (effect.isTremoloBar() || effect.isTapping() || effect.isSlapping() || effect.isPopping() || effect.isFadeIn()) {
			flags |= 0x08;
		}
		if (!duration.getDivision().isEqual(TGDivisionType.NORMAL)) {
			flags |= 0x20;
		}
		if (changeTempo) {
			flags |= 0x10;
		}
		if (voice.isRestVoice()) {
			flags |= 0x40;
		}
		writeUnsignedByte(flags);
		
		if ((flags & 0x40) != 0) {
			writeUnsignedByte(0x02);
		}
		writeByte(parseDuration(duration));
		if ((flags & 0x20) != 0) {
			writeInt(duration.getDivision().getEnters());
		}
		if ((flags & 0x02) != 0) {
			writeChord(beat.getChord());
		}
		if ((flags & 0x04) != 0) {
			writeText(beat.getText());
		}
		if ((flags & 0x08) != 0) {
			writeBeatEffects(beat,effect);
		}
		if ((flags & 0x10) != 0) {
			writeMixChange(measure.getTempo());
		}
		int stringFlags = 0;
		if (!voice.isRestVoice()) {
			for (int i = 0; i < voice.countNotes(); i++) {
				TGNote playedNote = voice.getNote(i);
				int string = (7 - playedNote.getString());
				stringFlags |= (1 << string);
			}
		}
		writeUnsignedByte(stringFlags);
		for (int i = 6; i >= 0; i--) {
			if ((stringFlags & (1 << i)) != 0 ) {
				for( int n = 0; n < voice.countNotes(); n ++){
					TGNote playedNote = voice.getNote( n );
					if( playedNote.getString() == (6 - i + 1) ){
						writeNote(playedNote);
						break;
					}
				}
			}
		}
	}
	
	private void writeNote(TGNote note) throws IOException {
		int flags = ( 0x20 | 0x10 );
		if(note.getEffect().isGhostNote()){
			flags |= 0x04;
		}
		if(note.getEffect().isAccentuatedNote()){
			flags |= 0x40;
		}
		if (note.getEffect().isVibrato()		||
			note.getEffect().isBend()		   ||
			note.getEffect().isGrace()		  ||
			note.getEffect().isSlide()		  ||
			note.getEffect().isHammer()		 ||
			note.getEffect().isPalmMute()	   ||
			note.getEffect().isStaccato()	   ||
			note.getEffect().isTapping()		||
			note.getEffect().isSlapping()	   ||
			note.getEffect().isPopping()		||
			note.getEffect().isHarmonic()	   ||
			note.getEffect().isTremoloPicking() ||
			note.getEffect().isTrill()) {
			flags |= 0x08;
		}
		writeUnsignedByte(flags);
		
		if ((flags & 0x20) != 0) {
			int typeHeader = 0x01;
			if (note.isTiedNote()) {
				typeHeader = 0x02;
			}else if(note.getEffect().isDeadNote()){
				typeHeader = 0x03;
			}
			writeUnsignedByte(typeHeader);
		}
		if ((flags & 0x10) != 0) {
			writeByte((byte)(((note.getVelocity() - TGVelocities.MIN_VELOCITY) / TGVelocities.VELOCITY_INCREMENT) + 1));
		}
		if ((flags & 0x20) != 0) {
			writeByte((byte) note.getValue());
		}
		if ((flags & 0x08) != 0) {
			writeNoteEffects(note.getEffect());
		}
	}
	
	private byte parseDuration(TGDuration duration) {
		byte value = 0;
		switch (duration.getValue()) {
		case TGDuration.WHOLE:
			value = -2;
			break;
		case TGDuration.HALF:
			value = -1;
			break;
		case TGDuration.QUARTER:
			value = 0;
			break;
		case TGDuration.EIGHTH:
			value = 1;
			break;
		case TGDuration.SIXTEENTH:
			value = 2;
			break;
		case TGDuration.THIRTY_SECOND:
			value = 3;
			break;
		case TGDuration.SIXTY_FOURTH:
			value = 4;
			break;
		}
		return value;
	}
	
	private void writeText(TGText text) throws IOException{
		writeStringByteSizeOfInteger(text.getValue());
	}
	
	private void writeChord(TGChord chord) throws IOException{
		writeUnsignedByte( 0x01 );
		skipBytes(16);
		writeStringByte(chord.getName(),21);
		skipBytes(4);
		writeInt(chord.getFirstFret());
		for (int i = 0; i < 7; i++) {
			writeInt( (i < chord.countStrings() ? chord.getFretValue(i) : -1 ) ) ;
		}
		skipBytes(32);
	}
	
	private void writeBeatEffects(TGBeat beat,TGNoteEffect effect) throws IOException{
		int flags1 = 0;
		int flags2 = 0;
		if(effect.isFadeIn()){
			flags1 |= 0x10;
		}
		if(effect.isTapping() || effect.isSlapping() || effect.isPopping()){
			flags1 |= 0x20;
		}
		if(effect.isTremoloBar()){
			flags2 |= 0x04;
		}
		if(beat.getStroke().getDirection() != TGStroke.STROKE_NONE){
			flags1 |= 0x40;
		}
		writeUnsignedByte(flags1);
		writeUnsignedByte(flags2);
		
		if ((flags1 & 0x20) != 0) {
			if(effect.isTapping()){
				writeUnsignedByte(1);
			}else if(effect.isSlapping()){
				writeUnsignedByte(2);
			}else if(effect.isPopping()){
				writeUnsignedByte(3);
			}
		}
		if ((flags2 & 0x04) != 0) {
			writeTremoloBar(effect.getTremoloBar());
		}
		if ((flags1 & 0x40) != 0) {
			writeUnsignedByte( (beat.getStroke().getDirection() == TGStroke.STROKE_DOWN ? toStrokeValue(beat.getStroke()) : 0 ) );
			writeUnsignedByte( (beat.getStroke().getDirection() == TGStroke.STROKE_UP ? toStrokeValue(beat.getStroke()) : 0 ) );
		}
	}
	
	private void writeNoteEffects(TGNoteEffect effect) throws IOException {
		int flags1 = 0;
		int flags2 = 0;
		if (effect.isBend()) {
			flags1 |= 0x01;
		}
		if (effect.isHammer()) {
			flags1 |= 0x02;
		}
		if (effect.isGrace()) {
			flags1 |= 0x10;
		}
		if(effect.isStaccato()){
			flags2 |= 0x01;
		}
		if(effect.isPalmMute()){
			flags2 |= 0x02;
		}
		if(effect.isTremoloPicking()){
			flags2 |= 0x04;
		}
		if (effect.isSlide()) {
			flags2 |= 0x08;
		}
		if (effect.isVibrato()) {
			flags2 |= 0x40;
		}
		if(effect.isHarmonic()){
			flags2 |= 0x10;
		}
		if(effect.isTrill()){
			flags2 |= 0x20;
		}
		writeUnsignedByte(flags1);
		writeUnsignedByte(flags2);
		
		if ((flags1 & 0x01) != 0) {
			writeBend(effect.getBend());
		}
		if ((flags1 & 0x10) != 0) {
			writeGrace(effect.getGrace());
		}
		if ((flags2 & 0x04) != 0) {
			writeTremoloPicking(effect.getTremoloPicking());
		}
		if ((flags2 & 0x08) != 0) {
			writeByte((byte)1);
		}
		if ((flags2 & 0x10) != 0) {
			if(effect.getHarmonic().getType() == TGEffectHarmonic.TYPE_NATURAL){
				writeByte((byte)1);
			}else if(effect.getHarmonic().getType() == TGEffectHarmonic.TYPE_TAPPED){
				writeByte((byte)3);
			}else if(effect.getHarmonic().getType() == TGEffectHarmonic.TYPE_PINCH){
				writeByte((byte)4);
			}else if(effect.getHarmonic().getType() == TGEffectHarmonic.TYPE_SEMI){
				writeByte((byte)5);
			}else if(effect.getHarmonic().getType() == TGEffectHarmonic.TYPE_ARTIFICIAL){
				writeByte((byte)15);
			}
		}
		if ((flags2 & 0x20) != 0) {
			writeByte((byte)effect.getTrill().getFret());
			if(effect.getTrill().getDuration().getValue() ==  TGDuration.SIXTEENTH){
				writeByte((byte)1);
			}else if(effect.getTrill().getDuration().getValue() ==  TGDuration.THIRTY_SECOND){
				writeByte((byte)2);
			}else if(effect.getTrill().getDuration().getValue() ==  TGDuration.SIXTY_FOURTH){
				writeByte((byte)3);
			}
		}
	}
	
	private void writeBend(TGEffectBend bend) throws IOException {
		int points = bend.getPoints().size();
		writeByte((byte) 1);
		writeInt(0);
		writeInt(points);
		for (int i = 0; i < points; i++) {
			TGEffectBend.BendPoint point = (TGEffectBend.BendPoint) bend.getPoints().get(i);
			writeInt( (point.getPosition() * GP_BEND_POSITION / TGEffectBend.MAX_POSITION_LENGTH) );
			writeInt( (point.getValue() * GP_BEND_SEMITONE / TGEffectBend.SEMITONE_LENGTH) );
			writeByte((byte) 0);
		}
	}
	
	private void writeTremoloBar(TGEffectTremoloBar effect) throws IOException {
		int points = effect.getPoints().size();
		writeByte((byte) 6);
		writeInt(0);
		writeInt(points);
		for (int i = 0; i < points; i++) {
			TGEffectTremoloBar.TremoloBarPoint point = (TGEffectTremoloBar.TremoloBarPoint) effect.getPoints().get(i);
			writeInt( (point.getPosition() * GP_BEND_POSITION / TGEffectTremoloBar.MAX_POSITION_LENGTH) );
			writeInt( (point.getValue() * (GP_BEND_SEMITONE * 2)) );
			writeByte((byte) 0);
		}
	}
	
	public void writeTremoloPicking(TGEffectTremoloPicking effect) throws IOException{
		if(effect.getDuration().getValue() == TGDuration.EIGHTH){
			writeUnsignedByte(1);
		}else if(effect.getDuration().getValue() == TGDuration.SIXTEENTH){
			writeUnsignedByte(2);
		}else if(effect.getDuration().getValue() == TGDuration.THIRTY_SECOND){
			writeUnsignedByte(3);
		}
	}
	
	private void writeGrace(TGEffectGrace grace) throws IOException {
		if(grace.isDead()){
			writeUnsignedByte(255);
		}else{
			writeUnsignedByte(grace.getFret());
		}
		writeUnsignedByte(((grace.getDynamic() - TGVelocities.MIN_VELOCITY) / TGVelocities.VELOCITY_INCREMENT) + 1);
		if(grace.getTransition() == TGEffectGrace.TRANSITION_NONE){
			writeUnsignedByte(0);
		}
		else if(grace.getTransition() == TGEffectGrace.TRANSITION_SLIDE){
			writeUnsignedByte(1);
		}
		else if(grace.getTransition() == TGEffectGrace.TRANSITION_BEND){
			writeUnsignedByte(2);
		}
		else if(grace.getTransition() == TGEffectGrace.TRANSITION_HAMMER){
			writeUnsignedByte(3);
		}
		writeUnsignedByte(grace.getDuration());
	}
	
	private void writeMixChange(TGTempo tempo) throws IOException {
		for (int i = 0; i < 7; i++) {
			writeByte((byte) -1);
		}
		writeInt(tempo.getValue());
		writeByte((byte) 0);
		writeUnsignedByte(1);
	}
	
	private void writeMarker(TGMarker marker) throws IOException {
		writeStringByteSizeOfInteger(marker.getTitle());
		writeColor(marker.getColor());
	}
	
	private void writeColor(TGColor color) throws IOException {
		writeUnsignedByte(color.getR());
		writeUnsignedByte(color.getG());
		writeUnsignedByte(color.getB());
		writeByte((byte)0);
	}
	
	private TGChannel[] makeChannels(TGSong song) {
		TGChannel[] channels = new TGChannel[64];
		for (int i = 0; i < channels.length; i++) {
			channels[i] = getFactory().newChannel();
			channels[i].setChannel((short)i);
			channels[i].setEffectChannel((short)i);
			channels[i].setInstrument((short)24);
			channels[i].setVolume((short)13);
			channels[i].setBalance((short)8);
			channels[i].setChorus((short)0);
			channels[i].setReverb((short)0);
			channels[i].setPhaser((short)0);
			channels[i].setTremolo((short)0);
		}
		
		Iterator it = song.getTracks();
		while (it.hasNext()) {
			TGTrack track = (TGTrack) it.next();
			channels[track.getChannel().getChannel()].setInstrument(track.getChannel().getInstrument());
			channels[track.getChannel().getChannel()].setVolume(track.getChannel().getVolume());
			channels[track.getChannel().getChannel()].setBalance(track.getChannel().getBalance());
			channels[track.getChannel().getEffectChannel()].setInstrument(track.getChannel().getInstrument());
			channels[track.getChannel().getEffectChannel()].setVolume(track.getChannel().getVolume());
			channels[track.getChannel().getEffectChannel()].setBalance(track.getChannel().getBalance());
		}
		
		return channels;
	}
	
	private int toStrokeValue( TGStroke stroke ){
		if( stroke.getValue() == TGDuration.SIXTY_FOURTH ){
			return 2;
		}
		if( stroke.getValue() == TGDuration.THIRTY_SECOND ){
			return 3;
		}
		if( stroke.getValue() == TGDuration.SIXTEENTH ){
			return 4;
		}
		if( stroke.getValue() == TGDuration.EIGHTH ){
			return 5;
		}
		if( stroke.getValue() == TGDuration.QUARTER ){
			return 6;
		}
		return 2;
	}
	
	private byte toChannelByte(short s){
		return  (byte) ((s + 1) / 8);
	}
	
	private List toCommentLines( String comments ){
		List lines = new ArrayList();
		
		String line = comments;
		while( line.length() > Byte.MAX_VALUE ) {
			String subline = line.substring(0, Byte.MAX_VALUE);
			lines.add( subline );
			line = line.substring( Byte.MAX_VALUE );
		}
		lines.add( line );
		
		return lines;
	}
}
