package org.herac.tuxguitar.android.view.dialog.browser.filesystem;

import java.io.File;

public class TGBrowserSettingsFolderAdapterItem {

	private String label;
	private File file;
	
	public TGBrowserSettingsFolderAdapterItem(String label, File file) {
		this.label = label;
		this.file = file;
	}

	public String getLabel() {
		return this.label;
	}

	public File getFile() {
		return this.file;
	}
}
