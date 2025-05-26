package app.tuxguitar.song.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import app.tuxguitar.graphics.control.TGDrumMap;
import app.tuxguitar.song.helpers.TGMeasureError;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGColor;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVoice;

public class TGTrackManager {

	private TGSongManager songManager;

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

	// if a note in following voice is tied to current note and tie is valid, returns this note
	// else null
	public TGNote getNextTiedNote(TGNote note) {
		TGVoice nextVoice = getSongManager().getMeasureManager().getNextVoice(
				note.getVoice().getBeat().getMeasure().getBeats(), note.getVoice().getBeat(), note.getVoice().getIndex());
		if (nextVoice == null) {
			TGMeasure nextMeasure = getNextMeasure(note.getVoice().getBeat().getMeasure());
			if (nextMeasure == null) {
				return null;
			}
			nextVoice = getSongManager().getMeasureManager().getFirstVoice(
					nextMeasure.getBeats(), note.getVoice().getIndex());
		}
		if (nextVoice == null) {
			return null;
		}
		// look for a valid tied note
		TGNote nextTiedNote = null;
		for (TGNote nextNote : nextVoice.getNotes()) {
			if (nextNote.isTiedNote() && (nextNote.getString() == note.getString()) && (nextNote.getValue() == note.getValue())) {
				nextTiedNote = nextNote;
				break;
			}
		}
		return nextTiedNote;
	}
	
	// look for a previous note to be tied to
	public TGNote getPreviousNoteForTie(TGNote note) {
		return this.getPreviousNoteForTie(note.getVoice(), note.getString(), note.getValue());
	}
	public TGNote getPreviousNoteForTie(TGVoice voice, int string, Integer value) {
		TGVoice previousVoice = getSongManager().getMeasureManager().getPreviousVoice(
				voice.getBeat().getMeasure().getBeats(), voice.getBeat(), voice.getIndex());
		if (previousVoice == null) {
			TGMeasure previousMeasure = getPrevMeasure(voice.getBeat().getMeasure());
			if (previousMeasure == null) {
				return null;
			}
			previousVoice = getSongManager().getMeasureManager().getLastVoice(
					previousMeasure.getBeats(), voice.getIndex());
		}
		if (previousVoice == null) {
			return null;
		}
		// look for a valid tied note
		TGNote previousNoteForTie = null;
		for (TGNote previousNote : previousVoice.getNotes()) {
			if ((previousNote.getString() == string) && 
					((value==null) || (previousNote.getValue() == value)) ) {
				previousNoteForTie = previousNote;
				break;
			}
		}
		return previousNoteForTie;
	}

	// returns true if a note in following voice is tied to current note, and tie is valid
	public boolean isAnyTiedTo(TGNote note) {
		return (getNextTiedNote(note) != null);
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

	public TGMeasure getMeasureAtPreciseStart(TGTrack track, long preciseStart){
		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasure measure = (TGMeasure)it.next();
			long measurePreciseStart = measure.getPreciseStart();
			long measurePreciseLength = measure.getPreciseLength();
			if(preciseStart >= measurePreciseStart && preciseStart < measurePreciseStart + measurePreciseLength){
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
		getSongManager().getMeasureManager().autoCompleteSilences(newMeasure);
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

	public List<TGBeat> replaceBeats(TGTrack track, List<TGBeat> beatsToInsert, long preciseStart) {
		// cannot just remove then insert beats: removing beats shifts left all following measures
		// and this may break some structures (e.g. split notes over several measures)
		// need to replace beats *in situ*
		List<TGBeat> updatedBeats = new ArrayList<TGBeat>();
		TGMeasureManager measureManager = getSongManager().getMeasureManager();

		Long nextUpdatePreciseStart = preciseStart;	// start time of next available beat to update
		for (TGBeat beatToInsert : beatsToInsert) {
			TGBeat updatedBeat = null;
			TGMeasure updatedMeasure = null;
			if (nextUpdatePreciseStart != null) {
				TGMeasure startMeasure = getMeasureAtPreciseStart(track, nextUpdatePreciseStart);
				long updatePreciseStart = nextUpdatePreciseStart;
				nextUpdatePreciseStart = null;
				// make some room, to make sure beat update is safe (modification remains local)
				long clearedTime = 0;
				long neededClearTime = measureManager.getMaximumDuration(beatToInsert).getPreciseTime();
				boolean enoughRoomAvailable = false;
				for (TGMeasure measure : getMeasuresBeforeEnd(track, startMeasure.getStart())) {
					if (!enoughRoomAvailable) {
						for (TGBeat beat : measureManager.getBeatsBeforeEndPrecise(measure.getBeats(), updatePreciseStart)) {
							if (!enoughRoomAvailable) {
								if (updatedBeat == null) {
									// first beat cleared: update shall take place here
									updatedBeat = beat;
									updatedMeasure = measure;
								}
								for (int i=0; i<beat.countVoices(); i++) {
									beat.getVoice(i).clearNotes();
								}
								clearedTime += measureManager.getMinimumDuration(beat).getPreciseTime();
								enoughRoomAvailable = (clearedTime >= neededClearTime);
							}
						}
					}
				}
				if (enoughRoomAvailable) {
					// OK to update beat
					for (int i=0; i<beatToInsert.countVoices(); i++) {
						// Warning: after this, measure may be invalid
						// Typically this can create overlapping beats
						// This overlapping can only concern rest beat(s), since enough room was made available before doing this
						if (!beatToInsert.getVoice(i).isEmpty()) {
							measureManager.changeDuration(updatedMeasure, updatedBeat, beatToInsert.getVoice(i).getDuration(), i, true);
						}
					}
					// replace all beat attributes, except start
					long updatedPreciseStart = updatedBeat.getPreciseStart();
					updatedBeat.copyFrom(beatToInsert, songManager.getFactory());
					updatedBeat.setPreciseStart(updatedPreciseStart);
					// now, some cleanup is needed in measure
					measureManager.removeOverlappingRestBeats(updatedMeasure);
					// recompute measure to consider new beat duration, this may split updated beat in several beats over measures boundaries
					// this can create overlapping rest beats in following measure(s), or missing rest beats
					// note: several following measures may be impacted (e.g. when inserting a whole in measure with time signature 1/4)
					measureManager.moveOutOfBoundsBeatsToNewMeasure(updatedMeasure);

					updatedBeats.add(updatedBeat);
					// look for next beat available for update
					startMeasure = getMeasureAtPreciseStart(track, updatedBeat.getPreciseStart());
					long expectedPreciseStart = updatedBeat.getPreciseStart() + measureManager.getMinimumDuration(beatToInsert).getPreciseTime();
					boolean found=false;
					for (TGMeasure measure : getMeasuresBeforeEnd(track, startMeasure.getStart())) {
						// need to cleanup measures following the updated one, possibly impacted by updated beat duration change
						measureManager.autoCompleteSilences(measure);
						measureManager.removeOverlappingRestBeats(measure);
						if (!found) {
							for (TGBeat beat : measureManager.getBeatsBeforeEndPrecise(measure.getBeats(), updatedBeat.getPreciseStart())) {
								if (beat.getPreciseStart() >= expectedPreciseStart) {
									nextUpdatePreciseStart = beat.getPreciseStart();
									found=true;
									break;
								}
							}
						}
					}
				}
			}
		}
		return updatedBeats;
	}

	/**
	 * Cambia el Key Signature
	 */
	public void changeKeySignature(TGTrack track,TGMeasure measure,int keySignature,boolean toEnd){
		//asigno el nuevo Key
		measure.setKeySignature(keySignature);
		measure.resetAltEnharmonic();

		if(toEnd){
			List<TGMeasure> measures = getMeasuresBeforeEnd(track,measure.getStart() + 1);
			Iterator<TGMeasure> it = measures.iterator();
			while(it.hasNext()){
				TGMeasure nextMeasure = (TGMeasure)it.next();
				nextMeasure.setKeySignature(keySignature);
				nextMeasure.resetAltEnharmonic();
			}
		}
	}
	public void changeKeySignature(TGTrack track,List<TGMeasure> measures,int keySignature){
		for (TGMeasure measure : measures) {
			measure.setKeySignature(keySignature);
			measure.resetAltEnharmonic();
		}
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

	public void changeInfo(TGTrack track,String name,TGColor color,int offset, int maxFret){
		this.changeInfo(track, name, color, offset);
		this.removeNotesAfterFret(track, maxFret);
		track.setMaxFret(maxFret);
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

			if( track.isPercussion() ) {
				track.setStrings(getSongManager().createPercussionStrings(count));
			} else {
				track.setStrings(getSongManager().createDefaultInstrumentStrings(count));
			}
		}
	}

	public void removeNotesAfterString(TGTrack track,int string){
		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasure measure = (TGMeasure)it.next();
			getSongManager().getMeasureManager().removeNotesAfterString(measure,string);
		}
	}

	public void removeNotesAfterFret(TGTrack track,int fret){
		Iterator<TGMeasure> it = track.getMeasures();
		while(it.hasNext()){
			TGMeasure measure = (TGMeasure)it.next();
			getSongManager().getMeasureManager().removeNotesAfterFret(measure,fret);
		}
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

	public void allocateNotesToStrings(List<Integer> fromStringValues, TGTrack track, List<TGString> toStrings) {
		Iterator<TGMeasure> it = track.getMeasures();
		while (it.hasNext()) {
			this.allocateMeasureNotesToStrings(fromStringValues, it.next(), toStrings, track.getMaxFret());
		}
	}

	public void allocateMeasureNotesToStrings(List<Integer> fromStringValues, List<TGMeasure> measures, List<TGString> toStrings, int maxFret) {
		List<TGBeat> beats = new ArrayList<TGBeat>();
		for (TGMeasure measure : measures) {
			beats.addAll(measure.getBeats());
		}
		this.allocateNotesToStrings(fromStringValues, beats, toStrings, maxFret);
	}

	public void allocateMeasureNotesToStrings(List<Integer> fromStringValues, TGMeasure measure, List<TGString> toStrings, int maxFret) {
		this.allocateNotesToStrings(fromStringValues, measure.getBeats(), toStrings, maxFret);
	}

	public void allocateNotesToStrings(List<Integer> fromStringValues, List<TGBeat> beats, List<TGString> toStrings, int maxFret) {
		if (fromStringValues==null || fromStringValues.isEmpty() || beats==null || beats.isEmpty() || toStrings==null || toStrings.isEmpty()) {
			return;
		}
		// don't move any note if tuning did not change
		if (!this.tuningChanged(fromStringValues, toStrings)) {
			return;
		}
		for (TGBeat beat : beats) {
			beat.removeChord();
			allocateNotesToStrings(fromStringValues, beat, toStrings, maxFret);
		}
	}

	private void allocateNotesToStrings(List<Integer> fromStringValues, TGBeat beat, List<TGString> toStrings, int maxFret) {
		if (!this.allocateNotesToClosestString(fromStringValues, beat, toStrings, maxFret)) {
			this.allocateNotesToLowestFret(fromStringValues, beat, toStrings, maxFret);
		}
	}

	// try to allocate each note to the closest string in new tuning
	// "closest" in terms of string pitch (not string number)
	// objective is to keep "fingering pattern" globally unchanged (typ. use case: new tuning is 1/2 tone higher for all strings)
	// returns true in case of success, else false
	private boolean allocateNotesToClosestString(List<Integer> fromStringValues, TGBeat beat, List<TGString> toStrings, int maxFret) {
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
						boolean canMove = newFret>=0 && newFret<=maxFret;
						int newGraceFret = 0;
						if (note.getEffect().isGrace()) {
							newGraceFret = noteStringValue + note.getEffect().getGrace().getFret() - closestString.getValue();
							canMove &= (newGraceFret>=0 && newGraceFret<=maxFret);
						}
						if (canMove) {
							note.setValue(newFret);
							note.setString(closestString.getNumber());
							if (note.getEffect().isGrace()) {
								note.getEffect().getGrace().setFret(newGraceFret);
							}
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
	private void allocateNotesToLowestFret(List<Integer> fromStringValues, TGBeat beat, List<TGString> toStrings, int maxFret) {
		List<TGString> freeStrings = new ArrayList<TGString>(toStrings);
		for (int i=0; i<beat.countVoices(); i++) {
			TGVoice voice = beat.getVoice(i);
			// allocating notes to lowest possible fret means to highest possible string
			// so, process highest notes first to maximize probability to find a place for each note
			// move all notes to a single "zero" string first, to enable sorting by fret number
			List<TGNote> listNotes = voice.getNotes();
			moveNotesToStringZero(fromStringValues, listNotes);
			Collections.sort(listNotes);
			Collections.reverse(listNotes);
			for (TGNote note : listNotes) {
				int minFret = -1;
				TGString newString = null;
				for (TGString string : freeStrings) {
					int fret = note.getValue() - string.getValue();
					boolean canMove = (fret>=0 && fret<=maxFret);
					if (note.getEffect().isGrace()) {
						int graceFret = note.getEffect().getGrace().getFret() - string.getValue();
						canMove &= (graceFret>=0 & graceFret<=maxFret);
					}
					if (canMove && (minFret<0 || fret<minFret) ) {
						newString = string;
						minFret = fret;
					}
				}
				if (newString != null) {
					note.setValue(note.getValue() - newString.getValue());
					note.setString(newString.getNumber());
					if (note.getEffect().isGrace()) {
						note.getEffect().getGrace().setFret(note.getEffect().getGrace().getFret() - newString.getValue());
					}
					freeStrings.remove(newString);
				}
			}
			// Remove notes with no allocation found
			removeUnallocatedNotes(voice);
		}
	}

	public void allocatePercussionNotesToStrings(List<Integer> fromStringValues, List<TGBeat> beats, List<TGString> toStrings) {
		TGDrumMap drumMap = new TGDrumMap();
		for (TGBeat beat : beats) {
			List<TGString> freeStrings = new ArrayList<TGString>(toStrings);
			for (int i=0; i<beat.countVoices(); i++) {
				TGVoice voice = beat.getVoice(i);
				List <TGNote> listNotes = voice.getNotes();
				// sort by ascending preferred string number
				Collections.sort(listNotes, (a,b) -> (drumMap.getPreferredStringNumber(a.getValue()) - drumMap.getPreferredStringNumber(b.getValue())));
				moveNotesToStringZero(fromStringValues, listNotes);
				for (TGNote note : listNotes) {
					// try to allocate to preferred string
					int preferred = drumMap.getPreferredStringNumber(note.getValue());
					TGString newString = null;
					for (TGString string : freeStrings) {
						if (string.getNumber() == preferred) {
							newString = string;
							note.setString(preferred);
							freeStrings.remove(newString);
							break;
						}
					}
					// if not possible, try to find closest string
					if (newString == null) {
						int minDistance = -1;
						for (TGString string : freeStrings) {
							int distance = Math.abs(string.getNumber() - preferred);
							if (distance <= minDistance || minDistance<0) {
								newString = string;
								minDistance = distance;
							}
						}
						if (newString!=null) {
							// found a place
							note.setString(newString.getNumber());
							freeStrings.remove(newString);
						}
					}
				}
				// Remove notes with no allocation found
				this.removeUnallocatedNotes(voice);
			}
		}
	}

	private void moveNotesToStringZero(List<Integer> fromStringValues, List<TGNote> listNotes) {
		for (TGNote note : listNotes) {
			note.setValue(fromStringValues.get(note.getString()-1) + note.getValue());
			if (note.getEffect().isGrace()) {
				note.getEffect().getGrace().setFret(fromStringValues.get(note.getString()-1) + note.getEffect().getGrace().getFret());
			}
			note.setString(0);
		}
	}

	private void removeUnallocatedNotes(TGVoice voice) {
		List<TGNote> toRemove = new ArrayList<TGNote>();
		for (TGNote note : voice.getNotes()) {
			if (note.getString()==0) {
				toRemove.add(note);
			}
		}
		while(toRemove.size() > 0 ){
			voice.removeNote(toRemove.get(0));
			toRemove.remove( 0 );
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

	public boolean hasMeasureDurationError(TGTrack track) {
		TGMeasureManager measureManager = getSongManager().getMeasureManager();
		Iterator<TGMeasure> itMeasure = track.getMeasures();
		while (itMeasure.hasNext()) {
			for (TGMeasureError err : measureManager.getMeasureErrors(itMeasure.next())) {
				if (err.getErrorType() == TGMeasureError.TYPE_VOICE_DURATION_ERROR) {
					return true;
				}
			}
		}
		return false;
	}
}
