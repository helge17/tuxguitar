package app.tuxguitar.app.action.impl.edit;

import java.util.List;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.impl.caret.TGGoRightAction;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.graphics.control.TGMeasureImpl;
import app.tuxguitar.graphics.control.TGTrackImpl;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.util.TGBeatRange;
import app.tuxguitar.util.TGContext;

public class TGRepeatAction extends TGActionBase {

	public static final String NAME = "action.edit.repeat";

	public TGRepeatAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext tgActionContext) {
		if (Boolean.TRUE.equals(tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SELECTION_IS_ACTIVE))) {
			// copy
			TGActionManager.getInstance(this.getContext()).execute(TGCopyAction.NAME, tgActionContext);
			// move to last beat of selection
			TGTrackImpl track = ((TGTrackImpl) tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK));
			TGBeatRange beatRange = (TGBeatRange)tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);
			List<TGBeat> beatList = beatRange.getBeats();
			TGBeat lastBeat = beatList.get(beatList.size()-1);
			TGMeasureImpl measure = (TGMeasureImpl) lastBeat.getMeasure();
			TGString string = ((TGString) tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING));
			TablatureEditor.getInstance(getContext()).getTablature().getCaret().moveTo(track, measure, lastBeat, string.getNumber());
			// move right
			TGActionManager.getInstance(this.getContext()).execute(TGGoRightAction.NAME);
			// paste
			TGActionManager.getInstance(this.getContext()).execute(TGPasteAction.NAME);
		}
	}
}
