package org.herac.tuxguitar.app.view.dialog.printer;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.print.TGPrintSettings;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UICheckBox;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UISpinner;
import org.herac.tuxguitar.ui.widget.UIWindow;

public class TGPrintSettingsDialog {

	public static final String ATTRIBUTE_HANDLER = TGPrintSettingsHandler.class.getName();
	
	public void show(final TGViewContext context) {
		final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGPrintSettingsHandler handler = context.getAttribute(ATTRIBUTE_HANDLER);
		
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT2);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("options"));
		
		//------------------TRACK SELECTION------------------
		UITableLayout trackLayout = new UITableLayout();
		UILegendPanel track = uiFactory.createLegendPanel(dialog);
		track.setLayout(trackLayout);
		track.setText(TuxGuitar.getProperty("track"));
		dialogLayout.set(track, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 300f, null, null);
		
		final UILabel trackLabel = uiFactory.createLabel(track);
		trackLabel.setText(TuxGuitar.getProperty("track"));
		trackLayout.set(trackLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		
		final UIDropDownSelect<Integer> trackCombo = uiFactory.createDropDownSelect(track);
		for(int number = 1; number <= song.countTracks(); number ++){
			trackCombo.addItem(new UISelectItem<Integer>(TuxGuitar.getInstance().getSongManager().getTrack(song, number).getName(), number));
		}
		trackCombo.setSelectedValue(TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack().getNumber());
		trackLayout.set(trackCombo, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UICheckBox trackAllCheck = uiFactory.createCheckBox(track);
		trackAllCheck.setText(TuxGuitar.getProperty("edit.all-tracks"));
		trackAllCheck.setSelected(false);
		trackAllCheck.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				trackLabel.setEnabled( !trackAllCheck.isSelected() );
				trackCombo.setEnabled( !trackAllCheck.isSelected() );
			}
		});
		trackLayout.set(trackAllCheck, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false, 1, 2);
		
		//------------------MEASURE RANGE------------------
		UITableLayout rangeLayout = new UITableLayout();
		UILegendPanel range = uiFactory.createLegendPanel(dialog);
		range.setLayout(rangeLayout);
		range.setText(TuxGuitar.getProperty("print.range"));
		dialogLayout.set(range, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 300f, null, null);
		
		final int minSelection = 1;
		final int maxSelection = song.countMeasureHeaders();
		
		UILabel fromLabel = uiFactory.createLabel(range);
		fromLabel.setText(TuxGuitar.getProperty("edit.from"));
		rangeLayout.set(fromLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		
		final UISpinner fromSpinner = uiFactory.createSpinner(range);
		fromSpinner.setMaximum(maxSelection);
		fromSpinner.setMinimum(minSelection);
		fromSpinner.setValue(minSelection);
		rangeLayout.set(fromSpinner, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 60f, null, null);
		
		UILabel toLabel = uiFactory.createLabel(range);
		toLabel.setText(TuxGuitar.getProperty("edit.to"));
		rangeLayout.set(toLabel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		
		final UISpinner toSpinner = uiFactory.createSpinner(range);
		toSpinner.setMinimum(minSelection);
		toSpinner.setMaximum(maxSelection);
		toSpinner.setValue(maxSelection);
		rangeLayout.set(toSpinner, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 60f, null, null);
		
		fromSpinner.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				int fromSelection = fromSpinner.getValue();
				int toSelection = toSpinner.getValue();
				
				if(fromSelection < minSelection){
					fromSpinner.setValue(minSelection);
				}else if(fromSelection > toSelection){
					fromSpinner.setValue(toSelection);
				}
			}
		});
		toSpinner.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				int toSelection = toSpinner.getValue();
				int fromSelection = fromSpinner.getValue();
				if(toSelection < fromSelection){
					toSpinner.setValue(fromSelection);
				}else if(toSelection > maxSelection){
					toSpinner.setValue(maxSelection);
				}
			}
		});
		
		//------------------CHECK OPTIONS--------------------
		UITableLayout optionsLayout = new UITableLayout();
		UILegendPanel options = uiFactory.createLegendPanel(dialog);
		options.setLayout(optionsLayout);
		options.setText(TuxGuitar.getProperty("options"));
		dialogLayout.set(options, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 300f, null, null);
		
		final UICheckBox tablatureEnabled = uiFactory.createCheckBox(options);
		tablatureEnabled.setText(TuxGuitar.getProperty("export.tablature-enabled"));
		tablatureEnabled.setSelected(true);
		optionsLayout.set(tablatureEnabled, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UICheckBox scoreEnabled = uiFactory.createCheckBox(options);
		scoreEnabled.setText(TuxGuitar.getProperty("export.score-enabled"));
		scoreEnabled.setSelected(true);
		optionsLayout.set(scoreEnabled, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UICheckBox chordNameEnabled = uiFactory.createCheckBox(options);
		chordNameEnabled.setText(TuxGuitar.getProperty("export.chord-name-enabled"));
		chordNameEnabled.setSelected(true);
		optionsLayout.set(chordNameEnabled, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UICheckBox chordDiagramEnabled = uiFactory.createCheckBox(options);
		chordDiagramEnabled.setText(TuxGuitar.getProperty("export.chord-diagram-enabled"));
		chordDiagramEnabled.setSelected(true);
		optionsLayout.set(chordDiagramEnabled, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UICheckBox blackAndWhite = uiFactory.createCheckBox(options);
		blackAndWhite.setText(TuxGuitar.getProperty("export.black-and-white"));
		blackAndWhite.setSelected(true);
		optionsLayout.set(blackAndWhite, 5, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		tablatureEnabled.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				if(!tablatureEnabled.isSelected()){
					scoreEnabled.setSelected(true);
				}
			}
		});
		scoreEnabled.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				if(!scoreEnabled.isSelected()){
					tablatureEnabled.setSelected(true);
				}
			}
		});
		
		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 4, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				int style = 0;
				style |= (scoreEnabled.isSelected() ? TGLayout.DISPLAY_SCORE : 0);
				style |= (tablatureEnabled.isSelected() ? TGLayout.DISPLAY_TABLATURE : 0);
				style |= (chordNameEnabled.isSelected() ? TGLayout.DISPLAY_CHORD_NAME : 0);
				style |= (chordDiagramEnabled.isSelected() ? TGLayout.DISPLAY_CHORD_DIAGRAM : 0);
				style |= (blackAndWhite.isSelected() ? TGLayout.DISPLAY_MODE_BLACK_WHITE : 0);
				
				Integer selectedTrack = (!trackAllCheck.isSelected() ? trackCombo.getSelectedValue() : null);
				
				TGPrintSettings printStyles = new TGPrintSettings();
				printStyles.setTrackNumber(selectedTrack != null ? selectedTrack : TGPrintSettings.ALL_TRACKS);
				printStyles.setFromMeasure(fromSpinner.getValue());
				printStyles.setToMeasure(toSpinner.getValue());
				printStyles.setStyle(style);
				
				dialog.dispose();
				
				handler.updatePrintSettings(printStyles);
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
