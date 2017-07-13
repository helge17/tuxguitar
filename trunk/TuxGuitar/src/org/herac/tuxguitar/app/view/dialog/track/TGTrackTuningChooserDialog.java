package org.herac.tuxguitar.app.view.dialog.track;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.TGMessageDialogUtil;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIReadOnlyTextField;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UIWindow;

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
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("tuning"));
		
		//-------------MAIN PANEL-----------------------------------------------
		UITableLayout panelLayout = new UITableLayout();
		UILegendPanel panel = uiFactory.createLegendPanel(dialog);
		panel.setLayout(panelLayout);
		panel.setText(TuxGuitar.getProperty("tuning"));
		dialogLayout.set(panel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		// value
		UILabel tuningValueLabel = uiFactory.createLabel(panel);
		tuningValueLabel.setText(TuxGuitar.getProperty("tuning.value"));
		panelLayout.set(tuningValueLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		
		final UIDropDownSelect<Integer> tuningValueControl = uiFactory.createDropDownSelect(panel);
		tuningValueControl.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("tuning.value.select")));
		
		String[] tuningTexts = getValueLabels();
		for(int value = 0 ; value < tuningTexts.length ; value ++) {
			tuningValueControl.addItem(new UISelectItem<Integer>(tuningTexts[value], value));
		}
		
		for (int i = 1; i <= 32; i++) {
			tuningValueControl.addItem(new UISelectItem<Integer>(Integer.toString(i), i));
		}
		tuningValueControl.setSelectedValue(model != null ? model.getValue() : null);
		panelLayout.set(tuningValueControl, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 150f, null, null);
		
		// label
		UILabel tuningLabelLabel = uiFactory.createLabel(panel);
		tuningLabelLabel.setText(TuxGuitar.getProperty("tuning.label"));
		panelLayout.set(tuningLabelLabel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		
		final UIReadOnlyTextField tuningLabelControl = uiFactory.createReadOnlyTextField(panel);
		if( model != null ) {
			tuningLabelControl.setText(getValueLabel(model.getValue()));
		}
		panelLayout.set(tuningLabelControl, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 150f, null, null);
		
		tuningValueControl.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				tuningLabelControl.setText(getValueLabel(tuningValueControl.getSelectedValue()));
			}
		});
		
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
				if( handleSelection(handler, dialog, tuningValueControl) ) {
					dialog.dispose();
				}
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
	
	public boolean handleSelection(TGTrackTuningChooserHandler handler, UIWindow dialog, UIDropDownSelect<Integer> value) {
		TGTrackTuningModel model = new TGTrackTuningModel();
		model.setValue(value.getSelectedValue());
		
		if( model.getValue() == null ){
			TGMessageDialogUtil.errorMessage(this.tuningDialog.getContext().getContext(), dialog, TuxGuitar.getProperty("tuning.value.empty-error"));
			return false;
		}
		handler.handleSelection(model);
		
		return true;
	}
	
	private String[] getValueLabels() {
		return this.tuningDialog.getValueLabels();
	}
	
	private String getValueLabel(Integer value) {
		return this.tuningDialog.getValueLabel(value);
	}
}
