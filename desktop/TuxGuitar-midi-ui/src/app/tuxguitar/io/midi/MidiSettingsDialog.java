package app.tuxguitar.io.midi;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.io.persistence.TGPersistenceSettingsMode;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.player.base.MidiSequenceParser;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UICheckBox;
import app.tuxguitar.ui.widget.UIDropDownSelect;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UILegendPanel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UISelectItem;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

public class MidiSettingsDialog {

	public static final int MAX_TRANSPOSE = 24;
	public static final int MIN_TRANSPOSE = -24;

	private TGContext context;
	private TGPersistenceSettingsMode mode;

	public MidiSettingsDialog(TGContext context, TGPersistenceSettingsMode mode){
		this.context = context;
		this.mode = mode;
		}

	public void open(final MidiSettings settings, final Runnable onSuccess) {
		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UIWindow parent = TGWindow.getInstance(this.context).getWindow();
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(parent, true, false);

		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("options"));

		//------------------ transposition------------------
		UITableLayout groupLayoutTranspose = new UITableLayout();
		UILegendPanel groupTranspose = uiFactory.createLegendPanel(dialog);
		groupTranspose.setLayout(groupLayoutTranspose);
		if (mode == TGPersistenceSettingsMode.WRITE) {
			groupTranspose.setText(TuxGuitar.getProperty("export.transpose-notes"));
		} else {
			groupTranspose.setText(TuxGuitar.getProperty("import.transpose-notes"));
		}
		dialogLayout.set(groupTranspose, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 320f, null, null);

		final UILabel transposeLabel = uiFactory.createLabel(groupTranspose);
		if (mode == TGPersistenceSettingsMode.WRITE) {
			transposeLabel.setText(TuxGuitar.getProperty("export.transpose") + ":");
		} else {
			transposeLabel.setText(TuxGuitar.getProperty("import.transpose") + ":");
		}
		groupLayoutTranspose.set(transposeLabel, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);

		final UIDropDownSelect<Integer> transposeCombo = uiFactory.createDropDownSelect(groupTranspose);
		for(int i = MIN_TRANSPOSE;i <= MAX_TRANSPOSE;i ++){
			transposeCombo.addItem(new UISelectItem<Integer>(Integer.toString(i), i));
		}
		transposeCombo.setSelectedValue(0);
		groupLayoutTranspose.set(transposeCombo, 1, 2, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, true, false);

		//------------------ write options------------------
		UILegendPanel groupOptions = uiFactory.createLegendPanel(dialog);
		final UICheckBox strip_CC_PC = uiFactory.createCheckBox(groupOptions);
		if (mode == TGPersistenceSettingsMode.WRITE) {
			UITableLayout groupOptionsLayout = new UITableLayout();
			groupOptions.setLayout(groupOptionsLayout);
			groupOptions.setText(TuxGuitar.getProperty("options"));
			dialogLayout.set(groupOptions, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 320f, null, null);
	
			strip_CC_PC.setText(TuxGuitar.getProperty("export.midi.strip-cc-pc"));
			strip_CC_PC.setSelected(false);
			groupOptionsLayout.set(strip_CC_PC, 2, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, true, false);
		}

		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);

		final UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				Integer transposition = transposeCombo.getSelectedValue();
				settings.setTranspose(transposition != null ? transposition : 0);
				if ((mode == TGPersistenceSettingsMode.WRITE) && strip_CC_PC.isSelected()) {
					settings.setFlags(MidiSequenceParser.NO_CONTROL_CHANGE_PROGRAM_CHANGE);
				}
				dialog.dispose();
				onSuccess.run();
			}
		});
		buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);

		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);

		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
}
