package org.herac.tuxguitar.editor.action.effect;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloPicking;
import org.herac.tuxguitar.util.TGContext;

public class TGChangeTremoloPickingAction extends TGActionBase {
	
	public static final String NAME = "action.note.effect.change-tremolo-picking";

	public static final String ATTRIBUTE_EFFECT = TGEffectTremoloPicking.class.getName();
	
	public TGChangeTremoloPickingAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGMeasure measure = ((TGMeasure) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE));
		TGBeat beat = ((TGBeat) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT));
		TGString string = ((TGString) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING));
		TGEffectTremoloPicking effect = ((TGEffectTremoloPicking) context.getAttribute(ATTRIBUTE_EFFECT));
		
		getSongManager(context).getMeasureManager().changeTremoloPicking(measure, beat.getStart(), string.getNumber(), effect);
	}
}
