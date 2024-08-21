package org.herac.tuxguitar.io.tg;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGStroke;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.song.models.effects.TGEffectGrace;
import org.herac.tuxguitar.song.models.effects.TGEffectHarmonic;
import org.herac.tuxguitar.util.TGVersion;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/* this class hosts everything common to read/write tg file operations */

public class TGStream {
	
	public static final String MODULE_NAME = "dev-fileformat";

	protected static final TGVersion FILE_FORMAT_TGVERSION = new TGVersion(2,0,0);
	
	public static final String TG_FORMAT_NAME = ("TuxGuitar File Format");
	public static final String TG_FORMAT_CODE = ("tg");
	public static final TGFileFormat TG_FORMAT = new TGFileFormat("TuxGuitar 2.0", "application/x-tuxguitar", new String[]{ TG_FORMAT_CODE });

	// XML tags and values of enums
	protected static final String CONTENT_FILE_NAME = "content.xml";
	protected static final String VERSION_FILE_NAME = "version.txt";
	protected static final String VERSION_PREFIX = "TuxGuitar_file_format";
	protected static final String VERSION_SEPARATOR = " ";
	protected static final String TAG_TG_VERSION = "TGVersion";
	protected static final String TAG_TGFile = "TuxGuitarFile";
	protected static final String TAG_TGSONG = "TGSong" ;
	protected static final String TAG_NAME = "name";
	protected static final String TAG_ARTIST= "artist";
	protected static final String TAG_ALBUM = "album";
	protected static final String TAG_AUTHOR = "author";
	protected static final String TAG_DATE = "date";
	protected static final String TAG_COPYRIGHT = "copyright";
	protected static final String TAG_WRITER = "writer";
	protected static final String TAG_TRANSCRIBER = "transcriber";
	protected static final String TAG_COMMENTS = "comments";
	protected static final String TAG_CHANNEL = "TGChannel";
	protected static final String TAG_ID = "id";
	protected static final String TAG_BANK = "bank";
	protected static final String TAG_PROGRAM = "program";
	protected static final String TAG_VOLUME = "volume";
	protected static final String TAG_BALANCE = "balance";
	protected static final String TAG_CHORUS = "chorus";
	protected static final String TAG_REVERB = "reverb";
	protected static final String TAG_PHASER = "phaser";
	protected static final String TAG_TREMOLO = "tremolo";
	protected static final String TAG_CHANNEL_PARAMETER = "TGChannelParameter";
	protected static final String TAG_KEY = "key";
	protected static final String TAG_VALUE = "value";
	protected static final String TAG_MEASURE_HEADER = "TGMeasureHeader";
	protected static final String TAG_TIME_SIGNATURE = "timeSignature";
	protected static final String TAG_NUMERATOR = "numerator";
	protected static final String TAG_DENOMINATOR = "denominator";
	protected static final String TAG_TEMPO = "tempo";
	protected static final String TAG_REPEAT_OPEN = "repeatOpen";
	protected static final String TAG_REPEAT_CLOSE = "repeatClose";
	protected static final String TAG_REPEAT_ALTERNATIVE = "repeatAlternative";
	protected static final String TAG_ALTERNATIVE = "alternative";
	protected static final String TAG_MARKER = "marker";
	protected static final String TAG_COLOR_R = "R";
	protected static final String TAG_COLOR_G = "G";
	protected static final String TAG_COLOR_B = "B";
	protected static final String TAG_TRIPLET_FEEL = "tripletFeel";
	protected static final String TAG_LINE_BREAK = "lineBreak";
	protected static final String TAG_TGTRACK = "TGTrack";
	protected static final String TAG_SOLOMUTE = "soloMute";
	protected static final String VAL_SOLO = "solo";
	protected static final String VAL_MUTE = "mute";
	protected static final String TAG_CHANNELID = "channelId";
	protected static final String TAG_OFFSET = "offset";
	protected static final String TAG_COLOR = "color";
	protected static final String TAG_TGSTRING = "TGString";
	protected static final String TAG_TGLYRIC = "TGLyric";
	protected static final String TAG_FROM = "from";
	protected static final String TAG_TGMEASURE = "TGMeasure";
	protected static final String TAG_CLEF = "clef";
	protected static final String TAG_KEYSIGNATURE = "keySignature";
	protected static final String TAG_TGBEAT = "TGBeat";
	protected static final String TAG_START = "start";
	protected static final String TAG_STROKE = "stroke";
	protected static final String TAG_DIRECTION = "direction";
	protected static final String TAG_CHORD = "chord";
	protected static final String TAG_STRING = "string";
	protected static final String TAG_FIRSTFRET = "firstFret";
	protected static final String TAG_TEXT = "text";
	protected static final String TAG_VOICE = "voice";
	protected static final String TAG_DURATION = "duration";
	protected static final String TAG_DOTTED = "dotted";
	protected static final String VAL_DOTTED = "dotted";
	protected static final String VAL_DOUBLEDOTTED = "doubleDotted";
	protected static final String TAG_DIVISIONTYPE = "divisionType";
	protected static final String TAG_ENTERS = "enters";
	protected static final String TAG_TIMES = "times";
	protected static final String TAG_NOTE = "note";
	protected static final String TAG_VELOCITY = "velocity";
	protected static final String TAG_TIEDNOTE = "tiedNote";
	protected static final String TAG_VIBRATO ="vibrato";
	protected static final String TAG_DEADNOTE = "deadNote";
	protected static final String TAG_SLIDE = "slide";
	protected static final String TAG_HAMMER = "hammer";
	protected static final String TAG_GHOSTNOTE = "ghostNote";
	protected static final String TAG_ACCENTUATEDNOTE = "accentuatedNote";
	protected static final String TAG_HEAVYACCENTUATEDNOTE = "heavyAccentuatedNote";
	protected static final String TAG_PALMMUTE = "palmMute";
	protected static final String TAG_STACCATO = "staccato";
	protected static final String TAG_TAPPING = "tapping";
	protected static final String TAG_SLAPPING = "slapping";
	protected static final String TAG_POPPING = "popping";
	protected static final String TAG_FADEIN = "fadeIn";
	protected static final String TAG_LETRING = "letRing";
	protected static final String TAG_BEND = "bend";
	protected static final String TAG_TREMOLOBAR = "tremoloBar";
	protected static final String TAG_HARMONIC = "harmonic";
	protected static final String TAG_GRACE = "grace";
	protected static final String TAG_TRILL = "trill";
	protected static final String TAG_TREMOLOPICKING = "tremoloPicking";
	protected static final String TAG_POINT = "point";
	protected static final String TAG_POSITION = "position";
	protected static final String TAG_TYPE = "type";
	protected static final String TAG_DATA = "data";
	protected static final String TAG_FRET = "fret";
	protected static final String TAG_DYNAMIC = "dynamic";
	protected static final String TAG_ONBEAT = "onBeat";
	protected static final String TAG_DEAD = "dead";
	protected static final String TAG_TRANSITION = "transition";

	protected Map<String, Integer> tripletsReadMap;
	protected Map<Integer, String> tripletsWriteMap;
	protected Map<String, Integer> mapReadClefs;
	protected Map<Integer, String> mapWriteClefs;
	protected Map<String, Integer> mapReadStroke;
	protected Map<Integer, String> mapWriteStroke;
	protected Map<String, Integer> mapReadDirection;
	protected Map<Integer, String> mapWriteDirection;
	protected Map<String, Integer> harmonicReadMap;
	protected Map<Integer, String> harmonicWritedMap;
	protected Map<String, Integer> mapReadTransition;
	protected Map<Integer, String> mapWriteTransition;
	protected Map<Integer, Integer> mapReadGraceDuration;
	protected Map<Integer, Integer> mapWriteGraceDuration;


	public TGStream() {
		this.tripletsReadMap = new HashMap<String,Integer>();
		this.tripletsReadMap.put("none", TGMeasureHeader.TRIPLET_FEEL_NONE);
		this.tripletsReadMap.put("eighth", TGMeasureHeader.TRIPLET_FEEL_EIGHTH);
		this.tripletsReadMap.put("sixteenth", TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH);
		this.tripletsWriteMap = this.revertMap(this.tripletsReadMap);

		this.mapReadClefs = new HashMap<String, Integer>();
		this.mapReadClefs.put("treble", TGMeasure.CLEF_TREBLE);
		this.mapReadClefs.put("bass", TGMeasure.CLEF_BASS);
		this.mapReadClefs.put("tenor", TGMeasure.CLEF_TENOR);
		this.mapReadClefs.put("alto", TGMeasure.CLEF_ALTO);
		this.mapWriteClefs = this.revertMap(this.mapReadClefs);
		
		this.mapReadStroke = new HashMap<String, Integer>();
		this.mapReadStroke.put("none", TGStroke.STROKE_NONE);
		this.mapReadStroke.put("up", TGStroke.STROKE_UP);
		this.mapReadStroke.put("down", TGStroke.STROKE_DOWN);
		this.mapWriteStroke = this.revertMap(this.mapReadStroke);
		
		this.mapReadDirection = new HashMap<String, Integer>();
		this.mapReadDirection.put("up", TGVoice.DIRECTION_UP);
		this.mapReadDirection.put("down", TGVoice.DIRECTION_DOWN);
		this.mapWriteDirection = this.revertMap(this.mapReadDirection);
		
		this.harmonicReadMap = new HashMap<String, Integer>();
		this.harmonicReadMap.put(TGEffectHarmonic.KEY_NATURAL, TGEffectHarmonic.TYPE_NATURAL);
		this.harmonicReadMap.put(TGEffectHarmonic.KEY_ARTIFICIAL, TGEffectHarmonic.TYPE_ARTIFICIAL);
		this.harmonicReadMap.put(TGEffectHarmonic.KEY_PINCH, TGEffectHarmonic.TYPE_PINCH);
		this.harmonicReadMap.put(TGEffectHarmonic.KEY_SEMI, TGEffectHarmonic.TYPE_SEMI);
		this.harmonicReadMap.put(TGEffectHarmonic.KEY_TAPPED, TGEffectHarmonic.TYPE_TAPPED);
		this.harmonicWritedMap = this.revertMap(this.harmonicReadMap);
		
		this.mapReadTransition = new HashMap<String, Integer>();
		this.mapReadTransition.put("none", TGEffectGrace.TRANSITION_NONE);
		this.mapReadTransition.put("slide", TGEffectGrace.TRANSITION_SLIDE);
		this.mapReadTransition.put("bend", TGEffectGrace.TRANSITION_BEND);
		this.mapReadTransition.put("hammer", TGEffectGrace.TRANSITION_HAMMER);
		this.mapWriteTransition = this.revertMap(this.mapReadTransition);
		
		this.mapReadGraceDuration = new HashMap<Integer, Integer>();
		this.mapReadGraceDuration.put(64, 1);
		this.mapReadGraceDuration.put(32, 2);
		this.mapReadGraceDuration.put(16, 3);
		this.mapWriteGraceDuration = this.revertMap(this.mapReadGraceDuration);
	}
	
	protected TGVersion getFileFormatVersion(InputStream versionStream) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(versionStream));
			String line = reader.readLine();
			String[] parts = line.split(VERSION_SEPARATOR);
			if ((parts.length != 2) || !VERSION_PREFIX.equals(parts[0])) {
				return null;
			}
			return new TGVersion(parts[1]);
		} catch (Throwable e) {
			return null;
		}
	}

	
	protected class PositionValue {
		private int position;
		private int value;
		protected PositionValue(int pos, int val) {
			this.position = pos;
			this.value=val;
		}
		protected int getPosition() {
			return this.position;
		}
		protected int getValue() {
			return this.value;
		}
	}
	
	private <V,K> Map<V,K> revertMap(Map<K,V> map) {
		Map<V,K> revMap = new HashMap<V, K>();
		for (K key : map.keySet()) {
			revMap.put(map.get(key), key);
		}
		return revMap;
	}
	
	protected Document getDocument(InputStream inputStream) throws IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// see CVE-2020-14940
		try {
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			factory.setXIncludeAware(false);
		} catch (Throwable throwable) {
		}
		
		try {
			return factory.newDocumentBuilder().parse(inputStream);
		} catch (Throwable throwable) {
			throw new TGFileFormatException("Invalid xml file format", throwable);
		}
	}
	
	protected Node getChildNode(Node node, String nodeName) {
		Node n = node.getFirstChild();
		while (n!=null && !nodeName.equals(n.getNodeName())) {
			n = n.getNextSibling();
		}
		return n;
	}
	
	protected Node getSiblingNode(Node node, String nodeName) {
		Node n = node;
		while (n!=null && !nodeName.equals(n.getNodeName())) {
			n = n.getNextSibling();
		}
		return n;
	}
	
	protected boolean hasChild(Node node, String name) {
		return (getChildNode(node, name) != null);
	}
	
	protected String readSibling(Node node, String nodeName) {
		return getSiblingNode(node, nodeName).getTextContent();
	}
	protected int readSiblingInt(Node node, String nodeName) {
		return Integer.valueOf(readSibling(node, nodeName));
	}
	protected short readSiblingShort(Node node, String nodeName) {
		return Short.valueOf(readSibling(node, nodeName));
	}
	
	protected String readAttribute(Node node, String attributeName) {
		return node.getAttributes().getNamedItem(attributeName).getTextContent();
	}
	
	protected int readAttributeInt(Node node, String attributeName) {
		return Integer.valueOf(readAttribute(node, attributeName));
	}
	
	protected int readInt(Node node) {
		return Integer.valueOf(node.getTextContent());
	}
	
	public InputStream getDecompressedContent(InputStream inputStream) throws IOException {
		return this.getDecompressedFile(inputStream, CONTENT_FILE_NAME);
	}
	
	public InputStream getDecompressedVersion(InputStream inputStream) throws IOException {
		return this.getDecompressedFile(inputStream, VERSION_FILE_NAME);
	}
	
	public InputStream[] getDecompressedVersionAndContent(InputStream inputStream) throws IOException {
		return this.getDecompressedFiles(inputStream, new String[] {VERSION_FILE_NAME, CONTENT_FILE_NAME});
	}
	
	private InputStream getDecompressedFile(InputStream inputStream, String fileName) throws IOException {
		return this.getDecompressedFiles(inputStream, new String[] {fileName})[0];
	}
	
	private InputStream[] getDecompressedFiles(InputStream inputStream, String[] fileNames) throws IOException {
		InputStream[] streams = new InputStream[fileNames.length];
		BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
		ZipArchiveInputStream zipInputStream = new ZipArchiveInputStream(bufferedInputStream);
		ArchiveEntry zipEntry = null;
		while ((zipEntry = zipInputStream.getNextEntry()) != null) {
			for (int i=0; i<fileNames.length;i++) {
				if (zipEntry.getName().equals(fileNames[i])) {
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
					IOUtils.copy(zipInputStream, bufferedOutputStream);
					bufferedOutputStream.flush();
					byte buffer[] = outputStream.toByteArray();
					streams[i] = new ByteArrayInputStream(buffer);
				}
			}
		}
		return streams;
	}

}
