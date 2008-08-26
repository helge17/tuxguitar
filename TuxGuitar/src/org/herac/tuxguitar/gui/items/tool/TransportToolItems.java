/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.items.tool;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.transport.TransportPlayAction;
import org.herac.tuxguitar.gui.actions.transport.TransportStopAction;
import org.herac.tuxguitar.gui.items.ToolItems;
/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TransportToolItems  extends ToolItems{
	public static final String NAME = "transport.items";
	
	private static final int STATUS_STOPPED = 1;
	private static final int STATUS_PAUSED = 2;
	private static final int STATUS_RUNNING = 3;
	
	private ToolItem first;
	private ToolItem last;
	private ToolItem previous;
	private ToolItem next;
	private ToolItem stop;
	private ToolItem play;
	private int status;
	
	public TransportToolItems(){
		super(NAME);
	}
	
	public void showItems(ToolBar toolBar){
		this.first = new ToolItem(toolBar,SWT.PUSH);
		this.first.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TuxGuitar.instance().getTransport().gotoFirst();
			}
		});
		
		this.previous = new ToolItem(toolBar,SWT.PUSH);
		this.previous.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TuxGuitar.instance().getTransport().gotoPrevious();
			}
		});
		
		this.stop = new ToolItem(toolBar,SWT.PUSH);
		this.stop.addSelectionListener(TuxGuitar.instance().getAction(TransportStopAction.NAME));
		
		this.play = new ToolItem(toolBar,SWT.PUSH);
		this.play.addSelectionListener(TuxGuitar.instance().getAction(TransportPlayAction.NAME));
		
		this.next = new ToolItem(toolBar,SWT.PUSH);
		this.next.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TuxGuitar.instance().getTransport().gotoNext();
			}
		});
		
		this.last = new ToolItem(toolBar,SWT.PUSH);
		this.last.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TuxGuitar.instance().getTransport().gotoLast();
			}
		});
		
		this.status = STATUS_STOPPED;
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		loadIcons(false);
	}
	
	public void loadProperties(){
		this.play.setToolTipText(TuxGuitar.getProperty("transport.start"));
		this.stop.setToolTipText(TuxGuitar.getProperty("transport.stop"));
		this.first.setToolTipText(TuxGuitar.getProperty("transport.first"));
		this.last.setToolTipText(TuxGuitar.getProperty("transport.last"));
		this.previous.setToolTipText(TuxGuitar.getProperty("transport.previous"));
		this.next.setToolTipText(TuxGuitar.getProperty("transport.next"));
	}
	
	public void loadIcons(){
		loadIcons(true);
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
				this.first.setImage(TuxGuitar.instance().getIconManager().getTransportIconFirst2());
				this.last.setImage(TuxGuitar.instance().getIconManager().getTransportIconLast2());
				this.previous.setImage(TuxGuitar.instance().getIconManager().getTransportIconPrevious2());
				this.next.setImage(TuxGuitar.instance().getIconManager().getTransportIconNext2());
				this.stop.setImage(TuxGuitar.instance().getIconManager().getTransportIconStop2());
				this.play.setImage(TuxGuitar.instance().getIconManager().getTransportIconPause());
				this.play.setToolTipText(TuxGuitar.getProperty("transport.pause"));
			}else if(this.status == STATUS_PAUSED){
				this.first.setImage(TuxGuitar.instance().getIconManager().getTransportIconFirst2());
				this.last.setImage(TuxGuitar.instance().getIconManager().getTransportIconLast2());
				this.previous.setImage(TuxGuitar.instance().getIconManager().getTransportIconPrevious2());
				this.next.setImage(TuxGuitar.instance().getIconManager().getTransportIconNext2());
				this.stop.setImage(TuxGuitar.instance().getIconManager().getTransportIconStop2());
				this.play.setImage(TuxGuitar.instance().getIconManager().getTransportIconPlay2());
				this.play.setToolTipText(TuxGuitar.getProperty("transport.start"));
			}else if(this.status == STATUS_STOPPED){
				this.first.setImage(TuxGuitar.instance().getIconManager().getTransportIconFirst1());
				this.last.setImage(TuxGuitar.instance().getIconManager().getTransportIconLast1());
				this.previous.setImage(TuxGuitar.instance().getIconManager().getTransportIconPrevious1());
				this.next.setImage(TuxGuitar.instance().getIconManager().getTransportIconNext1());
				this.stop.setImage(TuxGuitar.instance().getIconManager().getTransportIconStop1());
				this.play.setImage(TuxGuitar.instance().getIconManager().getTransportIconPlay1());
				this.play.setToolTipText(TuxGuitar.getProperty("transport.start"));
			}
		}
		
	}
}
