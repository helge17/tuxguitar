package org.herac.tuxguitar.app.tools.browser.base;

import java.util.List;

public interface TGBrowser {
	
	void open(TGBrowserCallBack<Object> cb);
	
	void close(TGBrowserCallBack<Object> cb);
	
	void cdRoot(TGBrowserCallBack<Object> cb);
	
	void cdUp(TGBrowserCallBack<Object> cb);
	
	void cdElement(TGBrowserCallBack<Object> cb, TGBrowserElement element);
	
	void listElements(TGBrowserCallBack<List<TGBrowserElement>> cb);
}
