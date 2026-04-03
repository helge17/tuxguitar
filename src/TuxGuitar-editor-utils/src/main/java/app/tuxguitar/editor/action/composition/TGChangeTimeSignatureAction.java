package app.tuxguitar.editor.action.composition;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTimeSignature;
import app.tuxguitar.util.TGContext;

public class TGChangeTimeSignatureAction extends TGActionBase {

	public static final String NAME = "action.composition.change-time-signature";

	public static final String ATTRIBUTE_APPLY_TO_END = "applyToEnd";

	public TGChangeTimeSignatureAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGSongManager songManager = getSongManager(context);
		TGSong song = ((TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		TGMeasureHeader header = ((TGMeasureHeader) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER));
		TGTimeSignature timeSignature = ((TGTimeSignature) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TIME_SIGNATURE));
		boolean applyToEnd = ((Boolean) context.getAttribute(ATTRIBUTE_APPLY_TO_END)).booleanValue();

		songManager.changeTimeSignature(song, header, timeSignature, applyToEnd);
	}
}
