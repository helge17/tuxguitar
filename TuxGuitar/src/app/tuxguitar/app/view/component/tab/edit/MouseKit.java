package app.tuxguitar.app.view.component.tab.edit;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.edit.tablature.TGMouseClickAction;
import app.tuxguitar.app.action.impl.edit.tablature.TGMouseExitAction;
import app.tuxguitar.app.action.impl.edit.tablature.TGMouseMoveAction;
import app.tuxguitar.app.action.impl.layout.TGSetLayoutScaleDecrementAction;
import app.tuxguitar.app.action.impl.layout.TGSetLayoutScaleIncrementAction;
import app.tuxguitar.app.action.impl.selector.TGStartDragSelectionAction;
import app.tuxguitar.app.action.impl.selector.TGUpdateDragSelectionAction;
import app.tuxguitar.app.action.listener.gui.TGActionProcessingListener;
import app.tuxguitar.editor.TGEditorManager;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.ui.event.*;
import app.tuxguitar.ui.resource.UIPosition;
import app.tuxguitar.util.TGContext;

public class MouseKit implements UIMouseDownListener, UIMouseUpListener, UIMouseDragListener, UIMouseMoveListener, UIMouseExitListener, UIMenuShowListener, UIMenuHideListener, UIZoomListener {

	private EditorKit kit;
	private UIPosition position;
	private UIPosition startPosition;
	private boolean menuOpen;

	public MouseKit(EditorKit kit){
		this.kit = kit;
		this.position = new UIPosition(0,0);
		this.menuOpen = false;
	}

	public Float getStartPositionX() {
		return this.startPosition == null ? null : this.startPosition.getX();
	}
	
	public boolean isBusy() {
		TGContext context = this.kit.getTablature().getContext();
		return (TGEditorManager.getInstance(context).isLocked() || MidiPlayer.getInstance(context).isRunning());
	}

	public void executeAction(String actionId, UIPosition position, UIEvent event, boolean byPassProcessing) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.kit.getTablature().getContext(), actionId);
		tgActionProcessor.setAttribute(EditorKit.ATTRIBUTE_X, position.getX());
		tgActionProcessor.setAttribute(EditorKit.ATTRIBUTE_Y, position.getY());
		tgActionProcessor.setAttribute(TGActionProcessingListener.ATTRIBUTE_BY_PASS, byPassProcessing);
		tgActionProcessor.process();
	}

	public void onMouseDown(UIMouseEvent event) {
		if (event.getButton() == 1) {
			this.position.set(event.getPosition());
			this.startPosition = this.position.clone();
			if (event.isShiftDown()) {
				this.executeAction(TGUpdateDragSelectionAction.NAME, this.position.clone(), event, false);
			} else {
				this.executeAction(TGStartDragSelectionAction.NAME, this.position.clone(), event, false);
			}
		}
	}

	public void onMouseUp(UIMouseEvent event) {
		if (event.getButton() == 1) {
			this.startPosition = null;
			this.executeAction(TGMouseClickAction.NAME, this.position.clone(), event, false);
		}
	}

	public void onMouseDrag(UIMouseEvent event) {
		if (this.startPosition != null) {
			this.position.set(this.startPosition);
			this.position.add(event.getPosition());
			this.executeAction(TGUpdateDragSelectionAction.NAME, this.position.clone(), event, false);
		}
	}

	public void onMouseMove(UIMouseEvent event) {
		if(!this.menuOpen && this.kit.isMouseEditionAvailable() && !this.isBusy()){
			this.executeAction(TGMouseMoveAction.NAME, event.getPosition().clone(), event, true);
		}
	}

	public void onMouseExit(UIMouseEvent event) {
		if(!this.menuOpen && this.kit.isMouseEditionAvailable()) {
			this.executeAction(TGMouseExitAction.NAME, event.getPosition().clone(), event, true);
		}
	}

	public void onMenuShow(UIMenuEvent event) {
		this.menuOpen = true;
	}

	public void onMenuHide(UIMenuEvent event) {
		this.menuOpen = false;
		TuxGuitar.getInstance().updateCache(true);
	}

	public void onZoom(UIZoomEvent event) {
		if( event.getValue() > 0 ) {
			new TGActionProcessor(this.kit.getTablature().getContext(), TGSetLayoutScaleIncrementAction.NAME).process();
		} else {
			new TGActionProcessor(this.kit.getTablature().getContext(), TGSetLayoutScaleDecrementAction.NAME).process();
		}
	}
}
