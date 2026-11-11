package app.tuxguitar.io.tg.v13;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.base.TGSongWriterHandle;
import app.tuxguitar.io.tg.util.TGMeasureHeaderFlags;
import app.tuxguitar.io.tg.util.TGStringLimitUtil;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGChannelParameter;
import app.tuxguitar.song.models.TGChord;
import app.tuxguitar.song.models.TGColor;
import app.tuxguitar.song.models.TGDivisionType;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGLyric;
import app.tuxguitar.song.models.TGMarker;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGNoteEffect;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGStroke;
import app.tuxguitar.song.models.TGTempo;
import app.tuxguitar.song.models.TGText;
import app.tuxguitar.song.models.TGTimeSignature;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.song.models.effects.TGEffectBend;
import app.tuxguitar.song.models.effects.TGEffectBend.BendPoint;
import app.tuxguitar.song.models.effects.TGEffectGrace;
import app.tuxguitar.song.models.effects.TGEffectHarmonic;
import app.tuxguitar.song.models.effects.TGEffectTremoloBar;
import app.tuxguitar.song.models.effects.TGEffectTremoloBar.TremoloBarPoint;
import app.tuxguitar.song.models.effects.TGEffectTremoloPicking;
import app.tuxguitar.song.models.effects.TGEffectTrill;

public class TGSongWriterImpl extends TGStream implements TGSongWriter {

	private static final TGMeasureHeaderFlags MEASURE_HEADER_FLAGS = new TGMeasureHeaderFlags(
			MEASURE_HEADER_TIMESIGNATURE, MEASURE_HEADER_TEMPO, MEASURE_HEADER_REPEAT_OPEN, MEASURE_HEADER_REPEAT_CLOSE,
			MEASURE_HEADER_REPEAT_ALTERNATIVE, MEASURE_HEADER_MARKER, MEASURE_HEADER_TRIPLET_FEEL);

	private DataOutputStream dataOutputStream;

	public TGFileFormat getFileFormat(){
		return TG_FORMAT;
	}

	public void write(TGSongWriterHandle handle) throws TGFileFormatException {
		try {
			this.dataOutputStream = new DataOutputStream(handle.getOutputStream());

			this.writeVersion();
			this.write(handle.getSong());
			this.dataOutputStream.flush();
			this.dataOutputStream.close();
		}catch( Throwable throwable ){
			throw new TGFileFormatException(throwable);
		}
	}

	private void writeVersion() throws IOException{
		writeUnsignedByteString(TG_FORMAT_VERSION);
	}

	private void write(TGSong song) throws IOException{
		//escribo el nombre
		writeUnsignedByteString(song.getName());

		//escribo el artista
		writeUnsignedByteString(song.getArtist());

		//escribo el album
		writeUnsignedByteString(song.getAlbum());

		//escribo el autor
		writeUnsignedByteString(song.getAuthor());

		//escribo la fecha
		writeUnsignedByteString(song.getDate());

		//escribo el copyright
		writeUnsignedByteString(song.getCopyright());

		//escribo el creador
		writeUnsignedByteString(song.getWriter());

		//escribo el transcriptor
		writeUnsignedByteString(song.getTranscriber());

		//escribo los comentarios
		writeIntegerString(song.getComments());

		//escribo la cantidad de canales
		writeByte(song.countChannels());

		//escribo las canales
		for(int i = 0;i < song.countChannels();i++){
			writeChannel(song.getChannel(i));
		}

		//escribo la cantidad de measure headers
		writeShort((short)song.countMeasureHeaders());

		//escribo las pistas
		TGMeasureHeader lastHeader = null;
		Iterator<TGMeasureHeader> headers = song.getMeasureHeaders();
		while(headers.hasNext()){
			TGMeasureHeader header = headers.next();
			writeMeasureHeader(header,lastHeader);
			lastHeader = header;
		}

		//escribo la cantidad de pistas
		writeByte(song.countTracks());

		//escribo las pistas
		for(int i = 0;i < song.countTracks();i++){
			writeTrack(song.getTrack(i));
		}
	}

	private void writeTrack(TGTrack track) throws IOException{
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

		//escribo el id del canal
		writeShort((short)track.getChannelId());

		//escribo los compases
		TGMeasure lastMeasure = null;
		Iterator<TGMeasure> measures  = track.getMeasures();
		while(measures.hasNext()){
			TGMeasure measure = measures.next();
			writeMeasure(measure,lastMeasure);
			lastMeasure = measure;
		}

		//obtengo las cuerdas a escribir
		List<TGString> strings = TGStringLimitUtil.createWritableStrings(track);

		//escribo la cantidad de cuerdas
		writeByte(strings.size());

		//escribo las cuerdas
		Iterator<TGString> stringIt  = strings.iterator();
		while(stringIt.hasNext()){
			TGString string = stringIt.next();
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

	private void writeMeasureHeader(TGMeasureHeader measureheader,TGMeasureHeader lastMeasureHeader) throws IOException{
		int header = MEASURE_HEADER_FLAGS.create(measureheader, lastMeasureHeader);

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

	private void writeMeasure(TGMeasure measure,TGMeasure lastMeasure) throws IOException{
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

	private void writeChannel(TGChannel channel) throws IOException{
		//escribo el id
		writeShort((short)channel.getChannelId());

		//escribo el banco de sonidos
		writeByte(channel.getBank());

		//escribo el programa
		writeByte(channel.getProgram());

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

		//escribo el nombre
		writeUnsignedByteString(channel.getName());

		//escribo los parametros
		writeChannelParameters(channel);
	}

	private void writeChannelParameters(TGChannel channel) throws IOException{
		//escribo la cantidad de parametros
		writeShort((short)channel.countParameters());

		Iterator<TGChannelParameter> iterator = channel.getParameters();
		while( iterator.hasNext() ){
			writeChannelParameter(iterator.next());
		}
	}

	private void writeChannelParameter(TGChannelParameter parameter) throws IOException{
		//escribo el key
		writeUnsignedByteString(parameter.getKey());

		//escribo el value
		writeIntegerString(parameter.getValue());
	}

	private void writeBeats(TGMeasure measure,TGBeatData data) throws IOException{
		int count = measure.countBeats();
		for(int i = 0; i < count; i ++){
			TGBeat beat = measure.getBeat(i);
			writeBeat(beat,data, (i + 1 < count ));
		}
	}

	private void writeBeat(TGBeat beat,TGBeatData data, boolean hasNext) throws IOException{
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

	private void writeVoices(int header, TGBeat beat,TGBeatData data) throws IOException{
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

	private void writeNotes(TGVoice voice,TGBeatData data) throws IOException{
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

	private void writeNote(int header,TGNote note) throws IOException{
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

	private void writeStroke(TGStroke stroke) throws IOException{
		//escribo la direccion
		writeByte(stroke.getDirection());

		//escribo el valor
		writeByte(stroke.getValue());
	}

	private void writeChord(TGChord chord) throws IOException{
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

	private void writeText(TGText text) throws IOException{
		//escribo el texto
		writeUnsignedByteString(text.getValue());
	}

	private void writeInstrumentString(TGString string) throws IOException{
		//escribo el valor
		writeByte(string.getValue());
	}

	private void writeTempo(TGTempo tempo) throws IOException{
		//escribo el valor
		writeShort((short)tempo.getQuarterValue());
	}

	private void writeTimeSignature(TGTimeSignature timeSignature) throws IOException{
		//escribo el numerador
		writeByte(timeSignature.getNumerator());

		//escribo el denominador
		writeDuration(timeSignature.getDenominator());
	}

	private void writeDuration(TGDuration duration) throws IOException{
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

	private void writeDivisionType(TGDivisionType divisionType) throws IOException{
		//escribo los enters
		writeByte(divisionType.getEnters());

		//escribo los tiempos
		writeByte(divisionType.getTimes());
	}

	private void writeNoteEffect(TGNoteEffect effect) throws IOException{
		int header = createNoteEffectHeader(effect);

		writeHeader(header,3);

		writeNoteEffectData(header, effect);
	}

	private int createNoteEffectHeader(TGNoteEffect effect){
		int header = 0;

		header |= flag(effect.isBend(), EFFECT_BEND);
		header |= flag(effect.isTremoloBar(), EFFECT_TREMOLO_BAR);
		header |= flag(effect.isHarmonic(), EFFECT_HARMONIC);
		header |= flag(effect.isGrace(), EFFECT_GRACE);
		header |= flag(effect.isTrill(), EFFECT_TRILL);
		header |= flag(effect.isTremoloPicking(), EFFECT_TREMOLO_PICKING);
		header |= flag(effect.isVibrato(), EFFECT_VIBRATO);
		header |= flag(effect.isDeadNote(), EFFECT_DEAD);
		header |= flag(effect.isSlide(), EFFECT_SLIDE);
		header |= flag(effect.isHammer(), EFFECT_HAMMER);
		header |= flag(effect.isGhostNote(), EFFECT_GHOST);
		header |= flag(effect.isAccentuatedNote(), EFFECT_ACCENTUATED);
		header |= flag(effect.isHeavyAccentuatedNote(), EFFECT_HEAVY_ACCENTUATED);
		header |= flag(effect.isPalmMute(), EFFECT_PALM_MUTE);
		header |= flag(effect.isStaccato(), EFFECT_STACCATO);
		header |= flag(effect.isTapping(), EFFECT_TAPPING);
		header |= flag(effect.isSlapping(), EFFECT_SLAPPING);
		header |= flag(effect.isPopping(), EFFECT_POPPING);
		header |= flag(effect.isFadeIn(), EFFECT_FADE_IN);
		header |= flag(effect.isLetRing(), EFFECT_LET_RING);

		return header;
	}

	private static int flag(boolean enabled, int bit){
		return (enabled ? bit : 0);
	}

	private void writeNoteEffectData(int header, TGNoteEffect effect) throws IOException{
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

	private void writeBendEffect(TGEffectBend effect) throws IOException{
		//escribo la cantidad de puntos
		writeByte(effect.getPoints().size());

		Iterator<BendPoint> it = effect.getPoints().iterator();
		while(it.hasNext()){
			TGEffectBend.BendPoint point = it.next();

			//escribo la posicion
			writeByte(point.getPosition());

			//escribo el valor
			writeByte(point.getValue());
		}
	}

	private void writeTremoloBarEffect(TGEffectTremoloBar effect) throws IOException{
		//escribo la cantidad de puntos
		writeByte(effect.getPoints().size());

		Iterator<TremoloBarPoint> it = effect.getPoints().iterator();
		while(it.hasNext()){
			TGEffectTremoloBar.TremoloBarPoint point = it.next();

			//escribo la posicion
			writeByte(point.getPosition());

			//escribo el valor
			writeByte( (point.getValue() + TGEffectTremoloBar.MAX_VALUE_LENGTH) );
		}
	}

	private void writeHarmonicEffect(TGEffectHarmonic effect) throws IOException{
		//excribo el tipo
		writeByte(effect.getType());

		//excribo la data
		if(effect.getType() != TGEffectHarmonic.TYPE_NATURAL){
			writeByte(effect.getData());
		}
	}

	private void writeGraceEffect(TGEffectGrace effect) throws IOException{
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

	private void writeTremoloPickingEffect(TGEffectTremoloPicking effect) throws IOException{
		//excribo la duracion
		writeByte(effect.getDuration().getValue());
	}

	private void writeTrillEffect(TGEffectTrill effect) throws IOException{
		//excribo el fret
		writeByte(effect.getFret());

		//excribo la duracion
		writeByte(effect.getDuration().getValue());
	}

	private void writeMarker(TGMarker marker) throws IOException{
		//escribo el titulo
		writeUnsignedByteString(marker.getTitle());

		//escribo el color
		writeRGBColor(marker.getColor());
	}

	private void writeRGBColor(TGColor color) throws IOException{
		//escribo el RGB
		writeByte(color.getR());
		writeByte(color.getG());
		writeByte(color.getB());
	}

	private void writeLyrics(TGLyric lyrics) throws IOException{
		//escribo el compas de comienzo
		writeShort((short)lyrics.getFrom());

		//escribo el texto
		writeIntegerString(lyrics.getLyrics());
	}

	public void writeByte(int v) throws IOException{
		this.dataOutputStream.write(v);
	}

	private void writeUnsignedByteString(String v) throws IOException{
		String byteString = (v == null ? new String() : ((v.length() > 0xFF)?v.substring(0, 0xFF):v) );
		this.dataOutputStream.write(byteString.length());
		this.dataOutputStream.writeChars(byteString);
	}

	private void writeIntegerString(String v) throws IOException{
		this.dataOutputStream.writeInt(v.length());
		this.dataOutputStream.writeChars(v);
	}

	public void writeHeader(int v) throws IOException{
		this.dataOutputStream.write(v);
	}

	public void writeHeader(int v,int bCount) throws IOException{
		for(int i = bCount; i > 0; i --){
			writeHeader( (v >>> ( (8 * i) - 8 ) )  &  0xFF);
		}
	}

	public void writeShort(short v) throws IOException{
		this.dataOutputStream.writeShort(v);
	}
}
