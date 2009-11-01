package org.herac.tuxguitar.community.utils;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import org.herac.tuxguitar.community.TGCommunitySingleton;
import org.herac.tuxguitar.gui.system.config.TGConfigManager;

public class TGCommunityWeb {
	
	public static String HOME_URL = TGCommunitySingleton.getInstance().getConfig().getStringConfigValue("community.url");
	
	public static void open( String suffix ){
		try {
			open( new URL(HOME_URL + "/" + suffix) );
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean open( URL url ){
		if( openDesktopBrowser( url ) ){
			return true;
		}
		if( openCommandLineBrowser( url ) ){
			return true;
		}
		return false;
	}
	
	private static boolean openDesktopBrowser( URL url ){
		try {
			Class desktopClass = Class.forName("java.awt.Desktop");
			if( desktopClass != null ){
				Method desktop_getDesktop = desktopClass.getDeclaredMethod("getDesktop", new Class[]{} );
				Method desktop_browse = desktopClass.getDeclaredMethod("browse", new Class[]{ java.net.URI.class });
				if( desktop_getDesktop != null && desktop_browse != null ){
					Object desktopObject = desktop_getDesktop.invoke( null , new Object[]{} );
					if( desktopObject != null ){
						desktop_browse.invoke( desktopObject , new Object[] { new java.net.URI( url.toExternalForm() ) } );
					}
				}
			}
			return true;
		} catch ( Throwable throwable ) {
			throwable.printStackTrace();
		}
		return false;
	}
	
	private static boolean openCommandLineBrowser( URL url ){
		TGConfigManager config = TGCommunitySingleton.getInstance().getConfig();
		
		String[] browserCmds = config.getStringConfigValue("community.browser","").split(";");
		for( int i = 0 ; i < browserCmds.length ; i ++ ){
			try {
				String browserCmd = browserCmds[i];
				
				if( browserCmd != null && browserCmd.length() > 0 ){
					String pattern = ("%s");
					int indexOfPattern = browserCmd.indexOf( pattern );
					if( indexOfPattern >= 0 ){
						String commandLine = new String();
						commandLine += browserCmd.substring(0, indexOfPattern );
						commandLine += url.toExternalForm();
						if( browserCmd.length() > (indexOfPattern + pattern.length() ) ){
							commandLine += ( browserCmd.substring( (indexOfPattern + pattern.length() ) , browserCmd.length() ) );
						}
						Runtime.getRuntime().exec( commandLine );
						return true;
					}
				}
			} catch ( Throwable throwable ) {
				// nothing to do.
			}
		}
		return false;
	}
}
