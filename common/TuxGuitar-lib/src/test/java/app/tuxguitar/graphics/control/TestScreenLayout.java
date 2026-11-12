package app.tuxguitar.graphics.control;

import static app.tuxguitar.graphics.control.LayoutHarness.SCORE;
import static app.tuxguitar.graphics.control.LayoutHarness.TAB;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import app.tuxguitar.graphics.control.print.TGPrintController;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.ui.resource.UIRectangle;

/**
 * Pins how the screen layouts (TGLayoutVertical / TGLayoutHorizontal .paintSong) place and paint a song of
 * two tracks and 8 measures, the first measure of track 1 holding a note above and a note below the staff.
 *
 * Description: "size=WxH lines=<line of each measure> scroll=<ms per measure> calls=<drawing calls>",
 * then one row per staff line: "t<track> y=<posY> h=<track height> x=<posX of each measure, * when not painted>".
 */
public class TestScreenLayout {

	private final LayoutHarness harness = new LayoutHarness();
	private int trackSelection = -1;

	public TestScreenLayout() {
		harness.songManager.addTrack(harness.song);
		for (int i = 1; i < 8; i++) {
			harness.songManager.addNewMeasureBeforeEnd(harness.song);
		}
		harness.note(0, TGDuration.QUARTER, 0, 1, 20);
		harness.note(LayoutHarness.QUARTER, TGDuration.QUARTER, 0, 6, 0);
	}

	private TGLayout paint(TGLayout layout, UIRectangle clientArea) {
		layout.loadStyles(1f);
		layout.updateSong();
		harness.painter.clear();
		layout.paintSong(harness.painter, clientArea, 0, 0);
		return layout;
	}

	private TGController controller() {
		return new TGPrintController(harness.song, harness.songManager, new FakeResourceFactory(), new LayoutHarness.TestLayoutStyles()) {
			public int getTrackSelection() {
				return trackSelection;
			}
		};
	}

	private String vertical(int style, UIRectangle clientArea) {
		return describe(paint(new TGLayoutVertical(controller(), style), clientArea));
	}

	private String horizontal(int style, UIRectangle clientArea) {
		return describe(paint(new TGLayoutHorizontal(controller(), style), clientArea));
	}

	private String describe(TGLayout layout) {
		StringBuilder description = new StringBuilder();
		description.append("size=").append(layout.getWidth()).append('x').append(layout.getHeight());
		description.append(" lines=").append(layout.measureLines);
		description.append(" scroll=").append(layout.timeToNextScrollMs);
		description.append(" calls=").append(harness.painter.calls().size()).append('\n');

		Map<String, StringBuilder> rows = new LinkedHashMap<String, StringBuilder>();
		Iterator<TGTrack> tracks = harness.song.getTracks();
		while (tracks.hasNext()) {
			TGTrack track = tracks.next();
			if (trackSelection < 0 || track.getNumber() == trackSelection) {
				Iterator<TGMeasure> measures = track.getMeasures();
				while (measures.hasNext()) {
					TGMeasureImpl measure = (TGMeasureImpl) measures.next();
					TGLayout.TrackPosition position = layout.getTrackPositionAt(measure.getPosY() + 1);
					String row = "t" + track.getNumber() + " y=" + measure.getPosY() + " h=" + (position == null ? "-" : position.getHeight()) + " x=";
					rows.computeIfAbsent(row, key -> new StringBuilder(key)).append(measure.getPosX()).append(measure.isOutOfBounds() ? "* " : " ");
				}
			}
		}
		for (StringBuilder row : rows.values()) {
			description.append(row.toString().trim()).append('\n');
		}
		return description.toString();
	}

	private static UIRectangle area(float x, float y, float width, float height) {
		return new UIRectangle(x, y, width, height);
	}

	// ---- vertical ----

	@Test
	public void verticalWrapsMeasuresIntoLinesOfAllTracks() {
		assertEquals(""
			+ "size=400.0x603.0 lines=[1, 1, 1, 1, 2, 2, 2, 2, 0, 0, 0] scroll=[8000, 6000, 4000, 2000, 0, 0, 0, 0] calls=1872\n"
			+ "t1 y=5.0 h=187.0 x=15.0 224.0 307.0\n"
			+ "t1 y=0.0 h=187.0 x=0.0*\n"
			+ "t1 y=333.0 h=129.0 x=15.0 80.0 145.0 210.0\n"
			+ "t2 y=198.0 h=129.0 x=15.0 162.0 238.0 314.0\n"
			+ "t2 y=468.0 h=129.0 x=15.0 80.0 145.0 210.0\n"
			, vertical(SCORE | TAB, area(0, 0, 400, 2000)));
	}

	@Test
	public void verticalShowsOnlyTheSelectedTrack() {
		trackSelection = 2;
		assertEquals(""
			+ "size=400.0x275.0 lines=[0, 0, 0, 0, 1, 1, 1, 1] scroll=[8000, 6000, 4000, 2000, 0, 0, 0, 0] calls=894\n"
			+ "t2 y=5.0 h=129.0 x=15.0 162.0 238.0 314.0\n"
			+ "t2 y=140.0 h=129.0 x=15.0 80.0 145.0 210.0\n"
			, vertical(SCORE | TAB, area(0, 0, 400, 2000)));
	}

	@Test
	public void verticalBreaksTheLineWhereAMeasureAsksForIt() {
		harness.song.getMeasureHeader(2).setLineBreak(true);
		assertEquals(""
			+ "size=400.0x527.0 lines=[1, 1, 2, 2, 2, 2, 2, 4, 0, 0] scroll=[4000, 2000, 10000, 8000, 6000, 4000, 2000, 0] calls=1212\n"
			+ "t1 y=5.0 h=91.0 x=15.0 170.0\n"
			+ "t1 y=187.0 h=79.0 x=15.0 90.0 165.0 240.0 315.0\n"
			+ "t1 y=357.0 h=79.0 x=15.0\n"
			+ "t2 y=102.0 h=79.0 x=15.0 115.0\n"
			+ "t2 y=272.0 h=79.0 x=15.0 90.0 165.0 240.0 315.0\n"
			+ "t2 y=442.0 h=79.0 x=15.0\n"
			, vertical(TAB, area(0, 0, 400, 2000)));
	}

	@Test
	public void verticalPaintsOnlyTheLinesInTheClientArea() {
		assertEquals(""
			+ "size=400.0x603.0 lines=[1, 1, 1, 1, 2, 2, 2, 2, 0, 0, 0] scroll=[8000, 6000, 4000, 2000, 0, 0, 0, 0] calls=1248\n"
			+ "t1 y=5.0 h=187.0 x=15.0* 224.0* 307.0*\n"
			+ "t1 y=0.0 h=187.0 x=0.0*\n"
			+ "t1 y=333.0 h=129.0 x=15.0 80.0 145.0 210.0\n"
			+ "t2 y=198.0 h=129.0 x=15.0 162.0 238.0 314.0\n"
			+ "t2 y=468.0 h=129.0 x=15.0 80.0 145.0 210.0\n"
			, vertical(SCORE | TAB, area(0, 300, 400, 100)));
	}

	@Test
	public void verticalPaintsOnlyTheMeasuresInAScrolledClientArea() {
		assertEquals(""
			+ "size=200.0x697.0 lines=[1, 1, 2, 2, 4, 4, 6, 6, 0] scroll=[4000, 2000, 4000, 2000, 4000, 2000, 0, 0] calls=553\n"
			+ "t1 y=5.0 h=91.0 x=15.0\n"
			+ "t1 y=0.0 h=91.0 x=0.0*\n"
			+ "t1 y=187.0 h=79.0 x=15.0* 102.5\n"
			+ "t1 y=357.0 h=79.0 x=15.0* 102.5\n"
			+ "t1 y=527.0 h=79.0 x=15.0* 80.0*\n"
			+ "t2 y=102.0 h=79.0 x=15.0* 120.0\n"
			+ "t2 y=272.0 h=79.0 x=15.0* 102.5\n"
			+ "t2 y=442.0 h=79.0 x=15.0* 102.5\n"
			+ "t2 y=612.0 h=79.0 x=15.0* 80.0*\n"
			, vertical(TAB, area(150, 0, 200, 2000)));
		assertEquals(""
			+ "size=100.0x1377.0 lines=[1, 2, 4, 6, 8, 10, 12, 14, 0] scroll=[2000, 2000, 2000, 2000, 2000, 2000, 2000, 0] calls=0\n"
			+ "t1 y=5.0 h=91.0 x=15.0*\n"
			+ "t1 y=187.0 h=79.0 x=15.0*\n"
			+ "t1 y=357.0 h=79.0 x=15.0*\n"
			+ "t1 y=527.0 h=79.0 x=15.0*\n"
			+ "t1 y=697.0 h=79.0 x=15.0*\n"
			+ "t1 y=867.0 h=79.0 x=15.0*\n"
			+ "t1 y=1037.0 h=79.0 x=15.0*\n"
			+ "t1 y=1207.0 h=79.0 x=15.0*\n"
			+ "t2 y=102.0 h=79.0 x=15.0*\n"
			+ "t2 y=272.0 h=79.0 x=15.0*\n"
			+ "t2 y=442.0 h=79.0 x=15.0*\n"
			+ "t2 y=612.0 h=79.0 x=15.0*\n"
			+ "t2 y=782.0 h=79.0 x=15.0*\n"
			+ "t2 y=952.0 h=79.0 x=15.0*\n"
			+ "t2 y=1122.0 h=79.0 x=15.0*\n"
			+ "t2 y=1292.0 h=79.0 x=15.0*\n"
			, vertical(TAB, area(380, 0, 100, 2000)));
	}

	@Test
	public void verticalSizesTheStaffForEachStyle() {
		assertEquals(""
			+ "size=400.0x486.0 lines=[1, 1, 1, 1, 2, 2, 2, 2, 0, 0, 0] scroll=[8000, 6000, 4000, 2000, 0, 0, 0, 0] calls=1405\n"
			+ "t1 y=5.0 h=130.0 x=15.0 224.0 307.0\n"
			+ "t1 y=0.0 h=130.0 x=0.0*\n"
			+ "t1 y=256.0 h=109.0 x=15.0 80.0 145.0 210.0\n"
			+ "t2 y=141.0 h=109.0 x=15.0 162.0 238.0 314.0\n"
			+ "t2 y=371.0 h=109.0 x=15.0 80.0 145.0 210.0\n"
			, vertical(SCORE, area(0, 0, 400, 2000)));
		assertEquals(""
			+ "size=400.0x357.0 lines=[1, 1, 1, 1, 1, 2, 2, 2, 0, 0, 0, 0] scroll=[10000, 8000, 6000, 4000, 2000, 0, 0, 0] calls=1147\n"
			+ "t1 y=5.0 h=91.0 x=15.0 176.25 247.5 318.75\n"
			+ "t1 y=256.0 h=79.0 x=15.0*\n"
			+ "t1 y=187.0 h=79.0 x=15.0 80.0 145.0\n"
			+ "t2 y=102.0 h=79.0 x=15.0 118.0 186.0 254.0 322.0\n"
			+ "t2 y=272.0 h=79.0 x=15.0 80.0 145.0\n"
			, vertical(TAB, area(0, 0, 400, 2000)));
	}

	@Test
	public void verticalPutsAWideSongOnOneLinePerTrack() {
		assertEquals(""
			+ "size=635.0x187.0 lines=[1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0] scroll=[0, 0, 0, 0, 0, 0, 0, 0] calls=1188\n"
			+ "t1 y=5.0 h=91.0 x=15.0 170.0 235.0 300.0 365.0 430.0 495.0 560.0\n"
			+ "t2 y=102.0 h=79.0 x=15.0 115.0 180.0 245.0 310.0 375.0 440.0 505.0\n"
			, vertical(TAB, area(0, 0, 2000, 2000)));
	}

	// ---- horizontal ----

	@Test
	public void horizontalPutsEachTrackOnOneLine() {
		assertEquals(""
			+ "size=676.0x333.0 lines=[] scroll=[2000, 2000, 2000, 2000, 2000, 2000, 2000, 2000] calls=1768\n"
			+ "t1 y=5.0 h=187.0 x=15.0 206.0 271.0 336.0 401.0 466.0 531.0 596.0*\n"
			+ "t2 y=198.0 h=129.0 x=15.0 151.0 216.0 281.0 346.0 411.0 476.0 541.0\n"
			, horizontal(SCORE | TAB, area(0, 0, 400, 2000)));
	}

	@Test
	public void horizontalShowsOnlyTheSelectedTrack() {
		trackSelection = 2;
		assertEquals(""
			+ "size=621.0x140.0 lines=[] scroll=[2000, 2000, 2000, 2000, 2000, 2000, 2000, 2000] calls=852\n"
			+ "t2 y=5.0 h=129.0 x=15.0 151.0 216.0 281.0 346.0 411.0 476.0 541.0\n"
			, horizontal(SCORE | TAB, area(0, 0, 400, 2000)));
	}

	@Test
	public void horizontalDoesNotWidenASongThatFits() {
		assertEquals(""
			+ "size=625.0x187.0 lines=[] scroll=[2000, 2000, 2000, 2000, 2000, 2000, 2000, 2000] calls=1188\n"
			+ "t1 y=5.0 h=91.0 x=15.0 170.0 235.0 300.0 365.0 430.0 495.0 560.0\n"
			+ "t2 y=102.0 h=79.0 x=15.0 115.0 180.0 245.0 310.0 375.0 440.0 505.0\n"
			, horizontal(TAB, area(0, 0, 2000, 2000)));
	}

	@Test
	public void horizontalPaintsOnlyWhatIsInTheClientArea() {
		assertEquals(""
			+ "size=676.0x333.0 lines=[] scroll=[2000, 2000, 2000, 2000, 2000, 2000, 2000, 2000] calls=420\n"
			+ "t1 y=5.0 h=187.0 x=15.0* 206.0* 271.0* 336.0* 401.0* 466.0 531.0 596.0\n"
			+ "t2 y=198.0 h=129.0 x=15.0* 151.0* 216.0* 281.0* 346.0* 411.0* 476.0 541.0\n"
			, horizontal(SCORE | TAB, area(600, 0, 200, 2000)));
		assertEquals(""
			+ "size=676.0x333.0 lines=[] scroll=[2000, 2000, 2000, 2000, 2000, 2000, 2000, 2000] calls=852\n"
			+ "t1 y=5.0 h=187.0 x=15.0* 206.0* 271.0* 336.0* 401.0* 466.0* 531.0* 596.0*\n"
			+ "t2 y=198.0 h=129.0 x=15.0 151.0 216.0 281.0 346.0 411.0 476.0 541.0\n"
			, horizontal(SCORE | TAB, area(0, 200, 400, 50)));
	}

	@Test
	public void horizontalSizesTheStaffForEachStyle() {
		assertEquals(""
			+ "size=676.0x256.0 lines=[] scroll=[2000, 2000, 2000, 2000, 2000, 2000, 2000, 2000] calls=1353\n"
			+ "t1 y=5.0 h=130.0 x=15.0 206.0 271.0 336.0 401.0 466.0 531.0 596.0*\n"
			+ "t2 y=141.0 h=109.0 x=15.0 151.0 216.0 281.0 346.0 411.0 476.0 541.0\n"
			, horizontal(SCORE, area(0, 0, 400, 2000)));
		assertEquals(""
			+ "size=640.0x187.0 lines=[] scroll=[2000, 2000, 2000, 2000, 2000, 2000, 2000, 2000] calls=1154\n"
			+ "t1 y=5.0 h=91.0 x=15.0 170.0 235.0 300.0 365.0 430.0 495.0 560.0\n"
			+ "t2 y=102.0 h=79.0 x=15.0 115.0 180.0 245.0 310.0 375.0 440.0 505.0\n"
			, horizontal(TAB, area(0, 0, 400, 2000)));
	}

	// ---- what the scrolling editor asks the layout about a measure ----

	private TGMeasureImpl firstMeasure() {
		return (TGMeasureImpl) harness.song.getTrack(0).getMeasure(0);
	}

	private String visibility(TGLayout layout, float fromX, float fromY, UIRectangle area) {
		layout.paintSong(harness.painter, area(0, 0, 400, 2000), fromX, fromY);
		TGMeasureImpl measure = firstMeasure();
		return measure.getPosX() + "," + measure.getPosY() + " " + measure.getWidth(layout) + "x" + measure.getTs().getSize()
			+ (layout.isFullyVisible(measure, area) ? " visible" : " hidden");
	}

	@Test
	public void aMeasureIsFullyVisibleWhenItLiesInsideTheArea() {
		TGLayout layout = paint(new TGLayoutHorizontal(controller(), TAB), area(0, 0, 400, 2000));

		assertEquals("15.0,5.0 155.0x91.0 visible", visibility(layout, 0, 0, area(0, 0, 170, 96)));
		assertEquals("15.0,5.0 155.0x91.0 hidden", visibility(layout, 0, 0, area(0, 0, 169, 96)));
		assertEquals("15.0,5.0 155.0x91.0 hidden", visibility(layout, 0, 0, area(0, 0, 170, 95)));
		assertEquals("0.0,0.0 155.0x91.0 visible", visibility(layout, -15, -5, area(0, 0, 170, 96)));
		assertEquals("-1.0,0.0 155.0x91.0 hidden", visibility(layout, -16, -5, area(0, 0, 170, 96)));
		assertEquals("0.0,-1.0 155.0x91.0 hidden", visibility(layout, -15, -6, area(0, 0, 170, 96)));
	}

	@Test
	public void aMeasureNotYetLaidOutIsNeverFullyVisible() {
		TGLayout layout = new TGLayoutVertical(controller(), TAB);

		assertEquals(false, layout.isFullyVisible(firstMeasure(), area(0, 0, 2000, 2000)));
		assertEquals(null, layout.getMeasureScrollableSize(firstMeasure()));
	}

	@Test
	public void verticalScrollsByTheStaffHeightAndHorizontalByTheMeasureWidth() {
		TGLayout vertical = paint(new TGLayoutVertical(controller(), TAB), area(0, 0, 400, 2000));
		assertEquals(Float.valueOf(91f), vertical.getMeasureScrollableSize(firstMeasure()));

		TGLayout horizontal = paint(new TGLayoutHorizontal(controller(), TAB), area(0, 0, 400, 2000));
		assertEquals(Float.valueOf(155f), horizontal.getMeasureScrollableSize(firstMeasure()));

		assertEquals(null, harness.update(TAB).getMeasureScrollableSize(firstMeasure()));
	}

	@Test
	public void theLineNumberOfAMeasureIsKnownOnlyInTheVerticalLayout() {
		TGLayout vertical = paint(new TGLayoutVertical(controller(), TAB), area(0, 0, 400, 2000));
		assertEquals("1 1 2 -1 -1", vertical.getMeasureLineNumber(0) + " " + vertical.getMeasureLineNumber(4) + " "
			+ vertical.getMeasureLineNumber(5) + " " + vertical.getMeasureLineNumber(-1) + " " + vertical.getMeasureLineNumber(12));

		TGLayout horizontal = paint(new TGLayoutHorizontal(controller(), TAB), area(0, 0, 400, 2000));
		assertEquals(-1, horizontal.getMeasureLineNumber(0));
	}

	private float checkScale(int style, float scoreLineSpacing, float stringSpacing) {
		TGLayoutStyles styles = new LayoutHarness.TestLayoutStyles();
		styles.setScoreLineSpacing(scoreLineSpacing);
		styles.setStringSpacing(stringSpacing);
		TGLayout layout = new TGLayoutVertical(new TGPrintController(harness.song, harness.songManager, new FakeResourceFactory(), styles), style);
		layout.loadStyles(1f);
		return layout.checkScale();
	}

	@Test
	public void theScaleCheckFollowsTheTallerOfScoreAndTablatureSpacing() {
		assertEquals(0.875f, checkScale(SCORE | TAB, 7, 8));
		assertEquals(0.875f, checkScale(SCORE, 7, 8));
		assertEquals(0.8f, checkScale(TAB, 7, 8));
		// a layout showing neither staff shows the tablature
		assertEquals(0.8f, checkScale(LayoutHarness.CHORDS, 7, 8));
	}

	@Test
	public void theScaleCheckIgnoresTheSpacingOfAHiddenStaff() {
		assertEquals(0.08f, checkScale(SCORE | TAB, 0.4f, 0.8f));
		assertEquals(0.05f, checkScale(SCORE, 0.4f, 0.8f));
		assertEquals(0.08f, checkScale(TAB, 0.4f, 0.8f));
	}
}
