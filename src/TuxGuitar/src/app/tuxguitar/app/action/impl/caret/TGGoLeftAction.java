package app.tuxguitar.app.action.impl.caret;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.transport.TGTransport;
import app.tuxguitar.app.view.component.tab.Tablature;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.config.TGConfigKeys;
import app.tuxguitar.app.system.config.TGConfigManager;
import app.tuxguitar.app.view.component.tab.Caret;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.song.models.TGBeat;

public class TGGoLeftAction extends TGActionBase{

	public static final String NAME = "action.caret.go-left";

	public TGGoLeftAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		if( MidiPlayer.getInstance(getContext()).isRunning() ){
			TGTransport.getInstance(getContext()).gotoPrevious();
		}
		else{
			Tablature tablature = TablatureEditor.getInstance(getContext()).getTablature();
			tablature.getCaret().moveLeft();

			if (!Boolean.TRUE.equals(context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_KEEP_SELECTION))) {
				tablature.getSelector().clearSelection();
			}

			boolean playWhenMoving = TGConfigManager.getInstance(getContext()).getBooleanValue(TGConfigKeys.PLAY_WHEN_MOVING);
			if (playWhenMoving) { // current beat is played when caret is moved
				Caret caret = TablatureEditor.getInstance(getContext()).getTablature().getCaret();
				TGBeat beat = (TGBeat) caret.getSelectedBeat();
				if (beat != null) {
					TuxGuitar tuxguitar = TuxGuitar.getInstance();
					tuxguitar.playBeat(beat);
				}
			}
		}
	}
}
