/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions;

import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.TablatureEditor;
import org.herac.tuxguitar.gui.undo.UndoableEdit;
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
	
	protected abstract int execute(TypedEvent e);
	
	public synchronized void process(final TypedEvent e) {
		if (!ActionLock.isLocked() && !TuxGuitar.instance().isLocked()) {
			final int flags = getFlags();
			
			if ((flags & DISABLE_ON_PLAYING) != 0 && TuxGuitar.instance().getPlayer().isRunning()) {
				TuxGuitar.instance().updateCache(((flags & AUTO_UPDATE) != 0));
				return;
			}
			
			if ((flags & AUTO_LOCK) != 0) {
				ActionLock.lock();
			}
			/*
			new SyncThread(new Runnable() {
				public void run() {
					if (!TuxGuitar.isDisposed()) {
						int result = execute(e);
						
						TuxGuitar.instance().updateCache((((flags | result) & AUTO_UPDATE) != 0));
						
						if (((flags | result) & AUTO_UNLOCK) != 0) {
							ActionLock.unlock();
						}
					}
				}
			}).start();
			*/
			try {
				TGSynchronizer.instance().runLater(new TGSynchronizer.TGRunnable() {
					public void run() throws Throwable {
						if (!TuxGuitar.isDisposed()) {
							int result = execute(e);
							
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
	
	public void fireUpdate(int measureNumber) {
		this.getEditor().getTablature().getViewLayout().fireUpdate(measureNumber);
	}
	
	public void addUndoableEdit(UndoableEdit anEdit) {
		TuxGuitar.instance().getUndoableManager().addEdit(anEdit);
	}
}
