package app.tuxguitar.io.synth;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.midi.synth.TGSynthAudioExporter;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UIDropDownSelect;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UISelectItem;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

/**
 * The audio export's options when an audio exporter is registered: one of the formats it offers (the first chosen).
 */
public class TGSynthExportDialog {

	private final TGContext context;
	private final UIFactory factory;
	private UIWindow dialog;
	private UIDropDownSelect<String> formats;

	public TGSynthExportDialog(TGContext context) {
		this.context = context;
		this.factory = TGApplication.getInstance(context).getFactory();
	}

	public void open(TGSynthAudioExporter exporter, TGSynthAudioSettings settings, Runnable onSuccess) {
		UITableLayout layout = new UITableLayout();
		this.dialog = this.factory.createWindow(TGWindow.getInstance(this.context).getWindow(), true, false);
		this.dialog.setLayout(layout);
		this.dialog.setText(TuxGuitar.getProperty("tuxguitar-synth-export.options"));
		UILabel label = this.factory.createLabel(this.dialog);
		label.setText(TuxGuitar.getProperty("tuxguitar-synth-export.options.audio-format") + ":");
		layout.set(label, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		this.formats = this.factory.createDropDownSelect(this.dialog);
		exporter.getFormats().forEach(format -> this.formats.addItem(new UISelectItem<String>(format, format)));
		this.formats.setSelectedValue(exporter.getFormats().get(0));
		layout.set(this.formats, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false, 1, 2, 200f, null, null);
		this.button(2, 2, "ok", () -> this.choose(exporter, settings, onSuccess));
		this.button(2, 3, "cancel", this.dialog::dispose);
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	private void button(int row, int column, String key, Runnable action) {
		UIButton button = this.factory.createButton(this.dialog);
		button.setText(TuxGuitar.getProperty(key));
		button.addSelectionListener(event -> action.run());
		((UITableLayout) this.dialog.getLayout()).set(button, row, column, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false, 1, 1, 80f, 25f, null);
	}

	private void choose(TGSynthAudioExporter exporter, TGSynthAudioSettings settings, Runnable onSuccess) {
		String format = this.formats.getSelectedValue();
		settings.setExport(format, exporter.getExtension(format));
		this.dialog.dispose();
		onSuccess.run();
	}
}
