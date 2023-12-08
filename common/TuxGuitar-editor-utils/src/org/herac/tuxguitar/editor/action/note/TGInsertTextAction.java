package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGText;
import org.herac.tuxguitar.util.TGContext;

public class TGInsertTextAction extends TGActionBase {
	
	public static final String NAME = "action.beat.general.insert-text";
	
	public static final String ATTRIBUTE_TEXT_VALUE = "textValue";
	
	public TGInsertTextAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGBeat beat = ((TGBeat) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT));
		String textValue = ((String) context.getAttribute(ATTRIBUTE_TEXT_VALUE));
		
		if( beat != null && textValue != null ){
			TGSongManager tgSongManager = getSongManager(context);
			TGText tgText = getSongManager(context).getFactory().newText();
			tgText.setValue(textValue);
			
			tgSongManager.getMeasureManager().addText(beat, tgText);
		}
	}
}
