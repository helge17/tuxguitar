package org.herac.tuxguitar.android.view.tool;

import org.herac.tuxguitar.android.action.TGActionProcessor;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenFragmentAction;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenMenuAction;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.application.TGApplication;
import org.herac.tuxguitar.android.editor.TGEditorManager;
import org.herac.tuxguitar.android.editor.TGUpdateEvent;
import org.herac.tuxguitar.android.fragment.TGFragment;
import org.herac.tuxguitar.android.menu.context.TGContextMenuController;
import org.herac.tuxguitar.android.view.dialog.TGDialogController;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

public abstract class TGToolView extends RelativeLayout implements TGEventListener {
	
	public TGToolView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public abstract void addListeners();
	
	public abstract void updateItems();
	
	public void onFinishInflate() {
		this.addListeners();
		this.updateItems();
	}
	
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		
		TGEditorManager.getInstance(findContext()).addUpdateListener(this);
	}
	
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		
		TGEditorManager.getInstance(findContext()).removeUpdateListener(this);
	}
	
	public TGContext findContext() {
		return ((TGApplication)getContext().getApplicationContext()).getContext();
	}
	
	public TGActivity findActivity() {
		return (TGActivity) getContext();
	}
	
	public TGActionProcessor createActionProcessor(String actionId) {
		return new TGActionProcessor(findContext(), actionId);
	}
	
	public TGToolViewItemListener createActionListener(String actionId) {
		return new TGToolViewItemListener(createActionProcessor(actionId));
	}
	
	public TGToolViewItemListener createDialogActionListener(TGDialogController controller) {
		TGActionProcessor tgActionProcessor = this.createActionProcessor(TGOpenDialogAction.NAME);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, controller);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, findActivity());
		return new TGToolViewItemListener(tgActionProcessor);
	}
	
	public TGToolViewItemListener createContextMenuActionListener(TGContextMenuController controller) {
		TGActionProcessor tgActionProcessor = this.createActionProcessor(TGOpenMenuAction.NAME);
		tgActionProcessor.setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_CONTROLLER, controller);
		tgActionProcessor.setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_ACTIVITY, findActivity());
		return new TGToolViewItemListener(tgActionProcessor);
	}
	
	public TGToolViewItemListener createFragmentActionListener(TGFragment fragment) {
		TGActionProcessor tgActionProcessor = this.createActionProcessor(TGOpenFragmentAction.NAME);
		tgActionProcessor.setAttribute(TGOpenFragmentAction.ATTRIBUTE_FRAGMENT, fragment);
		tgActionProcessor.setAttribute(TGOpenFragmentAction.ATTRIBUTE_ACTIVITY, findActivity());
		return new TGToolViewItemListener(tgActionProcessor);
	}
	
	public void updateItem(int id, boolean enabled) {
		View view = findViewById(id);
		view.setEnabled(enabled);
	}
	
	public void updateCheckItem(int id, boolean enabled, boolean checked) {
		CheckBox checkBox = (CheckBox) findViewById(id);
		checkBox.setEnabled(enabled);
		checkBox.setChecked(checked);
	}
	
	public void processEvent(TGEvent event) {
		if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			int type = ((Integer)event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
			if( type == TGUpdateEvent.SELECTION ) {
				TGSynchronizer.getInstance(this.findContext()).executeLater(new Runnable() {
					public void run() throws TGException {
						updateItems();
					}
				});
			}
		}
	}
}
