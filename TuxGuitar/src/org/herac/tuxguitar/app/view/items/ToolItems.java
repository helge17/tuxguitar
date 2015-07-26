package org.herac.tuxguitar.app.view.items;

import org.eclipse.swt.widgets.ToolBar;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.util.TGContext;

public abstract class ToolItems implements ItemBase{
	
	private String name;
	private boolean enabled;
	
	public ToolItems(String name){
		this.name = name;
		this.enabled = true;
	}
	
	public String getName(){
		return this.name;
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	protected TablatureEditor getEditor(){
		return TuxGuitar.getInstance().getTablatureEditor();
	}
	
	public TGActionProcessorListener createActionProcessor(String actionId) {
		return new TGActionProcessorListener(findContext(), actionId);
	}
	
	public TGContext findContext() {
		return TuxGuitar.getInstance().getContext();
	}
	
	public abstract void showItems(ToolBar toolBar);
	
}
