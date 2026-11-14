package app.tuxguitar.editor.action.channel;

import static app.tuxguitar.editor.action.SongActionFixture.reads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.event.TGUpdateEvent;
import app.tuxguitar.event.TGEventManager;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGChannelParameter;

/**
 * Pins TGUpdateChannelAction: the song channel with the id of the caret channel takes each setting the context gives,
 * keeping the others; given parameters replace all of its parameters. A volume change is announced with a
 * volume-changed update event. An audio channel takes no other bank or program: the action leaves it alone.
 *
 * The new song's channel has bank 0, program 25, volume 127, balance 64, no chorus, reverb, phaser or tremolo, its
 * default name, and the parameter "k=v". It reads back as "bank program volume balance chorus reverb phaser tremolo / name /
 * parameters", then " / volume event" when one was fired, then " / success" when the action reported it.
 */
public class TestUpdateChannelAction {

	private static final Consumer<TGActionContext> NOTHING = context -> {
	};
	private static final Consumer<TGActionContext> EVERY_SETTING = context -> {
		short value = 1;
		for (String attribute : Arrays.asList(TGUpdateChannelAction.ATTRIBUTE_BANK, TGUpdateChannelAction.ATTRIBUTE_PROGRAM, TGUpdateChannelAction.ATTRIBUTE_VOLUME,
				TGUpdateChannelAction.ATTRIBUTE_BALANCE, TGUpdateChannelAction.ATTRIBUTE_CHORUS, TGUpdateChannelAction.ATTRIBUTE_REVERB,
				TGUpdateChannelAction.ATTRIBUTE_PHASER, TGUpdateChannelAction.ATTRIBUTE_TREMOLO)) {
			context.setAttribute(attribute, value++);
		}
		context.setAttribute(TGUpdateChannelAction.ATTRIBUTE_NAME, "Lead");
	};

	private static final String DEFAULT_NAME = "file.save.default-name 1";

	private final SongActionFixture fixture = new SongActionFixture();
	private final TGChannel channel = fixture.song.getChannel(0);
	private final List<Integer> updates = new ArrayList<Integer>();

	public TestUpdateChannelAction() {
		channel.addParameter(parameter("k", "v"));
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, channel);
		TGEventManager.getInstance(fixture.context).addListener(TGUpdateEvent.EVENT_TYPE,
				event -> updates.add(event.<Integer>getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)));
	}

	private TGChannelParameter parameter(String key, String value) {
		TGChannelParameter parameter = fixture.songManager.getFactory().newChannelParameter();
		parameter.setKey(key);
		parameter.setValue(value);
		return parameter;
	}

	private String update(Consumer<TGActionContext> given) {
		given.accept(fixture.actionContext);
		fixture.execute(new TGUpdateChannelAction(fixture.context));
		List<String> parameters = new ArrayList<String>();
		for (Iterator<TGChannelParameter> it = channel.getParameters(); it.hasNext();) {
			TGChannelParameter parameter = it.next();
			parameters.add(parameter.getKey() + "=" + parameter.getValue());
		}
		return channel.getBank() + " " + channel.getProgram() + " " + channel.getVolume() + " " + channel.getBalance() + " " + channel.getChorus() + " "
				+ channel.getReverb() + " " + channel.getPhaser() + " " + channel.getTremolo() + " / " + channel.getName() + " / " + parameters
				+ (updates.contains(TGUpdateEvent.VOLUME_CHANGED) ? " / volume event" : "")
				+ (Boolean.TRUE.equals(fixture.actionContext.getAttribute(TGActionBase.ATTRIBUTE_SUCCESS)) ? " / success" : "");
	}

	// the same update, on the channel made an audio channel
	private String updateAudio(Consumer<TGActionContext> given) {
		channel.setAudioInput(TGAudioInput.mono(1));
		return update(given);
	}

	@TestFactory
	public List<DynamicTest> updatingTheChannel() {
		return Arrays.asList(
			updating("every given setting is taken", "1 2 3 4 5 6 7 8 / Lead / [k=v] / volume event / success", EVERY_SETTING),
			updating("missing settings are kept", "0 25 127 64 0 0 0 0 / " + DEFAULT_NAME + " / [k=v] / success", NOTHING),
			updating("another volume fires the event", "0 25 100 64 0 0 0 0 / " + DEFAULT_NAME + " / [k=v] / volume event / success", volume(100)),
			updating("the same volume fires none", "0 25 127 64 0 0 0 0 / " + DEFAULT_NAME + " / [k=v] / success", volume(127)),
			reads("an audio channel takes another volume", "0 25 100 64 0 0 0 0 / " + DEFAULT_NAME + " / [k=v] / volume event / success",
				() -> new TestUpdateChannelAction().updateAudio(volume(100))),
			reads("an audio channel takes no other program", "0 25 127 64 0 0 0 0 / " + DEFAULT_NAME + " / [k=v]",
				() -> new TestUpdateChannelAction().updateAudio(context -> context.setAttribute(TGUpdateChannelAction.ATTRIBUTE_PROGRAM, Short.valueOf((short) 30)))),
			reads("an audio channel takes no other bank", "0 25 127 64 0 0 0 0 / " + DEFAULT_NAME + " / [k=v]",
				() -> new TestUpdateChannelAction().updateAudio(context -> context.setAttribute(TGUpdateChannelAction.ATTRIBUTE_BANK, TGChannel.DEFAULT_PERCUSSION_BANK))),
			reads("given parameters replace the old ones", "0 25 127 64 0 0 0 0 / " + DEFAULT_NAME + " / [a=1, b=2] / success", () -> {
				TestUpdateChannelAction test = new TestUpdateChannelAction();
				return test.update(context -> context.setAttribute(TGUpdateChannelAction.ATTRIBUTE_PARAMETERS, Arrays.asList(test.parameter("a", "1"), test.parameter("b", "2"))));
			}));
	}

	private static Consumer<TGActionContext> volume(int volume) {
		return context -> context.setAttribute(TGUpdateChannelAction.ATTRIBUTE_VOLUME, Short.valueOf((short) volume));
	}

	private static DynamicTest updating(String name, String expected, Consumer<TGActionContext> given) {
		return reads(name, expected, () -> new TestUpdateChannelAction().update(given));
	}
}
