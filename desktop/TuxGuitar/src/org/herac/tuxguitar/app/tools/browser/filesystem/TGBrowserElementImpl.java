package org.herac.tuxguitar.app.tools.browser.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.herac.tuxguitar.app.tools.browser.TGBrowserException;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserElement;

public class TGBrowserElementImpl implements TGBrowserElement{
	
	private TGBrowserElementImpl parent;
	private File file;
	
	public TGBrowserElementImpl(TGBrowserElementImpl parent,File file) {
		this.parent = parent;
		this.file = file;
	}
	
	public File getFile() {
		return this.file;
	}
	
	public TGBrowserElementImpl getParent() {
		return this.parent;
	}
	
	public String getName(){
		return getFile().getName();
	}
	
	public boolean isFolder(){
		return getFile().isDirectory();
	}
	
	public InputStream getInputStream() throws TGBrowserException {
		if(!isFolder()){
			try {
				return new FileInputStream(getFile());
			} catch (FileNotFoundException e) {
				throw new TGBrowserException(e);
			}
		}
		return null;
	}
	
}
