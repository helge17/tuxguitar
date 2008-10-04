package org.herac.tuxguitar.gui.tools.browser.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.herac.tuxguitar.gui.tools.browser.TGBrowserException;
import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserElement;

public class TGBrowserElementImpl extends TGBrowserElement{
	
	private TGBrowserElementImpl parent;
	private File file;
	
	public TGBrowserElementImpl(TGBrowserElementImpl parent,File file) {
		super(file.getName());
		this.parent = parent;
		this.file = file;
	}
	
	public File getFile() {
		return this.file;
	}
	
	public TGBrowserElementImpl getParent() {
		return this.parent;
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
