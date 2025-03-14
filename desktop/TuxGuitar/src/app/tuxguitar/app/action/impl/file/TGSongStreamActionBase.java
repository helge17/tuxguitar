package app.tuxguitar.app.action.impl.file;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.io.base.TGSongStreamContext;
import app.tuxguitar.io.base.TGSongStreamProvider;
import app.tuxguitar.util.TGContext;

public abstract class TGSongStreamActionBase extends TGActionBase {

	public static final String ATTRIBUTE_CONTEXT = TGSongStreamContext.class.getName();
	public static final String ATTRIBUTE_PROVIDER = TGSongStreamProvider.class.getName();

	public TGSongStreamActionBase(TGContext context, String name) {
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
