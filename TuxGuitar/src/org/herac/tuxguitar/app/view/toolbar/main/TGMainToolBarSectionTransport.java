package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.app.action.impl.transport.TGTransportPlayAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportStopAction;
import org.herac.tuxguitar.app.transport.TGTransport;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.toolbar.UIToolActionItem;

public class TGMainToolBarSectionTransport extends TGMainToolBarSection {
	
	private static final int STATUS_STOPPED = 1;
	private static final int STATUS_PAUSED = 2;
	private static final int STATUS_RUNNING = 3;
	
	private UIToolActionItem first;
	private UIToolActionItem last;
	private UIToolActionItem previous;
	private UIToolActionItem next;
	private UIToolActionItem stop;
	private UIToolActionItem play;
	private int status;
	
	public TGMainToolBarSectionTransport(TGMainToolBar toolBar) {
		super(toolBar);
	}
	
	public void createSection() {
		this.first = this.getToolBar().getControl().createActionItem();
		this.first.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTransport.getInstance(getToolBar().getContext()).gotoFirst();
			}
		});
		
		this.previous = this.getToolBar().getControl().createActionItem();
		this.previous.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTransport.getInstance(getToolBar().getContext()).gotoPrevious();
			}
		});
		
		this.stop = this.getToolBar().getControl().createActionItem();
		this.stop.addSelectionListener(this.createActionProcessor(TGTransportStopAction.NAME));
		
		this.play = this.getToolBar().getControl().createActionItem();
		this.play.addSelectionListener(this.createActionProcessor(TGTransportPlayAction.NAME));
		
		this.next = this.getToolBar().getControl().createActionItem();
		this.next.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTransport.getInstance(getToolBar().getContext()).gotoNext();
			}
		});
		
		this.last = this.getToolBar().getControl().createActionItem();
		this.last.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTransport.getInstance(getToolBar().getContext()).gotoLast();
			}
		});
		
		this.status = STATUS_STOPPED;
		this.loadIcons();
		this.loadProperties();
	}
	
	public void updateItems(){
		this.loadIcons(false);
	}
	
	public void loadProperties(){
		this.play.setToolTipText(this.getText("transport.start"));
		this.stop.setToolTipText(this.getText("transport.stop"));
		this.first.setToolTipText(this.getText("transport.first"));
		this.last.setToolTipText(this.getText("transport.last"));
		this.previous.setToolTipText(this.getText("transport.previous"));
		this.next.setToolTipText(this.getText("transport.next"));
	}
	
	public void loadIcons(){
		this.loadIcons(true);
	}
	
	public void loadIcons(boolean force){
		int lastStatus = this.status;
		
		MidiPlayer player = MidiPlayer.getInstance(this.getToolBar().getContext());
		if( player.isRunning()){
			this.status = STATUS_RUNNING;
		}else if(player.isPaused()){
			this.status = STATUS_PAUSED;
		}else{
			this.status = STATUS_STOPPED;
		}
		
		if(force || lastStatus != this.status){
			if( this.status == STATUS_RUNNING ){
				this.first.setImage(this.getIconManager().getTransportIconFirst2());
				this.last.setImage(this.getIconManager().getTransportIconLast2());
				this.previous.setImage(this.getIconManager().getTransportIconPrevious2());
				this.next.setImage(this.getIconManager().getTransportIconNext2());
				this.stop.setImage(this.getIconManager().getTransportIconStop2());
				this.play.setImage(this.getIconManager().getTransportIconPause());
				this.play.setToolTipText(this.getText("transport.pause"));
			}else if( this.status == STATUS_PAUSED ){
				this.first.setImage(this.getIconManager().getTransportIconFirst2());
				this.last.setImage(this.getIconManager().getTransportIconLast2());
				this.previous.setImage(this.getIconManager().getTransportIconPrevious2());
				this.next.setImage(this.getIconManager().getTransportIconNext2());
				this.stop.setImage(this.getIconManager().getTransportIconStop2());
				this.play.setImage(this.getIconManager().getTransportIconPlay2());
				this.play.setToolTipText(this.getText("transport.start"));
			}else if( this.status == STATUS_STOPPED ){
				this.first.setImage(this.getIconManager().getTransportIconFirst1());
				this.last.setImage(this.getIconManager().getTransportIconLast1());
				this.previous.setImage(this.getIconManager().getTransportIconPrevious1());
				this.next.setImage(this.getIconManager().getTransportIconNext1());
				this.stop.setImage(this.getIconManager().getTransportIconStop1());
				this.play.setImage(this.getIconManager().getTransportIconPlay1());
				this.play.setToolTipText(this.getText("transport.start"));
			}
		}
	}
}
