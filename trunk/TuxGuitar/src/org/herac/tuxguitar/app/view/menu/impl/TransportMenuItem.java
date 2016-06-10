package org.herac.tuxguitar.app.view.menu.impl;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.transport.TGOpenTransportModeDialogAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportCountDownAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportMetronomeAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportPlayAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportSetLoopEHeaderAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportSetLoopSHeaderAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportStopAction;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;
import org.herac.tuxguitar.player.base.MidiPlayerMode;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.ui.menu.UIMenu;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.menu.UIMenuCheckableItem;
import org.herac.tuxguitar.ui.menu.UIMenuSubMenuItem;

public class TransportMenuItem extends TGMenuItem {
	
	private static final int STATUS_STOPPED = 1;
	private static final int STATUS_PAUSED = 2;
	private static final int STATUS_RUNNING = 3;
	
	private UIMenuSubMenuItem transportMenuItem;
	
	private UIMenuActionItem play;
	private UIMenuActionItem stop;
	private UIMenuCheckableItem metronome;
	private UIMenuCheckableItem countDown;
	private UIMenuActionItem mode;
	private UIMenuCheckableItem loopSHeader;
	private UIMenuCheckableItem loopEHeader;
	
	private int status;
	
	public TransportMenuItem(UIMenu parent) {
		this.transportMenuItem = parent.createSubMenuItem();
	}
	
	public void showItems(){
		this.play = this.transportMenuItem.getMenu().createActionItem();
		this.play.addSelectionListener(this.createActionProcessor(TGTransportPlayAction.NAME));
		
		this.stop = this.transportMenuItem.getMenu().createActionItem();
		this.stop.addSelectionListener(this.createActionProcessor(TGTransportStopAction.NAME));
		
		//--SEPARATOR--
		this.transportMenuItem.getMenu().createSeparator();
		
		this.metronome = this.transportMenuItem.getMenu().createCheckItem();
		this.metronome.addSelectionListener(this.createActionProcessor(TGTransportMetronomeAction.NAME));
		
		this.countDown = this.transportMenuItem.getMenu().createCheckItem();
		this.countDown.addSelectionListener(this.createActionProcessor(TGTransportCountDownAction.NAME));
		
		this.mode = this.transportMenuItem.getMenu().createActionItem();
		this.mode.addSelectionListener(this.createActionProcessor(TGOpenTransportModeDialogAction.NAME));
		
		//--SEPARATOR--
		this.transportMenuItem.getMenu().createSeparator();
		
		this.loopSHeader = this.transportMenuItem.getMenu().createCheckItem();
		this.loopSHeader.addSelectionListener(this.createActionProcessor(TGTransportSetLoopSHeaderAction.NAME));
		
		this.loopEHeader = this.transportMenuItem.getMenu().createCheckItem();
		this.loopEHeader.addSelectionListener(this.createActionProcessor(TGTransportSetLoopEHeaderAction.NAME));
		
		this.status = STATUS_STOPPED;
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		TGMeasure measure = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getMeasure();
		MidiPlayerMode pm = TuxGuitar.getInstance().getPlayer().getMode();
		this.metronome.setChecked(TuxGuitar.getInstance().getPlayer().isMetronomeEnabled());
		this.countDown.setChecked(TuxGuitar.getInstance().getPlayer().getCountDown().isEnabled());
		this.loopSHeader.setEnabled( pm.isLoop() );
		this.loopSHeader.setChecked( measure != null && measure.getNumber() == pm.getLoopSHeader() );
		this.loopEHeader.setEnabled( pm.isLoop() );
		this.loopEHeader.setChecked( measure != null && measure.getNumber() == pm.getLoopEHeader() );
		this.loadIcons(false);
	}
	
	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.transportMenuItem, "transport", null);
		setMenuItemTextAndAccelerator(this.play, "transport.start", TGTransportPlayAction.NAME);
		setMenuItemTextAndAccelerator(this.stop, "transport.stop", TGTransportStopAction.NAME);
		setMenuItemTextAndAccelerator(this.mode, "transport.mode", TGOpenTransportModeDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.metronome, "transport.metronome", TGTransportMetronomeAction.NAME);
		setMenuItemTextAndAccelerator(this.countDown, "transport.count-down", TGTransportCountDownAction.NAME);
		setMenuItemTextAndAccelerator(this.loopSHeader, "transport.set-loop-start", TGTransportSetLoopSHeaderAction.NAME);
		setMenuItemTextAndAccelerator(this.loopEHeader, "transport.set-loop-end", TGTransportSetLoopEHeaderAction.NAME);
	}
	
	public void loadIcons(){
		this.loadIcons(true);
		this.mode.setImage(TuxGuitar.getInstance().getIconManager().getTransportMode());
		this.metronome.setImage(TuxGuitar.getInstance().getIconManager().getTransportMetronome());
	}
	
	public void loadIcons(boolean force){
		int lastStatus = this.status;
		
		if(TuxGuitar.getInstance().getPlayer().isRunning()){
			this.status = STATUS_RUNNING;
		}else if(TuxGuitar.getInstance().getPlayer().isPaused()){
			this.status = STATUS_PAUSED;
		}else{
			this.status = STATUS_STOPPED;
		}
		
		if(force || lastStatus != this.status){
			if(this.status == STATUS_RUNNING){
				this.stop.setImage(TuxGuitar.getInstance().getIconManager().getTransportIconStop2());
				this.play.setImage(TuxGuitar.getInstance().getIconManager().getTransportIconPause());
			}else if(this.status == STATUS_PAUSED){
				this.stop.setImage(TuxGuitar.getInstance().getIconManager().getTransportIconStop2());
				this.play.setImage(TuxGuitar.getInstance().getIconManager().getTransportIconPlay2());
			}else if(this.status == STATUS_STOPPED){
				this.stop.setImage(TuxGuitar.getInstance().getIconManager().getTransportIconStop1());
				this.play.setImage(TuxGuitar.getInstance().getIconManager().getTransportIconPlay1());
			}
		}
	}
}
