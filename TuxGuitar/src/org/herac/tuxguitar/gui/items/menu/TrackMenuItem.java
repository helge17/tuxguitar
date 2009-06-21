/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.items.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.track.AddTrackAction;
import org.herac.tuxguitar.gui.actions.track.CloneTrackAction;
import org.herac.tuxguitar.gui.actions.track.EditLyricsAction;
import org.herac.tuxguitar.gui.actions.track.GoFirstTrackAction;
import org.herac.tuxguitar.gui.actions.track.GoLastTrackAction;
import org.herac.tuxguitar.gui.actions.track.GoNextTrackAction;
import org.herac.tuxguitar.gui.actions.track.GoPreviousTrackAction;
import org.herac.tuxguitar.gui.actions.track.MoveTrackDownAction;
import org.herac.tuxguitar.gui.actions.track.MoveTrackUpAction;
import org.herac.tuxguitar.gui.actions.track.RemoveTrackAction;
import org.herac.tuxguitar.gui.actions.track.TrackPropertiesAction;
import org.herac.tuxguitar.gui.editors.tab.TGTrackImpl;
import org.herac.tuxguitar.gui.items.MenuItems;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TrackMenuItem extends MenuItems{
	private MenuItem trackMenuItem;
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
	private MenuItem lyrics;
	private MenuItem properties;
	
	public TrackMenuItem(Shell shell,Menu parent, int style) {
		this.trackMenuItem = new MenuItem(parent, style);
		this.menu = new Menu(shell, SWT.DROP_DOWN);
	}
	
	public void showItems(){
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
		//--LYRICS--
		this.lyrics = new MenuItem(this.menu, SWT.PUSH);
		this.lyrics.addSelectionListener(TuxGuitar.instance().getAction(EditLyricsAction.NAME));
		//--PROPERTIES--
		this.properties = new MenuItem(this.menu, SWT.PUSH);
		this.properties.addSelectionListener(TuxGuitar.instance().getAction(TrackPropertiesAction.NAME));
		
		this.trackMenuItem.setMenu(this.menu);
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.trackMenuItem, "track", null);
		setMenuItemTextAndAccelerator(this.first, "track.first", GoFirstTrackAction.NAME);
		setMenuItemTextAndAccelerator(this.last, "track.last", GoLastTrackAction.NAME);
		setMenuItemTextAndAccelerator(this.previous, "track.previous", GoPreviousTrackAction.NAME);
		setMenuItemTextAndAccelerator(this.next, "track.next", GoNextTrackAction.NAME);
		setMenuItemTextAndAccelerator(this.addTrack, "track.add", AddTrackAction.NAME);
		setMenuItemTextAndAccelerator(this.cloneTrack, "track.clone", CloneTrackAction.NAME);
		setMenuItemTextAndAccelerator(this.removeTrack, "track.remove", RemoveTrackAction.NAME);
		setMenuItemTextAndAccelerator(this.moveUp, "track.move-up", MoveTrackUpAction.NAME);
		setMenuItemTextAndAccelerator(this.moveDown, "track.move-down", MoveTrackDownAction.NAME);
		setMenuItemTextAndAccelerator(this.lyrics, "track.lyrics", EditLyricsAction.NAME);
		setMenuItemTextAndAccelerator(this.properties, "track.properties", TrackPropertiesAction.NAME);
	}
	
	public void update(){
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
	}
	
	public void loadIcons(){
		//Nothing to do
	}
}
