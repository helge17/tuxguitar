package org.herac.tuxguitar.android.view.expandableList;

public abstract class TGExpandableResource {
	
	private boolean enabled;
	private TGExpandableHandler handler;
	
	public TGExpandableResource() {
		this.enabled = true;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public TGExpandableHandler getHandler() {
		return handler;
	}

	public void setHandler(TGExpandableHandler handler) {
		this.handler = handler;
	}
}
