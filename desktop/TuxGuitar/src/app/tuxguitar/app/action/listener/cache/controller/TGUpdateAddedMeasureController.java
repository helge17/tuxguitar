package app.tuxguitar.app.action.listener.cache.controller;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.view.component.tab.Caret;
import app.tuxguitar.app.view.component.tab.Tablature;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.measure.TGAddMeasureAction;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGUpdateAddedMeasureController extends TGUpdateItemsController {

	public static final String ATTRIBUTE_EXTEND_SELECTION = "extend-selection";

	public TGUpdateAddedMeasureController() {
		super();
	}

	@Override
	public void update(final TGContext context, final TGActionContext actionContext) {
		final TGSongManager tgSongManager = (TGSongManager) actionContext.getAttribute(TGSongManager.class.getName());
		final TGSong tgSong = (TGSong) actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final Integer number = (Integer) actionContext.getAttribute(TGAddMeasureAction.ATTRIBUTE_MEASURE_NUMBER);

		// Update the created measure
		this.findUpdateBuffer(context, actionContext).requestUpdateMeasure(number);

		// Update caret position to new measure
		this.findUpdateBuffer(context, actionContext).doPostUpdate(new Runnable() {
			public void run() {
				long start = tgSongManager.getMeasureHeader(tgSong, number.intValue()).getStart();
				Tablature tablature = TuxGuitar.getInstance().getTablatureEditor().getTablature();
				Caret caret = tablature.getCaret();
				caret.update(caret.getTrack().getNumber(), start, caret.getStringNumber());
				if (actionContext.hasAttribute(ATTRIBUTE_EXTEND_SELECTION) && (boolean) actionContext.getAttribute(ATTRIBUTE_EXTEND_SELECTION)) {
					tablature.getSelector().updateSelection(caret.getSelectedBeat());
				}
			}
		});

		// Call super update.
		super.update(context, actionContext);
	}
}
