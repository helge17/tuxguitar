package app.tuxguitar.player.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGVelocities;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.configuration.TGConfigManager;
import app.tuxguitar.util.properties.MapProperties;
import app.tuxguitar.util.properties.TGPropertiesManager;

/**
 * The click sound Count In and the Metronome play: a drum note from 0 to 127 at a velocity from 1 to 127 (0 would be
 * no note at all), Side Stick at the default velocity until changed, the player's own setting.
 */
public class TestMidiClick {

	private static String read(MidiClick click) {
		return click.getNote() + " at " + click.getVelocity();
	}

	@Test
	public void isSideStickAtTheDefaultVelocityUntilChanged() {
		MidiPlayer player = MidiPlayer.getInstance(new TGContext());

		assertEquals("37 at " + TGVelocities.DEFAULT, read(player.getClick()));
	}

	@Test
	public void isTheOneThePlayerIsGiven() {
		MidiPlayer player = MidiPlayer.getInstance(new TGContext());
		player.setClick(new MidiClick(76, 127));

		assertEquals("76 at 127", read(player.getClick()));
	}

	// the song's one track, on a percussion channel, has no notes: every note of the sequence is the Metronome's
	@Test
	public void isWhatThePlayersMetronomeTicks() {
		PlayingFixture fixture = new PlayingFixture();
		TGChannel percussion = fixture.songManager.createChannel();
		percussion.setBank(TGChannel.DEFAULT_PERCUSSION_BANK);
		fixture.song.addChannel(percussion);
		fixture.song.getTrack(0).setChannelId(percussion.getChannelId());
		fixture.player.setClick(new MidiClick(76, 127));
		fixture.player.updateLoop(true);
		fixture.player.addSequence();

		List<String> notes = fixture.sequencer.sequence.events("on");
		assertEquals(16, notes.stream().filter(note -> note.endsWith(" c" + percussion.getChannelId() + " k76 v127 s-1 b0")).count(), notes.toString());
	}

	@Test
	public void keepsTheLowestAndHighestNoteAndVelocity() {
		assertEquals("0 at 1, 127 at 127", read(new MidiClick(0, 1)) + ", " + read(new MidiClick(127, 127)));
	}

	// a TuxGuitar setting: kept in a configuration of the app's, which a test keeps in memory
	private static TGConfigManager config() {
		TGContext context = new TGContext();
		TGPropertiesManager.getInstance(context).setPropertiesFactory(MapProperties::new);
		return new TGConfigManager(context, "tuxguitar");
	}

	@Test
	public void keptIsReadBackAsWrittenAndNothingKeptIsTheDefault() {
		TGConfigManager config = config();
		String none = read(MidiClick.read(config));
		new MidiClick(76, 127).write(config);
		assertEquals("37 at " + TGVelocities.DEFAULT + " | 76 at 127", none + " | " + read(MidiClick.read(config)));
	}

	// a configuration edited by hand: what is not a MIDI note and velocity is the default click
	@Test
	public void keptOutsideMidiIsTheDefault() {
		TGConfigManager config = config();
		config.setValue(MidiClick.NOTE, 128);
		config.setValue(MidiClick.VELOCITY, 127);
		String note = read(MidiClick.read(config));
		config.setValue(MidiClick.NOTE, 76);
		config.setValue(MidiClick.VELOCITY, 0);
		assertEquals("37 at " + TGVelocities.DEFAULT + " | 37 at " + TGVelocities.DEFAULT, note + " | " + read(MidiClick.read(config)));
	}

	@Test
	public void refusesANoteOrVelocityOutsideMidi() {
		assertThrows(IllegalArgumentException.class, () -> new MidiClick(-1, 95));
		assertThrows(IllegalArgumentException.class, () -> new MidiClick(128, 95));
		assertThrows(IllegalArgumentException.class, () -> new MidiClick(37, 0));
		assertThrows(IllegalArgumentException.class, () -> new MidiClick(37, 128));
	}
}
