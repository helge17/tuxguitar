package org.herac.tuxguitar.app.view.toolbar;

import org.herac.tuxguitar.editor.action.track.TGAddNewTrackAction;
import org.herac.tuxguitar.editor.action.track.TGRemoveTrackAction;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.ui.toolbar.UIToolActionItem;

public class TGToolBarSectionTrack implements TGToolBarSection {
	
	private UIToolActionItem add;
	private UIToolActionItem remove;
	
	public void createSection(final TGToolBar toolBar) {
		this.add = toolBar.getControl().createActionItem();
		this.add.addSelectionListener(toolBar.createActionProcessor(TGAddNewTrackAction.NAME));
		
		this.remove = toolBar.getControl().createActionItem();
		this.remove.addSelectionListener(toolBar.createActionProcessor(TGRemoveTrackAction.NAME));
		
		this.loadIcons(toolBar);
		this.loadProperties(toolBar);
	}
	
	public void loadProperties(TGToolBar toolBar){
		this.add.setToolTipText(toolBar.getText("track.add"));
		this.remove.setToolTipText(toolBar.getText("track.remove"));
	}
	
	public void loadIcons(TGToolBar toolBar){
		this.add.setImage(toolBar.getIconManager().getTrackAdd());
		this.remove.setImage(toolBar.getIconManager().getTrackRemove());
	}
	
	public void updateItems(TGToolBar toolBar){
		boolean running = MidiPlayer.getInstance(toolBar.getContext()).isRunning();
		this.add.setEnabled(!running);
		this.remove.setEnabled(!running);
	}
}
