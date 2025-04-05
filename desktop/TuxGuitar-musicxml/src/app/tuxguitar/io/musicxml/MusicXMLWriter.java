package app.tuxguitar.io.musicxml;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import app.tuxguitar.gm.GMChannelRoute;
import app.tuxguitar.gm.GMChannelRouter;
import app.tuxguitar.gm.GMChannelRouterConfigurator;
import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.io.musicxml.MusicXMLLyricWriter.MusicXMLMeasureLyric;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGDivisionType;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMarker;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGTempo;
import app.tuxguitar.song.models.TGTimeSignature;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVelocities;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.song.models.effects.TGEffectBend;
import app.tuxguitar.song.models.effects.TGEffectBend.BendPoint;
import app.tuxguitar.song.models.effects.TGEffectHarmonic;
import app.tuxguitar.util.TGMusicKeyUtils;
import app.tuxguitar.util.TGVersion;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;

// Note: The order of elements is important
// see https://www.w3.org/2021/06/musicxml40/musicxml-reference/elements/

public class MusicXMLWriter{

	private static final String[] DURATION_NAMES = new String[]{ "whole", "half", "quarter", "eighth", "16th", "32nd", "64th", };
	private Map<Integer, String> durations;	// duration name per TGDuration value

	private static final int DURATION_DIVISIONS = (int)TGDuration.QUARTER_TIME;

	private static final int[] DURATION_VALUES = new int[]{
		DURATION_DIVISIONS * 4, // WHOLE
		DURATION_DIVISIONS * 2, // HALF
		DURATION_DIVISIONS * 1, // QUARTER
		DURATION_DIVISIONS / 2, // EIGHTH
		DURATION_DIVISIONS / 4, // SIXTEENTH
		DURATION_DIVISIONS / 8, // THIRTY_SECOND
		DURATION_DIVISIONS / 16, // SIXTY_FOURTH
	};

	private TGSongManager manager;

	private OutputStream stream;

	private Document document;

	public MusicXMLWriter(OutputStream stream){
		this.stream = stream;
		this.durations = new HashMap<Integer, String>();
		this.durations.put(TGDuration.WHOLE, DURATION_NAMES[0]);
		this.durations.put(TGDuration.HALF, DURATION_NAMES[1]);
		this.durations.put(TGDuration.QUARTER, DURATION_NAMES[2]);
		this.durations.put(TGDuration.EIGHTH, DURATION_NAMES[3]);
		this.durations.put(TGDuration.SIXTEENTH, DURATION_NAMES[4]);
	}

	public void writeSong(TGSong song) throws TGFileFormatException{
		try{
			this.manager = new TGSongManager();
			this.document = newDocument();

			Node node = this.addNode(this.document, "score-partwise");
			this.addAttribute(node, "version", "4.0");
			this.writeHeaders(song, node);
			this.writeSong(song, node);
			this.saveDocument();

			this.stream.flush();
			this.stream.close();
		}catch(Throwable throwable){
			throw new TGFileFormatException("Could not write song!.",throwable);
		}
	}

	private void writeHeaders(TGSong song, Node parent){
		this.writeWork(song, parent);
		this.writeIdentification(song, parent);
	}

	private void writeWork(TGSong song, Node parent){
		this.addNode(this.addNode(parent, "work"), "work-title", song.getName());
	}

	private void writeIdentification(TGSong song, Node parent){
		Node identification = this.addNode(parent, "identification");
		this.addAttribute(this.addNode(identification, "creator",song.getAuthor()), "type", "composer");
		this.addNode(this.addNode(identification, "encoding"), "software", "TuxGuitar " + TGVersion.CURRENT.getVersion());
	}

	private void writeSong(TGSong song, Node parent){
		this.writePartList(song, parent);
		this.writeParts(song, parent);
	}

	private void writePartList(TGSong song, Node parent){
		Node partList = this.addNode(parent, "part-list");

		GMChannelRouter gmChannelRouter = new GMChannelRouter();
		GMChannelRouterConfigurator gmChannelRouterConfigurator = new GMChannelRouterConfigurator(gmChannelRouter);
		gmChannelRouterConfigurator.configureRouter(song.getChannels());

		Iterator<TGTrack> tracks = song.getTracks();
		while(tracks.hasNext()){
			TGTrack track = (TGTrack)tracks.next();
			TGChannel channel = this.manager.getChannel(song, track.getChannelId());

			Node scoreParts = this.addNode(partList, "score-part");
			this.addAttribute(scoreParts, "id", "P" + track.getNumber());

			this.addNode(scoreParts, "part-name", track.getName());

			if( channel != null ){
				GMChannelRoute gmChannelRoute = gmChannelRouter.getRoute(channel.getChannelId());

				Node scoreInstrument = this.addAttribute(this.addNode(scoreParts, "score-instrument"), "id", "P" + track.getNumber() + "-I1");
				this.addNode(scoreInstrument, "instrument-name",channel.getName());

				Node midiInstrument = this.addAttribute(this.addNode(scoreParts, "midi-instrument"), "id", "P" + track.getNumber() + "-I1");
				this.addNode(midiInstrument, "midi-channel",Integer.toString(gmChannelRoute != null ? gmChannelRoute.getChannel1() + 1 : 16));
				this.addNode(midiInstrument, "midi-program",Integer.toString(channel.getProgram() + 1));
			}
		}
	}

	private void writeParts(TGSong song, Node parent){
		Iterator<TGTrack> tracks = song.getTracks();
		while(tracks.hasNext()){
			TGTrack track = (TGTrack)tracks.next();
			this.writeTrack(track, parent);
		}
	}

	private void writeTrack(TGTrack track, Node parent){
		Node part = this.addAttribute(this.addNode(parent, "part"), "id", "P" + track.getNumber());

		TGMeasure previousMeasure = null;

		MusicXMLLyricWriter lyricWriter = new MusicXMLLyricWriter(track);

		Iterator<TGMeasure> measures = track.getMeasures();
		TGMeasure nextMeasure = null;
		while(measures.hasNext() || nextMeasure != null){
			TGMeasure currentMeasure = nextMeasure != null ? nextMeasure : (TGMeasure)measures.next();
			nextMeasure = measures.hasNext() ? (TGMeasure)measures.next() : null;

			Node measureNode = this.addAttribute(this.addNode(part, "measure"), "number",Integer.toString(currentMeasure.getNumber()));

			this.writeMeasureAttributes(measureNode, currentMeasure, previousMeasure, track.isPercussion());

			this.writeDirection(measureNode, currentMeasure, previousMeasure);

			this.writeBarline(measureNode, currentMeasure, previousMeasure, nextMeasure);

			MusicXMLMeasureLyric[] measureLyrics = lyricWriter.generateLyricList(currentMeasure);

			// score
			boolean currentMeasureIsEmpty = true;
			for (int nVoice=0; nVoice<TGBeat.MAX_VOICES; nVoice++){
				// assuming lyrics are attached to voice 0
				this.writeBeats(measureNode, currentMeasure, nVoice, currentMeasureIsEmpty, false, nVoice==0 ? measureLyrics : null);
				currentMeasureIsEmpty = false;
			}
			// tab
			if (!track.isPercussion()){
				currentMeasureIsEmpty = true;
				backToMeasureStart(measureNode, currentMeasure);
				for (int nVoice=0; nVoice<TGBeat.MAX_VOICES; nVoice++){
					this.writeBeats(measureNode, currentMeasure, nVoice, currentMeasureIsEmpty, true, null);
					currentMeasureIsEmpty = false;
				}
			}

			previousMeasure = currentMeasure;
		}
	}

	private void backToMeasureStart(Node parent, TGMeasure measure){
		Node backupNode = this.addNode(parent, "backup");
		TGTimeSignature ts = measure.getTimeSignature();
		this.addNode(backupNode, "duration", String.valueOf((int)(TGDuration.QUARTER * DURATION_DIVISIONS * ts.getNumerator() / ts.getDenominator().getValue())));
	}

	private void writeBarline(Node parent, TGMeasure currentMeasure, TGMeasure previousMeasure, TGMeasure nextMeasure){
		// TODO:
		// add, when available in tuxguitar; bar-style, coda, wavy-line etc..
		// https://www.w3.org/2021/06/musicxml40/musicxml-reference/elements/barline/

		Node startBarLine = null;
		Node endBarLine = null;

		String previousMeasureAlternateEndings = generateAlternateEndingString(previousMeasure);
		String currentMeasureAlternateEndings = generateAlternateEndingString(currentMeasure);
		String nextMeasureAlternateEndings = generateAlternateEndingString(nextMeasure);

		if (!currentMeasureAlternateEndings.equals("")){
			if (!currentMeasureAlternateEndings.equals(previousMeasureAlternateEndings)){
				Node barLine = this.addNode(parent, "barline");
				startBarLine = barLine;

				Node ending = this.addNode(barLine, "ending", currentMeasureAlternateEndings);
				this.addAttribute(ending, "number", currentMeasureAlternateEndings);
				this.addAttribute(ending, "type", "start");
			}

			if (!currentMeasureAlternateEndings.equals(nextMeasureAlternateEndings)){
					Node barLine = this.addNode(parent, "barline");
					endBarLine = barLine;

					Node ending = this.addNode(barLine, "ending", currentMeasureAlternateEndings);
					this.addAttribute(ending, "number", currentMeasureAlternateEndings);
					this.addAttribute(ending, "type", "stop");
			}
		}

		if (currentMeasure.isRepeatOpen()){
			Node barLine = startBarLine != null ? startBarLine : this.addNode(parent, "barline");
			startBarLine = barLine;
			Node repeat = this.addNode(barLine, "repeat");
			this.addAttribute(repeat, "direction", "forward");
		}

		// In the case where we start a repeat and end a repeat on the same measure, we want to have two separate barline elements.
		// MusicXML only allows 1 repeat per barline, but allows multiple barlines in a single measure.
		if (currentMeasure.getRepeatClose() > 0){
			Node barLine = endBarLine != null ? endBarLine : this.addNode(parent, "barline");
			endBarLine = barLine;
			Node repeat = this.addNode(barLine, "repeat");
			this.addAttribute(repeat, "direction", "backward");
			// TuxGuitar treats this as zero-indexed, but MusicXML is one-indexed, so we need to add 1.
			this.addAttribute(repeat, "times", Integer.toString(currentMeasure.getRepeatClose() + 1));
		}
	}

	// Takes in a measure, outputs a string for what alternate endings are used.
	// If 1 and 3 are used, result is "1,3"
	private String generateAlternateEndingString(TGMeasure measure){
		StringBuilder alternateEndingNumbers = new StringBuilder();

		if (measure != null && measure.getHeader().getRepeatAlternative() != 0){
			byte repeatAlternativeByte = (byte)measure.getHeader().getRepeatAlternative();
			BitSet repeatAlternateBitset = BitSet.valueOf(new byte[]{ repeatAlternativeByte });

			for (int i = repeatAlternateBitset.nextSetBit(0); i >= 0; i = repeatAlternateBitset.nextSetBit(i+1)){
				// +1 to account for zero-based indexing internally, while musicxml is one-based indexed.
				alternateEndingNumbers.append(Integer.toString(i + 1) + ",");
			}
			if (alternateEndingNumbers.length() > 0){
				// Delete extra comma at end.
				alternateEndingNumbers.deleteCharAt(alternateEndingNumbers.length() - 1);
			}
		}

		return alternateEndingNumbers.toString();
	}

	private void writeMeasureAttributes(Node parent, TGMeasure measure, TGMeasure previous, boolean isPercussion){
		boolean divisionChanges = (previous == null);
		boolean keyChanges = (previous == null || measure.getKeySignature() != previous.getKeySignature());
		boolean clefChanges = (previous == null || measure.getClef() != previous.getClef());
		boolean timeSignatureChanges = (previous == null || !measure.getTimeSignature().isEqual(previous.getTimeSignature()));

		if (divisionChanges || keyChanges || clefChanges || timeSignatureChanges){
			Node measureAttributes = this.addNode(parent, "attributes");
			if(divisionChanges){
				this.addNode(measureAttributes, "divisions",Integer.toString(DURATION_DIVISIONS));
			}
			if(keyChanges){
				this.writeKeySignature(measureAttributes, measure.getKeySignature());
			}
			if(timeSignatureChanges){
				this.writeTimeSignature(measureAttributes,measure.getTimeSignature());
			}

			this.addNode(measureAttributes, "staves", "2");
			this.addNode(measureAttributes, "part-symbol", "none");

			if(clefChanges){
				this.writeClef(measureAttributes,measure.getClef(), isPercussion);
			}

			if (!isPercussion && (previous==null || measure.getNumber() == 1)){
				this.writeTuning(measureAttributes, measure.getTrack(), measure.getKeySignature());
			}
		}
	}

	private void writeTuning(Node parent, TGTrack track, int keySignature){
		Node staffDetailsNode = this.addNode(parent, "staff-details");
		this.addNode(staffDetailsNode, "staff-type", "alternate");
		this.addAttribute(staffDetailsNode, "number", "2");
		this.addNode(staffDetailsNode, "staff-lines", Integer.toString( track.stringCount() ));
		for( int i = track.stringCount() ; i > 0 ; i --){
			TGString string = track.getString( i );
			Node stringNode = this.addNode(staffDetailsNode, "staff-tuning");
			this.addAttribute(stringNode, "line", Integer.toString( (track.stringCount() - string.getNumber()) + 1 ) );
			this.writeNote(stringNode, "tuning-", string.getValue(), keySignature);
		}
		// MusicXML 4.0 defines capos as offsets that are non-negative.
		int trackOffset = track.getOffset();
		if (trackOffset > 0){
			this.addNode(staffDetailsNode, "capo", Integer.toString( trackOffset ));
		}
	}

	private void writeNote(Node parent, String prefix, int value, int keySignature){
		this.addNode(parent,prefix+"step", TGMusicKeyUtils.noteShortName(value,keySignature));
		int alteration = TGMusicKeyUtils.noteAlteration(value, keySignature);
		if(alteration != TGMusicKeyUtils.NATURAL){
			this.addNode(parent,prefix+"alter", ( alteration == TGMusicKeyUtils.SHARP ? "1" : "-1" ) );
		}
		this.addNode(parent,prefix+"octave", String.valueOf(TGMusicKeyUtils.noteOctave(value, keySignature)));
	}

	private void writeTimeSignature(Node parent, TGTimeSignature ts){
		Node node = this.addNode(parent, "time");
		this.addNode(node, "beats",Integer.toString(ts.getNumerator()));
		this.addNode(node, "beat-type",Integer.toString(ts.getDenominator().getValue()));
	}

	private void writeKeySignature(Node parent, int ks){
		int value = ks;
		if(value != 0){
			value = ( (((ks - 1) % 7) + 1) * ( ks > 7?-1:1));
		}
		Node key = this.addNode(parent, "key");
		this.addNode(key, "fifths",Integer.toString( value ));
	}

	private void writeClef(Node parent, int clef, boolean isPercussion){
		// first clef: score
		Node node = this.addNode(parent, "clef");
		if (!isPercussion){
			this.addAttribute(node, "number", "1");
		}

		if (isPercussion){
			this.addNode(node, "sign", "percussion");
		}
		else if(clef == TGMeasure.CLEF_TREBLE){
			this.addNode(node, "sign", "G");
			this.addNode(node, "line", "2");
			this.addNode(node, "clef-octave-change", String.valueOf(-1));
		}
		else if(clef == TGMeasure.CLEF_BASS){
			this.addNode(node, "sign", "F");
			this.addNode(node, "line", "4");
			this.addNode(node, "clef-octave-change", String.valueOf(-1));
		}
		else if(clef == TGMeasure.CLEF_TENOR){
			this.addNode(node, "sign", "G");
			this.addNode(node, "line", "2");
		}
		else if(clef == TGMeasure.CLEF_ALTO){
			this.addNode(node, "sign", "G");
			this.addNode(node, "line", "2");
		}

		// second clef: tablature
		if (!isPercussion){
			node = this.addNode(parent, "clef");
			this.addAttribute(node, "number", "2");
			this.addNode(node, "sign", "TAB");
		}
	}

	private String getDynamicNameFromVelocity(TGNote note){
		int noteVelocity = note.getVelocity();

		switch(noteVelocity){
			case TGVelocities.PIANO_PIANISSIMO:
				return "ppp";
			case TGVelocities.PIANISSIMO:
				return "pp";
			case TGVelocities.PIANO:
				return "p";
			case TGVelocities.MEZZO_PIANO:
				return "mp";
			case TGVelocities.MEZZO_FORTE:
				return "mf";
			case TGVelocities.FORTE:
				return "f";
			case TGVelocities.FORTISSIMO:
				return "ff";
			case TGVelocities.FORTE_FORTISSIMO:
				return "fff";
			default:
				return "";
		}
	}

	private void writeDynamics(Node parent, TGNote note){
		Node direction = this.addAttribute(this.addNode(parent, "direction"), "placement", "above");
		Node directionType = this.addNode(direction, "direction-type");
		Node dynamics = this.addNode(directionType, "dynamics");

		String dynamicType = getDynamicNameFromVelocity(note);

		if (!dynamicType.equals("")){
			this.addNode(dynamics, dynamicType);
		}
	}

	private void writeDirection(Node parent, TGMeasure measure, TGMeasure previous){
		boolean needsDirectionNode = false;
		Node direction = this.addAttribute(this.addNode(parent, "direction"), "placement", "above");

		boolean tempoChanges = (previous == null ||
				measure.getTempo().getRawValue() != previous.getTempo().getRawValue() ||
				measure.getTempo().getBase() != previous.getTempo().getBase());

		// TODO: Add coda, and segno support once added elsewhere.
		// https://www.w3.org/2021/06/musicxml40/musicxml-reference/elements/direction-type/

		if(tempoChanges){
			Node directionType = this.addNode(direction, "direction-type");
			Node metronome = this.addNode(directionType, "metronome");
			TGTempo tempo = measure.getTempo();
			this.addNode(metronome, "beat-unit", this.durations.get(tempo.getBase()));
			if (tempo.isDotted()) {
				this.addNode(metronome, "beat-unit-dot");
			}
			this.addNode(metronome, "per-minute", String.valueOf(measure.getTempo().getRawValue()));
			needsDirectionNode = true;
		}

		TGMarker measureMarker = measure.getHeader().getMarker();
		if (measureMarker != null){
			Node directionType = this.addNode(direction, "direction-type");
			this.addNode(directionType, "rehearsal", measureMarker.getTitle());
			needsDirectionNode = true;
		}

		if (!needsDirectionNode){
			this.removeNode(direction);
		}
	}


	private void writeBeats(Node parent, TGMeasure measure, int nVoice, boolean measureIsEmpty, boolean isTablature, MusicXMLMeasureLyric[] lyrics){
		int ks = measure.getKeySignature();
		int beatCount = measure.countBeats();
		int lyricIndex = 0;
		// store first rest beats of voice in list, before finding possibly a non-empty beat
		List<TGBeat> firstBeats = new ArrayList<TGBeat>();
		boolean wroteSomething = false;
		long lastWrittenNoteEnd = 0;	// tick of last (non-empty) beat corresponding to voice

		int lastVelocity = TGVelocities.MIN_VELOCITY - 1; // Below minimum to have overwrite with first note.
		Map<Integer, TGNote> previousNotesOnAllStrings = new HashMap<Integer, TGNote>();

		for(int b = 0; b < beatCount; b ++){
			TGBeat beat = measure.getBeat( b );
			TGVoice voice = beat.getVoice(nVoice);

			if (voice.isRestVoice() && !wroteSomething){
				firstBeats.add(beat);
				continue;
			}
			// here, something has been found to be written
			// need to rewind to measure start?
			if (!measureIsEmpty && !wroteSomething){
				backToMeasureStart(parent, measure);
			}
			// need to insert rests before?
			if (!firstBeats.isEmpty()){
				for (TGBeat restBeat : firstBeats){
					insertRest(parent, restBeat.getVoice(nVoice).getDuration(), nVoice, isTablature);
				}
				firstBeats.clear();
			}
			if(voice.isRestVoice()){
				if (beat.getStart() >= lastWrittenNoteEnd){
					insertRest(parent, voice.getDuration(), nVoice, isTablature);
				}
			} else{
				int noteCount = voice.countNotes();

				for(int n = 0; n < noteCount; n ++){
					TGNote note = voice.getNote( n );
					TGNote previousNoteOnString = previousNotesOnAllStrings.get(note.getString());

					int noteVelocity = note.getVelocity();
					if (noteVelocity != lastVelocity){
						lastVelocity = noteVelocity;
						this.writeDynamics(parent, note);
					}

					// write palm mute symbol as text
					if (!isTablature && note.getEffect().isPalmMute()) {
						Node direction = this.addAttribute(this.addNode(parent, "direction"), "placement", "above");
						this.addAttribute(direction, "placement", "below");
						Node directionType = this.addNode(direction, "direction-type");
						Node words = this.addNode(directionType, "words", "P.M.");	
					}

					Node noteNode = this.addNode(parent, "note");

					int stringValue = beat.getMeasure().getTrack().getString(note.getString()).getValue();
					int noteValue = note.getValue();
					int harmonicValue = 0;

					// Tablature remains unchanged, staff needs to be modified to adjust pitch.
					if (!isTablature){
						TGEffectHarmonic harmonicEffect = note.getEffect().getHarmonic();
						if (harmonicEffect != null && harmonicEffect.isNatural()){
							harmonicValue = TGEffectHarmonic.NATURAL_FREQUENCIES[harmonicEffect.getData()][1];
							harmonicValue -= noteValue;
						}
					}

					int harmonicAdjustedValue = stringValue + noteValue + harmonicValue;

					if (n > 0){
						this.addNode(noteNode, "chord");
					}

					Node pitchNode = this.addNode(noteNode, "pitch");
					this.writeNote(pitchNode, "", harmonicAdjustedValue, ks);

					this.writeDurationAndVoice(noteNode, voice.getDuration(), note.isTiedNote(), nVoice);

					if (isTablature){
						this.addNode(noteNode, "stem", "none");
					}

					if (note.getEffect().isGhostNote()){
						Node noteheadNode = this.addNode(noteNode, "notehead", "normal");
						this.addAttribute(noteheadNode, "parentheses", "yes");
					} else if (!isTablature && note.getEffect().isDeadNote()){
						Node noteheadNode = this.addNode(noteNode, "notehead", "x");
					}

					this.addNode(noteNode, "staff", isTablature ? "2" : "1");

					Node notationsNode = this.addNode(noteNode, "notations");
					writeArticulationNotations(notationsNode, note);
					writeTechnicalNotations(notationsNode, note, previousNoteOnString, isTablature);
					writeOrnamentsNotations(notationsNode, note);

					// Slapping / Popping would be applied here... But the MusicXML 4.0 spec does not have defined elements for that.
					// GP has it as a processing instruction, but until it is officially supported, we will leave it out.
					// Uncomment and update when the MusicXML spec changes to support slapping / popping.
					// if (note.getEffect().isSlapping()){
					// 	Node slapNode = this.addNode(notationsNode, "bass-attack");
					// 	this.addAttribute(slapNode, "type", "slap");
					// }
					// if (note.getEffect().isPopping()){
					// 	Node slapNode = this.addNode(notationsNode, "bass-attack");
					// 	this.addAttribute(slapNode, "type", "pop");
					// }

					// We are blindly trusting the input data is correct, ex: A slide from a fret to the same fret.
					// This is handled on our end, but may not be handled in other softwares.
					boolean isNoteSlide = note.getEffect().isSlide();
					boolean isPreviousNoteSlide = previousNoteOnString != null ? previousNoteOnString.getEffect().isSlide() : false;
					if (isPreviousNoteSlide){
						Node slideNode = this.addNode(notationsNode, "slide");
						this.addAttribute(slideNode, "type", "stop");
					}
					if (isNoteSlide){
						Node slideNode = this.addNode(notationsNode, "slide");
						this.addAttribute(slideNode, "type", "start");
					}

					this.removeNodeIfNoChildren(notationsNode);

					if (note.getEffect().isLetRing()){
						Node tiedNode = this.addNode(noteNode, "tied");
						this.addAttribute(tiedNode, "type", "let-ring");
					}

					if(!isTablature && n==0){
						// Attach lyric to the first note
						try{
							MusicXMLMeasureLyric measureLyric = lyrics[lyricIndex++];
							writeLyric(noteNode, measureLyric);
						} catch (Exception e){
							// ignore
							// can be out of bound? when there are more lyrics than text
							// can be null if there is an offset
						}
					}

					writePlay(noteNode, note);

					lastWrittenNoteEnd = beat.getStart() + voice.getDuration().getTime();
					previousNotesOnAllStrings.put(note.getString(), note);
				}
				wroteSomething = true;
			}
		}
		// empty measure? If so, fill with rests
		if (!wroteSomething && measureIsEmpty && !firstBeats.isEmpty()){
			for (TGBeat restBeat : firstBeats){
				insertRest(parent, restBeat.getVoice(nVoice).getDuration(), nVoice, isTablature);
			}
		}
	}

	private void writeArticulationNotations(Node parent, TGNote note){
		Node articulationsNode = this.addNode(parent, "articulations");

		if (note.getEffect().isAccentuatedNote()){
			this.addNode(articulationsNode, "accent");
		}
		if (note.getEffect().isHeavyAccentuatedNote()){
			this.addNode(articulationsNode, "strong-accent");
		}
		if (note.getEffect().isStaccato()){
			this.addNode(articulationsNode, "staccato");
		}

		this.removeNodeIfNoChildren(articulationsNode);
	}

	private void writeTechnicalNotations(Node parent, TGNote note, TGNote previousNoteOnString, boolean isTablature){
		Node technicalNode = this.addNode(parent, "technical");

		if (isTablature){
			this.addNode(technicalNode, "fret", Integer.toString( note.getValue() ));
			this.addNode(technicalNode, "string", Integer.toString( note.getString() ));
		}

		// TODO: Add logic for using <hammer-on> or <pull-off>.
		// https://www.w3.org/2021/06/musicxml40/musicxml-reference/elements/pull-off/
		// Need to know PREV note on same string to determine if last note was HO or PO, for stop tag.
		// Need to know NEXT note on same string to determine if this note is a HO or a PO, for start tag.
		if (previousNoteOnString != null && previousNoteOnString.getEffect().isHammer()){
			Node hammerNode = this.addNode(technicalNode, "hammer-on");
			this.addAttribute(hammerNode, "type", "stop");
		}

		if (note.getEffect().isHammer()){
			Node hammerNode = this.addNode(technicalNode, "hammer-on", "H");
			this.addAttribute(hammerNode, "type", "start");
		}

		if (note.getEffect().isTapping()){
			this.addNode(technicalNode, "tap");
		}

		TGEffectBend bendEffect = note.getEffect().getBend();
		if (bendEffect != null){
			boolean isFirstBend = true;
			Integer previousBendValue = 0;

			for (BendPoint currentBendPoint : bendEffect.getPoints()){
				if (previousBendValue == currentBendPoint.getValue()){
					isFirstBend = false;
					continue;
				}

				boolean isPreBend = false;
				boolean isRelease = false;

				if (isFirstBend && currentBendPoint.getValue() != 0){
					isPreBend = true;
				}

				else if (currentBendPoint.getValue() == 0){
					isRelease = true;
				}

				Node bendNode = this.addNode(technicalNode, "bend");
				this.addAttribute(bendNode, "shape", "curved");

				// Pitch is stored in 1/4s of semitones. MusicXML wants 1/2s of semitones, so divide by 2.
				float pitchAlter = currentBendPoint.getValue() / 2.0f;
				this.addNode(bendNode, "bend-alter", Float.toString(pitchAlter));

				if (isPreBend){
					this.addNode(bendNode, "pre-bend");
				}
				else if (isRelease){
					this.addNode(bendNode, "release");
				}

				// TODO: Add trembar support, with with-bar element
				// https://www.w3.org/2021/06/musicxml40/musicxml-reference/elements/with-bar/

				previousBendValue = currentBendPoint.getValue();
				isFirstBend = false;
			}
		}

		TGEffectHarmonic harmonicEffect = note.getEffect().getHarmonic();
		if (harmonicEffect != null){
			Node harmonicNode = this.addNode(technicalNode, "harmonic");

			if (harmonicEffect.isNatural()){
				this.addNode(harmonicNode, "natural");
			}
			else if (harmonicEffect.isArtificial() || harmonicEffect.isPinch()){
				this.addNode(harmonicNode, "artificial");
			}

			// TODO: Can add base-pitch, touching-pitch, sounding-pitch.
			// https://www.w3.org/2021/06/musicxml40/musicxml-reference/elements/harmonic/
		}

		this.removeNodeIfNoChildren(technicalNode);
	}

	private void writeOrnamentsNotations(Node parent, TGNote note){
		Node ornamentsNode = this.addNode(parent, "ornaments");

		if (note.getEffect().isTremoloPicking()){
			int tremoloDuration = note.getEffect().getTremoloPicking().getDuration().getValue();
			int tremoloMarkings = 0;

			int loopDuration = TGDuration.QUARTER;
			while (loopDuration < tremoloDuration){
				loopDuration *= 2;
				tremoloMarkings++;
			}

			this.addNode(ornamentsNode, "tremolo", Integer.toString(tremoloMarkings));
		}

		if (note.getEffect().isTrill()){
			Node trillNode = this.addNode(ornamentsNode, "trill-mark");

			// Default case of unison. As MusicXML only supports the following.
			// half, unison, whole
			// https://www.w3.org/2021/06/musicxml40/musicxml-reference/data-types/trill-step/
			String trillStep = "unison";
			int trillFret = note.getEffect().getTrill().getFret();
			int trillFretDifference = trillFret - note.getValue();
			if (trillFretDifference == 2){
				trillStep = "whole";
			}
			else if (trillFretDifference == 1){
				trillStep = "half";
			}

			this.addAttribute(trillNode, "trill-step", trillStep);
		}

		this.removeNodeIfNoChildren(ornamentsNode);
	}

	private void writePlay(Node parent, TGNote note){
		Node playNode = this.addNode(parent, "play");

		if (note.getEffect().isDeadNote()){
			this.addNode(playNode, "mute", "straight");
		}
		else if (note.getEffect().isPalmMute()){
			this.addNode(playNode, "mute", "palm");
		}

		this.removeNodeIfNoChildren(playNode);
	}

	private void insertRest(Node parent, TGDuration duration, int nVoice, boolean isTablature){
		Node noteRestNode = this.addNode(parent, "note");
		this.addNode(noteRestNode, "rest");
		this.writeDurationAndVoice(noteRestNode, duration, false, nVoice);
		this.addNode(noteRestNode, "staff", isTablature ? "2" : "1");
	}

	private void writeLyric(Node parent, MusicXMLMeasureLyric measureLyric){
		if (measureLyric.text.length() > 0){
			Node lyricNode = this.addNode(parent, "lyric");

			this.addNode(lyricNode, "syllabic", measureLyric.syllabic.toString());
			this.addNode(lyricNode, "text", measureLyric.text);
		}
	}

	private void writeDurationAndVoice(Node parent, TGDuration duration, boolean isTiedNote, int nVoice){
		int index = duration.getIndex();
		if( index >=0 && index <= 6 ){
			int value = (DURATION_VALUES[ index ] * duration.getDivision().getTimes() / duration.getDivision().getEnters());
			if(duration.isDotted()){
				value += (value / 2);
			}
			else if(duration.isDoubleDotted()){
				value += ((value / 4) * 3);
			}

			this.addNode(parent, "duration",Integer.toString(value));
			if(isTiedNote){
				this.addAttribute(this.addNode(parent, "tie"), "type", "stop");
			}
			this.addNode(parent, "voice", String.valueOf(nVoice+1));

			this.addNode(parent, "type",DURATION_NAMES[ index ]);

			if(duration.isDotted()){
				this.addNode(parent, "dot");
			}
			else if(duration.isDoubleDotted()){
				this.addNode(parent, "dot");
				this.addNode(parent, "dot");
			}

			if(!duration.getDivision().isEqual(TGDivisionType.NORMAL)){
				Node divisionType = this.addNode(parent, "time-modification");
				this.addNode(divisionType, "actual-notes",Integer.toString(duration.getDivision().getEnters()));
				this.addNode(divisionType, "normal-notes",Integer.toString(duration.getDivision().getTimes()));
			}
		}
	}

	private Node addAttribute(Node node, String name, String value){
		Attr attribute = this.document.createAttribute(name);
		attribute.setNodeValue(value);
		node.getAttributes().setNamedItem(attribute);
		return node;
	}

	private Node addNode(Node parent, String name){
		Node node = this.document.createElement(name);
		parent.appendChild(node);
		return node;
	}

	private Node addNode(Node parent, String name, String content){
		Node node = this.addNode(parent, name);
		node.setTextContent(content);
		return node;
	}

	private void removeNode(Node nodeToRemove){
		nodeToRemove.getParentNode().removeChild(nodeToRemove);
	}

	private void removeNodeIfNoChildren(Node nodeToRemove){
		if (nodeToRemove.getChildNodes().getLength() != 0){
			return;
		}

		this.removeNode(nodeToRemove);
	}

	private Document newDocument(){
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			return document;
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return null;
	}

	private void saveDocument(){
		try{
			TransformerFactory xformFactory = TransformerFactory.newInstance();
			Transformer idTransform = xformFactory.newTransformer();
			Source input = new DOMSource(this.document);
			Result output = new StreamResult(this.stream);
			idTransform.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMImplementation domImpl = this.document.getImplementation();
			DocumentType docType = domImpl.createDocumentType("doctype",
					"-//Recordare//DTD MusicXML 4.0 Partwise//EN",
					"http://www.musicxml.org/dtds/partwise.dtd");
			idTransform.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, docType.getPublicId());
			idTransform.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, docType.getSystemId());
			idTransform.transform(input, output);
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
	}


}
