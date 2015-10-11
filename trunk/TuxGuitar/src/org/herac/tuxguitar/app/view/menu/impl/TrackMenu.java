/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.view.menu.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.track.TGToggleLyricEditorAction;
import org.herac.tuxguitar.app.action.impl.track.TGGoFirstTrackAction;
import org.herac.tuxguitar.app.action.impl.track.TGGoLastTrackAction;
import org.herac.tuxguitar.app.action.impl.track.TGGoNextTrackAction;
import org.herac.tuxguitar.app.action.impl.track.TGGoPreviousTrackAction;
import org.herac.tuxguitar.app.action.impl.track.TGOpenTrackPropertiesDialogAction;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;
import org.herac.tuxguitar.editor.action.track.TGAddNewTrackAction;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackMuteAction;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackSoloAction;
import org.herac.tuxguitar.editor.action.track.TGCloneTrackAction;
import org.herac.tuxguitar.editor.action.track.TGMoveTrackDownAction;
import org.herac.tuxguitar.editor.action.track.TGMoveTrackUpAction;
import org.herac.tuxguitar.editor.action.track.TGRemoveTrackAction;
import org.herac.tuxguitar.graphics.control.TGTrackImpl;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TrackMenu extends TGMenuItem{
	
	private Menu menu;
	private MenuItem first;
	private MenuItem last;
	private MenuItem next;
	private MenuItem previous;
	private MenuItem addTrack;
	private MenuItem cloneTrack;
	private MenuItem removeTrack;
	private MenuItem moveUp;
	private MenuItem moveDown;
	private MenuItem changeMute;
	private MenuItem changeSolo;
	private MenuItem lyrics;
	private MenuItem properties;
	
	public TrackMenu(Menu menu) {
		this.menu = menu;
	}
	
	public TrackMenu(Shell shell, int style) {
		this(new Menu(shell, style));
	}
	
	public Menu getMenu() {
		return this.menu;
	}
	
	public void showItems(){
		if(!isDisposed()){
			//--First--
			this.first = new MenuItem(this.menu, SWT.PUSH);
			this.first.addSelectionListener(this.createActionProcessor(TGGoFirstTrackAction.NAME));
			//--previous--
			this.previous = new MenuItem(this.menu, SWT.PUSH);
			this.previous.addSelectionListener(this.createActionProcessor(TGGoPreviousTrackAction.NAME));
			//--next--
			this.next = new MenuItem(this.menu, SWT.PUSH);
			this.next.addSelectionListener(this.createActionProcessor(TGGoNextTrackAction.NAME));
			//--last--
			this.last = new MenuItem(this.menu, SWT.PUSH);
			this.last.addSelectionListener(this.createActionProcessor(TGGoLastTrackAction.NAME));
			//--SEPARATOR
			new MenuItem(this.menu, SWT.SEPARATOR);
			//--ADD TRACK--
			this.addTrack = new MenuItem(this.menu, SWT.PUSH);
			this.addTrack.addSelectionListener(this.createActionProcessor(TGAddNewTrackAction.NAME));
			//--CLONE TRACK--
			this.cloneTrack = new MenuItem(this.menu, SWT.PUSH);
			this.cloneTrack.addSelectionListener(this.createActionProcessor(TGCloneTrackAction.NAME));
			//--REMOVE TRACK--
			this.removeTrack = new MenuItem(this.menu, SWT.PUSH);
			this.removeTrack.addSelectionListener(this.createActionProcessor(TGRemoveTrackAction.NAME));
			//--SEPARATOR
			new MenuItem(this.menu, SWT.SEPARATOR);
			//--MOVE UP--
			this.moveUp = new MenuItem(this.menu, SWT.PUSH);
			this.moveUp.addSelectionListener(this.createActionProcessor(TGMoveTrackUpAction.NAME));
			//--MOVE DOWN--
			this.moveDown = new MenuItem(this.menu, SWT.PUSH);
			this.moveDown.addSelectionListener(this.createActionProcessor(TGMoveTrackDownAction.NAME));
			//--SEPARATOR
			new MenuItem(this.menu, SWT.SEPARATOR);
			//--SOLO--
			this.changeSolo = new MenuItem(this.menu, SWT.CHECK);
			this.changeSolo.addSelectionListener(this.createActionProcessor(TGChangeTrackSoloAction.NAME));
			//--MUTE--
			this.changeMute = new MenuItem(this.menu, SWT.CHECK);
			this.changeMute.addSelectionListener(this.createActionProcessor(TGChangeTrackMuteAction.NAME));
			//--SEPARATOR
			new MenuItem(this.menu, SWT.SEPARATOR);
			//--LYRICS--
			this.lyrics = new MenuItem(this.menu, SWT.PUSH);
			this.lyrics.addSelectionListener(this.createActionProcessor(TGToggleLyricEditorAction.NAME));
			//--PROPERTIES--
			this.properties = new MenuItem(this.menu, SWT.PUSH);
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
			this.changeSolo.setSelection(track.isSolo());
			this.changeMute.setSelection(track.isMute());
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
