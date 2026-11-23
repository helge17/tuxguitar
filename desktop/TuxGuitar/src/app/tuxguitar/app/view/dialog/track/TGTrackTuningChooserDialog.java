package app.tuxguitar.app.view.dialog.track;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.*;
import app.tuxguitar.util.TGMusicKeyUtils;

public class TGTrackTuningChooserDialog {

	private TGTrackTuningDialog tuningDialog;

	public TGTrackTuningChooserDialog(TGTrackTuningDialog tuningDialog){
		this.tuningDialog = tuningDialog;
	}

	public void select(final TGTrackTuningChooserHandler handler) {
		this.select(handler, null);
	}

	public void select(final TGTrackTuningChooserHandler handler, TGTrackTuningModel model) {
		final UIFactory uiFactory = this.tuningDialog.getUIFactory();
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(this.tuningDialog.getDialog(), true, false);
		final int initialValue = getInitialValue(model);

		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty(model != null ? "tuning.string.edit" : "tuning.string.add"));

		//-------------MAIN PANEL-----------------------------------------------
		UITableLayout panelLayout = new UITableLayout();
		UILegendPanel panel = uiFactory.createLegendPanel(dialog);
		panel.setLayout(panelLayout);
		panel.setText(TuxGuitar.getProperty("tuning"));
		dialogLayout.set(panel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		// value
		UILabel tuningValueLabel = uiFactory.createLabel(panel);
		tuningValueLabel.setText(TuxGuitar.getProperty("tuning.value") + ":");
		panelLayout.set(tuningValueLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_RIGHT, false, true);

		final UIDropDownSelect<Integer> tuningValueControl = uiFactory.createDropDownSelect(panel);
		for(int value = TGMusicKeyUtils.MIN_MIDI_NOTE ; value <= TGMusicKeyUtils.MAX_MIDI_NOTE ; value ++) {
			tuningValueControl.addItem(new UISelectItem<Integer>(TGMusicKeyUtils.sharpNoteFullName(value), value));
		}

		tuningValueControl.setSelectedValue(initialValue);
		panelLayout.set(tuningValueControl, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 150f, null, null);

		// value spinner
		UILabel tuningSpinnerLabel = uiFactory.createLabel(panel);
		tuningSpinnerLabel.setText(TuxGuitar.getProperty("tuning.midi-note") + ":");
		panelLayout.set(tuningSpinnerLabel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_RIGHT, false, true);

		final UISpinner tuningValueSpinner = uiFactory.createSpinner(panel);
		tuningValueSpinner.setMinimum(TGMusicKeyUtils.MIN_MIDI_NOTE);
		tuningValueSpinner.setMaximum(TGMusicKeyUtils.MAX_MIDI_NOTE);
		tuningValueSpinner.setValue(initialValue);
		panelLayout.set(tuningValueSpinner, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 150f, null, null);

		// label
		UILabel tuningLabelLabel = uiFactory.createLabel(panel);
		tuningLabelLabel.setText(TuxGuitar.getProperty("tuning.label") + ":");
		panelLayout.set(tuningLabelLabel, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_RIGHT, false, true);

		final UIReadOnlyTextField tuningLabelControl = uiFactory.createReadOnlyTextField(panel);
		tuningLabelControl.setText(TGMusicKeyUtils.sharpNoteName(initialValue));
		panelLayout.set(tuningLabelControl, 3, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 150f, null, null);

		tuningValueControl.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				int noteValue = tuningValueControl.getSelectedValue();
				tuningLabelControl.setText(TGMusicKeyUtils.sharpNoteName(noteValue));
				tuningValueSpinner.setValue(noteValue);
			}
		});
		tuningValueSpinner.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				tuningValueControl.setSelectedValue(tuningValueSpinner.getValue());
				tuningLabelControl.setText(TGMusicKeyUtils.sharpNoteName(tuningValueControl.getSelectedValue()));
			}
		});

		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);

		UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTrackTuningModel model = new TGTrackTuningModel();
				model.setValue(tuningValueControl.getSelectedValue());
				handler.handleSelection(model);
				dialog.dispose();
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

	private static int getInitialValue(TGTrackTuningModel model) {
		int value = (model != null ? model.getValue() : TGMusicKeyUtils.MIN_MIDI_NOTE);
		return Math.max(TGMusicKeyUtils.MIN_MIDI_NOTE, Math.min(TGMusicKeyUtils.MAX_MIDI_NOTE, value));
	}

}
