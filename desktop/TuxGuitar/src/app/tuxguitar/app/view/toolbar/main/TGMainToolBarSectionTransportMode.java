package app.tuxguitar.app.view.toolbar.main;

import app.tuxguitar.app.action.TGActionProcessorListener;
import app.tuxguitar.app.action.impl.transport.TGOpenTransportModeDialogAction;
import app.tuxguitar.app.action.impl.transport.TGTransportCountDownAction;
import app.tuxguitar.app.action.impl.transport.TGTransportMetronomeAction;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.ui.toolbar.UIToolActionItem;
import app.tuxguitar.ui.toolbar.UIToolBar;
import app.tuxguitar.ui.toolbar.UIToolCheckableItem;
import app.tuxguitar.util.TGContext;

public class TGMainToolBarSectionTransportMode extends TGMainToolBarSection {

	private UIToolCheckableItem metronome;
	private UIToolCheckableItem countDown;
	private UIToolActionItem mode;

	public TGMainToolBarSectionTransportMode(TGContext context, UIToolBar toolBar) {
		super(context, toolBar);
	}

	public void createSection() {
		this.metronome = this.getToolBar().createCheckItem();
		this.metronome.addSelectionListener(new TGActionProcessorListener(this.getContext(), TGTransportMetronomeAction.NAME));
		this.countDown = this.getToolBar().createCheckItem();
		this.countDown.addSelectionListener(new TGActionProcessorListener(this.getContext(), TGTransportCountDownAction.NAME));
		this.mode = this.getToolBar().createActionItem();
		this.mode.addSelectionListener(new TGActionProcessorListener(this.getContext(), TGOpenTransportModeDialogAction.NAME));

		this.loadIcons();
		this.loadProperties();
	}

	public void updateItems(){
		MidiPlayer player = MidiPlayer.getInstance(this.getContext());
		this.metronome.setChecked(player.isMetronomeEnabled());
		this.countDown.setChecked(player.getCountDown().isEnabled());
	}

	public void loadProperties(){
		this.metronome.setToolTipText(this.getText("transport.metronome"));
		this.countDown.setToolTipText(this.getText("transport.count-down"));
		this.mode.setToolTipText(this.getText("transport.mode"));
	}

	public void loadIcons(){
		this.loadIcons(true);
	}

	public void loadIcons(boolean force){
		this.metronome.setImage(this.getIconManager().getTransportMetronome());
		this.countDown.setImage(this.getIconManager().getTransportCountIn());
		this.mode.setImage(this.getIconManager().getTransportMode());
	}
}
