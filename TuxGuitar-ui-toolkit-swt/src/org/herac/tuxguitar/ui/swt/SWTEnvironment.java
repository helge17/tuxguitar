package org.herac.tuxguitar.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.ui.swt.widget.SWTCustomKnob;
import org.herac.tuxguitar.ui.swt.widget.SWTCustomScale;
import org.herac.tuxguitar.ui.swt.widget.SWTDropDownSelect;
import org.herac.tuxguitar.ui.swt.widget.SWTDropDownSelectLight;
import org.herac.tuxguitar.ui.swt.widget.SWTScale;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UIKnob;
import org.herac.tuxguitar.ui.widget.UIScale;

public class SWTEnvironment {
	
	private static final String PLATFORM_GTK = "gtk";
	private static final String PLATFORM_WIN32 = "win32";
	private static final String PLATFORM_COCOA = "cocoa";
	
	private static final String SWT_GTK3 = "SWT_GTK3";
	private static final String SWT_GTK3_FALSE = "0";
	
	private static SWTEnvironment instance;
	
	private Display display;
	
	private String defaultFontName;
	private String knobAlternative;
	private String dropDownSelectAlternative;
	private String verticalScaleAlternative;
	private String horizontalScaleAlternative;
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
			this.toolItemResizeAvailable = (PLATFORM_WIN32.equals(SWT.getPlatform()) || PLATFORM_COCOA.equals(SWT.getPlatform()));
		}
		return this.toolItemResizeAvailable;
	}
	
	public int getToolItemWidth(ToolItem control) {
		int style = control.getStyle();
		if((style & SWT.SEPARATOR) == 0 && PLATFORM_COCOA.equals(SWT.getPlatform())) {
			return ((style & SWT.HORIZONTAL) != 0 ? control.getBounds().width : control.getBounds().height);
		}
		return control.getWidth();
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
	
	public String getVerticalScaleAlternative() {
		if( this.verticalScaleAlternative == null ) {
			this.verticalScaleAlternative = this.getEnvValue(UIScale.class.getName() + ".vertical");
		}
		
		if( this.verticalScaleAlternative == null ) {
			this.verticalScaleAlternative = this.getEnvValue(UIScale.class.getName());
		}
		
		if( this.verticalScaleAlternative == null ) {
			if( PLATFORM_COCOA.equals(SWT.getPlatform())) {
				this.verticalScaleAlternative = SWTCustomScale.class.getName();
			}
		}
		
		// Default value
		if( this.verticalScaleAlternative == null ) {
			this.verticalScaleAlternative = SWTScale.class.getName();
		}
		
		return this.verticalScaleAlternative;
	}
	
	public String getHorizontalScaleAlternative() {
		if( this.horizontalScaleAlternative == null ) {
			this.horizontalScaleAlternative = this.getEnvValue(UIScale.class.getName() + ".horizontal");
		}
		
		if( this.horizontalScaleAlternative == null ) {
			this.horizontalScaleAlternative = this.getEnvValue(UIScale.class.getName());
		}
		
		// Default value
		if( this.horizontalScaleAlternative == null ) {
			this.horizontalScaleAlternative = SWTScale.class.getName();
		}
		
		return this.horizontalScaleAlternative;
	}
	
	public String getKnobAlternative() {
		if( this.knobAlternative == null ) {
			this.knobAlternative = this.getEnvValue(UIKnob.class.getName());
		}
		
		// Default value
		if( this.knobAlternative == null ) {
			this.knobAlternative = SWTCustomKnob.class.getName();
		}
		
		return this.knobAlternative;
	}
	
	public String getEnvValue(String property) {
		String value = System.getProperty(property);
		if( value == null ) {
			value = System.getenv(property);
		}
		return value;
	}
}
