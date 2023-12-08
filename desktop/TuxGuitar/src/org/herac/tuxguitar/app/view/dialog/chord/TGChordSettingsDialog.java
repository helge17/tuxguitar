package org.herac.tuxguitar.app.view.dialog.chord;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UICheckBox;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UISpinner;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

/**
 * Dialog for customizing chord criteria parameters
 * 
 * @author Nikola Kolarovic
 *
 */
public class TGChordSettingsDialog {
	
	private TGContext context;
	private UIWindow dialog;
	private UICheckBox emptyStringChords;
	private UISpinner chordsToDisplay;
	private UIDropDownSelect<Integer> typeCombo;
	private UISpinner minFret;
	private UISpinner maxFret;
	
	public TGChordSettingsDialog(TGContext context) {
		this.context = context;
	}
	
	public void open(UIWindow parent, final TGChordSettingsHandler handler){
		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UITableLayout dialogLayout = new UITableLayout();
		
		this.dialog = uiFactory.createWindow(parent, true, false);
		this.dialog.setLayout(dialogLayout);
		this.dialog.setText(TuxGuitar.getProperty("settings"));
		
		UITableLayout groupLayout = new UITableLayout();
		UILegendPanel group = uiFactory.createLegendPanel(dialog);
		group.setLayout(groupLayout);
		group.setText(TuxGuitar.getProperty("chord.settings.tip"));
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		initTypeCombo(uiFactory, group, 1);
		initChordsToDisplay(uiFactory, group, 2);
		initEmptyStringChords(uiFactory, group, 3);
		initFretSearch(uiFactory, group, 4);
		
		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(this.dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		final UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGChordSettingsDialog.this.updateAndDispose(handler);
			}
		});
		buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		
		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGChordSettingsDialog.this.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);
		
		TGDialogUtil.openDialog(this.dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	private void initTypeCombo(UIFactory factory, UILayoutContainer parent, int row) {
		UILabel label = factory.createLabel(parent);
		label.setText(TuxGuitar.getProperty("chord.settings.type"));
		
		this.typeCombo = factory.createDropDownSelect(parent);
		this.typeCombo.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("chord.settings.type.most-common"), 0));
		this.typeCombo.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("chord.settings.type.inversions"), 1));
		this.typeCombo.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("chord.settings.type.close-voiced"), 2));
		this.typeCombo.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("chord.settings.type.open-voiced"), 3));
		this.typeCombo.setSelectedValue(TGChordSettings.instance().getChordTypeIndex());
		
		UITableLayout uiLayout = (UITableLayout) parent.getLayout();
		uiLayout.set(label, row, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		uiLayout.set(this.typeCombo, row, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 200f, null, null);
	}
	
	private void initChordsToDisplay(UIFactory factory, UILayoutContainer parent, int row) {
		UILabel label = factory.createLabel(parent);
		label.setText(TuxGuitar.getProperty("chord.settings.chords-to-display"));
		
		this.chordsToDisplay = factory.createSpinner(parent);
		this.chordsToDisplay.setMinimum(1);
		this.chordsToDisplay.setMaximum(100);
		this.chordsToDisplay.setValue(TGChordSettings.instance().getChordsToDisplay());
		
		UITableLayout uiLayout = (UITableLayout) parent.getLayout();
		uiLayout.set(label, row, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		uiLayout.set(this.chordsToDisplay, row, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 200f, null, null);
	}
	
	private void initEmptyStringChords(UIFactory factory, UILayoutContainer parent, int row) {
		this.emptyStringChords = factory.createCheckBox(parent);
		this.emptyStringChords.setSelected(TGChordSettings.instance().isEmptyStringChords());
		this.emptyStringChords.setText(TuxGuitar.getProperty("chord.settings.open-chords"));
		
		UITableLayout uiLayout = (UITableLayout) parent.getLayout();
		uiLayout.set(this.emptyStringChords, row, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, 2);
	}
	
	private void initFretSearch(UIFactory factory, UILayoutContainer parent, int row) {
		UITableLayout groupLayout = new UITableLayout();
		UILegendPanel group = factory.createLegendPanel(parent);
		group.setLayout(groupLayout);
		group.setText(TuxGuitar.getProperty("chord.settings.search-frets"));
		UITableLayout uiLayout = (UITableLayout) parent.getLayout();
		uiLayout.set(group, row, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, 2);
		
		UILabel minFretLabel = factory.createLabel(group);
		minFretLabel.setText(TuxGuitar.getProperty("chord.settings.minimum-fret"));
		
		this.minFret = factory.createSpinner(group);
		this.minFret.setMinimum(0);
		this.minFret.setMaximum(15);
		this.minFret.setValue(TGChordSettings.instance().getFindChordsMin());
		
		UILabel maxFretLabel = factory.createLabel(group);
		maxFretLabel.setText(TuxGuitar.getProperty("chord.settings.maximum-fret"));
		
		this.maxFret = factory.createSpinner(group);
		this.maxFret.setMinimum(2);
		this.maxFret.setMaximum(25);
		this.maxFret.setValue(TGChordSettings.instance().getFindChordsMax());
		
		this.minFret.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				checkMinimumFretValue();
			}
		});
		this.maxFret.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				checkMaximumFretValue();
			}
		});
		
		groupLayout.set(minFretLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
		groupLayout.set(this.minFret, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		groupLayout.set(maxFretLabel, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
		groupLayout.set(this.maxFret, 1, 4, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
	}
	
	private void checkMinimumFretValue(){
		int maxSelection = this.maxFret.getValue();
		int minSelection = this.minFret.getValue();
		if(maxSelection < minSelection){
			this.maxFret.setValue(minSelection);
		}
	}
	
	private void checkMaximumFretValue(){
		int maxSelection = this.maxFret.getValue();
		int minSelection = this.minFret.getValue();
		if(maxSelection < minSelection){
			this.maxFret.setValue(minSelection);
		}
	}
	
	private void update(){
		Integer type = this.typeCombo.getSelectedValue();
		TGChordSettings.instance().setChordTypeIndex(type != null ? type : 0);
		TGChordSettings.instance().setEmptyStringChords(this.emptyStringChords.isSelected());
		TGChordSettings.instance().setChordsToDisplay(this.chordsToDisplay.getValue());
		TGChordSettings.instance().setFindChordsMax(this.maxFret.getValue());
		TGChordSettings.instance().setFindChordsMin(this.minFret.getValue());
	}
	
	private void dispose() {
		this.dialog.dispose();
	}
	
	private void updateAndDispose(TGChordSettingsHandler handler){
		this.update();
		this.dispose();
		if( handler != null ) {
			handler.onSettingsUpdated();
		}
	}
}
