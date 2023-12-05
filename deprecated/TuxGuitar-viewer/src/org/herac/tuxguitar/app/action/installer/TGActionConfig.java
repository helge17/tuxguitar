package org.herac.tuxguitar.app.action.installer;

import org.herac.tuxguitar.app.action.listener.cache.TGUpdateController;

public class TGActionConfig {
	
	private TGUpdateController updateController;
	private boolean lockableAction;
	private boolean shortcutAvailable;
	private boolean disableOnPlaying;
	private boolean syncThread;
	
	public TGActionConfig() {
		super();
	}

	public TGUpdateController getUpdateController() {
		return updateController;
	}

	public void setUpdateController(TGUpdateController updateController) {
		this.updateController = updateController;
	}

	public boolean isLockableAction() {
		return lockableAction;
	}

	public void setLockableAction(boolean lockableAction) {
		this.lockableAction = lockableAction;
	}

	public boolean isShortcutAvailable() {
		return shortcutAvailable;
	}

	public void setShortcutAvailable(boolean shortcutAvailable) {
		this.shortcutAvailable = shortcutAvailable;
	}

	public boolean isDisableOnPlaying() {
		return disableOnPlaying;
	}

	public void setDisableOnPlaying(boolean disableOnPlaying) {
		this.disableOnPlaying = disableOnPlaying;
	}

	public boolean isSyncThread() {
		return syncThread;
	}

	public void setSyncThread(boolean syncThread) {
		this.syncThread = syncThread;
	}
}
