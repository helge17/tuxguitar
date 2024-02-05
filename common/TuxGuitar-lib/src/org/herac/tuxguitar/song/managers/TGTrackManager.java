package org.herac.tuxguitar.song.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.helpers.TGStoredBeatList;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGColor;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVoice;

public class TGTrackManager {
	
	private TGSongManager songManager;
	private final int MAX_FRET = 29;	//included
	
	public TGTrackManager(TGSongManager songManager){
		this.songManager = songManager;
	}
	
	public TGSongManager getSongManager(){
		return this.songManager;
	}
	
	public TGMeasure getFirstMeasure(TGTrack track){
		TGMeasure firstMeasure = null;
		Iterator<TGMeasure> measures = track.getMeasures();
		while(measures.hasNext()){
			TGMeasure currMeasure = measures.next();
			if(firstMeasure == null || (currMeasure.getStart() < firstMeasure.getStart())){
				firstMeasure = currMeasure;
			}
		}
		return firstMeasure;
	}
	
	public TGMeasure getLastMeasure(TGTrack track){
		return track.getMeasure(track.countMeasures() - 1);
	}
	
	public TGMeasure getPrevMeasure(TGMeasure measure){
		return measure.getTrack().getMeasure(measure.getNumber() - 2);
	}
	
	public TGMeasure getNextMeasure(TGMeasure measure){
		return measure.getTrack().getMeasure(measure.getNumber());
	}
	
	public TGMeasure getMeasureAt(TGTrack track,long start){
		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasure measure = (TGMeasure)it.next();
			long measureStart = measure.getStart();
			long measureLength = measure.getLength();
			if(start >= measureStart && start < measureStart + measureLength){
				return measure;
			}
		}
		return null;
	}
	
	public TGMeasure getMeasure(TGTrack track,int number){
		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasure measure = (TGMeasure)it.next();
			if(measure.getNumber() == number){
				return measure;
			}
		}
		return null;
	}
	
	/**
	 * Retorna Todos los desde Start hasta el final del compas
	 */
	public List<TGMeasure> getMeasuresBeforeEnd(TGTrack track,long fromStart) {
		List<TGMeasure> measures = new ArrayList<TGMeasure>();
		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasure currMeasure = (TGMeasure)it.next();
			if (currMeasure.getStart() >= fromStart) {
				measures.add(currMeasure);
			}
		}
		return measures;
	}
	
	public List<TGMeasure> getMeasuresFrom(TGTrack track,long fromStart) {
		return getMeasuresBetween(track, fromStart, Long.MAX_VALUE);
	}
	
	/**
	 * Retorna Todos los desde Start hasta el final del compas
	 */
	public List<TGMeasure> getMeasuresBetween(TGTrack track,long p1,long p2) {
		List<TGMeasure> measures = new ArrayList<TGMeasure>();
		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasure measure = (TGMeasure)it.next();
			if ((measure.getStart() + measure.getLength()) > p1  &&  measure.getStart() < p2) {
				measures.add(measure);
			}
		}
		return measures;
	}
	
	public void addNewMeasureBeforeEnd(TGTrack track,TGMeasureHeader header){
		addNewMeasureAfter(track,header,getLastMeasure(track));
	}
	
	/**
	 * Agrega un Compas
	 */
	public void addNewMeasureAfter(TGTrack track,TGMeasureHeader header,TGMeasure measure){
		TGMeasure newMeasure = getSongManager().getFactory().newMeasure(header);
		newMeasure.setClef(measure.getClef());
		newMeasure.setKeySignature(measure.getKeySignature());
		addMeasure(track,newMeasure);
	}
	
	public void addNewMeasure(TGTrack track,TGMeasureHeader header){
		TGMeasure previous = getMeasure(track, (header.getNumber() == 1)?(header.getNumber()+1):header.getNumber()-1);
		TGMeasure newMeasure = getSongManager().getFactory().newMeasure(header);
		newMeasure.setTrack(track);
		newMeasure.setClef(previous.getClef());
		newMeasure.setKeySignature(previous.getKeySignature());
		addMeasure(track,header.getNumber() - 1,newMeasure);
	}
	
	/**
	 * Agrega un Compas
	 */
	public void addMeasure(TGTrack track,TGMeasure measure){
		track.addMeasure(measure);
	}
	
	/**
	 * Agrega un Compas
	 */
	public void addMeasure(TGTrack track,int index,TGMeasure measure){
		track.addMeasure(index,measure);
	}
	
	public void removeLastMeasure(TGTrack track){
		removeMeasure(getLastMeasure(track));
	}
	
	public void removeMeasure(TGTrack track,long start){
		removeMeasure(getMeasureAt(track,start));
	}
	
	public void removeMeasure(TGMeasure measure){
		measure.getTrack().removeMeasure(measure.getNumber() - 1);
	}
	
	public void copyMeasureFrom(TGMeasure measure, TGMeasure from){
		measure.copyFrom(getSongManager().getFactory(), from);
	}
	
	public TGMeasure replaceMeasure(TGTrack track, TGMeasure newMeasure){
		TGMeasure tgMeasure = getMeasureAt(track,newMeasure.getStart());
		this.copyMeasureFrom(tgMeasure, newMeasure);
		
		return tgMeasure;
	}
	
	/**
	 * Mueve el compas
	 */
	public void moveMeasure(TGMeasure measure,long theMove){
		getSongManager().getMeasureManager().moveAllBeats(measure,theMove);
	}
	
	public void moveOutOfBoundsBeatsToNewMeasure(TGTrack track, long start){
		Iterator<TGMeasure> it = getMeasuresFrom(track,start).iterator();
		while( it.hasNext() ){
			TGMeasure measure = (TGMeasure)it.next();
			getSongManager().getMeasureManager().moveOutOfBoundsBeatsToNewMeasure(measure);
		}
	}
	
	public void moveTrackBeats(TGTrack track, long measureStart, long moveStart, long theMove ){
		List<TGMeasure> measures = getMeasuresBeforeEnd(track,measureStart);
		for( int i = 0 ; i < measures.size() ; i ++ ){
			TGMeasure measure = (TGMeasure)measures.get(i);
			if( moveStart + theMove < moveStart ){
				getSongManager().getMeasureManager().removeBeatsBetween(measure, moveStart, (moveStart + Math.abs(theMove)));
			}
			getSongManager().getMeasureManager().moveBeats(measure, moveStart, theMove);
		}
		for( int i = 0 ; i < measures.size() ; i ++ ){
			TGMeasure measure = (TGMeasure)measures.get(i);
			getSongManager().getMeasureManager().moveOutOfBoundsBeatsToNewMeasure(measure,false);
		}
	}

	public List<TGBeat> addBeats(TGTrack track, TGStoredBeatList beats, long start) {
		TGMeasureManager measureManager = getSongManager().getMeasureManager();
		TGFactory factory = getSongManager().getFactory();
		List<TGBeat> results = new ArrayList<>();
		TGMeasure startMeasure = getMeasureAt(track, start);
		for (TGMeasure measure : getMeasuresBeforeEnd(track, startMeasure.getStart())) {
			for (TGBeat beat : measureManager.getBeatsBeforeEnd(measure.getBeats(), start)) {
				beat.setStart(beat.getStart() + beats.getLength());
			}
		}
		for (TGBeat beat : beats.getBeats()) {
			TGBeat newBeat = beat.clone(factory);
			newBeat.setStart(start + beat.getStart());
		    measureManager.addBeat(startMeasure, newBeat);
		    results.add(newBeat);
		}
        measureManager.orderBeats(startMeasure);
		return results;
	}

	
	public void changeKeySignature(TGTrack track,long start,int keySignature,boolean toEnd){
		changeKeySignature(track,getMeasureAt(track,start),keySignature,toEnd);
	}
	
	/**
	 * Cambia el Key Signature
	 */
	public void changeKeySignature(TGTrack track,TGMeasure measure,int keySignature,boolean toEnd){
		//asigno el nuevo Key
		measure.setKeySignature(keySignature);
		
		if(toEnd){
			List<TGMeasure> measures = getMeasuresBeforeEnd(track,measure.getStart() + 1);
			Iterator<TGMeasure> it = measures.iterator();
			while(it.hasNext()){
				TGMeasure nextMeasure = (TGMeasure)it.next();
				nextMeasure.setKeySignature(keySignature);
			}
		}
	}
	
	public void changeClef(TGTrack track,long start,int clef,boolean toEnd){
		changeClef(track,getMeasureAt(track,start),clef,toEnd);
	}
	
	/**
	 * Cambia el Clef
	 */
	public void changeClef(TGTrack track,TGMeasure measure,int clef,boolean toEnd){
		//asigno el nuevo clef
		measure.setClef(clef);
		
		if(toEnd){
			List<TGMeasure> measures = getMeasuresBeforeEnd(track,measure.getStart() + 1);
			Iterator<TGMeasure> it = measures.iterator();
			while(it.hasNext()){
				TGMeasure nextMeasure = (TGMeasure)it.next();
				nextMeasure.setClef(clef);
			}
		}
	}
	
	public void changeSolo(TGTrack track,boolean solo){
		track.setSolo(solo);
		track.setMute(track.isSolo() ? false : track.isMute());
	}
	
	public void changeMute(TGTrack track,boolean mute){
		track.setMute(mute);
		track.setSolo(track.isMute() ? false : track.isSolo());
	}
	
	public void changeInfo(TGTrack track,String name,TGColor color,int offset){
		track.setName(name);
		track.setOffset(offset);
		track.getColor().copyFrom(color);
	}
	
	public void changeOffset(TGTrack track, int offset) {
		track.setOffset(offset);
	}
	
	public void changeStringCount(TGTrack track, int count){		
		if( track.stringCount() != count ) {
			if( count < track.getStrings().size() ){
				removeNotesAfterString(track, count);
			}
			
			if( getSongManager().isPercussionChannel(track.getSong(), track.getChannelId()) ) {
				track.setStrings(getSongManager().createPercussionStrings(count));
			} else {
				track.setStrings(getSongManager().createDefaultInstrumentStrings(count));
			}
		}
	}
	
	public void changeInstrumentStrings(TGTrack track, List<TGString> strings){
		if(strings.size() < track.getStrings().size()){
			removeNotesAfterString(track,strings.size());
		}
		track.setStrings(strings);
	}
	
	public void removeNotesAfterString(TGTrack track,int string){
		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasure measure = (TGMeasure)it.next();
			getSongManager().getMeasureManager().removeNotesAfterString(measure,string);
		}
	}
	
	public void changeChannel(TGTrack track, int channelId){
		this.changeChannel(track, getSongManager().getChannel(track.getSong(), channelId));
	}
	
	public void changeChannel(TGTrack track, TGChannel channel) {
		TGChannel oldChannel = getSongManager().getChannel(track.getSong(), track.getChannelId());
		
		track.setChannelId( (channel != null ? channel.getChannelId() : -1) );
		
		boolean oldPercussion = (oldChannel != null && oldChannel.isPercussionChannel());
		boolean newPercussion = (channel != null && channel.isPercussionChannel());
		if( oldPercussion != newPercussion ) {
			if( newPercussion ) {
				track.setStrings(getSongManager().createPercussionStrings(track.stringCount()));
			} else {
				track.setStrings(getSongManager().createDefaultInstrumentStrings(track.stringCount()));
			}
		}
	}
	
	public void autoCompleteSilences(TGTrack track){
		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasure measure = (TGMeasure)it.next();
			this.songManager.getMeasureManager().autoCompleteSilences(measure);
		}
	}
	
	public void orderBeats(TGTrack track){
		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasure measure = (TGMeasure)it.next();
			this.songManager.getMeasureManager().orderBeats(measure);
		}
	}
	
	public void allocateMeasureNotesToStrings(List<Integer> fromStringValues, List<TGMeasure> measures, List<TGString> toStrings) {
		List<TGBeat> beats = new ArrayList<TGBeat>();
		for (TGMeasure measure : measures) {
			beats.addAll(measure.getBeats());
		}
		this.allocateNotesToStrings(fromStringValues, beats, toStrings);
	}
	
	public void allocateMeasureNotesToStrings(List<Integer> fromStringValues, TGMeasure measure, List<TGString> toStrings) {
		this.allocateNotesToStrings(fromStringValues, measure.getBeats(), toStrings);
	}
	
	public void allocateNotesToStrings(List<Integer> fromStringValues, List<TGBeat> beats, List<TGString> toStrings) {
		if (fromStringValues==null || fromStringValues.isEmpty() || beats==null || beats.isEmpty() || toStrings==null || toStrings.isEmpty()) {
			return;
		}
		// don't move any note if tuning did not change
		if (!this.tuningChanged(fromStringValues, toStrings)) {
			return;
		}
		for (TGBeat beat : beats) {
			beat.removeChord();
			allocateNotesToStrings(fromStringValues, beat, toStrings);
		}
	}
	
	private void allocateNotesToStrings(List<Integer> fromStringValues, TGBeat beat, List<TGString> toStrings) {
		if (!this.allocateNotesToClosestString(fromStringValues, beat, toStrings)) {
			this.allocateNotesToLowestFret(fromStringValues, beat, toStrings);
		}
	}
	
	// try to allocate each note to the closest string in new tuning
	// "closest" in terms of string pitch (not string number)
	// objective is to keep "fingering pattern" globally unchanged (typ. use case: new tuning is 1/2 tone higher for all strings)
	// returns true in case of success, else false
	private boolean allocateNotesToClosestString(List<Integer> fromStringValues, TGBeat beat, List<TGString> toStrings) {
		// only possible if old tuning is valid
		boolean oldTuningIsValid = true;
		for (int stringValue : fromStringValues) {
			oldTuningIsValid &= (stringValue!=0);
		}
		if (!oldTuningIsValid) {
			return false;
		}
		
		List<TGString> freeStrings = new ArrayList<TGString>(toStrings);
		TGBeat newBeat = beat.clone(getSongManager().getFactory());
		boolean ok = true;
		
		for (int voiceIndex=0; voiceIndex<newBeat.countVoices(); voiceIndex++) {
			TGVoice voice = newBeat.getVoice(voiceIndex);
			if (ok) {
				for (TGNote note : voice.getNotes()) {
					// look for closest string
					int noteStringValue = fromStringValues.get(note.getString()-1);
					int minDistance = -1;
					TGString closestString = null;
					for (TGString string : toStrings) {
						int distance = Math.abs(noteStringValue - string.getValue());
						if (minDistance<0 || distance<minDistance) {
							closestString = string;
							minDistance = distance;
						}
					}
					if (freeStrings.contains(closestString)) {
						int newFret = noteStringValue + note.getValue() - closestString.getValue();
						if (newFret>=0 && newFret<=MAX_FRET) {
							note.setValue(newFret);
							note.setString(closestString.getNumber());
							freeStrings.remove(closestString);
						}
						else {
							// can't place note on closest string, note does not fit
							ok = false;
							break;
						}
					}
					else {
						// can't place note on closest string, already occupied, abort
						ok = false;
						break;
					}
				}
			}
		}
		if (ok) {
			// all notes found a place: keep this result
			for (int voiceIndex=0; voiceIndex<newBeat.countVoices(); voiceIndex++) {
				beat.setVoice(voiceIndex, newBeat.getVoice(voiceIndex));
			}
		}
		return ok;
	}
	
	// allocate each note to the lowest possible fret, discard notes with no place found
	private void allocateNotesToLowestFret(List<Integer> fromStringValues, TGBeat beat, List<TGString> toStrings) {
		TGFactory factory = getSongManager().getFactory();
		List<TGString> freeStrings = new ArrayList<TGString>(toStrings);
		List<TGNote> notesToRemove = new ArrayList<TGNote>();
		for (int i=0; i<beat.countVoices(); i++) {
			TGVoice voice = beat.getVoice(i);
			// allocating notes to lowest possible fret means to highest possible string
			// so, process highest notes first to maximize probability to find a place for each note
			// move all notes to a single "zero" string first, to enable sorting by fret number
			List<TGNote> listNotes = new ArrayList<TGNote>();
			while (voice.getNotes().size() != 0) {
				TGNote note = voice.getNote(0);
				TGNote tmpNote = note.clone(factory);
				tmpNote.setValue(fromStringValues.get(tmpNote.getString()-1) + tmpNote.getValue());
				tmpNote.setString(0);
				listNotes.add(tmpNote);
				voice.removeNote(note);
			}
			Collections.sort(listNotes);
			Collections.reverse(listNotes);
			for (TGNote note : listNotes) {
				int minFret = -1;
				TGString newString = null;
				for (TGString string : freeStrings) {
					int fret = note.getValue() - string.getValue();
					if (fret>=0 && fret<=MAX_FRET && (minFret<0 || fret<minFret) ) {
						newString = string;
						minFret = fret;
					}
				}
				if (newString != null) {
					note.setValue(note.getValue() - newString.getValue());
					note.setString(newString.getNumber());
					freeStrings.remove(newString);
				}
				else {
					// can't find a string for this note, so discard it
					notesToRemove.add( note );
				}
			}
			// Remove notes with no allocation found
			while( notesToRemove.size() > 0 ){
				listNotes.remove(notesToRemove.get(0));
				notesToRemove.remove( 0 );
			}
			// move the valid ones back into voice
			for (TGNote note : listNotes) {
				voice.addNote(note);
			}
		}
	}
	
	private boolean tuningChanged(List<Integer> fromStringValues, List<TGString> toStrings) {
		if ((fromStringValues==null) != (toStrings==null)) return true;
		// lists are both null or neither
		if (fromStringValues==null) return false;
		// lists are not null
		if (fromStringValues.size() != toStrings.size()) return true;
		for (int i=0; i<fromStringValues.size(); i++) {
			// if toStrings are not sorted by number, consider tuning changed
			if (toStrings.get(i).getNumber() != (i+1)) return true;
			if (toStrings.get(i).getValue() != fromStringValues.get(i)) return true;
		}
		return false;
	}
	
	public void transposeNotes(TGTrack track, int transposition , boolean tryKeepString, boolean applyToChords, int applyToString){
		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasure measure = (TGMeasure)it.next();
			this.songManager.getMeasureManager().transposeNotes(measure, transposition, tryKeepString, applyToChords, applyToString );
		}
	}
	
	public void transposeNotes(TGTrack track, int[] transpositionStrings , boolean tryKeepString , boolean applyToChords ){
		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasure measure = (TGMeasure)it.next();
			this.songManager.getMeasureManager().transposeNotes(measure, transpositionStrings, tryKeepString , applyToChords);
		}
	}
	
	/**
	 * Retorna true si es el primer compas
	 */
	public boolean isFirstMeasure(TGMeasure measure){
		return (measure.getNumber() == 1);
	}
	
	/**
	 * Retorna true si es el ultimo compas
	 */
	public boolean isLastMeasure(TGMeasure measure){
		return (measure.getTrack().getSong().countMeasureHeaders() == measure.getNumber());
	}
}
