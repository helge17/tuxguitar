package app.tuxguitar.app.view.toolbar.main;

import app.tuxguitar.util.TGContext;

public interface TGMainToolBarItemUpdater {

	default boolean enabled(TGContext context, boolean isRunning) {
		return true;
	}

	default boolean checked(TGContext context, boolean isRunning) {
		return false;
	}

	default String getIconName(TGContext context, boolean isRunning) {
		return null;
	}
	
	default String getText(TGContext context, boolean isRunning) {
		return null;
	}
}
