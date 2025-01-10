package org.herac.tuxguitar.io.tef3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.io.tef3.base.TEChordDefinition;
import org.herac.tuxguitar.io.tef3.base.TEComponentBase;
import org.herac.tuxguitar.io.tef3.base.TEComponentChord;
import org.herac.tuxguitar.io.tef3.base.TEComponentEnding;
import org.herac.tuxguitar.io.tef3.base.TEComponentGraceNoteMetadata;
import org.herac.tuxguitar.io.tef3.base.TEComponentNote;
import org.herac.tuxguitar.io.tef3.base.TEComponentNote.TEComponentNoteDynamics;
import org.herac.tuxguitar.io.tef3.base.TEComponentRest;
import org.herac.tuxguitar.io.tef3.base.TEComponentTempoChange;
import org.herac.tuxguitar.io.tef3.base.TEComponentTextEvent;
import org.herac.tuxguitar.io.tef3.base.TELyrics;
import org.herac.tuxguitar.io.tef3.base.TEMeasure;
import org.herac.tuxguitar.io.tef3.base.TEPosition;
import org.herac.tuxguitar.io.tef3.base.TESong;
import org.herac.tuxguitar.io.tef3.base.TETimeSignature;
import org.herac.tuxguitar.io.tef3.base.TETrack;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGLyric;
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
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloPicking;

public class TESongParser {
	
	private static final int[][] PERCUSSION_TUNINGS = new int[][]{
		new int[]{ 49, 41, 32 },
		new int[]{ 49, 51, 42, 50 },
		new int[]{ 49, 42, 50, 37, 32 },
		new int[]{ 49, 51, 42, 50, 45, 37 },
		new int[]{ 49, 51, 42, 50, 45, 37, 41 },
	};
	
	private TGSongManager manager;
	
	public TESongParser(TGFactory factory) {
		this.manager = new TGSongManager(factory);
	}

	public TGSong parseSong(TESong teSong){
		TGSong tgSong = this.manager.newSong();

		this.addSongHeader(tgSong, teSong);
		this.addTracks(tgSong, teSong);
		this.addMeasures(tgSong, teSong);
		this.sortComponents(teSong);
		this.addComponents(tgSong, teSong);
		
		return new TGSongAdjuster(this.manager, tgSong).process();
	}

	private void addSongHeader(TGSong tgSong, TESong teSong) {
		tgSong.setName(teSong.getSongMetadata().getSongTitle());
		tgSong.setAuthor(teSong.getSongMetadata().getAuthorName());
		tgSong.setComments(teSong.getSongMetadata().getComments());
		tgSong.setCopyright(teSong.getSongMetadata().getCopyright());
	}

	private void addTracks(TGSong tgSong, TESong teSong) {
		int totalTeTracks = teSong.getTracks().size();

		while (tgSong.countTracks() < totalTeTracks) {
			this.manager.addTrack(tgSong);
		}

		this.manager.removeAllChannels(tgSong);

		for (int i = 0; i < totalTeTracks; i++) {
			TGTrack tgTrack = tgSong.getTrack(i);
			TETrack teTrack = teSong.getTracks().get(i);
			
			TGChannel tgChannel = this.manager.addChannel(tgSong);
			tgChannel.setVolume((short)((  (15 - teTrack.getVolume()) * 127) / 15));
			tgChannel.setBalance((short)(( teTrack.getPan() * 127) / 15));
			tgChannel.setProgram((short)teTrack.getMidiInstrument());
			tgChannel.setBank( teTrack.getIsPercussion() ? TGChannel.DEFAULT_PERCUSSION_BANK : TGChannel.DEFAULT_BANK);
			tgChannel.setName(this.manager.createChannelNameFromProgram(tgSong, tgChannel));
			tgChannel.setChorus((short)(teSong.getFileMetadata().getToneChorus() << 8));
			tgChannel.setReverb((short)(teSong.getFileMetadata().getToneReverb() << 8));
			
			tgTrack.setChannelId(tgChannel.getChannelId());

			tgTrack.setOffset(teTrack.getCapo());
			tgTrack.setName(teTrack.getTrackName());

			for (TELyrics teLyrics : teSong.getSongMetadata().getLyrics())
			{
				if (teLyrics.getTrackNumber() != i) {
					continue;
				}

				TGLyric tgLyrics = this.manager.getFactory().newLyric();
				tgLyrics.setFrom(1);
				tgLyrics.setLyrics(teLyrics.getLyrics());
				tgTrack.setLyrics(tgLyrics);
			}
			
			tgTrack.getStrings().clear();
			byte tuning[] = teTrack.getTuning();
			
			for(int stringIdx = 0; stringIdx < tuning.length; stringIdx++) {
				if(stringIdx >= 7) {
					break;
				}

				TGString string = this.manager.getFactory().newString();
				string.setNumber( stringIdx + 1 );
				string.setValue( teTrack.getIsPercussion() ? 0 : (96 - tuning[stringIdx]) );
				tgTrack.getStrings().add(string);
			}
		}
	}

	private void addMeasures(TGSong tgSong, TESong teSong) {
		int teTotalMeasures = teSong.getMeasures().size();

		this.manager.getFirstMeasureHeader(tgSong).getTempo().setQuarterValue(teSong.getFileMetadata().getInitialBpm());

		while(tgSong.countMeasureHeaders() < teTotalMeasures) {
			this.manager.addNewMeasureBeforeEnd(tgSong);
		}

		int tgTotalTracks = tgSong.countTracks();

		for (int measureIndex = 0; measureIndex < teTotalMeasures; measureIndex++) {
			TGMeasureHeader tgMeasureHeader = tgSong.getMeasureHeader(measureIndex);
			TEMeasure teMeasure = teSong.getMeasures().get(measureIndex);

			TETimeSignature teTimeSignature = teMeasure.getTimeSignature();
			TGTimeSignature tgTimeSignature = this.manager.getFactory().newTimeSignature();
			tgTimeSignature.setNumerator(teTimeSignature.getNumerator());
			tgTimeSignature.getDenominator().setValue(teTimeSignature.getDenominator());
			this.manager.changeTimeSignature(tgSong, tgMeasureHeader, tgTimeSignature, false);

			for (int trackIndex = 0; trackIndex < tgTotalTracks; trackIndex++) {
				TGTrack tgTrack = tgSong.getTrack(trackIndex);
				TETrack teTrack = teSong.getTracks().get(trackIndex);
				TGMeasure tgMeasure = tgTrack.getMeasure(measureIndex);

				int teKeySignature = (byte) teMeasure.getKeySignature();
				if (teKeySignature < 0) {
					teKeySignature = 7 - teKeySignature; // translate -1 to 8, etc.
				}
				tgMeasure.setKeySignature(teKeySignature);

				switch (teTrack.getClef())
				{
					case TrebleClef:
						tgMeasure.setClef(TGMeasure.CLEF_TREBLE);
						break;
					case BassClef:
						tgMeasure.setClef(TGMeasure.CLEF_BASS);
						break;
					case TenorClef:
						tgMeasure.setClef(TGMeasure.CLEF_TENOR);
						break;
					case AltoClef:
						tgMeasure.setClef(TGMeasure.CLEF_ALTO);
						break;
					default:
						tgMeasure.setClef(TGMeasure.DEFAULT_CLEF);
						break;
				}
			}
		}
	}
	
	private void addComponents(TGSong tgSong, TESong teSong){
		List<TETrack> teTracks = teSong.getTracks();
		Iterator<TEComponentBase> it = teSong.getComponents().iterator();

		List<TGNote[]> lastNoteOnStrings = new ArrayList<>();
		for (int i = 0; i < teTracks.size(); i++) {
			int stringCount = teTracks.get(i).getStringCount();
			TGNote[] noteList = new TGNote[stringCount];
			Arrays.fill(noteList, null);
			lastNoteOnStrings.add(noteList);
		}

		while(it.hasNext()){
			TEComponentBase component = (TEComponentBase)it.next();
			
			int measure = component.getPosition().getMeasure();

			if (measure < 0 || measure >= tgSong.countMeasureHeaders()) {
				continue;
			}

			int componentTrackIndex = component.getPosition().getTrackIndex();
			if (componentTrackIndex < 0 || componentTrackIndex >= teTracks.size()) {
				continue;
			}

			TGNote[] previousNotes = lastNoteOnStrings.get(componentTrackIndex);

			TETrack teTrack = teTracks.get(componentTrackIndex);
			TGTrack tgTrack = tgSong.getTrack(componentTrackIndex);
			TGMeasure tgMeasure = tgTrack.getMeasure(measure);
			TGBeat tgBeat = getBeat(tgMeasure, this.getGridPosition(tgMeasure, component.getPosition()));

			if(component instanceof TEComponentNote) {
				TGNote tgNote = this.addNote(teTrack, (TEComponentNote)component, tgBeat);
				previousNotes[component.getPosition().getString()] = tgNote;
			}
			else if(component instanceof TEComponentChord) {
				this.addChord(teSong.getChordDefinitions(), (TEComponentChord)component, tgTrack, tgBeat);
			}
			else if (component instanceof TEComponentGraceNoteMetadata) {
				TGNote previousTgNote = previousNotes[component.getPosition().getString()];
				this.addGraceNoteMetadata(previousTgNote, (TEComponentGraceNoteMetadata)component);
			}
			else if (component instanceof TEComponentRest) {
				this.addRest((TEComponentRest)component, tgBeat);
			}
			else if (component instanceof TEComponentTextEvent) {
				this.addTextEvent(teSong, (TEComponentTextEvent)component, tgBeat);
			}
			else if (component instanceof TEComponentTempoChange) {
				this.addTempoChange((TEComponentTempoChange)component, tgSong, tgMeasure);
			}
			else if (component instanceof TEComponentEnding) {
				this.addEnding((TEComponentEnding) component, tgMeasure);
			}
		}
	}

	public void sortComponents(TESong song){
		Collections.sort(song.getComponents(), new Comparator<TEComponentBase>() {

			public int compare(TEComponentBase lhs, TEComponentBase rhs) {

				if (lhs == null || rhs == null) {
					return 0;
				}

				if ( lhs.getPosition().getMeasure() < rhs.getPosition().getMeasure() ){
					return -1;
				} else if ( lhs.getPosition().getMeasure() > rhs.getPosition().getMeasure() ){
					return 1;
				}

				if ( lhs.getPosition().getPositionInMeasure() < rhs.getPosition().getPositionInMeasure() ){
					return -1;
				} else if ( lhs.getPosition().getPositionInMeasure() > rhs.getPosition().getPositionInMeasure() ){
					return 1;
				}

				if( lhs.getSortOrder() < rhs.getSortOrder() ){
					return -1;
				} else if( lhs.getSortOrder() > rhs.getSortOrder() ){
					return 1;
				}

				return 0;
			}
		});
	}
	
	private TGBeat getBeat(TGMeasure measure, long start){
		TGBeat beat = this.manager.getMeasureManager().getBeat(measure, start);
		if(beat == null){
			beat = this.manager.getFactory().newBeat();
			beat.setStart(start);
			measure.addBeat(beat);
		}
		return beat;
	}
	
	// TablEdit uses a 16th note grid.
	// Anything smaller than a 16th note is padded with rests to be a 16th note.
	// So a 1/64th note is actually 1/64th note, with 3 1/64th note rests after it.
	private long getGridPosition(TGMeasure measure, TEPosition position){
		long measureStart = measure.getStart();
		int positionInMeasure = position.getPositionInMeasure();

		float durationOfSixteenthNote = (float)TGDuration.QUARTER / TGDuration.SIXTEENTH;
		float sizePerGridPosition = TGDuration.QUARTER_TIME * durationOfSixteenthNote;

		long gridPosition = (long) (measureStart + (sizePerGridPosition * positionInMeasure));
		
		return gridPosition;
	}
	
	private TGDuration getDuration(int duration){
		TGDuration tgDuration = this.manager.getFactory().newDuration();

		// Filler numbers: 20, 23, 26, 29 = Sixteenth Note. No Dot.
		// Filler numbers: 21, 24, 27, 30 = Sixtyfourth Note. No Dot.
		// 31 = Dotted whole note.
		switch (duration) {
			case 20: // intentional fall-through
			case 23: // intentional fall-through
			case 26: // intentional fall-through
			case 29:
				tgDuration.setValue(TGDuration.SIXTEENTH);
				return tgDuration;

			case 21: // intentional fall-through
			case 24: // intentional fall-through
			case 27: // intentional fall-through
			case 30:
				tgDuration.setValue(TGDuration.SIXTY_FOURTH);
				return tgDuration;

			case 31:
				tgDuration.setValue(TGDuration.WHOLE);
				tgDuration.setDotted(true);
				return tgDuration;

			default:
				break;
		}
		
		int durationOfSixtyFourthNote = 18;
		boolean isDoubleDotted = duration > durationOfSixtyFourthNote;

		if (isDoubleDotted) {
			duration -= durationOfSixtyFourthNote;
			tgDuration.setDoubleDotted(true);

			// 19 = Half Double Dotted
			// 22 = Quarter Double Dotted
			// 25 = Eighth Double Dotted
			// 28 = Sixteenth Double Dotted
		}

		int value = TGDuration.WHOLE;

		for(int i = 0; i <  (duration / 3); i ++){
			value = (value * 2);
		}
		if( (duration % 3) == 1){
			value = (value * 2);

			if (!isDoubleDotted) {
				tgDuration.setDotted(true);
			}
		}
		else if( (duration % 3) == 2){
			tgDuration.getDivision().setEnters(3);
			tgDuration.getDivision().setTimes(2);
		}
		
		tgDuration.setValue(value);
		
		return tgDuration;
	}

	private int getVelocityFromDynamic(TEComponentNoteDynamics dynamic) {
		switch (dynamic) {
			case FFF:
				return TGVelocities.FORTE_FORTISSIMO;
			case FF:
				return TGVelocities.FORTISSIMO;
			case F:
				return TGVelocities.FORTE;
			case MF:
				return TGVelocities.MEZZO_FORTE;
			case MP:
				return TGVelocities.MEZZO_PIANO;
			case P:
				return TGVelocities.PIANO;
			case PP:
				return TGVelocities.PIANISSIMO;
			case PPP:
				return TGVelocities.PIANO_PIANISSIMO;
			default: 
				return TGVelocities.DEFAULT;
		}
	}
	
	private TGNote addNote(TETrack track, TEComponentNote teNote, TGBeat tgBeat){
		int value = teNote.getFret();
		int string = teNote.getPosition().getString();

		if(track.getIsPercussion() ){
			int tuning = Math.min(track.getStringCount() - 2, PERCUSSION_TUNINGS.length) - 1;
			if(string >= 0 && string < PERCUSSION_TUNINGS[tuning].length){
				value += PERCUSSION_TUNINGS[tuning][string];
			}
		}
		
		TGNote tgNote = this.manager.getFactory().newNote();
		tgNote.setString(string + 1);
		tgNote.setValue(value);
		tgNote.setVelocity(this.getVelocityFromDynamic(teNote.getDynamics()));

		this.addNoteEffects(tgNote, teNote);

		int voiceIndex = 0;
		switch (teNote.getAttributes()) {
			case UpperVoice:
				voiceIndex = 0;
				break;
			case LowerVoice:
				voiceIndex = 1;
				break;
			default:
				break;
		}
		
		TGDuration tgDuration = this.getDuration(teNote.getDuration().getValue());
		TGVoice tgVoice = tgBeat.getVoice(voiceIndex);

		switch (teNote.getStroke()) {
			case UpStroke:
				tgBeat.getStroke().setDirection(TGStroke.STROKE_UP);
				tgBeat.getStroke().setValue(TGDuration.SIXTEENTH);
				break;
			case DownStroke:
				tgBeat.getStroke().setDirection(TGStroke.STROKE_DOWN);
				tgBeat.getStroke().setValue(TGDuration.SIXTEENTH);
				break;
			default:
				break;
		}

		tgVoice.getDuration().copyFrom(tgDuration);
		tgVoice.addNote(tgNote);

		return tgNote;
	}
	
	private void addNoteEffects(TGNote tgNote, TEComponentNote teNote) {
		TGNoteEffect tgNoteEffect = tgNote.getEffect();

		if (teNote.getIsGraceNote()) {
			TGEffectGrace grace = this.manager.getFactory().newEffectGrace();

			grace.setFret(teNote.getGraceNoteFret());
			grace.setDynamic(this.getVelocityFromDynamic(teNote.getDynamics()));

			switch (teNote.getGraceNoteEffect()) {
				case GraceHammerOn:
					grace.setTransition(TGEffectGrace.TRANSITION_HAMMER);
					break;
				case GraceSlide:
					grace.setTransition(TGEffectGrace.TRANSITION_SLIDE);
					break;
				case GraceBendRelease:
					grace.setTransition(TGEffectGrace.TRANSITION_BEND);
					break;
				case GraceMordent:
					break;
				case GraceDouble:
					break;
				case GraceGruppetto:
					break;
				case GraceTrill:
					break;
				default:
					break;
			}

			tgNoteEffect.setGrace(grace);
		}

		switch (teNote.getEffect1()) {
			case HammerOn: // intentional fall-through
			case PullOff:
				tgNoteEffect.setHammer(true);
				break;
			case Slide:
				tgNoteEffect.setSlide(true);
				break;
			case Choke:
				break;
			case Brush:
				break;
			case NaturalHarmonic:
			{
				TGEffectHarmonic harmonic = this.manager.getFactory().newEffectHarmonic();
				harmonic.setType(TGEffectHarmonic.TYPE_NATURAL);
				tgNoteEffect.setHarmonic(harmonic);
				break;
			}
			case ArtificialHarmonic:
			{
				TGEffectHarmonic harmonic = this.manager.getFactory().newEffectHarmonic();
				harmonic.setType(TGEffectHarmonic.TYPE_ARTIFICIAL);
				tgNoteEffect.setHarmonic(harmonic);
				break;
			}
			case PalmMute:
				tgNoteEffect.setPalmMute(true);
				break;
			case Tap:
				tgNoteEffect.setTapping(true);
				break;
			case Vibrato:
				tgNoteEffect.setVibrato(true);
				break;
			case Tremolo:
			{
				TGEffectTremoloPicking tremPicking = this.manager.getFactory().newEffectTremoloPicking();
				// No duration information in the TEF file.
				tgNoteEffect.setTremoloPicking(tremPicking);
				break;
			}
			case Bend:
			{
				TGEffectBend bend = this.manager.getFactory().newEffectBend();
				bend.addPoint(0, 0);
				bend.addPoint(6, 2);
				bend.addPoint(12, 2);
				tgNoteEffect.setBend(bend);
				break;
			}
			case BendRelease:
			{
				TGEffectBend bend = this.manager.getFactory().newEffectBend();
				bend.addPoint(0, 0);
				bend.addPoint(3, 2);
				bend.addPoint(6, 2);
				bend.addPoint(9, 0);
				bend.addPoint(12, 0);
				tgNote.getEffect().setBend(bend);
				break;
			}
			case Roll:
				break;
			case DeadNote:
				tgNoteEffect.setDeadNote(true);
				break;
			default:
				break;
		}

		switch (teNote.getEffect2()) {
			case LetRing:
				tgNoteEffect.setLetRing(true);
				break;
			case Slap:
				tgNoteEffect.setSlapping(true);
				break;
			case Rasgueado:
				break;
			case GhostNote:
				tgNoteEffect.setGhostNote(true);
				break;
			case TremUpOrDown:
				break;
			case TremDiveReturn:
				break;
			case Staccato:
				tgNoteEffect.setStaccato(true);
				break;
			case FadeIn:
				tgNoteEffect.setFadeIn(true);
				break;
			case FadeOut:
				break;
			case HideInNotation:
				break;
			default:
				break;
		}

		switch (teNote.getEffect3()) {
			case HammerOn: // intentional fall-through
			case PullOff:
				tgNoteEffect.setHammer(true);
				break;
			case Roll:
				break;
			case Choke:
				break;
			case Brush:
				break;
			case NaturalHarmonic:
			{
				TGEffectHarmonic harmonic = this.manager.getFactory().newEffectHarmonic();
				harmonic.setType(TGEffectHarmonic.TYPE_NATURAL);
				tgNoteEffect.setHarmonic(harmonic);
				break;
			}
			case ArtificialHarmonic:
			{
				TGEffectHarmonic harmonic = this.manager.getFactory().newEffectHarmonic();
				harmonic.setType(TGEffectHarmonic.TYPE_ARTIFICIAL);
				tgNoteEffect.setHarmonic(harmonic);
				break;
			}
			case LetRing:
				tgNoteEffect.setLetRing(true);
				break;
			case GhostNote:
				tgNoteEffect.setGhostNote(true);
				break;
			case DeadNote:
				tgNoteEffect.setDeadNote(true);
				break;
			case Variation:
				break;
			default:
				break;
		}

		if (teNote.getIsTieUpward() || teNote.getIsTieDownward()) {
			tgNote.setTiedNote(true);
		}

		// Additionally, TablEdit treats the dynamic PPP to mean a note is tied.
		// So any note with the dynamic PPP is to be considered tied, even if 
		// musically that may not always be the case.
		if (teNote.getDynamics() == TEComponentNoteDynamics.PPP) {
			tgNote.setTiedNote(true);
		}
	}
	
	private void addChord(List<TEChordDefinition> teChordDefinitions, TEComponentChord teChord, TGTrack tgTrack, TGBeat tgBeat) {
		int chordIndex = teChord.getChordIndex();
		if (chordIndex < 0 || chordIndex >= teChordDefinitions.size())
		{
			return;
		}

		TEChordDefinition teChordDefinition = teChordDefinitions.get(chordIndex);
		int[] strings = teChordDefinition.getFrets();
		
		TGChord tgChord = this.manager.getFactory().newChord(tgTrack.stringCount());
		tgChord.setName(teChordDefinition.getName());

		for(int i = 0; i < tgChord.countStrings(); i ++){
			byte fret = (byte)(strings[i]);
			int value = i < strings.length ? (int)fret : -1;
			tgChord.addFretValue(i, value);
		}

		if (tgChord.countNotes() <= 0) {
			return;
		}

		tgBeat.setChord(tgChord);
	}

	private void addGraceNoteMetadata(TGNote tgNote, TEComponentGraceNoteMetadata graceNoteMetadata) {
		TGEffectGrace graceEffect = tgNote.getEffect().getGrace();

		if (graceEffect == null) {
			graceEffect = this.manager.getFactory().newEffectGrace();
			tgNote.getEffect().setGrace(graceEffect);
		}

		// If the fret number is > 31 (5 bits) on the note, then we use this field in the metadata.
		// Using it blindly here as the fret will not be set (0) in this struct unless it is larger than 31.
		if (graceNoteMetadata.getDoubleNoteOne() > 0) {
			graceEffect.setFret(graceNoteMetadata.getDoubleNoteOne());
		}

		switch(graceNoteMetadata.getDuration()) {
			case GraceThirtySecond:
				graceEffect.setDuration(TGEffectGrace.DURATION_THIRTY_SECOND);
				break;
			case GraceSixteenth:
				graceEffect.setDuration(TGEffectGrace.DURATION_SIXTEENTH);
				break;
			case GraceEighth: // intentional fall-through
			case GraceQuarter: // intentional fall-through
			case GraceHalf:
				// Currently unsupported in TuxGuitar UI. We only show 1/64, 1/32, and 1/16.
				// So defaulting to 1/16 as it's the closest value we can use.
				graceEffect.setDuration(TGEffectGrace.DURATION_SIXTEENTH);
				break;
			default:
				break;
		}
	}

	private void addRest(TEComponentRest teRest, TGBeat tgBeat) {
		int voiceIndex = 0;

		if (teRest.getIsLowerVoice()) {
			voiceIndex = 1;
		}

		TGVoice tgVoice = tgBeat.getVoice(voiceIndex);
		tgVoice.clearNotes();
	}

	private void addTextEvent(TESong teSong, TEComponentTextEvent teTextEvent, TGBeat tgBeat) {
		TGText tgText = this.manager.getFactory().newText();
		tgText.setBeat(tgBeat);

		int textIndex = teTextEvent.getTextIndex();
		List<String> textEvents = teSong.getSongMetadata().getTextEvents();

		if (textIndex < 0 || textIndex >= textEvents.size()) {
			return;
		}

		String teTextEventText = textEvents.get(textIndex);
		tgText.setValue(teTextEventText);

		tgBeat.setText(tgText);
	}

	private void addTempoChange(TEComponentTempoChange teTempoChange, TGSong tgSong, TGMeasure tgMeasure) {
		TGTempo newTempoEvent = this.manager.getFactory().newTempo();
		newTempoEvent.copyFrom(tgMeasure.getTempo());
		newTempoEvent.setQuarterValue(teTempoChange.getBpm());

		this.manager.changeTempos(tgSong, tgMeasure.getHeader(), newTempoEvent, true);
	}

	private void addEnding(TEComponentEnding teEnding, TGMeasure tgMeasure) {
		TGMeasureHeader measureHeader = tgMeasure.getHeader();

		if (teEnding.getIsOpenBracket()) {
			measureHeader.setRepeatOpen(true);
		}

		int endingNumber = teEnding.getEndingNumber();

		if (teEnding.getIsCloseBracket() && endingNumber >= 2) {
			measureHeader.setRepeatClose(endingNumber - 1);
		} else if (measureHeader.getRepeatClose() == 0 && endingNumber != 0) {
			measureHeader.setRepeatAlternative(1 << (endingNumber - 1));
		}
	}
}

class TGSongAdjuster {
	
	protected TGSong song;
	protected TGSongManager manager;
	
	public TGSongAdjuster(TGSongManager manager, TGSong song) {
		this.manager = manager;
		this.song = song;
	}
	
	public TGSong process() {
		Iterator<TGTrack> tracks = this.song.getTracks();
		while(tracks.hasNext()) {
			TGTrack track = (TGTrack)tracks.next();
			Iterator<TGMeasure> measures = track.getMeasures();
			while(measures.hasNext()) {
				TGMeasure measure = (TGMeasure)measures.next();
				this.process(measure);
			}
		}
		return this.song;
	}
	
	public void process(TGMeasure measure){
		this.manager.getMeasureManager().orderBeats(measure);
		this.adjustBeats(measure);
	}
	
	public void adjustBeats(TGMeasure measure) {
		TGBeat previous = null;
		boolean finish = true;
		
		long measureStart = measure.getStart();
		long measureEnd = (measureStart + measure.getLength());
		for(int i = 0;i < measure.countBeats();i++){
			TGBeat beat = measure.getBeat( i );
			long beatStart = beat.getStart();
			long beatLength = beat.getVoice(0).getDuration().getTime();
			if(previous != null){
				long previousStart = previous.getStart();
				long previousLength = previous.getVoice(0).getDuration().getTime();
				
				// check for a chord in a rest beat
				if( beat.getVoice(0).isRestVoice() && beat.isChordBeat() ){
					TGBeat candidate = null;
					TGBeat next = this.manager.getMeasureManager().getFirstBeat( measure.getBeats() );
					while( next != null ){
						if( candidate != null && next.getStart() > beat.getStart() ){
							break;
						}
						if(! next.getVoice(0).isRestVoice() && !next.isChordBeat() ){
							candidate = next;
						}
						next = this.manager.getMeasureManager().getNextBeat(measure.getBeats(), next);
					}
					if(candidate != null){
						candidate.setChord( beat.getChord() );
					}
					measure.removeBeat(beat);
					finish = false;
					break;
				}
				
				// check the duration
				if(previousStart < beatStart && (previousStart + previousLength) > beatStart){
					if(beat.getVoice(0).isRestVoice()){
						measure.removeBeat(beat);
						finish = false;
						break;
					}
					TGDuration duration = TGDuration.fromTime(this.manager.getFactory(), (beatStart - previousStart) );
					previous.getVoice(0).getDuration().copyFrom( duration );
				}
			}
			if( (beatStart + beatLength) > measureEnd ){
				if(beat.getVoice(0).isRestVoice()){
					measure.removeBeat(beat);
					finish = false;
					break;
				}
				TGDuration duration = TGDuration.fromTime(this.manager.getFactory(), (measureEnd - beatStart) );
				beat.getVoice(0).getDuration().copyFrom( duration );
			}
			previous = beat;
		}
		if(!finish){
			adjustBeats(measure);
		}
	}
}
