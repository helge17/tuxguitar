package app.tuxguitar.editor.action.channel;

import static app.tuxguitar.editor.action.SongActionFixture.reads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGChannel;

/**
 * Pins TGSetChannelsAction: the song's channels are replaced by the given ones, each added through the add channel
 * action, which numbers a channel without id after the highest id. Without a channel list nothing changes.
 *
 * The song starts with its one channel; the song channels read back as "id:name ...".
 */
public class TestSetChannelsAction {

	private final SongActionFixture fixture = new SongActionFixture();

	public TestSetChannelsAction() {
		fixture.song.getChannel(0).setName("old");
		fixture.map(new TGAddChannelAction(fixture.context));
	}

	private TGChannel channel(int id, String name) {
		TGChannel channel = fixture.songManager.createChannel();
		channel.setChannelId(id);
		channel.setName(name);
		return channel;
	}

	private String set(List<TGChannel> channels) {
		fixture.actionContext.setAttribute(TGSetChannelsAction.ATTRIBUTE_CHANNELS, channels);
		fixture.execute(new TGSetChannelsAction(fixture.context));
		List<String> read = new ArrayList<String>();
		for (Iterator<TGChannel> it = fixture.song.getChannels(); it.hasNext();) {
			TGChannel channel = it.next();
			read.add(channel.getChannelId() + ":" + channel.getName());
		}
		return String.join(" ", read);
	}

	@TestFactory
	public List<DynamicTest> settingTheChannels() {
		return Arrays.asList(
			reads("the given channels replace the old ones", "5:a 6:b", () -> {
				TestSetChannelsAction test = new TestSetChannelsAction();
				return test.set(Arrays.asList(test.channel(5, "a"), test.channel(0, "b")));
			}),
			reads("an empty list removes them all", "", () -> new TestSetChannelsAction().set(new ArrayList<TGChannel>())),
			reads("without a list nothing changes", "1:old", () -> new TestSetChannelsAction().set(null)));
	}
}
