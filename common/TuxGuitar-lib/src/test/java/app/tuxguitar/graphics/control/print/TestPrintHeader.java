package app.tuxguitar.graphics.control.print;

import static app.tuxguitar.graphics.control.LayoutHarness.SCORE;
import static app.tuxguitar.graphics.control.LayoutHarness.TAB;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.graphics.control.LayoutHarness;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;

/**
 * Pins the page header of a printed track (TGPrintLayout.paintHeader): which song fields are printed,
 * with which font, where (centred or right-aligned on a 550x800 page with 20 margins, 4 units per
 * character), and the height it takes ("offset=", before the painter calls).
 * Messages are not loaded, so labels print as their message keys.
 */
public class TestPrintHeader {

	private final LayoutHarness harness = new LayoutHarness();
	private final TGSong song = harness.song;
	private final TGTrack track = song.getTrack(0);

	@Test
	public void everyFieldIsPrintedInOrder() {
		fillSong();

		assertEquals(""
			+ "offset=150.0\n"
			+ "setFont font(16.0,bold)\n"
			+ "drawString 'Song' 266.5 22.0\n"
			+ "setFont font(12.0,bold)\n"
			+ "drawString 'Artist' 262.5 42.0\n"
			+ "setFont font(10.0,bold)\n"
			+ "drawString 'composition.album Album (1999)' 214.5 62.0\n"
			+ "setFont font(8.0,bold)\n"
			+ "drawString 'track Guitar' 250.5 82.0\n"
			+ "setFont font(8.0,bold)\n"
			+ "drawString 'composition.author Author' 429.0 102.0\n"
			+ "setFont font(8.0,bold)\n"
			+ "drawString 'composition.copyright Copyright' 405.0 112.0\n"
			+ "setFont font(8.0,bold)\n"
			+ "drawString 'composition.transcriber Transcriber' 389.0 122.0\n"
			+ "setFont font(8.0,bold)\n"
			+ "drawString 'composition.writer Writer' 429.0 132.0\n"
			+ "setFont font(8.0,bold)\n"
			+ "drawString 'tuning: E A D G B E' 20.0 152.0"
			, header(TAB));
	}

	@Test
	public void anAlbumWithoutDateIsPrintedAlone() {
		song.setAlbum("Album");

		assertEquals(""
			+ "offset=60.0\n"
			+ "setFont font(10.0,bold)\n"
			+ "drawString 'composition.album Album ' 226.5 22.0\n"
			+ "setFont font(8.0,bold)\n"
			+ "drawString 'track track 1' 248.5 42.0\n"
			+ "setFont font(8.0,bold)\n"
			+ "drawString 'tuning: E A D G B E' 20.0 62.0"
			, header(TAB));
	}

	@Test
	public void aDateWithoutAlbumIsPrintedAlone() {
		song.setDate("1999");

		assertEquals(""
			+ "offset=60.0\n"
			+ "setFont font(10.0,bold)\n"
			+ "drawString '(1999)' 262.5 22.0\n"
			+ "setFont font(8.0,bold)\n"
			+ "drawString 'track track 1' 248.5 42.0\n"
			+ "setFont font(8.0,bold)\n"
			+ "drawString 'tuning: E A D G B E' 20.0 62.0"
			, header(TAB));
	}

	@Test
	public void anEmptySongPrintsOnlyTheTrackAndTuning() {
		assertEquals(""
			+ "offset=40.0\n"
			+ "setFont font(8.0,bold)\n"
			+ "drawString 'track track 1' 248.5 22.0\n"
			+ "setFont font(8.0,bold)\n"
			+ "drawString 'tuning: E A D G B E' 20.0 42.0"
			, header(TAB));
	}

	@Test
	public void aNamelessTrackOnAScoreOnlyPageHasNoHeader() {
		track.setName("");

		assertEquals(""
			+ "offset=0.0\n"
			, header(SCORE));
	}

	@Test
	public void aTrackWithoutStringsHasNoTuningLine() {
		track.setName("");
		track.getStrings().clear();

		assertEquals(""
			+ "offset=0.0\n"
			, header(TAB));
	}

	@Test
	public void aPercussionTrackHasNoTuningLine() {
		track.setName("");
		TGChannel channel = song.getChannels().next();

		harness.print(TAB);
		channel.setBank(TGChannel.DEFAULT_PERCUSSION_BANK);

		assertEquals(""
			+ "offset=0.0\n"
			, paintHeader());
	}

	@Test
	public void lineSpacingFollowsTheScale() {
		song.setName("Song");
		song.setAuthor("Author");
		harness.scale = 2f;

		assertEquals(""
			+ "offset=140.0\n"
			+ "setFont font(32.0,bold)\n"
			+ "drawString 'Song' 266.0 22.0\n"
			+ "setFont font(16.0,bold)\n"
			+ "drawString 'track track 1' 248.0 62.0\n"
			+ "setFont font(16.0,bold)\n"
			+ "drawString 'composition.author Author' 428.0 102.0\n"
			+ "setFont font(16.0,bold)\n"
			+ "drawString 'tuning: E A D G B E' 20.0 122.0"
			, header(TAB));
	}

	@Test
	public void nothingIsPrintedOnAPageOutsideTheDocument() {
		fillSong();
		harness.print(TAB);
		harness.paintable = false;

		assertEquals(""
			+ "offset=0.0\n"
			, paintHeader());
	}

	// ---- helpers ----

	private void fillSong() {
		song.setName("Song");
		song.setArtist("Artist");
		song.setAlbum("Album");
		song.setDate("1999");
		song.setAuthor("Author");
		song.setCopyright("Copyright");
		song.setTranscriber("Transcriber");
		song.setWriter("Writer");
		track.setName("Guitar");
	}

	private String header(int style) {
		harness.print(style);
		return paintHeader();
	}

	private String paintHeader() {
		harness.painter.clear();
		float offset = harness.layout().paintHeader(harness.painter, track);
		return "offset=" + offset + "\n" + String.join("\n", harness.painter.calls());
	}
}
