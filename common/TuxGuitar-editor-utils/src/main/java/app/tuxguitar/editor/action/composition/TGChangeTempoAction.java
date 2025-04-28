package app.tuxguitar.editor.action.composition;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGTempo;
import app.tuxguitar.util.TGContext;

public class TGChangeTempoAction extends TGActionBase {

	public static final String NAME = "action.composition.change-tempo";

	public TGChangeTempoAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGSongManager tgSongManager = getSongManager(context);
		TGMeasureHeader tgHeader = (TGMeasureHeader) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		TGTempo tgTempo = (TGTempo) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TEMPO);

		tgSongManager.changeTempo(tgHeader, tgTempo);
	}
}
