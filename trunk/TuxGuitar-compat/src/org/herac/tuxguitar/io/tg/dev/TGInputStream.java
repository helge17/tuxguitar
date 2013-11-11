/*
 * Created on 16-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.io.tg.dev;

import org.herac.tuxguitar.util.TGVersion;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TGInputStream extends org.herac.tuxguitar.io.tg.TGInputStream {
	
	private static final String[] DEVELOPMENT_VERSIONS = new String[] {
		(TG_FORMAT_NAME + " - " + new TGVersion(1,2,20111001).getVersion() )
	};
	
	public TGInputStream() {
		super();
	}
	
	public boolean isSupportedVersion(String version){
		for(int i = 0 ; i < DEVELOPMENT_VERSIONS.length ; i ++){
			if( version.equals(DEVELOPMENT_VERSIONS[i]) ){
				System.out.println("This file was saved with an old develpment version of tuxguitar, please save it again with current format.");
				return true;
			}
		}
		return false;
	}
}
