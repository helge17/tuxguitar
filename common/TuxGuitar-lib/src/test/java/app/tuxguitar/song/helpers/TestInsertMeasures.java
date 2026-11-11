package app.tuxguitar.song.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Pins TGSongSegmentHelper.insertMeasures, as pasting or inserting measures does: the segment's headers are
 * inserted at a measure number, the following measures move after them, and each track gets the segment's
 * measures of its track (or of the single segment track, when pasting to a track), else empty measures
 * with the clef and key signature of the measure before (or, at the song start, after) the insertion.
 *
 * The song is the {@link SegmentFixture}.
 */
public class TestInsertMeasures extends SegmentFixture {

	@Test
	public void insertsEveryTrackOfTheSegmentAndMovesTheFollowingMeasures() {
		TGSongSegment segment = helper.copyMeasures(song, 1, 2);
		helper.insertMeasures(song, segment, 3, start(3) - start(1), 0);

		assertEquals(""
			+ "track 1: 1@1/1/1:11+0 2@5/2/2:12+0 3@9/1/1:11+0 4@13/2/2:12+0 5@17/3/3:13+0\n"
			+ "track 2: 1@1/1/1:21+0 2@5/2/2:22+0 3@9/1/1:21+0 4@13/2/2:22+0 5@17/3/3:23+0\n"
			, describe());
	}

	@Test
	public void insertsAtTheSongStart() {
		TGSongSegment segment = helper.copyMeasures(song, 3, 3);
		helper.insertMeasures(song, segment, 1, start(1) - start(3), 0);

		assertEquals(""
			+ "track 1: 1@1/3/3:13+0 2@5/1/1:11+0 3@9/2/2:12+0 4@13/3/3:13+0\n"
			+ "track 2: 1@1/3/3:23+0 2@5/1/1:21+0 3@9/2/2:22+0 4@13/3/3:23+0\n"
			, describe());
	}

	@Test
	public void pastesASingleTrackToTheChosenTrackAndFillsTheOthersWithEmptyMeasures() {
		TGSongSegment segment = helper.copyMeasures(song, 1, 1, song.getTrack(0));
		helper.insertMeasures(song, segment, 3, start(3) - start(1), 2);

		assertEquals(""
			+ "track 1: 1@1/1/1:11+0 2@5/2/2:12+0 3@9/2/2: 4@13/3/3:13+0\n"
			+ "track 2: 1@1/1/1:21+0 2@5/2/2:22+0 3@9/1/1:11+0 4@13/3/3:23+0\n"
			, describe());
	}

	@Test
	public void emptyMeasuresAtTheSongStartTakeTheClefAndKeyOfTheMeasureAfter() {
		TGSongSegment segment = helper.copyMeasures(song, 2, 3, song.getTrack(1));
		helper.insertMeasures(song, segment, 1, start(1) - start(2), 1);

		assertEquals(""
			+ "track 1: 1@1/2/2:22+0 2@5/3/3:23+0 3@9/1/1:11+0 4@13/2/2:12+0 5@17/3/3:13+0\n"
			+ "track 2: 1@1/1/1: 2@5/1/1: 3@9/1/1:21+0 4@13/2/2:22+0 5@17/3/3:23+0\n"
			, describe());
	}

	@Test
	public void aTrackOfAnotherKindGetsEmptyMeasures() {
		percussion(song.getTrack(1));
		TGSongSegment segment = helper.copyMeasures(song, 1, 1);
		helper.insertMeasures(song, segment, 2, start(2) - start(1), 0);

		assertEquals(""
			+ "track 1: 1@1/1/1:11+0 2@5/1/1:11+0 3@9/2/2:12+0 4@13/3/3:13+0\n"
			+ "track 2: 1@1/0/1:21+0 2@5/0/1:21+0 3@9/0/2:22+0 4@13/0/3:23+0\n"
			, describe());
	}

	@Test
	public void aSegmentOfAnotherKindGivesEmptyMeasures() {
		TGSongSegment segment = helper.copyMeasures(song, 1, 1, song.getTrack(0));
		percussion(song.getTrack(1));
		helper.insertMeasures(song, segment, 2, start(2) - start(1), 2);

		assertEquals(""
			+ "track 1: 1@1/1/1:11+0 2@5/1/1: 3@9/2/2:12+0 4@13/3/3:13+0\n"
			+ "track 2: 1@1/0/1:21+0 2@5/0/1: 3@9/0/2:22+0 4@13/0/3:23+0\n"
			, describe());
	}
}
