package org.herac.tuxguitar.tray;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.file.ExitAction;
import org.herac.tuxguitar.gui.actions.transport.TransportPlayAction;
import org.herac.tuxguitar.gui.actions.transport.TransportStopAction;

public class TGTrayMenu {
	
	private Menu menu;
	private MenuItem play;
	private MenuItem stop;
	private MenuItem exit;
	
	public void make(){
		this.menu = new Menu (TuxGuitar.instance().getShell(), SWT.POP_UP);
		
		this.play = new MenuItem(this.menu,SWT.PUSH);
		this.play.addSelectionListener(TuxGuitar.instance().getAction(TransportPlayAction.NAME));
		
		this.stop = new MenuItem(this.menu, SWT.PUSH);
		this.stop.addSelectionListener(TuxGuitar.instance().getAction(TransportStopAction.NAME));
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		this.exit = new MenuItem(this.menu, SWT.PUSH);
		this.exit.addSelectionListener(TuxGuitar.instance().getAction(ExitAction.NAME));
		
		this.loadProperties();
		this.loadIcons();
	}
	
	public void loadProperties(){
		if(this.menu != null && !this.menu.isDisposed()){
			this.play.setText(TuxGuitar.getProperty("transport.start"));
			this.stop.setText(TuxGuitar.getProperty("transport.stop"));
			this.exit.setText(TuxGuitar.getProperty("file.exit"));
		}
	}
	
	public void loadIcons(){
		if(this.menu != null && !this.menu.isDisposed()){
			this.stop.setImage(TuxGuitar.instance().getIconManager().getTransportIconStop1());
			this.play.setImage(TuxGuitar.instance().getIconManager().getTransportIconPlay1());
		}
	}
	
	public void show(){
		if(this.menu != null && !this.menu.isDisposed()){
			this.menu.setVisible(true);
		}
	}
	
	public void dispose(){
		if(this.menu != null && !this.menu.isDisposed()){
			this.menu.dispose();
		}
	}
	
}
