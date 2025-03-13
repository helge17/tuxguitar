package app.tuxguitar.editor.action.composition;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGChangeTripletFeelAction extends TGActionBase {

	public static final String NAME = "action.composition.change-triplet-feel";

	public static final String ATTRIBUTE_TRIPLET_FEEL = "tripletFeel";
	public static final String ATTRIBUTE_APPLY_TO_END = "applyToEnd";

	public TGChangeTripletFeelAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		int tripletFeel = ((Integer) context.getAttribute(ATTRIBUTE_TRIPLET_FEEL)).intValue();
		boolean applyToEnd = ((Boolean) context.getAttribute(ATTRIBUTE_APPLY_TO_END)).booleanValue();

		TGSongManager songManager = getSongManager(context);
		TGSong song = ((TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
		TGMeasureHeader header = ((TGMeasureHeader) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER));

		songManager.changeTripletFeel(song, header, tripletFeel, applyToEnd);
	}
}
