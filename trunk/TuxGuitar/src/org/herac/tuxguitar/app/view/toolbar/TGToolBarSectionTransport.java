package org.herac.tuxguitar.app.view.toolbar;

import org.herac.tuxguitar.app.action.impl.transport.TGTransportPlayAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportStopAction;
import org.herac.tuxguitar.app.transport.TGTransport;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.toolbar.UIToolActionItem;

public class TGToolBarSectionTransport implements TGToolBarSection {
	
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
	
	public void createSection(final TGToolBar toolBar) {
		this.first = toolBar.getControl().createActionItem();
		this.first.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTransport.getInstance(toolBar.getContext()).gotoFirst();
			}
		});
		
		this.previous = toolBar.getControl().createActionItem();
		this.previous.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTransport.getInstance(toolBar.getContext()).gotoPrevious();
			}
		});
		
		this.stop = toolBar.getControl().createActionItem();
		this.stop.addSelectionListener(toolBar.createActionProcessor(TGTransportStopAction.NAME));
		
		this.play = toolBar.getControl().createActionItem();
		this.play.addSelectionListener(toolBar.createActionProcessor(TGTransportPlayAction.NAME));
		
		this.next = toolBar.getControl().createActionItem();
		this.next.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTransport.getInstance(toolBar.getContext()).gotoNext();
			}
		});
		
		this.last = toolBar.getControl().createActionItem();
		this.last.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGTransport.getInstance(toolBar.getContext()).gotoLast();
			}
		});
		
		this.status = STATUS_STOPPED;
		this.loadIcons(toolBar);
		this.loadProperties(toolBar);
	}
	
	public void updateItems(TGToolBar toolBar){
		this.loadIcons(toolBar, false);
	}
	
	public void loadProperties(TGToolBar toolBar){
		this.play.setToolTipText(toolBar.getText("transport.start"));
		this.stop.setToolTipText(toolBar.getText("transport.stop"));
		this.first.setToolTipText(toolBar.getText("transport.first"));
		this.last.setToolTipText(toolBar.getText("transport.last"));
		this.previous.setToolTipText(toolBar.getText("transport.previous"));
		this.next.setToolTipText(toolBar.getText("transport.next"));
	}
	
	public void loadIcons(TGToolBar toolBar){
		this.loadIcons(toolBar, true);
	}
	
	public void loadIcons(TGToolBar toolBar, boolean force){
		int lastStatus = this.status;
		
		MidiPlayer player = MidiPlayer.getInstance(toolBar.getContext());
		if( player.isRunning()){
			this.status = STATUS_RUNNING;
		}else if(player.isPaused()){
			this.status = STATUS_PAUSED;
		}else{
			this.status = STATUS_STOPPED;
		}
		
		if(force || lastStatus != this.status){
			if( this.status == STATUS_RUNNING ){
				this.first.setImage(toolBar.getIconManager().getTransportIconFirst2());
				this.last.setImage(toolBar.getIconManager().getTransportIconLast2());
				this.previous.setImage(toolBar.getIconManager().getTransportIconPrevious2());
				this.next.setImage(toolBar.getIconManager().getTransportIconNext2());
				this.stop.setImage(toolBar.getIconManager().getTransportIconStop2());
				this.play.setImage(toolBar.getIconManager().getTransportIconPause());
				this.play.setToolTipText(toolBar.getText("transport.pause"));
			}else if( this.status == STATUS_PAUSED ){
				this.first.setImage(toolBar.getIconManager().getTransportIconFirst2());
				this.last.setImage(toolBar.getIconManager().getTransportIconLast2());
				this.previous.setImage(toolBar.getIconManager().getTransportIconPrevious2());
				this.next.setImage(toolBar.getIconManager().getTransportIconNext2());
				this.stop.setImage(toolBar.getIconManager().getTransportIconStop2());
				this.play.setImage(toolBar.getIconManager().getTransportIconPlay2());
				this.play.setToolTipText(toolBar.getText("transport.start"));
			}else if( this.status == STATUS_STOPPED ){
				this.first.setImage(toolBar.getIconManager().getTransportIconFirst1());
				this.last.setImage(toolBar.getIconManager().getTransportIconLast1());
				this.previous.setImage(toolBar.getIconManager().getTransportIconPrevious1());
				this.next.setImage(toolBar.getIconManager().getTransportIconNext1());
				this.stop.setImage(toolBar.getIconManager().getTransportIconStop1());
				this.play.setImage(toolBar.getIconManager().getTransportIconPlay1());
				this.play.setToolTipText(toolBar.getText("transport.start"));
			}
		}
		
	}
}
