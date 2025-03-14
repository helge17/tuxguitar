package app.tuxguitar.android.menu.controller.impl.contextual;

import android.view.Menu;
import android.view.MenuInflater;

import app.tuxguitar.android.R;
import app.tuxguitar.android.action.TGActionProcessorListener;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.menu.controller.TGMenuBase;
import app.tuxguitar.android.view.tablature.TGCaret;
import app.tuxguitar.android.view.tablature.TGSongViewController;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.note.TGChangeVelocityAction;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGVelocities;
import app.tuxguitar.util.TGContext;

public class TGVelocityMenu extends TGMenuBase {

	public TGVelocityMenu(TGActivity activity) {
		super(activity);
	}

	public void inflate(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_velocity, menu);
		initializeItems(menu);
	}

	public void initializeItems(Menu menu) {
		TGContext context = findContext();
		TGCaret caret = TGSongViewController.getInstance(context).getCaret();
		TGNote note = caret.getSelectedNote();
		int selection = ((note != null) ? note.getVelocity() : caret.getVelocity());
		boolean running = MidiPlayer.getInstance(context).isRunning();

		this.initializeVelocityItem(menu, R.id.action_set_velocity_ppp, TGVelocities.PIANO_PIANISSIMO, selection, running);
		this.initializeVelocityItem(menu, R.id.action_set_velocity_pp, TGVelocities.PIANISSIMO, selection, running);
		this.initializeVelocityItem(menu, R.id.action_set_velocity_p, TGVelocities.PIANO, selection, running);
		this.initializeVelocityItem(menu, R.id.action_set_velocity_mp, TGVelocities.MEZZO_PIANO, selection, running);
		this.initializeVelocityItem(menu, R.id.action_set_velocity_mf, TGVelocities.MEZZO_FORTE, selection, running);
		this.initializeVelocityItem(menu, R.id.action_set_velocity_f, TGVelocities.FORTE, selection, running);
		this.initializeVelocityItem(menu, R.id.action_set_velocity_ff, TGVelocities.FORTISSIMO, selection, running);
		this.initializeVelocityItem(menu, R.id.action_set_velocity_fff, TGVelocities.FORTE_FORTISSIMO, selection, running);
	}

	public void initializeVelocityItem(Menu menu, int id, int value, int selection, boolean playerRunning) {
		this.initializeItem(menu, id, this.createVelocityActionProcessor(value), !playerRunning, (value == selection));
	}

	public TGActionProcessorListener createVelocityActionProcessor(Integer velocity) {
		TGActionProcessorListener tgActionProcessor = this.createActionProcessor(TGChangeVelocityAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY, velocity);
		return tgActionProcessor;
	}
}
