package org.herac.tuxguitar.gui.tools.browser.filesystem;

import java.util.Comparator;

import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserElement;

public class TGBrowserElementComparator implements Comparator {
	
	private static final int RESULT_LESS = -1;
	
	private static final int RESULT_EQUAL = 0;
	
	private static final int RESULT_GREATER = 1;
	
	private static final int DIRECTION = 1;
	
	private static final int DIRECTION_FOLDER = 1;
	
	public int compare(Object o1, Object o2) {
		if(o1 instanceof TGBrowserElement && o2 instanceof TGBrowserElement){
			TGBrowserElement element1 = (TGBrowserElement)o1;
			TGBrowserElement element2 = (TGBrowserElement)o2;
			
			if(element1.isFolder() && !element2.isFolder()){
				return (DIRECTION_FOLDER * RESULT_LESS);
			}
			if(element2.isFolder() && !element1.isFolder()){
				return (DIRECTION_FOLDER * RESULT_GREATER);
			}
			
			return (DIRECTION * (element1.getName().compareTo(element2.getName())));
		}
		return RESULT_EQUAL;
	}
	
}
