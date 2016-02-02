package org.herac.tuxguitar.android.variables;

import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.util.TGContext;

public class TGVarSongName {
	
	public static final String NAME = "songname";
	
	private TGContext context;
	
	public TGVarSongName(TGContext context) {
		this.context = context;
	}
	
	public String toString() {
		return TGDocumentManager.getInstance(this.context).getSong().getName();
	}
}
