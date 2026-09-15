package app.tuxguitar.app.view.dialog.transpose;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.tools.TGTransposeAction;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.song.models.TGSong;
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
import app.tuxguitar.util.TGBeatRange;

public class TGTransposeDialog {

	private static final int TRANSPOSITION_SEMITONES = 12;
	private UIRadioButton applyToMeasureRangeButton;
	private UIDropDownSelect<Integer> measuresFrom;
	private UIDropDownSelect<Integer> measuresTo;

	public void show(final TGViewContext context) {
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);

		final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		int measureCount = song.countMeasureHeaders();
		boolean isSelectionActive = Boolean.TRUE.equals(context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SELECTION_IS_ACTIVE));
		final TGBeatRange beats = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);
		int fromMeasureNumber = 0;
		int toMeasureNumber = 0;
		if (isSelectionActive && (beats != null) && !beats.isEmpty()) {
			fromMeasureNumber = beats.firstMeasure().getNumber();
			toMeasureNumber = beats.lastMeasure().getNumber();
		}
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("tools.transpose"));

		//----------------- SEMITONES ------------------------
		UITableLayout groupLayout = new UITableLayout();
		UILegendPanel group = uiFactory.createLegendPanel(dialog);
		group.setLayout(groupLayout);
		group.setText(TuxGuitar.getProperty("tools.transpose"));
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		UILabel transpositionLabel = uiFactory.createLabel(group);
		transpositionLabel.setText(TuxGuitar.getProperty("tools.transpose.semitones") + ":");
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
		applyToAllMeasuresButton.setSelected(!isSelectionActive);
		applyToAllMeasuresButton.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				updateOptions();
				}
		});
		optionsLayout.set(applyToAllMeasuresButton, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		final UIRadioButton applyToCurrentMeasureButton = uiFactory.createRadioButton(options);
		applyToCurrentMeasureButton.setText(TuxGuitar.getProperty("tools.transpose.apply-to-measure"));
		applyToCurrentMeasureButton.setSelected(isSelectionActive && (fromMeasureNumber == toMeasureNumber));
		applyToCurrentMeasureButton.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				updateOptions();
				}
		});
		optionsLayout.set(applyToCurrentMeasureButton, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		this.applyToMeasureRangeButton = uiFactory.createRadioButton(options);
		applyToMeasureRangeButton.setText(TuxGuitar.getProperty("tools.transpose.apply-to-measure-range"));
		applyToMeasureRangeButton.setSelected(isSelectionActive && (fromMeasureNumber != toMeasureNumber));
		applyToMeasureRangeButton.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				updateOptions();
				}
		});
		optionsLayout.set(applyToMeasureRangeButton, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		final UICheckBox applyToAllTracksButton = uiFactory.createCheckBox(options);
		applyToAllTracksButton.setText(TuxGuitar.getProperty("tools.transpose.apply-to-all-tracks"));
		// if a selection is active, "apply to all tracks" may be confusing
		applyToAllTracksButton.setSelected(!isSelectionActive);
		optionsLayout.set(applyToAllTracksButton, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		final UICheckBox applyToChordsButton = uiFactory.createCheckBox(options);
		applyToChordsButton.setText(TuxGuitar.getProperty("tools.transpose.apply-to-chords"));
		applyToChordsButton.setSelected(true);
		optionsLayout.set(applyToChordsButton, 5, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UICheckBox tryKeepStringButton = uiFactory.createCheckBox(options);
		tryKeepStringButton.setText(TuxGuitar.getProperty("tools.transpose.try-keep-strings"));
		tryKeepStringButton.setSelected(true);
		optionsLayout.set(tryKeepStringButton, 6, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		//-----------------MEASURES SELECTION------------------------
		UITableLayout measuresLayout = new UITableLayout();
		UILegendPanel measuresPanel = uiFactory.createLegendPanel(dialog);
		measuresPanel.setLayout(measuresLayout);
		measuresPanel.setText(TuxGuitar.getProperty("tools.transpose.measures"));
		dialogLayout.set(measuresPanel, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UILabel measuresLabelFrom = uiFactory.createLabel(measuresPanel);
		measuresLabelFrom.setText(TuxGuitar.getProperty("tools.transpose.measures-from"));
		measuresLayout.set(measuresLabelFrom, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		
		this.measuresFrom = uiFactory.createDropDownSelect(measuresPanel);
		for( int i = 1 ; i <= measureCount; i ++ ){
			this.measuresFrom.addItem(new UISelectItem<Integer>(Integer.toString(i), i));
			if ((i == fromMeasureNumber) || (this.measuresFrom.getSelectedValue() == null) ) {
				this.measuresFrom.setSelectedValue(i);
			}
		}
		this.measuresFrom.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				if ((measuresFrom.getSelectedValue() != null) && (measuresTo.getSelectedValue() != null)
						&& (measuresFrom.getSelectedValue() > measuresTo.getSelectedValue())) {
					measuresTo.setSelectedValue(measuresFrom.getSelectedValue());
				}
			}
		});
		measuresLayout.set(this.measuresFrom, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		UILabel measuresLabelTo = uiFactory.createLabel(measuresPanel);
		measuresLabelTo.setText(TuxGuitar.getProperty("tools.transpose.measures-to"));
		measuresLayout.set(measuresLabelTo, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		
		this.measuresTo = uiFactory.createDropDownSelect(measuresPanel);
		for( int i = 1; i <= measureCount; i ++ ){
			this.measuresTo.addItem(new UISelectItem<Integer>(Integer.toString(i), i));
			if (i == toMeasureNumber) {
				this.measuresTo.setSelectedValue(i);
			}
			if (this.measuresTo.getSelectedValue() == null) {
				this.measuresTo.setSelectedValue(measureCount);
			}
		}
		this.measuresTo.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				if ((measuresFrom.getSelectedValue() != null) && (measuresTo.getSelectedValue() != null)
						&& (measuresFrom.getSelectedValue() > measuresTo.getSelectedValue())) {
					measuresFrom.setSelectedValue(measuresTo.getSelectedValue());
				}
			}
		});
		
		measuresLayout.set(this.measuresTo, 1, 4, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);


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
				Integer transposition = transpositionCombo.getSelectedValue();
				if( transposition != null ){
					Integer from = measuresFrom.getSelectedValue();
					Integer to = measuresTo.getSelectedValue();
					final boolean tryKeepString = tryKeepStringButton.isSelected();
					final boolean applyToChords = applyToChordsButton.isSelected();
					final boolean applyToAllTracks = applyToAllTracksButton.isSelected();
					final boolean applyToAllMeasures = applyToAllMeasuresButton.isSelected();
					final boolean applyToMeasureRange = applyToMeasureRangeButton.isSelected();
					transposeNotes(context, transposition, tryKeepString, applyToChords , applyToAllMeasures, applyToAllTracks, applyToMeasureRange, from, to);
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

		this.updateOptions();

		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public void transposeNotes(TGViewContext context, int transposition , boolean tryKeepString , boolean applyToChords , 
								boolean applyToAllMeasures , boolean applyToAllTracks, boolean applyToMeasureRange, Integer from, Integer to) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context.getContext(), TGTransposeAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));
		tgActionProcessor.setAttribute(TGTransposeAction.ATTRIBUTE_TRANSPOSITION, transposition);
		tgActionProcessor.setAttribute(TGTransposeAction.ATTRIBUTE_TRY_KEEP_STRING, tryKeepString);
		tgActionProcessor.setAttribute(TGTransposeAction.ATTRIBUTE_APPLY_TO_CHORDS, applyToChords);
		tgActionProcessor.setAttribute(TGTransposeAction.ATTRIBUTE_APPLY_TO_ALL_TRACKS, applyToAllTracks);
		tgActionProcessor.setAttribute(TGTransposeAction.ATTRIBUTE_APPLY_TO_ALL_MEASURES, applyToAllMeasures);
		// measure range
		if (applyToMeasureRange && (from != null) && (to != null)) {
			tgActionProcessor.setAttribute(TGTransposeAction.ATTRIBUTE_APPLY_TO_MEASURE_RANGE, true);
			tgActionProcessor.setAttribute(TGTransposeAction.ATTRIBUTE_MEASURE_FROM, from);
			tgActionProcessor.setAttribute(TGTransposeAction.ATTRIBUTE_MEASURE_TO, to);
		}
		tgActionProcessor.processOnNewThread();
	}

	private void updateOptions() {
		this.measuresFrom.setEnabled(this.applyToMeasureRangeButton.isSelected());
		this.measuresTo.setEnabled(this.applyToMeasureRangeButton.isSelected());
	}
}
