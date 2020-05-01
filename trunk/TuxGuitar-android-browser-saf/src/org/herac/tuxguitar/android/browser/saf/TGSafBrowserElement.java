package org.herac.tuxguitar.android.browser.saf;

import org.herac.tuxguitar.android.browser.model.TGBrowserElement;

import androidx.documentfile.provider.DocumentFile;

public class TGSafBrowserElement implements TGBrowserElement{
	
	private DocumentFile file;
	private TGSafBrowserElement parent;
	
	public TGSafBrowserElement(DocumentFile file, TGSafBrowserElement parent) {
		this.file = file;
		this.parent = parent;
	}
	
	public TGSafBrowserElement getParent() {
		return this.parent;
	}
	
	public DocumentFile getFile() {
		return this.file;
	}
	
	public String getName(){
		return getFile().getName();
	}
	
	public boolean isFolder(){
		return getFile().isDirectory();
	}
	
	public boolean isWritable(){
		return (getFile() != null && getFile().exists() ? getFile().canWrite() : getParent() != null && getParent().isWritable());
	}
}
