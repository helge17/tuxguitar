package app.tuxguitar.app.view.dialog.keysignature;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.composition.TGChangeKeySignatureAction;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTrack;
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
import app.tuxguitar.util.TGBeatRange;
import app.tuxguitar.util.TGContext;

public class TGKeySignatureDialog {

	public void show(final TGViewContext context) {
		final TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		final TGMeasure measure = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		final TGBeatRange beatRange = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);
		final Boolean isSelectionActive = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SELECTION_IS_ACTIVE);

		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);

		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("composition.keysignature"));

		//-------key Signature-------------------------------------
		UITableLayout keySignatureLayout = new UITableLayout();
		UILegendPanel keySignature = uiFactory.createLegendPanel(dialog);
		keySignature.setLayout(keySignatureLayout);
		keySignature.setText(TuxGuitar.getProperty("composition.keysignature"));
		dialogLayout.set(keySignature, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		UILabel keySignatureLabel = uiFactory.createLabel(keySignature);
		keySignatureLabel.setText(TuxGuitar.getProperty("composition.keysignature") + ":");
		keySignatureLayout.set(keySignatureLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);

		final UIDropDownSelect<Integer> keySignatures = uiFactory.createDropDownSelect(keySignature);

		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.natural"), 0));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.sharp-1"), 1));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.sharp-2"), 2));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.sharp-3"), 3));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.sharp-4"), 4));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.sharp-5"), 5));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.sharp-6"), 6));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.sharp-7"), 7));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.flat-1"), 8));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.flat-2"), 9));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.flat-3"), 10));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.flat-4"), 11));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.flat-5"), 12));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.flat-6"), 13));
		keySignatures.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty("composition.keysignature.flat-7"), 14));
		keySignatures.setSelectedValue(measure.getKeySignature());
		keySignatureLayout.set(keySignatures, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 150f, null, null);

		//--------------------Option Checkboxes-------------------------------
		UITableLayout checkLayout = new UITableLayout();
		UILegendPanel check = uiFactory.createLegendPanel(dialog);
		check.setLayout(checkLayout);
		check.setText(TuxGuitar.getProperty("options"));
		dialogLayout.set(check, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		final UICheckBox applyToSelection = uiFactory.createCheckBox(check);
		applyToSelection.setText(TuxGuitar.getProperty("composition.keysignature.apply-to-selection"));
		checkLayout.set(applyToSelection, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		final UICheckBox toEnd = uiFactory.createCheckBox(check);
		toEnd.setText(TuxGuitar.getProperty("composition.keysignature.to-the-end"));
		checkLayout.set(toEnd, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		if (isSelectionActive && (beatRange != null) && !beatRange.isEmpty()) {
			applyToSelection.setEnabled(true);
			applyToSelection.setSelected(true);
			toEnd.setSelected(false);
		} else {
			applyToSelection.setEnabled(false);
			applyToSelection.setSelected(false);
			toEnd.setSelected(true);
		}

		// check boxes are exclusive (no radio button: none of them may be selected)
		toEnd.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				applyToSelection.setSelected(false);
			}
		});
		applyToSelection.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				toEnd.setSelected(false);
			}
		});

		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);

		final UIButton buttonApply = uiFactory.createButton(buttons);
		buttonApply.setText(TuxGuitar.getProperty("apply"));
		buttonApply.setDefaultButton();
		buttonApply.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				changeKeySignature(context.getContext(), track, measure, keySignatures.getSelectedValue(), beatRange, applyToSelection.isSelected(), toEnd.isSelected());
			}
		});
		buttonsLayout.set(buttonApply, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);

		final UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				changeKeySignature(context.getContext(), track, measure, keySignatures.getSelectedValue(), beatRange, applyToSelection.isSelected(), toEnd.isSelected());
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonOK, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);

		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);

		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	public void changeKeySignature(TGContext context, TGTrack track, TGMeasure measure, Integer value, TGBeatRange beatRange, Boolean applyToSelection, Boolean applyToEnd) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGChangeKeySignatureAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		tgActionProcessor.setAttribute(TGChangeKeySignatureAction.ATTRIBUTE_KEY_SIGNATURE, value);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE, beatRange);
		tgActionProcessor.setAttribute(TGChangeKeySignatureAction.ATTRIBUTE_APPLY_TO_SELECTION, applyToSelection);
		if (applyToSelection && (beatRange != null) && !beatRange.isEmpty()) {
			// first measure of selection (start point for undo controller, whatever the caret position in selection)
			tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, beatRange.getMeasures().get(0));
			// so that undo controller restores all modified measures
			tgActionProcessor.setAttribute(TGChangeKeySignatureAction.ATTRIBUTE_APPLY_TO_END, Boolean.TRUE);
		} else {
			tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
			tgActionProcessor.setAttribute(TGChangeKeySignatureAction.ATTRIBUTE_APPLY_TO_END, applyToEnd);
		}
		tgActionProcessor.processOnNewThread();
	}
}
