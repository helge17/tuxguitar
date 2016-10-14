package org.herac.tuxguitar.android.browser.gdrive.gdaa;

import java.io.InputStream;

import org.herac.tuxguitar.android.browser.model.TGBrowserElement;
import org.herac.tuxguitar.android.browser.model.TGBrowserException;

import com.google.android.gms.drive.DriveFolder;

public class TGDriveBrowserFolder implements TGBrowserElement{
	
	private TGDriveBrowserFolder parent;
	private DriveFolder folder;
	private String name;
	
	public TGDriveBrowserFolder(TGDriveBrowserFolder parent, DriveFolder folder, String name) {
		this.parent = parent;
		this.name = name;
		this.folder = folder;
	}
	
	public TGDriveBrowserFolder getParent() {
		return this.parent;
	}
	
	public DriveFolder getFolder() {
		return folder;
	}

	public String getName() {
		return this.name;
	}
	
	public boolean isFolder() {
		return true;
	}
	
	public boolean isWritable() {
		return true;
	}
	
	public InputStream getInputStream() throws TGBrowserException {
		return null;
	}
}
