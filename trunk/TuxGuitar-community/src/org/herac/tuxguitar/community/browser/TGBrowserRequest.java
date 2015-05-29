package org.herac.tuxguitar.community.browser;

import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import org.herac.tuxguitar.community.auth.TGCommunityAuth;
import org.herac.tuxguitar.community.utils.TGCommunityWeb;
import org.herac.tuxguitar.util.TGContext;

public class TGBrowserRequest {
	
	private TGContext context;
	private String request;
	
	public TGBrowserRequest(TGContext context, TGCommunityAuth auth, TGBrowserElementImpl element) throws Throwable {
		this.context = context;
		this.initialize(auth, element);
	}
	
	public TGBrowserRequest(TGContext context, TGCommunityAuth auth) throws Throwable {
		this(context, auth , null);
	}
	
	private void initialize( TGCommunityAuth auth, TGBrowserElementImpl element ) throws Throwable {
		this.request = new String();
		this.request += URLEncoder.encode( "auth" , "UTF-8" );
		this.request += ("=");
		this.request += URLEncoder.encode( auth.getAuthCode() , "UTF-8" );
		
		if( element != null ){
			Iterator<Map.Entry<String, String>> it = element.getProperties();
			while( it.hasNext() ){
				Map.Entry<String, String> property = it.next();
				this.request += ("&");
				this.request += URLEncoder.encode( (String)property.getKey() , "UTF-8" );
				this.request += ("=");
				this.request += URLEncoder.encode( (String)property.getValue() , "UTF-8" );
			}
		}
	}
	
	public TGBrowserResponse getResponse() throws Throwable {
		URL url = new URL((TGCommunityWeb.getHomeUrl(this.context) + "/rd.php/sharing/tuxguitar/browser.do"));
		URLConnection conn = url.openConnection();
		conn.setDoOutput(true);
		
		OutputStreamWriter outputStream = new OutputStreamWriter(conn.getOutputStream());
		outputStream.write(this.request);
		outputStream.flush();
		outputStream.close();
		
		return new TGBrowserResponse( conn.getInputStream() ) ;
	}
}
