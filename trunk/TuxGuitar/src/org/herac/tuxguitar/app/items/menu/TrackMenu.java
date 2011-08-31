/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.items.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.track.AddTrackAction;
import org.herac.tuxguitar.app.actions.track.ChangeTrackMuteAction;
import org.herac.tuxguitar.app.actions.track.ChangeTrackSoloAction;
import org.herac.tuxguitar.app.actions.track.CloneTrackAction;
import org.herac.tuxguitar.app.actions.track.EditLyricsAction;
import org.herac.tuxguitar.app.actions.track.GoFirstTrackAction;
import org.herac.tuxguitar.app.actions.track.GoLastTrackAction;
import org.herac.tuxguitar.app.actions.track.GoNextTrackAction;
import org.herac.tuxguitar.app.actions.track.GoPreviousTrackAction;
import org.herac.tuxguitar.app.actions.track.MoveTrackDownAction;
import org.herac.tuxguitar.app.actions.track.MoveTrackUpAction;
import org.herac.tuxguitar.app.actions.track.RemoveTrackAction;
import org.herac.tuxguitar.app.actions.track.TrackPropertiesAction;
import org.herac.tuxguitar.app.items.MenuItems;
import org.herac.tuxguitar.graphics.control.TGTrackImpl;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TrackMenu extends MenuItems{
	
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
			this.first.addSelectionListener(TuxGuitar.instance().getAction(GoFirstTrackAction.NAME));
			//--previous--
			this.previous = new MenuItem(this.menu, SWT.PUSH);
			this.previous.addSelectionListener(TuxGuitar.instance().getAction(GoPreviousTrackAction.NAME));
			//--next--
			this.next = new MenuItem(this.menu, SWT.PUSH);
			this.next.addSelectionListener(TuxGuitar.instance().getAction(GoNextTrackAction.NAME));
			//--last--
			this.last = new MenuItem(this.menu, SWT.PUSH);
			this.last.addSelectionListener(TuxGuitar.instance().getAction(GoLastTrackAction.NAME));
			//--SEPARATOR
			new MenuItem(this.menu, SWT.SEPARATOR);
			//--ADD TRACK--
			this.addTrack = new MenuItem(this.menu, SWT.PUSH);
			this.addTrack.addSelectionListener(TuxGuitar.instance().getAction(AddTrackAction.NAME));
			//--CLONE TRACK--
			this.cloneTrack = new MenuItem(this.menu, SWT.PUSH);
			this.cloneTrack.addSelectionListener(TuxGuitar.instance().getAction(CloneTrackAction.NAME));
			//--REMOVE TRACK--
			this.removeTrack = new MenuItem(this.menu, SWT.PUSH);
			this.removeTrack.addSelectionListener(TuxGuitar.instance().getAction(RemoveTrackAction.NAME));
			//--SEPARATOR
			new MenuItem(this.menu, SWT.SEPARATOR);
			//--MOVE UP--
			this.moveUp = new MenuItem(this.menu, SWT.PUSH);
			this.moveUp.addSelectionListener(TuxGuitar.instance().getAction(MoveTrackUpAction.NAME));
			//--MOVE DOWN--
			this.moveDown = new MenuItem(this.menu, SWT.PUSH);
			this.moveDown.addSelectionListener(TuxGuitar.instance().getAction(MoveTrackDownAction.NAME));
			//--SEPARATOR
			new MenuItem(this.menu, SWT.SEPARATOR);
			//--SOLO--
			this.changeSolo = new MenuItem(this.menu, SWT.CHECK);
			this.changeSolo.addSelectionListener(TuxGuitar.instance().getAction(ChangeTrackSoloAction.NAME));
			//--MUTE--
			this.changeMute = new MenuItem(this.menu, SWT.CHECK);
			this.changeMute.addSelectionListener(TuxGuitar.instance().getAction(ChangeTrackMuteAction.NAME));
			//--SEPARATOR
			new MenuItem(this.menu, SWT.SEPARATOR);
			//--LYRICS--
			this.lyrics = new MenuItem(this.menu, SWT.PUSH);
			this.lyrics.addSelectionListener(TuxGuitar.instance().getAction(EditLyricsAction.NAME));
			//--PROPERTIES--
			this.properties = new MenuItem(this.menu, SWT.PUSH);
			this.properties.addSelectionListener(TuxGuitar.instance().getAction(TrackPropertiesAction.NAME));
			
			this.loadIcons();
			this.loadProperties();
		}
	}
	
	public void loadProperties(){
		if(!isDisposed()){
			setMenuItemTextAndAccelerator(this.first, "track.first", GoFirstTrackAction.NAME);
			setMenuItemTextAndAccelerator(this.last, "track.last", GoLastTrackAction.NAME);
			setMenuItemTextAndAccelerator(this.previous, "track.previous", GoPreviousTrackAction.NAME);
			setMenuItemTextAndAccelerator(this.next, "track.next", GoNextTrackAction.NAME);
			setMenuItemTextAndAccelerator(this.addTrack, "track.add", AddTrackAction.NAME);
			setMenuItemTextAndAccelerator(this.cloneTrack, "track.clone", CloneTrackAction.NAME);
			setMenuItemTextAndAccelerator(this.removeTrack, "track.remove", RemoveTrackAction.NAME);
			setMenuItemTextAndAccelerator(this.moveUp, "track.move-up", MoveTrackUpAction.NAME);
			setMenuItemTextAndAccelerator(this.moveDown, "track.move-down", MoveTrackDownAction.NAME);
			setMenuItemTextAndAccelerator(this.changeSolo, "track.solo", ChangeTrackSoloAction.NAME);
			setMenuItemTextAndAccelerator(this.changeMute, "track.mute", ChangeTrackMuteAction.NAME);
			setMenuItemTextAndAccelerator(this.lyrics, "track.lyrics", EditLyricsAction.NAME);
			setMenuItemTextAndAccelerator(this.properties, "track.properties", TrackPropertiesAction.NAME);
		}
	}
	
	public void update(){
		if(!isDisposed()){
			TGTrackImpl track = TuxGuitar.instance().getTablatureEditor().getTablature().getCaret().getTrack();
			int tracks = track.getSong().countTracks();
			boolean isFirst = (track.getNumber() == 1);
			boolean isLast = (track.getNumber() == tracks);
			boolean running = TuxGuitar.instance().getPlayer().isRunning();
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
