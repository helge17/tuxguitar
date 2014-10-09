package org.herac.tuxguitar.app.items;

import org.eclipse.swt.widgets.ToolBar;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editors.TablatureEditor;

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
	
	public abstract void showItems(ToolBar toolBar);
	
}
