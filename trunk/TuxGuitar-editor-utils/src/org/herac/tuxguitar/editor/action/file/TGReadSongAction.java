package org.herac.tuxguitar.editor.action.file;

import java.io.InputStream;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGSongLoaderHandle;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.util.TGContext;

public class TGReadSongAction extends TGActionBase{
	
	public static final String NAME = "action.song.read";
	
	public static final String ATTRIBUTE_INPUT_STREAM = InputStream.class.getName();
	public static final String ATTRIBUTE_FORMAT = TGFileFormat.class.getName();
	
	public TGReadSongAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(final TGActionContext context) {
		try{
			InputStream stream = (InputStream) context.getAttribute(ATTRIBUTE_INPUT_STREAM);
			TGSongManager tgSongManager = getSongManager(context);
			
			TGSongLoaderHandle tgSongLoaderHandle = new TGSongLoaderHandle();
			tgSongLoaderHandle.setFactory(tgSongManager.getFactory());
			tgSongLoaderHandle.setInputStream(stream);
			
			TGFileFormatManager.getInstance(getContext()).getLoader().load(tgSongLoaderHandle);
			
			context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, tgSongLoaderHandle.getSong());
			context.setAttribute(ATTRIBUTE_FORMAT, tgSongLoaderHandle.getFormat());
			
			TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
			tgActionManager.execute(TGLoadSongAction.NAME, context);
		} catch(TGFileFormatException e){
			throw new TGActionException(e);
		}
	}
}
