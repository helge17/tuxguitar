package org.herac.tuxguitar.app.editors.tab.edit;

import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Point;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionLock;

public class MouseKit implements MouseListener, MouseMoveListener, MouseTrackListener, MenuListener{
	
	private EditorKit kit;
	private Point position;
	private boolean menuOpen;
	
	public MouseKit(EditorKit kit){
		this.kit = kit;
		this.position = new Point(0,0);
		this.menuOpen = false;
	}
	
	public void mouseDown(MouseEvent e) {
		this.position.x = e.x;
		this.position.y = e.y;
	}
	
	public void mouseUp(MouseEvent e) {
		this.position.x = e.x;
		this.position.y = e.y;
		this.kit.getTablature().setFocus();
		if( this.kit.select(this.position.x, this.position.y) ){
			TuxGuitar.getInstance().updateCache(true);
			if(!this.menuOpen && e.button == 1 && this.kit.isMouseEditionAvailable()){
				if(!TuxGuitar.getInstance().isLocked() && !TGActionLock.isLocked() && !TuxGuitar.getInstance().getPlayer().isRunning()){
					this.kit.addOrRemoveNote(e.x, e.y);
				}
			}
		}
	}
	
	public void mouseMove(MouseEvent e) {
		if(!this.menuOpen && this.kit.isMouseEditionAvailable()){
			if(!TuxGuitar.getInstance().isLocked() && !TGActionLock.isLocked() && !TuxGuitar.getInstance().getPlayer().isRunning()){
				this.kit.updateSelectedMeasure(e.x, e.y);
			}
		}
	}
	
	public void mouseExit(MouseEvent e) {
		if(!this.menuOpen && this.kit.isMouseEditionAvailable()){
			if(!TuxGuitar.getInstance().getPlayer().isRunning() ){
				this.kit.resetSelectedMeasure();
			}
		}
	}
	
	public void menuShown(MenuEvent e) {
		this.menuOpen = true;
		this.kit.select(this.position.x, this.position.y);
		TuxGuitar.getInstance().updateCache(true);
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
