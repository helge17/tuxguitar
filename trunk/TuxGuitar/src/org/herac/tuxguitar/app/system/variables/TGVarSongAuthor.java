package org.herac.tuxguitar.app.system.variables;

import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.util.TGContext;

public class TGVarSongAuthor {
	
	public static final String NAME = "songauthor";
	
	private TGContext context;
	
	public TGVarSongAuthor(TGContext context) {
		this.context = context;
	}
	
	public String toString() {
		return TGDocumentManager.getInstance(this.context).getSong().getAuthor();
	}
}
