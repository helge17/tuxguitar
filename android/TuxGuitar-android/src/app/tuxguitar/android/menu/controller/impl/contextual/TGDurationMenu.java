package app.tuxguitar.android.menu.controller.impl.contextual;

import android.view.Menu;
import android.view.MenuInflater;

import app.tuxguitar.android.R;
import app.tuxguitar.android.action.TGActionProcessorListener;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.menu.controller.TGMenuBase;
import app.tuxguitar.android.view.tablature.TGCaret;
import app.tuxguitar.android.view.tablature.TGSongViewController;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.editor.action.duration.TGChangeDottedDurationAction;
import app.tuxguitar.editor.action.duration.TGChangeDoubleDottedDurationAction;
import app.tuxguitar.editor.action.duration.TGSetDivisionTypeDurationAction;
import app.tuxguitar.editor.action.duration.TGSetEighthDurationAction;
import app.tuxguitar.editor.action.duration.TGSetHalfDurationAction;
import app.tuxguitar.editor.action.duration.TGSetQuarterDurationAction;
import app.tuxguitar.editor.action.duration.TGSetSixteenthDurationAction;
import app.tuxguitar.editor.action.duration.TGSetSixtyFourthDurationAction;
import app.tuxguitar.editor.action.duration.TGSetThirtySecondDurationAction;
import app.tuxguitar.editor.action.duration.TGSetWholeDurationAction;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGDivisionType;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.util.TGContext;

public class TGDurationMenu extends TGMenuBase {

	public TGDurationMenu(TGActivity activity) {
		super(activity);
	}

	public void inflate(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_duration, menu);
		initializeItems(menu);
	}

	public void initializeItems(Menu menu) {
		TGContext context = findContext();
		TGCaret caret = TGSongViewController.getInstance(context).getCaret();
		TGDuration duration = caret.getDuration();
		boolean running = MidiPlayer.getInstance(context).isRunning();

		this.initializeItem(menu, R.id.action_set_duration_whole, this.createActionProcessor(TGSetWholeDurationAction.NAME), !running, duration.getValue() == TGDuration.WHOLE);
		this.initializeItem(menu, R.id.action_set_duration_half, this.createActionProcessor(TGSetHalfDurationAction.NAME), !running, duration.getValue() == TGDuration.HALF);
		this.initializeItem(menu, R.id.action_set_duration_quarter, this.createActionProcessor(TGSetQuarterDurationAction.NAME), !running, duration.getValue() == TGDuration.QUARTER);
		this.initializeItem(menu, R.id.action_set_duration_eighth, this.createActionProcessor(TGSetEighthDurationAction.NAME), !running, duration.getValue() == TGDuration.EIGHTH);
		this.initializeItem(menu, R.id.action_set_duration_sixteenth, this.createActionProcessor(TGSetSixteenthDurationAction.NAME), !running, duration.getValue() == TGDuration.SIXTEENTH);
		this.initializeItem(menu, R.id.action_set_duration_thirtysecond, this.createActionProcessor(TGSetThirtySecondDurationAction.NAME), !running, duration.getValue() == TGDuration.THIRTY_SECOND);
		this.initializeItem(menu, R.id.action_set_duration_sixtyfourth, this.createActionProcessor(TGSetSixtyFourthDurationAction.NAME), !running, duration.getValue() == TGDuration.SIXTY_FOURTH);
		this.initializeItem(menu, R.id.action_set_duration_dotted, this.createActionProcessor(TGChangeDottedDurationAction.NAME), !running, duration.isDotted());
		this.initializeItem(menu, R.id.action_set_duration_doubledotted, this.createActionProcessor(TGChangeDoubleDottedDurationAction.NAME), !running, duration.isDoubleDotted());

		this.initializeDivisionItem(menu, R.id.action_set_duration_division_type_1, TGDivisionType.NORMAL, duration, running);
		this.initializeDivisionItem(menu, R.id.action_set_duration_division_type_3, TGDivisionType.DIVISION_TYPES[1], duration, running);
		this.initializeDivisionItem(menu, R.id.action_set_duration_division_type_5, TGDivisionType.DIVISION_TYPES[2], duration, running);
		this.initializeDivisionItem(menu, R.id.action_set_duration_division_type_6, TGDivisionType.DIVISION_TYPES[3], duration, running);
		this.initializeDivisionItem(menu, R.id.action_set_duration_division_type_7, TGDivisionType.DIVISION_TYPES[4], duration, running);
		this.initializeDivisionItem(menu, R.id.action_set_duration_division_type_9, TGDivisionType.DIVISION_TYPES[5], duration, running);
		this.initializeDivisionItem(menu, R.id.action_set_duration_division_type_10, TGDivisionType.DIVISION_TYPES[6], duration, running);
		this.initializeDivisionItem(menu, R.id.action_set_duration_division_type_11, TGDivisionType.DIVISION_TYPES[7], duration, running);
		this.initializeDivisionItem(menu, R.id.action_set_duration_division_type_12, TGDivisionType.DIVISION_TYPES[8], duration, running);
		this.initializeDivisionItem(menu, R.id.action_set_duration_division_type_13, TGDivisionType.DIVISION_TYPES[9], duration, running);
	}

	public void initializeDivisionItem(Menu menu, int id, TGDivisionType divisionType, TGDuration duration, boolean running) {
		this.initializeItem(menu, id, this.createDivisionTypeActionProcessor(divisionType), !running, divisionType.isEqual(duration.getDivision()));
	}

	public TGActionProcessorListener createDivisionTypeActionProcessor(TGDivisionType divisionType) {
		TGSongManager tgSongManager = TGDocumentManager.getInstance(this.findContext()).getSongManager();
		TGActionProcessorListener tgActionProcessor = this.createActionProcessor(TGSetDivisionTypeDurationAction.NAME);
		tgActionProcessor.setAttribute(TGSetDivisionTypeDurationAction.PROPERTY_DIVISION_TYPE, divisionType.clone(tgSongManager.getFactory()));
		return tgActionProcessor;
	}
}
