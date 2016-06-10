package org.herac.tuxguitar.app.view.dialog.transpose;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.tools.TGTransposeAction;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
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
import org.herac.tuxguitar.ui.widget.UIRadioButton;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UIWindow;

public class TGTransposeDialog {
	
	private static final int TRANSPOSITION_SEMITONES = 12;
	
	public void show(final TGViewContext context) {		
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT2);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("tools.transpose"));
		
		//-----------------TEMPO------------------------
		UITableLayout groupLayout = new UITableLayout();
		UILegendPanel group = uiFactory.createLegendPanel(dialog);
		group.setLayout(groupLayout);
		group.setText(TuxGuitar.getProperty("tools.transpose"));
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UILabel transpositionLabel = uiFactory.createLabel(group);
		transpositionLabel.setText(TuxGuitar.getProperty("tools.transpose.semitones"));
		groupLayout.set(transpositionLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		
		final UIDropDownSelect<Integer> transpositionCombo = uiFactory.createDropDownSelect(group);
		for( int i = -TRANSPOSITION_SEMITONES ; i <= TRANSPOSITION_SEMITONES; i ++ ){
			transpositionCombo.addItem(new UISelectItem<Integer>(Integer.toString(i), i));
		}
		
		transpositionCombo.setSelectedValue(0);
		groupLayout.set(transpositionCombo, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		//------------------OPTIONS--------------------------
		UITableLayout optionsLayout = new UITableLayout();
		UILegendPanel options = uiFactory.createLegendPanel(dialog);
		options.setLayout(optionsLayout);
		options.setText(TuxGuitar.getProperty("options"));
		dialogLayout.set(options, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UIRadioButton applyToAllMeasuresButton = uiFactory.createRadioButton(options);
		applyToAllMeasuresButton.setText(TuxGuitar.getProperty("tools.transpose.apply-to-track"));
		applyToAllMeasuresButton.setSelected(true);
		optionsLayout.set(applyToAllMeasuresButton, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UIRadioButton applyToCurrentMeasureButton = uiFactory.createRadioButton(options);
		applyToCurrentMeasureButton.setText(TuxGuitar.getProperty("tools.transpose.apply-to-measure"));
		optionsLayout.set(applyToCurrentMeasureButton, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UICheckBox applyToAllTracksButton = uiFactory.createCheckBox(options);
		applyToAllTracksButton.setText(TuxGuitar.getProperty("tools.transpose.apply-to-all-tracks"));
		applyToAllTracksButton.setSelected(true);
		optionsLayout.set(applyToAllTracksButton, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UICheckBox applyToChordsButton = uiFactory.createCheckBox(options);
		applyToChordsButton.setText(TuxGuitar.getProperty("tools.transpose.apply-to-chords"));
		applyToChordsButton.setSelected(true);
		optionsLayout.set(applyToChordsButton, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UICheckBox tryKeepStringButton = uiFactory.createCheckBox(options);
		tryKeepStringButton.setText(TuxGuitar.getProperty("tools.transpose.try-keep-strings"));
		tryKeepStringButton.setSelected(true);
		optionsLayout.set(tryKeepStringButton, 5, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
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
				Integer transposition = transpositionCombo.getSelectedValue();
				if( transposition != null ){
					final boolean tryKeepString = tryKeepStringButton.isSelected();
					final boolean applyToChords = applyToChordsButton.isSelected();
					final boolean applyToAllTracks = applyToAllTracksButton.isSelected();
					final boolean applyToAllMeasures = applyToAllMeasuresButton.isSelected();
					
					transposeNotes(context, transposition, tryKeepString, applyToChords , applyToAllMeasures, applyToAllTracks);
				}
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
		
		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public void transposeNotes(TGViewContext context, int transposition , boolean tryKeepString , boolean applyToChords , boolean applyToAllMeasures , boolean applyToAllTracks) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context.getContext(), TGTransposeAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));
		tgActionProcessor.setAttribute(TGTransposeAction.ATTRIBUTE_TRANSPOSITION, transposition);
		tgActionProcessor.setAttribute(TGTransposeAction.ATTRIBUTE_TRY_KEEP_STRING, tryKeepString);
		tgActionProcessor.setAttribute(TGTransposeAction.ATTRIBUTE_APPLY_TO_CHORDS, applyToChords);
		tgActionProcessor.setAttribute(TGTransposeAction.ATTRIBUTE_APPLY_TO_ALL_TRACKS, applyToAllTracks);
		tgActionProcessor.setAttribute(TGTransposeAction.ATTRIBUTE_APPLY_TO_ALL_MEASURES, applyToAllMeasures);
		tgActionProcessor.processOnNewThread();
	}
}
