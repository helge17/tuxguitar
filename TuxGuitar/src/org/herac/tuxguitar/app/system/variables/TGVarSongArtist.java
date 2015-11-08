package org.herac.tuxguitar.app.system.variables;

import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.util.TGContext;

public class TGVarSongArtist {
	
	public static final String NAME = "songartist";
	
	private TGContext context;
	
	public TGVarSongArtist(TGContext context) {
		this.context = context;
	}
	
	public String toString() {
		return TGDocumentManager.getInstance(this.context).getSong().getArtist();
	}
}
