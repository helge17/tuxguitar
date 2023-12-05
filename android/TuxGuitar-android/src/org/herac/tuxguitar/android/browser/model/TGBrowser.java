package org.herac.tuxguitar.android.browser.model;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface TGBrowser {
	
	void open(TGBrowserCallBack<Object> cb);
	
	void close(TGBrowserCallBack<Object> cb);
	
	void cdRoot(TGBrowserCallBack<Object> cb);
	
	void cdUp(TGBrowserCallBack<Object> cb);
	
	void cdElement(TGBrowserCallBack<Object> cb, TGBrowserElement element);
	
	void listElements(TGBrowserCallBack<List<TGBrowserElement>> cb);
	
	void createElement(TGBrowserCallBack<TGBrowserElement> cb, String name);
	
	void getInputStream(TGBrowserCallBack<InputStream> cb, TGBrowserElement element);
	
	void getOutputStream(TGBrowserCallBack<OutputStream> cb, TGBrowserElement element);
	
	boolean isWritable();
}
