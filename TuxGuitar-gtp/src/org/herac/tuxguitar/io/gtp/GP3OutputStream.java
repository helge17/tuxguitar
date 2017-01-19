package org.herac.tuxguitar.io.gtp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.gm.GMChannelRoute;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGColor;
import org.herac.tuxguitar.song.models.TGDivisionType;
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
import org.herac.tuxguitar.song.models.TGVelocities;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.song.models.effects.TGEffectBend;
import org.herac.tuxguitar.song.models.effects.TGEffectGrace;
import org.herac.tuxguitar.song.models.effects.TGEffectHarmonic;

public class GP3OutputStream extends GTPOutputStream {
	
	private static final String GP3_VERSION = "FICHIER GUITAR PRO v3.00";
	private static final int GP_BEND_SEMITONE = 25;
	private static final int GP_BEND_POSITION = 60;
	
	public GP3OutputStream(GTPSettings settings){
		super(settings);
	}
	
	public TGFileFormat getFileFormat(){
		return new TGFileFormat("Guitar Pro 3", "audio/x-gtp", new String[]{"gp3"});
	}
	
	public void writeSong(TGSong song) {
		try {
			if(song.isEmpty()){
				throw new TGFileFormatException("Empty Song!!!");
			}
			configureChannelRouter(song);
			TGMeasureHeader header = song.getMeasureHeader(0);
			writeStringByte(GP3_VERSION, 30, DEFAULT_VERSION_CHARSET);
			writeInfo(song);
			writeBoolean( (header.getTripletFeel() == TGMeasureHeader.TRIPLET_FEEL_EIGHTH) );
			writeInt(header.getTempo().getValue());
			writeInt(0);
			writeChannels(song);
			writeInt(song.countMeasureHeaders());
			writeInt(song.countTracks());
			writeMeasureHeaders(song);
			writeTracks(song);
			writeMeasures(song,header.getTempo().clone(getFactory()));
			close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void writeInfo(TGSong song) throws IOException{
		List<String> comments = toCommentLines(song.getComments());
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
			writeInt(channels[i].getProgram());
			writeByte(toChannelByte(channels[i].getVolume()));
			writeByte(toChannelByte(channels[i].getBalance()));
			writeByte(toChannelByte(channels[i].getChorus()));
			writeByte(toChannelByte(channels[i].getReverb()));
			writeByte(toChannelByte(channels[i].getPhaser()));
			writeByte(toChannelByte(channels[i].getTremolo()));
			writeBytes(new byte[]{0,0});
		}
	}
	
	private void writeMeasureHeaders(TGSong song) throws IOException {
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
	
	private void writeMeasures(TGSong song,TGTempo tempo) throws IOException{
		for (int i = 0; i < song.countMeasureHeaders(); i++) {
			TGMeasureHeader header = song.getMeasureHeader(i);
			for (int j = 0; j < song.countTracks(); j++) {
				TGTrack track = song.getTrack(j);
				TGMeasure measure = track.getMeasure(i);
				writeMeasure(measure, (header.getTempo().getValue() != tempo.getValue()) );
			}
			tempo.copyFrom( header.getTempo() );
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
			createTrack(track);
		}
	}
	
	private void createTrack(TGTrack track) throws IOException {
		GMChannelRoute channel = getChannelRoute(track.getChannelId());
		
		int flags = 0;
		if (isPercussionChannel(track.getSong(),track.getChannelId())) {
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
		writeInt(channel.getChannel1() + 1);
		writeInt(channel.getChannel2() + 1);
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
			writeBeat(beat, measure, ( changeTempo && i == 0 ) );
		}
	}
	
	private void writeBeat(TGBeat beat,TGMeasure measure, boolean changeTempo) throws IOException {
		TGVoice voice = beat.getVoice(0);
		TGDuration duration = voice.getDuration();
		int flags = 0;
		if (duration.isDotted() || duration.isDoubleDotted() ) {
			flags |= 0x01;
		}
		if (!duration.getDivision().isEqual(TGDivisionType.NORMAL)) {
			flags |= 0x20;
		}
		if(beat.isTextBeat()){
			flags |= 0x04;
		}
		if (changeTempo) {
			flags |= 0x10;
		}
		TGNoteEffect effect = null;
		if (voice.isRestVoice()) {
			flags |= 0x40;
		} else if(voice.countNotes() > 0){
			TGNote note = voice.getNote(0);
			effect = note.getEffect();
			if (effect.isVibrato() ||
				effect.isTremoloBar() ||
				effect.isTapping() ||
				effect.isSlapping() ||
				effect.isPopping() ||
				effect.isHarmonic() ||
				effect.isFadeIn() ||
				beat.getStroke().getDirection() != TGStroke.STROKE_NONE) {
				flags |= 0x08;
			}
		}
		writeUnsignedByte(flags);
		
		if ((flags & 0x40) != 0) {
			writeUnsignedByte(2);
		}
		writeByte(parseDuration(duration));
		if ((flags & 0x20) != 0) {
			writeInt(duration.getDivision().getEnters());
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
		if (note.getEffect().isBend() || note.getEffect().isGrace() || note.getEffect().isSlide() || note.getEffect().isHammer() || note.getEffect().isLetRing()) {
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
	
	private void writeText(TGText text) throws IOException {
		writeStringByteSizeOfInteger(text.getValue());
	}
	
	private void writeBeatEffects(TGBeat beat, TGNoteEffect noteEffect) throws IOException {
		int flags = 0;
		if (noteEffect.isVibrato()) {
			flags += 0x01;
		}
		if (noteEffect.isTremoloBar() || noteEffect.isTapping() || noteEffect.isSlapping() || noteEffect.isPopping()) {
			flags += 0x20;
		}
		if(beat.getStroke().getDirection() != TGStroke.STROKE_NONE){
			flags |= 0x40;
		}
		if (noteEffect.isHarmonic() && noteEffect.getHarmonic().getType() == TGEffectHarmonic.TYPE_NATURAL) {
			flags += 0x04;
		}
		if (noteEffect.isHarmonic() && noteEffect.getHarmonic().getType() != TGEffectHarmonic.TYPE_NATURAL) {
			flags += 0x08;
		}
		if (noteEffect.isFadeIn()) {
			flags += 0x10;
		}
		writeUnsignedByte(flags);
		
		if ((flags & 0x20) != 0) {
			if (noteEffect.isTremoloBar()){
				writeUnsignedByte(0);
				writeInt(100);
			}
			else if (noteEffect.isTapping()){
				writeUnsignedByte(1);
				writeInt(0);
			}
			else if (noteEffect.isSlapping()){
				writeUnsignedByte(2);
				writeInt(0);
			}
			else if (noteEffect.isPopping()){
				writeUnsignedByte(3);
				writeInt(0);
			}
		}
		if ((flags & 0x40) != 0) {
			writeUnsignedByte( (beat.getStroke().getDirection() == TGStroke.STROKE_DOWN ? toStrokeValue(beat.getStroke()) : 0 ) );
			writeUnsignedByte( (beat.getStroke().getDirection() == TGStroke.STROKE_UP ? toStrokeValue(beat.getStroke()) : 0 ) );
		}
	}
	
	private void writeNoteEffects(TGNoteEffect effect) throws IOException {
		int flags = 0;
		if (effect.isBend()) {
			flags |= 0x01;
		}
		if (effect.isHammer()) {
			flags |= 0x02;
		}
		if (effect.isSlide()) {
			flags |= 0x04;
		}
		if (effect.isLetRing()) {
			flags |= 0x08;
		}
		if (effect.isGrace()) {
			flags |= 0x10;
		}
		writeUnsignedByte(flags);
		
		if ((flags & 0x01) != 0) {
			writeBend(effect.getBend());
		}
		if ((flags & 0x10) != 0) {
			writeGrace(effect.getGrace());
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
	
	private void writeGrace(TGEffectGrace grace) throws IOException {
		if(grace.isDead()){
			writeUnsignedByte(0xff);
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
			channels[i].setProgram((short)24);
			channels[i].setVolume((short)13);
			channels[i].setBalance((short)8);
			channels[i].setChorus((short)0);
			channels[i].setReverb((short)0);
			channels[i].setPhaser((short)0);
			channels[i].setTremolo((short)0);
		}
		
		Iterator<TGChannel> it = song.getChannels();
		while (it.hasNext()) {
			TGChannel tgChannel = (TGChannel) it.next();
			GMChannelRoute gmChannelRoute = getChannelRoute(tgChannel.getChannelId());
			channels[gmChannelRoute.getChannel1()].setProgram(tgChannel.getProgram());
			channels[gmChannelRoute.getChannel1()].setVolume(tgChannel.getVolume());
			channels[gmChannelRoute.getChannel1()].setBalance(tgChannel.getBalance());
			channels[gmChannelRoute.getChannel2()].setProgram(tgChannel.getProgram());
			channels[gmChannelRoute.getChannel2()].setVolume(tgChannel.getVolume());
			channels[gmChannelRoute.getChannel2()].setBalance(tgChannel.getBalance());
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
	
	private List<String> toCommentLines( String comments ){
		List<String> lines = new ArrayList<String>();
		
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