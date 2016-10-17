package org.herac.tuxguitar.android.menu.context.impl;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.TGActionProcessorListener;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.menu.context.TGContextMenuBase;
import org.herac.tuxguitar.android.view.tablature.TGCaret;
import org.herac.tuxguitar.android.view.tablature.TGSongViewController;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.editor.action.duration.TGChangeDottedDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGChangeDoubleDottedDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetDivisionTypeDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetEighthDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetHalfDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetQuarterDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetSixteenthDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetSixtyFourthDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetThirtySecondDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetWholeDurationAction;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGDivisionType;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.util.TGContext;

import android.view.ContextMenu;
import android.view.MenuInflater;

public class TGDurationMenu extends TGContextMenuBase {
	
	public TGDurationMenu(TGActivity activity) {
		super(activity);
	}

	public void inflate(ContextMenu menu, MenuInflater inflater) {
		menu.setHeaderTitle(R.string.menu_duration);
		inflater.inflate(R.menu.menu_duration, menu);
		initializeItems(menu);
	}
	
	public void initializeItems(ContextMenu menu) {
		TGContext context = findContext();
		TGCaret caret = TGSongViewController.getInstance(context).getCaret();
		TGDuration duration = caret.getDuration();
		boolean running = MidiPlayer.getInstance(context).isRunning();
		
		this.initializeItem(menu, R.id.menu_duration_whole, this.createActionProcessor(TGSetWholeDurationAction.NAME), !running, duration.getValue() == TGDuration.WHOLE);
		this.initializeItem(menu, R.id.menu_duration_half, this.createActionProcessor(TGSetHalfDurationAction.NAME), !running, duration.getValue() == TGDuration.HALF);
		this.initializeItem(menu, R.id.menu_duration_quarter, this.createActionProcessor(TGSetQuarterDurationAction.NAME), !running, duration.getValue() == TGDuration.QUARTER);
		this.initializeItem(menu, R.id.menu_duration_eighth, this.createActionProcessor(TGSetEighthDurationAction.NAME), !running, duration.getValue() == TGDuration.EIGHTH);
		this.initializeItem(menu, R.id.menu_duration_sixteenth, this.createActionProcessor(TGSetSixteenthDurationAction.NAME), !running, duration.getValue() == TGDuration.SIXTEENTH);
		this.initializeItem(menu, R.id.menu_duration_thirtysecond, this.createActionProcessor(TGSetThirtySecondDurationAction.NAME), !running, duration.getValue() == TGDuration.THIRTY_SECOND);
		this.initializeItem(menu, R.id.menu_duration_sixtyfourth, this.createActionProcessor(TGSetSixtyFourthDurationAction.NAME), !running, duration.getValue() == TGDuration.SIXTY_FOURTH);
		this.initializeItem(menu, R.id.menu_duration_dotted, this.createActionProcessor(TGChangeDottedDurationAction.NAME), !running, duration.isDotted());
		this.initializeItem(menu, R.id.menu_duration_doubledotted, this.createActionProcessor(TGChangeDoubleDottedDurationAction.NAME), !running, duration.isDoubleDotted());
		
		this.initializeDivisionItem(menu, R.id.menu_duration_division_type_1, TGDivisionType.NORMAL, duration, running);
		this.initializeDivisionItem(menu, R.id.menu_duration_division_type_3, TGDivisionType.ALTERED_DIVISION_TYPES[0], duration, running);
		this.initializeDivisionItem(menu, R.id.menu_duration_division_type_5, TGDivisionType.ALTERED_DIVISION_TYPES[1], duration, running);
		this.initializeDivisionItem(menu, R.id.menu_duration_division_type_6, TGDivisionType.ALTERED_DIVISION_TYPES[2], duration, running);
		this.initializeDivisionItem(menu, R.id.menu_duration_division_type_7, TGDivisionType.ALTERED_DIVISION_TYPES[3], duration, running);
		this.initializeDivisionItem(menu, R.id.menu_duration_division_type_9, TGDivisionType.ALTERED_DIVISION_TYPES[4], duration, running);
		this.initializeDivisionItem(menu, R.id.menu_duration_division_type_10, TGDivisionType.ALTERED_DIVISION_TYPES[5], duration, running);
		this.initializeDivisionItem(menu, R.id.menu_duration_division_type_11, TGDivisionType.ALTERED_DIVISION_TYPES[6], duration, running);
		this.initializeDivisionItem(menu, R.id.menu_duration_division_type_12, TGDivisionType.ALTERED_DIVISION_TYPES[7], duration, running);
		this.initializeDivisionItem(menu, R.id.menu_duration_division_type_13, TGDivisionType.ALTERED_DIVISION_TYPES[8], duration, running);
	}
	
	public void initializeDivisionItem(ContextMenu menu, int id, TGDivisionType divisionType, TGDuration duration, boolean running) {
		this.initializeItem(menu, id, this.createDivisionTypeActionProcessor(divisionType), !running, divisionType.isEqual(duration.getDivision()));
	}
	
	public TGActionProcessorListener createDivisionTypeActionProcessor(TGDivisionType divisionType) {
		TGSongManager tgSongManager = TGDocumentManager.getInstance(this.findContext()).getSongManager();
		TGActionProcessorListener tgActionProcessor = this.createActionProcessor(TGSetDivisionTypeDurationAction.NAME);
		tgActionProcessor.setAttribute(TGSetDivisionTypeDurationAction.PROPERTY_DIVISION_TYPE, divisionType.clone(tgSongManager.getFactory()));
		return tgActionProcessor;
	}
}
