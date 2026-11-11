package app.tuxguitar.editor.action.composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTempo;
import app.tuxguitar.util.TGBeatRange;
import app.tuxguitar.util.TGContext;

public class TestChangeTempoRangeAction {

	// initial tempos of measures 1..5
	private static final int[] INITIAL_TEMPOS = { 100, 100, 100, 150, 100 };

	private TGContext context;
	private TGActionManager actionManager;
	private TGSongManager songManager;
	private TGActionContext actionContext;
	private TGSong song;
	private List<String> executions;

	private class RecordingChangeTempoAction extends TGChangeTempoAction {
		public RecordingChangeTempoAction(TGContext context) {
			super(context);
		}

		protected void processAction(TGActionContext context) {
			TGMeasureHeader header = (TGMeasureHeader) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
			TGTempo tempo = (TGTempo) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TEMPO);
			executions.add(header.getNumber() + ":" + tempo.getRawValue());
			super.processAction(context);
		}
	}

	@BeforeEach
	public void setUp() {
		this.context = new TGContext();
		this.actionManager = TGActionManager.getInstance(this.context);
		this.actionManager.mapAction(TGChangeTempoAction.NAME, new RecordingChangeTempoAction(this.context));
		this.actionManager.mapAction(TGChangeTempoRangeAction.NAME, new TGChangeTempoRangeAction(this.context));
		this.songManager = new TGSongManager();
		this.executions = new ArrayList<String>();
		this.song = createSong(TGDuration.QUARTER_TIME);
		this.actionContext = new TGActionContext() {
		};
		this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER, this.songManager);
		this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, this.song);
	}

	private TGSong createSong(long firstStart) {
		TGSong tgSong = this.songManager.newSong();
		tgSong.getMeasureHeader(0).setStart(firstStart);
		for (int i = 1; i < INITIAL_TEMPOS.length; i++) {
			this.songManager.addNewMeasureBeforeEnd(tgSong);
		}
		for (int i = 0; i < INITIAL_TEMPOS.length; i++) {
			tgSong.getMeasureHeader(i).getTempo().setQuarterValue(INITIAL_TEMPOS[i]);
		}
		return tgSong;
	}

	private void execute(int applyTo, int tempoValue) {
		execute(applyTo, tempoValue, TGDuration.QUARTER, false);
	}

	private void execute(int applyTo, int tempoValue, int tempoBase, boolean dotted) {
		this.actionContext.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_APPLY_TO, applyTo);
		this.actionContext.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_TEMPO, tempoValue);
		this.actionContext.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_TEMPO_BASE, tempoBase);
		this.actionContext.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_TEMPO_BASE_DOTTED, dotted);
		this.actionManager.execute(TGChangeTempoRangeAction.NAME, this.actionContext);
	}

	private void setHeader(int number) {
		this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, this.song.getMeasureHeader(number - 1));
	}

	private void setRange(TGBeatRange range) {
		this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE, range);
	}

	private TGBeat addBeat(TGMeasure measure, long offset) {
		TGBeat beat = this.songManager.getFactory().newBeat();
		beat.setStart(measure.getStart() + offset);
		measure.addBeat(beat);
		return beat;
	}

	private TGBeat addBeat(int measureNumber, long offset) {
		return addBeat(this.song.getTrack(0).getMeasure(measureNumber - 1), offset);
	}

	private void assertExecutions(String... expected) {
		assertEquals(Arrays.asList(expected), this.executions);
	}

	private void assertTempos(int... expected) {
		int[] actual = new int[this.song.countMeasureHeaders()];
		for (int i = 0; i < actual.length; i++) {
			actual[i] = this.song.getMeasureHeader(i).getTempo().getRawValue();
		}
		assertEquals(Arrays.toString(expected), Arrays.toString(actual));
	}

	private void assertUnchanged() {
		assertExecutions();
		assertTempos(INITIAL_TEMPOS);
	}

	@Test
	public void testTempoBelowMinimumIsIgnored() {
		execute(TGChangeTempoRangeAction.APPLY_TO_ALL, TGChangeTempoRangeAction.MIN_TEMPO - 1);
		assertUnchanged();
	}

	@Test
	public void testTempoAboveMaximumIsIgnored() {
		execute(TGChangeTempoRangeAction.APPLY_TO_ALL, TGChangeTempoRangeAction.MAX_TEMPO + 1);
		assertUnchanged();
	}

	@Test
	public void testMinimumTempoIsApplied() {
		execute(TGChangeTempoRangeAction.APPLY_TO_ALL, TGChangeTempoRangeAction.MIN_TEMPO);
		assertExecutions("1:1", "2:1", "3:1", "4:1", "5:1");
		assertTempos(1, 1, 1, 1, 1);
	}

	@Test
	public void testMaximumTempoIsApplied() {
		execute(TGChangeTempoRangeAction.APPLY_TO_ALL, TGChangeTempoRangeAction.MAX_TEMPO);
		assertExecutions("1:320", "2:320", "3:320", "4:320", "5:320");
		assertTempos(320, 320, 320, 320, 320);
	}

	@Test
	public void testApplyToAllIgnoresCurrentHeader() {
		setHeader(3);
		execute(TGChangeTempoRangeAction.APPLY_TO_ALL, 200);
		assertExecutions("1:200", "2:200", "3:200", "4:200", "5:200");
		assertTempos(200, 200, 200, 200, 200);
	}

	@Test
	public void testTempoBaseAndDottedArePassedThrough() {
		execute(TGChangeTempoRangeAction.APPLY_TO_ALL, 100, TGDuration.EIGHTH, true);
		for (int i = 0; i < this.song.countMeasureHeaders(); i++) {
			TGTempo tempo = this.song.getMeasureHeader(i).getTempo();
			assertEquals(100, tempo.getRawValue());
			assertEquals(TGDuration.EIGHTH, tempo.getBase());
			assertTrue(tempo.isDotted());
			assertEquals(75, tempo.getQuarterValue());
		}
	}

	@Test
	public void testApplyToEndFromMiddleHeader() {
		setHeader(2);
		execute(TGChangeTempoRangeAction.APPLY_TO_END, 200);
		assertExecutions("2:200", "3:200", "4:200", "5:200");
		assertTempos(100, 200, 200, 200, 200);
	}

	@Test
	public void testApplyToNextStopsAtFirstDifferentTempo() {
		setHeader(2);
		execute(TGChangeTempoRangeAction.APPLY_TO_NEXT, 200);
		assertExecutions("2:200", "3:200");
		assertTempos(100, 200, 200, 150, 100);
	}

	@Test
	public void testApplyToNextComparesWithStartHeaderOriginalTempo() {
		setHeader(4);
		execute(TGChangeTempoRangeAction.APPLY_TO_NEXT, 100);
		// header 5 had tempo 100 == new tempo, but differs from the original tempo of header 4 (150)
		assertExecutions("4:100");
		assertTempos(100, 100, 100, 100, 100);
	}

	@Test
	public void testApplyToNextUpToLastHeader() {
		setHeader(5);
		execute(TGChangeTempoRangeAction.APPLY_TO_NEXT, 200);
		assertExecutions("5:200");
		assertTempos(100, 100, 100, 150, 200);
	}

	@Test
	public void testApplyToSelectionSpanningMeasures() {
		setRange(new TGBeatRange(Arrays.asList(addBeat(2, TGDuration.QUARTER_TIME), addBeat(3, 0), addBeat(4, 2 * TGDuration.QUARTER_TIME))));
		execute(TGChangeTempoRangeAction.APPLY_TO_SELECTION, 200);
		// last execution refreshes the header following the selection, keeping its tempo
		assertExecutions("2:200", "3:200", "4:200", "5:100");
		assertTempos(100, 200, 200, 200, 100);
	}

	@Test
	public void testApplyToSelectionUpToLastMeasure() {
		setRange(new TGBeatRange(Arrays.asList(addBeat(4, 0), addBeat(5, TGDuration.QUARTER_TIME))));
		execute(TGChangeTempoRangeAction.APPLY_TO_SELECTION, 200);
		// the refresh is applied again to the last header, with its (new) tempo
		assertExecutions("4:200", "5:200", "5:200");
		assertTempos(100, 100, 100, 200, 200);
	}

	@Test
	public void testApplyToSelectionIgnoresCurrentHeader() {
		setHeader(1);
		setRange(TGBeatRange.single(addBeat(3, 0)));
		execute(TGChangeTempoRangeAction.APPLY_TO_SELECTION, 200);
		assertExecutions("3:200", "4:150");
		assertTempos(100, 100, 200, 150, 100);
	}

	@Test
	public void testApplyToSelectionWithNullRange() {
		setHeader(2);
		setRange(null);
		execute(TGChangeTempoRangeAction.APPLY_TO_SELECTION, 200);
		assertUnchanged();
	}

	@Test
	public void testApplyToSelectionWithEmptyRange() {
		setHeader(2);
		setRange(TGBeatRange.empty());
		execute(TGChangeTempoRangeAction.APPLY_TO_SELECTION, 200);
		assertUnchanged();
	}

	@Test
	public void testApplyToSelectionEndingInMeasureNumberZeroIsIgnored() {
		TGMeasure orphan = this.songManager.getFactory().newMeasure(this.songManager.getFactory().newHeader());
		assertEquals(0, orphan.getNumber());
		setRange(new TGBeatRange(Arrays.asList(addBeat(2, 0), addBeat(orphan, 0))));
		execute(TGChangeTempoRangeAction.APPLY_TO_SELECTION, 200);
		assertUnchanged();
	}

	@Test
	public void testApplyToSelectionStartingOutsideSongIsIgnored() {
		TGMeasureHeader outside = this.songManager.getFactory().newHeader();
		outside.setNumber(3);
		outside.setStart(100 * TGDuration.QUARTER_TIME);
		TGMeasure measure = this.songManager.getFactory().newMeasure(outside);
		setRange(TGBeatRange.single(addBeat(measure, 0)));
		execute(TGChangeTempoRangeAction.APPLY_TO_SELECTION, 200);
		assertUnchanged();
	}

	@Test
	public void testInvalidApplyToIsIgnored() {
		setHeader(2);
		setRange(TGBeatRange.single(addBeat(2, 0)));
		for (int applyTo : new int[] { 0, 5, 99 }) {
			execute(applyTo, 200);
			assertUnchanged();
		}
	}

	@Test
	public void testHeaderOutsideSongIsIgnored() {
		TGMeasureHeader outside = this.songManager.getFactory().newHeader();
		outside.setNumber(2);
		outside.setStart(100 * TGDuration.QUARTER_TIME);
		this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, outside);
		execute(TGChangeTempoRangeAction.APPLY_TO_END, 200);
		execute(TGChangeTempoRangeAction.APPLY_TO_NEXT, 200);
		assertUnchanged();
	}

	@Test
	public void testHeaderStartingAtTickZeroIsIgnored() {
		this.song = createSong(0);
		this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, this.song);
		assertFalse(this.song.getMeasureHeader(0).getStart() > 0);
		setHeader(1);
		execute(TGChangeTempoRangeAction.APPLY_TO_END, 200);
		execute(TGChangeTempoRangeAction.APPLY_TO_NEXT, 200);
		setRange(TGBeatRange.single(addBeat(1, 0)));
		execute(TGChangeTempoRangeAction.APPLY_TO_SELECTION, 200);
		assertUnchanged();
		// APPLY_TO_ALL always starts at tick QUARTER_TIME, which is in the first measure of this song
		execute(TGChangeTempoRangeAction.APPLY_TO_ALL, 200);
		assertExecutions("1:200", "2:200", "3:200", "4:200", "5:200");
	}

	@Test
	public void testSongWithoutMeasuresIsIgnored() {
		this.song = this.songManager.getFactory().newSong();
		this.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, this.song);
		execute(TGChangeTempoRangeAction.APPLY_TO_ALL, 200);
		assertExecutions();
	}
}
