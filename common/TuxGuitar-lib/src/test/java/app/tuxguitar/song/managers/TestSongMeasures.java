package app.tuxguitar.song.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.helpers.SegmentFixture;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGTempo;
import app.tuxguitar.song.models.TGTrack;

/**
 * Pins how TGSongManager adds a measure to every track, restores a track or the song from a copy (as undoing an
 * edit does), and changes the tempo or triplet feel of measures. The song is the {@link SegmentFixture}.
 */
public class TestSongMeasures extends SegmentFixture {

	// one value per measure header, in order
	private String headers(Function<TGMeasureHeader, Object> value) {
		List<String> values = new ArrayList<String>();
		for (int h = 0; h < song.countMeasureHeaders(); h++) {
			values.add(String.valueOf(value.apply(song.getMeasureHeader(h))));
		}
		return String.join(" ", values);
	}

	private TGTempo tempo(int quarterValue) {
		TGTempo tempo = factory.newTempo();
		tempo.setQuarterValue(quarterValue);
		return tempo;
	}

	@Test
	public void aNewMeasureInsideTheSongTakesTheKeyAndClefOfTheMeasureBeforeAndMovesTheFollowingOnes() {
		TGMeasureHeader first = song.getMeasureHeader(0);
		first.setMarker(factory.newMarker());
		first.setRepeatOpen(true);
		first.setRepeatClose(2);
		first.setRepeatAlternative(1);
		first.setLineBreak(true);
		songManager.addNewMeasure(song, 2);

		assertEquals(""
			+ "track 1: 1@1/1/1:11+0 2@5/1/1: 3@9/2/2:12+0 4@13/3/3:13+0\n"
			+ "track 2: 1@1/1/1:21+0 2@5/1/1: 3@9/2/2:22+0 4@13/3/3:23+0\n"
			, describe());
		// the new header copies the one before, without its marker, repeats or line break
		assertEquals("true false false false", headers(h -> h.hasMarker()));
		assertEquals("true false false false", headers(h -> h.isRepeatOpen()));
		assertEquals("2 0 0 0", headers(h -> h.getRepeatClose()));
		assertEquals("1 0 0 0", headers(h -> h.getRepeatAlternative()));
		assertEquals("true false false false", headers(h -> h.isLineBreak()));
	}

	@Test
	public void aNewFirstMeasureTakesTheKeyAndClefOfTheMeasureAfterAndKeepsTheLineBreak() {
		song.getMeasureHeader(0).setLineBreak(true);
		songManager.addNewMeasure(song, 1);

		assertEquals(""
			+ "track 1: 1@1/1/1: 2@5/1/1:11+0 3@9/2/2:12+0 4@13/3/3:13+0\n"
			+ "track 2: 1@1/1/1: 2@5/1/1:21+0 3@9/2/2:22+0 4@13/3/3:23+0\n"
			, describe());
		assertEquals("true true false false", headers(h -> h.isLineBreak()));
	}

	@Test
	public void aNewLastMeasureTakesTheKeyAndClefOfTheMeasureBefore() {
		songManager.addNewMeasure(song, 4);

		assertEquals(""
			+ "track 1: 1@1/1/1:11+0 2@5/2/2:12+0 3@9/3/3:13+0 4@13/3/3:\n"
			+ "track 2: 1@1/1/1:21+0 2@5/2/2:22+0 3@9/3/3:23+0 4@13/3/3:\n"
			, describe());
	}

	// the settings of a track, besides its measures
	private static String settings(TGTrack track) {
		StringBuilder settings = new StringBuilder();
		settings.append(track.getNumber()).append(' ').append(track.getName()).append(" offset ").append(track.getOffset())
			.append(track.isSolo() ? " solo" : "").append(track.isMute() ? " mute" : "").append(" channel ").append(track.getChannelId())
			.append(" color ").append(track.getColor().getR()).append(" lyrics ").append(track.getLyrics().getLyrics()).append(" strings");
		for (TGString string : track.getStrings()) {
			settings.append(' ').append(string.getValue());
		}
		return settings.toString();
	}

	@Test
	public void restoringATrackFromACopyRestoresItsSettingsAndMeasuresAndTheSongLength() {
		TGTrack track = song.getTrack(1);
		track.setMute(true);
		TGTrack copy = track.clone(factory, song);
		String saved = settings(track);
		track.setName("edited");
		track.setOffset(3);
		track.setSolo(true);
		track.setMute(false);
		track.setChannelId(9);
		track.getColor().setR(7);
		track.getLyrics().setLyrics("la la");
		track.getString(6).setValue(38);
		track.getStrings().remove(0);
		songManager.addNewMeasureBeforeEnd(song);
		songManager.copyTrackFrom(song, track, copy);

		assertEquals(""
			+ "track 1: 1@1/1/1:11+0 2@5/2/2:12+0 3@9/3/3:13+0\n"
			+ "track 2: 1@1/1/1:21+0 2@5/2/2:22+0 3@9/3/3:23+0\n"
			, describe());
		assertEquals(saved, settings(track));

		songManager.removeMeasure(song, 2);
		songManager.copyTrackFrom(song, track, copy);
		assertEquals(""
			+ "track 1: 1@1/1/1:11+0 2@5/3/3:13+0 3@9/3/3:\n"
			+ "track 2: 1@1/1/1:21+0 2@5/2/2:22+0 3@9/3/3:23+0\n"
			, describe());
	}

	// the information and the channel ids of the song
	private String information() {
		StringBuilder information = new StringBuilder();
		information.append(String.join(" ", song.getName(), song.getArtist(), song.getAlbum(), song.getAuthor(), song.getDate(),
			song.getCopyright(), song.getWriter(), song.getTranscriber(), song.getComments())).append(" channels");
		for (TGChannel channel : songManager.getChannels(song)) {
			information.append(' ').append(channel.getChannelId());
		}
		return information.toString();
	}

	@Test
	public void aSongCopiedAndCopiedBackIsRestored() {
		songManager.setProperties(song, "name", "artist", "album", "author", "date", "copyright", "writer", "transcriber", "comments");
		String saved = information();
		String measures = describe();
		TGSong copy = factory.newSong();
		songManager.copySongFrom(copy, song);

		songManager.setProperties(song, "", "", "", "", "", "", "", "", "");
		songManager.addChannel(song);
		songManager.addNewMeasureBeforeEnd(song);
		song.getTrack(0).getMeasure(0).getBeat(0).getVoice(0).getNote(0).setValue(1);
		songManager.copySongFrom(song, copy);

		assertEquals(measures, describe());
		assertEquals(saved, information());
	}

	@Test
	public void aTempoChangeRunsWhileTheTempoWasTheSameOrToTheEnd() {
		songManager.addNewMeasureBeforeEnd(song);
		song.getMeasureHeader(2).getTempo().setQuarterValue(90);

		songManager.changeTempos(song, song.getMeasureHeader(0), tempo(100), false);
		assertEquals("100 100 90 120", headers(h -> h.getTempo().getQuarterValue()));

		songManager.changeTempos(song, song.getMeasureHeader(1), tempo(80), true);
		assertEquals("100 80 80 80", headers(h -> h.getTempo().getQuarterValue()));

		songManager.changeTempos(song, song.getMeasureHeader(2), tempo(70), false);
		assertEquals("100 80 70 70", headers(h -> h.getTempo().getQuarterValue()));
	}

	@Test
	public void theTripletFeelChangesInTheMeasureOnlyOrToTheEnd() {
		songManager.changeTripletFeel(song, song.getMeasureHeader(1), TGMeasureHeader.TRIPLET_FEEL_EIGHTH, false);
		assertEquals(TGMeasureHeader.TRIPLET_FEEL_NONE + " " + TGMeasureHeader.TRIPLET_FEEL_EIGHTH + " " + TGMeasureHeader.TRIPLET_FEEL_NONE,
			headers(h -> h.getTripletFeel()));

		songManager.changeTripletFeel(song, song.getMeasureHeader(0), TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH, true);
		assertEquals(TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH + " " + TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH + " " + TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH,
			headers(h -> h.getTripletFeel()));
	}
}
