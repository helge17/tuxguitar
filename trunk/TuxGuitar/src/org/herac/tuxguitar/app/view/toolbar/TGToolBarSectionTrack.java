package org.herac.tuxguitar.app.view.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.editor.action.track.TGAddNewTrackAction;
import org.herac.tuxguitar.editor.action.track.TGRemoveTrackAction;
import org.herac.tuxguitar.player.base.MidiPlayer;

public class TGToolBarSectionTrack implements TGToolBarSection {
	
	private ToolItem add;
	private ToolItem remove;
	
	public void createSection(final TGToolBar toolBar) {
		this.add = new ToolItem(toolBar.getControl(), SWT.PUSH);
		this.add.addSelectionListener(toolBar.createActionProcessor(TGAddNewTrackAction.NAME));
		
		this.remove = new ToolItem(toolBar.getControl(), SWT.PUSH);
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
