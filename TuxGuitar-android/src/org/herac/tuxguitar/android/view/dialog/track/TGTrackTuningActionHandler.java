package org.herac.tuxguitar.android.view.dialog.track;

import android.view.MenuItem;
import android.view.View;

import org.herac.tuxguitar.android.action.TGActionProcessorListener;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenCabMenuAction;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import org.herac.tuxguitar.android.menu.controller.TGMenuController;
import org.herac.tuxguitar.android.view.dialog.TGDialogController;
import org.herac.tuxguitar.editor.TGEditorManager;

public class TGTrackTuningActionHandler {

	private TGTrackTuningDialog dialog;

	public TGTrackTuningActionHandler(TGTrackTuningDialog channelList) {
		this.dialog = channelList;
	}

	public TGActionProcessorListener createAction(String actionId) {
		return new TGActionProcessorListener(this.dialog.findContext(), actionId);
	}

	public TGActionProcessorListener createOpenDialogAction(TGDialogController controller) {
		TGActionProcessorListener tgActionProcessor = this.createAction(TGOpenDialogAction.NAME);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, this.dialog.findActivity());
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, controller);
		return tgActionProcessor;
	}

	public TGActionProcessorListener createOpenCabMenuAction(TGMenuController controller, View selectableView) {
		TGActionProcessorListener tgActionProcessor = this.createAction(TGOpenCabMenuAction.NAME);
		tgActionProcessor.setAttribute(TGOpenCabMenuAction.ATTRIBUTE_MENU_ACTIVITY, this.dialog.findActivity());
		tgActionProcessor.setAttribute(TGOpenCabMenuAction.ATTRIBUTE_MENU_CONTROLLER, controller);
		tgActionProcessor.setAttribute(TGOpenCabMenuAction.ATTRIBUTE_MENU_SELECTABLE_VIEW, selectableView);
		return tgActionProcessor;
	}

	public TGActionProcessorListener createTuningModelMenuAction(TGTrackTuningModel model, View selectableView) {
		return this.createOpenCabMenuAction(new TGTrackTuningListItemMenu(this.dialog, model), selectableView);
	}

	public TGActionProcessorListener createEditTuningModelAction(final TGTrackTuningModel model) {
		TGActionProcessorListener tgActionProcessor = this.createOpenDialogAction(new TGTrackTuningModelDialogController());
		tgActionProcessor.setAttribute(TGTrackTuningModelDialogController.ATTRIBUTE_MODEL, model);
		tgActionProcessor.setAttribute(TGTrackTuningModelDialogController.ATTRIBUTE_HANDLER, new TGTrackTuningModelHandler() {
			public void handleSelection(TGTrackTuningModel modifiedModel) {
				TGTrackTuningActionHandler.this.dialog.postModifyTuningModel(model, modifiedModel.getValue());
			}
		});
		return tgActionProcessor;
	}

	public TGActionProcessorListener createAddTuningModelAction() {
		TGActionProcessorListener tgActionProcessor = this.createOpenDialogAction(new TGTrackTuningModelDialogController());
		tgActionProcessor.setAttribute(TGTrackTuningModelDialogController.ATTRIBUTE_HANDLER, new TGTrackTuningModelHandler() {
			public void handleSelection(final TGTrackTuningModel model) {
				TGTrackTuningActionHandler.this.dialog.postAddTuningModel(model);
			}
		});
		return tgActionProcessor;
	}

	public MenuItem.OnMenuItemClickListener createRemoveTuningModelAction(final TGTrackTuningModel model) {
		return new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem menuItem) {
				TGTrackTuningActionHandler.this.dialog.postRemoveTuningModel(model);
				TGEditorManager.getInstance(TGTrackTuningActionHandler.this.dialog.findContext()).updateSelection();
				return true;
			}
		};
	}
}
