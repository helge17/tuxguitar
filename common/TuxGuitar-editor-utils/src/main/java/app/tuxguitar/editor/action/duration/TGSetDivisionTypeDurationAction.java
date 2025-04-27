package app.tuxguitar.editor.action.duration;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGDivisionType;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.util.TGContext;

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
