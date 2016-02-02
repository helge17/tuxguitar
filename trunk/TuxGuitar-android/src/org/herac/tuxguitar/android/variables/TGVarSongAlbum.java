package org.herac.tuxguitar.android.variables;

import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.util.TGContext;

public class TGVarSongAlbum {
	
	public static final String NAME = "songalbum";
	
	private TGContext context;
	
	public TGVarSongAlbum(TGContext context) {
		this.context = context;
	}
	
	public String toString() {
		return TGDocumentManager.getInstance(this.context).getSong().getAlbum();
	}
}
