package org.herac.tuxguitar.app.action.impl.edit;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.impl.measure.TGOpenMeasureCopyDialogAction;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.editor.clipboard.TGClipboard;
import org.herac.tuxguitar.song.helpers.TGStoredBeatList;
import org.herac.tuxguitar.util.TGBeatRange;
import org.herac.tuxguitar.util.TGContext;

public class TGCopyAction extends TGActionBase {
	
	public static final String NAME = "action.edit.copy";
	
	public TGCopyAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext tgActionContext){
	    TGBeatRange beats = tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);
		if (beats.isEmpty()) {
			TGActionManager.getInstance(this.getContext()).execute(TGOpenMeasureCopyDialogAction.NAME, tgActionContext);
		} else {
			TGClipboard.getInstance(this.getContext()).setData(new TGStoredBeatList(beats.getBeats(), getSongManager(tgActionContext).getFactory()));
		}
	}
}
