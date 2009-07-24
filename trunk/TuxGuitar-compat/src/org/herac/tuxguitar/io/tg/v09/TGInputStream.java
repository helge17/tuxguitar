/*
 * Created on 16-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.io.tg.v09;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGInputStreamBase;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGBeat;
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
import org.herac.tuxguitar.song.models.TGTempo;
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
import org.herac.tuxguitar.song.models.effects.TGEffectTrill;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TGInputStream extends TGStream implements TGInputStreamBase{
	
	private DataInputStream dataInputStream;
	private String version;
	private TGFactory factory;
	
	private int velocity;
	
	public TGInputStream() {
		super();
	}
	
	public void init(TGFactory factory,InputStream stream) {
		this.factory = factory;
		this.dataInputStream = new DataInputStream(stream);
		this.version = null;
	}
	
	public TGFileFormat getFileFormat(){
		return new TGFileFormat("TuxGuitar","*.tg");
	}
	
	public boolean isSupportedVersion(String version){
		return (version.equals(TG_VERSION));
	}
	
	public boolean isSupportedVersion(){
		try{
			readVersion();
			return isSupportedVersion(this.version);
		}catch(Throwable throwable){
			return false;
		}
	}
	
	private void readVersion(){
		if(this.version == null){
			this.version = readString();
		}
	}
	
	public TGSong readSong() throws TGFileFormatException{
		try {
			if(this.isSupportedVersion()){
				TGSong song = this.read();
				this.dataInputStream.close();
				return song;
			}
			throw new TGFileFormatException("Unsopported Version");
		} catch (Throwable throwable) {
			throw new TGFileFormatException(throwable);
		}
	}
	
	private TGSong read(){
		TGSong song = this.factory.newSong();
		
		//leo el nombre
		song.setName(readString());
		
		//leo el artista
		song.setArtist(readString());
		
		//leo el album
		song.setAlbum(readString());
		
		//leo el autor
		song.setAuthor(readString());
		
		//leo la cantidad de measure headers
		int headerCount = readShort();
		
		//leo las pistas
		long headerStart = TGDuration.QUARTER_TIME;
		TGMeasureHeader lastHeader = null;
		for(int i = 0;i < headerCount;i++){
			TGMeasureHeader header = readMeasureHeader(i + 1,headerStart,lastHeader);
			song.addMeasureHeader(header);
			headerStart += header.getLength();
			lastHeader = header;
		}
		
		//leo la cantidad de pistas
		int trackCount = readByte();
		
		//leo las pistas
		for(int i = 0;i < trackCount;i++){
			song.addTrack(readTrack(i + 1,song) );
		}
		
		return song;
	}
	
	private TGTrack readTrack(int number,TGSong song){
		//header
		int header = readHeader();
		
		TGTrack track = this.factory.newTrack();
		
		track.setNumber(number);
		
		//leo el nombre
		track.setName( readString() );
		
		//leo el canal
		readChannel(track);
		
		//leo la cantidad de compases
		int measureCount = song.countMeasureHeaders();
		
		//leo los compases
		TGMeasure lastMeasure = null;
		for(int i = 0;i < measureCount;i++){
			TGMeasure measure = readMeasure(song.getMeasureHeader(i),lastMeasure);
			track.addMeasure(measure);
			lastMeasure = measure;
		}
		
		//leo la cantidad de cuerdas
		int stringCount = readByte();
		
		//leo las cuerdas
		for(int i = 0;i < stringCount;i++){
			track.getStrings().add(readInstrumentString(i + 1));
		}
		
		//leo el offset
		track.setOffset( (TGTrack.MIN_OFFSET + readByte()) );
		
		//leo el color
		readColor(track.getColor());
		
		//leo el lyrics
		if(((header & TRACK_LYRICS) != 0)){
			readLyrics(track.getLyrics());
		}
		
		return track;
	}
	
	private TGMeasureHeader readMeasureHeader(int number,long start,TGMeasureHeader lastMeasureHeader){
		int header = readHeader();
		
		TGMeasureHeader measureHeader = this.factory.newHeader();
		measureHeader.setNumber(number);
		measureHeader.setStart(start);
		
		//leo el time signature
		if(((header & MEASURE_HEADER_TIMESIGNATURE) != 0)){
			readTimeSignature(measureHeader.getTimeSignature());
		}else if(lastMeasureHeader != null){
			lastMeasureHeader.getTimeSignature().copy(measureHeader.getTimeSignature());
		}
		
		//leo el tempo
		if(((header & MEASURE_HEADER_TEMPO) != 0)){
			readTempo(measureHeader.getTempo());
		}else if(lastMeasureHeader != null){
			lastMeasureHeader.getTempo().copy(measureHeader.getTempo());
		}
		
		//leo el comienzo de la repeticion
		measureHeader.setRepeatOpen((header & MEASURE_HEADER_OPEN_REPEAT) != 0);
		
		//leo el numero de repeticiones
		if(((header & MEASURE_HEADER_CLOSE_REPEAT) != 0)){
			measureHeader.setRepeatClose(readShort());
		}
		
		//leo el marker
		if(((header & MEASURE_HEADER_MARKER) != 0)){
			measureHeader.setMarker(readMarker(number));
		}
		
		measureHeader.setTripletFeel ((lastMeasureHeader != null)?lastMeasureHeader.getTripletFeel():TGMeasureHeader.TRIPLET_FEEL_NONE);
		if(((header & MEASURE_HEADER_TRIPLET_FEEL) != 0)){
			measureHeader.setTripletFeel( readByte() );
		}
		
		return measureHeader;
	}
	
	private TGMeasure readMeasure(TGMeasureHeader measureHeader,TGMeasure lastMeasure){
		this.velocity = TGVelocities.DEFAULT;
		
		int header = readHeader();
		
		TGMeasure measure = this.factory.newMeasure(measureHeader);
		
		//leo la cantidad de componentes
		TGBeat previous = null;
		int componentCount = readShort();
		for(int i = 0;i < componentCount;i++){
			previous = readComponent(measure,previous);
		}
		
		//leo la clave
		measure.setClef( (lastMeasure == null)?TGMeasure.CLEF_TREBLE:lastMeasure.getClef());
		if(((header & MEASURE_CLEF) != 0)){
			measure.setClef( readByte() );
		}
		
		//leo el key signature
		measure.setKeySignature((lastMeasure == null)?0:lastMeasure.getKeySignature());
		if(((header & MEASURE_KEYSIGNATURE) != 0)){
			measure.setKeySignature(readByte());
		}
		
		return measure;
		
	}
	
	private void readChannel(TGTrack track){
		int header = readHeader();
		
		//leo el canal
		track.getChannel().setChannel( (short)readByte() );
		
		//leo el canal de efectos
		track.getChannel().setEffectChannel( (short)readByte() );
		
		//leo el instrumento
		track.getChannel().setInstrument( (short)readByte() );
		
		//leo el volumen
		track.getChannel().setVolume( (short)readByte() );
		
		//leo el balance
		track.getChannel().setBalance( (short)readByte() );
		
		//leo el chorus
		track.getChannel().setChorus( (short)readByte() );
		
		//leo el reverb
		track.getChannel().setReverb( (short)readByte() );
		
		//leo el phaser
		track.getChannel().setPhaser( (short)readByte() );
		
		//leo el tremolo
		track.getChannel().setTremolo( (short)readByte() );
		
		//leo el solo
		track.setSolo( ((header & CHANNEL_SOLO) != 0) );
		
		//leo el mute
		track.setMute( ((header & CHANNEL_MUTE) != 0) );
	}
	
	private TGBeat readComponent(TGMeasure measure,TGBeat previous){
		TGBeat beat = previous;
		
		int header = readHeader();
		
		//leo el start
		if(beat == null){
			beat = this.factory.newBeat();
			beat.setStart(measure.getStart());
			measure.addBeat(beat);
		}else if(((header & COMPONENT_NEXT_BEAT) != 0)){
			beat = this.factory.newBeat();
			beat.setStart(previous.getStart() + previous.getVoice(0).getDuration().getTime());
			measure.addBeat(beat);
		}
		TGVoice voice = beat.getVoice(0);
		voice.setEmpty(false);
		
		//leo la duracion
		if(((header & COMPONENT_NEXT_DURATION) != 0)){
			readDuration(voice.getDuration());
		}else if(previous != null && !previous.equals(beat)){
			previous.getVoice(0).getDuration().copy( voice.getDuration() );
		}
		
		if(((header & COMPONENT_NOTE) != 0)){
			TGNote note = this.factory.newNote();
			
			//leo el valor
			note.setValue(readByte());
			
			//leo la cuerda
			note.setString(readByte());
			
			//leo la ligadura
			note.setTiedNote((header & COMPONENT_TIEDNOTE) != 0);
			
			//leo el velocity
			if(((header & COMPONENT_VELOCITY) != 0)){
				this.velocity = readByte();
			}
			note.setVelocity(this.velocity);
			
			//leo los efectos
			if(((header & COMPONENT_EFFECT) != 0)){
				readNoteEffect(note.getEffect());
			}
			voice.addNote(note);
		}
		return beat;
	}
	
	private TGString readInstrumentString(int number){
		TGString string = this.factory.newString();
		
		string.setNumber(number);
		
		//leo el valor
		string.setValue( readByte() );
		
		return string;
	}
	
	private void readTempo(TGTempo tempo){
		//leo el valor
		tempo.setValue(readShort());
	}
	
	private void readTimeSignature(TGTimeSignature timeSignature){
		//leo el numerador
		timeSignature.setNumerator(readByte());
		
		//leo el denominador
		readDuration(timeSignature.getDenominator());
	}
	
	private void readDuration(TGDuration duration){
		int header = readHeader();
		
		// leo el puntillo
		duration.setDotted((header & DURATION_DOTTED) != 0);
		
		//leo el doble puntillo
		duration.setDoubleDotted((header & DURATION_DOUBLE_DOTTED) != 0);
		
		//leo el valor
		duration.setValue(readByte());
		
		//leo el tipo de divisiones
		if(((header & DURATION_NO_TUPLET) != 0)){
			readDivisionType(duration.getDivision());
		}
	}
	
	private void readDivisionType(TGDivisionType divisionType){
		//leo los enters
		divisionType.setEnters(readByte());
		
		//leo los tiempos
		divisionType.setTimes(readByte());
	}
	
	private void readNoteEffect(TGNoteEffect effect){
		int header = readHeader(3);
		
		//leo el bend
		if(((header & EFFECT_BEND) != 0)){
			effect.setBend(readBendEffect());
		}
		
		//leo el tremolo bar
		if(((header & EFFECT_TREMOLO_BAR) != 0)){
			effect.setTremoloBar(readTremoloBarEffect());
		}
		
		//leo el harmonic
		if(((header & EFFECT_HARMONIC) != 0)){
			effect.setHarmonic(readHarmonicEffect());
		}
		
		//leo el grace
		if(((header & EFFECT_GRACE) != 0)){
			effect.setGrace(readGraceEffect());
		}
		
		//leo el trill
		if(((header & EFFECT_TRILL) != 0)){
			effect.setTrill(readTrillEffect());
		}
		
		//leo el tremolo picking
		if(((header & EFFECT_TREMOLO_PICKING) != 0)){
			effect.setTremoloPicking(readTremoloPickingEffect());
		}
		
		//vibrato
		effect.setVibrato(((header & EFFECT_VIBRATO) != 0));
		
		//dead note
		effect.setDeadNote(((header & EFFECT_DEAD) != 0));
		
		//slide
		effect.setSlide(((header & EFFECT_SLIDE) != 0));
		
		//hammer-on/pull-off
		effect.setHammer(((header & EFFECT_HAMMER) != 0));
		
		//ghost note
		effect.setGhostNote(((header & EFFECT_GHOST) != 0));
		
		//accentuated note
		effect.setAccentuatedNote(((header & EFFECT_ACCENTUATED) != 0));
		
		//heavy accentuated note
		effect.setHeavyAccentuatedNote(((header & EFFECT_HEAVY_ACCENTUATED) != 0));
		
		//palm mute
		effect.setPalmMute(((header & EFFECT_PALM_MUTE) != 0));
		
		//staccato
		effect.setStaccato(((header & EFFECT_STACCATO) != 0));
		
		//tapping
		effect.setTapping(((header & EFFECT_TAPPING) != 0));
		
		//slapping
		effect.setSlapping(((header & EFFECT_SLAPPING) != 0));
		
		//popping
		effect.setPopping(((header & EFFECT_POPPING) != 0));
		
		//fade in
		effect.setFadeIn(((header & EFFECT_FADE_IN) != 0));
	}
	
	private TGEffectBend readBendEffect(){
		TGEffectBend bend = this.factory.newEffectBend();
		
		//leo la cantidad de puntos
		int count = readByte();
		
		for(int i = 0;i < count;i++){
			//leo la posicion
			int position = readByte();
			
			//leo el valor
			int value = readByte();
			
			//agrego el punto
			bend.addPoint(position,value);
		}
		return bend;
	}
	
	private TGEffectTremoloBar readTremoloBarEffect(){
		TGEffectTremoloBar tremoloBar = this.factory.newEffectTremoloBar();
		
		//leo la cantidad de puntos
		int count = readByte();
		
		for(int i = 0;i < count;i++){
			//leo la posicion
			int position = readByte();
			
			//leo el valor
			int value =  (readByte() - TGEffectTremoloBar.MAX_VALUE_LENGTH);
			
			//agrego el punto
			tremoloBar.addPoint(position,value);
		}
		return tremoloBar;
	}
	
	private TGEffectHarmonic readHarmonicEffect(){
		TGEffectHarmonic harmonic = this.factory.newEffectHarmonic();
		
		//leo el tipo
		harmonic.setType(readByte());
		
		//leo la data
		if(harmonic.getType() == TGEffectHarmonic.TYPE_ARTIFICIAL){
			harmonic.setData(TGEffectHarmonic.MIN_ARTIFICIAL_OFFSET + readByte());
		}else if(harmonic.getType() == TGEffectHarmonic.TYPE_TAPPED){
			harmonic.setData(readByte());
		}
		return harmonic;
	}
	
	private TGEffectGrace readGraceEffect(){
		TGEffectGrace grace = this.factory.newEffectGrace();
		
		int header = readHeader();
		
		grace.setDead(((header & GRACE_FLAG_DEAD) != 0));
		
		grace.setOnBeat(((header & GRACE_FLAG_ON_BEAT) != 0));
		
		//leo el fret
		grace.setFret(readByte());
		
		//leo la duracion
		grace.setDuration(readByte());
		
		//leo el velocity
		grace.setDynamic(readByte());
		
		//leo la transicion
		grace.setTransition(readByte());
		
		return grace;
	}
	
	private TGEffectTremoloPicking readTremoloPickingEffect(){
		TGEffectTremoloPicking tremoloPicking = this.factory.newEffectTremoloPicking();
		
		//leo la duracion
		tremoloPicking.getDuration().setValue(readByte());
		
		return tremoloPicking;
	}
	
	private TGEffectTrill readTrillEffect(){
		TGEffectTrill trill = this.factory.newEffectTrill();
		
		//leo el fret
		trill.setFret(readByte());
		
		//leo la duracion
		trill.getDuration().setValue(readByte());
		
		return trill;
	}
	
	private TGMarker readMarker(int measure){
		TGMarker marker = this.factory.newMarker();
		
		marker.setMeasure(measure);
		
		//leo el titulo
		marker.setTitle(readString());
		
		//leo el color
		readColor(marker.getColor());
		
		return marker;
	}
	
	private void readColor(TGColor color){
		//leo el RGB
		color.setR(readShort());
		color.setG(readShort());
		color.setB(readShort());
	}
	
	private void readLyrics(TGLyric lyrics){
		//leo el compas de comienzo
		lyrics.setFrom(readShort());
		
		//leo el texto
		lyrics.setLyrics(readString());
	}
	
	private int readByte(){
		try {
			return this.dataInputStream.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private int readHeader(){
		try {
			return this.dataInputStream.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private int readHeader(int bCount){
		int header = 0;
		for(int i = bCount; i > 0; i --){
			header += ( readHeader() << ( (8 * i) - 8 ) );
		}
		return header;
	}
	
	private short readShort(){
		try {
			return this.dataInputStream.readShort();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private String readString(){
		try {
			int length = this.dataInputStream.read();
			char[] chars = new char[length];
			for(int i = 0;i < chars.length; i++){
				chars[i] = this.dataInputStream.readChar();
			}
			return String.copyValueOf(chars);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}
}
