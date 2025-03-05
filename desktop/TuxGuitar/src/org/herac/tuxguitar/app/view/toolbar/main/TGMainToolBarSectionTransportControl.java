package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.measure.TGGoFirstMeasureAction;
import org.herac.tuxguitar.app.action.impl.measure.TGGoLastMeasureAction;
import org.herac.tuxguitar.app.action.impl.measure.TGGoNextMeasureAction;
import org.herac.tuxguitar.app.action.impl.measure.TGGoPreviousMeasureAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportPlayPauseAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportStopAction;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.ui.toolbar.UIToolActionItem;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.util.TGContext;

public class TGMainToolBarSectionTransportControl extends TGMainToolBarSection {
	
	private UIToolActionItem first;
	private UIToolActionItem last;
	private UIToolActionItem previous;
	private UIToolActionItem next;
	private UIToolActionItem stop;
	private UIToolActionItem play;
	private boolean isRunning;
	
	public TGMainToolBarSectionTransportControl(TGContext context, UIToolBar toolBar) {
		super(context, toolBar);
	}
	
	public void createSection() {
		this.first = this.getToolBar().createActionItem();
		this.first.addSelectionListener(new TGActionProcessorListener(this.getContext(), TGGoFirstMeasureAction.NAME));
		
		this.previous = this.getToolBar().createActionItem();
		this.previous.addSelectionListener(new TGActionProcessorListener(this.getContext(), TGGoPreviousMeasureAction.NAME));
		
		this.play = this.getToolBar().createActionItem();
		this.play.addSelectionListener(this.createActionProcessor(TGTransportPlayPauseAction.NAME));
		
		this.stop = this.getToolBar().createActionItem();
		this.stop.addSelectionListener(this.createActionProcessor(TGTransportStopAction.NAME));

		this.next = this.getToolBar().createActionItem();
		this.next.addSelectionListener(new TGActionProcessorListener(this.getContext(), TGGoNextMeasureAction.NAME));
		
		this.last = this.getToolBar().createActionItem();
		this.last.addSelectionListener(new TGActionProcessorListener(this.getContext(), TGGoLastMeasureAction.NAME));
		
		this.isRunning = false;
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
		boolean lastStatusRunning = this.isRunning;
		
		MidiPlayer player = MidiPlayer.getInstance(this.getContext());
		this.isRunning = player.isRunning();
		
		if(force || lastStatusRunning != this.isRunning){
			this.first.setImage(this.getIconManager().getTransportIconFirst());
			this.last.setImage(this.getIconManager().getTransportIconLast());
			this.previous.setImage(this.getIconManager().getTransportIconPrevious());
			this.next.setImage(this.getIconManager().getTransportIconNext());
			this.stop.setImage(this.getIconManager().getTransportIconStop());
			if( this.isRunning ){
				this.play.setImage(this.getIconManager().getTransportIconPause());
				this.play.setToolTipText(this.getText("transport.pause"));
			} else {
				this.play.setImage(this.getIconManager().getTransportIconPlay());
				this.play.setToolTipText(this.getText("transport.start"));
			}
		}
	}
}
