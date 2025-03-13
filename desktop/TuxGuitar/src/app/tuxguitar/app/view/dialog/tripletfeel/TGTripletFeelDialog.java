package app.tuxguitar.app.view.dialog.tripletfeel;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.composition.TGChangeTripletFeelAction;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UICheckBox;
import app.tuxguitar.ui.widget.UILegendPanel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIRadioButton;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

public class TGTripletFeelDialog {

	public void show(final TGViewContext context) {
		final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGMeasureHeader header = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);

		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);

		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("composition.tripletfeel"));

		//-------------TRIPLET FEEL-----------------------------------------------
		UITableLayout tripletFeelLayout = new UITableLayout();
		UILegendPanel tripletFeel = uiFactory.createLegendPanel(dialog);
		tripletFeel.setLayout(tripletFeelLayout);
		tripletFeel.setText(TuxGuitar.getProperty("composition.tripletfeel"));
		dialogLayout.set(tripletFeel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 250f, null, null);

		//none
		final UIRadioButton tripletFeelNone = uiFactory.createRadioButton(tripletFeel);
		tripletFeelNone.setText(TuxGuitar.getProperty("composition.tripletfeel.none"));
		tripletFeelNone.setSelected(header.getTripletFeel() == TGMeasureHeader.TRIPLET_FEEL_NONE);
		tripletFeelLayout.set(tripletFeelNone, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		final UIRadioButton tripletFeelEighth = uiFactory.createRadioButton(tripletFeel);
		tripletFeelEighth.setText(TuxGuitar.getProperty("composition.tripletfeel.eighth"));
		tripletFeelEighth.setSelected(header.getTripletFeel() == TGMeasureHeader.TRIPLET_FEEL_EIGHTH);
		tripletFeelLayout.set(tripletFeelEighth, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		final UIRadioButton tripletFeelSixteenth = uiFactory.createRadioButton(tripletFeel);
		tripletFeelSixteenth.setText(TuxGuitar.getProperty("composition.tripletfeel.sixteenth"));
		tripletFeelSixteenth.setSelected(header.getTripletFeel() == TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH);
		tripletFeelLayout.set(tripletFeelSixteenth, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		//--------------------To End Checkbox-------------------------------
		UITableLayout checkLayout = new UITableLayout();
		UILegendPanel check = uiFactory.createLegendPanel(dialog);
		check.setLayout(checkLayout);
		check.setText(TuxGuitar.getProperty("options"));
		dialogLayout.set(check, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		final UICheckBox toEnd = uiFactory.createCheckBox(check);
		toEnd.setText(TuxGuitar.getProperty("composition.tripletfeel.to-the-end"));
		toEnd.setSelected(true);
		checkLayout.set(toEnd, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

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
				changeTripletFeel(context.getContext(), song, header, parseTripletFeel(tripletFeelNone, tripletFeelEighth, tripletFeelSixteenth), toEnd.isSelected());
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

	protected int parseTripletFeel(UIRadioButton tripletFeelNone, UIRadioButton tripletFeelEighth, UIRadioButton tripletFeelSixteenth){
		if(tripletFeelNone.isSelected()){
			return TGMeasureHeader.TRIPLET_FEEL_NONE;
		}else if(tripletFeelEighth.isSelected()){
			return TGMeasureHeader.TRIPLET_FEEL_EIGHTH;
		}else if(tripletFeelSixteenth.isSelected()){
			return TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH;
		}
		return TGMeasureHeader.TRIPLET_FEEL_NONE;
	}

	public void changeTripletFeel(TGContext context, TGSong song, TGMeasureHeader header, Integer tripletFeel, Boolean applyToEnd) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGChangeTripletFeelAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header);
		tgActionProcessor.setAttribute(TGChangeTripletFeelAction.ATTRIBUTE_TRIPLET_FEEL, tripletFeel);
		tgActionProcessor.setAttribute(TGChangeTripletFeelAction.ATTRIBUTE_APPLY_TO_END, applyToEnd);
		tgActionProcessor.processOnNewThread();
	}
}
