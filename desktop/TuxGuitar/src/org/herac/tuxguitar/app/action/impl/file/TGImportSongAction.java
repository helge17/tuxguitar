package org.herac.tuxguitar.app.action.impl.file;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.file.TGLoadSongAction;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongStream;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.io.base.TGSongStreamProvider;
import org.herac.tuxguitar.util.TGContext;

public class TGImportSongAction extends TGSongStreamActionBase {
	
	public static final String NAME = "action.song.import";
	
	public TGImportSongAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		try {
			TGSongStreamProvider streamProvider = context.getAttribute(ATTRIBUTE_PROVIDER);
			TGSongStreamContext streamContext = this.findSongStreamContext(context);
			TGSongStream stream = streamProvider.openStream(streamContext);
			
			stream.process();
			
			context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, streamContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
			
			TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
			tgActionManager.execute(TGLoadSongAction.NAME, context);
		} catch (TGFileFormatException e) {
			throw new TGActionException(e);
		}
	}
}
