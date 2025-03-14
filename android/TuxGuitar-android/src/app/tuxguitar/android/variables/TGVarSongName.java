package app.tuxguitar.android.variables;

import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.util.TGContext;

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
