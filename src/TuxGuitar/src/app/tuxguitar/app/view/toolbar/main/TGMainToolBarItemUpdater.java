package app.tuxguitar.app.view.toolbar.main;

import app.tuxguitar.util.TGContext;

public abstract class TGMainToolBarItemUpdater {

	public boolean enabled(TGContext context, boolean isRunning) {
		return true;
	}

	public boolean checked(TGContext context, boolean isRunning) {
		return false;
	}

	public String getIconName(TGContext context, boolean isRunning) {
		return null;
	}
	
	public String getText(TGContext context, boolean isRunning) {
		return null;
	}
}
