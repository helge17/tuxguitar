package app.tuxguitar.audio.ui;

import java.util.Arrays;
import java.util.stream.IntStream;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.audio.TGAudioSession;
import app.tuxguitar.audio.TGInputChoice;
import app.tuxguitar.audio.TGLiveAudio;
import app.tuxguitar.audio.TGMonitoring;
import app.tuxguitar.audio.action.TGSetInputChannelsAction;
import app.tuxguitar.audio.action.TGSetMonitoringAction;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIDropDownSelect;
import app.tuxguitar.ui.widget.UISelectItem;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

/**
 * An audio track's settings: its monitoring (Off, Auto, Always: session state, no undo step) and the input channel(s)
 * it records from (a channel, and a second one for a stereo pair: song data, one undo step, and only when changed).
 * The channels offered are the device's, and at least those the track records from now (TGInputChoice).
 */
public class TGAudioTrackSettingsDialog {

	private final TGContext context;
	private final TGTrack track;
	private final TGChannel channel;
	private final UIFactory factory;
	private UIWindow dialog;
	private UITableLayout layout;
	private UIDropDownSelect<TGMonitoring> monitoring;
	private UIDropDownSelect<Integer> first;
	private UIDropDownSelect<Integer> second;

	public TGAudioTrackSettingsDialog(TGContext context, TGTrack track) {
		TGDocumentManager document = TGDocumentManager.getInstance(context);
		this.context = context;
		this.track = track;
		this.channel = document.getSongManager().getChannel(document.getSong(), track.getChannelId());
		this.factory = TGApplication.getInstance(context).getFactory();
	}

	public void open() {
		TGAudioInput input = this.channel.getAudioInput();
		int channels = TGInputChoice.countChannels(TGLiveAudio.getInstance(this.context).countInputChannels(), input);
		this.layout = new UITableLayout();
		this.dialog = this.factory.createWindow(TGWindow.getInstance(this.context).getWindow(), true, false);
		this.dialog.setLayout(this.layout);
		this.dialog.setText(TuxGuitar.getProperty("audio.track-settings.title") + ": " + this.track.getName());
		this.monitoring = this.select(1, "audio.track-settings.monitoring");
		Arrays.stream(TGMonitoring.values()).forEach(value -> this.monitoring.addItem(new UISelectItem<TGMonitoring>(TuxGuitar.getProperty("audio.track-settings.monitoring." + value.name().toLowerCase()), value)));
		this.monitoring.setSelectedValue(TGAudioSession.getInstance(this.context).getMonitoring(this.track));
		this.first = this.select(2, "audio.track-settings.input");
		this.addChannels(this.first, channels);
		this.first.setSelectedValue(input.getChannel(0));
		this.second = this.select(3, "audio.track-settings.second");
		this.second.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("audio.track-settings.mono"), TGInputChoice.NONE));
		this.addChannels(this.second, channels);
		this.second.setSelectedValue(TGInputChoice.second(input));
		TGDialogParts.button(this.factory, this.dialog, 4, 1, "ok", this::apply);
		TGDialogParts.button(this.factory, this.dialog, 4, 2, "cancel", this.dialog::dispose);
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	// a label and a drop-down on the row
	private <T> UIDropDownSelect<T> select(int row, String key) {
		TGDialogParts.label(this.factory, this.dialog, row, key);
		UIDropDownSelect<T> select = this.factory.createDropDownSelect(this.dialog);
		this.layout.set(select, row, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false, 1, 1, 160f, null, null);
		return select;
	}

	// the input channels from 1
	private void addChannels(UIDropDownSelect<Integer> select, int channels) {
		IntStream.rangeClosed(1, channels).forEach(number -> select.addItem(new UISelectItem<Integer>(Integer.toString(number), number)));
	}

	// the monitoring chosen; the input channels picked, when they change the track's
	private void apply() {
		TGActionProcessor monitoring = new TGActionProcessor(this.context, TGSetMonitoringAction.NAME);
		monitoring.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, this.track);
		monitoring.setAttribute(TGSetMonitoringAction.ATTRIBUTE_MONITORING, this.monitoring.getSelectedValue());
		monitoring.process();
		int[] picked = TGInputChoice.pick(this.first.getSelectedValue(), this.second.getSelectedValue());
		if (TGInputChoice.changes(this.channel.getAudioInput(), picked)) {
			TGActionProcessor input = new TGActionProcessor(this.context, TGSetInputChannelsAction.NAME);
			input.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, this.channel);
			input.setAttribute(TGSetInputChannelsAction.ATTRIBUTE_INPUT_CHANNELS, picked);
			input.process();
		}
		this.dialog.dispose();
	}
}
