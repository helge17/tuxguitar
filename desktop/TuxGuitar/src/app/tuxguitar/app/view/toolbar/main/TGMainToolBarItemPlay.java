package app.tuxguitar.app.view.toolbar.main;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.transport.TGTransportPlayPauseAction;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.util.TGContext;

// a specific button: its icon changes depending from player status (play/pause)
public class TGMainToolBarItemPlay extends TGMainToolBarItemButton {

	private boolean isRunning;

	public TGMainToolBarItemPlay(String groupName, String text) {
		super(groupName, text, TGTransportPlayPauseAction.NAME, TGIconManager.TRANSPORT_ICON_PLAY, null);
		this.isRunning = false;
	}

	@Override
	public void update(TGContext context, boolean running) {
		boolean lastStatusRunning = this.isRunning;

		MidiPlayer player = MidiPlayer.getInstance(context);
		this.isRunning = player.isRunning();
		TGIconManager iconManager = TuxGuitar.getInstance().getIconManager();

		if (lastStatusRunning != this.isRunning) {
			this.getToolItem().setImage(iconManager.getImageByName(TGIconManager.TRANSPORT_ICON_PAUSE));
			this.getToolItem().setToolTipText(TuxGuitar.getProperty("transport.pause"));
		} else {
			this.getToolItem().setImage(iconManager.getImageByName(TGIconManager.TRANSPORT_ICON_PLAY));
			this.getToolItem().setToolTipText(TuxGuitar.getProperty("transport.start"));
		}
	}

}
