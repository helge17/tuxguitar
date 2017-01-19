package org.herac.tuxguitar.editor.action.file;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGSongPersistenceHelper;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.util.TGContext;

public abstract class TGSongPersistenceActionBase extends TGActionBase {

	public static final String ATTRIBUTE_CONTEXT = TGSongStreamContext.class.getName();
	public static final String ATTRIBUTE_FORMAT = TGFileFormat.class.getName();
	public static final String ATTRIBUTE_FORMAT_CODE = TGSongPersistenceHelper.ATTRIBUTE_FORMAT_CODE;
	public static final String ATTRIBUTE_MIME_TYPE = TGSongPersistenceHelper.ATTRIBUTE_MIME_TYPE;
	
	public TGSongPersistenceActionBase(TGContext context, String name) {
		super(context, name);
	}
	
	protected TGSongStreamContext findSongStreamContext(TGActionContext actionContext) {
		TGSongStreamContext streamContext = actionContext.getAttribute(ATTRIBUTE_CONTEXT);
		if( streamContext != null ) {
			return streamContext;
		}
		
		streamContext = new TGSongStreamContext();
		streamContext.addContext(actionContext);
		actionContext.setAttribute(ATTRIBUTE_CONTEXT, streamContext);
		
		return this.findSongStreamContext(actionContext);
	}
}
