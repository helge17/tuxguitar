package org.herac.tuxguitar.android.view.dialog.track;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.TGActionProcessorListener;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenMenuAction;
import org.herac.tuxguitar.android.menu.context.TGContextMenuController;
import org.herac.tuxguitar.android.view.dialog.TGDialogController;
import org.herac.tuxguitar.android.view.dialog.confirm.TGConfirmDialogController;

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

	public TGActionProcessorListener createOpenMenuAction(TGContextMenuController controller) {
		TGActionProcessorListener tgActionProcessor = this.createAction(TGOpenMenuAction.NAME);
		tgActionProcessor.setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_ACTIVITY, this.dialog.findActivity());
		tgActionProcessor.setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_CONTROLLER, controller);
		return tgActionProcessor;
	}

	public TGActionProcessorListener createTuningModelMenuAction(TGTrackTuningModel model) {
		return this.createOpenMenuAction(new TGTrackTuningListItemMenu(this.dialog, model));
	}

	public TGActionProcessorListener createRemoveTuningModelAction(final TGTrackTuningModel model) {
		TGActionProcessorListener tgActionProcessor = this.createOpenDialogAction(new TGConfirmDialogController());
		tgActionProcessor.setAttribute(TGConfirmDialogController.ATTRIBUTE_MESSAGE, this.dialog.findActivity().getString(R.string.menu_track_tuning_list_item_remove_confirm_question));
		tgActionProcessor.setAttribute(TGConfirmDialogController.ATTRIBUTE_RUNNABLE, new Runnable() {
			public void run() {
				TGTrackTuningActionHandler.this.dialog.postRemoveTuningModel(model);
			}
		});
		return tgActionProcessor;
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
}
