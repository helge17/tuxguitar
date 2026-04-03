package app.tuxguitar.app.view.dialog.timesignature;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.composition.TGChangeTimeSignatureAction;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTimeSignature;
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
import app.tuxguitar.util.TGContext;

public class TGTimeSignatureDialog {

	public void show(final TGViewContext context) {
		final TGSongManager songManager = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGMeasureHeader header = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);

		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);

		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("composition.timesignature"));

		//-------------TIME SIGNATURE-----------------------------------------------
		UITableLayout timeSignatureLayout = new UITableLayout();
		UILegendPanel timeSignature = uiFactory.createLegendPanel(dialog);
		timeSignature.setLayout(timeSignatureLayout);
		timeSignature.setText(TuxGuitar.getProperty("composition.timesignature"));
		dialogLayout.set(timeSignature, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		TGTimeSignature currentTimeSignature = header.getTimeSignature();

		//numerator
		UILabel numeratorLabel = uiFactory.createLabel(timeSignature);
		numeratorLabel.setText(TuxGuitar.getProperty("composition.timesignature.Numerator") + ":");
		timeSignatureLayout.set(numeratorLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);

		final UIDropDownSelect<Integer> numerator = uiFactory.createDropDownSelect(timeSignature);
		for (int i = 1; i <= 32; i++) {
			numerator.addItem(new UISelectItem<Integer>(Integer.toString(i), i));
		}
		numerator.setSelectedValue(currentTimeSignature.getNumerator());
		timeSignatureLayout.set(numerator, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 150f, null, null);

		//denominator
		UILabel denominatorLabel = uiFactory.createLabel(timeSignature);
		denominatorLabel.setText(TuxGuitar.getProperty("composition.timesignature.denominator") + ":");
		timeSignatureLayout.set(denominatorLabel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);

		final UIDropDownSelect<Integer> denominator = uiFactory.createDropDownSelect(timeSignature);
		for (int i = 1; i <= 32; i = i * 2) {
			denominator.addItem(new UISelectItem<Integer>(Integer.toString(i), i));
		}
		denominator.setSelectedValue(currentTimeSignature.getDenominator().getValue());
		timeSignatureLayout.set(denominator, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 150f, null, null);

		//--------------------To End Checkbox-------------------------------
		UITableLayout checkLayout = new UITableLayout();
		UILegendPanel check = uiFactory.createLegendPanel(dialog);
		check.setLayout(checkLayout);
		check.setText(TuxGuitar.getProperty("options"));
		dialogLayout.set(check, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		final UICheckBox toEnd = uiFactory.createCheckBox(check);
		toEnd.setText(TuxGuitar.getProperty("composition.timesignature.to-the-end"));
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
				changeTimeSignature(context.getContext(), song, header, parseTimeSignature(songManager, numerator, denominator), toEnd.isSelected());
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

	public TGTimeSignature parseTimeSignature(TGSongManager songManager, UIDropDownSelect<Integer> numerator, UIDropDownSelect<Integer> denominator) {
		TGTimeSignature tgTimeSignature = songManager.getFactory().newTimeSignature();
		tgTimeSignature.setNumerator(numerator.getSelectedValue());
		tgTimeSignature.getDenominator().setValue(denominator.getSelectedValue());
		return tgTimeSignature;
	}

	public void changeTimeSignature(TGContext context, TGSong song, TGMeasureHeader header, TGTimeSignature timeSignature, Boolean applyToEnd) {
		TablatureEditor.getInstance(context).getTablature().getSelector().clearSelection();
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGChangeTimeSignatureAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TIME_SIGNATURE, timeSignature);
		tgActionProcessor.setAttribute(TGChangeTimeSignatureAction.ATTRIBUTE_APPLY_TO_END, applyToEnd);
		tgActionProcessor.processOnNewThread();
	}
}
