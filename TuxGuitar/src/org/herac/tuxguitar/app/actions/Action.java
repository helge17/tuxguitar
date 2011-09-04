/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editors.TablatureEditor;
import org.herac.tuxguitar.app.undo.UndoableEdit;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.util.TGSynchronizer;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window 
 * Preferences - Java - Code Style - Code Templates
 */
public abstract class Action extends ActionAdapter {
	
	protected static final int AUTO_LOCK = 0x01;
	
	protected static final int AUTO_UNLOCK = 0x02;
	
	protected static final int AUTO_UPDATE = 0x04;
	
	protected static final int KEY_BINDING_AVAILABLE = 0x08;
	
	protected static final int DISABLE_ON_PLAYING = 0x10;
	
	private String name;
	
	private int flags;
	
	public Action(String name, int flags) {
		this.name = name;
		this.flags = flags;
	}
	
	protected abstract int execute(ActionData actionData);
	
	public synchronized void process(final ActionData actionData) {
		if (!ActionLock.isLocked() && !TuxGuitar.instance().isLocked()) {
			final int flags = getFlags();
			
			if ((flags & DISABLE_ON_PLAYING) != 0 && TuxGuitar.instance().getPlayer().isRunning()) {
				TuxGuitar.instance().updateCache(((flags & AUTO_UPDATE) != 0));
				return;
			}
			
			if ((flags & AUTO_LOCK) != 0) {
				ActionLock.lock();
			}
			
			try {
				TGSynchronizer.instance().runLater(new TGSynchronizer.TGRunnable() {
					public void run() throws Throwable {
						if (!TuxGuitar.isDisposed()) {
							int result = execute(actionData);
							
							TuxGuitar.instance().updateCache((((flags | result) & AUTO_UPDATE) != 0));
							
							if (((flags | result) & AUTO_UNLOCK) != 0) {
								ActionLock.unlock();
							}
						}
					}
				});
			} catch (Throwable throwable) {
				throwable.printStackTrace();
			}
		}
	}
	
	protected int getFlags() {
		return this.flags;
	}
	
	public TGSongManager getSongManager() {
		return TuxGuitar.instance().getSongManager();
	}
	
	public TablatureEditor getEditor() {
		return TuxGuitar.instance().getTablatureEditor();
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean isKeyBindingAvailable() {
		return ((getFlags() & KEY_BINDING_AVAILABLE) != 0);
	}
	
	public synchronized void updateTablature() {
		TuxGuitar.instance().fireUpdate();
	}
	
	public void fireUpdate(int number) {
		this.getEditor().getTablature().updateMeasure(number);
	}
	
	public void addUndoableEdit(UndoableEdit anEdit) {
		TuxGuitar.instance().getUndoableManager().addEdit(anEdit);
	}
}
