package org.herac.tuxguitar.app.view.menu.impl;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.track.TGGoFirstTrackAction;
import org.herac.tuxguitar.app.action.impl.track.TGGoLastTrackAction;
import org.herac.tuxguitar.app.action.impl.track.TGGoNextTrackAction;
import org.herac.tuxguitar.app.action.impl.track.TGGoPreviousTrackAction;
import org.herac.tuxguitar.app.action.impl.track.TGOpenTrackPropertiesDialogAction;
import org.herac.tuxguitar.app.action.impl.track.TGToggleLyricEditorAction;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;
import org.herac.tuxguitar.editor.action.track.TGAddNewTrackAction;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackMuteAction;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackSoloAction;
import org.herac.tuxguitar.editor.action.track.TGCloneTrackAction;
import org.herac.tuxguitar.editor.action.track.TGMoveTrackDownAction;
import org.herac.tuxguitar.editor.action.track.TGMoveTrackUpAction;
import org.herac.tuxguitar.editor.action.track.TGRemoveTrackAction;
import org.herac.tuxguitar.graphics.control.TGTrackImpl;
import org.herac.tuxguitar.ui.menu.UIMenu;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.menu.UIMenuCheckableItem;

public class TrackMenu extends TGMenuItem {
	
	private UIMenu menu;
	private UIMenuActionItem first;
	private UIMenuActionItem last;
	private UIMenuActionItem next;
	private UIMenuActionItem previous;
	private UIMenuActionItem addTrack;
	private UIMenuActionItem cloneTrack;
	private UIMenuActionItem removeTrack;
	private UIMenuActionItem moveUp;
	private UIMenuActionItem moveDown;
	private UIMenuCheckableItem changeMute;
	private UIMenuCheckableItem changeSolo;
	private UIMenuActionItem lyrics;
	private UIMenuActionItem properties;
	
	public TrackMenu(UIMenu menu) {
		this.menu = menu;
	}
	
	public UIMenu getMenu() {
		return this.menu;
	}
	
	public void showItems(){
		if(!isDisposed()){
			//--First--
			this.first = this.menu.createActionItem();
			this.first.addSelectionListener(this.createActionProcessor(TGGoFirstTrackAction.NAME));
			//--previous--
			this.previous = this.menu.createActionItem();
			this.previous.addSelectionListener(this.createActionProcessor(TGGoPreviousTrackAction.NAME));
			//--next--
			this.next = this.menu.createActionItem();
			this.next.addSelectionListener(this.createActionProcessor(TGGoNextTrackAction.NAME));
			//--last--
			this.last = this.menu.createActionItem();
			this.last.addSelectionListener(this.createActionProcessor(TGGoLastTrackAction.NAME));
			//--SEPARATOR
			this.menu.createSeparator();
			//--ADD TRACK--
			this.addTrack = this.menu.createActionItem();
			this.addTrack.addSelectionListener(this.createActionProcessor(TGAddNewTrackAction.NAME));
			//--CLONE TRACK--
			this.cloneTrack = this.menu.createActionItem();
			this.cloneTrack.addSelectionListener(this.createActionProcessor(TGCloneTrackAction.NAME));
			//--REMOVE TRACK--
			this.removeTrack = this.menu.createActionItem();
			this.removeTrack.addSelectionListener(this.createActionProcessor(TGRemoveTrackAction.NAME));
			//--SEPARATOR
			this.menu.createSeparator();
			//--MOVE UP--
			this.moveUp = this.menu.createActionItem();
			this.moveUp.addSelectionListener(this.createActionProcessor(TGMoveTrackUpAction.NAME));
			//--MOVE DOWN--
			this.moveDown = this.menu.createActionItem();
			this.moveDown.addSelectionListener(this.createActionProcessor(TGMoveTrackDownAction.NAME));
			//--SEPARATOR
			this.menu.createSeparator();
			//--SOLO--
			this.changeSolo = this.menu.createCheckItem();
			this.changeSolo.addSelectionListener(this.createActionProcessor(TGChangeTrackSoloAction.NAME));
			//--MUTE--
			this.changeMute = this.menu.createCheckItem();
			this.changeMute.addSelectionListener(this.createActionProcessor(TGChangeTrackMuteAction.NAME));
			//--SEPARATOR
			this.menu.createSeparator();
			//--LYRICS--
			this.lyrics = this.menu.createActionItem();
			this.lyrics.addSelectionListener(this.createActionProcessor(TGToggleLyricEditorAction.NAME));
			//--PROPERTIES--
			this.properties = this.menu.createActionItem();
			this.properties.addSelectionListener(this.createActionProcessor(TGOpenTrackPropertiesDialogAction.NAME));
			
			this.loadIcons();
			this.loadProperties();
		}
	}
	
	public void loadProperties(){
		if(!isDisposed()){
			setMenuItemTextAndAccelerator(this.first, "track.first", TGGoFirstTrackAction.NAME);
			setMenuItemTextAndAccelerator(this.last, "track.last", TGGoLastTrackAction.NAME);
			setMenuItemTextAndAccelerator(this.previous, "track.previous", TGGoPreviousTrackAction.NAME);
			setMenuItemTextAndAccelerator(this.next, "track.next", TGGoNextTrackAction.NAME);
			setMenuItemTextAndAccelerator(this.addTrack, "track.add", TGAddNewTrackAction.NAME);
			setMenuItemTextAndAccelerator(this.cloneTrack, "track.clone", TGCloneTrackAction.NAME);
			setMenuItemTextAndAccelerator(this.removeTrack, "track.remove", TGRemoveTrackAction.NAME);
			setMenuItemTextAndAccelerator(this.moveUp, "track.move-up", TGMoveTrackUpAction.NAME);
			setMenuItemTextAndAccelerator(this.moveDown, "track.move-down", TGMoveTrackDownAction.NAME);
			setMenuItemTextAndAccelerator(this.changeSolo, "track.solo", TGChangeTrackSoloAction.NAME);
			setMenuItemTextAndAccelerator(this.changeMute, "track.mute", TGChangeTrackMuteAction.NAME);
			setMenuItemTextAndAccelerator(this.lyrics, "track.lyrics", TGToggleLyricEditorAction.NAME);
			setMenuItemTextAndAccelerator(this.properties, "track.properties", TGOpenTrackPropertiesDialogAction.NAME);
		}
	}
	
	public void update(){
		if(!isDisposed()){
			TGTrackImpl track = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack();
			int tracks = track.getSong().countTracks();
			boolean isFirst = (track.getNumber() == 1);
			boolean isLast = (track.getNumber() == tracks);
			boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
			this.addTrack.setEnabled(!running);
			this.cloneTrack.setEnabled(!running);
			this.removeTrack.setEnabled(!running);
			this.moveUp.setEnabled(!running && tracks > 1);
			this.moveDown.setEnabled(!running && tracks > 1);
			this.first.setEnabled(!isFirst);
			this.previous.setEnabled(!isFirst);
			this.next.setEnabled(!isLast);
			this.last.setEnabled(!isLast);
			this.properties.setEnabled(!running);
			this.changeSolo.setChecked(track.isSolo());
			this.changeMute.setChecked(track.isMute());
		}
	}
	
	public void loadIcons(){
		//Nothing to do
	}
	
	public boolean isDisposed(){
		return (this.menu == null || this.menu.isDisposed());
	}
	
	public void dispose(){
		if(!isDisposed()){
			this.menu.dispose();
		}
	}
}
