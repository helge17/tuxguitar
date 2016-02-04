package org.herac.tuxguitar.android.browser.assets;

import java.util.Comparator;

import org.herac.tuxguitar.android.browser.model.TGBrowserElement;

public class TGAssetBrowserElementComparator implements Comparator<TGBrowserElement> {
	
	private static final int RESULT_LESS = -1;
	
	private static final int RESULT_GREATER = 1;
	
	private static final int DIRECTION = 1;
	
	private static final int DIRECTION_FOLDER = 1;
	
	public int compare(TGBrowserElement element1, TGBrowserElement element2) {
		if( element1.isFolder() && !element2.isFolder() ){
			return (DIRECTION_FOLDER * RESULT_LESS);
		}
		if( element2.isFolder() && !element1.isFolder() ){
			return (DIRECTION_FOLDER * RESULT_GREATER);
		}
		
		return (DIRECTION * (element1.getName().compareTo(element2.getName())));
	}
}
