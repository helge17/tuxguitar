package app.tuxguitar.editor.action.file;

import java.io.InputStream;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.io.base.TGFileFormatManager;
import app.tuxguitar.io.base.TGSongReaderHandle;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.util.TGContext;

public class TGReadSongAction extends TGSongPersistenceActionBase {

	public static final String NAME = "action.song.read";
	public static final String IS_NEWER_FILE_FORMAT = "action.song.read.newerFileFormat";

	public static final String ATTRIBUTE_INPUT_STREAM = InputStream.class.getName();

	public TGReadSongAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(final TGActionContext context) {
		try{
			InputStream stream = context.getAttribute(ATTRIBUTE_INPUT_STREAM);
			TGFileFormat tgFileFormat = context.getAttribute(ATTRIBUTE_FORMAT);
			TGSongManager tgSongManager = getSongManager(context);

			TGSongReaderHandle tgSongLoaderHandle = new TGSongReaderHandle();
			tgSongLoaderHandle.setFactory(tgSongManager.getFactory());
			tgSongLoaderHandle.setFormat(tgFileFormat);
			tgSongLoaderHandle.setInputStream(stream);
			tgSongLoaderHandle.setContext(this.findSongStreamContext(context));
			tgSongLoaderHandle.getContext().setAttribute(ATTRIBUTE_MIME_TYPE, context.getAttribute(ATTRIBUTE_MIME_TYPE));
			tgSongLoaderHandle.getContext().setAttribute(ATTRIBUTE_FORMAT_CODE, context.getAttribute(ATTRIBUTE_FORMAT_CODE));

			TGFileFormatManager.getInstance(getContext()).read(tgSongLoaderHandle);
			context.setAttribute(IS_NEWER_FILE_FORMAT, tgSongLoaderHandle.isNewerFileFormatDetected());
			context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, tgSongLoaderHandle.getSong());
			context.setAttribute(ATTRIBUTE_FORMAT, tgSongLoaderHandle.getFormat());

			TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
			tgActionManager.execute(TGLoadSongAction.NAME, context);
		} catch(TGFileFormatException e){
			throw new TGActionException(e);
		}
	}
}
