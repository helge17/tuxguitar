package app.tuxguitar.editor.action.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.action.TGActionException;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.editor.InMemoryResources;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGSong;

/**
 * Pins TGReadSongAction: the song read from the input stream, its format found from the content, becomes the song of
 * the action context and, through the load song action, of the document; the context also learns the format and
 * whether the file came from a newer version. The beats read are aligned one after the other (a rest then fills the
 * measure). A stream that is not a song fails the action.
 *
 * It reads back as "document song / context song / format / newer file format".
 */
public class TestReadSongAction {

	private final SongActionFixture fixture = new SongActionFixture();

	public TestReadSongAction() {
		fixture.map(new TGLoadSongAction(fixture.context));
	}

	private void read(byte[] bytes) {
		fixture.actionContext.setAttribute(TGReadSongAction.ATTRIBUTE_INPUT_STREAM, new ByteArrayInputStream(bytes));
		fixture.execute(new TGReadSongAction(fixture.context));
	}

	@Test
	public void theSongReadIsLoaded() throws TGFileFormatException {
		read(InMemoryResources.song("Blues"));

		TGSong song = fixture.actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		TGFileFormat format = fixture.actionContext.getAttribute(TGReadSongAction.ATTRIBUTE_FORMAT);
		assertEquals("Blues / Blues / TuxGuitar 2.0 / false", TGDocumentManager.getInstance(fixture.context).getSong().getName() + " / " + song.getName()
				+ " / " + format.getName() + " / " + fixture.actionContext.getAttribute(TGReadSongAction.IS_NEWER_FILE_FORMAT));
	}

	@Test
	public void theBeatsReadAreAlignedOneAfterTheOther() throws TGFileFormatException {
		TGMeasure written = fixture.song.getTrack(0).getMeasure(0);
		fixture.note(fixture.beat(written, 0), 1, 0);
		fixture.note(fixture.beat(written, 2), 1, 2);

		read(InMemoryResources.song(fixture.songManager, fixture.song));

		TGSong song = fixture.actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		TGMeasure measure = song.getTrack(0).getMeasure(0);
		List<String> beats = new ArrayList<String>();
		for (TGBeat beat : measure.getBeats()) {
			beats.add((beat.getStart() - measure.getStart()) / TGDuration.QUARTER_TIME + ":" + SongActionFixture.describe(beat));
		}
		assertEquals("0:s1f0 1:s1f2 2:", String.join(" ", beats));
	}

	@Test
	public void aStreamThatIsNotASongFails() {
		assertThrows(TGActionException.class, () -> read("not a song".getBytes(StandardCharsets.UTF_8)));
	}
}
