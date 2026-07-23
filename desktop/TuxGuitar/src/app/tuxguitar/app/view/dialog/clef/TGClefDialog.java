package app.tuxguitar.app.view.dialog.clef;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.composition.TGChangeClefAction;
import app.tuxguitar.song.models.TGClef;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UICheckBox;
import app.tuxguitar.ui.widget.UIDropDownSelect;
import app.tuxguitar.ui.widget.UIImageView;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UILegendPanel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UISelectItem;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

public class TGClefDialog {

	private UIDropDownSelect<TGClef> clefs;
	private UIImageView clefImage;
	private TGIconManager iconManager;

	public void show(final TGViewContext context) {
		this.iconManager = TGIconManager.getInstance(context.getContext());
		
		final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		final TGMeasure measure = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);

		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);

		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("composition.clef"));

		//-------clef-------------------------------------
		UITableLayout clefLayout = new UITableLayout();
		UILegendPanel clef = uiFactory.createLegendPanel(dialog);
		clef.setLayout(clefLayout);
		clef.setText(TuxGuitar.getProperty("composition.clef"));
		dialogLayout.set(clef, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		UILabel numeratorLabel = uiFactory.createLabel(clef);
		numeratorLabel.setText(TuxGuitar.getProperty("composition.clef") + ":");
		clefLayout.set(numeratorLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);

		this.clefs = uiFactory.createDropDownSelect(clef);
		for (TGClef newClef : TGClef.CLEFS) {
			clefs.addItem(new UISelectItem<TGClef>(TuxGuitar.getProperty(newClef.getName()), newClef));
			if (newClef == measure.getClef()) {
				clefs.setSelectedValue(newClef);
			}
		}
		clefs.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				TGClefDialog.this.loadImage();
			}
		});
		clefLayout.set(clefs, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true, 1, 1, 150f, null, null);

		// image
		this.clefImage = uiFactory.createImageView(clef);
		clefLayout.set(this.clefImage, 1, 3, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, true, true);
		this.loadImage();

		//--------------------To End Checkbox-------------------------------
		UITableLayout checkLayout = new UITableLayout();
		UILegendPanel check = uiFactory.createLegendPanel(dialog);
		check.setLayout(checkLayout);
		check.setText(TuxGuitar.getProperty("options"));
		dialogLayout.set(check, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		final UICheckBox toEnd = uiFactory.createCheckBox(check);
		toEnd.setText(TuxGuitar.getProperty("composition.clef.to-the-end"));
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
				changeClef(context.getContext(), song, track, measure, clefs.getSelectedValue() , toEnd.isSelected());
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

	private void loadImage() {
		this.clefImage.setImage(this.iconManager.getImageByName(clefs.getSelectedValue().getIconFileName()));
	}

	private void changeClef(TGContext context, TGSong song, TGTrack track, TGMeasure measure, TGClef clef, Boolean applyToEnd) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGChangeClefAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGChangeClefAction.ATTRIBUTE_CLEF, clef);
		tgActionProcessor.setAttribute(TGChangeClefAction.ATTRIBUTE_APPLY_TO_END, applyToEnd);
		tgActionProcessor.processOnNewThread();
	}
}
