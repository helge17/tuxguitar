package org.herac.tuxguitar.community.utils;

import java.net.MalformedURLException;
import java.net.URL;

import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.community.TGCommunitySingleton;
import org.herac.tuxguitar.util.TGContext;

public class TGCommunityWeb {
	
	public static String getHomeUrl(TGContext context){
		return TGCommunitySingleton.getInstance(context).getConfig().getStringValue("community.url");
	}
	
	public static void open(TGContext context, String suffix){
		try {
			TGCommunityWeb.open(context, new URL(getHomeUrl(context) + "/" + suffix) );
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public static void open(TGContext context, URL url){
		TGApplication.getInstance(context).getApplication().openUrl(url);
	}
}
