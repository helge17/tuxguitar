/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.action;

import org.herac.tuxguitar.action.TGAction;
import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editors.TablatureEditor;
import org.herac.tuxguitar.app.undo.UndoableEdit;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.song.managers.TGSongManager;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window 
 * Preferences - Java - Code Style - Code Templates
 */
public abstract class TGActionBase implements TGAction {
	
	protected static final int AUTO_LOCK = 0x01;
	
	protected static final int AUTO_UNLOCK = 0x02;
	
	protected static final int AUTO_UPDATE = 0x04;
	
	public static final int KEY_BINDING_AVAILABLE = 0x08;
	
	protected static final int DISABLE_ON_PLAYING = 0x10;
	
	private String name;
	
	private int flags;
	
	public TGActionBase(String name, int flags) {
		this.name = name;
		this.flags = flags;
	}
	
	protected abstract void processAction(TGActionContext context);
	
	public synchronized void execute(final TGActionContext context) {
		this.processAction(context);
///////////		
//		if (!TGActionLock.isLocked() && !TuxGuitar.instance().isLocked()) {
//			final int flags = getFlags();
//			
//			if ((flags & DISABLE_ON_PLAYING) != 0 && TuxGuitar.instance().getPlayer().isRunning()) {
//				TuxGuitar.instance().updateCache(((flags & AUTO_UPDATE) != 0));
//				return;
//			}
//			
//			if ((flags & AUTO_LOCK) != 0) {
//				TGActionLock.lock();
//			}
//			
//			try {
//				TGSynchronizer.instance().executeLater(new TGSynchronizer.TGRunnable() {
//					public void run() throws TGException {
//						if (!TuxGuitar.isDisposed()) {
//							int result = processAction(context);
//							
//							TuxGuitar.instance().updateCache((((flags | result) & AUTO_UPDATE) != 0));
//							
//							if (((flags | result) & AUTO_UNLOCK) != 0) {
//								TGActionLock.unlock();
//							}
//						}
//					}
//				});
//			} catch (Throwable throwable) {
//				throwable.printStackTrace();
//			}

//		}
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getFlags() {
		return this.flags;
	}
	
	public TGDocumentManager getDocumentManager() {
		return TuxGuitar.getInstance().getDocumentManager();
	}
	
	public TGSongManager getSongManager() {
		return TuxGuitar.getInstance().getSongManager();
	}
	
	public TablatureEditor getEditor() {
		return TuxGuitar.getInstance().getTablatureEditor();
	}
	
	public void updateSong() {
		TuxGuitar.getInstance().updateSong();
	}
	
	public void updateMeasure(int number) {
		this.getEditor().getTablature().updateMeasure(number);
	}
	
	public void addUndoableEdit(UndoableEdit anEdit) {
		TuxGuitar.getInstance().getUndoableManager().addEdit(anEdit);
	}
}
