package app.tuxguitar.android.storage;

import app.tuxguitar.util.TGAbstractContext;

public interface TGStorageProvider {

	void openDocument();

	void saveDocument();

	void saveDocumentAs();

	void updateSession(TGAbstractContext source);
}
