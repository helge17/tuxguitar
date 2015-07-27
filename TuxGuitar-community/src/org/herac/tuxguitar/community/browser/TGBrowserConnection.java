package org.herac.tuxguitar.community.browser;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserElement;
import org.herac.tuxguitar.app.tools.browser.base.handler.TGBrowserListElementsHandler;
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
	
	public void fillElements(final TGBrowserElementImpl element, final TGBrowserListElementsHandler handler) {
		try {
			TGBrowserRequest request = new TGBrowserRequest(this.context, this.auth, element);
			TGBrowserResponse response = request.getResponse();
			
			String status = response.getStatus();
			if( status != null && status.equals(HTTP_STATUS_OK) ){
				List<TGBrowserElement> elements = new ArrayList<TGBrowserElement>();
				response.loadElements(elements);
				handler.onSuccess(elements);
			}else if( status != null && status.equals(HTTP_STATUS_UNAUTHORIZED) ){
				TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
					public void run() throws TGException {
						if(!TuxGuitar.getInstance().getBrowser().isDisposed()){
							TGCommunityAuthDialog authDialog = new TGCommunityAuthDialog(TGBrowserConnection.this.context);
							authDialog.open( TuxGuitar.getInstance().getBrowser().getShell() );
							if( authDialog.isAccepted() ){
								TGBrowserConnection.this.auth.update();
								TGBrowserConnection.this.fillElements(element, handler);
							} else {
								handler.onSuccess(null);
							}
						}
					}
				});
			} else {
				handler.onSuccess(null);
			}
		} catch(Throwable throwable) {
			handler.handleError(throwable);
		}
	}
}
