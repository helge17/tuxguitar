package org.herac.tuxguitar.app.action.impl.file;

import java.io.InputStream;
import java.net.URL;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.util.TGFileUtils;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.editor.action.file.TGReadSongAction;
import org.herac.tuxguitar.io.base.TGFileFormatUtils;
import org.herac.tuxguitar.util.TGContext;

public class TGReadURLAction extends TGActionBase {
	
	public static final String NAME = "action.url.read";
	
	public static final String ATTRIBUTE_URL = URL.class.getName();
	
	public TGReadURLAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		try {
			URL url = context.getAttribute(ATTRIBUTE_URL);
			InputStream stream = (TGFileUtils.isLocalFile(url) ? url.openStream() : TGFileFormatUtils.getInputStream(url.openStream()));
			context.setAttribute(TGReadSongAction.ATTRIBUTE_FORMAT_CODE, TGFileFormatUtils.getFileFormatCode(url.getFile()));
			context.setAttribute(TGReadSongAction.ATTRIBUTE_INPUT_STREAM, stream);
			
			TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
			tgActionManager.execute(TGReadSongAction.NAME, context);
		} catch (Throwable e) {
			throw new TGActionException(e.getMessage(), e);
		}
	}
}
