package org.herac.tuxguitar.android.menu.context;

import org.herac.tuxguitar.android.action.TGActionProcessorListener;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenMenuAction;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.application.TGApplicationUtil;
import org.herac.tuxguitar.android.view.dialog.TGDialogController;
import org.herac.tuxguitar.android.view.dialog.confirm.TGConfirmDialogController;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.util.TGContext;

import android.view.ContextMenu;
import android.view.MenuItem;

public abstract class TGContextMenuBase implements TGContextMenuController {
	
	private TGActivity activity;
	
	public TGContextMenuBase(TGActivity activity) {
		this.activity = activity;
	}

	public TGActivity getActivity() {
		return activity;
	}

	public TGContext findContext() {
		return TGApplicationUtil.findContext(this.getActivity());
	}
	
	public void initializeItem(ContextMenu menu, int id, TGActionProcessorListener actionProcessor, boolean enabled, boolean checked) {
		MenuItem menuItem = menu.findItem(id);
		menuItem.setOnMenuItemClickListener(actionProcessor);
		menuItem.setEnabled(enabled);
		menuItem.setChecked(checked);
	}
	
	public void initializeItem(ContextMenu menu, int id, TGActionProcessorListener actionProcessor, boolean enabled) {
		this.initializeItem(menu, id, actionProcessor, enabled, false);	
	}
	
	public void initializeItem(ContextMenu menu, int id, TGDialogController dialogController, boolean enabled) {
		this.initializeItem(menu, id, this.createDialogActionProcessor(dialogController), enabled);
	}
	
	public void initializeItem(ContextMenu menu, int id, TGContextMenuController contextMenuController, boolean enabled) {
		this.initializeItem(menu, id, this.createContextMenuActionProcessor(contextMenuController), enabled);
	}
	
	public TGActionProcessorListener createActionProcessor(String actionId) {
		return new TGActionProcessorListener(findContext(), actionId);
	}
	
	public TGActionProcessorListener createDialogActionProcessor(TGDialogController controller) {
		TGActionProcessorListener tgActionProcessor = this.createActionProcessor(TGOpenDialogAction.NAME);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, controller);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, getActivity());
		return tgActionProcessor;
	}
	
	public TGActionProcessorListener createContextMenuActionProcessor(TGContextMenuController controller) {
		TGActionProcessorListener tgActionProcessor = this.createActionProcessor(TGOpenMenuAction.NAME);
		tgActionProcessor.setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_CONTROLLER, controller);
		tgActionProcessor.setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_ACTIVITY, getActivity());
		return tgActionProcessor;
	}
	
	public TGActionProcessorListener createConfirmableActionProcessor(final TGActionProcessor actionProcessor, final String confirmMessage) {
		TGActionProcessorListener tgActionProcessor = this.createDialogActionProcessor(new TGConfirmDialogController());
		tgActionProcessor.setAttribute(TGConfirmDialogController.ATTRIBUTE_MESSAGE, confirmMessage);
		tgActionProcessor.setAttribute(TGConfirmDialogController.ATTRIBUTE_RUNNABLE, new Runnable() {
			public void run() {
				actionProcessor.process();
			}
		});
		return tgActionProcessor;
	}
}
