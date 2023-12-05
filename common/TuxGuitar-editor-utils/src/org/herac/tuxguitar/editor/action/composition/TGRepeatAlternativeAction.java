package org.herac.tuxguitar.editor.action.composition;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGRepeatAlternativeAction extends TGActionBase {
	
	public static final String NAME = "action.insert.close-alternative";
	
	public static final String ATTRIBUTE_REPEAT_ALTERNATIVE = "repeatAlternative";
	
	public TGRepeatAlternativeAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGSongManager songManager = getSongManager(context);
		TGSong song = ((TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		TGMeasureHeader measureHeader = ((TGMeasureHeader) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER));
		int repeatAlternative = ((Integer) context.getAttribute(ATTRIBUTE_REPEAT_ALTERNATIVE)).intValue();
		
		//Solo si hubieron cambios
		if( repeatAlternative != measureHeader.getRepeatAlternative() ){
			//Si no estoy editando, y la alternativa no contiene el primer final,
			//por defecto se cierra la repeticion del compas anterior
			boolean previousRepeatClose = (measureHeader.getRepeatAlternative() == 0 && ((repeatAlternative & (1 << 0)) == 0)) ;
			
			//Guardo la repeticion alternativa
			songManager.changeAlternativeRepeat(song, measureHeader.getStart(), repeatAlternative);
			
			if(previousRepeatClose){
				//Agrego un cierre de repeticion al compaz anterior
				TGMeasureHeader previousHeader = songManager.getMeasureHeader(song, measureHeader.getNumber() - 1);
				if( previousHeader != null && previousHeader.getRepeatClose() == 0 ){
					context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, previousHeader);
					context.setAttribute(TGRepeatCloseAction.ATTRIBUTE_REPEAT_COUNT, 1);
					
					TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
					tgActionManager.execute(TGRepeatCloseAction.NAME, context);
					
					context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, measureHeader);
				}
			}
		}
	}
}
