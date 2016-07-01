package org.herac.tuxguitar.ui.swt.resource;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIFontModel;
import org.herac.tuxguitar.ui.swt.SWTComponent;
import org.herac.tuxguitar.ui.swt.SWTEnvironment;

public class SWTFont extends SWTComponent<Font> implements UIFont {
	
	public SWTFont(Font handle){
		super(handle);
	}
	
	public SWTFont(Device device, String name, float height, boolean bold, boolean italic){
		this(new Font(device, SWTFont.checkName(device, name), Math.round(height), (SWT.NORMAL | (bold ? SWT.BOLD : 0) | (italic ? SWT.ITALIC : 0))));
	}
	
	public SWTFont(Device device, UIFontModel fm) {
		this(device, fm.getName(), fm.getHeight(), fm.isBold(), fm.isItalic());
	}
	
	public void dispose(){
		this.getControl().dispose();
	}
	
	public boolean isDisposed(){
		return this.getControl().isDisposed();
	}
	
	public Font getHandle(){
		return this.getControl();
	}
	
	public String getName() {
		FontData[] fd = this.getControl().getFontData();
		return ( fd != null && fd.length > 0 ? fd[0].getName() : new String() );
	}
	
	public float getHeight() {
		FontData[] fd = this.getControl().getFontData();
		return ( fd != null && fd.length > 0 ? fd[0].getHeight() : 0 );
	}
	
	public boolean isBold() {
		FontData[] fd = this.getControl().getFontData();
		return ( fd != null && fd.length > 0 ? ((fd[0].getStyle() & SWT.BOLD) != 0) : false );
	}
	
	public boolean isItalic() {
		FontData[] fd = this.getControl().getFontData();
		return ( fd != null && fd.length > 0 ? ((fd[0].getStyle() & SWT.ITALIC) != 0) : false );
	}
	
	public static String checkName(Device device, String name) {
		if( name != null && name.length() > 0 && !UIFontModel.DEFAULT_NAME.equals(name)) {
			return name;
		}
		return SWTEnvironment.getInstance().getDefaultFontName();
	}
}
