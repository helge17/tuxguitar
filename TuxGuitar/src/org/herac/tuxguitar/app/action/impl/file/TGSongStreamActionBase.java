package org.herac.tuxguitar.app.action.impl.file;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.io.base.TGSongStreamProvider;
import org.herac.tuxguitar.util.TGContext;

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
