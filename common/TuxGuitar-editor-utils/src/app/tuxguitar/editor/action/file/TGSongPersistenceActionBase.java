package app.tuxguitar.editor.action.file;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGSongPersistenceHelper;
import app.tuxguitar.io.base.TGSongStreamContext;
import app.tuxguitar.util.TGContext;

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
