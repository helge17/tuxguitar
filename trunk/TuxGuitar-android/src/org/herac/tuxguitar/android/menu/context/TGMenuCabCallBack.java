package org.herac.tuxguitar.android.menu.context;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.editor.event.TGUpdateEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventException;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.util.TGAbstractContext;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGMenuCabCallBack implements TGEventListener, ActionMode.Callback {

	public static final String ATTRIBUTE_BY_PASS_CLOSE_MENU = (TGMenuCabCallBack.class.getName() + "-byPassCloseMenu");

	private TGContext context;
	private TGMenuController controller;
	private ActionMode actionMode;
	private View selectableView;

	public TGMenuCabCallBack(TGContext context, TGMenuController controller, View selectableView) {
		this.context = context;
		this.controller = controller;
		this.selectableView = selectableView;
	}

	@Override
	public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
		this.actionMode = actionMode;
		this.controller.inflate(menu, actionMode.getMenuInflater());
		if( this.selectableView != null ) {
			this.selectableView.setActivated(true);
		}
		this.addEventListener();
		return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode actionMode) {
		if( this.selectableView != null ) {
			this.selectableView.setActivated(false);
		}
	}

	@Override
	public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
		return false;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
		return false;
	}

	public void finish() {
		if( this.actionMode != null ) {
			this.actionMode.finish();
			this.actionMode = null;
		}
	}

	public void addEventListener() {
		TGEditorManager.getInstance(this.context).addUpdateListener(this);
	}

	public void removeEventListener() {
		TGEditorManager.getInstance(this.context).removeUpdateListener(this);
	}

	public boolean shouldByPassEvent(TGEvent event) {
		if(!TGUpdateEvent.EVENT_TYPE.equals(event.getEventType())) {
			return true;
		}

		TGAbstractContext sourceContext = event.getAttribute(TGEvent.ATTRIBUTE_SOURCE_CONTEXT);
		if( sourceContext != null ) {
			return Boolean.TRUE.equals(sourceContext.getAttribute(ATTRIBUTE_BY_PASS_CLOSE_MENU));
		}
		return false;
	}

	public void processEvent(TGEvent event) throws TGEventException {
		if(!this.shouldByPassEvent(event)) {
			this.removeEventListener();

			TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
				public void run() {
					TGMenuCabCallBack.this.finish();
				}
			});
		}
	}
}
