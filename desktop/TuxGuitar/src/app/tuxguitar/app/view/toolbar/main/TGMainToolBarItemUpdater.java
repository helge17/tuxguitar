package app.tuxguitar.app.view.toolbar.main;

import app.tuxguitar.util.TGContext;

public interface TGMainToolBarItemUpdater {

	default boolean enabled(TGContext context, boolean isRunning) {
		return true;
	}

	default boolean checked(TGContext context, boolean isRunning) {
		return false;
	}

}
