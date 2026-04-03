package app.tuxguitar.app.action.impl.file;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.file.TGLoadSongAction;
import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.io.base.TGSongStream;
import app.tuxguitar.io.base.TGSongStreamContext;
import app.tuxguitar.io.base.TGSongStreamProvider;
import app.tuxguitar.util.TGContext;

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
