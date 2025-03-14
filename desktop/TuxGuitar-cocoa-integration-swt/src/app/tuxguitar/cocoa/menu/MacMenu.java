package app.tuxguitar.cocoa.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.internal.cocoa.NSApplication;
import org.eclipse.swt.internal.cocoa.NSMenu;
import org.eclipse.swt.internal.cocoa.NSMenuItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import app.tuxguitar.app.action.impl.file.TGExitAction;
import app.tuxguitar.app.action.impl.help.TGOpenAboutDialogAction;
import app.tuxguitar.app.action.impl.settings.TGOpenSettingsEditorAction;
import app.tuxguitar.cocoa.TGCocoa;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.util.TGContext;

public class MacMenu {

	private static final long kAboutMenuItem = 0;
	private static final long kPreferencesMenuItem = 2;

	private static long sel_preferencesMenuItemSelected_ = TGCocoa.sel_registerName("preferencesMenuItemSelected:");
	private static long sel_aboutMenuItemSelected_ = TGCocoa.sel_registerName("aboutMenuItemSelected:");

	private boolean enabled;

	private TGContext context;

	public MacMenu(TGContext context) {
		this.context = context;
	}

	public void init() throws Throwable{
		long cls = TGCocoa.objc_lookUpClass ("SWTApplicationDelegate");
		if( cls != 0 ){
			Callback callback = TGCocoa.newCallback( this , "callbackProc64", "callbackProc32", 3 );
			long callbackProc = TGCocoa.getCallbackAddress( callback );
			if( callbackProc != 0 ){
				TGCocoa.class_addMethod(cls, sel_preferencesMenuItemSelected_, callbackProc , "@:@");
				TGCocoa.class_addMethod(cls, sel_aboutMenuItemSelected_, callbackProc , "@:@");
			}
		}
		NSApplication app = NSApplication.sharedApplication();
		NSMenu mainMenu = app.mainMenu();
		if( TGCocoa.getMenuNumberOfItems( mainMenu ) > 0 ){
			NSMenuItem appMenuItem = TGCocoa.getMenuItemAtIndex( mainMenu , 0 );
			NSMenu appMenu = appMenuItem.submenu();

			long itemCount = TGCocoa.getMenuNumberOfItems( appMenu );
			if( itemCount > kPreferencesMenuItem ) {
				NSMenuItem menuItem = TGCocoa.getMenuItemAtIndex( appMenu , kPreferencesMenuItem );
				menuItem.setEnabled( true );
				TGCocoa.setControlAction(menuItem, sel_preferencesMenuItemSelected_);
			}
			if( itemCount > kAboutMenuItem ) {
				NSMenuItem menuItem = TGCocoa.getMenuItemAtIndex( appMenu , kAboutMenuItem );
				menuItem.setEnabled( true );
				TGCocoa.setControlAction(menuItem, sel_aboutMenuItemSelected_);
			}
		}

		Display.getCurrent().addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				handleQuitCommand();
				event.doit = false;
			}
		});
	}

	public long callbackProc( long id, long sel, long arg0 ) {
		if ( this.isEnabled() ){
			if ( sel == sel_preferencesMenuItemSelected_ ) {
				return handlePreferencesCommand();
			}else if ( sel == sel_aboutMenuItemSelected_ ) {
				return handleAboutCommand();
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

	public long handleQuitCommand(){
		this.executeAction(TGExitAction.NAME);
		return TGCocoa.noErr;
	}

	public long handleAboutCommand(){
		this.executeAction(TGOpenAboutDialogAction.NAME);
		return TGCocoa.noErr;
	}

	public long handlePreferencesCommand(){
		this.executeAction(TGOpenSettingsEditorAction.NAME);
		return TGCocoa.noErr;
	}

	private void executeAction(final String actionId){
		new TGActionProcessor(this.context, actionId).process();
	}
}
