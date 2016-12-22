package org.herac.tuxguitar.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.herac.tuxguitar.ui.swt.widget.SWTDropDownSelect;
import org.herac.tuxguitar.ui.swt.widget.SWTDropDownSelectLight;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;

public class SWTEnvironment {
	
	private static final String PLATFORM_GTK = "gtk";
	private static final String PLATFORM_WIN32 = "win32";
	
	private static final String SWT_GTK3 = "SWT_GTK3";
	private static final String SWT_GTK3_FALSE = "0";
	
	private static SWTEnvironment instance;
	
	private Display display;
	
	private String defaultFontName;
	private String dropDownSelectAlternative;
	private Boolean toolItemResizeAvailable;
	
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
		this.display = display;
	}
	
	public String getDefaultFontName() {
		if( this.defaultFontName == null ) {
			Font systemFont = this.display.getSystemFont();
			if( systemFont != null ) {
				FontData[] fd = systemFont.getFontData();
				if( fd != null && fd.length > 0 ) {
					this.defaultFontName = fd[0].getName();
				}
			}
			if( this.defaultFontName == null ) {
				this.defaultFontName = "";
			}
		}
		return this.defaultFontName;
	}
	
	public boolean isToolItemResizeAvailable() {
		if( this.toolItemResizeAvailable == null ) {
			this.toolItemResizeAvailable = (PLATFORM_WIN32.equals(SWT.getPlatform()));
		}
		return this.toolItemResizeAvailable;
	}
	
	public String getDropDownSelectAlternative() {
		if( this.dropDownSelectAlternative == null ) {
			this.dropDownSelectAlternative = this.getEnvValue(UIDropDownSelect.class.getName());
		}
		if( this.dropDownSelectAlternative == null ) {
			// ---------------------------------------------------- //
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=487271 //
			// ---------------------------------------------------- //
			if( PLATFORM_GTK.equals(SWT.getPlatform()) && !SWT_GTK3_FALSE.equals(getEnvValue(SWT_GTK3)) ) {
				this.dropDownSelectAlternative = SWTDropDownSelectLight.class.getName();
			}
		}
		
		// Default value
		if( this.dropDownSelectAlternative == null ) {
			this.dropDownSelectAlternative = SWTDropDownSelect.class.getName();
		}
		
		return this.dropDownSelectAlternative;
	}
	
	public String getEnvValue(String property) {
		String value = System.getProperty(property);
		if( value == null ) {
			value = System.getenv(property);
		}
		return value;
	}
}
