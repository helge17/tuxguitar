package app.tuxguitar.audio.ui;

import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.audio.TGAudioSettings;
import app.tuxguitar.audio.TGLiveAudio;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIDropDownSelect;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UISelectItem;
import app.tuxguitar.ui.widget.UISpinner;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.configuration.TGConfigManager;

/**
 * The audio device settings: what the device does now (or why there is none), the host API, the input and output
 * devices of that host API (or the default ones), the input channels and the recording offset. OK keeps them and opens
 * those devices.
 */
public class TGAudioSettingsDialog {

	private final TGContext context;
	private final TGLiveAudio audio;
	private final UIFactory factory;
	private UIWindow dialog;
	private UITableLayout layout;
	private UIDropDownSelect<String> hostApi;
	private UIDropDownSelect<String> input;
	private UIDropDownSelect<String> output;
	private UISpinner inputChannels;
	private UISpinner recordingOffset;

	public TGAudioSettingsDialog(TGContext context) {
		this.context = context;
		this.audio = TGLiveAudio.getInstance(context);
		this.factory = TGApplication.getInstance(context).getFactory();
	}

	public void open() {
		TGAudioSettings settings = TGAudioSettings.read(TGAudioPlugin.config(this.context));
		this.layout = new UITableLayout();
		this.dialog = this.factory.createWindow(TGWindow.getInstance(this.context).getWindow(), true, false);
		this.dialog.setLayout(this.layout);
		this.dialog.setText(TuxGuitar.getProperty("audio.settings.title"));
		UILabel status = this.factory.createLabel(this.dialog);
		status.setText(this.audio.describe());
		this.layout.set(status, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false, 1, 2);
		this.hostApi = this.select(2, "audio.settings.host-api", this.audio.getHostApis(), settings.getHostApi());
		this.input = this.select(3, "audio.settings.input", this.audio.getInputs(settings.getHostApi()), settings.getInput());
		this.output = this.select(4, "audio.settings.output", this.audio.getOutputs(settings.getHostApi()), settings.getOutput());
		this.hostApi.addSelectionListener(event -> this.listDevices());
		this.inputChannels = this.spinner(5, "audio.settings.input-channels", 0, 64, settings.getInputChannels());
		this.recordingOffset = this.spinner(6, "audio.settings.recording-offset", -96000, 96000, settings.getRecordingOffset());
		TGDialogParts.button(this.factory, this.dialog, 7, 1, "ok", this::keep);
		TGDialogParts.button(this.factory, this.dialog, 7, 2, "cancel", this.dialog::dispose);
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	// the default ("") first, then the names
	private UIDropDownSelect<String> select(int row, String key, List<String> names, String selected) {
		TGDialogParts.label(this.factory, this.dialog, row, key);
		UIDropDownSelect<String> select = this.factory.createDropDownSelect(this.dialog);
		this.layout.set(select, row, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false, 1, 1, 320f, null, null);
		fill(select, names, selected);
		return select;
	}

	private static void fill(UIDropDownSelect<String> select, List<String> names, String selected) {
		select.removeItems();
		select.addItem(new UISelectItem<String>(TuxGuitar.getProperty("audio.settings.default"), ""));
		for (String name : names) {
			select.addItem(new UISelectItem<String>(name, name));
		}
		select.setSelectedValue(selected);
	}

	// the devices of the host API chosen
	private void listDevices() {
		fill(this.input, this.audio.getInputs(this.hostApi.getSelectedValue()), "");
		fill(this.output, this.audio.getOutputs(this.hostApi.getSelectedValue()), "");
	}

	private UISpinner spinner(int row, String key, int minimum, int maximum, int value) {
		TGDialogParts.label(this.factory, this.dialog, row, key);
		UISpinner spinner = this.factory.createSpinner(this.dialog);
		spinner.setMinimum(minimum);
		spinner.setMaximum(maximum);
		spinner.setValue(value);
		this.layout.set(spinner, row, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		return spinner;
	}

	// the settings are kept, and their devices open
	private void keep() {
		TGAudioSettings settings = new TGAudioSettings(this.hostApi.getSelectedValue(), this.input.getSelectedValue(), this.output.getSelectedValue(), this.inputChannels.getValue(), this.recordingOffset.getValue());
		TGConfigManager config = TGAudioPlugin.config(this.context);
		settings.write(config);
		config.save();
		this.dialog.dispose();
		TGAudioPlugin.open(this.context, settings);
	}
}
