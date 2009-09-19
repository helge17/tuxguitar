package org.herac.tuxguitar.cocoa.opendoc;

import org.eclipse.swt.internal.Callback;
import org.herac.tuxguitar.cocoa.TGCocoa;

public class OpenDocListener {
	
	public static final long sel_application_openFile_ = TGCocoa.sel_registerName("application:openFile:");
	
	private boolean enabled;
	
	public OpenDocListener(){
		this.enabled = false;
	}
	
	public void init() throws Throwable{
		long cls = TGCocoa.objc_lookUpClass ("SWTApplicationDelegate");
		if( cls != 0 ){
			Callback callback = new Callback( this , "callbackProc", 4 );
			
			long callbackProc = callback.getAddress();
			if( callbackProc != 0 ){
				TGCocoa.class_addMethod(cls, sel_application_openFile_, callbackProc , "B:@@");
			}
		}
	}
	
	public int callbackProc(long id, long sel,long arg0, long arg1) {
		if( this.isEnabled() ){
			if (sel == sel_application_openFile_) {
				try {
					String filename = TGCocoa.getNSStringValue(arg1);
					if( filename.length() > 0 ){
						OpenDocAction.saveAndOpen( filename );
					}
				}catch (Throwable throwable){
					throwable.printStackTrace();
				}
			}
		}
		return 0;
	}
	
	public int callbackProc(int id, int sel,int arg0, int arg1) {
		return callbackProc( (long)id, (long)sel, (long)arg0, (long)arg1);
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
