package app.tuxguitar.player.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

/**
 * Pins how the player matches its synthesizer channels to the song channels (MidiPlayer.updateChannels): a
 * channel no longer in the song, or no longer open in the synthesizer, is closed and dropped; every song channel
 * without one gets one opened (unless the synthesizer has none to give); then every song channel is sent its
 * settings.
 *
 * The song has channels 1 (program 25) and 2 (program 30). The synthesizer logs "open id" and "close id"; a channel
 * logs the program it is sent as "program id=value". The result is the log, then the channels the player routes to.
 */
public class TestMidiPlayerChannels {

	private final TGContext context = new TGContext();
	private final MidiPlayer player = MidiPlayer.getInstance(context);
	private final TGSongManager songManager = TGDocumentManager.getInstance(context).getSongManager();
	private final TGSong song = TGDocumentManager.getInstance(context).getSong();
	private final FakeSynthesizer synthesizer = new FakeSynthesizer();
	private final List<String> log = new ArrayList<String>();

	private class FakeChannel implements MidiChannel {
		private final int id;
		private boolean open = true;

		FakeChannel(int id) {
			this.id = id;
		}

		public void sendProgramChange(int value) { log.add("program " + this.id + "=" + value); }
		public void sendNoteOn(int key, int velocity, int voice, boolean bendMode) { }
		public void sendNoteOff(int key, int velocity, int voice, boolean bendMode) { }
		public void sendPitchBend(int value, int voice, boolean bendMode) { }
		public void sendControlChange(int controller, int value) { }
		public void sendParameter(String key, String value) {
			if (MidiParameters.AUDIO_CHANNEL.equals(key)) {
				log.add("audio " + this.id + "=" + value);
			}
		}
		public void sendAllNotesOff() { }
	}

	private class FakeSynthesizer implements MidiSynthesizer {
		private final Map<Integer, FakeChannel> channels = new HashMap<Integer, FakeChannel>();
		private boolean exhausted = false;

		public MidiChannel openChannel(int channelId) {
			log.add("open " + channelId);
			if (this.exhausted) {
				return null;
			}
			this.channels.put(channelId, new FakeChannel(channelId));
			return this.channels.get(channelId);
		}

		public void closeChannel(MidiChannel midiChannel) {
			FakeChannel channel = (FakeChannel) midiChannel;
			log.add("close " + channel.id);
			channel.open = false;
		}

		public boolean isChannelOpen(MidiChannel midiChannel) {
			return ((FakeChannel) midiChannel).open;
		}

		public boolean isBusy() { return false; }
	}

	public TestMidiPlayerChannels() {
		songManager.removeAllChannels(song);
		addChannel(1, 25);
		addChannel(2, 30);
		player.getSynthesizerProxy().setMidiSynthesizer(synthesizer);
	}

	private void addChannel(int id, int program) {
		TGChannel channel = songManager.createChannel();
		channel.setChannelId(id);
		channel.setProgram((short) program);
		songManager.addChannel(song, channel);
	}

	private String update() throws MidiPlayerException {
		log.clear();
		player.updateChannels();
		return String.join(", ", log) + " -> " + player.getChannelRouter().getMidiChannelIds();
	}

	@Test
	public void opensAChannelOnceForEverySongChannelThenSendsItsSettings() throws MidiPlayerException {
		String first = update();

		assertEquals("open 1, open 2, program 1=25, program 2=30 -> [1, 2] | program 1=25, program 2=30 -> [1, 2]",
			first + " | " + update());
	}

	// an audio track's channel sounds its takes: the synthesizer is told so with its settings
	@Test
	public void anAudioChannelIsToldItIsOneWithItsSettings() throws MidiPlayerException {
		songManager.getChannel(song, 2).setAudioInput(TGAudioInput.mono(1));

		assertEquals("open 1, open 2, program 1=25, audio 2=true, program 2=30 -> [1, 2]", update());
	}

	@Test
	public void closesTheChannelsNoLongerInTheSong() throws MidiPlayerException {
		update();
		songManager.removeChannel(song, songManager.getChannel(song, 1));

		assertEquals("close 1, program 2=30 -> [2]", update());
	}

	@Test
	public void reopensAChannelTheSynthesizerClosed() throws MidiPlayerException {
		update();
		synthesizer.channels.get(2).open = false;

		assertEquals("close 2, open 2, program 1=25, program 2=30 -> [1, 2]", update());
	}

	@Test
	public void leavesOutAChannelTheSynthesizerCannotOpen() throws MidiPlayerException {
		synthesizer.exhausted = true;

		assertEquals("open 1, open 2 -> []", update());
	}

	// the volume is kept, and only while playing are the channels updated at once
	@Test
	public void aNewVolumeUpdatesTheChannelsWhilePlayingOnly() {
		player.setVolume(5);
		String stopped = String.join(", ", log) + " -> " + player.getVolume();
		player.setRunning(true);
		player.setVolume(7);
		player.setRunning(false);

		assertEquals(" -> 5 | open 1, open 2, program 1=25, program 2=30 -> 7",
			stopped + " | " + String.join(", ", log) + " -> " + player.getVolume());
	}
}
