package app.tuxguitar.graphics.control;

import static app.tuxguitar.graphics.control.LayoutHarness.SCORE;
import static app.tuxguitar.graphics.control.LayoutHarness.TAB;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import app.tuxguitar.graphics.control.painters.TGTripletFeelPainter;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.ui.resource.UIPainter;

/**
 * Pins how a measure paints a triplet feel change (TGMeasureImpl.paintTripletFeel): "written = played", the straight
 * pair of eighths or sixteenths then the swung one when a triplet feel starts, the other way round when it ends.
 * Nothing is painted where the feel does not change.
 *
 * Measure n gets the n-th feel; the symbols painted are read back in order: "=" for the " = " text, and
 * straight8, swung8, straight16, swung16 for the note pairs, a line per measure.
 */
public class TestTripletFeelPainting {

	private static final int NONE = TGMeasureHeader.TRIPLET_FEEL_NONE;
	private static final int EIGHTH = TGMeasureHeader.TRIPLET_FEEL_EIGHTH;
	private static final int SIXTEENTH = TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH;

	private final LayoutHarness harness = new LayoutHarness();

	// each symbol's painter calls, numbers left out: a symbol is the same wherever it is painted;
	// the longest first, as a straight pair starts like the swung one
	private static final Map<String, List<String>> SYMBOLS = new LinkedHashMap<String, List<String>>();
	static {
		SYMBOLS.put("swung16", shape(p -> TGTripletFeelPainter.paintTripletFeel16(p, 0, 0, 5f)));
		SYMBOLS.put("straight16", shape(p -> TGTripletFeelPainter.paintTripletFeelNone16(p, 0, 0, 5f)));
		SYMBOLS.put("swung8", shape(p -> TGTripletFeelPainter.paintTripletFeel8(p, 0, 0, 5f)));
		SYMBOLS.put("straight8", shape(p -> TGTripletFeelPainter.paintTripletFeelNone8(p, 0, 0, 5f)));
	}

	private static List<String> shape(Consumer<UIPainter> paint) {
		RecordingPainter painter = new RecordingPainter();
		paint.accept(painter);
		return withoutNumbers(painter.calls());
	}

	private static List<String> withoutNumbers(List<String> calls) {
		List<String> shapes = new ArrayList<String>();
		for (String call : calls) {
			shapes.add(call.replaceAll("-?\\d+(\\.\\d+)?(E-?\\d+)?", "#"));
		}
		return shapes;
	}

	private void feels(int... feels) {
		for (int i = 1; i < feels.length; i++) {
			harness.songManager.addNewMeasureBeforeEnd(harness.song);
		}
		for (int i = 0; i < feels.length; i++) {
			harness.song.getMeasureHeader(i).setTripletFeel(feels[i]);
		}
	}

	private String symbols(int style) {
		List<String> calls = harness.print(style).calls();
		List<String> shapes = withoutNumbers(calls);
		List<Float> measureXs = new ArrayList<Float>();
		for (int i = 1; i < harness.song.countMeasureHeaders(); i++) {
			measureXs.add(harness.measure(i).getPosX());
		}
		StringBuilder symbols = new StringBuilder();
		int measure = 0;
		for (int i = 0; i < calls.size(); i++) {
			String symbol = symbolAt(shapes, i);
			if (symbol != null) {
				float x = Float.parseFloat(calls.get(i).replaceAll("^\\D*?(-?\\d+(\\.\\d+)?).*$", "$1"));
				for (; measure < measureXs.size() && x >= measureXs.get(measure); measure++) {
					symbols.append("|");
				}
				symbols.append(symbols.length() == 0 || symbols.charAt(symbols.length() - 1) == '|' ? "" : " ").append(symbol);
				i += (symbol.equals("=") ? 0 : SYMBOLS.get(symbol).size() - 1);
			}
		}
		for (; measure < measureXs.size(); measure++) {
			symbols.append("|");
		}
		return symbols.toString();
	}

	private static String symbolAt(List<String> shapes, int index) {
		if (shapes.get(index).equals("drawString ' = ' # #")) {
			return "=";
		}
		for (Map.Entry<String, List<String>> symbol : SYMBOLS.entrySet()) {
			List<String> shape = symbol.getValue();
			if (index + shape.size() <= shapes.size() && shapes.subList(index, index + shape.size()).equals(shape)) {
				return symbol.getKey();
			}
		}
		return null;
	}

	private String equalsSign(int style) {
		harness.painter.clear();
		return harness.print(style).callsStartingWith("drawString ' = '").toString();
	}

	@Test
	public void theSymbolsCanBeToldApart() {
		assertEquals(4, new HashSet<List<String>>(SYMBOLS.values()).size());
	}

	@Test
	public void aTripletFeelStartsStraightThenSwung() {
		feels(EIGHTH, SIXTEENTH);
		assertEquals("= straight8 swung8|= straight16 swung16", symbols(SCORE));
	}

	@Test
	public void aTripletFeelEndsSwungThenStraight() {
		feels(EIGHTH, NONE, SIXTEENTH, NONE);
		assertEquals("= straight8 swung8|= swung8 straight8|= straight16 swung16|= swung16 straight16", symbols(SCORE));
	}

	@Test
	public void nothingIsPaintedWhereTheFeelDoesNotChange() {
		feels(NONE, EIGHTH, EIGHTH, NONE, NONE);
		assertEquals("|= straight8 swung8||= swung8 straight8|", symbols(SCORE));
	}

	@Test
	public void aboveTheScoreMiddleLinesElseAboveTheTablature() {
		feels(EIGHTH);
		assertEquals("[drawString ' = ' 96.0 59.25]", equalsSign(SCORE));
		assertEquals("[drawString ' = ' 96.0 79.25]", equalsSign(SCORE | TAB));
		assertEquals("[drawString ' = ' 96.0 79.25]", equalsSign(TAB));
	}

	@Test
	public void aboveASingleStringTablatureLeavingRoomForTheString() {
		TGTrack track = harness.song.getTrack(0);
		track.setStrings(new ArrayList<>(Arrays.asList(track.getString(1))));
		feels(EIGHTH);
		assertEquals("[drawString ' = ' 96.0 71.25]", equalsSign(TAB));
	}
}
