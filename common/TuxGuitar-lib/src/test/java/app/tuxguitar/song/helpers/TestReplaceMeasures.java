package app.tuxguitar.song.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasureHeader;

/**
 * Pins TGSongSegmentHelper.replaceMeasures, as pasting measures in replace mode does: from the target measure on,
 * the segment's measures replace each track's measures (of its own track, or of the chosen track when pasting a
 * single track), skipping a track of another kind (percussion or not) or missing from the song. The song headers
 * take the segment headers when pasting every track, or to a song of one track; then the measures after follow
 * without gap.
 *
 * The song is the {@link SegmentFixture}; its headers are read back as "number@start/numerator" (start in quarters).
 */
public class TestReplaceMeasures extends SegmentFixture {

	// as the paste action does: room for the segment past the song end, then a copy replacing from the target
	private void paste(TGSongSegment segment, int toNumber, int toTrack) {
		int freeSpace = song.countMeasureHeaders() - (toNumber - 1);
		for (int i = freeSpace; i < segment.getHeaders().size(); i++) {
			songManager.addNewMeasureBeforeEnd(song);
		}
		long move = start(toNumber) - segment.getHeaders().get(0).getStart();
		helper.replaceMeasures(song, segment.clone(factory), move, toTrack);
	}

	private String headers() {
		StringBuilder sb = new StringBuilder();
		Iterator<TGMeasureHeader> headers = song.getMeasureHeaders();
		while (headers.hasNext()) {
			TGMeasureHeader header = headers.next();
			sb.append(sb.length() == 0 ? "" : " ").append(header.getNumber()).append('@')
				.append(header.getStart() / TGDuration.QUARTER_TIME).append('/').append(header.getTimeSignature().getNumerator());
		}
		return sb.toString();
	}

	// the later headers keep their start
	private void threeFour(int number) {
		songManager.getMeasureHeader(song, number).getTimeSignature().setNumerator(3);
	}

	@Test
	public void replacesEveryTrackFromTheTargetMeasure() {
		paste(helper.copyMeasures(song, 1, 2), 2, 0);

		assertEquals(""
			+ "track 1: 1@1/1/1:11+0 2@5/1/1:11+0 3@9/2/2:12+0\n"
			+ "track 2: 1@1/1/1:21+0 2@5/1/1:21+0 3@9/2/2:22+0\n"
			, describe());
	}

	@Test
	public void addsMeasuresPastTheSongEnd() {
		paste(helper.copyMeasures(song, 1, 2), 3, 0);

		assertEquals(""
			+ "track 1: 1@1/1/1:11+0 2@5/2/2:12+0 3@9/1/1:11+0 4@13/2/2:12+0\n"
			+ "track 2: 1@1/1/1:21+0 2@5/2/2:22+0 3@9/1/1:21+0 4@13/2/2:22+0\n"
			, describe());
	}

	@Test
	public void theSongHeadersTakeTheSegmentHeadersAndTheNextOnesFollow() {
		threeFour(1);
		paste(helper.copyMeasures(song, 1, 1), 2, 0);

		assertEquals("1@1/3 2@5/3 3@8/4", headers());
	}

	@Test
	public void theMeasuresAfterAShorterHeaderBringTheirNotesAlong() {
		threeFour(1);
		paste(helper.copyMeasures(song, 1, 1), 2, 0);

		assertEquals(""
			+ "track 1: 1@1/1/1:11+0 2@5/1/1:11+0 3@8/3/3:13+0\n"
			+ "track 2: 1@1/1/1:21+0 2@5/1/1:21+0 3@8/3/3:23+0\n"
			, describe());
	}

	@Test
	public void aSingleTrackPastedToATrackLeavesTheOthersAndTheHeaders() {
		threeFour(1);
		paste(helper.copyMeasures(song, 1, 1, song.getTrack(0)), 3, 2);

		assertEquals("1@1/3 2@5/4 3@9/4", headers());
		assertEquals(""
			+ "track 1: 1@1/1/1:11+0 2@5/2/2:12+0 3@9/3/3:13+0\n"
			+ "track 2: 1@1/1/1:21+0 2@5/2/2:22+0 3@9/1/1:11+0\n"
			, describe());
	}

	@Test
	public void aSingleTrackSongTakesTheSegmentHeaders() {
		songManager.removeTrack(song, song.getTrack(1));
		threeFour(1);
		paste(helper.copyMeasures(song, 1, 1, song.getTrack(0)), 2, 1);

		assertEquals("1@1/3 2@5/3 3@8/4", headers());
	}

	@Test
	public void aTrackOfAnotherKindKeepsItsMeasures() {
		TGSongSegment segment = helper.copyMeasures(song, 1, 1);
		percussion(song.getTrack(1));
		paste(segment, 3, 0);

		assertEquals(""
			+ "track 1: 1@1/1/1:11+0 2@5/2/2:12+0 3@9/1/1:11+0\n"
			+ "track 2: 1@1/0/1:21+0 2@5/0/2:22+0 3@9/0/3:23+0\n"
			, describe());
	}

	@Test
	public void aSegmentTrackMissingFromTheSongIsSkipped() {
		TGSongSegment segment = helper.copyMeasures(song, 1, 1);
		songManager.removeTrack(song, song.getTrack(1));
		paste(segment, 3, 0);

		assertEquals("track 1: 1@1/1/1:11+0 2@5/2/2:12+0 3@9/1/1:11+0\n", describe());
	}
}
