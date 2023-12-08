package org.herac.tuxguitar.editor.action.file;

import java.io.InputStream;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGSongReaderHandle;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.util.TGContext;

public class TGReadSongAction extends TGSongPersistenceActionBase {
	
	public static final String NAME = "action.song.read";
	
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
			
			context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, tgSongLoaderHandle.getSong());
			context.setAttribute(ATTRIBUTE_FORMAT, tgSongLoaderHandle.getFormat());
			
			TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
			tgActionManager.execute(TGLoadSongAction.NAME, context);
		} catch(TGFileFormatException e){
			throw new TGActionException(e);
		}
	}
}
