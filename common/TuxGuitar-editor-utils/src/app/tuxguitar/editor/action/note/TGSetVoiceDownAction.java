package app.tuxguitar.editor.action.note;

import java.util.Iterator;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.graphics.control.TGBeatGroup;
import app.tuxguitar.graphics.control.TGVoiceImpl;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.util.TGContext;

public class TGSetVoiceDownAction extends TGActionBase {

	public static final String NAME = "action.beat.general.voice-down";

	public TGSetVoiceDownAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGVoiceImpl voice = (TGVoiceImpl) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE);
		if( voice != null ){
			TGBeatGroup group = voice.getBeatGroup();
			if(!voice.isEmpty() && !voice.isRestVoice() && group != null ){
				Iterator<?> it = group.getVoices().iterator();
				while( it.hasNext() ){
					TGVoice current = (TGVoice)it.next();
					getSongManager(context).getMeasureManager().changeVoiceDirection(current, TGVoice.DIRECTION_DOWN);
				}
			}
		}
	}
}
