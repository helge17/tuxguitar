package app.tuxguitar.editor.action.composition;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

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
