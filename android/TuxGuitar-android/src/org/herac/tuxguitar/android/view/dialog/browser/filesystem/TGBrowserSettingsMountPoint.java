package org.herac.tuxguitar.android.view.dialog.browser.filesystem;

import java.io.File;

public class TGBrowserSettingsMountPoint {

	private File path;
	private String label;

	public TGBrowserSettingsMountPoint(File path, String label) {
		this.path = path;
		this.label = label;
	}

	public File getPath() {
		return this.path;
	}

	public String getLabel() {
		return this.label;
	}
}
