package org.herac.tuxguitar.android.storage;

import org.herac.tuxguitar.util.TGAbstractContext;

public interface TGStorageProvider {

	void openDocument();

	void saveDocument();

	void saveDocumentAs();

	void updateSession(TGAbstractContext source);
}
