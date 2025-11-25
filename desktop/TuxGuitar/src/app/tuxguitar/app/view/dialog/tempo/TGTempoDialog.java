package app.tuxguitar.app.view.dialog.tempo;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.composition.TGChangeTempoRangeAction;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTempo;
import app.tuxguitar.song.models.TGTempoBase;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UILegendPanel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIRadioButton;
import app.tuxguitar.ui.widget.UISpinner;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

public class TGTempoDialog {

	// possible tempo bases:
	private final TGTempoBase tempoBase[] = TGTempoBase.getTempoBases();

	private int selectedBase;
	private boolean selectedDotted;

	public void show(final TGViewContext context) {
		final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGMeasureHeader header = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		final Boolean isSelectionActive = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SELECTION_IS_ACTIVE);
		
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);

		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("composition.tempo"));

		//-----------------TEMPO------------------------
		UITableLayout groupLayout = new UITableLayout();
		UILegendPanel group = uiFactory.createLegendPanel(dialog);
		group.setLayout(groupLayout);
		group.setText(TuxGuitar.getProperty("composition.tempo"));
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		TGTempo currentTempo = header.getTempo();

		UITableLayout radioButtonsLayout = new UITableLayout();
		UIPanel radioButtonsPanel = uiFactory.createPanel(group, false);
		radioButtonsPanel.setLayout(radioButtonsLayout);
		for (int i=0; i<tempoBase.length; i++) {
			UIRadioButton button = uiFactory.createRadioButton(radioButtonsPanel);
			button.setImage(TuxGuitar.getInstance().getIconManager().getDuration(tempoBase[i].getBase(), tempoBase[i].isDotted()));
			if ( (currentTempo.getBase() == tempoBase[i].getBase()) && (currentTempo.isDotted() == tempoBase[i].isDotted()) ) {
				button.setSelected(true);
				this.selectedBase = tempoBase[i].getBase();
				this.selectedDotted = tempoBase[i].isDotted();
			} else {
				button.setSelected(false);
			}
			button.addSelectionListener(this.createSelectionListener(tempoBase[i].getBase(), tempoBase[i].isDotted()));

			radioButtonsLayout.set(button, 1, i+1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_FILL, false, true, 1, 1, 60f, null, null);
		}
		groupLayout.set(radioButtonsPanel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		UITableLayout tempoValueLayout = new UITableLayout();
		UIPanel tempoValuePanel = uiFactory.createPanel(group, false);
		tempoValuePanel.setLayout(tempoValueLayout);
		UILabel tempoLabel = uiFactory.createLabel(tempoValuePanel);
		tempoLabel.setText(TuxGuitar.getProperty("composition.tempo") + ":");
		tempoValueLayout.set(tempoLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);

		final UISpinner tempo = uiFactory.createSpinner(tempoValuePanel);
		tempo.setMinimum(TGChangeTempoRangeAction.MIN_TEMPO);
		tempo.setMaximum(TGChangeTempoRangeAction.MAX_TEMPO);
		tempo.setValue(currentTempo.getRawValue());
		tempoValueLayout.set(tempo, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true, 1, 1, 150f, null, null);

		groupLayout.set(tempoValuePanel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		//------------------OPTIONS--------------------------
		UITableLayout optionsLayout = new UITableLayout();
		UILegendPanel options = uiFactory.createLegendPanel(dialog);
		options.setLayout(optionsLayout);
		options.setText(TuxGuitar.getProperty("options"));
		dialogLayout.set(options, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		final UIRadioButton applyToAllMeasures = uiFactory.createRadioButton(options);
		applyToAllMeasures.setText(TuxGuitar.getProperty("composition.tempo.start-to-end"));
		optionsLayout.set(applyToAllMeasures, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		final UIRadioButton applyToEnd = uiFactory.createRadioButton(options);
		applyToEnd.setText(TuxGuitar.getProperty("composition.tempo.position-to-end"));
		optionsLayout.set(applyToEnd, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		final UIRadioButton applyToNextOrSelection = uiFactory.createRadioButton(options);
		if (isSelectionActive) {
			applyToNextOrSelection.setText(TuxGuitar.getProperty("composition.tempo.position-to-selection"));
		} else {
			applyToNextOrSelection.setText(TuxGuitar.getProperty("composition.tempo.position-to-next"));
		}
		optionsLayout.set(applyToNextOrSelection, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		applyToNextOrSelection.setSelected(true);

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
				Integer value = tempo.getValue();
				Integer applyTo = parseApplyTo(applyToAllMeasures, applyToEnd, applyToNextOrSelection, isSelectionActive);

				changeTempo(context.getContext(), song, header, TGTempoDialog.this.selectedBase , TGTempoDialog.this.selectedDotted, value, applyTo);
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

	private Integer parseApplyTo(UIRadioButton applyToAll, UIRadioButton applyToEnd, UIRadioButton applyToNextOrSelection, boolean isSelectionActive) {
		if( applyToAll.isSelected() ) {
			return TGChangeTempoRangeAction.APPLY_TO_ALL;
		}
		if( applyToEnd.isSelected() ) {
			return TGChangeTempoRangeAction.APPLY_TO_END;
		}
		if( applyToNextOrSelection.isSelected() ) {
			return isSelectionActive ? TGChangeTempoRangeAction.APPLY_TO_SELECTION : TGChangeTempoRangeAction.APPLY_TO_NEXT;
		}
		return 0;
	}

	private UISelectionListener createSelectionListener(int base, boolean dotted) {
		return new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				TGTempoDialog.this.selectedBase = base;
				TGTempoDialog.this.selectedDotted = dotted;
			}
		};
	}


	public void changeTempo(TGContext context, TGSong song, TGMeasureHeader header, Integer base, Boolean dotted, Integer value, Integer applyTo) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGChangeTempoRangeAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header);
		tgActionProcessor.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_TEMPO, value);
		tgActionProcessor.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_TEMPO_BASE, base);
		tgActionProcessor.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_TEMPO_BASE_DOTTED, dotted);
		tgActionProcessor.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_APPLY_TO, applyTo);
		tgActionProcessor.processOnNewThread();
	}

}
