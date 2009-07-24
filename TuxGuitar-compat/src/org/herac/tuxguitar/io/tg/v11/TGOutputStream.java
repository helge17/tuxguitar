/*
 * Created on 16-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.io.tg.v11;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.song.factory.TGFactory;
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
import org.herac.tuxguitar.song.models.TGDivisionType;
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
public class TGOutputStream extends TGStream implements TGLocalFileExporter{
	
	private DataOutputStream dataOutputStream;
	
	public boolean isSupportedExtension(String extension) {
		return (extension.toLowerCase().equals(TG_FORMAT_EXTENSION));
	}
	
	public String getExportName(){
		return "TuxGuitar 1.1";
	}
	
	public TGFileFormat getFileFormat(){
		return new TGFileFormat("TuxGuitar","*.tg");
	}
	
	public boolean configure(boolean setDefaults){
		return true;
	}
	
	public void init(TGFactory factory,OutputStream stream){
		this.dataOutputStream = new DataOutputStream(stream);
	}
	
	public void exportSong(TGSong song) throws TGFileFormatException {
		try{
			this.writeVersion();
			this.write(song);
			this.dataOutputStream.flush();
			this.dataOutputStream.close();
		}catch( Throwable throwable){
			throw new TGFileFormatException(throwable);
		}
	}
	
	private void writeVersion(){
		writeUnsignedByteString(TG_FORMAT_VERSION);
	}
	
	private void write(TGSong song){
		//escribo el nombre
		writeUnsignedByteString(song.getName());
		
		//escribo el artista
		writeUnsignedByteString(song.getArtist());
		
		//escribo el album
		writeUnsignedByteString(song.getAlbum());
		
		//escribo el autor
		writeUnsignedByteString(song.getAuthor());
		
		//escribo la cantidad de measure headers 
		writeShort((short)song.countMeasureHeaders());
		
		//escribo las pistas
		TGMeasureHeader lastHeader = null;
		Iterator headers = song.getMeasureHeaders();
		while(headers.hasNext()){
			TGMeasureHeader header = (TGMeasureHeader)headers.next();
			writeMeasureHeader(header,lastHeader);
			lastHeader = header;
		}
		
		//escribo la cantidad de pistas
		writeByte(song.countTracks());
		
		//escribo las pistas
		for(int i = 0;i < song.countTracks();i++){
			TGTrack track = song.getTrack(i);
			writeTrack(track);
		}
	}
	
	private void writeTrack(TGTrack track){
		//header
		int header = 0;
		if (track.isSolo()) {
			header |= TRACK_SOLO;
		}
		if (track.isMute()) {
			header |= TRACK_MUTE;
		}
		if(!track.getLyrics().isEmpty()){
			header |= TRACK_LYRICS;
		}
		writeHeader(header);
		
		//escribo el nombre
		writeUnsignedByteString(track.getName());
		
		//escribo el canal
		writeChannel(track.getChannel());
		
		//escribo los compases
		TGMeasure lastMeasure = null;
		Iterator measures  = track.getMeasures();
		while(measures.hasNext()){
			TGMeasure measure = (TGMeasure)measures.next();
			writeMeasure(measure,lastMeasure);
			lastMeasure = measure;
		}
		
		//escribo la cantidad de cuerdas 
		writeByte(track.getStrings().size());
		
		//escribo las cuerdas
		Iterator stringIt  = track.getStrings().iterator();
		while(stringIt.hasNext()){
			TGString string = (TGString)stringIt.next();
			writeInstrumentString(string);
		}
		
		//escribo el offset
		writeByte(track.getOffset() - TGTrack.MIN_OFFSET);
		
		//escribo el color
		writeRGBColor(track.getColor());
		
		//escribo el lyrics
		if(((header & TRACK_LYRICS) != 0)){
			writeLyrics(track.getLyrics());
		}
	}
	
	private void writeMeasureHeader(TGMeasureHeader measureheader,TGMeasureHeader lastMeasureHeader){
		int header = 0;
		if(lastMeasureHeader == null){
			header |= MEASURE_HEADER_TIMESIGNATURE;
			header |= MEASURE_HEADER_TEMPO;
			if(measureheader.getTripletFeel() != TGMeasureHeader.TRIPLET_FEEL_NONE){
				header |= MEASURE_HEADER_TRIPLET_FEEL;
			}
		}else{
			//Time Signature
			int numerator = measureheader.getTimeSignature().getNumerator();
			int value = measureheader.getTimeSignature().getDenominator().getValue();
			int prevNumerator = lastMeasureHeader.getTimeSignature().getNumerator();
			int prevValue = lastMeasureHeader.getTimeSignature().getDenominator().getValue();
			if(numerator != prevNumerator || value != prevValue){
				header |= MEASURE_HEADER_TIMESIGNATURE;
			}
			//Tempo
			if(measureheader.getTempo().getValue() != lastMeasureHeader.getTempo().getValue()){
				header |= MEASURE_HEADER_TEMPO;
			}
			//Triplet Feel
			if(measureheader.getTripletFeel() != lastMeasureHeader.getTripletFeel()){
				header |= MEASURE_HEADER_TRIPLET_FEEL;
			}
		}
		header = (measureheader.isRepeatOpen())?header |= MEASURE_HEADER_REPEAT_OPEN:header;
		header = (measureheader.getRepeatClose() > 0)?header |= MEASURE_HEADER_REPEAT_CLOSE:header;
		header = (measureheader.getRepeatAlternative() > 0)?header |= MEASURE_HEADER_REPEAT_ALTERNATIVE:header;
		header = (measureheader.hasMarker())?header |= MEASURE_HEADER_MARKER:header;
		
		writeHeader(header);
		
		//escribo el timeSignature
		if(((header & MEASURE_HEADER_TIMESIGNATURE) != 0)){
			writeTimeSignature(measureheader.getTimeSignature());
		}
		
		//escribo el tempo
		if(((header & MEASURE_HEADER_TEMPO) != 0)){
			writeTempo(measureheader.getTempo());
		}
		
		//escribo el numero de repeticiones
		if(((header & MEASURE_HEADER_REPEAT_CLOSE) != 0)){
			writeShort((short)measureheader.getRepeatClose());
		}
		
		//escribo los finales alternativos
		if(((header & MEASURE_HEADER_REPEAT_ALTERNATIVE) != 0)){
			writeByte(measureheader.getRepeatAlternative());
		}
		
		//escribo el marker
		if(((header & MEASURE_HEADER_MARKER) != 0)){
			writeMarker(measureheader.getMarker());
		}
		
		//escribo el triplet feel
		if(((header & MEASURE_HEADER_TRIPLET_FEEL) != 0)){
			writeByte(measureheader.getTripletFeel());
		}
	}
	
	private void writeMeasure(TGMeasure measure,TGMeasure lastMeasure){
		int header = 0;
		if(lastMeasure == null){
			header |= MEASURE_CLEF;
			header |= MEASURE_KEYSIGNATURE;
		}else{
			//Clef
			if(measure.getClef() != lastMeasure.getClef()){
				header |= MEASURE_CLEF;
			}
			//KeySignature
			if(measure.getKeySignature() != lastMeasure.getKeySignature()){
				header |= MEASURE_KEYSIGNATURE;
			}
		}
		//escribo la cabecera
		writeHeader(header);
		
		//escribo los beats
		TGBeatData data = new TGBeatData(measure);
		writeBeats(measure, data);
		
		//escribo la clave
		if(((header & MEASURE_CLEF) != 0)){
			writeByte(measure.getClef());
		}
		
		//escribo el key signature
		if(((header & MEASURE_KEYSIGNATURE) != 0)){
			writeByte(measure.getKeySignature());
		}
	}
	
	private void writeChannel(TGChannel channel){
		//escribo el canal
		writeByte(channel.getChannel());
		
		//escribo el canal de efectos
		writeByte(channel.getEffectChannel());
		
		//escribo el instrumento
		writeByte(channel.getInstrument());
		
		//escribo el volumen
		writeByte(channel.getVolume());
		
		//escribo el balance
		writeByte(channel.getBalance());
		
		//escribo el chorus
		writeByte(channel.getChorus());
		
		//escribo el reverb
		writeByte(channel.getReverb());
		
		//escribo el phaser
		writeByte(channel.getPhaser());
		
		//escribo el tremolo
		writeByte(channel.getTremolo());
	}
	
	private void writeBeats(TGMeasure measure,TGBeatData data){
		int count = measure.countBeats();
		for(int i = 0; i < count; i ++){
			TGBeat beat = measure.getBeat(i);
			writeBeat(beat,data, (i + 1 < count ));
		}
	}
	
	private void writeBeat(TGBeat beat,TGBeatData data, boolean hasNext){
		int header = hasNext ? BEAT_HAS_NEXT : 0;
		
		//Berifico si hay cambios en las voces
		for(int i = 0 ; i < TGBeat.MAX_VOICES; i ++ ){
			int shift = (i * 2 );
			if(!beat.getVoice(i).isEmpty()){
				header |= ( BEAT_HAS_VOICE << shift ); 
				
				int flags = ( beat.getVoice(i).isRestVoice() ? 0 : VOICE_HAS_NOTES );
				if(!beat.getVoice(i).getDuration().isEqual(data.getVoice(i).getDuration())){
					flags |= VOICE_NEXT_DURATION;
					data.getVoice(i).setDuration(beat.getVoice(i).getDuration());
				}
				if(beat.getVoice(i).getDirection() != TGVoice.DIRECTION_NONE ){
					if(beat.getVoice(i).getDirection() == TGVoice.DIRECTION_UP ){
						flags |= VOICE_DIRECTION_UP;
					}
					else if(beat.getVoice(i).getDirection() == TGVoice.DIRECTION_DOWN ){
						flags |= VOICE_DIRECTION_DOWN;
					}
				}
				if( data.getVoice(i).getFlags() != flags ){
					header |= ( BEAT_HAS_VOICE_CHANGES << shift ); 
					data.getVoice(i).setFlags( flags );
				}
			}
			
		}
		//Berifico si tiene stroke
		if(beat.getStroke().getDirection() != TGStroke.STROKE_NONE){
			header |= BEAT_HAS_STROKE;
		}
		//Berifico si tiene acorde
		if(beat.getChord() != null){
			header |= BEAT_HAS_CHORD;
		}
		//Berifico si tiene texto
		if(beat.getText() != null){
			header |= BEAT_HAS_TEXT;
		}
		
		// escribo la cabecera
		writeHeader(header);
		
		//escribo las voces
		writeVoices(header, beat, data);
		
		//escribo el stroke
		if(((header & BEAT_HAS_STROKE) != 0)){
			writeStroke(beat.getStroke());
		}
		
		//escribo el acorde
		if(((header & BEAT_HAS_CHORD) != 0)){
			writeChord(beat.getChord());
		}
		
		//escribo el texto
		if(((header & BEAT_HAS_TEXT) != 0)){
			writeText(beat.getText());
		}
	}
	
	private void writeVoices(int header, TGBeat beat,TGBeatData data){
		for(int i = 0 ; i < TGBeat.MAX_VOICES; i ++ ){
			int shift = (i * 2 );
			if((( header & (BEAT_HAS_VOICE << shift)) != 0)){
				
				if(((header & (BEAT_HAS_VOICE_CHANGES << shift)) != 0)){
					writeHeader( data.getVoice(i).getFlags() );
				}
				
				//escribo la duracion
				if((( data.getVoice(i).getFlags() & VOICE_NEXT_DURATION) != 0)){
					writeDuration(beat.getVoice(i).getDuration());
				}
				
				//escribo las notas
				if((( data.getVoice(i).getFlags() & VOICE_HAS_NOTES) != 0)){
					writeNotes(beat.getVoice(i), data);
				}
			}
		}
	}
	
	private void writeNotes(TGVoice voice,TGBeatData data){
		for( int i = 0 ; i < voice.countNotes() ; i ++){
			TGNote note = voice.getNote(i);
			
			int header = ( i + 1 < voice.countNotes() ? NOTE_HAS_NEXT : 0 );
			header = (note.isTiedNote())?header |= NOTE_TIED:header;
			if(note.getVelocity() != data.getVoice(voice.getIndex()).getVelocity()){
				data.getVoice(voice.getIndex()).setVelocity(note.getVelocity());
				header |= NOTE_VELOCITY;
			}
			header = (note.getEffect().hasAnyEffect())?header |= NOTE_EFFECT:header;
			
			writeHeader(header);
			
			writeNote(header,note);
		}
	}
	
	private void writeNote(int header,TGNote note){
		//escribo el valor
		writeByte(note.getValue());
		
		//escribo la cuerda
		writeByte(note.getString());
		
		//escribo el velocity
		if(((header & NOTE_VELOCITY) != 0)){
			writeByte(note.getVelocity());
		}
		
		//escribo los efectos
		if(((header & NOTE_EFFECT) != 0)){
			writeNoteEffect(note.getEffect());
		}
	}
	
	private void writeStroke(TGStroke stroke){
		//escribo la direccion
		writeByte(stroke.getDirection());
		
		//escribo el valor
		writeByte(stroke.getValue());
	}
	
	private void writeChord(TGChord chord){
		//escribo la cantidad de cuerdas
		writeByte(chord.countStrings());
		
		//escribo el nombre
		writeUnsignedByteString(chord.getName());
		
		//escribo el primer fret
		writeByte(chord.getFirstFret());
		
		//escribo el valor de cada cuerda
		for(int string = 0; string < chord.countStrings(); string ++){
			writeByte(chord.getFretValue(string));
		}
	}
	
	private void writeText(TGText text){
		//escribo el texto
		writeUnsignedByteString(text.getValue());
	}
	
	private void writeInstrumentString(TGString string){
		//escribo el valor
		writeByte(string.getValue());
	}
	
	private void writeTempo(TGTempo tempo){
		//escribo el valor
		writeShort((short)tempo.getValue());
	}
	
	private void writeTimeSignature(TGTimeSignature timeSignature){
		//escribo el numerador
		writeByte(timeSignature.getNumerator());
		
		//escribo el denominador
		writeDuration(timeSignature.getDenominator());
	}
	
	private void writeDuration(TGDuration duration){
		int header = 0;
		header = (duration.isDotted())?header |= DURATION_DOTTED:header;
		header = (duration.isDoubleDotted())?header |= DURATION_DOUBLE_DOTTED:header;
		header = (!duration.getDivision().isEqual(TGDivisionType.NORMAL))?header |= DURATION_NO_TUPLET:header;
		writeHeader(header);
		
		//escribo el valor
		writeByte(duration.getValue());
		
		//escribo el tipo de divisiones
		if(((header & DURATION_NO_TUPLET) != 0)){
			writeDivisionType(duration.getDivision());
		}
	}
	
	private void writeDivisionType(TGDivisionType divisionType){
		//escribo los enters
		writeByte(divisionType.getEnters());
		
		//escribo los tiempos
		writeByte(divisionType.getTimes());
	}
	
	private void writeNoteEffect(TGNoteEffect effect){
		int header = 0;
		
		header = (effect.isBend())?header |= EFFECT_BEND:header;
		header = (effect.isTremoloBar())?header |= EFFECT_TREMOLO_BAR:header;
		header = (effect.isHarmonic())?header |= EFFECT_HARMONIC:header;
		header = (effect.isGrace())?header |= EFFECT_GRACE:header;
		header = (effect.isTrill())?header |= EFFECT_TRILL:header;
		header = (effect.isTremoloPicking())?header |= EFFECT_TREMOLO_PICKING:header;
		header = (effect.isVibrato())?header |= EFFECT_VIBRATO:header;
		header = (effect.isDeadNote())?header |= EFFECT_DEAD:header;
		header = (effect.isSlide())?header |= EFFECT_SLIDE:header;
		header = (effect.isHammer())?header |= EFFECT_HAMMER:header;
		header = (effect.isGhostNote())?header |= EFFECT_GHOST:header;
		header = (effect.isAccentuatedNote())?header |= EFFECT_ACCENTUATED:header;
		header = (effect.isHeavyAccentuatedNote())?header |= EFFECT_HEAVY_ACCENTUATED:header;
		header = (effect.isPalmMute())?header |= EFFECT_PALM_MUTE:header;
		header = (effect.isStaccato())?header |= EFFECT_STACCATO:header;
		header = (effect.isTapping())?header |= EFFECT_TAPPING:header;
		header = (effect.isSlapping())?header |= EFFECT_SLAPPING:header;
		header = (effect.isPopping())?header |= EFFECT_POPPING:header;
		header = (effect.isFadeIn())?header |= EFFECT_FADE_IN:header;
		
		writeHeader(header,3);
		
		//escribo el bend
		if(((header & EFFECT_BEND) != 0)){
			writeBendEffect(effect.getBend());
		}
		
		//leo el tremolo bar
		if(((header & EFFECT_TREMOLO_BAR) != 0)){
			writeTremoloBarEffect(effect.getTremoloBar());
		}
		
		//leo el harmonic
		if(((header & EFFECT_HARMONIC) != 0)){
			writeHarmonicEffect(effect.getHarmonic());
		}
		
		//leo el grace
		if(((header & EFFECT_GRACE) != 0)){
			writeGraceEffect(effect.getGrace());
		}
		
		//leo el trill
		if(((header & EFFECT_TRILL) != 0)){
			writeTrillEffect(effect.getTrill());
		}
		
		//leo el tremolo picking
		if(((header & EFFECT_TREMOLO_PICKING) != 0)){
			writeTremoloPickingEffect(effect.getTremoloPicking());
		}
	}
	
	private void writeBendEffect(TGEffectBend effect){
		//escribo la cantidad de puntos
		writeByte(effect.getPoints().size());
		
		Iterator it = effect.getPoints().iterator();
		while(it.hasNext()){
			TGEffectBend.BendPoint point = (TGEffectBend.BendPoint)it.next();
			
			//escribo la posicion
			writeByte(point.getPosition());
			
			//escribo el valor
			writeByte(point.getValue());
		}
	}
	
	private void writeTremoloBarEffect(TGEffectTremoloBar effect){
		//escribo la cantidad de puntos
		writeByte(effect.getPoints().size());
		
		Iterator it = effect.getPoints().iterator();
		while(it.hasNext()){
			TGEffectTremoloBar.TremoloBarPoint point = (TGEffectTremoloBar.TremoloBarPoint)it.next();
			
			//escribo la posicion
			writeByte(point.getPosition());
			
			//escribo el valor
			writeByte( (point.getValue() + TGEffectTremoloBar.MAX_VALUE_LENGTH) );
		}
	}
	
	private void writeHarmonicEffect(TGEffectHarmonic effect){
		//excribo el tipo
		writeByte(effect.getType());
		
		//excribo la data
		if(effect.getType() != TGEffectHarmonic.TYPE_NATURAL){
			writeByte(effect.getData());
		}
	}
	
	private void writeGraceEffect(TGEffectGrace effect){
		int header = 0;
		header = (effect.isDead())?header |= GRACE_FLAG_DEAD:header;
		header = (effect.isOnBeat())?header |= GRACE_FLAG_ON_BEAT:header;
		
		//excribo el header
		writeHeader(header);
		
		//excribo el fret
		writeByte(effect.getFret());
		
		//excribo la duracion
		writeByte(effect.getDuration());
		
		//excribo el velocity
		writeByte(effect.getDynamic());
		
		//excribo la transicion
		writeByte(effect.getTransition());
	}
	
	private void writeTremoloPickingEffect(TGEffectTremoloPicking effect){
		//excribo la duracion
		writeByte(effect.getDuration().getValue());
	}
	
	private void writeTrillEffect(TGEffectTrill effect){
		//excribo el fret
		writeByte(effect.getFret());
		
		//excribo la duracion
		writeByte(effect.getDuration().getValue());
	}
	
	private void writeMarker(TGMarker marker){
		//escribo el titulo
		writeUnsignedByteString(marker.getTitle());
		
		//escribo el color
		writeRGBColor(marker.getColor());
	}
	
	private void writeRGBColor(TGColor color){
		//escribo el RGB
		writeByte(color.getR());
		writeByte(color.getG());
		writeByte(color.getB());
	}
	
	private void writeLyrics(TGLyric lyrics){
		//escribo el compas de comienzo
		writeShort((short)lyrics.getFrom());
		
		//escribo el texto
		writeIntegerString(lyrics.getLyrics());
	}
	
	public void writeByte(int v){
		try {
			this.dataOutputStream.write(v);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeUnsignedByteString(String v){
		try {
			String byteString = (v == null ? new String() : ((v.length() > 0xFF)?v.substring(0, 0xFF):v) );
			this.dataOutputStream.write(byteString.length());
			this.dataOutputStream.writeChars(byteString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeIntegerString(String v){
		try {
			this.dataOutputStream.writeInt(v.length());
			this.dataOutputStream.writeChars(v);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeHeader(int v){
		try {
			this.dataOutputStream.write(v);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeHeader(int v,int bCount){
		for(int i = bCount; i > 0; i --){
			writeHeader( (v >>> ( (8 * i) - 8 ) )  &  0xFF);
		}
	}
	
	public void writeShort(short v){
		try {
			this.dataOutputStream.writeShort(v);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
