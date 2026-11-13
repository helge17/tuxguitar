package app.tuxguitar.player.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGChannel;

/**
 * The Metronome while the song plays live: the player makes its clicks a track of the sequence it plays (the last one,
 * muted while the Metronome is off), the click note held a beat on every beat of every measure played.
 *
 * The song has 4 measures of 4/4 and one track; it plays through a FakeSequencer (see PlayingFixture), which keeps
 * the sequence. A click is written "on TICK cCHANNEL kKEY vVELOCITY".
 */
public class TestMidiPlayerMetronome {

	private final PlayingFixture fixture = new PlayingFixture();
	private final MidiPlayer player = fixture.player;

	// a percussion channel as a song file may have it: with id 0
	private void withPercussion() {
		TGChannel channel = fixture.songManager.createChannel();
		channel.setChannelId(0);
		channel.setBank(TGChannel.DEFAULT_PERCUSSION_BANK);
		fixture.song.addChannel(channel);
	}

	// plays: how many clicks the sequence has | its first | its last
	private String clicks() throws MidiPlayerException {
		player.play();
		String track = " t" + (fixture.song.countTracks() + 1) + " ";
		List<String> clicks = fixture.sequencer.sequence.events("on").stream()
				.filter(event -> event.contains(track))
				.map(event -> event.replace(track, " ").replaceAll(" s-?\\d+ b\\d$", ""))
				.collect(Collectors.toList());
		return clicks.size() + (clicks.isEmpty() ? "" : " | " + clicks.get(0) + " | " + clicks.get(clicks.size() - 1));
	}

	// plays on a synthesizer with a sample clock (the TuxGuitar Synthesizer's): the clicks, as clicks() writes them
	private String clicksOnASampleClock() throws MidiPlayerException {
		player.getSynthesizerProxy().setMidiSynthesizer(new ClockedSynthesizer(new ArrayList<String>()));
		return clicks();
	}

	// a MIDI Port without a sample clock (one that sends MIDI out): on the song's percussion channel
	@Test
	public void onAPortWithoutASampleClockTheMetronomeClicksOnThePercussionChannel() throws MidiPlayerException {
		withPercussion();

		assertEquals("16 | on 960 c0 k37 v95 | on 15360 c0 k37 v95", clicks());
	}

	@Test
	public void onAPortWithoutASampleClockTheMetronomeDoesNotClickWithoutAPercussionChannel() throws MidiPlayerException {
		assertEquals("0", clicks());
	}

	// today's: on the TuxGuitar Synthesizer's sample clock, on the song's percussion channel (its sound chain)
	@Test
	public void onASampleClockTheMetronomeClicksOnThePercussionChannel() throws MidiPlayerException {
		withPercussion();

		assertEquals("16 | on 960 c0 k37 v95 | on 15360 c0 k37 v95", clicksOnASampleClock());
	}

	// today's: on the TuxGuitar Synthesizer's sample clock, no click without a percussion channel
	@Test
	public void onASampleClockTheMetronomeDoesNotClickWithoutAPercussionChannel() throws MidiPlayerException {
		assertEquals("0", clicksOnASampleClock());
	}

	@Test
	public void theMetronomeClicksTheClickSound() throws MidiPlayerException {
		withPercussion();
		player.setClick(new MidiClick(76, 127));

		assertEquals("16 | on 960 c0 k76 v127 | on 15360 c0 k76 v127", clicks());
	}
}
