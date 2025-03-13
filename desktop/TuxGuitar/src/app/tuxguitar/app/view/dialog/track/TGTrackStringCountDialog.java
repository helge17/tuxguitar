package app.tuxguitar.app.view.dialog.track;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.track.TGSetTrackStringCountAction;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UILegendPanel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UISpinner;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

public class TGTrackStringCountDialog {

	public void show(final TGViewContext context) {
		final TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);

		if( track.isPercussion()) {
			final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
			final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
			final UITableLayout dialogLayout = new UITableLayout();
			final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);

			dialog.setLayout(dialogLayout);
			dialog.setText(TuxGuitar.getProperty("track.string.count.dialog.title"));

			UITableLayout groupLayout = new UITableLayout();
			UILegendPanel group = uiFactory.createLegendPanel(dialog);
			group.setLayout(groupLayout);
			group.setText(TuxGuitar.getProperty("track.string.count.dialog.tip"));
			dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			UILabel rcountLabel = uiFactory.createLabel(group);
			rcountLabel.setText(TuxGuitar.getProperty("track.string.count") + ":");
			groupLayout.set(rcountLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);

			final UISpinner countValue = uiFactory.createSpinner(group);
			countValue.setMinimum(TGTrack.MIN_STRINGS);
			countValue.setMaximum(TGTrack.MAX_STRINGS);
			countValue.setValue(track.stringCount());
			groupLayout.set(countValue, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 100f, null, null);

			//----------------------BUTTONS--------------------------------
			UITableLayout buttonsLayout = new UITableLayout(0f);
			UIPanel buttons = uiFactory.createPanel(dialog, false);
			buttons.setLayout(buttonsLayout);
			dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);

			final UIButton buttonOK = uiFactory.createButton(buttons);
			buttonOK.setText(TuxGuitar.getProperty("ok"));
			buttonOK.setDefaultButton();
			buttonOK.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					updateTrackTuning(context.getContext(), track, countValue.getValue());
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
	}

	public void updateTrackTuning(TGContext context, TGTrack track, Integer count) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGSetTrackStringCountAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		tgActionProcessor.setAttribute(TGSetTrackStringCountAction.ATTRIBUTE_STRING_COUNT, count);
		tgActionProcessor.process();
	}
}
