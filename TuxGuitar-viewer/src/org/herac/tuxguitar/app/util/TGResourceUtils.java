package org.herac.tuxguitar.app.util;

import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class TGResourceUtils {
	
	public static Icon loadIcon(String resource){
		try {
			if( resource != null ){
				InputStream stream = TGResourceUtils.class.getClassLoader().getResourceAsStream("skin/" + resource );
				if( stream != null ){
					return new ImageIcon(ImageIO.read(stream));
				}
			}
		} catch( Throwable throwable ){
			throwable.printStackTrace();
		}
		return null;
	}
}
