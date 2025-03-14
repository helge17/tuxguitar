package app.tuxguitar.editor.action.duration;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGDivisionType;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.util.TGContext;

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
			divisionType.copyFrom(TGDivisionType.DIVISION_TYPES[1]);
		}else{
			divisionType.copyFrom(TGDivisionType.NORMAL);
		}

		context.setAttribute(TGSetDivisionTypeDurationAction.PROPERTY_DIVISION_TYPE, divisionType);

		TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
		tgActionManager.execute(TGSetDivisionTypeDurationAction.NAME, context);
	}
}
