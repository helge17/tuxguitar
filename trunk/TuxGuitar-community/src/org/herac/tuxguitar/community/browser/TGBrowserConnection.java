package org.herac.tuxguitar.community.browser;

import java.util.List;

import org.herac.tuxguitar.community.TGCommunitySingleton;
import org.herac.tuxguitar.community.auth.TGCommunityAuth;
import org.herac.tuxguitar.community.auth.TGCommunityAuthDialog;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.tools.browser.TGBrowserException;

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
				if( !TuxGuitar.instance().getBrowser().isDisposed() ){
					authDialog.open( TuxGuitar.instance().getBrowser().getShell() );
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
