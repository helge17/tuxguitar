package org.herac.tuxguitar.io.tef3;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import org.herac.tuxguitar.io.tef3.base.TEAnchorPosition;
import org.herac.tuxguitar.io.tef3.base.TEChordDefinition;
import org.herac.tuxguitar.io.tef3.base.TEChordDefinition.TEChordFretSymbol;
import org.herac.tuxguitar.io.tef3.base.TEComponentAccent;
import org.herac.tuxguitar.io.tef3.base.TEComponentBeamBreak;
import org.herac.tuxguitar.io.tef3.base.TEComponentChord;
import org.herac.tuxguitar.io.tef3.base.TEComponentConnection;
import org.herac.tuxguitar.io.tef3.base.TEComponentCrescendo;
import org.herac.tuxguitar.io.tef3.base.TEComponentDrumChange;
import org.herac.tuxguitar.io.tef3.base.TEComponentEnding;
import org.herac.tuxguitar.io.tef3.base.TEComponentEnding.TEComponentEndingFlag;
import org.herac.tuxguitar.io.tef3.base.TEComponentGraceNoteMetadata;
import org.herac.tuxguitar.io.tef3.base.TEComponentGraceNoteMetadata.TEComponentGraceNoteMetadataDuration;
import org.herac.tuxguitar.io.tef3.base.TEComponentLineBreak;
import org.herac.tuxguitar.io.tef3.base.TEComponentNote;
import org.herac.tuxguitar.io.tef3.base.TEComponentNote.TEComponentNoteAlterations;
import org.herac.tuxguitar.io.tef3.base.TEComponentNote.TEComponentNoteAttributes;
import org.herac.tuxguitar.io.tef3.base.TEComponentNote.TEComponentNoteDynamics;
import org.herac.tuxguitar.io.tef3.base.TEComponentNote.TEComponentNoteEffect1;
import org.herac.tuxguitar.io.tef3.base.TEComponentNote.TEComponentNoteEffect2;
import org.herac.tuxguitar.io.tef3.base.TEComponentNote.TEComponentNoteEffect3;
import org.herac.tuxguitar.io.tef3.base.TEComponentNote.TEComponentNoteFingering;
import org.herac.tuxguitar.io.tef3.base.TEComponentNote.TEComponentNoteGraceNoteEffect;
import org.herac.tuxguitar.io.tef3.base.TEComponentNote.TEComponentNotePitchShift;
import org.herac.tuxguitar.io.tef3.base.TEComponentNote.TEComponentNoteStroke;
import org.herac.tuxguitar.io.tef3.base.TEComponentRest;
import org.herac.tuxguitar.io.tef3.base.TEComponentScaleDiagram;
import org.herac.tuxguitar.io.tef3.base.TEComponentSpacingMarker;
import org.herac.tuxguitar.io.tef3.base.TEComponentStemLength;
import org.herac.tuxguitar.io.tef3.base.TEComponentSymbol;
import org.herac.tuxguitar.io.tef3.base.TEComponentSyncopation;
import org.herac.tuxguitar.io.tef3.base.TEComponentTempoChange;
import org.herac.tuxguitar.io.tef3.base.TEComponentTextEvent;
import org.herac.tuxguitar.io.tef3.base.TEComponentTextEvent.TEComponentTextEventBorderType;
import org.herac.tuxguitar.io.tef3.base.TEComponentVoiceChange;
import org.herac.tuxguitar.io.tef3.base.TEFileMetadata;
import org.herac.tuxguitar.io.tef3.base.TEFileMetadata.TEFileMetadataSyncopation;
import org.herac.tuxguitar.io.tef3.base.TEFontPreset;
import org.herac.tuxguitar.io.tef3.base.TELyrics;
import org.herac.tuxguitar.io.tef3.base.TEMeasure;
import org.herac.tuxguitar.io.tef3.base.TENoteDuration;
import org.herac.tuxguitar.io.tef3.base.TEPosition;
import org.herac.tuxguitar.io.tef3.base.TEPrintMetadata;
import org.herac.tuxguitar.io.tef3.base.TEReadingListEntry;
import org.herac.tuxguitar.io.tef3.base.TESong;
import org.herac.tuxguitar.io.tef3.base.TESongMetadata;
import org.herac.tuxguitar.io.tef3.base.TETrack;
import org.herac.tuxguitar.io.tef3.base.TETrack.TETrackClef;
import org.herac.tuxguitar.io.tef3.base.TETrack.TETrackMiddleCoffset;
import org.herac.tuxguitar.io.tef3.base.TETrack.TETrackTransposition;
import org.herac.tuxguitar.io.tef3.base.TETimeSignature;
import org.herac.tuxguitar.util.TGException;

public class TEInputStream {
	
	private TESong song;
	private InputStream stream;

	private static final int SIZE_OF_FOOTER = 4;
	
	public TEInputStream(InputStream stream) {
		this.stream = stream;
	}

	public TESong readSong() throws TGException {
		this.song = new TESong();

		this.readFileHeader();

		this.readSongMetadata();

		if (this.song.getFileMetadata().getHasChords()) {
			this.readChordDefinitions();
		}
		
		int sizeOfMeasure = this.readShort() - 4;
		int measureCount = this.readShort();
		this.skip(4); // 0x00 0x00 0x00 0x00
		this.readMeasures(measureCount, sizeOfMeasure);

		this.readTracks();

		this.readPrintMetadata();

		if (this.song.getFileMetadata().getHasReadingList()) {
			this.readReadingList();
		}

		byte[] remainingBytes = this.readComponents();

		int footer = (remainingBytes[0] << 24) | (remainingBytes[1] << 16) | (remainingBytes[2] << 8) | (remainingBytes[3] & 0xFF);

		if (footer != -1)
		{
			throw new TGException(String.format("Unexpected footer! Expected: 0x%X. Received: 0x%X", -1, footer));
		}

		this.close();
		
		return this.song;
	}

	private void readFileHeader() {
		this.skip(2); // 0x00, 0x00

		int minorVersion = this.readByte();
		int majorVersion = this.readByte();
		int revision = this.readByte();
		this.skip(1); // 0x00 | Could be patch version?

		int initialBpm = this.readShort();

		int toneChorus = this.readShort();
		int toneReverb = this.readShort();
		TEFileMetadataSyncopation syncopation = TEFileMetadataSyncopation.getEnumFromInt((short) this.readShort());

		this.skip(18);

		BitSet noteFlags = BitSet.valueOf(new byte[] {(byte)this.readByte()});
		boolean graceNotesShowMusicalSymbols = noteFlags.get(3);

		this.skip(27);

		int fileSize = this.readInt() + 4; // This could also be a short, but assuming it's an int because of it is a file size.
		
		// Positions
		int posTitle = this.readInt();
		int posAuthor = this.readInt();
		int posOfComments = this.readInt();
		int posOfNotes = this.readInt();
		int posOfLyrics = this.readInt();
		int posOfTextEvents = this.readInt();
		int posOfChords = this.readInt();
		int posOfMeasures = this.readInt();
		int posOfTracks = this.readInt();
		int posOfPrintMetadata = this.readInt();

		this.skip(24);
		
		int posOfReadingList = this.readInt();
		int posOfUrl = this.readInt();

		this.skip(4); // 0x00 0x00 0x00 0x00

		int posOfCopyright = this.readInt();

		this.skip(112);
		
		TEFileMetadata fileMetadata = new TEFileMetadata(majorVersion, minorVersion, revision, initialBpm, toneChorus, toneReverb,
		syncopation, graceNotesShowMusicalSymbols, posOfTextEvents != 0, posOfChords != 0, posOfCopyright != 0, posOfReadingList != 0, posOfUrl != 0);

		this.song.setFileMetadata(fileMetadata);
	}

	private void readSongMetadata() {
		String songTitle = this.readShortString();
		String authorName = this.readShortString();
		String comments = this.readShortString();
		String notes = this.readShortString();

		String url = "";
		if (this.song.getFileMetadata().getHasUrl())
		{
			url = this.readShortString();
		}

		String copyright = "";
		if (this.song.getFileMetadata().getHasCopyright())
		{
			copyright = this.readShortString();
		}
		
		String lyricFullString = this.readShortString();
		List<TELyrics> lyrics = parseLyricString(lyricFullString);

		List<String> textEvents = new ArrayList<>();
		
		if (this.song.getFileMetadata().getHasTextEvents())
		{
			int totalTextEvents = this.readShort();

			for (int i = 0; i < totalTextEvents; i++)
			{
				textEvents.add(readShortString());
			}
		}

		TESongMetadata songMetadata = new TESongMetadata(songTitle, authorName, comments, notes, url, copyright, lyrics, textEvents);
		this.song.setSongMetadata(songMetadata);
	}

	private List<TELyrics> parseLyricString(String fullLyricString) {
		List<TELyrics> lyrics = new ArrayList<>();

		String[] tracks = fullLyricString.split(">");

		for (String track : tracks)
		{
			int trackNumber = 0;
			int fontPresetInt = 0;
			int yPosition = 0;
			int unkOne = 0;
			int unkTwo = 0;

			String[] trackLines = track.split("\r\n");
			StringBuilder stringBuilder = new StringBuilder();

			for (int line = 0; line < trackLines.length; line++) {
				String trackLine = trackLines[line];

				if (line == 0) {
					String[] trackMetadataParts = trackLine.split(" ");

					int linePartType = 0;
					for (String trackPart : trackMetadataParts)
					{
						if (trackPart.length() == 0)
						{
							continue;
						}
		
						switch(linePartType) {
							case 0:
								trackNumber = trackPart.charAt(0) - 'A'; // A = track 0. B = track 1. etc.
								linePartType++;
								break;
							case 1:
								fontPresetInt = Integer.parseInt(trackPart.substring(1));
								linePartType++;
								break;
							case 2:
								boolean isNegative = trackPart.charAt(0) == '-';
								yPosition = Integer.parseInt(trackPart.substring(1));

								if (isNegative) {
									yPosition *= -1;
								}

								linePartType++;
								break;
							case 3:
								unkOne = Integer.parseInt(trackPart.substring(1));
								linePartType++;
								break;
							case 4: 
								unkTwo = Integer.parseInt(trackPart.substring(1));
								linePartType++;
								break;
							default:
								break;
						}
		
						if (linePartType > 4) {
							break;
						}
					}
					} else {
						stringBuilder.append(trackLine);
						stringBuilder.append("\r\n");
					}
			}

			stringBuilder.setLength(Math.max(stringBuilder.length() - 2, 0)); // Remove trailing \r\n 
			
			TEFontPreset fontPreset = TEFontPreset.getEnumFromInt(fontPresetInt);

			TELyrics lyric = new TELyrics(trackNumber, fontPreset, yPosition, stringBuilder.toString());
			lyrics.add(lyric);
		}

		return lyrics;
	}

	private void readChordDefinitions() {
		int chordStructSize = this.readShort();
		int totalChords = this.readShort();

		List<TEChordDefinition> chordDefinitions = new ArrayList<>();

		for (int chordIndex = 0; chordIndex < totalChords; chordIndex++)
		{
			byte[] chordBytes = new byte[chordStructSize];
			this.readBytes(chordBytes);
			ByteArrayInputStream chordStream = new ByteArrayInputStream(chordBytes);

			int numOfStringsInChord = 7;
			int[] chordFrets = new int[numOfStringsInChord];
			TEChordFretSymbol[] chordSymbols = new TEChordFretSymbol[numOfStringsInChord];

			for (int stringNum = 0; stringNum < numOfStringsInChord; stringNum++)
			{
				BitSet fretBitset = BitSet.valueOf(new byte[] {(byte)this.readByte(chordStream)});
				int fret = this.bitsetToInt(fretBitset.get(0, 5));
				TEChordFretSymbol symbol = TEChordFretSymbol.getEnumFromInt(this.bitsetToInt(fretBitset.get(5, 8)));

				chordFrets[stringNum] = fret;
				chordSymbols[stringNum] = symbol;
			}

			this.skip(chordStream, numOfStringsInChord); // 0xFF 0xFF 0xFF 0xFF 0xFF 0xFF 0xFF. Could be placeholder, for later use?

			int chordNameMaxLength = 17;
			String chordName = this.readNullTerminatedString(chordStream, chordNameMaxLength - 1);

			this.skip(chordStream, chordNameMaxLength - chordName.length() - 1);
			int anchorFret = this.readByte(chordStream);

			this.skip(chordStream, 4); // 0x00 0x00 0x00 0x00. Could be padding from struct size.

			this.close(chordStream);

			TEChordDefinition chordDefinition = new TEChordDefinition(chordFrets, chordSymbols, chordName, anchorFret);
			chordDefinitions.add(chordDefinition);
		}

		this.song.setChordDefinitions(chordDefinitions);
	}

	private void readMeasures(int measureCount, int sizeOfMeasure) {
		List<TEMeasure> measures = new ArrayList<>();

		for (int i = 0; i < measureCount; i++)
		{
			byte[] measureBytes = new byte[sizeOfMeasure];
			this.readBytes(measureBytes);
			ByteArrayInputStream measureStream = new ByteArrayInputStream(measureBytes);

			TEMeasure measure = new TEMeasure();

			// Flags
			BitSet flagByte = BitSet.valueOf(new byte[] {(byte)this.readByte(measureStream)});
			measure.setDoNotPrintMetric(flagByte.get(0));
			measure.setFreeBarOne(flagByte.get(1));
			measure.setFreeBarTwo(flagByte.get(2));
			measure.setPickupMeasure(flagByte.get(3));
			measure.setAdlibMeasure(flagByte.get(4));
			measure.setMinorKey(flagByte.get(5));
			measure.setDottedBarLine(flagByte.get(6));
			measure.setHalfBarLine(flagByte.get(7));

			this.skip(measureStream, 1); // 0x00
			measure.setKeySignature(this.readByte(measureStream)); // + is sharps, - is flats.
			this.skip(measureStream, 1); // 0x30

			// Time signature
			int tsDenominator = this.readByte(measureStream);
			int tsNumerator = this.readByte(measureStream);
			TETimeSignature timeSignature = new TETimeSignature(tsNumerator, tsDenominator);
			measure.setTimeSignature(timeSignature);

			measure.setLeftWidthPadding(this.readByte(measureStream));

			this.skip(measureStream, 1); // 0x00

			// There may be additional padding here that we skip as part of making a separate stream.

			this.close(measureStream);

			measures.add(measure);
		}

		this.song.setMeasures(measures);
	}

	private void readTracks() {
		int maxTrackSize = this.readShort();
		int trackCount = this.readShort();

		int totalStringsAllTracks = 0;

		List<TETrack> tracks = new ArrayList<>();

		for (int i = 0; i < trackCount; i++)
		{
			// Setup local input stream, to keep track of position.
			byte[] instrumentBytes = new byte[maxTrackSize];
			this.readBytes(instrumentBytes);
			ByteArrayInputStream instrumentStream = new ByteArrayInputStream(instrumentBytes);

			int stringCount = this.readByte(instrumentStream);
			totalStringsAllTracks += stringCount;

			this.skip(instrumentStream, 7); // 0x00 0x0C 0x00 0x00 0x00 0x00 0x00

			int midiInstrumentType = this.readByte(instrumentStream);

			this.skip(instrumentStream, 2); // 0x00 0x00

			TETrackTransposition transposition = TETrackTransposition.getEnumFromInt(this.readByte(instrumentStream));
			int capo = this.readByte(instrumentStream);

			this.skip(instrumentStream, 1); // 0x00

			TETrackMiddleCoffset middleCoffset = TETrackMiddleCoffset.getEnumFromInt(this.readByte(instrumentStream) - 12);

			// Flag Byte
			BitSet flagByte1 = BitSet.valueOf(new byte[] {(byte)this.readByte(instrumentStream)});
			TETrackClef clef = TETrackClef.getEnumFromInt(this.bitsetToInt(flagByte1.get(0, 3)));
			boolean grandStaff = flagByte1.get(3);
			boolean squareBracket = flagByte1.get(4);
			// Unknown last 3 bits.

			this.skip(instrumentStream, 1); // 0x30

			int pan = this.readByte(instrumentStream);
			int volume = this.readByte(instrumentStream);

			// Flag Byte
			BitSet flagByte2 = BitSet.valueOf(new byte[] {(byte)this.readByte(instrumentStream)});
			boolean doubleStrings = flagByte2.get(0);
			boolean letRing = flagByte2.get(1);
			boolean pedalSteelGuitar = flagByte2.get(2);
			boolean multipleAudioChannels = flagByte2.get(3);
			boolean rhythmTrack = flagByte2.get(4);
			// Unknown last 3 bits.

			byte[] tuning = new byte[stringCount];
			this.readBytes(instrumentStream, tuning);
			this.skip(instrumentStream, 12 - stringCount); // Padding for tuning.

			String instrumentName = this.readNullTerminatedString(instrumentStream, maxTrackSize);

			TETrack track = new TETrack(stringCount, midiInstrumentType, transposition, capo, middleCoffset, clef, grandStaff, squareBracket,
				pan, volume, doubleStrings, letRing, pedalSteelGuitar, multipleAudioChannels, rhythmTrack, tuning, instrumentName);

			tracks.add(track);

			// Another null-terminated string. Unknown usage. Looks like it may be some left-over text from another instrument.
			// Padding to hit maxTrackSize.
			// Both of which are accounted for by creating the specific instrument stream, that we can just skip through.

			this.close(instrumentStream);
		}

		this.song.setTotalStringCount(totalStringsAllTracks);
		this.song.setTracks(tracks);
	}

	private void readPrintMetadata() {
		int printDataLength = this.readByte();
		
		// Could also be a MSB for printDataLength. Both make sense (byte, or short).
		// Byte = Size of unkWithFooter.
		// Short = Size of full print metadata section.
		// Assuming it's a byte for now, but can change if it causes issues. We just lose the granularity of the page headers / footer
		this.skip(1); // 0x01

		String unkWithPageFooter = this.readNullTerminatedString(printDataLength); // Unknown. A bunch of numbers. Ends with page footer.
		this.skip(printDataLength - unkWithPageFooter.length() - 1); // Padding

		int maxHeaderSize = 128;

		String firstPageHeader = this.readNullTerminatedString(maxHeaderSize - 1); // -1 to account for NULL termination.
		this.skip(maxHeaderSize - firstPageHeader.length() - 1); // Padding

		String secondaryPageHeader = this.readNullTerminatedString(maxHeaderSize - 1); // -1 to account for NULL termination.
		this.skip(maxHeaderSize - secondaryPageHeader.length() - 1); // Padding

		TEPrintMetadata printMetadata = new TEPrintMetadata(unkWithPageFooter, firstPageHeader, secondaryPageHeader);
		this.song.setPrintMetadata(printMetadata);
	}

	private void readReadingList() {
		int sizeOfReadingListEntry = this.readShort();
		int totalReadingListEntries = this.readShort();

		List<TEReadingListEntry> readingListEntries = new ArrayList<>();

		for (int i = 0; i < totalReadingListEntries; i++) {
			byte[] readingListBytes = new byte[sizeOfReadingListEntry];
			this.readBytes(readingListBytes);
			ByteArrayInputStream readingListStream = new ByteArrayInputStream(readingListBytes);

			int startMeasure = this.readShort(readingListStream);
			int endMeasure = this.readShort(readingListStream);
			String name = this.readNullTerminatedString(readingListStream, sizeOfReadingListEntry - 5); // 2 for start, 2 for end, 1 for null termination.

			TEReadingListEntry readingListEntry = new TEReadingListEntry(startMeasure, endMeasure, name);
			readingListEntries.add(readingListEntry);
		}

		this.song.setReadingListEntries(readingListEntries);
	}

	private byte[] readComponents() throws TGException {
		byte[] componentBytes = new byte[12];

		this.song.setComponents(new ArrayList<>());

		int remainingBytes;
		while ((remainingBytes = this.readBytes(componentBytes)) == 12)
		{
			ByteArrayInputStream componentStream = new ByteArrayInputStream(componentBytes);

			int location = this.readInt(componentStream);

			TEPosition position = TEPosition.createPositionFromLocation(this.song, location);

			int componentTypeFull = this.readByte(componentStream);

			switch (componentTypeFull)
				{
					case 0x33:
						this.readRestComponent(componentStream, position);
						break;
					case 0x35:
						this.readChordComponent(componentStream, position);
						break;
					case 0x36:
						this.readLineBreakComponent(componentStream, position);
						break;
					case 0x37:
						this.readAccentComponent(componentStream, position);
						break;
					case 0x38:
						this.readCrescendoComponent(componentStream, position);
						break;
					case 0x39:
						this.readTextEventComponent(componentStream, position);
						break;
					case 0x3D:
						this.readConnectionComponent(componentStream, position);
						break;
					case 0x75:
						this.readScaleDiagramComponent(componentStream, position);
						break;
					case 0x78:
						this.readDrumChangeComponent(componentStream, position);
						break;
					case 0x7D:
					{
						// Spacing marker and grace note metadata share this component type.
						// Difference being the first byte.
						// 0 = Spacing Marker
						// Other = Grace Note Metadata
						int firstByte = this.readByte(componentStream);
						if (firstByte == 0x00) {
							this.readSpacingMarkerComponent(componentStream, position, firstByte);
							break;
						}
						else {
							this.readGraceNoteMetadataComponent(componentStream, position, firstByte);
							break;
						}
					}
					case 0x7E:
						this.readVoiceChangeComponent(componentStream, position);
						break;
					case 0xB6:
						this.readSymbolComponent(componentStream, position);
						break;
					case 0xB7:
						this.readEndingComponent(componentStream, position);
						break;
					case 0xBD:
						this.readBeamBreakComponent(componentStream, position);
						break;
					case 0xBE:
						this.readStemLengthComponent(componentStream, position);
						break;
					case 0xFD:
						this.readSyncopationComponent(componentStream, position);
						break;
					case 0xFE:
						this.readTempoChangeComponent(componentStream, position);
						break;
					default:
					{
						int componentTypeLowerBits = (componentTypeFull & 0x1f);
						if (componentTypeLowerBits > 0x00 && componentTypeLowerBits <= 0x19) {
							this.readNoteComponent(componentStream, position, componentTypeFull);
							break;
						}
						else {
							throw new TGException(String.format("Unexpected component! Received: 0x%X", componentTypeFull));
						}
					}
				}
			
			this.close(componentStream);
		}

		if (remainingBytes != SIZE_OF_FOOTER) {
			throw new TGException(String.format("Unexpected size for footer! Received: %s. Valid bytes: %d", Arrays.toString(componentBytes), remainingBytes));
		}

		return componentBytes; // Remaining bytes after all components
	}

	private void readNoteComponent(ByteArrayInputStream componentStream, TEPosition position, int componentType) {
		int fret = (componentType & 0x1f) - 1;

		BitSet noteFlags = BitSet.valueOf(new byte[] { (byte) componentType });
		boolean isGraceNote = noteFlags.get(6);
		boolean isPitchShifted = noteFlags.get(7);

		BitSet bitsetDurationDynamic = BitSet.valueOf(new byte[] { (byte) this.readByte(componentStream)});
		TENoteDuration duration = TENoteDuration.getEnumFromInt(this.bitsetToInt(bitsetDurationDynamic.get(0, 5)));
		TEComponentNoteDynamics dynamics = TEComponentNoteDynamics.getEnumFromInt(this.bitsetToInt(bitsetDurationDynamic.get(5, 8)));
		
		BitSet bitSetEffectAttributes = BitSet.valueOf(new byte[] { (byte) this.readByte(componentStream)});
		TEComponentNoteEffect1 noteEffect1 = TEComponentNoteEffect1.getEnumFromInt(this.bitsetToInt(bitSetEffectAttributes.get(0, 4)));
		TEComponentNoteAttributes attributes = TEComponentNoteAttributes.getEnumFromInt(this.bitsetToInt(bitSetEffectAttributes.get(4, 6)));
		TEComponentNoteAlterations alterations = TEComponentNoteAlterations.getEnumFromInt(this.bitsetToInt(bitSetEffectAttributes.get(6, 8)));

		BitSet bitSetPitchShiftGraceNote = BitSet.valueOf(new byte[] { (byte) this.readByte(componentStream)});
		TEComponentNotePitchShift pitchShift = TEComponentNotePitchShift.getEnumFromInt(this.bitsetToInt(bitSetPitchShiftGraceNote.get(0, 3)));
		TEComponentNoteGraceNoteEffect graceNoteEffect = TEComponentNoteGraceNoteEffect.getEnumFromInt(this.bitsetToInt(bitSetPitchShiftGraceNote.get(5, 8)));
		int graceNoteFret = bitsetToInt(bitSetPitchShiftGraceNote.get(0, 5));

		BitSet bitSetEffects = BitSet.valueOf(new byte[] { (byte) this.readByte(componentStream)});
		TEComponentNoteEffect2 noteEffect2 = TEComponentNoteEffect2.getEnumFromInt(this.bitsetToInt(bitSetEffects.get(0, 4)));
		TEComponentNoteEffect3 noteEffect3 = TEComponentNoteEffect3.getEnumFromInt(this.bitsetToInt(bitSetEffects.get(4, 8)));

		BitSet bitSetFonts = BitSet.valueOf(new byte[] { (byte) this.readByte(componentStream)});
		TEFontPreset fontPreset = TEFontPreset.getEnumFromInt(this.bitsetToInt(bitSetFonts.get(0, 4)));
		boolean stabilo = bitSetFonts.get(4);

		BitSet bitSetFingerStroke = BitSet.valueOf(new byte[] { (byte) this.readByte(componentStream)});
		int fingeringCombo = this.bitsetToInt(bitSetFingerStroke.get(0, 5));
		TEComponentNoteFingering firstFinger = TEComponentNoteFingering.getEnumFromInt((fingeringCombo % 6) - 1);
		TEComponentNoteFingering secondFinger = TEComponentNoteFingering.getEnumFromInt(fingeringCombo / 6);
		TEComponentNoteStroke stroke = TEComponentNoteStroke.getEnumFromInt(this.bitsetToInt(bitSetFingerStroke.get(5, 8)));

		BitSet bitsetNoteAttributes = BitSet.valueOf(new byte[] { (byte) this.readByte(componentStream)});
		boolean hasNoStemNoFlag = bitsetNoteAttributes.get(2);
		boolean isExcludedFromBeaming = bitsetNoteAttributes.get(3);
		boolean isMoveToLeft = bitsetNoteAttributes.get(4) && !bitsetNoteAttributes.get(0);
		boolean isMoveToRight = bitsetNoteAttributes.get(4) && bitsetNoteAttributes.get(0);
		boolean isTieUpward = bitsetNoteAttributes.get(5) && !bitsetNoteAttributes.get(1);
		boolean isTieDownward = bitsetNoteAttributes.get(5) && bitsetNoteAttributes.get(1);
		boolean isOttavaBassa = bitsetNoteAttributes.get(6) && !hasNoStemNoFlag;
		boolean isOttavaAlta = bitsetNoteAttributes.get(6) && hasNoStemNoFlag;

		TEComponentNote note = new TEComponentNote(position, fret, isGraceNote, isPitchShifted, duration, dynamics, noteEffect1, attributes, alterations,
			pitchShift, graceNoteEffect, graceNoteFret, noteEffect2, noteEffect3, fontPreset, stabilo, firstFinger, secondFinger, stroke, hasNoStemNoFlag,
			isExcludedFromBeaming, isMoveToLeft, isMoveToRight, isTieUpward, isTieDownward, isOttavaBassa, isOttavaAlta);
		this.song.getComponents().add(note);
	}

	private void readTempoChangeComponent(ByteArrayInputStream componentStream, TEPosition position) {
		int bpm = this.readShort(componentStream);

		BitSet bitsetRallentando = BitSet.valueOf(new byte[] { (byte) this.readByte(componentStream)});
		int rallentandoDuration = this.bitsetToInt(bitsetRallentando.get(0, 4));
		// Unknown last 4 bits.

		this.skip(componentStream, 4); // 0x00 0x00 0x00 0x00

		TEComponentTempoChange tempoChange = new TEComponentTempoChange(position, bpm, rallentandoDuration);
		this.song.getComponents().add(tempoChange);
	}

	private void readVoiceChangeComponent(ByteArrayInputStream componentStream, TEPosition position) {
		int midiPatch = this.readByte(componentStream);
		int bankLSB = this.readByte(componentStream);
		int bankMSB = this.readByte(componentStream) / 2; // Multiplied by 2 in data for some reason.

		this.skip(componentStream, 4); // 0x00 0x00 0x00 0x00

		TEComponentVoiceChange voiceChange = new TEComponentVoiceChange(position, midiPatch, bankMSB, bankLSB);
		this.song.getComponents().add(voiceChange);
	}

	private void readDrumChangeComponent(ByteArrayInputStream componentStream, TEPosition position) {
		int drumPatch = this.readByte(componentStream);
		int volume = this.readByte(componentStream);
		int character = this.readByte(componentStream);

		this.skip(componentStream, 4); // 0x00 0x00 0x00 0x00

		TEComponentDrumChange drumChange = new TEComponentDrumChange(position, drumPatch, volume, character);
		this.song.getComponents().add(drumChange);
	}

	private void readCrescendoComponent(ByteArrayInputStream componentStream, TEPosition position) {
		BitSet flagBitset = BitSet.valueOf(new byte[] { (byte)this.readByte(componentStream) });
		boolean decrescendo = flagBitset.get(4);

		int durationInSixteenthNotes = this.readByte(componentStream);
		int yPosition = this.readByte(componentStream);

		this.skip(componentStream, 3); // 0x00 0x00 0x00

		TEAnchorPosition anchorPosition = TEAnchorPosition.getEnumFromInt(this.readByte(componentStream));

		TEComponentCrescendo crescendo = new TEComponentCrescendo(position, decrescendo, durationInSixteenthNotes, yPosition, anchorPosition);
		this.song.getComponents().add(crescendo);
	}

	private void readAccentComponent(ByteArrayInputStream componentStream, TEPosition position) {
		this.skip(componentStream, 1); // 0x00

		int volume = this.readByte(componentStream);
		int yPosition = this.readByte(componentStream);
		int xPosition = this.readByte(componentStream);

		this.skip(componentStream, 2); // 0x00 0x00

		TEAnchorPosition anchorPosition = TEAnchorPosition.getEnumFromInt(this.readByte(componentStream));

		TEComponentAccent accent = new TEComponentAccent(position, volume, yPosition, xPosition, anchorPosition);
		this.song.getComponents().add(accent);
	}

	private void readConnectionComponent(ByteArrayInputStream componentStream, TEPosition position) {
		BitSet durationDashBitset = BitSet.valueOf(new byte[] {(byte) this.readByte(componentStream)});
		int duration = this.bitsetToInt(durationDashBitset.get(0, 7)) + 1;
		boolean dashed = durationDashBitset.get(7);

		BitSet amplitudeNumberBitset = BitSet.valueOf(new byte[] {(byte) this.readByte(componentStream)});
		int amplitude = this.bitsetToInt(amplitudeNumberBitset.get(0, 4));
		int number = this.bitsetToInt(amplitudeNumberBitset.get(4, 8));

		int yPosition = this.readByte(componentStream);

		this.skip(componentStream, 3); // 0x00 0x00 0x00

		BitSet bottomUpBracketBitset = BitSet.valueOf(new byte[] {(byte) this.readByte(componentStream)});
		boolean bottomUp = bottomUpBracketBitset.get(6);
		boolean bracket = bottomUpBracketBitset.get(7);

		TEComponentConnection connection = new TEComponentConnection(position, duration, dashed, amplitude, number, yPosition, bottomUp, bracket);
		this.song.getComponents().add(connection);
	}

	private void readLineBreakComponent(ByteArrayInputStream componentStream, TEPosition position) {
		this.skip(componentStream, 2); // 0x00 0x00

		BitSet verticalSpacingBitset = BitSet.valueOf(new byte[] {(byte) this.readByte(componentStream)});
		int verticalSpacing = this.bitsetToInt(verticalSpacingBitset.get(4, 8));

		int nextLineIndex = this.readByte(componentStream);
		int measure = this.readShort(componentStream);

		BitSet lineBreakFlagsBitset = BitSet.valueOf(new byte[] {(byte) this.readByte(componentStream)});
		boolean truncateCurrentLine = lineBreakFlagsBitset.get(0);
		boolean pageBreak = lineBreakFlagsBitset.get(1);
		boolean doubleBar = lineBreakFlagsBitset.get(2);
		boolean timeSign = lineBreakFlagsBitset.get(3);

		TEComponentLineBreak lineBreak = new TEComponentLineBreak(position, verticalSpacing, nextLineIndex, measure,
			truncateCurrentLine, pageBreak, doubleBar, timeSign);
		this.song.getComponents().add(lineBreak);
	}

	private void readSyncopationComponent(ByteArrayInputStream componentStream, TEPosition position) {
		BitSet syncopationTypeBitset = BitSet.valueOf(new byte[] {(byte)this.readByte(componentStream)});
		int syncopationType = this.bitsetToInt(syncopationTypeBitset.get(0, 4)) - 8; // Offset by +8 in data. 8 = 0. 7 = -1.

		this.skip(componentStream, 6); // 0x00 0x00 0x00 0x00 0x00 0x00

		TEComponentSyncopation syncopation = new TEComponentSyncopation(position, syncopationType);
		this.song.getComponents().add(syncopation);
	}

	private void readSymbolComponent(ByteArrayInputStream componentStream, TEPosition position) {
		int symbol = this.readByte(componentStream);

		this.skip(componentStream, 1); // 0x00

		int yPosition = this.readByte(componentStream);
		int xPosition = this.readByte(componentStream);

		this.skip(componentStream, 2); // 0x18 0x00

		TEAnchorPosition anchorPosition = TEAnchorPosition.getEnumFromInt(this.readByte(componentStream));

		TEComponentSymbol symbolComponent = new TEComponentSymbol(position, symbol, yPosition, xPosition, anchorPosition);
		this.song.getComponents().add(symbolComponent);
	}

	private void readEndingComponent(ByteArrayInputStream componentStream, TEPosition position) {
		this.skip(componentStream, 1); // 0x01

		BitSet endingBitset = BitSet.valueOf(new byte[] {(byte)this.readByte(componentStream)});
		int endingNumber = this.bitsetToInt(endingBitset.get(0, 3));
		TEComponentEndingFlag endingFlags = TEComponentEndingFlag.getEnumFromInt(this.bitsetToInt(endingBitset.get(3, 6)));
		boolean openBracket = endingBitset.get(6);
		boolean closeBracket = endingBitset.get(7);

		int yPosition = this.readByte(componentStream);
		int xPosition = this.readByte(componentStream);

		this.skip(componentStream, 3); // 0x00 0x00 0x00

		TEComponentEnding ending = new TEComponentEnding(position, endingNumber, endingFlags, openBracket, closeBracket, yPosition, xPosition);
		this.song.getComponents().add(ending);
	}

	private void readScaleDiagramComponent(ByteArrayInputStream componentStream, TEPosition position) {
		this.skip(componentStream, 2); // 0x00 0x00

		int yPosition = this.readByte(componentStream);
		int xPosition = this.readByte(componentStream);

		this.skip(componentStream, 2); // 0x00 0x00

		TEAnchorPosition anchorPosition = TEAnchorPosition.getEnumFromInt(this.readByte(componentStream));

		TEComponentScaleDiagram scaleDiagram = new TEComponentScaleDiagram(position, yPosition, xPosition, anchorPosition);
		this.song.getComponents().add(scaleDiagram);
	}

	private void readBeamBreakComponent(ByteArrayInputStream componentStream, TEPosition position) {
		this.skip(componentStream, 7); // 0x00 0x00 0x00 0x00 0x00 0x00 0x00

		TEComponentBeamBreak beamBreak = new TEComponentBeamBreak(position);
		this.song.getComponents().add(beamBreak);
	}

	private void readStemLengthComponent(ByteArrayInputStream componentStream, TEPosition position) {
		this.skip(componentStream, 2); // 0x00 0x00

		int yPosition = this.readByte(componentStream);

		this.skip(componentStream, 4); // 0x00 0x00 0x00 0x00
		
		TEComponentStemLength stemLength = new TEComponentStemLength(position, yPosition);
		this.song.getComponents().add(stemLength);
	}

	private void readSpacingMarkerComponent(ByteArrayInputStream componentStream, TEPosition position, int firstByte) {
		this.skip(componentStream, 2); // 0x00 0x00

		int xPosition = this.readByte(componentStream);

		this.skip(componentStream, 3); // 0x00 0x00 0x00

		TEComponentSpacingMarker spacingMarker = new TEComponentSpacingMarker(position, xPosition);
		this.song.getComponents().add(spacingMarker);
	}

	private void readRestComponent(ByteArrayInputStream componentStream, TEPosition position) {
		TENoteDuration duration = TENoteDuration.getEnumFromInt(this.readByte(componentStream));

		BitSet bitSetEffectAttributes = BitSet.valueOf(new byte[] { (byte) this.readByte(componentStream)});
		boolean isUpperVoice = bitSetEffectAttributes.get(5) && !bitSetEffectAttributes.get(4);
		boolean isLowerVoice = bitSetEffectAttributes.get(5) && bitSetEffectAttributes.get(4);

		int yPosition = this.readByte(componentStream);

		boolean isSecondaryBeamBreak = this.readByte(componentStream) == 7;

		this.skip(componentStream, 2); // 0x00 0x00

		BitSet bitsetRestAttributes = BitSet.valueOf(new byte[] { (byte) this.readByte(componentStream)});
		boolean hiddenInNotation = bitsetRestAttributes.get(0);
		boolean hasNoStemNoFlag = bitsetRestAttributes.get(2);
		boolean isExcludedFromBeaming = bitsetRestAttributes.get(3);
		boolean isMoveToLeft = bitsetRestAttributes.get(4) && !hiddenInNotation;
		boolean isMoveToRight = bitsetRestAttributes.get(4) && hiddenInNotation;
		boolean hiddenInTablature = bitsetRestAttributes.get(5);

		TEComponentRest rest = new TEComponentRest(position, duration, isUpperVoice, isLowerVoice, yPosition, isSecondaryBeamBreak,
		hiddenInNotation, hasNoStemNoFlag, isExcludedFromBeaming, isMoveToLeft, isMoveToRight, hiddenInTablature);
		this.song.getComponents().add(rest);
	}

	private void readGraceNoteMetadataComponent(ByteArrayInputStream componentStream, TEPosition position, int firstByte) {
		TEComponentGraceNoteMetadataDuration duration = TEComponentGraceNoteMetadataDuration.getEnumFromInt(firstByte);

		this.skip(componentStream, 1); // 0x00

		BitSet graceNoteStringBitset = BitSet.valueOf(new byte[] {(byte) this.readByte(componentStream)});
		int graceNoteString = this.bitsetToInt(graceNoteStringBitset.get(0, 7));
		boolean onDifferentString = graceNoteStringBitset.get(7);

		BitSet doubleNoteOneBitset = BitSet.valueOf(new byte[] {(byte) this.readByte(componentStream)});
		int doubleNoteOne = this.bitsetToInt(doubleNoteOneBitset.get(0, 7));
		boolean hasDoubleNoteOne = doubleNoteOneBitset.get(7);

		BitSet doubleNoteTwoBitset = BitSet.valueOf(new byte[] {(byte) this.readByte(componentStream)});
		int doubleNoteTwo = this.bitsetToInt(doubleNoteTwoBitset.get(0, 7));
		boolean hasDoubleNoteTwo = doubleNoteTwoBitset.get(7);

		int xPosition = this.readByte(componentStream);

		BitSet flags = BitSet.valueOf(new byte[] {(byte) this.readByte(componentStream)});
		boolean noFlags = flags.get(0);
		boolean noSlur = flags.get(1);
		boolean stemDown = flags.get(2);
		// Unknown bits 3, 4
		boolean sharpOrFlat = flags.get(5);
		boolean acciaccatura = flags.get(6);
		// Unknown bit 7

		TEComponentGraceNoteMetadata graceNoteMetadata = new TEComponentGraceNoteMetadata(position, duration, graceNoteString, onDifferentString, doubleNoteOne,
			hasDoubleNoteOne, doubleNoteTwo, hasDoubleNoteTwo, xPosition, noFlags, noSlur, stemDown, sharpOrFlat, acciaccatura);
		this.song.getComponents().add(graceNoteMetadata);
	}

	private void readChordComponent(ByteArrayInputStream componentStream, TEPosition position) {
		int chordIndex = this.readShort(componentStream);
		int yPosition = this.readByte(componentStream);
		int xPosition = this.readByte(componentStream);

		this.skip(componentStream, 2); // 0x00 0x00

		TEAnchorPosition anchorPosition = TEAnchorPosition.getEnumFromInt(this.readByte(componentStream));

		TEComponentChord chord = new TEComponentChord(position, chordIndex, yPosition, xPosition, anchorPosition);
		this.song.getComponents().add(chord);
	}

	private void readTextEventComponent(ByteArrayInputStream componentStream, TEPosition position) {
		int textIndex = this.readShort(componentStream);

		int yPosition = this.readByte(componentStream);
		int xPosition = this.readByte(componentStream);

		BitSet fontPresetCenteredBitset = BitSet.valueOf(new byte[] {(byte) this.readByte(componentStream)});
		TEFontPreset fontPreset = TEFontPreset.getEnumFromInt(this.bitsetToInt(fontPresetCenteredBitset.get(0, 4)));
		boolean centeredText = fontPresetCenteredBitset.get(4);

		TEComponentTextEventBorderType borderType = TEComponentTextEventBorderType.getEnumFromInt(this.bitsetToInt(fontPresetCenteredBitset.get(0, 4)));
		TEAnchorPosition anchorPosition = TEAnchorPosition.getEnumFromInt(this.readByte(componentStream));

		TEComponentTextEvent textEvent = new TEComponentTextEvent(position, textIndex, yPosition, xPosition, fontPreset, centeredText, borderType, anchorPosition);
		this.song.getComponents().add(textEvent);
	}
	
	//-----------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------//
	//-----------------------------------------------------------------------------//

	protected int bitsetToInt(BitSet bitset)
	{
		int value = 0;

		int bit = 0;
		while ((bit = bitset.nextSetBit(bit)) != -1)
		{
			value += 1 << bit;
			bit++;
		}

		return value;
	}

	protected int readBytes(byte[] bytes){
		return this.readBytes(this.stream, bytes);
	}

	protected int readBytes(InputStream stream, byte[] bytes){
		try {
			return stream.read(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return -1;
	}
	
	protected int readByte(){
		return this.readByte(this.stream);
	}

	protected int readByte(InputStream stream){
		try {
			return stream.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	protected int readShort(){
		return this.readShort(this.stream);
	}

	protected int readShort(InputStream stream){
		try {
			byte[] b = new byte[2];
			stream.read(b);
			return ((b[1] & 0xff) << 8) | (b[0] & 0xff);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	protected int readInt(){
		return this.readInt(this.stream);
	}

	protected int readInt(InputStream stream){
		try {
			byte[] b = new byte[4];
			stream.read(b);
			return ((b[3] & 0xff) << 24 ) | ((b[2] & 0xff) << 16 ) | ((b[1] & 0xff) << 8) | (b[0] & 0xff);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	protected String readShortString() {
		return this.readShortString(this.stream);
	}

	protected String readShortString(InputStream stream) {
		int strLength = this.readShort(stream);

		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < strLength; i++)
		{
			int byteRead = this.readByte(stream);

			// NULL termination
			if (byteRead == 0)
			{
				break;
			}

			char c = (char)byteRead;
			stringBuilder.append(c);
		}

		return stringBuilder.toString();
	}

	protected String readNullTerminatedString() {
		return this.readNullTerminatedString(this.stream, 256);
	}

	protected String readNullTerminatedString(int maxLength) {
		return this.readNullTerminatedString(this.stream, maxLength);
	}

	protected String readNullTerminatedString(InputStream stream) {
		return this.readNullTerminatedString(stream, 256);
	}

	protected String readNullTerminatedString(InputStream stream, int maxLength) {
		StringBuilder stringBuilder = new StringBuilder();
		
		try {
			int lastByte;

			while ((lastByte = stream.read()) != 0)
			{
				// NULL termination
				if (lastByte == 0 || stringBuilder.length() > maxLength)
				{
					break;
				}

				char c = (char)lastByte;
				stringBuilder.append(c);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return stringBuilder.toString();
	}
	
	protected void skip(int count){
		this.skip(this.stream, count);
	}

	protected void skip(InputStream stream, int count){
		for(int i = 0; i < count; i++){
			this.readByte(stream);
		}
	}

	protected void close(){
		this.close(this.stream);
	}
	
	protected void close(InputStream stream){
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
