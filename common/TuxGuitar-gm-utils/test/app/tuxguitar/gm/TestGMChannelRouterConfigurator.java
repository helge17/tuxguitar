package app.tuxguitar.gm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGChannelParameter;
import app.tuxguitar.song.models.TGSong;

/**
 * Pins GMChannelRouterConfigurator, which gives each channel of a song the MIDI channels it plays on: two while there
 * are free ones (the second for effects), MIDI channel 9 to a percussion channel, the ones a channel's parameters name
 * when it has them. An audio channel sounds takes, not MIDI: it gets none.
 *
 * The song starts with one channel (id 1). A channel's route reads "first,second", or "none".
 */
public class TestGMChannelRouterConfigurator {

	private final TGSongManager songManager = new TGSongManager();
	private final TGSong song = songManager.newSong();
	private final GMChannelRouter router = new GMChannelRouter();

	private String route(TGChannel channel) {
		new GMChannelRouterConfigurator(router).configureRouter(song.getChannels());
		GMChannelRoute route = router.getRoute(channel.getChannelId());
		return (route == null ? "none" : route.getChannel1() + "," + route.getChannel2());
	}

	private TGChannel audioChannel() {
		return songManager.getChannel(song, songManager.addAudioTrack(song, TGAudioInput.mono(1)).getChannelId());
	}

	private void parameter(TGChannel channel, String key, String value) {
		TGChannelParameter parameter = songManager.getFactory().newChannelParameter();
		parameter.setKey(key);
		parameter.setValue(value);
		channel.addParameter(parameter);
	}

	@Test
	public void eachChannelGetsTwoFreeMidiChannels() {
		TGChannel second = songManager.addChannel(song);

		assertEquals("0,1", route(song.getChannel(0)));
		assertEquals("2,3", route(second));
	}

	@Test
	public void aPercussionChannelGetsMidiChannel9() {
		assertEquals("9,9", route(songManager.addPercussionChannel(song)));
	}

	@Test
	public void aChannelGetsTheMidiChannelsItsParametersName() {
		TGChannel channel = songManager.addChannel(song);
		parameter(channel, GMChannelRoute.PARAMETER_GM_CHANNEL_1, "5");
		parameter(channel, GMChannelRoute.PARAMETER_GM_CHANNEL_2, "6");

		assertEquals("5,6", route(channel));
	}

	@Test
	public void anAudioChannelGetsNoneAndTakesNoneFromTheOthers() {
		TGChannel audio = audioChannel();
		TGChannel after = songManager.addChannel(song);

		assertEquals("none", route(audio));
		assertEquals("2,3", route(after));
	}
}
