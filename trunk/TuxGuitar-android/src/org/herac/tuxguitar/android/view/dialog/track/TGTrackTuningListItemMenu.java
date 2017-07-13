package org.herac.tuxguitar.android.view.dialog.track;

import android.view.ContextMenu;
import android.view.MenuInflater;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.TGActionProcessorListener;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.menu.context.TGContextMenuBase;
import org.herac.tuxguitar.android.view.dialog.channel.TGChannelEditDialogController;
import org.herac.tuxguitar.android.view.dialog.confirm.TGConfirmDialogController;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.channel.TGRemoveChannelAction;

public class TGTrackTuningListItemMenu extends TGContextMenuBase {

	private TGTrackTuningDialog dialog;
	private TGTrackTuningModel model;

	public TGTrackTuningListItemMenu(TGTrackTuningDialog dialog, TGTrackTuningModel model) {
		super(dialog.findActivity());

		this.dialog = dialog;
		this.model = model;
	}

	public void inflate(ContextMenu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_track_tuning_list_item, menu);
		initializeItems(menu);
	}
	
	public void initializeItems(ContextMenu menu) {		
		this.initializeItem(menu, R.id.menu_track_tuning_list_item_edit, this.dialog.getActionHandler().createEditTuningModelAction(this.model), true);
		this.initializeItem(menu, R.id.menu_track_tuning_list_item_remove, this.dialog.getActionHandler().createRemoveTuningModelAction(this.model), true);
	}
}