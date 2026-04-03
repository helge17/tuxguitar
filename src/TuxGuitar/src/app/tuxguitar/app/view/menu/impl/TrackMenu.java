package app.tuxguitar.app.view.menu.impl;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.view.menu.TGMenuItem;
import app.tuxguitar.app.action.impl.track.TGGoFirstTrackAction;
import app.tuxguitar.app.action.impl.track.TGGoPreviousTrackAction;
import app.tuxguitar.app.action.impl.track.TGGoNextTrackAction;
import app.tuxguitar.app.action.impl.track.TGGoLastTrackAction;
import app.tuxguitar.editor.action.track.TGAddNewTrackAction;
import app.tuxguitar.editor.action.track.TGCloneTrackAction;
import app.tuxguitar.editor.action.track.TGRemoveTrackAction;
import app.tuxguitar.editor.action.track.TGMoveTrackUpAction;
import app.tuxguitar.editor.action.track.TGMoveTrackDownAction;
import app.tuxguitar.editor.action.track.TGChangeTrackSoloAction;
import app.tuxguitar.editor.action.track.TGChangeTrackMuteAction;
import app.tuxguitar.app.action.impl.track.TGToggleLyricEditorAction;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.action.impl.track.TGOpenTrackPropertiesDialogAction;
import app.tuxguitar.graphics.control.TGTrackImpl;
import app.tuxguitar.ui.menu.UIMenu;
import app.tuxguitar.ui.menu.UIMenuActionItem;
import app.tuxguitar.ui.menu.UIMenuCheckableItem;

public class TrackMenu extends TGMenuItem {

	private UIMenu menu;
	private UIMenuActionItem first;
	private UIMenuActionItem previous;
	private UIMenuActionItem next;
	private UIMenuActionItem last;
	private UIMenuActionItem addTrack;
	private UIMenuActionItem cloneTrack;
	private UIMenuActionItem removeTrack;
	private UIMenuActionItem moveUp;
	private UIMenuActionItem moveDown;
	private UIMenuCheckableItem changeSolo;
	private UIMenuCheckableItem changeMute;
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
			//--FIRST--
			this.first = this.menu.createActionItem();
			this.first.addSelectionListener(this.createActionProcessor(TGGoFirstTrackAction.NAME));

			//--PREVIOUS--
			this.previous = this.menu.createActionItem();
			this.previous.addSelectionListener(this.createActionProcessor(TGGoPreviousTrackAction.NAME));

			//--NEXT--
			this.next = this.menu.createActionItem();
			this.next.addSelectionListener(this.createActionProcessor(TGGoNextTrackAction.NAME));

			//--LAST--
			this.last = this.menu.createActionItem();
			this.last.addSelectionListener(this.createActionProcessor(TGGoLastTrackAction.NAME));

			//--SEPARATOR--
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

			//--SEPARATOR--
			this.menu.createSeparator();

			//--MOVE UP--
			this.moveUp = this.menu.createActionItem();
			this.moveUp.addSelectionListener(this.createActionProcessor(TGMoveTrackUpAction.NAME));

			//--MOVE DOWN--
			this.moveDown = this.menu.createActionItem();
			this.moveDown.addSelectionListener(this.createActionProcessor(TGMoveTrackDownAction.NAME));

			//--SEPARATOR--
			this.menu.createSeparator();

			//--SOLO--
			this.changeSolo = this.menu.createCheckItem();
			this.changeSolo.addSelectionListener(this.createActionProcessor(TGChangeTrackSoloAction.NAME));

			//--MUTE--
			this.changeMute = this.menu.createCheckItem();
			this.changeMute.addSelectionListener(this.createActionProcessor(TGChangeTrackMuteAction.NAME));

			//--SEPARATOR--
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
			setMenuItemTextAndAccelerator(this.previous, "track.previous", TGGoPreviousTrackAction.NAME);
			setMenuItemTextAndAccelerator(this.next, "track.next", TGGoNextTrackAction.NAME);
			setMenuItemTextAndAccelerator(this.last, "track.last", TGGoLastTrackAction.NAME);
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
			this.first.setEnabled(!isFirst);
			this.previous.setEnabled(!isFirst);
			this.next.setEnabled(!isLast);
			this.last.setEnabled(!isLast);
			this.addTrack.setEnabled(!running);
			this.cloneTrack.setEnabled(!running);
			this.removeTrack.setEnabled(!running);
			this.moveUp.setEnabled(!running && tracks > 1);
			this.moveDown.setEnabled(!running && tracks > 1);
			this.changeSolo.setChecked(track.isSolo());
			this.changeMute.setChecked(track.isMute());
			this.properties.setEnabled(!running);
		}
	}

	public void loadIcons(){
		this.first.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.TRACK_FIRST));
		this.previous.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.TRACK_PREVIOUS));
		this.next.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.TRACK_NEXT));
		this.last.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.TRACK_LAST));
		this.addTrack.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.TRACK_ADD));
		this.cloneTrack.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.TRACK_CLONE));
		this.removeTrack.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.TRACK_REMOVE));
		this.moveUp.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.ARROW_UP));
		this.moveDown.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.ARROW_DOWN));
		this.changeSolo.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.TRACK_SOLO));
		this.changeMute.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.TRACK_MUTE));
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
