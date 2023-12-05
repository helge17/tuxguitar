package org.herac.tuxguitar.cocoa.toolbar;

import org.eclipse.swt.internal.C;
import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.internal.cocoa.NSButton;
import org.eclipse.swt.internal.cocoa.NSString;
import org.eclipse.swt.internal.cocoa.NSToolbar;
import org.eclipse.swt.internal.cocoa.NSWindow;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.cocoa.TGCocoa;

public class MacToolbar {
	
	private static final byte[] SWT_OBJECT = {'S', 'W', 'T', '_', 'O', 'B', 'J', 'E', 'C', 'T', '\0'};
	
	private static final long NSWindowToolbarButton = 3;
	
	private static final long sel_toolbarButtonClicked_ = TGCocoa.sel_registerName("toolbarButtonClicked:");
	
	private boolean enabled;
	
	private long delegateRef;
	
	private MacToolbarDelegate delegate;
	
	public MacToolbar(){
		super();
	}
	
	public void init( Shell shell ) throws Throwable{
		Callback callback = TGCocoa.newCallback( this , "callbackProc64", "callbackProc32", 3 );
		long callbackProc = TGCocoa.getCallbackAddress( callback );
		
		if( callbackProc != 0 ){
			String classname = ("MacToolbarDelegate");
			if( TGCocoa.objc_lookUpClass ( classname ) == 0 ) {
				long cls = TGCocoa.objc_allocateClassPair( classname , 0 ) ;
				TGCocoa.class_addIvar(cls, SWT_OBJECT, C.PTR_SIZEOF , (byte)(C.PTR_SIZEOF == 4 ? 2 : 3), new byte[]{'*','\0'} );
				TGCocoa.class_addMethod(cls, sel_toolbarButtonClicked_, callbackProc , "@:@");
				TGCocoa.objc_registerClassPair(cls);
			}
			
			this.delegate = TGCocoa.newMacToolbarDelegate();
			this.delegate.alloc().init();
			this.delegateRef = TGCocoa.NewGlobalRef( MacToolbar.this );
			
			TGCocoa.object_setInstanceVariable( MacToolbarDelegate.class.getField("id").get( delegate ) , SWT_OBJECT , this.delegateRef );
			
			NSToolbar dummyBar = new NSToolbar();
			dummyBar.alloc();
			dummyBar.initWithIdentifier(NSString.stringWith("SWTToolbar")); //$NON-NLS-1$
			dummyBar.setVisible(false);
			
			NSWindow nsWindow = shell.view.window();
			nsWindow.setToolbar(dummyBar);
			dummyBar.release();
			nsWindow.setShowsToolbarButton(true);
			
			NSButton toolbarButton = TGCocoa.getStandardWindowButton(nsWindow, NSWindowToolbarButton);
			if (toolbarButton != null) {
				toolbarButton.setTarget( delegate );
				TGCocoa.setControlAction( toolbarButton , sel_toolbarButtonClicked_ );
			}
		}
	}
	
	public void finalize() throws Throwable{
		if( this.delegateRef != 0 ){
			TGCocoa.DeleteGlobalRef( this.delegateRef );
			this.delegateRef = 0;
		}
	}
	
	public long callbackProc( long id, long sel, long arg0 ) {
		if ( this.isEnabled() ){
			if ( sel == sel_toolbarButtonClicked_ ) {
				return handleToogleToolbarCommand();
			}
		}
		return TGCocoa.noErr;
	}
	
	public long callbackProc64( long id, long sel, long arg0 ) {
		return this.callbackProc(id, sel, arg0);
	}
	
	public int callbackProc32( int id, int sel, int arg0 ) {
		return (int)this.callbackProc( (long)id, (long)sel, (long)arg0);
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public long handleToogleToolbarCommand(){
		MacToolbarAction.toogleToolbar();
		return TGCocoa.noErr;
	}
}
