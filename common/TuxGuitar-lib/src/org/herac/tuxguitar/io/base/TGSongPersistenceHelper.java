package org.herac.tuxguitar.io.base;

import org.herac.tuxguitar.util.TGContext;

public class TGSongPersistenceHelper {
	
	public static final String ATTRIBUTE_MIME_TYPE = "mimeType";
	public static final String ATTRIBUTE_FORMAT_CODE = "formatCode";
	
	private TGContext context;
	
	public TGSongPersistenceHelper(TGContext context){
		this.context = context;
	}

	public TGContext getContext() {
		return context;
	}
}
