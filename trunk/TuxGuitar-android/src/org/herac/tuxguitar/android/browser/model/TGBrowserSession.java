package org.herac.tuxguitar.android.browser.model;

import org.herac.tuxguitar.android.browser.TGBrowserCollection;
import org.herac.tuxguitar.io.base.TGFileFormat;

import java.util.List;


public class TGBrowserSession {
	
	public static final int READ_MODE = 1;
	public static final int WRITE_MODE = 2;
	
	private int sessionType;
	private TGBrowser browser;
	private TGBrowserCollection collection;
	private List<TGBrowserElement> currentElements;
	private TGBrowserElement currentElement;
	private TGFileFormat currentFormat;
	
	public TGBrowserSession(){
		this.sessionType = READ_MODE;
	}

	public int getSessionType() {
		return sessionType;
	}

	public void setSessionType(int sessionType) {
		this.sessionType = sessionType;
	}

	public TGBrowser getBrowser() {
		return browser;
	}
	
	public void setBrowser(TGBrowser browser) {
		this.browser = browser;
	}

	public TGBrowserCollection getCollection() {
		return collection;
	}

	public void setCollection(TGBrowserCollection collection) {
		this.collection = collection;
	}

	public List<TGBrowserElement> getCurrentElements() {
		return currentElements;
	}

	public void setCurrentElements(List<TGBrowserElement> currentElements) {
		this.currentElements = currentElements;
	}

	public TGBrowserElement getCurrentElement() {
		return currentElement;
	}

	public void setCurrentElement(TGBrowserElement currentElement) {
		this.currentElement = currentElement;
	}

	public TGFileFormat getCurrentFormat() {
		return currentFormat;
	}

	public void setCurrentFormat(TGFileFormat currentFormat) {
		this.currentFormat = currentFormat;
	}
}
