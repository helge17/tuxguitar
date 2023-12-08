package org.herac.tuxguitar.app.system.variables;

import org.herac.tuxguitar.app.document.TGDocumentFileManager;
import org.herac.tuxguitar.app.util.TGFileChooser;
import org.herac.tuxguitar.util.TGContext;

public class TGVarFileName {
	
	public static final String NAME = "filename";
	
	private TGContext context;
	
	public TGVarFileName(TGContext context) {
		this.context = context;
	}
	
	public String toString() {
		return TGDocumentFileManager.getInstance(this.context).getCurrentFileName(TGFileChooser.DEFAULT_SAVE_FILENAME);
	}
}
