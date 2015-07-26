package org.herac.tuxguitar.song.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGStroke;
import org.herac.tuxguitar.song.models.TGText;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.song.models.effects.TGEffectBend;
import org.herac.tuxguitar.song.models.effects.TGEffectGrace;
import org.herac.tuxguitar.song.models.effects.TGEffectHarmonic;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloBar;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloPicking;
import org.herac.tuxguitar.song.models.effects.TGEffectTrill;

public class TGMeasureManager {
	private TGSongManager songManager;
	
	public TGMeasureManager(TGSongManager songManager){
		this.songManager = songManager;
	}
	
	public TGSongManager getSongManager(){
		return this.songManager;
	}
	
	public void orderBeats(TGMeasure measure){
		for(int i = 0;i < measure.countBeats();i++){
			TGBeat minBeat = null;
			for(int j = i;j < measure.countBeats();j++){
				TGBeat beat = measure.getBeat(j);
				if(minBeat == null || beat.getStart() < minBeat.getStart()){
					minBeat = beat;
				}
			}
			measure.moveBeat(i, minBeat);
		}
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
			long start = beat.getStart();
			long length = (minimumDuration != null ? minimumDuration.getTime() : 0);
			
			TGBeat next = getNextBeat(measure.getBeats(),beat);
			if(next != null){
				length = next.getStart() - start;
			}
			moveBeatsInMeasure(beat.getMeasure(),start + length,-length, minimumDuration);
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
		
//		TGBeat beat = getBeat(measure, start);
//		if( beat != null ){
//			addNote(beat, note, duration, voice);
//		}
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
	
	public void removeNote(TGNote note, boolean checkRestBeat){
		//note.getVoice().removeNote(note);
		TGVoice voice = note.getVoice();
		if( voice != null ){
			// Remove the note
			voice.removeNote(note);
			
			TGBeat beat = voice.getBeat();
			if(checkRestBeat && beat.isRestBeat()){
				//Anulo un posible stroke
				beat.getStroke().setDirection( TGStroke.STROKE_NONE );
				
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
	
	public TGNote getPreviousNote(TGMeasure measure,long start, int voiceIndex, int string) {
		TGBeat beat = getBeat(measure, start);
		if( beat != null ){
			TGBeat previous = getPreviousBeat(measure.getBeats(),beat);
			while(previous != null){
				TGVoice voice = previous.getVoice(voiceIndex);
				if(!voice.isEmpty()){
					for (int i = 0; i < voice.countNotes(); i++) {
						TGNote current = voice.getNote(i);
						if (current.getString() == string) {
							return current;
						}
					}
				}
				previous = getPreviousBeat(measure.getBeats(),previous);
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
				if(minimumDuration == null || voice.getDuration().getTime() < minimumDuration.getTime()){
					minimumDuration = voice.getDuration();
				}
			}
		}
		return minimumDuration;
	}
	
	public TGBeat getBeat(TGTrack track,long start) {
		Iterator<TGMeasure> measures = track.getMeasures();
		while( measures.hasNext() ){
			TGMeasure measure = measures.next();
			Iterator<TGBeat> beats = measure.getBeats().iterator();
			while(beats.hasNext()){
				TGBeat beat = beats.next();
				if (beat.getStart() == start) {
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
		TGMeasure newMeasure = getSongManager().getTrackManager().getMeasureAt(track, beat.getStart() );
		if( newMeasure == null ){
			boolean createNewMeasure = newMeasureAlsoForRestBeats;
			if( !createNewMeasure ){
				createNewMeasure = ( !beat.isRestBeat() || beat.isTextBeat() );
			}
			if( createNewMeasure ){
				
				while( newMeasure == null && beat.getStart() >= TGDuration.QUARTER_TIME){
					getSongManager().addNewMeasureBeforeEnd(track.getSong());
					newMeasure = getSongManager().getTrackManager().getMeasureAt(track, beat.getStart() );
				}
			}
		}
		if( newMeasure != null ){
			long mStart = newMeasure.getStart();
			long mLength = newMeasure.getLength();
			long bStart = beat.getStart();
			for( int v = 0 ; v < beat.countVoices() ; v ++ ){
				TGVoice voice = beat.getVoice( v );
				long vDuration = voice.getDuration().getTime();
				if(!voice.isEmpty() && (bStart + vDuration) > (mStart + mLength) ){
					long vTiedDuration = ( (bStart + vDuration) - (mStart + mLength) );
					vDuration -= vTiedDuration;
					if( vDuration > 0 ){
						TGDuration duration = TGDuration.fromTime(getSongManager().getFactory(), vDuration);
						if( duration != null ){
							voice.getDuration().copyFrom( duration );
						}
					}
					if( vTiedDuration > 0 ) {
						TGDuration newVoiceDuration = TGDuration.fromTime(getSongManager().getFactory(), vTiedDuration);
						if( newVoiceDuration != null ){
							long newBeatStart = (bStart + vDuration);
							TGBeat newBeat = getBeat(track, newBeatStart);
							if( newBeat == null ){
								newBeat = getSongManager().getFactory().newBeat();
								newBeat.setStart( (bStart + vDuration) );
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
	
	public boolean moveBeatsInMeasure(TGMeasure measure,long start,long theMove, TGDuration fillDuration){
		if( theMove == 0 ){
			return false;
		}
		boolean success = true;
		long measureStart = measure.getStart();
		long measureEnd =  (measureStart + measure.getLength());
		
		// Muevo los componentes
		List<TGBeat> beatsToMove = getBeatsBeforeEnd(measure.getBeats(),start);
		moveBeats(beatsToMove,theMove);
		
		if(success){
			List<TGBeat> beatsToRemove = new ArrayList<TGBeat>();
			List<TGBeat> beats = new ArrayList<TGBeat>(measure.getBeats());
			
			// Verifica los silencios a eliminar al principio del compas
			TGBeat first = getFirstBeat( beats );
			while(first != null && first.isRestBeat() && !first.isTextBeat() && first.getStart() < measureStart){
				beats.remove(first);
				beatsToRemove.add(first);
				first = getNextBeat( beats,first);
			}
			
			// Verifica los silencios a eliminar al final del compas
			TGBeat last = getLastBeat(beats);
			TGDuration lastDuration = (last != null ? getMinimumDuration(last) : null);
			while(last != null && lastDuration != null && last.isRestBeat() && !last.isTextBeat()  && (last.getStart() + lastDuration.getTime() ) > measureEnd  ){
				beats.remove(last);
				beatsToRemove.add(last);
				last = getPreviousBeat(beats,last);
				lastDuration = (last != null ? getMinimumDuration(last) : null);
			}
			
			// Si el primer o ultimo componente, quedan fuera del compas, entonces el movimiento no es satisfactorio
			if(first != null && last != null && lastDuration != null){
				if(first.getStart() < measureStart || (last.getStart() + lastDuration.getTime()) > measureEnd){
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
					if( theMove < 0 ){
						last = getLastBeat(measure.getBeats());
						lastDuration = (last != null ? getMinimumDuration(last) : null);
						TGBeat beat = getSongManager().getFactory().newBeat();
						beat.setStart( (last != null && lastDuration != null ? last.getStart()  + lastDuration.getTime() : start  )  );
						if( (beat.getStart() + fillDuration.getTime()) <= measureEnd ){
							for(int v = 0; v < beat.countVoices(); v ++){
								TGVoice voice = beat.getVoice(v);
								voice.setEmpty(false);
								voice.getDuration().copyFrom( fillDuration );
							}
							addBeat(measure, beat );
						}
					}
					else{
						first = getFirstBeat(getBeatsBeforeEnd(measure.getBeats(),start));
						TGBeat beat = getSongManager().getFactory().newBeat();
						beat.setStart( start );
						if( (beat.getStart() + fillDuration.getTime()) <= (first != null ?first.getStart() : measureEnd ) ){
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
			moveBeats(beatsToMove,-theMove);
		}
		
		return success;
	}
	
	public void moveAllBeats(TGMeasure measure,long theMove){
		moveBeats(measure.getBeats(),theMove);
	}
	
	public void moveBeats(TGMeasure measure, long start, long theMove){
		moveBeats(getBeatsBeforeEnd(measure.getBeats(), start),theMove);
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
	
	/**
	 * Mueve el componente
	 */
	private void moveBeat(TGBeat beat,long theMove){
		//obtengo el start viejo
		long start = beat.getStart();
		
		//asigno el nuevo start
		beat.setStart(start + theMove);
	}
	
	public void cleanBeat(TGBeat beat){
		beat.getStroke().setDirection( TGStroke.STROKE_NONE );
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
	
	public void cleanBeatNotes(TGMeasure measure, long start){
		TGBeat beat = getBeat(measure, start);
		if(beat != null){
			cleanBeatNotes(beat);
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
	public void addChord(TGMeasure measure,long start, TGChord chord){
		TGBeat beat = getBeat(measure, start);
		if(beat != null){
			addChord(beat, chord);
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
	public void addText(TGMeasure measure,long start, TGText text){
		TGBeat beat = getBeat(measure, start);
		if(beat != null){
			addText(beat, text);
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
	 * Retorna el texto en la position
	 */
	public TGText getText(TGMeasure measure,long start) {
		TGBeat beat = getBeat(measure, start);
		if(beat != null){
			return beat.getText();
		}
		return null;
	}
	
	/**
	 * Borra el texto en el pulso
	 */
	public void removeText(TGBeat beat){
		beat.removeText();
	}
	
	/**
	 * Borra el texto en la position
	 */
	public boolean removeText(TGMeasure measure,long start) {
		TGBeat beat = getBeat(measure, start);
		if(beat != null){
			removeText(beat);
			return true;
		}
		return false;
	}
	
	public void cleanMeasure(TGMeasure measure){
		while( measure.countBeats() > 0){
			removeBeat( measure.getBeat(0));
		}
	}
	
	/**
	 * Mueve la nota a la cuerda de arriba
	 */
	public int shiftNoteUp(TGMeasure measure,long start,int string){
		return shiftNote(measure, start, string,-1);
	}
	
	/**
	 * Mueve la nota a la cuerda de abajo
	 */
	public int shiftNoteDown(TGMeasure measure,long start,int string){
		return shiftNote(measure, start, string,1);
	}
	
	/**
	 * Mueve la nota a la siguiente cuerda
	 */
	private int shiftNote(TGMeasure measure,long start,int string,int move){
		TGNote note = getNote(measure,start,string);
		if(note != null){
			int nextStringNumber = (note.getString() + move);
			while(getNote(measure,start,nextStringNumber) != null){
				nextStringNumber += move;
			}
			if(nextStringNumber >= 1 && nextStringNumber <= measure.getTrack().stringCount()){
				TGString currentString = measure.getTrack().getString(note.getString());
				TGString nextString = measure.getTrack().getString(nextStringNumber);
				int noteValue = (note.getValue() + currentString.getValue());
				boolean percussionChannel = getSongManager().isPercussionChannel(measure.getTrack().getSong(), measure.getTrack().getChannelId());
				if(noteValue >= nextString.getValue() && ((nextString.getValue() + 30 > noteValue) || percussionChannel) ){
					note.setValue(noteValue - nextString.getValue());
					note.setString(nextString.getNumber());
					return note.getString();
				}
			}
		}
		return 0;
	}
	
	/**
	 * Mueve la nota 1 semitono arriba
	 */
	public boolean moveSemitoneUp(TGMeasure measure,long start,int string){
		return moveSemitone(measure, start, string,1);
	}
	
	/**
	 * Mueve la nota 1 semitono abajo
	 */
	public boolean moveSemitoneDown(TGMeasure measure,long start,int string){
		return moveSemitone(measure, start, string,-1);
	}
	
	/**
	 * Mueve la nota los semitonos indicados
	 */
	private boolean moveSemitone(TGMeasure measure,long start,int string,int semitones){
		TGNote note = getNote(measure,start,string);
		if(note != null){
			int newValue = (note.getValue() + semitones);
			if( newValue >= 0 && (newValue < 30 || getSongManager().isPercussionChannel(measure.getTrack().getSong(), measure.getTrack().getChannelId())) ){
				note.setValue(newValue);
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
	
	public void autoCompleteSilences(TGMeasure measure){
		TGBeat beat = getFirstBeat( measure.getBeats() );
		if( beat == null ){
			createSilences(measure, measure.getStart(), measure.getLength(), 0);
			return;
		}
		for( int v = 0 ; v < TGBeat.MAX_VOICES ; v ++ ){
			TGVoice voice = getFirstVoice( measure.getBeats() , v );
			if( voice != null && voice.getBeat().getStart() > measure.getStart() ){
				createSilences(measure, measure.getStart(), (voice.getBeat().getStart() - measure.getStart()), v);
			}
		}
		
		long[] start = new long[beat.countVoices()];
		long[] uncompletedLength = new long[beat.countVoices()];
		for( int v = 0; v < uncompletedLength.length; v ++ ){
			start[v] = 0;
			uncompletedLength[v] = 0;
		}
		
		while (beat != null) {
			for( int v = 0; v < beat.countVoices(); v ++ ){
				TGVoice voice = beat.getVoice( v );
				if( !voice.isEmpty() ){
					long voiceEnd = (beat.getStart() + voice.getDuration().getTime());
					long nextPosition = (measure.getStart() + measure.getLength());
					
					TGVoice nextVoice = getNextVoice(measure.getBeats(), beat, voice.getIndex());
					if(nextVoice != null){
						nextPosition = nextVoice.getBeat().getStart();
					}
					if( voiceEnd < nextPosition ){
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
	
	/**
	 * Crea Silencios temporarios en base a length
	 */
	private void createSilences(TGMeasure measure,long start,long length, int voiceIndex){
		long nextStart = start;
		List<TGDuration> durations = createDurations(getSongManager().getFactory(),length);
		Iterator<TGDuration> it = durations.iterator();
		while(it.hasNext()){
			TGDuration duration = it.next();
			
			boolean isNew = false;
			long beatStart = getRealStart(measure, nextStart);
			TGBeat beat = getBeat(measure, beatStart);
			if( beat == null ){
				beat = getSongManager().getFactory().newBeat();
				beat.setStart( getRealStart(measure, nextStart) );
				isNew = true;
			}
			
			TGVoice voice = beat.getVoice(voiceIndex);
			voice.setEmpty(false);
			voice.getDuration().copyFrom(duration);
			
			if( isNew ){
				addBeat(measure,beat);
			}
			
			nextStart += duration.getTime();
		}
	}
	
	public long getRealStart(TGMeasure measure,long currStart){
		long beatLength = TGSongManager.getDivisionLength(measure.getHeader());
		long start = currStart;
		boolean startBeat = (start % beatLength == 0);
		if(!startBeat){
			TGDuration minDuration = getSongManager().getFactory().newDuration();
			minDuration.setValue(TGDuration.SIXTY_FOURTH);
			minDuration.getDivision().setEnters(3);
			minDuration.getDivision().setTimes(2);
			for(int i = 0;i < minDuration.getTime();i++){
				start ++;
				startBeat = (start % beatLength == 0);
				if(startBeat){
				   break;
				}
			}
			if(!startBeat){
				start = currStart;
			}
		}
		return start;
	}
	
	/** 
	 * Liga la nota
	 */
	public void changeTieNote(TGMeasure measure,long start,int string){
		TGNote note = getNote(measure,start,string);
		if(note != null){
			changeTieNote(note);
		}
	}
	
	/** 
	 * Liga la nota
	 */
	public void changeTieNote(TGNote note){
		note.setTiedNote(!note.isTiedNote());
		note.getEffect().setDeadNote(false);
	}
	
	/** 
	 * Agrega un vibrato
	 */
	public void changeVibratoNote(TGMeasure measure,long start,int string){
		TGNote note = getNote(measure,start,string);
		if(note != null){
			note.getEffect().setVibrato(!note.getEffect().isVibrato());
		}
	}
	
	/** 
	 * Agrega una nota muerta
	 */
	public void changeDeadNote(TGNote note){
		note.getEffect().setDeadNote(!note.getEffect().isDeadNote());
		note.setTiedNote(false);
	}
	
	/** 
	 * Agrega un slide
	 */
	public void changeSlideNote(TGMeasure measure,long start,int string){
		TGNote note = getNote(measure,start,string);
		if(note != null){
			note.getEffect().setSlide(!note.getEffect().isSlide());
		}
	}
	
	/** 
	 * Agrega un hammer
	 */
	public void changeHammerNote(TGMeasure measure,long start,int string){
		TGNote note = getNote(measure,start,string);
		if(note != null){
			note.getEffect().setHammer(!note.getEffect().isHammer());
		}
	}
	
	/** 
	 * Agrega un palm-mute
	 */
	public void changePalmMute(TGMeasure measure,long start,int string){
		TGNote note = getNote(measure,start,string);
		if(note != null){
			note.getEffect().setPalmMute(!note.getEffect().isPalmMute());
		}
	}
	
	/** 
	 * Agrega un staccato
	 */
	public void changeStaccato(TGMeasure measure,long start,int string){
		TGNote note = getNote(measure,start,string);
		if(note != null){
			note.getEffect().setStaccato(!note.getEffect().isStaccato());
		}
	}
	
	/** 
	 * Agrega un let-ring
	 */
	public void changeLetRing(TGMeasure measure,long start,int string){
		TGNote note = getNote(measure,start,string);
		if(note != null){
			note.getEffect().setLetRing(!note.getEffect().isLetRing());
		}
	}
	
	/** 
	 * Agrega un tapping
	 */
	public void changeTapping(TGMeasure measure,long start,int string){
		TGNote note = getNote(measure,start,string);
		if(note != null){
			note.getEffect().setTapping(!note.getEffect().isTapping());
		}
	}
	
	/** 
	 * Agrega un slapping
	 */
	public void changeSlapping(TGMeasure measure,long start,int string){
		TGNote note = getNote(measure,start,string);
		if(note != null){
			note.getEffect().setSlapping(!note.getEffect().isSlapping());
		}
	}
	
	/** 
	 * Agrega un popping
	 */
	public void changePopping(TGMeasure measure,long start,int string){
		TGNote note = getNote(measure,start,string);
		if(note != null){
			note.getEffect().setPopping(!note.getEffect().isPopping());
		}
	}
	
	/** 
	 * Agrega un bend
	 */
	public void changeBendNote(TGMeasure measure,long start,int string,TGEffectBend bend){
		TGNote note = getNote(measure,start,string);
		if(note != null){
			note.getEffect().setBend(bend);
		}
	}
	
	/** 
	 * Agrega un tremoloBar
	 */
	public void changeTremoloBar(TGMeasure measure,long start,int string,TGEffectTremoloBar tremoloBar){
		TGNote note = getNote(measure,start,string);
		if(note != null){
			note.getEffect().setTremoloBar(tremoloBar);
		}
	}
	
	/** 
	 * Agrega un GhostNote
	 */
	public void changeGhostNote(TGMeasure measure,long start,int string){
		TGNote note = getNote(measure,start,string);
		if(note != null){ 
			note.getEffect().setGhostNote(!note.getEffect().isGhostNote());
		}
	}
	
	/** 
	 * Agrega un AccentuatedNote
	 */
	public void changeAccentuatedNote(TGMeasure measure,long start,int string){
		TGNote note = getNote(measure,start,string);
		if(note != null){ 
			note.getEffect().setAccentuatedNote(!note.getEffect().isAccentuatedNote());
		}
	}
	
	/** 
	 * Agrega un GhostNote
	 */
	public void changeHeavyAccentuatedNote(TGMeasure measure,long start,int string){
		TGNote note = getNote(measure,start,string);
		if(note != null){
			note.getEffect().setHeavyAccentuatedNote(!note.getEffect().isHeavyAccentuatedNote());
		}
	}
	
	/** 
	 * Agrega un harmonic
	 */
	public void changeHarmonicNote(TGMeasure measure,long start,int string,TGEffectHarmonic harmonic){
		TGNote note = getNote(measure,start,string);
		if(note != null){
			note.getEffect().setHarmonic(harmonic);
		}
	}
	
	/** 
	 * Agrega un grace
	 */
	public void changeGraceNote(TGMeasure measure,long start,int string,TGEffectGrace grace){
		TGNote note = getNote(measure,start,string);
		if(note != null){
			note.getEffect().setGrace(grace);
		}
	}
	
	/** 
	 * Agrega un trill
	 */
	public void changeTrillNote(TGMeasure measure,long start,int string,TGEffectTrill trill){
		TGNote note = getNote(measure,start,string);
		if(note != null){
			note.getEffect().setTrill(trill);
		}
	}
	
	/** 
	 * Agrega un tremolo picking
	 */
	public void changeTremoloPicking(TGMeasure measure,long start,int string,TGEffectTremoloPicking tremoloPicking){
		TGNote note = getNote(measure,start,string);
		if(note != null){
			note.getEffect().setTremoloPicking(tremoloPicking);
		}
	}
	
	/** 
	 * Agrega un fadeIn
	 */
	public void changeFadeIn(TGMeasure measure,long start,int string){
		TGNote note = getNote(measure,start,string);
		if(note != null){
			note.getEffect().setFadeIn(!note.getEffect().isFadeIn());
		}
	}
	
	/** 
	 * Cambia el Velocity
	 */
	public void changeVelocity(int velocity,TGMeasure measure,long start,int string){
		TGNote note = getNote(measure,start,string);
		if(note != null){
			note.setVelocity(velocity);
		}
	}
	
	public static List<TGDuration> createDurations(TGFactory factory,long time){
		List<TGDuration> durations = new ArrayList<TGDuration>();
		TGDuration minimum = factory.newDuration();
		minimum.setValue(TGDuration.SIXTY_FOURTH);
		minimum.setDotted(false);
		minimum.setDoubleDotted(false);
		minimum.getDivision().setEnters(3);
		minimum.getDivision().setTimes(2);
		
		long missingTime = time;
		while( missingTime > minimum.getTime() ){
			TGDuration duration = TGDuration.fromTime(factory, missingTime, minimum ,  10);
			durations.add( duration.clone(factory) );
			missingTime -= duration.getTime();
		}
		return durations;
	}
	
	/**
	 * Retorna el Siguiente Componente
	 */
	public TGVoice getNextVoice(List<TGBeat> beats,TGBeat beat, int index) {
		TGVoice next = null;
		for (int i = 0; i < beats.size(); i++) {
			TGBeat current =  beats.get(i);
			if (current.getStart() > beat.getStart() && !current.getVoice(index).isEmpty()) {
				if (next == null) {
					next = current.getVoice(index);
				} else if (current.getStart() < next.getBeat().getStart()) {
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
	
	public List<TGVoice> getVoicesBeforeEnd(List<TGBeat> beats,long fromStart, int index) {
		List<TGVoice> list = new ArrayList<TGVoice>();
		Iterator<TGBeat> it = beats.iterator();
		while(it.hasNext()){
			TGBeat beat = it.next();
			if (beat.getStart() >= fromStart) {
				TGVoice voice = beat.getVoice(index);
				if(!voice.isEmpty()){
					list.add(voice);
				}
			}
		}
		return list;
	}
	
	public void addSilence(TGMeasure measure,long start, TGDuration duration, int voice){
		TGBeat beat = getBeat(measure, start);
		if(beat != null){
			addSilence(beat, duration, voice);
		}
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
			long start = voice.getBeat().getStart();
			long length = voice.getDuration().getTime();
			TGVoice next = getNextVoice(voice.getBeat().getMeasure().getBeats(),voice.getBeat(),voice.getIndex());
			if(next != null){
				length = next.getBeat().getStart() - start;
			}
			moveVoices(voice.getBeat().getMeasure(),start + length,-length, voice.getIndex(), voice.getDuration());
		}
	}
	
	public void removeVoice(TGMeasure measure,long start,int index, boolean moveNextComponents){
		TGBeat beat = getBeat(measure, start);
		if(beat != null){
			removeVoice(beat.getVoice(index), moveNextComponents);
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
	
	public void changeDuration(TGMeasure measure,TGBeat beat,TGDuration duration,int voice, boolean tryMove){
		//obtengo la duracion vieja
		TGDuration oldDuration = beat.getVoice(voice).getDuration().clone(getSongManager().getFactory());
		
		//si no entra vuelvo a dejar la vieja
		if(validateDuration(measure,beat, voice, duration,tryMove,false)){
			//se lo agrego a todas las notas en la posicion
			beat.getVoice(voice).setDuration(duration.clone(getSongManager().getFactory()));
			
			//trato de agregar un silencio similar al lado
			tryChangeSilenceAfter(measure,beat.getVoice(voice));
		}else{
			beat.getVoice(voice).getDuration().copyFrom( oldDuration );
		}
	}
	
	public void tryChangeSilenceAfter(TGMeasure measure,TGVoice voice){
		autoCompleteSilences(measure);
		
		TGVoice nextVoice = getNextVoice(measure.getBeats(),voice.getBeat(),voice.getIndex());
		
		long beatEnd = (voice.getBeat().getStart() + voice.getDuration().getTime());
		long measureEnd = (measure.getStart() + measure.getLength());
		if(nextVoice != null && !nextVoice.isEmpty() && nextVoice.isRestVoice() && beatEnd <= measureEnd){
			long theMove = (getRealStart(measure,beatEnd)) - getRealStart(measure,nextVoice.getBeat().getStart());
			if((nextVoice.getBeat().getStart() + theMove) < measureEnd && (nextVoice.getBeat().getStart() + nextVoice.getDuration().getTime() + theMove) <= measureEnd){
				moveVoice(nextVoice,theMove);
				changeDuration(measure,nextVoice.getBeat(),voice.getDuration().clone(getSongManager().getFactory()),nextVoice.getIndex(),false);
			}
		}
	}
	
	private void moveVoices(List<TGVoice> voices,long theMove){
		int count = voices.size();
		for( int i = 0 ; i < count ; i ++ ){
			TGVoice voice = voices.get( (theMove < 0 ? i : ( (count - 1) - i ) ) );
			moveVoice(voice,theMove);
		}
	}
	
	public void moveVoice(TGVoice voice, long theMove){
		long newStart = (voice.getBeat().getStart() + theMove);
		
		TGBeat newBeat = getBeat(voice.getBeat().getMeasure(),newStart);
		if( newBeat == null ){
			newBeat = getSongManager().getFactory().newBeat();
			newBeat.setStart( newStart );
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
				}
				// Make sure to remove another voice instance from old beat.
				TGVoice newVoice = getSongManager().getFactory().newVoice(voice.getIndex());
				currentBeat.setVoice( voice.getIndex(), newVoice);
				this.removeVoice(newVoice);
			}
			beat.setVoice( voice.getIndex() , voice);
		}
	}
	
	public boolean validateDuration(TGMeasure measure,TGBeat beat,int voice, TGDuration duration,boolean moveNextBeats, boolean setCurrentDuration){
		int errorMargin = 10;
		this.orderBeats(measure);
		long measureStart = measure.getStart();
		long measureEnd =  (measureStart + measure.getLength());
		long beatStart = beat.getStart();
		long beatLength = duration.getTime();
		long beatEnd = (beatStart + beatLength);
		List<TGBeat> beats = measure.getBeats();
		
		//Verifico si hay un beat en el mismo lugar, y comparo las duraciones.
		TGBeat currentBeat = getBeat(measure,beatStart);
		TGVoice currentVoice = null;
		if(currentBeat != null){
			currentVoice = currentBeat.getVoice(voice);
			if(!currentVoice.isEmpty() && beatLength <= currentVoice.getDuration().getTime()){
				return true;
			}
		}
		
		//Verifico si hay lugar para meter el beat
		TGVoice nextVoice = getNextVoice(beats,beat, voice);
		if(currentVoice == null || currentVoice.isEmpty()){
			if((nextVoice == null || nextVoice.isEmpty()) && beatEnd < (measureEnd + errorMargin)){
				return true;
			}
			if((nextVoice != null && !nextVoice.isEmpty()) && beatEnd < (nextVoice.getBeat().getStart() + errorMargin)){
				return true;
			}
		}
		
		// Busca si hay espacio disponible de silencios entre el componente y el el que le sigue.. si encuentra lo borra
		if(nextVoice != null && !nextVoice.isEmpty() && nextVoice.isRestVoice()){
			//Verifico si lo que sigue es un silencio. y lo borro
			long nextBeatEnd = 0;
			List<TGVoice> nextBeats = new ArrayList<TGVoice>();
			while(nextVoice != null && !nextVoice.isEmpty() && nextVoice.isRestVoice() && !nextVoice.getBeat().isTextBeat()){
				nextBeats.add(nextVoice);
				nextBeatEnd = nextVoice.getBeat().getStart() + nextVoice.getDuration().getTime();
				nextVoice = getNextVoice(beats,nextVoice.getBeat(), voice);
			}
			if(nextVoice == null || nextVoice.isEmpty()){
				nextBeatEnd = measureEnd;
			}else if(!nextVoice.isRestVoice() || nextVoice.getBeat().isTextBeat()){
				nextBeatEnd = nextVoice.getBeat().getStart();
			}
			if(beatEnd <= (nextBeatEnd + errorMargin)){
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
			nextVoice = getNextVoice(beats,beat, voice);
			if(nextVoice != null){
				long requiredLength = (beatLength  - (nextVoice.getBeat().getStart() - beatStart));
				long nextSilenceLength = 0;
				TGVoice nextRestBeat = getNextRestVoice(beats, beat.getVoice(voice));
				while(nextRestBeat != null){ 
					nextSilenceLength += nextRestBeat.getDuration().getTime();
					nextRestBeat = getNextRestVoice(beats, nextRestBeat);
				}
				
				if(requiredLength <= (nextSilenceLength + errorMargin)){
					List<TGVoice> voices = getVoicesBeforeEnd(measure.getBeats(),nextVoice.getBeat().getStart(), voice);
					while(!voices.isEmpty()){
						TGVoice currVoice = voices.get(0);
						if(currVoice.isRestVoice()){
							requiredLength -= currVoice.getDuration().getTime();
							removeVoice(currVoice, false);
						}else if(requiredLength > 0){
							moveVoice(currVoice,requiredLength);
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
	
	public boolean moveVoices(TGMeasure measure,long start,long theMove, int voiceIndex, TGDuration fillDuration){
		if( theMove == 0 ){
			return false;
		}
		boolean success = true;
		long measureStart = measure.getStart();
		long measureEnd =  (measureStart + measure.getLength());
		
		List<TGVoice> voicesToMove = getVoicesBeforeEnd(measure.getBeats(),start, voiceIndex);
		List<TGVoice> voicesToRemove = new ArrayList<TGVoice>();
		List<TGBeat> currentBeats = getBeatsBeforeEnd(measure.getBeats(), start);
		
		// Verifica los silencios a eliminar al principio del compas
		TGVoice first = getFirstVoice( currentBeats, voiceIndex );
		while(first != null && first.isRestVoice() && (!first.getBeat().isTextBeat() || !isUniqueVoice(first,false)) && (first.getBeat().getStart() + theMove) < measureStart){
			currentBeats.remove(first.getBeat());
			voicesToRemove.add(first);
			first = getNextVoice( currentBeats,first.getBeat(), voiceIndex);
		}
		
		// Verifica los silencios a eliminar al final del compas
		TGVoice last = getLastVoice(currentBeats, voiceIndex);
		TGDuration lastDuration = (last != null ? last.getDuration() : null);
		while(last != null && lastDuration != null && last.isRestVoice() && (!last.getBeat().isTextBeat() || !isUniqueVoice(last,false)) && (last.getBeat().getStart() + lastDuration.getTime() + theMove) > measureEnd  ){
			currentBeats.remove(last.getBeat());
			voicesToRemove.add(last);
			last = getPreviousVoice(currentBeats,last.getBeat(), voiceIndex);
			lastDuration = (last != null ? last.getDuration() : null);
		}
		
		// Si el primer o ultimo componente, quedan fuera del compas, entonces el movimiento no es satisfactorio
		if(first != null && last != null && lastDuration != null){
			if((first.getBeat().getStart() + theMove) < measureStart || (last.getBeat().getStart() + lastDuration.getTime() + theMove) > measureEnd){
				success = false;
			}
		}
		
		if(success){
			this.moveVoices(voicesToMove,theMove);
			
			// Elimino los silencios que quedaron fuera del compas.
			Iterator<TGVoice> it = voicesToRemove.iterator();
			while( it.hasNext() ){
				TGVoice beat = it.next();
				removeVoice(beat);
			}
			
			// Se crean silencios en los espacios vacios, si la duracion fue especificada.
			if( fillDuration != null ){
				if( theMove < 0 ){
					last = getLastVoice(measure.getBeats(), voiceIndex);
					lastDuration = (last != null ? last.getDuration() : null);
					long beatStart = ( (last != null && lastDuration != null ? last.getBeat().getStart()  + lastDuration.getTime() : start  )  );
					if( (beatStart + fillDuration.getTime()) <= measureEnd ){
						boolean beatNew = false;
						TGBeat beat = getBeat(measure, beatStart);
						if(beat == null){
							beat = getSongManager().getFactory().newBeat();
							beat.setStart( beatStart );
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
					first = getFirstVoice(getBeatsBeforeEnd(measure.getBeats(),start), voiceIndex);
					if( (start + fillDuration.getTime()) <= (first != null ?first.getBeat().getStart() : measureEnd ) ){
						boolean beatNew = false;
						TGBeat beat = getBeat(measure, start);
						if(beat == null){
							beat = getSongManager().getFactory().newBeat();
							beat.setStart( start );
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
						transposeNotes( beat, strings, transposition , tryKeepString, applyToChords, applyToString );
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
									transposeNotes( beat, strings, transposition , tryKeepString, applyToChords, applyToString );
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void transposeNotes( TGBeat beat, List<TGString> strings, int transposition , boolean tryKeepString, boolean applyToChord, int applyToString){
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
						transposeNote(note, notes, strings, transposition, tryKeepString, false );
					}
					
					if( applyToChord && beat.isChordBeat() ){
						TGChord chord = beat.getChord();
						int chordString = ( string.getNumber() - 1 );
						if( chord.getFretValue( chordString ) >= 0 ){
							transposeChordNote(chord, chordString, strings, transposition, tryKeepString, false);
						}
						chord.setFirstFret( -1 );
					}
				}
			}
		}
	}
	
	private boolean transposeNote( TGNote note, List<TGNote> notes, List<TGString> strings , int transposition , boolean tryKeepString, boolean forceChangeString ){
		boolean canTransposeFret = false;
		
		int maximumFret = 29;
		
		int transposedFret = ( note.getValue() + transposition );
		
		// Check if transposition could be done without change the string
		if( transposedFret >= 0 && transposedFret <= maximumFret ){
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
			if( transposedStringFret >= 0 && transposedStringFret <= maximumFret ){
				if( nextOwner != null ){
					if( ! transposeNote(nextOwner, notes, strings, 0 , tryKeepString , !canTransposeFret ) ){
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
	
	private boolean transposeChordNote( TGChord chord, int chordString, List<TGString> strings , int transposition , boolean tryKeepString, boolean forceChangeString ){
		boolean canTransposeFret = false;
		
		int maximumFret = 24;
		
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
					transposeChordNote(chord, nextChordString , strings, 0 , tryKeepString , !canTransposeFret );
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
}
