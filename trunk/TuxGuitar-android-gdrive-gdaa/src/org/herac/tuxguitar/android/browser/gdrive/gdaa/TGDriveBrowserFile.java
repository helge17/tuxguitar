package org.herac.tuxguitar.android.browser.gdrive.gdaa;

import org.herac.tuxguitar.android.browser.model.TGBrowserElement;

import com.google.android.gms.drive.DriveFile;

public class TGDriveBrowserFile implements TGBrowserElement{
	
	private TGDriveBrowserFolder parent;
	private DriveFile file;
	private String name;
	
	public TGDriveBrowserFile(TGDriveBrowserFolder parent, DriveFile file, String name) {
		this.parent = parent;
		this.name = name;
		this.file = file;
	}
	
	public TGDriveBrowserFolder getParent() {
		return this.parent;
	}
	
	public DriveFile getFile() {
		return file;
	}

	public String getName() {
		return this.name;
	}
	
	public boolean isFolder() {
		return false;
	}
	
	public boolean isWritable() {
		return true;
	}
}
