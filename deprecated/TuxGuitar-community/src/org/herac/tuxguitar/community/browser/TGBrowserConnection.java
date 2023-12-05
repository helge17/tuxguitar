package org.herac.tuxguitar.community.browser;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserCallBack;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserElement;
import org.herac.tuxguitar.app.view.dialog.browser.main.TGBrowserDialog;
import org.herac.tuxguitar.community.TGCommunitySingleton;
import org.herac.tuxguitar.community.auth.TGCommunityAuth;
import org.herac.tuxguitar.community.auth.TGCommunityAuthDialog;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGBrowserConnection {
	
	private static final String HTTP_STATUS_OK = "200";
	private static final String HTTP_STATUS_UNAUTHORIZED = "401";
	
	private TGContext context;
	private TGCommunityAuth auth;
	
	public TGBrowserConnection(TGContext context){
		this.context = context;
		this.auth = TGCommunitySingleton.getInstance(this.context).getAuth();
		this.auth.update();
	}
	
	public void fillElements(final TGBrowserCallBack<List<TGBrowserElement>> cb, final TGBrowserElementImpl element) {
		try {
			TGBrowserRequest request = new TGBrowserRequest(this.context, this.auth, element);
			TGBrowserResponse response = request.getResponse();
			
			String status = response.getStatus();
			if( status != null && status.equals(HTTP_STATUS_OK) ){
				List<TGBrowserElement> elements = new ArrayList<TGBrowserElement>();
				response.loadElements(elements);
				cb.onSuccess(elements);
			}else if( status != null && status.equals(HTTP_STATUS_UNAUTHORIZED) ){
				TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
					public void run() throws TGException {
						if(!TuxGuitar.getInstance().getBrowser().isDisposed()){
							new TGCommunityAuthDialog(TGBrowserConnection.this.context, new Runnable() {
								public void run() {
									TGBrowserConnection.this.auth.update();
									TGBrowserConnection.this.fillElements(cb, element);
								}
							}, new Runnable() {
								public void run() {
									cb.onSuccess(null);
								}
							}).open(TGBrowserDialog.getInstance(TGBrowserConnection.this.context).getWindow());
						}
					}
				});
			} else {
				cb.onSuccess(null);
			}
		} catch(Throwable throwable) {
			cb.handleError(throwable);
		}
	}
}
