package org.herac.tuxguitar.community.browser;

import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.tools.browser.TGBrowserException;
import org.herac.tuxguitar.community.TGCommunitySingleton;
import org.herac.tuxguitar.community.auth.TGCommunityAuth;
import org.herac.tuxguitar.community.auth.TGCommunityAuthDialog;

public class TGBrowserConnection {
	
	private static final String HTTP_STATUS_OK = "200";
	private static final String HTTP_STATUS_UNAUTHORIZED = "401";
	
	private TGCommunityAuth auth;
	
	public TGBrowserConnection(){
		this.auth = TGCommunitySingleton.getInstance().getAuth();
		this.auth.update();
	}
	
	public void getElements( List elements, TGBrowserElementImpl element ) throws TGBrowserException{
		try {
			TGBrowserRequest request = new TGBrowserRequest(this.auth, element);
			TGBrowserResponse response = request.getResponse();
			
			String status = response.getStatus();
			if( status != null && status.equals(HTTP_STATUS_OK) ){
				response.loadElements( elements );
			}else if( status != null && status.equals(HTTP_STATUS_UNAUTHORIZED) ){
				TGCommunityAuthDialog authDialog = new TGCommunityAuthDialog();
				if( !TuxGuitar.getInstance().getBrowser().isDisposed() ){
					authDialog.open( TuxGuitar.getInstance().getBrowser().getShell() );
					if( authDialog.isAccepted() ){
						this.auth.update();
						this.getElements(elements, element );
					}
				}
			}
		}catch(Throwable throwable){
			throw new TGBrowserException(throwable);
		}
	}
}
