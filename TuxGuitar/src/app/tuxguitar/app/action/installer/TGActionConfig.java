package app.tuxguitar.app.action.installer;

import app.tuxguitar.app.action.listener.cache.TGUpdateController;
import app.tuxguitar.editor.undo.TGUndoableActionController;

public class TGActionConfig {

	private TGUpdateController updateController;
	private TGUndoableActionController undoableController;
	private boolean lockableAction;
	private boolean shortcutAvailable;
	private boolean disableOnPlaying;
	private boolean stopTransport;
	private boolean documentModifier;
	private boolean syncThread;
	private boolean unsavedInterceptor;
	private boolean invalidSongInterceptor;

	public TGActionConfig() {
		super();
	}

	public TGUpdateController getUpdateController() {
		return updateController;
	}

	public void setUpdateController(TGUpdateController updateController) {
		this.updateController = updateController;
	}

	public TGUndoableActionController getUndoableController() {
		return undoableController;
	}

	public void setUndoableController(TGUndoableActionController undoableController) {
		this.undoableController = undoableController;
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

	public boolean isStopTransport() {
		return stopTransport;
	}

	public void setStopTransport(boolean stopTransport) {
		this.stopTransport = stopTransport;
	}

	public boolean isDocumentModifier() {
		return documentModifier;
	}

	public void setDocumentModifier(boolean documentModifier) {
		this.documentModifier = documentModifier;
	}

	public boolean isSyncThread() {
		return syncThread;
	}

	public void setSyncThread(boolean syncThread) {
		this.syncThread = syncThread;
	}

	public boolean isUnsavedInterceptor() {
		return unsavedInterceptor;
	}

	public void setUnsavedInterceptor(boolean unsavedInterceptor) {
		this.unsavedInterceptor = unsavedInterceptor;
	}
	
	public boolean isInvalidSongInterceptor() {
		return this.invalidSongInterceptor;
	}
	
	public void setInvalidSongInterceptor(boolean invalidSongInterceptor) {
		this.invalidSongInterceptor = invalidSongInterceptor;
	}
}
