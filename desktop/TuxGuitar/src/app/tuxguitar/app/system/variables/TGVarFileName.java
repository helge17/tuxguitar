package app.tuxguitar.app.system.variables;

import app.tuxguitar.app.document.TGDocumentFileManager;
import app.tuxguitar.app.util.TGFileChooser;
import app.tuxguitar.util.TGContext;

public class TGVarFileName {

	public static final String NAME = "filename";

	private TGContext context;

	public TGVarFileName(TGContext context) {
		this.context = context;
	}

	public String toString() {
		return TGDocumentFileManager.getInstance(this.context).getCurrentFileName(TGFileChooser.getDefaultSaveFileName());
	}
}
