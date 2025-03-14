package app.tuxguitar.app.system.variables;

import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.util.TGContext;

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
