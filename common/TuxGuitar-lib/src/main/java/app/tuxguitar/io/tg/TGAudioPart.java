package app.tuxguitar.io.tg;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTake;
import app.tuxguitar.song.models.TGTakeClip;
import app.tuxguitar.song.models.TGTrack;

/**
 * The audio of a song in a TuxGuitar file of format 3.0. content.xml holds one TGAudio node under TGSong: the takes by
 * id with their numbers, each audio channel's input channels, and each audio track's next take number and, measure by
 * measure, its clips and the take chosen there. Each take's samples are an entry of the file, "takes/<id>.<extension>",
 * written and read by the take codec. Takes are shared, never copied: a take that several measures or tracks play is
 * one entry, and one take again once read. Format 2.0 has none of it: a song without audio channels is written without.
 */
final class TGAudioPart {

	static final String TAKES = "takes/";

	private static final String TAG_AUDIO = "TGAudio";
	private static final String TAG_TAKE = "TGTake";
	private static final String TAG_CHANNEL = "TGAudioChannel";
	private static final String TAG_TRACK = "TGAudioTrack";
	private static final String TAG_MEASURE = "TGAudioMeasure";
	private static final String TAG_CLIP = "TGTakeClip";

	// the song's takes, in the order its audio tracks first name them: a take's id is its place here, from 1
	private final List<TGTake> takes = new ArrayList<TGTake>();
	private final Map<TGTake, Integer> ids = new IdentityHashMap<TGTake, Integer>();
	private final TGSong song;

	TGAudioPart(TGSong song) {
		this.song = song;
		for (TGTrack track : audioTracks(song)) {
			Iterator<TGMeasure> measures = track.getMeasures();
			while (measures.hasNext()) {
				this.name(measures.next());
			}
		}
	}

	// a song needs format 3.0 once a channel records audio
	static boolean isIn(TGSong song) {
		Iterator<TGChannel> channels = song.getChannels();
		while (channels.hasNext()) {
			if (channels.next().isAudioChannel()) {
				return true;
			}
		}
		return false;
	}

	static String entry(int id, TGTakeCodec codec) {
		return TAKES + id + "." + codec.getExtension();
	}

	private void name(TGMeasure measure) {
		for (TGTakeClip clip : measure.getTakeClips()) {
			this.name(clip.getTake());
			this.name(clip.getSource());
		}
		this.name(measure.getChosenTake());
	}

	private void name(TGTake take) {
		if (take != null && !this.ids.containsKey(take)) {
			this.takes.add(take);
			this.ids.put(take, this.takes.size());
		}
	}

	private static List<TGTrack> audioTracks(TGSong song) {
		List<TGTrack> tracks = new ArrayList<TGTrack>();
		song.getTracks().forEachRemaining(track -> {
			if (track.isAudio()) {
				tracks.add(track);
			}
		});
		return tracks;
	}

	// the TGAudio node, under the song's
	void write(Document document, Node nodeSong) {
		Element audio = add(document, nodeSong, TAG_AUDIO);
		for (TGTake take : this.takes) {
			add(document, audio, TAG_TAKE, "id", this.ids.get(take), "number", take.getNumber());
		}
		this.song.getChannels().forEachRemaining(channel -> {
			if (channel.isAudioChannel()) {
				add(document, audio, TAG_CHANNEL, "id", channel.getChannelId()).setAttribute("input", input(channel.getAudioInput()));
			}
		});
		for (TGTrack track : audioTracks(this.song)) {
			this.write(document, add(document, audio, TAG_TRACK, "number", track.getNumber(), "nextTakeNumber", track.getNextTakeNumber()), track);
		}
	}

	private void write(Document document, Element nodeTrack, TGTrack track) {
		Iterator<TGMeasure> measures = track.getMeasures();
		while (measures.hasNext()) {
			TGMeasure measure = measures.next();
			// a take is chosen only where it plays
			if (!measure.getTakeClips().isEmpty()) {
				this.write(document, add(document, nodeTrack, TAG_MEASURE, "number", measure.getNumber()), measure);
			}
		}
	}

	private void write(Document document, Element nodeMeasure, TGMeasure measure) {
		if (measure.getChosenTake() != null) {
			nodeMeasure.setAttribute("chosenTake", String.valueOf(this.ids.get(measure.getChosenTake())));
		}
		for (TGTakeClip clip : measure.getTakeClips()) {
			Element nodeClip = add(document, nodeMeasure, TAG_CLIP, "take", this.ids.get(clip.getTake()), "source", this.ids.get(clip.getSource()));
			nodeClip.setAttribute("start", String.valueOf(clip.getStart()));
			nodeClip.setAttribute("end", String.valueOf(clip.getEnd()));
			nodeClip.setAttribute("firstSample", String.valueOf(clip.getFirstSample()));
			nodeClip.setAttribute("tempo", String.valueOf(clip.getTempo()));
		}
	}

	private static String input(TGAudioInput input) {
		StringJoiner channels = new StringJoiner(" ");
		for (int side = 0; side < input.countChannels(); side++) {
			channels.add(String.valueOf(input.getChannel(side)));
		}
		return channels.toString();
	}

	// an element under the parent, with integer attributes given as name, value pairs
	private static Element add(Document document, Node parent, String tag, Object... attributes) {
		Element element = document.createElement(tag);
		for (int i = 0; i < attributes.length; i += 2) {
			element.setAttribute((String) attributes[i], String.valueOf(attributes[i + 1]));
		}
		parent.appendChild(element);
		return element;
	}

	// each take's entry, after content.xml, its samples streamed by the codec
	void writeTakes(ArchiveOutputStream<ZipArchiveEntry> zip, TGTakeCodec codec) throws IOException {
		for (TGTake take : this.takes) {
			zip.putArchiveEntry(new ZipArchiveEntry(entry(this.ids.get(take), codec)));
			codec.write(take, unclosed(zip));
			zip.closeArchiveEntry();
		}
	}

	// the codec writes onto the zip's entry and may not close the zip
	private static OutputStream unclosed(OutputStream out) {
		return new java.io.FilterOutputStream(out) {
			public void write(byte[] bytes, int offset, int length) throws IOException {
				this.out.write(bytes, offset, length);
			}

			public void close() throws IOException {
				this.flush();
			}
		};
	}

	// the TGAudio node of the song's node onto the song read from it, its takes from the entries read (by entry name,
	// unnumbered)
	static void read(Node nodeSong, TGSong song, Map<String, TGTake> entries, TGTakeCodec codec) throws TGFileFormatException {
		Element audio = (Element) TGAudioPart.child(nodeSong, TAG_AUDIO);
		if (audio == null) {
			return;
		}
		Map<Integer, TGTake> takes = new java.util.HashMap<Integer, TGTake>();
		for (Element take : elements(audio, TAG_TAKE)) {
			TGTake decoded = entries.get(entry(integer(take, "id"), codec));
			if (decoded == null) {
				throw new TGFileFormatException("No entry for take " + take.getAttribute("id"));
			}
			takes.put(integer(take, "id"), decoded.renumbered(integer(take, "number")));
		}
		for (Element channel : elements(audio, TAG_CHANNEL)) {
			channel(song, integer(channel, "id")).setAudioInput(TGAudioInput.of(integers(channel.getAttribute("input"))));
		}
		for (Element nodeTrack : elements(audio, TAG_TRACK)) {
			TGTrack track = song.getTrack(integer(nodeTrack, "number") - 1);
			track.setNextTakeNumber(integer(nodeTrack, "nextTakeNumber"));
			for (Element nodeMeasure : elements(nodeTrack, TAG_MEASURE)) {
				read(nodeMeasure, track.getMeasure(integer(nodeMeasure, "number") - 1), takes);
			}
		}
	}

	private static void read(Element nodeMeasure, TGMeasure measure, Map<Integer, TGTake> takes) {
		if (nodeMeasure.hasAttribute("chosenTake")) {
			measure.setChosenTake(takes.get(integer(nodeMeasure, "chosenTake")));
		}
		for (Element clip : elements(nodeMeasure, TAG_CLIP)) {
			measure.addTakeClip(new TGTakeClip(takes.get(integer(clip, "take")), takes.get(integer(clip, "source")),
				Long.parseLong(clip.getAttribute("start")), Long.parseLong(clip.getAttribute("end")),
				integer(clip, "firstSample"), Double.parseDouble(clip.getAttribute("tempo"))));
		}
	}

	private static TGChannel channel(TGSong song, int id) {
		Iterator<TGChannel> channels = song.getChannels();
		TGChannel channel = channels.next();
		while (channel.getChannelId() != id) {
			channel = channels.next();
		}
		return channel;
	}

	private static Node child(Node parent, String tag) {
		List<Element> children = elements(parent, tag);
		return (children.isEmpty() ? null : children.get(0));
	}

	private static List<Element> elements(Node parent, String tag) {
		List<Element> elements = new ArrayList<Element>();
		NodeList children = parent.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (tag.equals(children.item(i).getNodeName())) {
				elements.add((Element) children.item(i));
			}
		}
		return elements;
	}

	private static int integer(Element element, String attribute) {
		return Integer.parseInt(element.getAttribute(attribute));
	}

	private static int[] integers(String values) {
		String[] parts = values.split(" ");
		int[] integers = new int[parts.length];
		for (int i = 0; i < parts.length; i++) {
			integers[i] = Integer.parseInt(parts[i]);
		}
		return integers;
	}
}
