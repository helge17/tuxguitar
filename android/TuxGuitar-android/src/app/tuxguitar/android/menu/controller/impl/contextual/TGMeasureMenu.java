package app.tuxguitar.android.menu.controller.impl.contextual;

import android.view.Menu;
import android.view.MenuInflater;

import java.util.List;

import app.tuxguitar.android.R;
import app.tuxguitar.android.action.impl.measure.TGGoFirstMeasureAction;
import app.tuxguitar.android.action.impl.measure.TGGoPreviousMeasureAction;
import app.tuxguitar.android.action.impl.measure.TGGoNextMeasureAction;
import app.tuxguitar.android.action.impl.measure.TGGoLastMeasureAction;
import app.tuxguitar.android.action.TGActionProcessorListener;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.menu.controller.TGMenuBase;
import app.tuxguitar.android.view.dialog.measure.TGMeasureAddDialogController;
import app.tuxguitar.android.view.dialog.measure.TGMeasureCleanDialogController;
import app.tuxguitar.android.view.dialog.measure.TGMeasureRemoveDialogController;
import app.tuxguitar.android.view.tablature.TGCaret;
import app.tuxguitar.android.view.tablature.TGSongViewController;
import app.tuxguitar.editor.action.measure.TGFixMeasureVoiceAction;
import app.tuxguitar.editor.action.measure.TGRemoveUnusedVoiceAction;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.song.helpers.TGMeasureError;

public class TGMeasureMenu extends TGMenuBase {

	public TGMeasureMenu(TGActivity activity) {
		super(activity);
	}

	public void inflate(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_measure, menu);
		initializeItems(menu);
	}

	public void initializeItems(Menu menu) {
		boolean running = MidiPlayer.getInstance(this.findContext()).isRunning();
		TGCaret caret = TGSongViewController.getInstance(this.findContext()).getCaret();
		boolean isFirst = (caret.getMeasure().getNumber() == 1);
		boolean isLast = (caret.getMeasure().getNumber() == caret.getMeasure().getTrack().countMeasures());
		List<TGMeasureError> listErrors = caret.getSongManager().getMeasureManager().getMeasureErrors(caret.getMeasure());
		int voiceIndex = caret.getVoice();
		boolean voiceCanBeFixed = false;
		for (TGMeasureError err : listErrors) {
			voiceCanBeFixed |= (voiceIndex == err.getVoiceIndex());
		}
		if (voiceCanBeFixed) {
			// only some error types can be fixed
			for (TGMeasureError err : listErrors) {
				if (voiceIndex == err.getVoiceIndex()) {
					voiceCanBeFixed &= (err.canBeFixed());
				}
			}
		}
		this.initializeItem(menu, R.id.action_measure_first, this.createActionProcessor(TGGoFirstMeasureAction.NAME), !isFirst);
		this.initializeItem(menu, R.id.action_measure_previous, this.createActionProcessor(TGGoPreviousMeasureAction.NAME), !isFirst);
		this.initializeItem(menu, R.id.action_measure_next, this.createActionProcessor(TGGoNextMeasureAction.NAME), !isLast);
		this.initializeItem(menu, R.id.action_measure_last, this.createActionProcessor(TGGoLastMeasureAction.NAME), !isLast);
		this.initializeItem(menu, R.id.action_measure_add, new TGMeasureAddDialogController(), !running);
		this.initializeItem(menu, R.id.action_measure_clean, new TGMeasureCleanDialogController(), !running);
		this.initializeItem(menu, R.id.action_measure_remove, new TGMeasureRemoveDialogController(), !running);
		this.initializeItem(menu, R.id.action_remove_unused_voice, this.createActionProcessor(TGRemoveUnusedVoiceAction.NAME), !running);
		TGActionProcessorListener fixActionProcessorListener = this.createActionProcessor(TGFixMeasureVoiceAction.NAME);
		fixActionProcessorListener.setAttribute(TGFixMeasureVoiceAction.ATTRIBUTE_VOICE_INDEX, voiceIndex);
		this.initializeItem(menu, R.id.action_fix_voice, fixActionProcessorListener,  !running && voiceCanBeFixed);
	}
}
