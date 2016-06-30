package org.herac.tuxguitar.ui.swt;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

public class SWTEnvironment {
	
	private static SWTEnvironment instance;
	
	private String defaultFontName;
	
	private SWTEnvironment() {
		super();
	}
	
	public static SWTEnvironment getInstance() {
		synchronized (SWTEnvironment.class) {
			if( instance == null ) {
				instance = new SWTEnvironment();
			}
			return instance;
		}
	}
	
	public void start(Display display) {
		this.defaultFontName = this.findDefaultFontName(display);
	}
	
	public String findDefaultFontName(Display display) {
		Font systemFont = display.getSystemFont();
		if( systemFont != null ) {
			FontData[] fd = systemFont.getFontData();
			if( fd != null && fd.length > 0 ) {
				return fd[0].getName();
			}
		}
		return "";
	}

	public String getDefaultFontName() {
		return defaultFontName;
	}
}
