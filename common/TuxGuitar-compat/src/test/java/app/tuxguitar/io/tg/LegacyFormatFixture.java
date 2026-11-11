package app.tuxguitar.io.tg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.base.TGSongReaderHandle;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.base.TGSongWriterHandle;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGColor;
import app.tuxguitar.song.models.TGDivisionType;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGNoteEffect;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.song.models.effects.TGEffectBend;
import app.tuxguitar.song.models.effects.TGEffectGrace;
import app.tuxguitar.song.models.effects.TGEffectHarmonic;
import app.tuxguitar.song.models.effects.TGEffectTremoloBar;
import app.tuxguitar.song.models.effects.TGEffectTremoloPicking;
import app.tuxguitar.song.models.effects.TGEffectTrill;

/**
 * Shared helpers for the legacy .tg (1.0 - 1.3) writer tests:
 * build songs, write them with a given writer, read them back with the
 * matching reader and describe what the reader sees.
 */
public final class LegacyFormatFixture {

	/** Start of the first measure of a song built by TGSongManager.newSong(). */
	public static final long MEASURE_START = TGDuration.QUARTER_TIME;

	private LegacyFormatFixture() {
	}

	public static byte[] write(TGSongWriter writer, TGFactory factory, TGSong song) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		TGSongWriterHandle handle = new TGSongWriterHandle();
		handle.setFactory(factory);
		handle.setSong(song);
		handle.setOutputStream(outputStream);
		writer.write(handle);
		return outputStream.toByteArray();
	}

	public static TGSong read(TGSongReader reader, TGFactory factory, byte[] bytes) throws Exception {
		TGSongReaderHandle handle = new TGSongReaderHandle();
		handle.setFactory(factory);
		handle.setInputStream(new ByteArrayInputStream(bytes));
		reader.read(handle);
		return handle.getSong();
	}

	public static String sha256(byte[] bytes) throws Exception {
		StringBuilder hex = new StringBuilder();
		for (byte b : MessageDigest.getInstance("SHA-256").digest(bytes)) {
			hex.append(String.format("%02x", b));
		}
		return hex.toString();
	}

	public static TGSong emptySong(TGFactory factory) {
		return new TGSongManager(factory).newSong();
	}

	public static TGMeasure firstMeasure(TGSong song) {
		return song.getTrack(0).getMeasure(0);
	}

	/** Adds a beat starting at the given tick offset from the measure start. */
	public static TGBeat addBeat(TGFactory factory, TGMeasure measure, long offset) {
		TGBeat beat = factory.newBeat();
		beat.setPreciseStart(TGDuration.toPreciseTime(MEASURE_START + offset));
		measure.addBeat(beat);
		return beat;
	}

	/** Adds a note to the given voice and sets that voice's duration. */
	public static TGNote addNote(TGFactory factory, TGVoice voice, int durationValue, int string, int value) {
		TGNote note = factory.newNote();
		note.setString(string);
		note.setValue(value);
		voice.getDuration().setValue(durationValue);
		voice.addNote(note);
		return note;
	}

	/** Marks the voice as a (non empty) rest of the given duration. */
	public static void makeRest(TGVoice voice, int durationValue) {
		voice.setEmpty(false);
		voice.getDuration().setValue(durationValue);
	}

	/**
	 * A 4/4 measure of eight eighth-note beats. Every note effect the legacy
	 * formats know about is used at least once, grouped so the TGNoteEffect
	 * setters do not cancel each other.
	 */
	public static TGSong songWithEveryEffect(TGFactory factory) {
		TGSong song = emptySong(factory);
		TGMeasure measure = firstMeasure(song);
		long eighth = TGDuration.QUARTER_TIME / 2;
		for (int i = 0; i < 8; i++) {
			TGBeat beat = addBeat(factory, measure, i * eighth);
			TGNote note = addNote(factory, beat.getVoice(0), TGDuration.EIGHTH, 1 + (i % 6), 3 + i);
			applyEffectGroup(factory, note.getEffect(), i);
		}
		TGNote plain = addNote(factory, measure.getBeat(7).getVoice(0), TGDuration.EIGHTH, 6, 0);
		plain.setVelocity(plain.getVelocity() - 15);
		return song;
	}

	private static void applyEffectGroup(TGFactory factory, TGNoteEffect effect, int group) {
		switch (group) {
		case 0:
			effect.setBend(bend(factory));
			effect.setVibrato(true);
			effect.setHarmonic(harmonic(factory, TGEffectHarmonic.TYPE_ARTIFICIAL, 12));
			effect.setGrace(grace(factory, true, true, TGEffectGrace.TRANSITION_SLIDE));
			effect.setGhostNote(true);
			effect.setPalmMute(true);
			effect.setStaccato(true);
			effect.setTapping(true);
			effect.setFadeIn(true);
			effect.setLetRing(true);
			break;
		case 1:
			effect.setTremoloBar(tremoloBar(factory));
			effect.setHarmonic(harmonic(factory, TGEffectHarmonic.TYPE_NATURAL, 0));
			effect.setAccentuatedNote(true);
			effect.setSlapping(true);
			break;
		case 2:
			effect.setTrill(trill(factory));
			effect.setHeavyAccentuatedNote(true);
			effect.setPopping(true);
			effect.setGrace(grace(factory, false, false, TGEffectGrace.TRANSITION_HAMMER));
			break;
		case 3:
			effect.setTremoloPicking(tremoloPicking(factory));
			effect.setHarmonic(harmonic(factory, TGEffectHarmonic.TYPE_TAPPED, 7));
			break;
		case 4:
			effect.setDeadNote(true);
			break;
		case 5:
			effect.setSlide(true);
			break;
		case 6:
			effect.setHammer(true);
			effect.setLetRing(true);
			break;
		default:
			effect.setLetRing(true);
			break;
		}
	}

	private static TGEffectBend bend(TGFactory factory) {
		TGEffectBend bend = factory.newEffectBend();
		bend.addPoint(0, 0);
		bend.addPoint(6, 4);
		bend.addPoint(12, 1);
		return bend;
	}

	private static TGEffectTremoloBar tremoloBar(TGFactory factory) {
		TGEffectTremoloBar tremoloBar = factory.newEffectTremoloBar();
		tremoloBar.addPoint(0, 0);
		tremoloBar.addPoint(6, -4);
		tremoloBar.addPoint(12, 2);
		return tremoloBar;
	}

	private static TGEffectHarmonic harmonic(TGFactory factory, int type, int data) {
		TGEffectHarmonic harmonic = factory.newEffectHarmonic();
		harmonic.setType(type);
		harmonic.setData(data);
		return harmonic;
	}

	private static TGEffectGrace grace(TGFactory factory, boolean dead, boolean onBeat, int transition) {
		TGEffectGrace grace = factory.newEffectGrace();
		grace.setFret(3);
		grace.setDuration(TGEffectGrace.DURATION_SIXTEENTH);
		grace.setDynamic(95);
		grace.setTransition(transition);
		grace.setDead(dead);
		grace.setOnBeat(onBeat);
		return grace;
	}

	private static TGEffectTrill trill(TGFactory factory) {
		TGEffectTrill trill = factory.newEffectTrill();
		trill.setFret(5);
		trill.getDuration().setValue(TGDuration.SIXTEENTH);
		return trill;
	}

	private static TGEffectTremoloPicking tremoloPicking(TGFactory factory) {
		TGEffectTremoloPicking tremoloPicking = factory.newEffectTremoloPicking();
		tremoloPicking.getDuration().setValue(TGDuration.THIRTY_SECOND);
		return tremoloPicking;
	}

	/** One line per note of voice 0: position, string, fret, velocity and every effect. */
	public static List<String> describeNotes(TGSong song, boolean withLetRing) {
		List<String> lines = new ArrayList<String>();
		TGMeasure measure = firstMeasure(song);
		for (int b = 0; b < measure.countBeats(); b++) {
			TGBeat beat = measure.getBeat(b);
			for (TGNote note : beat.getVoice(0).getNotes()) {
				lines.add("@" + beat.getStart() + " s" + note.getString() + " f" + note.getValue()
						+ " v" + note.getVelocity() + describeEffect(note.getEffect(), withLetRing));
			}
		}
		return lines;
	}

	public static String describeEffect(TGNoteEffect effect, boolean withLetRing) {
		StringBuilder text = new StringBuilder();
		if (effect.isBend()) {
			text.append(" bend");
			for (TGEffectBend.BendPoint point : effect.getBend().getPoints()) {
				text.append("[" + point.getPosition() + ":" + point.getValue() + "]");
			}
		}
		if (effect.isTremoloBar()) {
			text.append(" tremoloBar");
			for (TGEffectTremoloBar.TremoloBarPoint point : effect.getTremoloBar().getPoints()) {
				text.append("[" + point.getPosition() + ":" + point.getValue() + "]");
			}
		}
		if (effect.isHarmonic()) {
			TGEffectHarmonic harmonic = effect.getHarmonic();
			text.append(" harmonic" + harmonic.getType());
			if (!harmonic.isNatural()) {
				text.append(":" + harmonic.getData());
			}
		}
		if (effect.isGrace()) {
			TGEffectGrace grace = effect.getGrace();
			text.append(" grace[f" + grace.getFret() + " d" + grace.getDuration() + " dyn" + grace.getDynamic()
					+ " t" + grace.getTransition() + " dead=" + grace.isDead() + " onBeat=" + grace.isOnBeat() + "]");
		}
		if (effect.isTrill()) {
			text.append(" trill[f" + effect.getTrill().getFret() + " d" + effect.getTrill().getDuration().getValue() + "]");
		}
		if (effect.isTremoloPicking()) {
			text.append(" tremoloPicking[d" + effect.getTremoloPicking().getDuration().getValue() + "]");
		}
		appendFlag(text, effect.isVibrato(), "vibrato");
		appendFlag(text, effect.isDeadNote(), "dead");
		appendFlag(text, effect.isSlide(), "slide");
		appendFlag(text, effect.isHammer(), "hammer");
		appendFlag(text, effect.isGhostNote(), "ghost");
		appendFlag(text, effect.isAccentuatedNote(), "accent");
		appendFlag(text, effect.isHeavyAccentuatedNote(), "heavyAccent");
		appendFlag(text, effect.isPalmMute(), "palmMute");
		appendFlag(text, effect.isStaccato(), "staccato");
		appendFlag(text, effect.isTapping(), "tapping");
		appendFlag(text, effect.isSlapping(), "slapping");
		appendFlag(text, effect.isPopping(), "popping");
		appendFlag(text, effect.isFadeIn(), "fadeIn");
		appendFlag(text, withLetRing && effect.isLetRing(), "letRing");
		return text.toString();
	}

	private static void appendFlag(StringBuilder text, boolean on, String name) {
		if (on) {
			text.append(" " + name);
		}
	}

	/** One line per beat of the first measure: start, voice 0 duration and voice 0 notes. */
	public static List<String> describeBeats(TGSong song) {
		List<String> lines = new ArrayList<String>();
		TGMeasure measure = firstMeasure(song);
		for (int b = 0; b < measure.countBeats(); b++) {
			TGBeat beat = measure.getBeat(b);
			TGVoice voice = beat.getVoice(0);
			StringBuilder line = new StringBuilder("+" + (beat.getStart() - MEASURE_START) + " d" + voice.getDuration().getValue());
			if (voice.getDuration().isDotted()) {
				line.append(".");
			}
			if (!voice.getDuration().getDivision().isEqual(TGDivisionType.NORMAL)) {
				line.append("/" + voice.getDuration().getDivision().getEnters() + ":" + voice.getDuration().getDivision().getTimes());
			}
			if (voice.isRestVoice()) {
				line.append(" rest");
			}
			for (TGNote note : voice.getNotes()) {
				line.append(" s" + note.getString() + "f" + note.getValue());
			}
			lines.add(line.toString());
		}
		return lines;
	}

	/** One line per measure header: number, start, time signature, tempo, repeats, triplet feel and marker. */
	public static List<String> describeHeaders(TGSong song) {
		List<String> lines = new ArrayList<String>();
		for (int i = 0; i < song.countMeasureHeaders(); i++) {
			lines.add(describeHeader(song.getMeasureHeader(i)));
		}
		return lines;
	}

	private static String describeHeader(TGMeasureHeader header) {
		String text = "#" + header.getNumber() + " @" + header.getStart()
				+ " " + header.getTimeSignature().getNumerator() + "/" + header.getTimeSignature().getDenominator().getValue()
				+ " q" + header.getTempo().getQuarterValue() + " open=" + header.isRepeatOpen()
				+ " close=" + header.getRepeatClose() + " triplet=" + header.getTripletFeel();
		if (header.getRepeatAlternative() > 0) {
			text += " alternative=" + header.getRepeatAlternative();
		}
		if (header.hasMarker()) {
			TGColor color = header.getMarker().getColor();
			text += " marker[" + header.getMarker().getTitle() + " #" + header.getMarker().getMeasure()
					+ " " + color.getR() + "," + color.getG() + "," + color.getB() + "]";
		}
		return text;
	}
}
