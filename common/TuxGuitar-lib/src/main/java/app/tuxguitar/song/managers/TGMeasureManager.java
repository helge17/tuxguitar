package app.tuxguitar.song.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import app.tuxguitar.song.helpers.TGMeasureError;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChord;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGPickStroke;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGStroke;
import app.tuxguitar.song.models.TGText;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.song.models.effects.TGEffectBend;
import app.tuxguitar.song.models.effects.TGEffectGrace;
import app.tuxguitar.song.models.effects.TGEffectHarmonic;
import app.tuxguitar.song.models.effects.TGEffectTremoloBar;
import app.tuxguitar.song.models.effects.TGEffectTremoloPicking;
import app.tuxguitar.song.models.effects.TGEffectTrill;

public class TGMeasureManager {
	
	private TGSongManager songManager;

	public TGMeasureManager(TGSongManager songManager){
		this.songManager = songManager;
	}

	public TGSongManager getSongManager(){
		return this.songManager;
	}

	public void orderBeats(TGMeasure measure){
		Collections.sort(measure.getBeats());
	}

	/**
	 * Agrega un beat al compas
	 */
	public void addBeat(TGMeasure measure,TGBeat beat){
		//Agrego el beat
		measure.addBeat(beat);
	}

	public void removeBeat(TGBeat beat){
		beat.getMeasure().removeBeat(beat);
	}

	public void removeBeat(TGMeasure measure,long start,boolean moveNextComponents){
		TGBeat beat = getBeat(measure, start);
		if(beat != null){
			removeBeat(beat, moveNextComponents);
		}
	}

	/**
	 * Elimina un silencio del compas.
	 * si se asigna moveNextComponents = true. los componentes que le siguen
	 * se moveran para completar el espacio vacio que dejo el silencio
	 */
	public void removeBeat(TGBeat beat,boolean moveNextBeats){
		TGMeasure measure = beat.getMeasure();

		removeBeat(beat);
		if(moveNextBeats){
			TGDuration minimumDuration = getMinimumDuration(beat);
			long preciseStart = beat.getPreciseStart();
			long preciseLength = (minimumDuration != null ? minimumDuration.getPreciseTime() : 0);
			
			TGBeat next = getNextBeat(measure.getBeats(),beat);
			if(next != null){
				preciseLength = next.getPreciseStart() - preciseStart;
			}
			moveBeatsInMeasurePrecise(beat.getMeasure(),preciseStart + preciseLength,-preciseLength, minimumDuration);
		}
	}

	public void removeEmptyBeats(TGMeasure measure){
		List<TGBeat> beats = new ArrayList<TGBeat>();
		Iterator<TGBeat> it = measure.getBeats().iterator();
		while(it.hasNext()){
			TGBeat beat = it.next();
			boolean emptyBeat = true;
			for( int v = 0; v < beat.countVoices() ; v ++){
				TGVoice voice = beat.getVoice( v );
				if(!voice.isEmpty()){
					emptyBeat = false;
				}
			}
			if( emptyBeat ){
				beats.add( beat );
			}
		}

		it = beats.iterator();
		while(it.hasNext()){
			TGBeat beat = it.next();
			this.removeBeat( beat );
		}
	}

	public void removeBeatsBeforeEnd(TGMeasure measure,long fromStart){
		List<TGBeat> beats = getBeatsBeforeEnd( measure.getBeats() , fromStart);
		Iterator<TGBeat> it = beats.iterator();
		while(it.hasNext()){
			TGBeat beat =  it.next();
			removeBeat(beat);
		}
	}

	public void removeBeatsBetween(TGMeasure measure,long p1, long p2){
		List<TGBeat> beats = getBeatsBeetween( measure.getBeats() , p1, p2 );
		Iterator<TGBeat> it = beats.iterator();
		while(it.hasNext()){
			TGBeat beat =  it.next();
			removeBeat(beat);
		}
	}

	public void addNote(TGMeasure measure,long start, TGNote note, TGDuration duration, int voice){
		TGBeat beat = getBeatIn(measure, start);
		if( beat != null ){
			addNote(beat, note, duration, start, voice);
		}
	}

	public void addNote(TGBeat beat, TGNote note, TGDuration duration, int voice){
		addNote(beat, note, duration, beat.getStart(),voice);
	}

	public void addNote(TGBeat beat, TGNote note, TGDuration duration, long start, int voice){
		boolean emptyVoice = beat.getVoice( voice ).isEmpty();
		if( emptyVoice ){
			beat.getVoice( voice ).setEmpty( false );
		}

		//Verifico si entra en el compas
		if(validateDuration(beat.getMeasure(),beat, voice, duration,true,true)){
			//Borro lo que haya en la misma posicion
			for( int v = 0 ; v < beat.countVoices() ; v ++ ){
				removeNote(beat.getMeasure(),beat.getStart(), v, note.getString(), false);
			}

			beat.getVoice(voice).getDuration().copyFrom(duration);

			//trato de agregar un silencio similar al lado
			tryChangeSilenceAfter(beat.getMeasure(),beat.getVoice(voice));

			// Despues de cambiar la duracion, verifico si hay un beat mejor para agregar la nota.
			TGVoice realVoice = beat.getVoice(voice);
			if(realVoice.getBeat().getStart() != start){
				TGVoice beatIn = getVoiceIn(realVoice.getBeat().getMeasure(), start, voice);
				if( beatIn != null ) {
					realVoice = beatIn;
				}
			}
			realVoice.addNote(note);
		}else{
			beat.getVoice( voice ).setEmpty( emptyVoice );
		}
	}

	public void addNoteWithoutControl(TGBeat beat, TGNote note, TGDuration duration, int voiceIndex) {
		TGVoice voice = beat.getVoice(voiceIndex);
		boolean emptyVoice = voice.isEmpty();
		if(emptyVoice){
			voice.setEmpty( false );
		}
		// delete other notes on the same string
		for( int v = 0 ; v < beat.countVoices() ; v ++ ){
			removeNote(beat.getMeasure(),beat.getStart(), v, note.getString(), false);
		}
		voice.getDuration().copyFrom(duration);
		voice.addNote(note);
	}

	public void removeNote(TGNote note, boolean checkRestBeat){
		TGVoice voice = note.getVoice();
		if( voice != null ){
			// Remove the note
			voice.removeNote(note);

			TGBeat beat = voice.getBeat();
			if(checkRestBeat && beat.isRestBeat()){
				//Anulo un posible stroke
				beat.getStroke().setDirection( TGStroke.STROKE_NONE );
				beat.getPickStroke().setDirection( TGPickStroke.PICK_STROKE_NONE );

				//Borro un posible acorde
				if( beat.getMeasure() != null ){
					removeChord(beat.getMeasure(), beat.getStart());
				}
			}
		}
	}

	public void removeNote(TGNote note){
		this.removeNote(note, true);
	}

	public void removeNote(TGMeasure measure,long start, int voiceIndex,int string){
		this.removeNote(measure, start, voiceIndex, string, true);
	}

	/**
	 * Elimina los Componentes que empiecen en Start y esten en la misma cuerda
	 * Si hay un Silencio lo borra sin importar la cuerda
	 */
	public void removeNote(TGMeasure measure,long start, int voiceIndex,int string, boolean checkRestBeat){
		TGBeat beat = getBeat(measure, start);
		if(beat != null){
			TGVoice voice = beat.getVoice(voiceIndex);
			for( int i = 0; i < voice.countNotes(); i ++){
				TGNote note = voice.getNote(i);
				if(note.getString() == string){
					removeNote(note , checkRestBeat);
					return;
				}
			}
		}
	}

	public void removeNotesAfterString(TGMeasure measure,int string){
		List<TGNote> notesToRemove = new ArrayList<TGNote>();

		Iterator<TGBeat> beats = measure.getBeats().iterator();
		while(beats.hasNext()){
			TGBeat beat = beats.next();
			for(int v = 0; v < beat.countVoices(); v ++){
				TGVoice voice = beat.getVoice( v );
				Iterator<TGNote> notes = voice.getNotes().iterator();
				while(notes.hasNext()){
					TGNote note = notes.next();
					if(note.getString() > string){
						notesToRemove.add(note);
					}
				}
			}
		}
		Iterator<TGNote> it = notesToRemove.iterator();
		while(it.hasNext()){
			TGNote note = it.next();
			removeNote(note);
		}
	}

	public void removeNotesAfterFret(TGMeasure measure,int fret){
		List<TGNote> notesToRemove = new ArrayList<TGNote>();

		Iterator<TGBeat> beats = measure.getBeats().iterator();
		while(beats.hasNext()){
			TGBeat beat = beats.next();
			for(int v = 0; v < beat.countVoices(); v ++){
				TGVoice voice = beat.getVoice( v );
				Iterator<TGNote> notes = voice.getNotes().iterator();
				while(notes.hasNext()){
					TGNote note = notes.next();
					if(note.getValue() > fret){
						notesToRemove.add(note);
					}
				}
			}
		}
		Iterator<TGNote> it = notesToRemove.iterator();
		while(it.hasNext()){
			TGNote note = it.next();
			removeNote(note);
		}
	}

	/**
	 * Retorna Todas las Notas en la posicion Start
	 */
	public List<TGNote> getNotes(TGMeasure measure,long start){
		List<TGNote> notes = new ArrayList<TGNote>();

		TGBeat beat = getBeat(measure, start);
		if(beat != null){
			for(int v = 0 ; v < beat.countVoices(); v ++){
				TGVoice voice = beat.getVoice( v );
				Iterator<TGNote> it = voice.getNotes().iterator();
				while(it.hasNext()){
					TGNote note = it.next();
					notes.add(note);
				}
			}
		}
		return notes;
	}

	/**
	 * Retorna Todas las Notas en el pulso
	 */
	public List<TGNote> getNotes(TGBeat beat){
		List<TGNote> notes = new ArrayList<TGNote>();

		if(beat != null){
			for(int v = 0 ; v < beat.countVoices(); v ++){
				TGVoice voice = beat.getVoice( v );
				if( !voice.isEmpty() && !voice.isRestVoice() ){
					Iterator<TGNote> it = voice.getNotes().iterator();
					while(it.hasNext()){
						TGNote note = it.next();
						notes.add(note);
					}
				}
			}
		}
		return notes;
	}

	/**
	 * Retorna la Nota en la posicion y cuerda
	 */
	public TGNote getNote(TGMeasure measure,long start,int string) {
		TGBeat beat = getBeat(measure, start);
		if(beat != null){
			return getNote(beat, string);
		}
		return null;
	}

	/**
	 * Retorna la Nota en la cuerda
	 */
	public TGNote getNote(TGBeat beat,int string) {
		for( int v = 0; v < beat.countVoices(); v ++){
			TGVoice voice = beat.getVoice(v);
			if(!voice.isEmpty()){
				TGNote note = getNote(voice, string);
				if(note != null){
					return note;
				}
			}
		}
		return null;
	}

	public TGNote getNote(TGVoice voice,int string) {
		Iterator<TGNote> it = voice.getNotes().iterator();
		while(it.hasNext()){
			TGNote note = it.next();
			if (note.getString() == string) {
				return note;
			}
		}
		return null;
	}

	public TGNote getNextNote(TGMeasure measure,long start, int voiceIndex, int string) {
		TGBeat beat = getBeat(measure, start);
		if( beat != null ){
			TGBeat next = getNextBeat(measure.getBeats(),beat);
			while(next != null){
				TGVoice voice = next.getVoice(voiceIndex);
				if(!voice.isEmpty()){
					for (int i = 0; i < voice.countNotes(); i++) {
						TGNote current = voice.getNote(i);
						if (current.getString() == string) {
							return current;
						}
					}
				}
				next = getNextBeat(measure.getBeats(),next);
			}
		}
		return null;
	}

	public TGDuration getMinimumDuration(TGBeat beat){
		TGDuration minimumDuration = null;
		for(int v = 0; v < beat.countVoices(); v ++){
			TGVoice voice = beat.getVoice( v );
			if( !voice.isEmpty() ){
				if(minimumDuration == null || (voice.getDuration().compareTo(minimumDuration) < 0 )){
					minimumDuration = voice.getDuration();
				}
			}
		}
		return minimumDuration;
	}

	public TGDuration getMaximumDuration(TGBeat beat){
		TGDuration maximumDuration = null;
		for(int v = 0; v < beat.countVoices(); v ++){
			TGVoice voice = beat.getVoice( v );
			if( !voice.isEmpty() ){
				if(maximumDuration == null || (voice.getDuration().compareTo(maximumDuration) > 0 )){
					maximumDuration = voice.getDuration();
				}
			}
		}
		return maximumDuration;
	}

	public TGBeat getBeatPrecise(TGTrack track,long preciseStart) {
		Iterator<TGMeasure> measures = track.getMeasures();
		while( measures.hasNext() ){
			TGMeasure measure = measures.next();
			Iterator<TGBeat> beats = measure.getBeats().iterator();
			while(beats.hasNext()){
				TGBeat beat = beats.next();
				if (beat.getPreciseStart() == preciseStart) {
					return beat;
				}
			}
		}
		return null;
	}
	
	/**
	 * Retorna las Nota en la posicion y cuerda
	 */
	public TGBeat getBeat(TGMeasure measure,long start) {
		Iterator<TGBeat> it = measure.getBeats().iterator();
		while(it.hasNext()){
			TGBeat beat = it.next();
			if (beat.getStart() == start) {
				return beat;
			}
		}
		return null;
	}

	public TGBeat getBeatPrecise(TGMeasure measure, long preciseStart) {
		Iterator<TGBeat> it = measure.getBeats().iterator();
		while(it.hasNext()){
			TGBeat beat = it.next();
			if (beat.getPreciseStart() == preciseStart) {
				return beat;
			}
		}
		return null;
	}

	/**
	 * Retorna las Nota en la posicion y cuerda
	 */
	public TGBeat getBeatIn(TGMeasure measure,long start) {
		TGBeat beatIn = null;
		Iterator<TGBeat> it = measure.getBeats().iterator();
		while(it.hasNext()){
			TGBeat beat = it.next();
			TGDuration duration = getMinimumDuration(beat);
			if (beat.getStart() <= start && (beat.getStart() + duration.getTime() > start)) {
				if( beatIn == null || beat.getStart() > beatIn.getStart() ){
					beatIn = beat;
				}
			}
		}
		return beatIn;
	}

	/**
	 * Retorna las Nota en la posicion y cuerda
	 */
	public TGVoice getVoiceIn(TGMeasure measure,long start, int voiceIndex) {
		Iterator<TGBeat> it = measure.getBeats().iterator();
		while(it.hasNext()){
			TGBeat beat = it.next();
			TGVoice voice = beat.getVoice(voiceIndex);
			if (!voice.isEmpty() && beat.getStart() <= start && (beat.getStart() + voice.getDuration().getTime() > start)) {
				return voice;
			}
		}
		return null;
	}

	/**
	 * Retorna el Siguiente Componente
	 */
	public TGBeat getNextBeat(List<TGBeat> beats,TGBeat beat) {
		TGBeat next = null;
		for (int i = 0; i < beats.size(); i++) {
			TGBeat current =  beats.get(i);
			if (current.getStart() > beat.getStart()) {
				if (next == null) {
					next = current;
				} else if (current.getStart() < next.getStart()) {
					next = current;
				}
			}
		}
		return next;
	}

	/**
	 * Retorna el Componente Anterior
	 */
	public TGBeat getPreviousBeat(List<TGBeat> beats,TGBeat beat) {
		TGBeat previous = null;
		for (int i = 0; i < beats.size(); i++) {
			TGBeat current =  beats.get(i);
			if (current.getStart() < beat.getStart()) {
				if (previous == null) {
					previous = current;
				} else if (current.getStart() > previous.getStart()) {
					previous = current;
				}
			}
		}
		return previous;
	}

	/**
	 * Retorna el Primer Componente
	 */
	public TGBeat getFirstBeat(List<TGBeat> components) {
		TGBeat first = null;
		for (int i = 0; i < components.size(); i++) {
			TGBeat component =  components.get(i);
			if (first == null || component.getStart() < first.getStart()) {
				first = component;
			}
		}
		return first;
	}

	/**
	 * Retorna el Ultimo Componente
	 */
	public TGBeat getLastBeat(List<TGBeat> components) {
		TGBeat last = null;
		for (int i = 0; i < components.size(); i++) {
			TGBeat component =  components.get(i);
			if (last == null || last.getStart() < component.getStart()) {
				last = component;
			}
		}
		return last;
	}

	/**
	 * Retorna Todos los desde Start hasta el final del compas
	 */
	public List<TGBeat> getBeatsBeforeEnd(List<TGBeat> beats,long fromStart) {
		List<TGBeat> list = new ArrayList<TGBeat>();
		Iterator<TGBeat> it = beats.iterator();
		while(it.hasNext()){
			TGBeat current = it.next();
			if (current.getStart() >= fromStart) {
				list.add(current);
			}
		}
		return list;
	}

	public List<TGBeat> getBeatsBeforeEndPrecise(List<TGBeat> beats,long fromPreciseStart) {
		List<TGBeat> list = new ArrayList<TGBeat>();
		Iterator<TGBeat> it = beats.iterator();
		while(it.hasNext()){
			TGBeat current = it.next();
			if (current.getPreciseStart() >= fromPreciseStart) {
				list.add(current);
			}
		}
		return list;
	}

	public List<TGBeat> getBeatsBeetween(List<TGBeat> beats,long p1, long p2) {
		List<TGBeat> list = new ArrayList<TGBeat>();
		Iterator<TGBeat> it = beats.iterator();
		while(it.hasNext()){
			TGBeat current = it.next();
			if (current.getStart() >= p1 && current.getStart() < p2 ) {
				list.add(current);
			}
		}
		return list;
	}

	public void locateBeat( TGBeat beat, TGTrack track , boolean newMeasureAlsoForRestBeats) {
		if( beat.getMeasure() != null ){
			beat.getMeasure().removeBeat(beat);
			beat.setMeasure(null);
		}
		TGMeasure newMeasure = getSongManager().getTrackManager().getMeasureAtPreciseStart(track, beat.getPreciseStart() );
		if( newMeasure == null ){
			boolean createNewMeasure = newMeasureAlsoForRestBeats;
			if( !createNewMeasure ){
				createNewMeasure = ( !beat.isRestBeat() || beat.isTextBeat() );
			}
			if( createNewMeasure ){

				while( newMeasure == null && beat.getStart() >= TGDuration.QUARTER_TIME){
					getSongManager().addNewMeasureBeforeEnd(track.getSong());
					newMeasure = getSongManager().getTrackManager().getMeasureAtPreciseStart(track, beat.getPreciseStart() );
				}
			}
		}
		if( newMeasure != null ){
			long mPreciseStart = newMeasure.getPreciseStart();
			long mPreciseLength = newMeasure.getPreciseLength();
			long bPreciseStart = beat.getPreciseStart();
			for( int v = 0 ; v < beat.countVoices() ; v ++ ){
				TGVoice voice = beat.getVoice( v );
				long vPreciseDuration = voice.getDuration().getPreciseTime();
				if(!voice.isEmpty() && (bPreciseStart + vPreciseDuration) > (mPreciseStart + mPreciseLength) ){
					long vTiedPreciseDuration = ( (bPreciseStart + vPreciseDuration) - (mPreciseStart + mPreciseLength) );
					vPreciseDuration -= vTiedPreciseDuration;
					if( vPreciseDuration > 0 ){
						TGDuration duration = getSongManager().getFactory().newDuration();
						duration.setPreciseValue(vPreciseDuration);
						if( duration != null ){
							voice.getDuration().copyFrom( duration );
						}
					}
					if( vTiedPreciseDuration > 0 ) {
						TGDuration newVoiceDuration = getSongManager().getFactory().newDuration();
						newVoiceDuration.setPreciseValue(vTiedPreciseDuration);
						if( newVoiceDuration != null ){
							long newBeatPreciseStart = (bPreciseStart + vPreciseDuration);
							TGBeat newBeat = getBeatPrecise(track, newBeatPreciseStart);
							if( newBeat == null ){
								newBeat = getSongManager().getFactory().newBeat();
								newBeat.setPreciseStart( (bPreciseStart + vPreciseDuration) );
							}
							TGVoice newVoice = newBeat.getVoice( v );
							for( int n = 0 ; n < voice.countNotes() ; n ++ ){
								TGNote note = voice.getNote( n );
								TGNote newNote = getSongManager().getFactory().newNote();
								newNote.setTiedNote( true );
								newNote.setValue( note.getValue() );
								newNote.setString( note.getString() );
								newNote.setVelocity( note.getVelocity() );
								newVoice.addNote( newNote );
							}
							newVoice.setEmpty( false );
							newVoice.getDuration().copyFrom( newVoiceDuration );

							locateBeat(newBeat, track, newMeasureAlsoForRestBeats);
						}
					}
				}
			}

			newMeasure.addBeat(beat);
		}
	}

	public void moveOutOfBoundsBeatsToNewMeasure(TGMeasure measure){
		this.moveOutOfBoundsBeatsToNewMeasure(measure, true );
	}

	public void moveOutOfBoundsBeatsToNewMeasure(TGMeasure measure, boolean newMeasureAlsoForRestBeats ){
		List<TGBeat> beats = new ArrayList<TGBeat>();
		long mStart = measure.getStart();
		long mLength = measure.getLength();
		for( int i = 0; i < measure.countBeats() ; i ++ ){
			TGBeat beat = measure.getBeat( i );
			if( beat.getStart() < mStart || beat.getStart() >= mStart + mLength ){
				beats.add( beat );
			}
			else{
				long bStart = beat.getStart();
				for( int v = 0 ; v < beat.countVoices() ; v ++ ){
					TGVoice voice = beat.getVoice( v );
					long vDuration = voice.getDuration().getTime();
					if(!voice.isEmpty() && (bStart + vDuration) > (mStart + mLength) ){
						beats.add( beat );
					}
				}
			}
		}
		while( !beats.isEmpty() ){
			TGBeat beat = beats.get( 0 );
			if( beat.getMeasure() != null ){
				beat.getMeasure().removeBeat(beat);
				beat.setMeasure(null);
			}
			this.locateBeat(beat, measure.getTrack(), newMeasureAlsoForRestBeats);

			beats.remove(0);
		}
	}

	public boolean moveBeatsInMeasurePrecise(TGMeasure measure,long preciseStart,long thePreciseMove, TGDuration fillDuration){
		if( thePreciseMove == 0 ){
			return false;
		}
		boolean success = true;
		long measurePreciseStart = measure.getPreciseStart();
		long measurePreciseEnd =  (measurePreciseStart + measure.getPreciseLength());
		
		// Muevo los componentes
		List<TGBeat> beatsToMove = getBeatsBeforeEndPrecise(measure.getBeats(),preciseStart);
		moveBeatsPrecise(beatsToMove,thePreciseMove);

		if(success){
			List<TGBeat> beatsToRemove = new ArrayList<TGBeat>();
			List<TGBeat> beats = new ArrayList<TGBeat>(measure.getBeats());

			// Verifica los silencios a eliminar al principio del compas
			TGBeat first = getFirstBeat( beats );
			while(first != null && first.isRestBeat() && !first.isTextBeat() && first.getPreciseStart() < measurePreciseStart){
				beats.remove(first);
				beatsToRemove.add(first);
				first = getNextBeat( beats,first);
			}

			// Verifica los silencios a eliminar al final del compas
			TGBeat last = getLastBeat(beats);
			TGDuration lastDuration = (last != null ? getMinimumDuration(last) : null);
			while(last != null && lastDuration != null && last.isRestBeat() && !last.isTextBeat()  && (last.getPreciseStart() + lastDuration.getPreciseTime() ) > measurePreciseEnd  ){
				beats.remove(last);
				beatsToRemove.add(last);
				last = getPreviousBeat(beats,last);
				lastDuration = (last != null ? getMinimumDuration(last) : null);
			}

			// Si el primer o ultimo componente, quedan fuera del compas, entonces el movimiento no es satisfactorio
			if(first != null && last != null && lastDuration != null){
				if(first.getPreciseStart() < measurePreciseStart || (last.getPreciseStart() + lastDuration.getPreciseTime()) > measurePreciseEnd){
					success = false;
				}
			}

			if(success){
				// Elimino los silencios que quedaron fuera del compas.
				Iterator<TGBeat> it = beatsToRemove.iterator();
				while( it.hasNext() ){
					TGBeat beat = it.next();
					removeBeat(beat);
				}

				// Se crean silencios en los espacios vacios, si la duracion fue especificada.
				if( fillDuration != null ){
					if( thePreciseMove < 0 ){
						last = getLastBeat(measure.getBeats());
						lastDuration = (last != null ? getMinimumDuration(last) : null);
						TGBeat beat = getSongManager().getFactory().newBeat();
						beat.setPreciseStart( (last != null && lastDuration != null ? last.getPreciseStart()  + lastDuration.getPreciseTime() : preciseStart  )  );
						if( (beat.getPreciseStart() + fillDuration.getPreciseTime()) <= measurePreciseEnd ){
							for(int v = 0; v < beat.countVoices(); v ++){
								TGVoice voice = beat.getVoice(v);
								voice.setEmpty(false);
								voice.getDuration().copyFrom( fillDuration );
							}
							addBeat(measure, beat );
						}
					}
					else{
						first = getFirstBeat(getBeatsBeforeEndPrecise(measure.getBeats(),preciseStart));
						TGBeat beat = getSongManager().getFactory().newBeat();
						beat.setPreciseStart( preciseStart );
						if( (beat.getPreciseStart() + fillDuration.getPreciseTime()) <= (first != null ?first.getPreciseStart() : measurePreciseEnd ) ){
							for(int v = 0; v < beat.countVoices(); v ++){
								TGVoice voice = beat.getVoice(v);
								voice.setEmpty(false);
								voice.getDuration().copyFrom( fillDuration );
							}
							addBeat(measure, beat );
						}
					}
				}
			}
		}

		// Si el movimiento no es satisfactorio, regreso todo como estaba
		if(! success ){
			moveBeatsPrecise(beatsToMove,-thePreciseMove);
		}

		return success;
	}

	public void moveAllBeats(TGMeasure measure,long theMove){
		moveBeats(measure.getBeats(),theMove);
		// refresh precise start
		this.updateBeatsPreciseStart(measure);
	}

	public void moveBeats(TGMeasure measure, long start, long theMove){
		moveBeats(getBeatsBeforeEnd(measure.getBeats(), start),theMove);
		// refresh precise start
		this.updateBeatsPreciseStart(measure);
	}

	/**
	 * Mueve los componentes
	 */
	private void moveBeats(List<TGBeat> beats,long theMove){
		Iterator<TGBeat> it = beats.iterator();
		while(it.hasNext()){
			TGBeat beat = it.next();
			moveBeat(beat,theMove);
		}
	}

	private void moveBeatsPrecise(List<TGBeat> beats,long thePreciseMove){
		Iterator<TGBeat> it = beats.iterator();
		while(it.hasNext()){
			TGBeat beat = it.next();
			moveBeatPrecise(beat,thePreciseMove);
		}
	}

	/**
	 * Mueve el componente
	 */
	private void moveBeat(TGBeat beat,long theMove){
		long start = beat.getStart();
		//define new (approximative) start
		beat.setStart(start + theMove);
	}
	private void moveBeatPrecise(TGBeat beat,long thePreciseMove){
		long preciseStart = beat.getPreciseStart();
		//define new (precise) start
		beat.setPreciseStart(preciseStart + thePreciseMove);
	}

	public void cleanBeat(TGBeat beat){
		beat.getStroke().setDirection( TGStroke.STROKE_NONE );
		beat.getPickStroke().setDirection( TGPickStroke.PICK_STROKE_NONE );
		if( beat.getText() != null ){
			beat.removeText();
		}
		if( beat.getChord() != null){
			beat.removeChord();
		}

		this.cleanBeatNotes(beat);
	}

	public void cleanBeatNotes(TGBeat beat){
		for(int v = 0; v < beat.countVoices(); v ++){
			cleanVoiceNotes(beat.getVoice( v ));
		}
	}

	public void cleanVoiceNotes(TGVoice voice){
		if(!voice.isEmpty()){
			while(voice.countNotes() > 0 ){
				TGNote note = voice.getNote(0);
				removeNote(note);
			}
		}
	}

	/**
	 * Agrega el acorde al compas
	 */
	public void addChord(TGBeat beat,TGChord chord){
		beat.removeChord();
		beat.setChord(chord);
	}

	/**
	 * Retorna el acorde en la position
	 */
	public TGChord getChord(TGMeasure measure,long start) {
		TGBeat beat = getBeat(measure, start);
		if(beat != null){
			return beat.getChord();
		}
		return null;
	}

	/**
	 * Borra el acorde en la position
	 */
	public void removeChord(TGMeasure measure,long start) {
		TGBeat beat = getBeat(measure, start);
		if(beat != null){
			beat.removeChord();
		}
	}

	/**
	 * Agrega el texto al compas
	 */
	public void addText(TGBeat beat,TGText text){
		beat.removeText();
		if(!text.isEmpty()){
			beat.setText(text);
		}
	}

	/**
	 * Borra el texto en el pulso
	 */
	public void removeText(TGBeat beat){
		beat.removeText();
	}

	public void cleanMeasure(TGMeasure measure){
		while( measure.countBeats() > 0){
			removeBeat( measure.getBeat(0));
		}
	}

	// (re)compute preciseStart of all beats in measure
	// assumption: beats start is precise enough to ensure beats can be sorted correctly
	public void updateBeatsPreciseStart(TGMeasure measure) {
		List<TGBeat> beatsToDelete = new ArrayList<TGBeat>();
		Collections.sort(measure.getBeats());
		long voiceEnd[] = new long[TGBeat.MAX_VOICES];
		boolean isFirstBeat = true;
		for (TGBeat beat : measure.getBeats()) {
			if (isFirstBeat) {
				// align beat at measure start
				beat.setPreciseStart(beat.getMeasure().getPreciseStart());
				long minVoiceEnd=0;
				for (int v=0; v<TGBeat.MAX_VOICES; v++) {
					if (!beat.getVoice(v).isEmpty()) {
						voiceEnd[v] = beat.getPreciseStart() + beat.getVoice(v).getDuration().getPreciseTime();
						if ((minVoiceEnd == 0) || (voiceEnd[v] < minVoiceEnd)) {
							minVoiceEnd = voiceEnd[v];
						}
					}
				}
				// theoretically useless: if one voice of first beat is empty, logically the voice
				// shall be empty in the full measure. But we never know
				for (int v=0; v<TGBeat.MAX_VOICES; v++) {
					voiceEnd[v] = Math.max(voiceEnd[v], minVoiceEnd);
					}
			}
			else {
				// align beat left, avoiding conflicts ("holes" are easier to fix than overlaps)
				long beatPreciseStart = 0;
				for (int v=0; v<TGBeat.MAX_VOICES; v++) {
					if (!beat.getVoice(v).isEmpty()) {
						if ((beatPreciseStart == 0) || (voiceEnd[v] > beatPreciseStart)) {
							beatPreciseStart = voiceEnd[v];
						}
					}
				}
				if (beatPreciseStart == 0) {
					// empty beat?!
					beatsToDelete.add(beat);
				}
				else {
					beat.setPreciseStart(beatPreciseStart);
					for (int v=0; v<TGBeat.MAX_VOICES; v++) {
						if (!beat.getVoice(v).isEmpty()) {
							voiceEnd[v] = beat.getPreciseStart() + beat.getVoice(v).getDuration().getPreciseTime();
						}
					}
				}
			}
			isFirstBeat = false;
		}
		for (TGBeat beat : beatsToDelete) {
			this.removeBeat(beat);
		}
	}

	/**
	 * try to shift note one string up, returns new string number in case of success else 0
	 */
	public int shiftNoteUp(TGMeasure measure,long start,int string){
		return shiftNote(measure, start, string,-1);
	}
	/**
	 * try to shift note one string down, returns new string number in case of success else 0
	 */
	public int shiftNoteDown(TGMeasure measure,long start,int string){
		return shiftNote(measure, start, string,1);
	}

	/**
	 * try to shift note one string up/down, returns new string number in case of success else 0
	 */
	private int shiftNote(TGMeasure measure,long start,int string,int move){
		TGNote note = getNote(measure,start,string);
		if(note != null){
			int nextStringNumber = (note.getString() + move);
			TGTrack track = measure.getTrack();
			if ((getNote(measure,start,nextStringNumber) == null) && (nextStringNumber >= 1) && (nextStringNumber <= track.stringCount())){
				TGString currentString = track.getString(note.getString());
				TGString nextString = track.getString(nextStringNumber);
				int noteValue = (note.getValue() + currentString.getValue());
				boolean canMove = noteValue >= nextString.getValue();
				canMove &= ((nextString.getValue() + track.getMaxFret() >= noteValue) || track.isPercussion());
				int graceValue = 0;
				if (note.getEffect().isGrace()) {
					graceValue = note.getEffect().getGrace().getFret() + currentString.getValue();
					canMove &= (graceValue >= nextString.getValue());
					canMove &= ((nextString.getValue() + track.getMaxFret() >= graceValue) || track.isPercussion());
				}
				if (canMove) {
					note.setValue(noteValue - nextString.getValue());
					note.setString(nextString.getNumber());
					if (note.getEffect().isGrace()) {
						note.getEffect().getGrace().setFret(graceValue-nextString.getValue());
					}
					return note.getString();
				}
			}
		}
		return 0;
	}

	/**
	 * Mueve la nota 1 semitono arriba
	 */
	public boolean canMoveSemitoneUp(TGMeasure measure,long start,int string){
		return moveSemitone(measure, start, string,1, false);
	}

	public boolean moveSemitoneUp(TGMeasure measure,long start,int string){
		return moveSemitone(measure, start, string,1, true);
	}

	/**
	 * Mueve la nota 1 semitono abajo
	 */
	public boolean canMoveSemitoneDown(TGMeasure measure,long start,int string){
		return moveSemitone(measure, start, string,-1, false);
	}
	public boolean moveSemitoneDown(TGMeasure measure,long start,int string){
		return moveSemitone(measure, start, string,-1, true);
	}

	/**
	 * Mueve la nota los semitonos indicados
	 */
	private boolean moveSemitone(TGMeasure measure,long start,int string,int semitones,boolean doAction){
		TGNote note = getNote(measure,start,string);
		if(note != null){
			int newValue = (note.getValue() + semitones);
			if( newValue >= 0 && (newValue <= measure.getTrack().getMaxFret() || measure.getTrack().isPercussion()) ){
				if (doAction) {
					note.setValue(newValue);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Set the beat stroke
	 */
	public boolean setStroke(TGMeasure measure, long start, int value, int direction){
		TGBeat beat = getBeat(measure, start);
		if( beat != null ){
			beat.getStroke().setValue(value);
			beat.getStroke().setDirection(direction);
			return true;
		}
		return false;
	}

	public boolean changePickStrokeUp(TGMeasure measure, long start){
		TGBeat beat = getBeat(measure, start);
		if( beat != null ){
			if (beat.getPickStroke().getDirection() != TGPickStroke.PICK_STROKE_UP){
				beat.getPickStroke().setDirection(TGPickStroke.PICK_STROKE_UP);
			} else {
				beat.getPickStroke().setDirection(TGPickStroke.PICK_STROKE_NONE);
			}
			return true;
		}
		return false;
	}

	public boolean changePickStrokeDown(TGMeasure measure, long start){
		TGBeat beat = getBeat(measure, start);
		if( beat != null ){
			if (beat.getPickStroke().getDirection() != TGPickStroke.PICK_STROKE_DOWN){
				beat.getPickStroke().setDirection(TGPickStroke.PICK_STROKE_DOWN);
			} else {
				beat.getPickStroke().setDirection(TGPickStroke.PICK_STROKE_NONE);
			}
			return true;
		}
		return false;
	}

	public void autoCompleteSilences(TGMeasure measure){
		TGBeat beat = getFirstBeat( measure.getBeats() );

		// no beat: fill measure and return
		if( beat == null ){
			createSilences(measure, measure.getPreciseStart(), measure.getPreciseLength(), 0);
			return;
		}

		// complete silences before first note
		boolean hasVoices = false;
		for( int v = 0 ; v < TGBeat.MAX_VOICES ; v ++ ){
			TGVoice voice = getFirstVoice( measure.getBeats() , v );
			if( voice != null && voice.getBeat().getPreciseStart() > measure.getPreciseStart() ){
				createSilences(measure, measure.getPreciseStart(), voice.getBeat().getPreciseStart() - measure.getPreciseStart(), v);
			}
			hasVoices = (hasVoices || voice != null);
		}

		// no voice: fill measure and return
		if(!hasVoices) {
			createSilences(measure, measure.getPreciseStart(), measure.getPreciseLength(), 0);
			return;
		}

		long[] start = new long[beat.countVoices()];
		long[] uncompletedLength = new long[beat.countVoices()];
		for( int v = 0; v < uncompletedLength.length; v ++ ){
			start[v] = 0;
			uncompletedLength[v] = 0;
		}

		// in each voice, look for empty spaces between notes, and until end of measure
		while (beat != null) {
			for( int v = 0; v < beat.countVoices(); v ++ ){
				TGVoice voice = beat.getVoice( v );
				if( !voice.isEmpty() ){
					long voiceEnd = beat.getPreciseStart() + voice.getDuration().getPreciseTime();
					long nextPosition = measure.getPreciseStart() + measure.getPreciseLength();

					TGVoice nextVoice = getNextVoice(measure.getBeats(), beat, voice.getIndex());
					if(nextVoice != null){
						nextPosition = nextVoice.getBeat().getPreciseStart();
					}
					if( voiceEnd < nextPosition){
						start[v] = voiceEnd;
						uncompletedLength[v] = (nextPosition - voiceEnd);
					}
				}
			}

			for( int v = 0; v < uncompletedLength.length; v ++ ){
				if( uncompletedLength[v] > 0 ){
					createSilences(measure,start[v],uncompletedLength[v], v);
				}
				start[v] = 0;
				uncompletedLength[v] = 0;
			}
			beat = getNextBeat(measure.getBeats(), beat );
		}
	}

	// checks if a measure is valid: all notes/rests in each voice are contiguous, all voices durations are OK
	public List<TGMeasureError> getMeasureErrors(TGMeasure measure) {
		List<TGMeasureError> list = new ArrayList<TGMeasureError>();

		for (int voiceIndex=0; voiceIndex<TGBeat.MAX_VOICES; voiceIndex++) {
			int errCode = TGMeasureError.OK;
			boolean isEmpty = true;
			long firstPreciseStart = -1;
			long lastPreciseEnd = -1;
			for (TGBeat beat : measure.getBeats()) {
				long beatPreciseStart = beat.getPreciseStart();
				TGVoice voice = beat.getVoice(voiceIndex);
				// check tied notes
				for (TGNote note : voice.getNotes()) {
					if (note.isTiedNote() && (getSongManager().getTrackManager().getPreviousNoteForTie(note) == null)) {
						list.add(new TGMeasureError(measure, note));
					}
				}
				// check duration
				if (!voice.isEmpty()) {
					isEmpty = false;
					if (firstPreciseStart < 0) {
						if (beatPreciseStart < measure.getPreciseStart()) {
							errCode |= TGMeasureError.VOICE_EARLY_START;
						}
						firstPreciseStart = beat.getPreciseStart();
					} else {
						if (beatPreciseStart < lastPreciseEnd) {
							errCode |= TGMeasureError.VOICE_OVERLAP;
						}
						else if (beatPreciseStart > lastPreciseEnd) {
							errCode |= TGMeasureError.VOICE_DISCONTINUOUS;
						}
					}
					lastPreciseEnd = beatPreciseStart + voice.getDuration().getPreciseTime();
				}
			}
			if (!isEmpty) {
				long measurePreciseEnd = measure.getPreciseStart() + measure.getPreciseLength();
				if (lastPreciseEnd < measurePreciseEnd) {
					errCode |= TGMeasureError.VOICE_TOO_SHORT;
				}
				else if (lastPreciseEnd > measurePreciseEnd) {
					errCode |= TGMeasureError.VOICE_TOO_LONG;
				}
			}
			if (errCode != TGMeasureError.OK) {
				list.add(new TGMeasureError(measure, voiceIndex, errCode));
			}
		}
		return list;
	}

	public boolean isMeasureDurationValid(TGMeasure measure) {
		for (TGMeasureError err : this.getMeasureErrors(measure)) {
			if (err.getErrorType() == TGMeasureError.TYPE_VOICE_DURATION_ERROR) {
				return false;
			}
		}
		return true;
	}
	
	public void fixVoice(TGMeasure measure, int voiceIndex, int errCode) {
		if ((errCode & TGMeasureError.VOICE_OVERLAP) != 0) {
			this.fixVoiceOverlap(measure, voiceIndex);
		}
		if ( ((errCode & TGMeasureError.VOICE_TOO_LONG) != 0) || ((errCode & TGMeasureError.VOICE_TOO_SHORT) != 0) ) {
			this.fixVoiceLongShort(measure, voiceIndex);
		}
	}

	// fix an invalid voice in measure: overlap
	public void fixVoiceOverlap(TGMeasure measure, int voiceIndex) {
		boolean overlap = true;
		while (overlap) {
			overlap = false;
			TGVoice previousVoice = null;
			for (TGBeat beat : measure.getBeats()) {
				TGVoice voice = beat.getVoice(voiceIndex);
				if (!voice.isEmpty()) {
					if (previousVoice != null) {
						long previousVoiceEnd = previousVoice.getBeat().getPreciseStart() + previousVoice.getDuration().getPreciseTime();
						long voiceStart = voice.getBeat().getPreciseStart();
						long overlapTime = previousVoiceEnd - voiceStart;
						if (overlapTime > 0) {
							overlap = true;
							// fix overlap, then break loop to restart from measure beginning
							long fixedPreviousPreciseTime = previousVoice.getDuration().getPreciseTime() - overlapTime;
							TGDuration fixedDuration = getSongManager().getFactory().newDuration();
							try {
								fixedDuration.setPreciseValue(fixedPreviousPreciseTime);
							}
							catch (IllegalArgumentException e) {
								// failed to fix, ignore
								overlap = false;
							}
							if (overlap) {
								previousVoice.setDuration(fixedDuration);
								this.updateBeatsPreciseStart(measure);
								break;
							}
						}
					}
					
					previousVoice = voice;
				}
			}
		}
	}

	// fix an invalid voice in measure: too long or too short
	public void fixVoiceLongShort(TGMeasure measure, int voiceIndex) {
		List<TGBeat> beats = measure.getBeats();
		// remove all voices starting after measure end
		long measurePreciseEnd = measure.getPreciseStart() + measure.getPreciseLength();
		int nBeat = beats.size()-1;	// last beat
		while (nBeat>=0 && beats.get(nBeat).getPreciseStart() >= measurePreciseEnd) {
			this.removeVoice(beats.get(nBeat).getVoice(voiceIndex));
			nBeat--;
		}
		// try to reduce duration of last voice if it overlaps measure end, else remove it
		beats = measure.getBeats();
		TGVoice voice = this.getLastVoice(beats, voiceIndex);
		if (voice.getBeat().getPreciseStart() + voice.getDuration().getPreciseTime() > measurePreciseEnd) {
			long correctDurationPreciseTime = measurePreciseEnd - voice.getBeat().getPreciseStart();
			TGDuration duration = getSongManager().getFactory().newDuration();
			try {
				duration.setPreciseValue(correctDurationPreciseTime);
				voice.setDuration(duration);
			} catch (IllegalArgumentException e) {
				this.removeVoice(voice);
			}
		}
		// at this stage, measure's voice is either valid, or too short
		beats = measure.getBeats();
		voice = this.getLastVoice(beats, voiceIndex);
		long gapToFill = measurePreciseEnd - voice.getBeat().getPreciseStart() - voice.getDuration().getPreciseTime();
		while (gapToFill > 0) {
			// try to fill with rests if possible
			List<TGDuration> listDurations =  TGDuration.splitPreciseDuration(gapToFill, TGDuration.WHOLE_PRECISE_DURATION, getSongManager().getFactory());
			if (listDurations != null) {
				this.autoCompleteSilences(measure);
				gapToFill = 0;
			}
			else {
				// cannot fill with rests, try to increase duration of last voice
				// if it fails, remove last voice
				long durationToFill = measurePreciseEnd - voice.getBeat().getPreciseStart();
				listDurations =  TGDuration.splitPreciseDuration(durationToFill, TGDuration.WHOLE_PRECISE_DURATION, getSongManager().getFactory());
				if (listDurations!=null && listDurations.size()==1) {
					// replace current duration by new one, this fixes measure's voice
					voice.setDuration(listDurations.get(0).clone(getSongManager().getFactory()));
					gapToFill = 0;
				}
				else {
					removeVoice(voice);
					// retry...
					voice = this.getLastVoice(beats, voiceIndex);
					gapToFill = measurePreciseEnd - voice.getBeat().getPreciseStart() - voice.getDuration().getPreciseTime();
				}
			}
		}
	}

	/**
	 * Crea Silencios temporarios en base a length
	 */
	private void createSilences(TGMeasure measure, long preciseStart, long preciseLength, int voiceIndex){
		createSilences(measure, preciseStart, preciseLength, measure.getPreciseLength(), voiceIndex);
	}

	private void createSilences(TGMeasure measure, long preciseStart, long preciseLength, long maxPreciseLength, int voiceIndex){
		createSilences(measure, preciseStart, preciseLength, maxPreciseLength, null, voiceIndex);
	}

	private void createSilences(TGMeasure measure, long preciseStart, long preciseLength, long maxPreciseLength, TGDuration preferredDuration, int voiceIndex){
		long nextStart = preciseStart;
		List<TGDuration> durations = TGDuration.splitPreciseDuration(preciseLength, maxPreciseLength, preferredDuration, getSongManager().getFactory());
		if (durations == null) {
			return;
		}
		Iterator<TGDuration> it = durations.iterator();
		while(it.hasNext()){
			TGDuration duration = it.next();

			boolean isNew = false;
			TGBeat beat = getBeatPrecise(measure, nextStart);
			if( beat == null ){
				beat = getSongManager().getFactory().newBeat();
				beat.setPreciseStart(nextStart);
				isNew = true;
			}

			TGVoice voice = beat.getVoice(voiceIndex);
			voice.setEmpty(false);
			voice.getDuration().copyFrom(duration);

			if( isNew ){
				addBeat(measure,beat);
			}

			nextStart += duration.getPreciseTime();
		}
	}

	/**
	 * Liga la nota
	 */
	public void changeTieNote(TGNote note){
		boolean isValid = (note.isTiedNote() || getSongManager().isFreeEditionMode(note.getVoice().getBeat().getMeasure()));
		if (!isValid) {
			// check if creating a tie is valid
			isValid = (getSongManager().getTrackManager().getPreviousNoteForTie(note) != null);
		}
		if (isValid) {
			note.setTiedNote(!note.isTiedNote());
			note.getEffect().setDeadNote(false);
		}
	}

	/**
	 * Agrega un vibrato
	 */
	public void setVibrato(TGNote note, boolean value){
		if(note != null){
			note.getEffect().setVibrato(value);
		}
	}

	/**
	 * Agrega una nota muerta
	 */
	public void changeDeadNote(TGNote note){
		note.getEffect().setDeadNote(!note.getEffect().isDeadNote());
		note.setTiedNote(false);
	}
	public void setDeadNote(TGNote note, boolean value){
		boolean oldValue = note.getEffect().isDeadNote();
		note.getEffect().setDeadNote(value);
		if (value != oldValue) {
			note.setTiedNote(false);
		}
	}

	/**
	 * Agrega un slide
	 */
	public void setSlideNote(TGNote note, boolean value){
		if(note != null){
			note.getEffect().setSlide(value);
		}
	}

	/**
	 * Agrega un hammer
	 */
	public void setHammerNote(TGNote note, boolean value){
		if(note != null){
			note.getEffect().setHammer(value);
		}
	}

	/**
	 * Agrega un palm-mute
	 */
	public void setPalmMute(TGNote note, boolean value){
		if(note != null){
			note.getEffect().setPalmMute(value);
		}
	}

	/**
	 * Agrega un staccato
	 */
	public void setStaccato(TGNote note, boolean value){
		if(note != null){
			note.getEffect().setStaccato(value);
		}
	}

	/**
	 * Agrega un let-ring
	 */
	public void setLetRing(TGNote note, boolean value){
		if(note != null){
			note.getEffect().setLetRing(value);
		}
	}

	/**
	 * Agrega un tapping
	 */
	public void setTapping(TGNote note, boolean value){
		if(note != null){
			note.getEffect().setTapping(value);
		}
	}

	/**
	 * Agrega un slapping
	 */
	public void setSlapping(TGNote note, boolean value){
		if(note != null){
			note.getEffect().setSlapping(value);
		}
	}

	/**
	 * Agrega un popping
	 */
	public void setPopping(TGNote note, boolean value){
		if(note != null){
			note.getEffect().setPopping(value);
		}
	}

	/**
	 * Agrega un bend
	 */
	public void setBendNote(TGNote note,TGEffectBend bend){
		if(note != null){
			note.getEffect().setBend(bend);
		}
	}

	/**
	 * Agrega un tremoloBar
	 */
	public void setTremoloBar(TGNote note, TGEffectTremoloBar tremoloBar){
		if(note != null){
			note.getEffect().setTremoloBar(tremoloBar);
		}
	}

	/**
	 * Agrega un GhostNote
	 */
	public void setGhostNote(TGNote note, boolean value) {
		if (note!= null) {
			note.getEffect().setGhostNote(value);
		}
	}

	/**
	 * Agrega un AccentuatedNote
	 */
	public void setAccentuatedNote(TGNote note, boolean value) {
		if (note != null) {
			note.getEffect().setAccentuatedNote(value);
		}
	}

	/**
	 * Agrega un HeavyAccentuatedNote
	 */
	public void setHeavyAccentuatedNote(TGNote note, boolean value){
		if(note != null){
			note.getEffect().setHeavyAccentuatedNote(value);
		}
	}

	/**
	 * Agrega un harmonic
	 */
	public void setHarmonicNote(TGNote note, TGEffectHarmonic harmonic){
		if(note != null){
			note.getEffect().setHarmonic(harmonic);
		}
	}

	/**
	 * Agrega un grace
	 */
	public void setGraceNote(TGNote note, TGEffectGrace grace){
		if(note != null){
			note.getEffect().setGrace(grace);
		}
	}

	/**
	 * Agrega un trill
	 */
	public void setTrillNote(TGNote note, TGEffectTrill trill){
		if(note != null){
			note.getEffect().setTrill(trill);
		}
	}

	/**
	 * Agrega un tremolo picking
	 */
	public void setTremoloPicking(TGNote note,TGEffectTremoloPicking tremoloPicking){
		if(note != null){
			note.getEffect().setTremoloPicking(tremoloPicking);
		}
	}

	/**
	 * Agrega un fadeIn
	 */
	public void setFadeIn(TGNote note, boolean value){
		if(note != null){
			note.getEffect().setFadeIn(value);
		}
	}

	/**
	 * Cambia el Velocity
	 */
	public void changeVelocity(int velocity, TGNote note){
		if(note != null){
			note.setVelocity(velocity);
		}
	}

	/**
	 * Retorna el Siguiente Componente
	 */
	public TGVoice getNextVoice(List<TGBeat> beats,TGBeat beat, int index) {
		TGVoice next = null;
		for (int i = 0; i < beats.size(); i++) {
			TGBeat current =  beats.get(i);
			if (current.getPreciseStart() > beat.getPreciseStart() && !current.getVoice(index).isEmpty()) {
				if (next == null) {
					next = current.getVoice(index);
				} else if (current.getPreciseStart() < next.getBeat().getPreciseStart()) {
					next = current.getVoice(index);
				}
			}
		}
		return next;
	}

	/**
	 * Retorna el Componente Anterior
	 */
	public TGVoice getPreviousVoice(List<TGBeat> beats,TGBeat beat, int index) {
		TGVoice previous = null;
		for (int i = 0; i < beats.size(); i++) {
			TGBeat current =  beats.get(i);
			if (current.getStart() < beat.getStart() && !current.getVoice(index).isEmpty()) {
				if (previous == null) {
					previous = current.getVoice(index);
				} else if (current.getStart() > previous.getBeat().getStart()) {
					previous = current.getVoice(index);
				}
			}
		}
		return previous;
	}

	/**
	 * Retorna el Primer Componente
	 */
	public TGVoice getFirstVoice(List<TGBeat> beats, int index) {
		TGVoice first = null;
		for (int i = 0; i < beats.size(); i++) {
			TGBeat current =  beats.get(i);
			if ( (first == null || current.getStart() < first.getBeat().getStart() ) && !current.getVoice(index).isEmpty()) {
				first = current.getVoice(index);
			}
		}
		return first;
	}

	/**
	 * Retorna el Ultimo Componente
	 */
	public TGVoice getLastVoice(List<TGBeat> beats, int index) {
		TGVoice last = null;
		for (int i = 0; i < beats.size(); i++) {
			TGBeat current =  beats.get(i);
			if ( (last == null || last.getBeat().getStart() < current.getStart()) && !current.getVoice(index).isEmpty() ) {
				last = current.getVoice(index);
			}
		}
		return last;
	}


	/**
	 * Retorna el Siguiente Componente
	 */
	public TGVoice getNextRestVoice(List<TGBeat> beats,TGVoice voice) {
		TGVoice next = getNextVoice(beats, voice.getBeat(), voice.getIndex());
		while(next != null && !next.isRestVoice()){
			next = getNextVoice(beats, next.getBeat(), next.getIndex());
		}
		return next;
	}

	public List<TGVoice> getVoicesBeforeEndPrecise(List<TGBeat> beats,long fromPreciseStart, int index) {
		List<TGVoice> list = new ArrayList<TGVoice>();
		Iterator<TGBeat> it = beats.iterator();
		while(it.hasNext()){
			TGBeat beat = it.next();
			if (beat.getPreciseStart() >= fromPreciseStart) {
				TGVoice voice = beat.getVoice(index);
				if(!voice.isEmpty()){
					list.add(voice);
				}
			}
		}
		return list;
	}

	public void addSilence(TGBeat beat, TGDuration duration, int voice){
		addSilence(beat, duration, beat.getStart(),voice);
	}

	public void addSilence(TGBeat beat, TGDuration duration, long start, int voice){
		boolean emptyVoice = beat.getVoice( voice ).isEmpty();
		if( emptyVoice ){
			beat.getVoice( voice ).setEmpty( false );
		}

		//Verifico si entra en el compas
		if(validateDuration(beat.getMeasure(),beat, voice, duration,true,true)){
			//Borro lo que haya en la misma posicion
			//removeNote(beat.getMeasure(),beat.getStart(),voice, note.getString());

			beat.getVoice(voice).getDuration().copyFrom(duration);

			//trato de agregar un silencio similar al lado
			tryChangeSilenceAfter(beat.getMeasure(),beat.getVoice(voice));

			// Despues de cambiar la duracion, verifico si hay un beat mejor para agregar el silencio.
			TGVoice realVoice = beat.getVoice(voice);
			if(realVoice.getBeat().getStart() != start){
				TGVoice beatIn = getVoiceIn(realVoice.getBeat().getMeasure(), start, voice);
				if( beatIn != null ) {
					realVoice = beatIn;
				}
			}
			realVoice.setEmpty(false);
		}else{
			beat.getVoice( voice ).setEmpty( emptyVoice );
		}
	}

	public void insertRestBeatWithoutControl(TGBeat fromBeat, int voiceIndex) {
		if (fromBeat.getVoice(voiceIndex).isEmpty()) {
			return;
		}
		TGMeasure measure = fromBeat.getMeasure();
		TGBeat newBeat = getSongManager().getFactory().newBeat();
		newBeat.setPreciseStart(fromBeat.getPreciseStart());
		long preciseDurationTime = fromBeat.getVoice(voiceIndex).getDuration().getPreciseTime();
		// shift right all measure beats, starting from specified beat, without any control
		for (TGBeat beat : measure.getBeats()) {
			if (beat.getPreciseStart() >= fromBeat.getPreciseStart()) {
				beat.setPreciseStart(beat.getPreciseStart() + preciseDurationTime);
			}
		}
		newBeat.getVoice(voiceIndex).setEmpty(false);
		newBeat.getVoice(voiceIndex).getDuration().copyFrom(fromBeat.getVoice(voiceIndex).getDuration());
		newBeat.setMeasure(measure);
		measure.getBeats().add(newBeat);
		this.orderBeats(measure);
	}

	public void removeVoice(TGVoice voice){
		voice.setEmpty(true);

		// Remove the beat If all voices are empty
		TGBeat beat = voice.getBeat();
		for(int i = 0; i < beat.countVoices(); i ++){
			if( !beat.getVoice(i).isEmpty() ){
				return;
			}
		}
		this.removeBeat(beat);
	}

	public void removeVoice(TGVoice voice,boolean moveNextVoices){
		removeVoice(voice);
		if(moveNextVoices){
			long preciseStart = voice.getBeat().getPreciseStart();
			long preciseLength = voice.getDuration().getPreciseTime();
			TGVoice next = getNextVoice(voice.getBeat().getMeasure().getBeats(),voice.getBeat(),voice.getIndex());
			if(next != null){
				preciseLength = next.getBeat().getPreciseStart() - preciseStart;
			}
			moveVoicesPrecise(voice.getBeat().getMeasure(), preciseStart + preciseLength, -preciseLength, voice.getIndex(), voice.getDuration());
		}
	}

	public void removeVoicesOutOfTime(TGMeasure measure){
		List<TGVoice> voicesToRemove = new ArrayList<TGVoice>();

		long mStart = measure.getStart();
		long mEnd = mStart + measure.getLength();

		Iterator<TGBeat> beats = measure.getBeats().iterator();
		while(beats.hasNext()){
			TGBeat beat = beats.next();
			for( int v = 0; v < beat.countVoices() ; v ++){
				TGVoice voice = beat.getVoice( v );
				if(!voice.isEmpty()){
					if( beat.getStart() < mStart || (beat.getStart() + voice.getDuration().getTime()) > mEnd){
						voicesToRemove.add( voice );
					}
				}
			}
		}
		Iterator<TGVoice> it = voicesToRemove.iterator();
		while(it.hasNext()){
			TGVoice voice = it.next();
			this.removeVoice( voice );
		}
	}

	public void removeMeasureVoices(TGMeasure measure,int index){
		boolean hasNotes = false;

		List<TGVoice> voices = new ArrayList<TGVoice>();
		Iterator<TGBeat> beatsIt = measure.getBeats().iterator();
		while(beatsIt.hasNext()){
			TGBeat beat = beatsIt.next();
			TGVoice voice = beat.getVoice(index);
			if(voice.isRestVoice()){
				voices.add(voice);
			}else if(!voice.isEmpty()){
				hasNotes = true;
				break;
			}
		}

		if( !hasNotes ){
			Iterator<TGVoice> voicesIt = voices.iterator();
			while(voicesIt.hasNext()){
				TGVoice voice = voicesIt.next();
				this.removeVoice( voice );
			}
		}
	}

	public void changeVoiceDirection( TGVoice voice , int direction){
		voice.setDirection( direction );
	}

	public void changeDuration(TGMeasure measure,TGBeat beat,TGDuration duration,int voiceIndex, boolean tryMove){
		if (getSongManager().isFreeEditionMode(measure)) {
			beat.getVoice(voiceIndex).getDuration().copyFrom(duration);
			this.updateBeatsPreciseStart(measure);
		}
		else {
			//obtengo la duracion vieja
			TGDuration oldDuration = beat.getVoice(voiceIndex).getDuration().clone(getSongManager().getFactory());
			
			//si no entra vuelvo a dejar la vieja
			if(validateDuration(measure,beat, voiceIndex, duration,tryMove,false)){
				//se lo agrego a todas las notas en la posicion
				beat.getVoice(voiceIndex).setDuration(duration.clone(getSongManager().getFactory()));
				
				//trato de agregar un silencio similar al lado
				tryChangeSilenceAfter(measure,beat.getVoice(voiceIndex));
			}else{
				beat.getVoice(voiceIndex).getDuration().copyFrom( oldDuration );
			}
		}
		if (getSongManager().isFreeEditionMode(measure)) {
			this.updateBeatsPreciseStart(measure);
		}
	}

	public void tryChangeSilenceAfter(TGMeasure measure,TGVoice voice){
		// delete all rests until next note (or end of measure)
		// all computations done with precise time
		List<TGBeat> beatsToDelete = new ArrayList<TGBeat>();
		long endInterval = measure.getPreciseStart() + measure.getPreciseLength();
		long beatStart = voice.getBeat().getPreciseStart();
		for (TGBeat b : measure.getBeats()) {
			if (b.getPreciseStart() > beatStart) {
				TGVoice tmpVoice = b.getVoice(voice.getIndex());
				if ((tmpVoice!=null) && !tmpVoice.isEmpty()) {
					if (tmpVoice.isRestVoice()) {
						tmpVoice.setEmpty(true);
						// check if beat needs to be deleted or not
						boolean isEmptyBeat = true;
						for (int v=0; v<b.countVoices(); v++) {
							isEmptyBeat &= (b.getVoice(v).isEmpty());
						}
						if (isEmptyBeat) {
							beatsToDelete.add(b);
						}
					} else {
						endInterval = b.getPreciseStart();
						break;
					}
				}
			}
		}
		// delete eventually empty beats just created
		while (!beatsToDelete.isEmpty()) {
			measure.removeBeat(beatsToDelete.get(0));
			beatsToDelete.remove(0);
		}

		// fill interval with rest, with preferred duration
		long start = beatStart + voice.getDuration().getPreciseTime();
		long length = endInterval - start;
		createSilences(measure, start, length, measure.getPreciseLength(), voice.getDuration(), voice.getIndex());

	}

	private void moveVoicesPrecise(List<TGVoice> voices,long thePreciseMove){
		int count = voices.size();
		for( int i = 0 ; i < count ; i ++ ){
			TGVoice voice = voices.get( (thePreciseMove < 0 ? i : ( (count - 1) - i ) ) );
			moveVoicePrecise(voice,thePreciseMove);
		}
	}

	public void moveVoicePrecise(TGVoice voice, long thePreciseMove){
		long newStart = voice.getBeat().getPreciseStart() + thePreciseMove;

		TGBeat newBeat = getBeatPrecise(voice.getBeat().getMeasure(),newStart);
		if( newBeat == null ){
			newBeat = getSongManager().getFactory().newBeat();
			newBeat.setPreciseStart( newStart );
			addBeat(voice.getBeat().getMeasure(), newBeat);
		}

		this.moveVoice(voice, newBeat);
	}

	public void moveVoice(TGVoice voice, TGBeat beat){
		TGBeat currentBeat = voice.getBeat();
		if(!currentBeat.equals(beat)){
			if(currentBeat.getVoice( voice.getIndex() ).equals( voice )){
				if ( currentBeat.isTextBeat() && isUniqueVoice(voice, false) ){
						beat.setText( currentBeat.getText() );
						currentBeat.removeText();
				}
				if( isUniqueVoice(voice, true) ){
					if( currentBeat.isChordBeat() ){
						beat.setChord( currentBeat.getChord() );
						currentBeat.removeChord();
					}
					if( currentBeat.getStroke().getDirection() != TGStroke.STROKE_NONE ){
						beat.getStroke().copyFrom( currentBeat.getStroke() );
						currentBeat.getStroke().setDirection(TGStroke.STROKE_NONE);
					}
					if( currentBeat.getPickStroke().getDirection() != TGPickStroke.PICK_STROKE_NONE ){
						beat.getPickStroke().copyFrom( currentBeat.getPickStroke() );
						currentBeat.getPickStroke().setDirection(TGPickStroke.PICK_STROKE_NONE);
					}
				}
				// Make sure to remove another voice instance from old beat.
				TGVoice newVoice = getSongManager().getFactory().newVoice(voice.getIndex());
				currentBeat.setVoice( voice.getIndex(), newVoice);
				this.removeVoice(newVoice);
			}
			beat.setVoice( voice.getIndex() , voice);
		}
	}

	public boolean validateDuration(TGMeasure measure,TGBeat beat,int voiceIndex, TGDuration duration,boolean moveNextBeats, boolean setCurrentDuration){
		long measurePreciseStart = measure.getPreciseStart();
		long measurePreciseEnd =  measurePreciseStart + measure.getPreciseLength();
		long beatPreciseStart = beat.getPreciseStart();
		long beatPreciseLength = duration.getPreciseTime();
		long beatPreciseEnd = beatPreciseStart + beatPreciseLength;
		List<TGBeat> beats = measure.getBeats();

		//Verifico si hay un beat en el mismo lugar, y comparo las duraciones.
		TGBeat currentBeat = getBeatPrecise(measure,beatPreciseStart);
		TGVoice currentVoice = null;
		if(currentBeat != null){
			currentVoice = currentBeat.getVoice(voiceIndex);
			if(!currentVoice.isEmpty() && beatPreciseLength <= currentVoice.getDuration().getPreciseTime()){
				// duration is reduced
				// check the interval until next note (or end of measure) can be filled with something valid (e.g. 1/9th of a 64th is not feasible)
				boolean ok = true;
				long intervalPreciseEnd = measure.getPreciseStart() + measure.getPreciseLength();
				for (TGBeat b : measure.getBeats()) {
					if (b.getPreciseStart() > beat.getPreciseStart()) {
						TGVoice voice = b.getVoice(voiceIndex);
						if ((voice!=null) && !voice.isEmpty() && !voice.isRestVoice()) {
							intervalPreciseEnd = b.getPreciseStart();
							break;
						}
					}
				}
				long toFillPrecise = intervalPreciseEnd - (beatPreciseStart + duration.getPreciseTime());
				if (toFillPrecise > 0) {
					ok = (TGDuration.splitPreciseDuration(toFillPrecise, measure.getPreciseLength(), getSongManager().getFactory()) != null);
				}
				return ok;
			}
		}

		//Verifico si hay lugar para meter el beat
		TGVoice nextVoice = getNextVoice(beats,beat, voiceIndex);
		if(currentVoice == null || currentVoice.isEmpty()){
			if((nextVoice == null || nextVoice.isEmpty()) && beatPreciseEnd <= measurePreciseEnd){
				return true;
			}
			if((nextVoice != null && !nextVoice.isEmpty()) && (beatPreciseEnd <= nextVoice.getBeat().getPreciseStart())){
				return true;
			}
		}

		// Busca si hay espacio disponible de silencios entre el componente y el el que le sigue.. si encuentra lo borra
		if(nextVoice != null && !nextVoice.isEmpty() && nextVoice.isRestVoice()){
			//Verifico si lo que sigue es un silencio. y lo borro
			long nextBeatPreciseEnd = 0;
			List<TGVoice> nextBeats = new ArrayList<TGVoice>();
			while(nextVoice != null && !nextVoice.isEmpty() && nextVoice.isRestVoice() && !nextVoice.getBeat().isTextBeat()){
				nextBeats.add(nextVoice);
				nextBeatPreciseEnd = nextVoice.getBeat().getPreciseStart() + nextVoice.getDuration().getPreciseTime();
				nextVoice = getNextVoice(beats,nextVoice.getBeat(), voiceIndex);
			}
			if(nextVoice == null || nextVoice.isEmpty()){
				nextBeatPreciseEnd = measurePreciseEnd;
			}else if(!nextVoice.isRestVoice() || nextVoice.getBeat().isTextBeat()){
				nextBeatPreciseEnd = nextVoice.getBeat().getPreciseStart();
			}
			if(beatPreciseEnd <= nextBeatPreciseEnd){
				while(!nextBeats.isEmpty()){
					TGVoice currVoice = nextBeats.get(0);
					nextBeats.remove(currVoice);
					removeVoice(currVoice, false);
				}
				return true;
			}
		}

		// Busca si hay espacio disponible de silencios entre el componente y el final.. si encuentra mueve todo

		if(moveNextBeats){
			nextVoice = getNextVoice(beats,beat, voiceIndex);
			if(nextVoice != null){
				long requiredPreciseLength = beatPreciseLength - (nextVoice.getBeat().getPreciseStart() - beatPreciseStart);
				long nextSilencePreciseLength = 0;
				TGVoice nextRestBeat = getNextRestVoice(beats, beat.getVoice(voiceIndex));
				while(nextRestBeat != null){
					nextSilencePreciseLength += nextRestBeat.getDuration().getPreciseTime();
					nextRestBeat = getNextRestVoice(beats, nextRestBeat);
				}

				if(requiredPreciseLength <= nextSilencePreciseLength){
					List<TGVoice> voices = getVoicesBeforeEndPrecise(measure.getBeats(), nextVoice.getBeat().getPreciseStart(), voiceIndex);
					while(!voices.isEmpty()){
						TGVoice currVoice = voices.get(0);
						if(currVoice.isRestVoice()){
							requiredPreciseLength -= currVoice.getDuration().getPreciseTime();
							removeVoice(currVoice, false);
						}else if(requiredPreciseLength > 0){
							moveVoicePrecise(currVoice,requiredPreciseLength);
						}
						voices.remove(0);
					}
					return true;
				}
			}
		}


		// como ultimo intento, asigno la duracion de cualquier componente existente en el lugar.
		if(setCurrentDuration && currentVoice != null && !currentVoice.isEmpty()){
			duration.copyFrom( currentVoice.getDuration() );
			return true;
		}
		return false;
	}

	public boolean moveVoicesPrecise(TGMeasure measure,long preciseStart,long thePreciseMove, int voiceIndex, TGDuration fillDuration){
		if( thePreciseMove == 0 ){
			return false;
		}
		boolean success = true;
		long measurePreciseStart = measure.getPreciseStart();
		long measurePreciseEnd =  (measurePreciseStart + measure.getPreciseLength());

		List<TGVoice> voicesToMove = getVoicesBeforeEndPrecise(measure.getBeats(), preciseStart, voiceIndex);
		List<TGVoice> voicesToRemove = new ArrayList<TGVoice>();
		List<TGBeat> currentBeats = getBeatsBeforeEndPrecise(measure.getBeats(), preciseStart);

		// Verifica los silencios a eliminar al principio del compas
		TGVoice first = getFirstVoice( currentBeats, voiceIndex );
		while(first != null && first.isRestVoice() && (!first.getBeat().isTextBeat() || !isUniqueVoice(first,false)) && (first.getBeat().getPreciseStart() + thePreciseMove) < measurePreciseStart){
			currentBeats.remove(first.getBeat());
			voicesToRemove.add(first);
			first = getNextVoice( currentBeats,first.getBeat(), voiceIndex);
		}

		// Verifica los silencios a eliminar al final del compas
		TGVoice last = getLastVoice(currentBeats, voiceIndex);
		TGDuration lastDuration = (last != null ? last.getDuration() : null);
		while(last != null && lastDuration != null && last.isRestVoice() && (!last.getBeat().isTextBeat() || !isUniqueVoice(last,false)) && (last.getBeat().getPreciseStart() + lastDuration.getPreciseTime() + thePreciseMove) > measurePreciseEnd  ){
			currentBeats.remove(last.getBeat());
			voicesToRemove.add(last);
			last = getPreviousVoice(currentBeats,last.getBeat(), voiceIndex);
			lastDuration = (last != null ? last.getDuration() : null);
		}

		// Si el primer o ultimo componente, quedan fuera del compas, entonces el movimiento no es satisfactorio
		if(first != null && last != null && lastDuration != null){
			if((first.getBeat().getPreciseStart() + thePreciseMove) < measurePreciseStart || (last.getBeat().getPreciseStart() + lastDuration.getPreciseTime() + thePreciseMove) > measurePreciseEnd){
				success = false;
			}
		}

		if(success){
			this.moveVoicesPrecise(voicesToMove, thePreciseMove);

			// Elimino los silencios que quedaron fuera del compas.
			Iterator<TGVoice> it = voicesToRemove.iterator();
			while( it.hasNext() ){
				TGVoice beat = it.next();
				removeVoice(beat);
			}

			// Se crean silencios en los espacios vacios, si la duracion fue especificada.
			if( fillDuration != null ){
				if( thePreciseMove < 0 ){
					last = getLastVoice(measure.getBeats(), voiceIndex);
					lastDuration = (last != null ? last.getDuration() : null);
					long beatPreciseStart = ( (last != null && lastDuration != null ? last.getBeat().getPreciseStart()  + lastDuration.getPreciseTime() : preciseStart  )  );
					if( (beatPreciseStart + fillDuration.getPreciseTime()) <= measurePreciseEnd ){
						boolean beatNew = false;
						TGBeat beat = getBeatPrecise(measure, beatPreciseStart);
						if(beat == null){
							beat = getSongManager().getFactory().newBeat();
							beat.setPreciseStart( beatPreciseStart );
							beatNew = true;
						}
						TGVoice voice = beat.getVoice(voiceIndex);
						voice.setEmpty(false);
						voice.getDuration().copyFrom( fillDuration );
						if( beatNew ){
							addBeat(measure, beat );
						}
					}
				}
				else{
					first = getFirstVoice(getBeatsBeforeEndPrecise(measure.getBeats(), preciseStart), voiceIndex);
					if( (preciseStart + fillDuration.getPreciseTime()) <= (first != null ?first.getBeat().getPreciseStart() : measurePreciseEnd ) ){
						boolean beatNew = false;
						TGBeat beat = getBeatPrecise(measure, preciseStart);
						if(beat == null){
							beat = getSongManager().getFactory().newBeat();
							beat.setPreciseStart( preciseStart );
							beatNew = true;
						}
						TGVoice voice = beat.getVoice(voiceIndex);
						voice.setEmpty(false);
						voice.getDuration().copyFrom( fillDuration );
						if( beatNew ){
							addBeat(measure, beat );
						}
					}
				}
			}

			// Borro todos los beats que quedaron vacios.
			this.removeEmptyBeats(measure);
		}

		return success;
	}

	public boolean isUniqueVoice(TGVoice voice, boolean ignoreRests){
		TGBeat beat = voice.getBeat();
		for( int v = 0 ; v < beat.countVoices() ; v ++ ){
			if( v != voice.getIndex() ){
				TGVoice currentVoice = beat.getVoice( v );
				if( !currentVoice.isEmpty() && (!ignoreRests || !currentVoice.isRestVoice())){
					return false;
				}
			}
		}
		return true;
	}

	public void transposeNotes( TGMeasure measure, int transposition , boolean tryKeepString, boolean applyToChords, int applyToString){
		if( transposition != 0 ){
			if( measure != null ){
				TGTrack track = measure.getTrack();
				if( track != null ){
					List<TGString> strings = getSortedStringsByValue(track, ( transposition > 0 ? 1 : -1 ) ) ;
					for( int i = 0 ; i < measure.countBeats() ; i ++ ){
						TGBeat beat = measure.getBeat( i );
						transposeNotes( beat, strings, transposition , tryKeepString, applyToChords, applyToString, track.getMaxFret());
					}
				}
			}
		}
	}

	public void transposeNotes( TGMeasure measure, int[] transpositionStrings , boolean tryKeepString , boolean applyToChords){
		if( transpositionStrings != null && transpositionStrings.length > 0 ){
			if( measure != null ){
				TGTrack track = measure.getTrack();
				if( track != null ){
					TGNote[] notes = new TGNote[ transpositionStrings.length ];

					for( int b = 0 ; b < measure.countBeats() ; b ++ ){
						TGBeat beat = measure.getBeat( b );

						for( int n = 0 ; n < notes.length ; n ++ ){
							notes[ n ] = getNote( beat, (n + 1) );
						}

						for( int i = 0 ; i < notes.length ; i ++ ){
							if( notes[ i ] != null ){
								int transposition = transpositionStrings[ i ];
								if( transposition != 0 ){
									int applyToString = notes[i].getString();
									List<TGString> strings = getSortedStringsByValue(track, ( transposition > 0 ? 1 : -1 ) ) ;
									transposeNotes( beat, strings, transposition , tryKeepString, applyToChords, applyToString, track.getMaxFret() );
								}
							}
						}
					}
				}
			}
		}
	}

	public void transposeNotes( TGBeat beat, List<TGString> strings, int transposition , boolean tryKeepString, boolean applyToChord, int applyToString, int maxFret){
		if( transposition != 0 ){
			List<TGNote> notes = getNotes(beat);

			int stringCount = strings.size();
			for( int i = 0 ; i < stringCount ; i ++ ){
				TGString string = strings.get( (stringCount - i) - 1 );
				if( applyToString == -1 || string.getNumber() == applyToString ){
					TGNote note = null;
					for( int n = 0 ; n < notes.size() ; n ++ ){
						TGNote current = notes.get( n );
						if( current.getString() == string.getNumber() ){
							note = current;
						}
					}
					if( note != null ){
						transposeNote(note, notes, strings, transposition, tryKeepString, false, maxFret );
					}

					if( applyToChord && beat.isChordBeat() ){
						TGChord chord = beat.getChord();
						int chordString = ( string.getNumber() - 1 );
						if( chord.getFretValue( chordString ) >= 0 ){
							transposeChordNote(chord, chordString, strings, transposition, tryKeepString, false, maxFret);
						}
					}
				}
			}
			if( applyToChord && beat.isChordBeat() ){
				TGChord chord = beat.getChord();
				chord.setFirstFret( -1 );
				chord.setName("");
			}
		}
	}

	private boolean transposeNote( TGNote note, List<TGNote> notes, List<TGString> strings , int transposition , boolean tryKeepString, boolean forceChangeString, int maxFret ){
		boolean canTransposeFret = false;

		int transposedFret = ( note.getValue() + transposition );

		// Check if transposition could be done without change the string
		if( transposedFret >= 0 && transposedFret <= maxFret ){
			// Do it now if keep string is the priority
			if( !forceChangeString && tryKeepString ){
				note.setValue( transposedFret );
				return true;
			}
			canTransposeFret = true;
		}

		// Check the current string index for this note
		int stringIndex = -1;
		for( int i = 0 ; i < strings.size() ; i ++ ){
			TGString string = ( TGString ) strings.get( i );
			if( string.getNumber() == note.getString() ){
				stringIndex = i;
				break;
			}
		}

		// Try to change the string of the note
		TGString string = ( TGString ) strings.get( stringIndex );
		int transposedValue = ( string.getValue() + note.getValue() + transposition );
		int nextStringIndex = ( stringIndex + 1 );
		while( nextStringIndex >= 0 && nextStringIndex < strings.size() ){
			TGString nextString = ( TGString ) strings.get( nextStringIndex );
			TGNote nextOwner = null;
			for( int i = 0 ; i < notes.size() ; i ++ ){
				TGNote nextNote =  notes.get( i );
				if( nextNote.getString() == nextString.getNumber() ){
					nextOwner = nextNote;
				}
			}

			int transposedStringFret = ( transposedValue - nextString.getValue() );
			if( transposedStringFret >= 0 && transposedStringFret <= maxFret ){
				if( nextOwner != null ){
					if( ! transposeNote(nextOwner, notes, strings, 0 , tryKeepString , !canTransposeFret, maxFret ) ){
						// Note was removed.
						nextOwner = null ;
					}
				}
				if( nextOwner == null || nextOwner.getString() != nextString.getNumber() ){
					note.setValue( transposedStringFret );
					note.setString( nextString.getNumber() );

					return true;
				}
			}
			nextStringIndex ++;
		}

		// Keep using same string if it's possible
		if( !forceChangeString && canTransposeFret ){
			note.setValue( transposedFret );
			return true;
		}

		// If note can't be transposed, it must be removed.
		notes.remove( note );
		removeNote(note);

		return false;
	}

	private boolean transposeChordNote( TGChord chord, int chordString, List<TGString> strings , int transposition , boolean tryKeepString, boolean forceChangeString, int maximumFret ){
		boolean canTransposeFret = false;

		int noteValue = chord.getFretValue( chordString );
		int noteString = (chordString + 1 );

		int transposedFret = ( noteValue + transposition );

		// Check if transposition could be done without change the string
		if( transposedFret >= 0 && transposedFret <= maximumFret ){
			// Do it now if keep string is the priority
			if( !forceChangeString && tryKeepString ){
				chord.addFretValue(chordString, transposedFret);
				return true;
			}
			canTransposeFret = true;
		}

		// Check the current string index for this note
		int stringIndex = -1;
		for( int i = 0 ; i < strings.size() ; i ++ ){
			TGString string = ( TGString ) strings.get( i );
			if( string.getNumber() == noteString ){
				stringIndex = i;
				break;
			}
		}

		// Try to change the string of the note
		TGString string = ( TGString ) strings.get( stringIndex );
		int transposedValue = ( string.getValue() + noteValue + transposition );
		int nextStringIndex = ( stringIndex + 1 );
		while( nextStringIndex >= 0 && nextStringIndex < strings.size() ){
			TGString nextString = ( TGString ) strings.get( nextStringIndex );
			int nextChordString = -1;
			for( int i = 0 ; i < chord.countStrings() ; i ++ ){
				if( (i + 1) == nextString.getNumber() ){
					if( chord.getFretValue( i ) >= 0 ){
						nextChordString = i;
					}
				}
			}

			int transposedStringFret = ( transposedValue - nextString.getValue() );
			if( transposedStringFret >= 0 && transposedStringFret <= maximumFret ){
				if( nextChordString >= 0 ){
					transposeChordNote(chord, nextChordString , strings, 0 , tryKeepString , !canTransposeFret , maximumFret);
				}
				if( nextChordString < 0 || chord.getFretValue( nextChordString ) < 0 ){
					chord.addFretValue( chordString , -1 );
					chord.addFretValue( ( nextString.getNumber() - 1 ) , transposedStringFret );

					return true;
				}
			}
			nextStringIndex ++;
		}

		// Keep using same string if it's possible
		if( !forceChangeString && canTransposeFret ){
			chord.addFretValue( chordString , transposedFret );
			return true;
		}

		// If note can't be transposed, it must be removed.
		chord.addFretValue( chordString , -1 );

		return false;
	}

	public List<TGString> getSortedStringsByValue( TGTrack track , final int direction ){
		List<TGString> strings = new ArrayList<TGString>();
		for( int number = 1 ; number <= track.stringCount() ; number ++ ){
			strings.add( track.getString( number ) );
		}

		Collections.sort( strings , new Comparator<TGString>() {
			public int compare(TGString s1, TGString s2) {
				if( s1 != null && s2 != null ){
					int status = ( s1.getValue() - s2.getValue() );
					if( status == 0 ){
						return 0;
					}
					return ( (status * direction) > 0 ? 1 : -1 );
				}
				return 0;
			}
		});

		return strings;
	}

	public int getRealNoteValue(TGNote note) {
		int value = note.getValue();
		TGVoice voice = note.getVoice();
		if( voice != null ) {
			TGBeat beat = voice.getBeat();
			if( beat != null ) {
				TGMeasure measure = beat.getMeasure();
				if( measure != null ) {
					TGTrack track = measure.getTrack();
					if( track != null ) {
						TGString string = track.getString(note.getString());
						if( string != null ) {
							value += string.getValue();
						}
					}
				}
			}
		}
		return value;
	}

	public void removeOverlappingRestBeats(TGMeasure measure) {
		List<TGBeat> beatsToRemove = new ArrayList<TGBeat>();
		for (TGBeat refBeat : measure.getBeats()) {
			for (TGBeat beat : measure.getBeats()) {
				long beatPreciseEnd = beat.getPreciseStart() + this.getMaximumDuration(beat).getPreciseTime();
				long refBeatPreciseEnd = refBeat.getPreciseStart() + this.getMaximumDuration(refBeat).getPreciseTime();
				if (beat.isRestBeat() && !refBeat.isRestBeat() && (!beatsToRemove.contains(beat))
					&& (beatPreciseEnd > refBeat.getPreciseStart()) && (beat.getPreciseStart() < refBeatPreciseEnd)) {
						beatsToRemove.add(beat);
				}
			}
		}
		while (beatsToRemove.size()>0) {
			this.removeBeat(beatsToRemove.get(0));
			beatsToRemove.remove(0);
		}

	}
}
