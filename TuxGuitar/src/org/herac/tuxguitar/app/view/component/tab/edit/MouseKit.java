package org.herac.tuxguitar.app.view.component.tab.edit;

import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Point;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.edit.tablature.TGMenuShownAction;
import org.herac.tuxguitar.app.action.impl.edit.tablature.TGMouseClickAction;
import org.herac.tuxguitar.app.action.impl.edit.tablature.TGMouseExitAction;
import org.herac.tuxguitar.app.action.impl.edit.tablature.TGMouseMoveAction;
import org.herac.tuxguitar.app.action.listener.gui.TGActionProcessingListener;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.util.TGContext;

public class MouseKit implements MouseListener, MouseMoveListener, MouseTrackListener, MenuListener{
	
	private EditorKit kit;
	private Point position;
	private boolean menuOpen;
	
	public MouseKit(EditorKit kit){
		this.kit = kit;
		this.position = new Point(0,0);
		this.menuOpen = false;
	}
	
	public boolean isBusy() {
		TGContext context = this.kit.getTablature().getContext();
		return (TGEditorManager.getInstance(context).isLocked() || MidiPlayer.getInstance(context).isRunning());
	}
	
	public void executeAction(String actionId, int x, int y, boolean byPassProcessing) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.kit.getTablature().getContext(), actionId);
		tgActionProcessor.setAttribute(EditorKit.ATTRIBUTE_X, x);
		tgActionProcessor.setAttribute(EditorKit.ATTRIBUTE_Y, y);
		tgActionProcessor.setAttribute(TGActionProcessingListener.ATTRIBUTE_BY_PASS, byPassProcessing);
		tgActionProcessor.process();
	}
	
	public void mouseDown(MouseEvent e) {
		this.position.x = e.x;
		this.position.y = e.y;
	}
	
	public void mouseUp(final MouseEvent e) {
		this.position.x = e.x;
		this.position.y = e.y;
		this.kit.getTablature().setFocus();
		this.executeAction(TGMouseClickAction.NAME, e.x, e.y, false);
	}
	
	public void mouseMove(final MouseEvent e) {
		if(!this.menuOpen && this.kit.isMouseEditionAvailable() && !this.isBusy()){
			this.executeAction(TGMouseMoveAction.NAME, e.x, e.y, true);
		}
	}
	
	public void mouseExit(final MouseEvent e) {
		if(!this.menuOpen && this.kit.isMouseEditionAvailable()) {
			this.executeAction(TGMouseExitAction.NAME, e.x, e.y, true);
		}
	}
	
	public void menuShown(final MenuEvent e) {
		this.menuOpen = true;
		this.executeAction(TGMenuShownAction.NAME, this.position.x, this.position.y, false);
	}
	
	public void menuHidden(MenuEvent e){
		this.menuOpen = false;
		TuxGuitar.getInstance().updateCache(true);
	}
	
	public void mouseDoubleClick(MouseEvent e) {
		//not implemented
	}
	
	public void mouseEnter(MouseEvent e) {
		//not implemented
	}
	
	public void mouseHover(MouseEvent e) {
		//not implemented
	}
}
