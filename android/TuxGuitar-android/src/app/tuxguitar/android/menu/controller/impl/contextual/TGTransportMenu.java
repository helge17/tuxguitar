package app.tuxguitar.android.menu.controller.impl.contextual;

import android.view.Menu;
import android.view.MenuInflater;

import app.tuxguitar.android.R;
import app.tuxguitar.android.action.impl.layout.TGToggleHighlightPlayedBeatAction;
import app.tuxguitar.android.action.impl.transport.TGTransportPlayAction;
import app.tuxguitar.android.action.impl.transport.TGTransportStopAction;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.menu.controller.TGMenuBase;
import app.tuxguitar.android.view.tablature.TGSongViewController;
import app.tuxguitar.editor.action.transport.TGTransportCountDownAction;
import app.tuxguitar.editor.action.transport.TGTransportMetronomeAction;
import app.tuxguitar.graphics.control.TGLayout;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.util.TGContext;

public class TGTransportMenu extends TGMenuBase {

	public TGTransportMenu(TGActivity activity) {
		super(activity);
	}

	public void inflate(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_transport, menu);
		initializeItems(menu);
	}

	public void initializeItems(Menu menu) {
		TGContext context = findContext();
		MidiPlayer midiPlayer = MidiPlayer.getInstance(context);
		boolean running = midiPlayer.isRunning();
		TGLayout layout = TGSongViewController.getInstance(context).getLayout();
		int style = layout.getStyle();

		this.initializeItem(menu, R.id.action_transport_play, this.createActionProcessor(TGTransportPlayAction.NAME), true);
		this.initializeItem(menu, R.id.action_transport_stop, this.createActionProcessor(TGTransportStopAction.NAME), running);
		this.initializeItem(menu, R.id.action_transport_metronome, this.createActionProcessor(TGTransportMetronomeAction.NAME), true, midiPlayer.isMetronomeEnabled());
		this.initializeItem(menu, R.id.action_transport_count_down, this.createActionProcessor(TGTransportCountDownAction.NAME), true, midiPlayer.getCountDown().isEnabled());
		this.initializeItem(menu, R.id.action_transport_highlight_played_beat, this.createActionProcessor(TGToggleHighlightPlayedBeatAction.NAME), true, (style & TGLayout.HIGHLIGHT_PLAYED_BEAT) != 0 );
	}
}
