package org.herac.tuxguitar.editor.action.composition;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

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
