package org.herac.tuxguitar.io.tg;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongReader;
import org.herac.tuxguitar.io.base.TGSongReaderHandle;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGChannelParameter;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGLyric;
import org.herac.tuxguitar.song.models.TGMarker;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGNoteEffect;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGText;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVelocities;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.song.models.effects.TGEffectBend;
import org.herac.tuxguitar.song.models.effects.TGEffectGrace;
import org.herac.tuxguitar.song.models.effects.TGEffectHarmonic;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloBar;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloPicking;
import org.herac.tuxguitar.song.models.effects.TGEffectTrill;
import org.herac.tuxguitar.util.TGMessagesManager;
import org.herac.tuxguitar.util.TGVersion;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class TGSongReaderImpl extends TGStream implements TGSongReader {

	private TGFactory factory;

	@Override
	public TGFileFormat getFileFormat() {
		return TG_FORMAT;
	}

	@Override
	public void read(TGSongReaderHandle handle) throws TGFileFormatException {
		try {
			InputStream[] versionAndContent = this.getDecompressedVersionAndContent(handle.getInputStream());
			TGVersion version = this.getFileFormatVersion(versionAndContent[0]);
			if (version.getMajor() < FILE_FORMAT_TGVERSION.getMajor()) {
				throw new TGFileFormatException();
			} else if (version.getMajor() > FILE_FORMAT_TGVERSION.getMajor()) {
				throw new TGFileFormatException(TGMessagesManager.getProperty("error.new-major-version"));
			}
			if (version.getMinor() > FILE_FORMAT_TGVERSION.getMinor()) {
				handle.setNewerFileFormatDetected(true);
			}
			this.readContent(handle, versionAndContent[1]);
		} catch (TGFileFormatException | IOException e) {
			throw new TGFileFormatException(e);
		}
	}
	
	public void readContent(TGSongReaderHandle handle, InputStream inputStream) throws TGFileFormatException {
		try {
			this.factory = handle.getFactory();
			TGSong song = this.factory.newSong();
			Document xmlDocument = this.getDocument(inputStream);
			Node root = getChildNode(xmlDocument, TAG_TGFile);
			Node nodeSong = getChildNode(root, TAG_TGSONG);
			this.readSong(song, nodeSong);
			inputStream.close();
			handle.setSong(song);
		} catch (Throwable throwable) {
			throw new TGFileFormatException(throwable);
		}
	}


	private void readSong(TGSong song, Node nodeSong) {
		Node nodeElement = nodeSong.getFirstChild();
		song.setName(readSibling(nodeElement, TAG_NAME));
		song.setArtist(readSibling(nodeElement, TAG_ARTIST));
		song.setAlbum(readSibling(nodeElement, TAG_ALBUM));
		song.setAuthor(readSibling(nodeElement, TAG_AUTHOR));
		song.setDate(readSibling(nodeElement, TAG_DATE));
		song.setCopyright(readSibling(nodeElement, TAG_COPYRIGHT));
		song.setWriter(readSibling(nodeElement, TAG_WRITER));
		song.setTranscriber(readSibling(nodeElement, TAG_TRANSCRIBER));
		song.setComments(readSibling(nodeElement, TAG_COMMENTS));
		this.readChannels(song, nodeSong);
		this.readMeasureHeaders(song, nodeSong);
		this.readTracks(song, nodeSong);
	}
	
	private void readChannels(TGSong song, Node nodeSong) {
		Node nodeChannel = getChildNode(nodeSong, TAG_CHANNEL);
		while (nodeChannel != null) {
			Node nodeElement = nodeChannel.getFirstChild();
			TGChannel channel = this.factory.newChannel();
			channel.setChannelId(readSiblingInt(nodeElement, TAG_ID));
			channel.setBank(readSiblingShort(nodeElement, TAG_BANK));
			channel.setProgram(readSiblingShort(nodeElement, TAG_PROGRAM));
			channel.setVolume(readSiblingShort(nodeElement, TAG_VOLUME));
			channel.setBalance(readSiblingShort(nodeElement, TAG_BALANCE));
			channel.setChorus(readSiblingShort(nodeElement, TAG_CHORUS));
			channel.setReverb(readSiblingShort(nodeElement, TAG_REVERB));
			channel.setPhaser(readSiblingShort(nodeElement, TAG_PHASER));
			channel.setTremolo(readSiblingShort(nodeElement, TAG_TREMOLO));
			channel.setName(readSibling(nodeElement, TAG_NAME));
			this.readChannelParameters(nodeChannel, channel);
			song.addChannel(channel);
			nodeChannel = getSiblingNode(nodeChannel.getNextSibling(), TAG_CHANNEL);
		}
	}
	
	private void readChannelParameters(Node nodeChannel, TGChannel channel) {
		Node nodeParameter = getChildNode(nodeChannel, TAG_CHANNEL_PARAMETER);
		while (nodeParameter != null) {
			TGChannelParameter parameter = this.factory.newChannelParameter();
			parameter.setKey(readAttribute(nodeParameter, TAG_KEY));
			parameter.setValue(readAttribute(nodeParameter,TAG_VALUE));
			channel.addParameter(parameter);
			nodeParameter = getSiblingNode(nodeParameter.getNextSibling(), TAG_CHANNEL_PARAMETER);
		}
	}
	
	private void readMeasureHeaders(TGSong song, Node nodeSong) {
		Node nodeMeasureHeader = getChildNode(nodeSong, TAG_MEASURE_HEADER);
		int timeSignatureNumerator = 4;
		int timeSignatureDenominator = 4;
		int tempoValue = 120;
		int tempoBase = TGDuration.QUARTER;
		boolean tempoDotted = false;
		int number = 1;
		long headerStart = TGDuration.QUARTER_TIME;
		while (nodeMeasureHeader != null) {
			TGMeasureHeader header = this.factory.newHeader();
			header.setNumber(number);
			header.setStart(headerStart);
			
			// timeSignature
			Node node = getChildNode(nodeMeasureHeader, TAG_TIME_SIGNATURE);
			if (node != null) {
				timeSignatureNumerator =readAttributeInt(node, TAG_NUMERATOR); 
				timeSignatureDenominator = readAttributeInt(node, TAG_DENOMINATOR);
			}
			header.getTimeSignature().setNumerator(timeSignatureNumerator);
			header.getTimeSignature().getDenominator().setValue(timeSignatureDenominator);
			
			// tempo
			node = getChildNode(nodeMeasureHeader, TAG_TEMPO);
			if (node != null) {
				tempoValue = readInt(node);
				Node nodeBase = node.getAttributes().getNamedItem(TAG_TEMPO_BASE);
				if (nodeBase != null) {
					tempoBase = this.readInt(nodeBase);
				}
				Node nodeDotted = node.getAttributes().getNamedItem(TAG_TEMPO_DOTTED);
				if (nodeDotted != null) {
					tempoDotted = nodeDotted.getTextContent().equals("true");
				}
			}
			header.getTempo().setValueBase(tempoValue, tempoBase, tempoDotted);
			
			// repeats
			header.setRepeatOpen(getChildNode(nodeMeasureHeader, TAG_REPEAT_OPEN) != null);
			node = getChildNode(nodeMeasureHeader, TAG_REPEAT_CLOSE);
			if (node != null) {
				header.setRepeatClose(readInt(node));
			}
			byte repeatAlternative = 0;
			node = getChildNode(nodeMeasureHeader, TAG_REPEAT_ALTERNATIVE);
			if (node!=null) {
				node = getChildNode(node, TAG_ALTERNATIVE);
				while (node != null) {
					repeatAlternative += Math.pow(2, readInt(node)-1);
					node = getSiblingNode(node.getNextSibling(), TAG_ALTERNATIVE);
				}
				header.setRepeatAlternative(repeatAlternative);
			}
			
			// marker
			node = getChildNode(nodeMeasureHeader, TAG_MARKER);
			if (node != null) {
				TGMarker marker = this.factory.newMarker();
				marker.setMeasure(number);
				marker.setTitle(node.getTextContent());
				marker.getColor().setR(readAttributeInt(node, TAG_COLOR_R));
				marker.getColor().setG(readAttributeInt(node, TAG_COLOR_G));
				marker.getColor().setB(readAttributeInt(node, TAG_COLOR_B));
				header.setMarker(marker);
			}
			
			// tripletFeel
			node = getChildNode(nodeMeasureHeader, TAG_TRIPLET_FEEL);
			if (node != null) {
				header.setTripletFeel(this.tripletsReadMap.get(node.getTextContent()));
			}
			// lineBreak
			if (getChildNode(nodeMeasureHeader, TAG_LINE_BREAK) != null) {
				header.setLineBreak(true);
			}
			// next measureHeader
			song.addMeasureHeader(header);
			headerStart += header.getLength();
			number++;
			nodeMeasureHeader = getSiblingNode(nodeMeasureHeader.getNextSibling(), TAG_MEASURE_HEADER);
		}
		
	}
	
	private void readTracks(TGSong song, Node nodeSong) {
		Node nodeTrack = getChildNode(nodeSong, TAG_TGTRACK);
		int number=1;
		while (nodeTrack != null) {
			TGTrack track = this.factory.newTrack();
			track.setNumber(number);
			track.setSong(song);
			Node nodeMaxFret = nodeTrack.getAttributes().getNamedItem(TAG_MAXFRET);
			if (nodeMaxFret!=null) {
				track.setMaxFret(readInt(nodeMaxFret));
			}
			Node nodeElement = getChildNode(nodeTrack, TAG_NAME);
			track.setName(nodeElement.getTextContent());
			Node node = getSiblingNode(nodeElement, TAG_SOLOMUTE);
			if (node != null) {
				track.setSolo(VAL_SOLO.equals(node.getTextContent()));
				track.setMute(VAL_MUTE.equals(node.getTextContent()));
			}
			track.setChannelId(readSiblingInt(nodeElement, TAG_CHANNELID));
			node = getSiblingNode(nodeElement, TAG_OFFSET);
			if (node != null) {
				track.setOffset(readInt(node));
			}
			node = getSiblingNode(nodeElement, TAG_COLOR);
			track.getColor().setR(readAttributeInt(node, TAG_COLOR_R));
			track.getColor().setG(readAttributeInt(node, TAG_COLOR_G));
			track.getColor().setB(readAttributeInt(node, TAG_COLOR_B));
			this.readStrings(track, getChildNode(nodeTrack, TAG_TGSTRING));
			this.readLyrics(track, getChildNode(nodeTrack, TAG_TGLYRIC));
			this.readMeasures(track, nodeTrack);
			song.addTrack(track);
			number++;
			nodeTrack = getSiblingNode(nodeTrack.getNextSibling(), TAG_TGTRACK);
		}
	}
	
	private void readLyrics(TGTrack track, Node nodeLyrics) {
		TGLyric lyrics = this.factory.newLyric();
		if (nodeLyrics != null) {
			lyrics.setLyrics(nodeLyrics.getTextContent());
			lyrics.setFrom(readAttributeInt(nodeLyrics, TAG_FROM));
			track.setLyrics(lyrics);
		}
	}
	
	private void readStrings(TGTrack track, Node nodeString) {
		List<TGString> list = new ArrayList<TGString>();
		int number=1;
		while (nodeString != null) {
			TGString string = this.factory.newString();
			string.setNumber(number);
			string.setValue(readInt(nodeString));
			list.add(string);
			number++;
			nodeString = getSiblingNode(nodeString.getNextSibling(), TAG_TGSTRING);
		}
		track.setStrings(list);
	}
	private void readMeasures(TGTrack track, Node nodeTrack) {
		boolean isFirstMeasure = true;
		Node nodeMeasure = getChildNode(nodeTrack, TAG_TGMEASURE);
		int index = 1;
		int clef = TGMeasure.CLEF_TREBLE;	// default
		int keySignature = 0;	// default
		while (nodeMeasure != null) {
			TGMeasure measure = this.factory.newMeasure(track.getSong().getMeasureHeader(index-1));
			// clef
			Node node = getChildNode(nodeMeasure, TAG_CLEF);
			if (node != null) {
				clef = this.mapReadClefs.get(node.getTextContent());
			}
			measure.setClef(clef);
			// keySignature
			node = getChildNode(nodeMeasure, TAG_KEYSIGNATURE);
			if (node != null) {
				keySignature = readInt(node);
			}
			measure.setKeySignature(keySignature);
			this.readBeats(measure, nodeMeasure, isFirstMeasure);
			isFirstMeasure = false;
			track.addMeasure(measure);
			index++;
			nodeMeasure = getSiblingNode(nodeMeasure.getNextSibling(), TAG_TGMEASURE);
		}
		if ((index-1) != track.getSong().countMeasureHeaders()) {
			System.out.printf("%d != %d\n", index, track.getSong().countMeasureHeaders());
			throw new TGFileFormatException("unexpected number of measures");
		}
	}
	
	private void readBeats(TGMeasure measure, Node nodeMeasure, boolean isFirst) {
		boolean isFirstBeat = isFirst;
		Node nodeBeat = getChildNode(nodeMeasure, TAG_TGBEAT);
		while (nodeBeat != null) {
			TGBeat beat = this.factory.newBeat();
			long preciseStart = readLong(getChildNode(nodeBeat, TAG_PRECISE_START));
			// force explicit incompatibility with intermediate development snapshots
			if (isFirstBeat && (preciseStart != TGDuration.getPreciseStartingPoint())) {
				throw new TGFileFormatException();
			}
			isFirstBeat = false;
			beat.setPreciseStart(preciseStart);
			// stroke
			Node node = getChildNode(nodeBeat, TAG_STROKE);
			if (node != null) {
				beat.getStroke().setDirection(this.mapReadStroke.get(readAttribute(node, TAG_DIRECTION)));
				beat.getStroke().setValue(readAttributeInt(node, TAG_VALUE));
			}
			// pickStroke
			node = getChildNode(nodeBeat, TAG_PICK_STROKE);
			if (node != null) {
				beat.getPickStroke().setDirection(this.mapReadPickStroke.get(node.getTextContent()));
			}
			// chord
			node = getChildNode(nodeBeat, TAG_CHORD);
			if (node != null) {
				beat.setChord(this.readChord(node));
			}
			node = getChildNode(nodeBeat, TAG_TEXT);
			if (node != null) {
				TGText text = this.factory.newText();
				text.setValue(node.getTextContent());
				beat.setText(text);
			}
			measure.addBeat(beat);
			Node nodeVoice = getChildNode(nodeBeat, TAG_VOICE);
			this.readVoices(beat, nodeVoice);
			nodeBeat = getSiblingNode(nodeBeat.getNextSibling(), TAG_TGBEAT);
		}
		
	}
	
	private TGChord readChord(Node nodeChord) {
		List<Integer> fretValues = new ArrayList<Integer>();
		Node nodeString = getChildNode(nodeChord, TAG_STRING);
		while (nodeString != null) {
			fretValues.add("".equals(nodeString.getTextContent()) ? -1 : readInt(nodeString));
			nodeString = getSiblingNode(nodeString.getNextSibling(), TAG_STRING);
		}
		TGChord chord = this.factory.newChord(fretValues.size());
		chord.setName(getChildNode(nodeChord, TAG_NAME).getTextContent());
		chord.setFirstFret(readInt(getChildNode(nodeChord, TAG_FIRSTFRET)));
		for (int i=0; i<fretValues.size(); i++) {
			chord.addFretValue(i, fretValues.get(i));
		}
		return chord;
	}
	
	private void readVoices(TGBeat beat, Node nodeVoice) {
		int index = 0;
		while (nodeVoice != null) {
			TGVoice voice = beat.getVoice(index);
			Node nodeDirection = nodeVoice.getAttributes().getNamedItem(TAG_DIRECTION);
			if (nodeDirection != null) {
				voice.setDirection(this.mapReadDirection.get(nodeDirection.getTextContent()));
			}
			Node nodeDuration = getChildNode(nodeVoice, TAG_DURATION);
			voice.getDuration().setValue(readAttributeInt(nodeDuration, TAG_VALUE));
			Node nodeDotted = nodeDuration.getAttributes().getNamedItem(TAG_DOTTED);
			if (nodeDotted != null) {
				voice.getDuration().setDotted(VAL_DOTTED.equals(nodeDotted.getTextContent()));
				voice.getDuration().setDoubleDotted(VAL_DOUBLEDOTTED.equals(nodeDotted.getTextContent()));
			}
			Node nodeDivisionType = getChildNode(nodeDuration, TAG_DIVISIONTYPE);
			if (nodeDivisionType != null) {
				voice.getDuration().getDivision().setEnters(readAttributeInt(nodeDivisionType, TAG_ENTERS));
				voice.getDuration().getDivision().setTimes(readAttributeInt(nodeDivisionType, TAG_TIMES));
			}
			this.readNotes(voice, nodeVoice);
			voice.setEmpty(voice.getNotes().size() == 0);
			Node nodeEmpty = nodeVoice.getAttributes().getNamedItem(TAG_EMPTY);
			if (nodeEmpty != null) {
				voice.setEmpty("true".equals(nodeEmpty.getNodeValue()));
			}
			beat.setVoice(index, voice);
			index++;
			nodeVoice = getSiblingNode(nodeVoice.getNextSibling(), TAG_VOICE);
		}
	}
	
	private void readNotes(TGVoice voice, Node nodeVoice) {
		int velocity = TGVelocities.DEFAULT;
		Node nodeNote = getChildNode(nodeVoice, TAG_NOTE);
		while (nodeNote != null) {
			TGNote note = this.factory.newNote();
			note.setVoice(voice);
			note.setValue(readAttributeInt(nodeNote, TAG_VALUE));
			Node node = nodeNote.getAttributes().getNamedItem(TAG_VELOCITY);
			if (node != null) {
				velocity = readInt(node);
			}
			note.setVelocity(velocity);
			note.setString(readAttributeInt(nodeNote, TAG_STRING));
			node = nodeNote.getAttributes().getNamedItem(TAG_TIEDNOTE);
			if (node != null) {
				note.setTiedNote("true".equals(node.getTextContent()));
			}
			TGNoteEffect effect = note.getEffect();
			effect.setVibrato(this.hasChild(nodeNote, TAG_VIBRATO));
			effect.setDeadNote(this.hasChild(nodeNote, TAG_DEADNOTE));
			effect.setSlide(this.hasChild(nodeNote, TAG_SLIDE));
			effect.setHammer(this.hasChild(nodeNote, TAG_HAMMER));
			effect.setGhostNote(this.hasChild(nodeNote, TAG_GHOSTNOTE));
			effect.setAccentuatedNote(this.hasChild(nodeNote, TAG_ACCENTUATEDNOTE));
			effect.setHeavyAccentuatedNote(this.hasChild(nodeNote, TAG_HEAVYACCENTUATEDNOTE));
			effect.setPalmMute(this.hasChild(nodeNote, TAG_PALMMUTE));
			effect.setStaccato(this.hasChild(nodeNote, TAG_STACCATO));
			effect.setTapping(this.hasChild(nodeNote, TAG_TAPPING));
			effect.setSlapping(this.hasChild(nodeNote, TAG_SLAPPING));
			effect.setPopping(this.hasChild(nodeNote, TAG_POPPING));
			effect.setFadeIn(this.hasChild(nodeNote, TAG_FADEIN));
			effect.setLetRing(this.hasChild(nodeNote, TAG_LETRING));
			node = getChildNode(nodeNote, TAG_BEND);
			if (node != null) {
				this.readBend(note, node);
			}
			node = getChildNode(nodeNote, TAG_TREMOLOBAR);
			if (node != null) {
				this.readTremoloBar(note, node);
			}
			node = getChildNode(nodeNote, TAG_HARMONIC);
			if (node != null) {
				this.readHarmonic(note, node);
			}
			node = getChildNode(nodeNote, TAG_GRACE);
			if (node != null) {
				this.readGrace(note, node);
			}
			node = getChildNode(nodeNote, TAG_TRILL);
			if (node != null) {
				this.readTrill(note, node);
			}
			node = getChildNode(nodeNote, TAG_TREMOLOPICKING);
			if (node != null) {
				this.readTremoloPicking(note, node);
			}
			if (this.hasChild(nodeNote, TAG_ALT_ENHARMONIC)) {
				note.toggleAltEnharmonic();
			}
			voice.addNote(note);
			nodeNote = getSiblingNode(nodeNote.getNextSibling(), TAG_NOTE);
		}
	}
	
	private void readBend(TGNote note, Node nodeBend) {
		TGEffectBend bend = this.factory.newEffectBend();
		for (PositionValue point : readPositionValueList(nodeBend)) {
			bend.addPoint(point.getPosition(), point.getValue());
		}
		note.getEffect().setBend(bend);
	}
	
	private void readTremoloBar(TGNote note, Node nodeTremoloBar) {
		TGEffectTremoloBar tremoloBar = this.factory.newEffectTremoloBar();
		for (PositionValue point : readPositionValueList(nodeTremoloBar)) {
			tremoloBar.addPoint(point.getPosition(), point.getValue());
		}
		note.getEffect().setTremoloBar(tremoloBar);
	}
	
	private void readHarmonic(TGNote note, Node nodeHarmonic) {
		TGEffectHarmonic harmonic = this.factory.newEffectHarmonic();
		harmonic.setType(harmonicReadMap.get(readAttribute(nodeHarmonic, TAG_TYPE)));
		harmonic.setData(readAttributeInt(nodeHarmonic, TAG_DATA));
		note.getEffect().setHarmonic(harmonic);
	}
	
	private void readGrace(TGNote note, Node nodeGrace) {
		TGEffectGrace grace = this.factory.newEffectGrace();
		grace.setFret(readAttributeInt(nodeGrace, TAG_FRET));
		grace.setDuration(this.mapReadGraceDuration.get(readAttributeInt(nodeGrace, TAG_DURATION)));
		grace.setDynamic(readAttributeInt(nodeGrace, TAG_DYNAMIC));
		grace.setTransition(this.mapReadTransition.get(readAttribute(nodeGrace, TAG_TRANSITION)));
		grace.setOnBeat("true".equals(readAttribute(nodeGrace, TAG_ONBEAT)));
		grace.setDead("true".equals(readAttribute(nodeGrace, TAG_DEAD)));
		note.getEffect().setGrace(grace);
	}
	
	private void readTrill(TGNote note, Node nodeTrill) {
		TGEffectTrill trill = this.factory.newEffectTrill();
		trill.setFret(readAttributeInt(nodeTrill, TAG_FRET));
		TGDuration duration = this.factory.newDuration();
		duration.setValue(readAttributeInt(nodeTrill, TAG_DURATION));
		trill.setDuration(duration);
		note.getEffect().setTrill(trill);
	}
	
	private void readTremoloPicking(TGNote note, Node nodeTremoloPicking) {
		TGEffectTremoloPicking picking = this.factory.newEffectTremoloPicking();
		TGDuration duration = this.factory.newDuration();
		duration.setValue(readAttributeInt(nodeTremoloPicking, TAG_DURATION));
		picking.setDuration(duration);
		note.getEffect().setTremoloPicking(picking);
	}
	
	private List<PositionValue> readPositionValueList(Node nodeList) {
		List<PositionValue> list = new ArrayList<PositionValue>();
		Node nodePoint = getChildNode(nodeList, TAG_POINT);
		while (nodePoint != null) {
			list.add(new PositionValue(
					readAttributeInt(nodePoint, TAG_POSITION),
					readAttributeInt(nodePoint, TAG_VALUE)));
			nodePoint = getSiblingNode(nodePoint.getNextSibling(), TAG_POINT);
		}
		return list;
	}
	
}
