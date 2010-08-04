package org.herac.tuxguitar.app.util;

import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class TGResourceUtils {
	
	public static Icon loadIcon(String resource){
		try{
			if( resource != null ){
				URL url = TGResourceUtils.class.getClassLoader().getResource("skin/" + resource );
				if( url != null ){
					return new ImageIcon( url );
				}
			}
		}catch( Throwable throwable ){
			throwable.printStackTrace();
		}
		return null;
	}
	
}
