package app.tuxguitar.android.storage.saf;

import android.net.Uri;

import app.tuxguitar.io.base.TGFileFormat;

public class TGSafSession {

	private Uri uri;
	private TGFileFormat fileFormat;

	public TGSafSession() {
		super();
	}

	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}

	public TGFileFormat getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(TGFileFormat fileFormat) {
		this.fileFormat = fileFormat;
	}
}
