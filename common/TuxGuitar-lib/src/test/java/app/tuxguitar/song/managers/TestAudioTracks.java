package app.tuxguitar.song.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTake;
import app.tuxguitar.song.models.TGTakeClip;
import app.tuxguitar.song.models.TGTrack;

/**
 * Pins the song model of audio tracks: an audio track has no strings and a channel of its own that records from an
 * input; removing the track removes that channel. Takes sit in measures as clips; copies share the takes, the take
 * chosen in a measure and the track's next take number.
 *
 * The song is a new one: one guitar track on channel 1, one measure.
 */
public class TestAudioTracks {

	private final TGSongManager songManager = new TGSongManager();
	private final TGSong song = songManager.newSong();
	private final TGTake take = new TGTake(1, new float[] { 0.25f, -0.5f });

	private TGTrack addAudioTrack() {
		return songManager.addAudioTrack(song, TGAudioInput.mono(2));
	}

	private TGChannel channelOf(TGTrack track) {
		return songManager.getChannel(song, track.getChannelId());
	}

	@Test
	public void anAudioTrackHasNoStringsAndAChannelOfItsOwnRecordingTheInput() {
		TGTrack track = addAudioTrack();

		assertEquals(0, track.stringCount());
		assertEquals(2, track.getNumber());
		assertEquals(song.countMeasureHeaders(), track.countMeasures());
		assertEquals(2, song.countChannels());
		assertEquals(2, channelOf(track).getAudioInput().getChannel());
		assertTrue(track.isAudio());
		assertFalse(track.isPercussion());
	}

	// an audio track holds no notes: its highest fret is the one of no note at all
	@Test
	public void aTrackWithoutNotesOrMeasuresHasNoHighestFret() {
		assertEquals(-1, addAudioTrack().getHighestFret());
		assertEquals(-1, new TGFactory().newTrack().getHighestFret());
	}

	@Test
	public void aGuitarTrackIsNoAudioTrack() {
		assertFalse(song.getTrack(0).isAudio());
	}

	@Test
	public void aTrackWithoutSongOrChannelIsNoAudioTrack() {
		TGTrack loose = new TGFactory().newTrack();
		TGTrack orphan = addAudioTrack();
		songManager.removeChannel(song, channelOf(orphan));

		assertFalse(loose.isAudio());
		assertFalse(orphan.isAudio());
	}

	@Test
	public void removingAnAudioTrackRemovesItsChannel() {
		songManager.removeTrack(song, addAudioTrack());

		assertEquals(1, song.countTracks());
		assertEquals(1, song.countChannels());
	}

	@Test
	public void removingAGuitarTrackKeepsItsChannel() {
		songManager.removeTrack(song, songManager.addTrack(song));

		assertEquals(1, song.countTracks());
		assertEquals(2, song.countChannels());
	}

	// pinned as it is: there is no track to remove, and the song cannot remove none
	@Test
	public void removingATrackThatIsNotThereFails() {
		assertThrows(NullPointerException.class, () -> songManager.removeTrack(song, 5));
	}

	@Test
	public void aClonedAudioTrackGetsACopyOfItsChannel() {
		TGTrack track = addAudioTrack();
		channelOf(track).setVolume((short) 90);

		TGTrack clone = songManager.cloneTrack(song, track);

		assertEquals(3, song.countChannels());
		assertEquals(3, clone.getChannelId());
		assertEquals(2, channelOf(clone).getAudioInput().getChannel());
		assertEquals(90, channelOf(clone).getVolume());
		assertEquals(2, track.getChannelId());
	}

	@Test
	public void aClonedGuitarTrackSharesItsChannel() {
		TGTrack clone = songManager.cloneTrack(song, song.getTrack(0));

		assertEquals(1, song.countChannels());
		assertEquals(1, clone.getChannelId());
	}

	@Test
	public void aCopiedChannelSharesTheAudioInput() {
		TGChannel channel = channelOf(addAudioTrack());

		assertSame(channel.getAudioInput(), channel.clone(new TGFactory()).getAudioInput());
	}

	@Test
	public void aCopiedTrackKeepsItsNextTakeNumber() {
		TGTrack track = addAudioTrack();
		track.setNextTakeNumber(4);

		assertEquals(4, track.clone(new TGFactory(), song).getNextTakeNumber());
	}

	@Test
	public void aCopiedMeasureSharesItsTakeClipsAndChosenTake() {
		TGMeasure measure = song.getTrack(0).getMeasure(0);
		TGTakeClip clip = new TGTakeClip(take, 960, 1920);
		measure.addTakeClip(clip);
		measure.setChosenTake(take);

		TGMeasure copy = measure.clone(new TGFactory(), measure.getHeader());

		assertEquals(1, copy.getTakeClips().size());
		assertSame(clip, copy.getTakeClips().get(0));
		assertSame(take, copy.getChosenTake());
	}

	@Test
	public void removingATakeFromAMeasureRemovesItsClipsAndTheChoiceOfIt() {
		TGMeasure measure = song.getTrack(0).getMeasure(0);
		TGTake other = new TGTake(2, new float[] { 0f });
		measure.addTakeClip(new TGTakeClip(take, 0, 960));
		measure.addTakeClip(new TGTakeClip(other, 960, 1920));
		measure.addTakeClip(new TGTakeClip(take, 1920, 3840));
		measure.setChosenTake(take);

		measure.removeTake(take);

		assertEquals(1, measure.getTakeClips().size());
		assertSame(other, measure.getTakeClips().get(0).getTake());
		assertNull(measure.getChosenTake());
	}

	@Test
	public void removingAnotherTakeKeepsTheChoice() {
		TGMeasure measure = song.getTrack(0).getMeasure(0);
		measure.setChosenTake(take);

		measure.removeTake(new TGTake(2, new float[] { 0f }));

		assertSame(take, measure.getChosenTake());
	}

	@Test
	public void aTakeKeepsItsSamplesWhateverHappensToTheArrayItWasMadeFrom() {
		float[] samples = { 0.25f, -0.5f };
		TGTake made = new TGTake(3, samples);
		samples[0] = 1f;

		assertEquals(3, made.getNumber());
		assertEquals(2, made.countSamples());
		assertEquals(0.25f, made.getSample(0));
		assertEquals(-0.5f, made.getSample(1));
	}

	@Test
	public void aClipIsTheTakeBetweenTwoTicksOfItsMeasure() {
		TGTakeClip clip = new TGTakeClip(take, 960, 1920);

		assertSame(take, clip.getTake());
		assertEquals(960, clip.getStart());
		assertEquals(1920, clip.getEnd());
	}
}
