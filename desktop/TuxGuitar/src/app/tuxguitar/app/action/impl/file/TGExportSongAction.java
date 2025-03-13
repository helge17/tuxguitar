package app.tuxguitar.app.action.impl.file;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.io.base.TGSongStream;
import app.tuxguitar.io.base.TGSongStreamContext;
import app.tuxguitar.io.base.TGSongStreamProvider;
import app.tuxguitar.util.TGContext;

public class TGExportSongAction extends TGSongStreamActionBase {

	public static final String NAME = "action.song.export";

	public TGExportSongAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		try {
			TGSongStreamProvider streamProvider = context.getAttribute(ATTRIBUTE_PROVIDER);
			TGSongStreamContext streamContext = this.findSongStreamContext(context);
			TGSongStream stream = streamProvider.openStream(streamContext);

			stream.process();
		} catch (TGFileFormatException e) {
			throw new TGActionException(e);
		}
	}
}
