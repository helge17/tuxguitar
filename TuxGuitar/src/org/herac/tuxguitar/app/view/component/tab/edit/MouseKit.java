package org.herac.tuxguitar.app.view.component.tab.edit;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.edit.tablature.TGMenuShownAction;
import org.herac.tuxguitar.app.action.impl.edit.tablature.TGMouseClickAction;
import org.herac.tuxguitar.app.action.impl.edit.tablature.TGMouseExitAction;
import org.herac.tuxguitar.app.action.impl.edit.tablature.TGMouseMoveAction;
import org.herac.tuxguitar.app.action.listener.gui.TGActionProcessingListener;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.ui.event.UIMenuEvent;
import org.herac.tuxguitar.ui.event.UIMenuHideListener;
import org.herac.tuxguitar.ui.event.UIMenuShowListener;
import org.herac.tuxguitar.ui.event.UIMouseDownListener;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UIMouseExitListener;
import org.herac.tuxguitar.ui.event.UIMouseMoveListener;
import org.herac.tuxguitar.ui.event.UIMouseUpListener;
import org.herac.tuxguitar.ui.resource.UIPosition;
import org.herac.tuxguitar.util.TGContext;

public class MouseKit implements UIMouseDownListener, UIMouseUpListener, UIMouseMoveListener, UIMouseExitListener, UIMenuShowListener, UIMenuHideListener {
	
	private EditorKit kit;
	private UIPosition position;
	private boolean menuOpen;
	
	public MouseKit(EditorKit kit){
		this.kit = kit;
		this.position = new UIPosition(0,0);
		this.menuOpen = false;
	}
	
	public boolean isBusy() {
		TGContext context = this.kit.getTablature().getContext();
		return (TGEditorManager.getInstance(context).isLocked() || MidiPlayer.getInstance(context).isRunning());
	}
	
	public void executeAction(String actionId, float x, float y, boolean byPassProcessing) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.kit.getTablature().getContext(), actionId);
		tgActionProcessor.setAttribute(EditorKit.ATTRIBUTE_X, x);
		tgActionProcessor.setAttribute(EditorKit.ATTRIBUTE_Y, y);
		tgActionProcessor.setAttribute(TGActionProcessingListener.ATTRIBUTE_BY_PASS, byPassProcessing);
		tgActionProcessor.process();
	}
	
	public void onMouseDown(UIMouseEvent event) {
		this.position.setX(event.getPosition().getX());
		this.position.setY(event.getPosition().getY());
	}

	public void onMouseUp(UIMouseEvent event) {
		this.position.setX(event.getPosition().getX());
		this.position.setY(event.getPosition().getY());
		this.executeAction(TGMouseClickAction.NAME, event.getPosition().getX(), event.getPosition().getY(), false);
	}
	
	public void onMouseMove(UIMouseEvent event) {
		if(!this.menuOpen && this.kit.isMouseEditionAvailable() && !this.isBusy()){
			this.executeAction(TGMouseMoveAction.NAME, event.getPosition().getX(), event.getPosition().getY(), true);
		}
	}
	
	public void onMouseExit(UIMouseEvent event) {
		if(!this.menuOpen && this.kit.isMouseEditionAvailable()) {
			this.executeAction(TGMouseExitAction.NAME, event.getPosition().getX(), event.getPosition().getY(), true);
		}
	}
	
	public void onMenuShow(UIMenuEvent event) {
		this.menuOpen = true;
		this.executeAction(TGMenuShownAction.NAME, this.position.getX(), this.position.getY(), false);
	}
	
	public void onMenuHide(UIMenuEvent event) {
		this.menuOpen = false;
		TuxGuitar.getInstance().updateCache(true);
	}
}
