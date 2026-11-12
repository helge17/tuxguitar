package app.tuxguitar.graphics.control;

import java.util.Iterator;

import app.tuxguitar.graphics.control.print.TGPrintController;
import app.tuxguitar.graphics.control.print.TGPrintDocument;
import app.tuxguitar.graphics.control.print.TGPrintLayout;
import app.tuxguitar.graphics.control.print.TGPrintSettings;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.managers.TGMeasureManager;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.song.models.effects.TGEffectBend;
import app.tuxguitar.ui.resource.UIInset;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.ui.resource.UISize;

/**
 * Headless rendering of a song built with the graphics factory: a new song (one 4/4 guitar track,
 * one measure), filled beat by beat, laid out and printed by a {@link TGPrintLayout} (at scale 1 by default)
 * onto a {@link RecordingPainter}.
 */
public class LayoutHarness implements TGPrintDocument {

	public static final int TAB = TGLayout.DISPLAY_TABLATURE;
	public static final int SCORE = TGLayout.DISPLAY_SCORE;
	public static final int COMPACT = TGLayout.DISPLAY_COMPACT;
	public static final int CHORDS = TGLayout.DISPLAY_CHORD_NAME;

	public static final long QUARTER = TGDuration.WHOLE_PRECISE_DURATION / 4;

	public final TGSongManager songManager = new TGSongManager(new TGFactoryImpl());
	public final TGFactory factory = songManager.getFactory();
	public final TGMeasureManager measureManager = songManager.getMeasureManager();
	public final TGSong song = songManager.newSong();
	public final RecordingPainter painter = new RecordingPainter();

	public final TGLayoutStyles styles = new TestLayoutStyles();

	public boolean paintable = true;
	public float scale = 1f;
	private TGPrintLayout layout;

	// for screen layouts: whether the layout is in play mode, and whether its controller reports beats as playing
	public boolean playMode = false;
	public boolean playing = true;

	// the numbers of the measures the controller reports as loop start, loop end and playing (-1: none)
	public int loopStart = -1;
	public int loopEnd = -1;
	public int playingMeasure = -1;

	public TGMeasureImpl measure() {
		return measure(0);
	}

	public TGMeasureImpl measure(int index) {
		return (TGMeasureImpl) song.getTrack(0).getMeasure(index);
	}

	public TGPrintLayout layout() {
		return this.layout;
	}

	// ---- song building: offsets in precise time from the measure start ----

	public TGBeatImpl beatAt(long offset) {
		return beatAt(0, offset);
	}

	public TGBeatImpl beatAt(int measureIndex, long offset) {
		TGMeasure measure = measure(measureIndex);
		long start = measure.getPreciseStart() + offset;
		TGBeat beat = measureManager.getBeatPrecise(measure, start);
		if (beat == null) {
			beat = factory.newBeat();
			beat.setPreciseStart(start);
			measureManager.addBeat(measure, beat);
			measureManager.orderBeats(measure);
		}
		return (TGBeatImpl) beat;
	}

	public TGVoiceImpl rest(long offset, int durationValue, int voiceIndex) {
		return rest(0, offset, durationValue, voiceIndex);
	}

	public TGVoiceImpl rest(int measureIndex, long offset, int durationValue, int voiceIndex) {
		TGVoiceImpl voice = beatAt(measureIndex, offset).getVoiceImpl(voiceIndex);
		voice.setEmpty(false);
		voice.getDuration().setValue(durationValue);
		return voice;
	}

	public TGNoteImpl note(long offset, int durationValue, int voiceIndex, int string, int fret) {
		return note(0, offset, durationValue, voiceIndex, string, fret);
	}

	public TGNoteImpl note(int measureIndex, long offset, int durationValue, int voiceIndex, int string, int fret) {
		TGVoiceImpl voice = rest(measureIndex, offset, durationValue, voiceIndex);
		TGNote note = factory.newNote();
		note.setString(string);
		note.setValue(fret);
		voice.addNote(note);
		return (TGNoteImpl) note;
	}

	// quarter notes in voice 0, on string 3 fret 5, at each of the given quarter positions
	public void quarterNotes(int... positions) {
		for (int position : positions) {
			note(position * QUARTER, TGDuration.QUARTER, 0, 3, 5);
		}
	}

	// the song plays on the percussion channel; a percussion track's strings are not tuned: the fret is the drum
	public void percussion() {
		Iterator<TGChannel> channels = song.getChannels();
		while (channels.hasNext()) {
			channels.next().setBank(TGChannel.DEFAULT_PERCUSSION_BANK);
		}
		for (TGString string : song.getTrack(0).getStrings()) {
			string.setValue(0);
		}
	}

	// bend through the given values (in quarter tones), evenly spread over the note
	public static void bend(TGNoteImpl note, TGFactory factory, int... values) {
		TGEffectBend bend = factory.newEffectBend();
		for (int i = 0; i < values.length; i++) {
			bend.addPoint((i * TGEffectBend.MAX_POSITION_LENGTH) / Math.max(1, values.length - 1), values[i]);
		}
		note.getEffect().setBend(bend);
	}

	public void bend(TGNoteImpl note, int... values) {
		bend(note, factory, values);
	}

	public TGVoice voice(int beatIndex, int voiceIndex) {
		return measure().getBeat(beatIndex).getVoice(voiceIndex);
	}

	// ---- rendering ----

	/** Lays the song out (create + update of every measure), as the editor does before painting. */
	public TGPrintLayout update(int style) {
		TGController controller = new HarnessController();
		TGPrintSettings settings = new TGPrintSettings(TGPrintSettings.ALL_TRACKS, 1, song.countMeasureHeaders(), style);
		this.layout = new TGPrintLayout(controller, settings);
		this.layout.loadStyles(this.scale);
		this.layout.updateSong();
		return this.layout;
	}

	/**
	 * Lays the song out offline on a vertical screen layout and paints it; the layout is then in play mode when
	 * {@link #playMode} is set, and its controller reports every beat as playing while {@link #playing} is set.
	 */
	public TGLayout screen(int style) {
		TGController controller = new HarnessController() {
			public boolean isRunning(TGBeat played) {
				return playing;
			}
		};
		TGLayout screen = new TGLayoutVertical(controller, style) {
			public boolean isPlayModeEnabled() {
				return playMode;
			}
		};
		boolean painting = this.playMode;
		this.playMode = false;
		screen.loadStyles(this.scale);
		screen.updateSong();
		screen.paintSong(this.painter, new UIRectangle(0, 0, 800, 2000), 0, 0);
		this.playMode = painting;
		return screen;
	}

	/** Lays the song out, then prints it; returns the painter holding every drawing call. */
	public RecordingPainter print(int style) {
		update(style).makeDocument(this);
		return this.painter;
	}

	/** A print controller reporting the harness's loop and playing measures. */
	private class HarnessController extends TGPrintController {
		HarnessController() {
			super(song, songManager, new FakeResourceFactory(), styles);
		}

		public boolean isLoopSHeader(TGMeasureHeader header) {
			return (header.getNumber() == loopStart);
		}

		public boolean isLoopEHeader(TGMeasureHeader header) {
			return (header.getNumber() == loopEnd);
		}

		public boolean isRunning(TGMeasure measure) {
			return (measure.getNumber() == playingMeasure);
		}
	}

	// ---- TGPrintDocument: one 550x800 page with 20 margins ----

	public void start() { }
	public void finish() { }
	public void pageStart() { }
	public void pageFinish() { }
	public boolean isPaintable(int page) { return this.paintable; }
	public boolean isTransparentBackground() { return true; }
	public UIPainter getPainter() { return this.painter; }
	public UISize getSize() { return new UISize(550, 800); }
	public UIInset getMargins() { return new UIInset(20, 20, 20, 20); }

	/** The desktop print defaults (PrintLayoutStyles), without fonts or colors. */
	static class TestLayoutStyles extends TGLayoutStyles {
		TestLayoutStyles() {
			setBufferEnabled(false);
			setFirstMeasureSpacing(15);
			setMinBufferSeparator(15);
			setMinTopSpacing(20);
			setMinScoreTabSpacing(15);
			setScoreLineSpacing(7);
			setFirstTrackSpacing(5);
			setTrackSpacing(5);
			setStringSpacing(8);
			setFirstNoteSpacing(10);
			setMeasureLeftSpacing(15);
			setMeasureRightSpacing(15);
			setClefSpacing(30);
			setKeySignatureSpacing(15);
			setTimeSignatureSpacing(15);
			setChordFretIndexSpacing(8);
			setChordStringSpacing(4);
			setChordFretSpacing(5);
			setChordNoteSize(3);
			setChordLineWidth(0);
			setRepeatEndingSpacing(20);
			setTextSpacing(15);
			setMarkerSpacing(15);
			setDivisionTypeSpacing(10);
			setEffectSpacing(8);
			setBendSpacing(12);
			setLineWidths(new float[] {0, 1, 2, 3, 4, 5});
			setDurationWidths(new float[] {25f, 22f, 20f, 18f, 16f});
		}
	}
}
