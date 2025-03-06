package org.herac.tuxguitar.app.action.impl.edit;

import java.util.List;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.impl.caret.TGGoRightAction;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.graphics.control.TGTrackImpl;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.util.TGBeatRange;
import org.herac.tuxguitar.util.TGContext;

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
