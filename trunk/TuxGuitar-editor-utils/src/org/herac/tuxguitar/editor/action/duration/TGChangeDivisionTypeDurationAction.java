package org.herac.tuxguitar.editor.action.duration;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGDivisionType;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.util.TGContext;

public class TGChangeDivisionTypeDurationAction extends TGActionBase {
	
	public static final String NAME = "action.note.duration.change-division-type";
	
	public TGChangeDivisionTypeDurationAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context) {
		TGDuration duration = ((TGDuration) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION));
		
		TGSongManager songManager = this.getSongManager(context);
		TGDivisionType divisionType = songManager.getFactory().newDivisionType();
		if( duration.getDivision().isEqual(TGDivisionType.NORMAL)){
			divisionType.copyFrom(TGDivisionType.TRIPLET);
		}else{
			divisionType.copyFrom(TGDivisionType.NORMAL);
		}
		
		context.setAttribute(TGSetDivisionTypeDurationAction.PROPERTY_DIVISION_TYPE, divisionType);
		
		TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
		tgActionManager.execute(TGSetDivisionTypeDurationAction.NAME, context);
	}
}
