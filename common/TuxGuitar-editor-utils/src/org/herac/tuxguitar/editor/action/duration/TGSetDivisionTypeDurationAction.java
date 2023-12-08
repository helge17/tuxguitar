package org.herac.tuxguitar.editor.action.duration;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGDivisionType;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.util.TGContext;

public class TGSetDivisionTypeDurationAction extends TGActionBase {
	
	public static final String NAME = "action.note.duration.set-division-type";
	
	public static final String PROPERTY_DIVISION_TYPE = TGDivisionType.class.getName();
	
	public TGSetDivisionTypeDurationAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGDivisionType divisionType = (TGDivisionType) context.getAttribute(PROPERTY_DIVISION_TYPE);
		TGDuration duration = (TGDuration) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION);
		
		duration.getDivision().setEnters(divisionType.getEnters());
		duration.getDivision().setTimes(divisionType.getTimes());
		
		TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
		tgActionManager.execute(TGSetDurationAction.NAME, context);
	}
}
