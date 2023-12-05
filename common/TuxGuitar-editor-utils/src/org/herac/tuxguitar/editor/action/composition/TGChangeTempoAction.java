package org.herac.tuxguitar.editor.action.composition;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGTempo;
import org.herac.tuxguitar.util.TGContext;

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
