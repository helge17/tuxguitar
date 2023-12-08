package org.herac.tuxguitar.editor.action.duration;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.util.TGContext;

public class TGSetSixtyFourthDurationAction extends TGActionBase {
	
	public static final String NAME = "action.note.duration.set-sixty-fourth";
	
	public static final int VALUE = TGDuration.SIXTY_FOURTH;
	
	public TGSetSixtyFourthDurationAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context) {
		TGVoice voice = ((TGVoice) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE));
		TGDuration duration = ((TGDuration) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_DURATION));
		
		if( duration.getValue() != VALUE || (!voice.isEmpty() && voice.getDuration().getValue() != VALUE) ){
			duration.setValue(VALUE);
			duration.setDotted(false);
			duration.setDoubleDotted(false);
			
			TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
			tgActionManager.execute(TGSetDurationAction.NAME, context);
		}
	}
}
