package org.herac.tuxguitar.app.view.dialog.track;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.track.TGSetTrackStringCountAction;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISpinner;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class TGTrackStringCountDialog {
	
	public void show(final TGViewContext context) {
		final TGSongManager songManager = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		final TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		
		if( songManager.isPercussionChannel(track.getSong(), track.getChannelId())) {
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
			rcountLabel.setText(TuxGuitar.getProperty("track.string.count"));
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