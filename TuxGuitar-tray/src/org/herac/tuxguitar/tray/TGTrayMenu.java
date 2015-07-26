package org.herac.tuxguitar.tray;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.file.TGExitAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportPlayAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportStopAction;
import org.herac.tuxguitar.util.TGContext;

public class TGTrayMenu {
	
	private TGContext context;
	private Menu menu;
	private MenuItem play;
	private MenuItem stop;
	private MenuItem exit;
	
	public TGTrayMenu(TGContext context) {
		this.context = context;
	}
	
	public void make(){
		this.menu = new Menu (TuxGuitar.getInstance().getShell(), SWT.POP_UP);
		
		this.play = new MenuItem(this.menu,SWT.PUSH);
		this.play.addSelectionListener(new TGActionProcessorListener(this.context, TGTransportPlayAction.NAME));
		
		this.stop = new MenuItem(this.menu, SWT.PUSH);
		this.stop.addSelectionListener(new TGActionProcessorListener(this.context, TGTransportStopAction.NAME));
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		this.exit = new MenuItem(this.menu, SWT.PUSH);
		this.exit.addSelectionListener(new TGActionProcessorListener(this.context, TGExitAction.NAME));
		
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
			this.stop.setImage(TuxGuitar.getInstance().getIconManager().getTransportIconStop1());
			this.play.setImage(TuxGuitar.getInstance().getIconManager().getTransportIconPlay1());
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
