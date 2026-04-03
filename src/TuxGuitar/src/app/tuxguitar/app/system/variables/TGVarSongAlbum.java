package app.tuxguitar.app.system.variables;

import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.util.TGContext;

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
