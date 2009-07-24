/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.items.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.transport.TransportMetronomeAction;
import org.herac.tuxguitar.gui.actions.transport.TransportModeAction;
import org.herac.tuxguitar.gui.actions.transport.TransportPlayAction;
import org.herac.tuxguitar.gui.actions.transport.TransportSetLoopEHeaderAction;
import org.herac.tuxguitar.gui.actions.transport.TransportSetLoopSHeaderAction;
import org.herac.tuxguitar.gui.actions.transport.TransportStopAction;
import org.herac.tuxguitar.gui.items.MenuItems;
import org.herac.tuxguitar.player.base.MidiPlayerMode;
import org.herac.tuxguitar.song.models.TGMeasure;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TransportMenuItem extends MenuItems{
	private static final int STATUS_STOPPED = 1;
	private static final int STATUS_PAUSED = 2;
	private static final int STATUS_RUNNING = 3;
	
	private MenuItem transportMenuItem;
	private Menu menu;
	private MenuItem play;
	private MenuItem stop;
	private MenuItem metronome;
	private MenuItem mode;
	private MenuItem loopSHeader;
	private MenuItem loopEHeader;
	
	private int status;
	
	public TransportMenuItem(Shell shell,Menu parent, int style) {
		this.transportMenuItem = new MenuItem(parent, style);
		this.menu = new Menu(shell, SWT.DROP_DOWN);
	}
	
	public void showItems(){
		this.play = new MenuItem(this.menu,SWT.PUSH);
		this.play.addSelectionListener(TuxGuitar.instance().getAction(TransportPlayAction.NAME));
		
		this.stop = new MenuItem(this.menu, SWT.PUSH);
		this.stop.addSelectionListener(TuxGuitar.instance().getAction(TransportStopAction.NAME));
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		this.metronome = new MenuItem(this.menu, SWT.CHECK);
		this.metronome.addSelectionListener(TuxGuitar.instance().getAction(TransportMetronomeAction.NAME));
		
		this.mode = new MenuItem(this.menu, SWT.PUSH);
		this.mode.addSelectionListener(TuxGuitar.instance().getAction(TransportModeAction.NAME));
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		this.loopSHeader = new MenuItem(this.menu, SWT.CHECK);
		this.loopSHeader.addSelectionListener(TuxGuitar.instance().getAction(TransportSetLoopSHeaderAction.NAME));
		
		this.loopEHeader = new MenuItem(this.menu, SWT.CHECK);
		this.loopEHeader.addSelectionListener(TuxGuitar.instance().getAction(TransportSetLoopEHeaderAction.NAME));
		
		this.transportMenuItem.setMenu(this.menu);
		
		this.status = STATUS_STOPPED;
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		TGMeasure measure = TuxGuitar.instance().getTablatureEditor().getTablature().getCaret().getMeasure();
		MidiPlayerMode pm = TuxGuitar.instance().getPlayer().getMode();
		this.metronome.setSelection(TuxGuitar.instance().getPlayer().isMetronomeEnabled());
		this.loopSHeader.setEnabled( pm.isLoop() );
		this.loopSHeader.setSelection( measure != null && measure.getNumber() == pm.getLoopSHeader() );
		this.loopEHeader.setEnabled( pm.isLoop() );
		this.loopEHeader.setSelection( measure != null && measure.getNumber() == pm.getLoopEHeader() );
		this.loadIcons(false);
	}
	
	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.transportMenuItem, "transport", null);
		setMenuItemTextAndAccelerator(this.play, "transport.start", TransportPlayAction.NAME);
		setMenuItemTextAndAccelerator(this.stop, "transport.stop", TransportStopAction.NAME);
		setMenuItemTextAndAccelerator(this.mode, "transport.mode", TransportModeAction.NAME);
		setMenuItemTextAndAccelerator(this.metronome, "transport.metronome", TransportMetronomeAction.NAME);
		setMenuItemTextAndAccelerator(this.loopSHeader, "transport.set-loop-start", TransportSetLoopSHeaderAction.NAME);
		setMenuItemTextAndAccelerator(this.loopEHeader, "transport.set-loop-end", TransportSetLoopEHeaderAction.NAME);
	}
	
	public void loadIcons(){
		this.loadIcons(true);
		this.mode.setImage(TuxGuitar.instance().getIconManager().getTransportMode());
		this.metronome.setImage(TuxGuitar.instance().getIconManager().getTransportMetronome());
	}
	
	public void loadIcons(boolean force){
		int lastStatus = this.status;
		
		if(TuxGuitar.instance().getPlayer().isRunning()){
			this.status = STATUS_RUNNING;
		}else if(TuxGuitar.instance().getPlayer().isPaused()){
			this.status = STATUS_PAUSED;
		}else{
			this.status = STATUS_STOPPED;
		}
		
		if(force || lastStatus != this.status){
			if(this.status == STATUS_RUNNING){
				this.stop.setImage(TuxGuitar.instance().getIconManager().getTransportIconStop2());
				this.play.setImage(TuxGuitar.instance().getIconManager().getTransportIconPause());
			}else if(this.status == STATUS_PAUSED){
				this.stop.setImage(TuxGuitar.instance().getIconManager().getTransportIconStop2());
				this.play.setImage(TuxGuitar.instance().getIconManager().getTransportIconPlay2());
			}else if(this.status == STATUS_STOPPED){
				this.stop.setImage(TuxGuitar.instance().getIconManager().getTransportIconStop1());
				this.play.setImage(TuxGuitar.instance().getIconManager().getTransportIconPlay1());
			}
		}
	}
}
