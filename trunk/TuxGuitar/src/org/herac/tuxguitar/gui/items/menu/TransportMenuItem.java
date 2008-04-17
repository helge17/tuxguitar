/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.items.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.transport.TransportMetronomeAction;
import org.herac.tuxguitar.gui.actions.transport.TransportModeAction;
import org.herac.tuxguitar.gui.items.MenuItems;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TransportMenuItem implements MenuItems{
	private static final int STATUS_STOPPED = 1;
	private static final int STATUS_PAUSED = 2;
	private static final int STATUS_RUNNING = 3;    	

    private MenuItem transportMenuItem;
    private Menu menu;     
    private MenuItem play;
    private MenuItem stop;    
    private MenuItem mode;
    private MenuItem metronome;
    
    private int status;
    
    public TransportMenuItem(Shell shell,Menu parent, int style) {
        this.transportMenuItem = new MenuItem(parent, style);
        this.menu = new Menu(shell, SWT.DROP_DOWN);
    }

    
    public void showItems(){                 				
		this.play = new MenuItem(this.menu,SWT.PUSH);
		this.play.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TuxGuitar.instance().getTransport().play(e);
			}		
		});		 
		
        this.stop = new MenuItem(this.menu, SWT.PUSH);
		this.stop.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TuxGuitar.instance().getTransport().stop(e);
			}		
		});				
        		
        //--SEPARATOR--
        new MenuItem(this.menu, SWT.SEPARATOR);		
		
        this.metronome = new MenuItem(this.menu, SWT.CHECK);
        this.metronome.addSelectionListener(TuxGuitar.instance().getAction(TransportMetronomeAction.NAME));
        
		this.mode = new MenuItem(this.menu, SWT.PUSH);
		this.mode.addSelectionListener(TuxGuitar.instance().getAction(TransportModeAction.NAME));
		
        this.transportMenuItem.setMenu(this.menu);
        
        this.status = STATUS_STOPPED;
        this.loadIcons();
        this.loadProperties();
    }

    public void update(){
    	this.metronome.setSelection(TuxGuitar.instance().getPlayer().isMetronomeEnabled());
    	this.loadIcons(false);
    }
    
    public void loadProperties(){
        this.transportMenuItem.setText(TuxGuitar.getProperty("transport"));  
        this.play.setText(TuxGuitar.getProperty("transport.start"));
        this.stop.setText(TuxGuitar.getProperty("transport.stop"));        
        this.mode.setText(TuxGuitar.getProperty("transport.mode"));
        this.metronome.setText(TuxGuitar.getProperty("transport.metronome"));
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
