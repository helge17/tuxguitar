package org.herac.tuxguitar.android.browser.gdrive;

import org.herac.tuxguitar.android.browser.model.TGBrowserElement;

import com.google.api.services.drive.model.File;

public class TGDriveBrowserFile implements TGBrowserElement{
	
	public static final String FILE_MIME_TYPE = "audio/x-tuxguitar";
	public static final String FOLDER_MIME_TYPE = "application/vnd.google-apps.folder";
	
	private File file;
	private TGDriveBrowserFile parent;
	
	public TGDriveBrowserFile(File file, TGDriveBrowserFile parent) {
		this.file = file;
		this.parent = parent;
	}
	
	public TGDriveBrowserFile getParent() {
		return this.parent;
	}
	
	public File getFile() {
		return file;
	}

	public String getName() {
		return this.file.getTitle();
	}
	
	public boolean isFolder() {
		return (FOLDER_MIME_TYPE.equals(this.file.getMimeType()));
	}
	
	public boolean isWritable() {
		return true;
	}
}
