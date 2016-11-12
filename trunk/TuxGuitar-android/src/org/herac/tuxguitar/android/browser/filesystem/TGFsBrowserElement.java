package org.herac.tuxguitar.android.browser.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.herac.tuxguitar.android.browser.model.TGBrowserElement;
import org.herac.tuxguitar.android.browser.model.TGBrowserException;

public class TGFsBrowserElement implements TGBrowserElement{
	
	private File file;
	private TGFsBrowserElement parent;
	
	public TGFsBrowserElement(File file, TGFsBrowserElement parent) {
		this.file = file;
		this.parent = parent;
	}
	
	public TGFsBrowserElement getParent() {
		return this.parent;
	}
	
	public File getFile() {
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
	
	public InputStream getInputStream() throws TGBrowserException {
		if(!isFolder()){
			try {
				return new FileInputStream(getFile());
			} catch (FileNotFoundException e) {
				throw new TGBrowserException(e.getMessage(), e);
			}
		}
		return null;
	}
	
	public OutputStream getOutputStream() throws TGBrowserException {
		if(!isFolder()){
			try {
				return new FileOutputStream(getFile());
			} catch (FileNotFoundException e) {
				throw new TGBrowserException(e.getMessage(), e);
			}
		}
		return null;
	}
}
