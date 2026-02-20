package app.tuxguitar.io.midi;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.io.persistence.TGPersistenceSettingsMode;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.player.base.MidiSequenceParser;
import app.tuxguitar.song.models.TGDuration;
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
import app.tuxguitar.ui.widget.UIRadioButton;
import app.tuxguitar.ui.widget.UISelectItem;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

public class MidiSettingsDialog {

	public static final int MAX_TRANSPOSE = 24;
	public static final int MIN_TRANSPOSE = -24;

	// static, to keep values when dialog is closed / reopened
	private static int maxDurationValue = TGDuration.SIXTEENTH;
	private static int maxDivision = 3;

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

		int optionRow = 1;

		//------------------ transposition------------------
		UITableLayout groupLayoutTranspose = new UITableLayout();
		UILegendPanel groupTranspose = uiFactory.createLegendPanel(dialog);
		groupTranspose.setLayout(groupLayoutTranspose);
		if (mode == TGPersistenceSettingsMode.WRITE) {
			groupTranspose.setText(TuxGuitar.getProperty("export.transpose-notes"));
		} else {
			groupTranspose.setText(TuxGuitar.getProperty("import.transpose-notes"));
		}
		dialogLayout.set(groupTranspose, optionRow, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 320f, null, null);
		optionRow ++;

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

		//------------------ quantization options (import only) ------------------
		if (mode == TGPersistenceSettingsMode.READ) {
			UILegendPanel groupQuantization = uiFactory.createLegendPanel(dialog);
			TGIconManager iconManager = TGIconManager.getInstance(context);
			UITableLayout groupQuantizationLayout = new UITableLayout();
			groupQuantization.setLayout(groupQuantizationLayout);
			groupQuantization.setText(TuxGuitar.getProperty("import.quantization"));
			dialogLayout.set(groupQuantization, optionRow, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 320f, null, null);
			optionRow ++;

			// durations
			UIPanel groupDuration = uiFactory.createPanel(groupQuantization, false);
			UITableLayout groupDurationLayout = new UITableLayout();
			groupDuration.setLayout(groupDurationLayout);
			groupQuantizationLayout.set(groupDuration, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);

			UILabel labelMaxDuration = uiFactory.createLabel(groupDuration);
			labelMaxDuration.setText(TuxGuitar.getProperty("import.quantization.max-duration-value"));
			groupDurationLayout.set(labelMaxDuration, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, true, false);

			List<OptionItem> durationItems = new ArrayList<OptionItem>();
			durationItems.add(new OptionItem(TGDuration.QUARTER, "4.png", "duration.quarter"));
			durationItems.add(new OptionItem(TGDuration.EIGHTH, "8.png", "duration.eighth"));
			durationItems.add(new OptionItem(TGDuration.SIXTEENTH, "16.png", "duration.sixteenth"));
			durationItems.add(new OptionItem(TGDuration.THIRTY_SECOND, "32.png", "duration.thirtysecond"));
			durationItems.add(new OptionItem(TGDuration.SIXTY_FOURTH, "64.png", "duration.sixtyfourth"));
			int column = 2;
			for (OptionItem item : durationItems) {
				UIRadioButton button = uiFactory.createRadioButton(groupDuration);
				button.setImage(iconManager.getImageByName(item.getIconName()));
				button.setToolTipText(TuxGuitar.getProperty(item.getText()));
				button.setSelected(maxDurationValue == item.getValue());
				button.addSelectionListener(new UISelectionListener() {
					@Override
					public void onSelect(UISelectionEvent event) {
						MidiSettingsDialog.maxDurationValue = item.getValue();
					}
				});
				groupDurationLayout.set(button, 1, column, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
				column ++;
			}

			// divisions
			UIPanel groupDivision = uiFactory.createPanel(groupQuantization, false);
			UITableLayout groupDivisionLayout = new UITableLayout();
			groupDivision.setLayout(groupDivisionLayout);
			groupQuantizationLayout.set(groupDivision, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);

			UILabel labelMaxDivision = uiFactory.createLabel(groupDivision);
			labelMaxDivision.setText(TuxGuitar.getProperty("import.quantization.max-division"));
			groupDivisionLayout.set(labelMaxDivision, 2, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, true, false);

			List<OptionItem> divisionItems = new ArrayList<OptionItem>();
			divisionItems.add(new OptionItem(1, "division-type-1.png", "duration.division-type.1"));
			divisionItems.add(new OptionItem(3, "division-type-3.png", "duration.division-type.3"));
			divisionItems.add(new OptionItem(5, "division-type-5.png", "duration.division-type.5"));
			// don't go beyond 5: 7-9-11-...etc lead to rounding errors in MIDI import
			column = 2;
			for (OptionItem item : divisionItems) {
				UIRadioButton button = uiFactory.createRadioButton(groupDivision);
				button.setImage(iconManager.getImageByName(item.getIconName()));
				button.setToolTipText(TuxGuitar.getProperty(item.getText()));
				button.setSelected(maxDivision == item.getValue());
				button.addSelectionListener(new UISelectionListener() {
					@Override
					public void onSelect(UISelectionEvent event) {
						MidiSettingsDialog.maxDivision = item.getValue();
					}
				});
				groupDivisionLayout.set(button, 2, column, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
				column ++;
			}
		}

		//------------------ write options (export only) ------------------
		UILegendPanel groupOptions = uiFactory.createLegendPanel(dialog);
		final UICheckBox strip_CC_PC = uiFactory.createCheckBox(groupOptions);
		if (mode == TGPersistenceSettingsMode.WRITE) {
			UITableLayout groupOptionsLayout = new UITableLayout();
			groupOptions.setLayout(groupOptionsLayout);
			groupOptions.setText(TuxGuitar.getProperty("options"));
			dialogLayout.set(groupOptions, optionRow, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 320f, null, null);
			optionRow++;

			strip_CC_PC.setText(TuxGuitar.getProperty("export.midi.strip-cc-pc"));
			strip_CC_PC.setSelected(false);
			groupOptionsLayout.set(strip_CC_PC, 2, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, true, false);
		}

		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, optionRow, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		optionRow ++;

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
				if (mode == TGPersistenceSettingsMode.READ) {
					settings.setMaxDurationValue(maxDurationValue);
					settings.setMaxDivision(maxDivision);
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

	private class OptionItem {
		private int value;
		private String iconName;
		private String text;
		public OptionItem (int value, String iconName, String text) {
			this.value = value;
			this.iconName = iconName;
			this.text = text;
		}
		protected int getValue() {
			return this.value;
		}
		protected String getIconName() {
			return this.iconName;
		}
		protected String getText() {
			return this.text;
		}
	}

}
