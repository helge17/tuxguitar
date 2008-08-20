package org.herac.tuxguitar.carbon.menu;

import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.internal.carbon.HICommand;
import org.eclipse.swt.internal.carbon.OS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.file.ExitAction;
import org.herac.tuxguitar.gui.actions.help.ShowAboutAction;
import org.herac.tuxguitar.gui.actions.settings.EditConfigAction;

public class MacMenu {
	
	private static final int kHICommandPreferences = ('p' << 24) + ('r' << 16) + ('e' << 8) + 'f';
	
	private static final int kHICommandAbout = ('a' << 24) + ('b' << 16) + ('o' << 8) + 'u';
	
	private static final int kHICommandServices = ('s' << 24) + ('e' << 16) + ('r' << 8) + 'v';
	
	private static final int kHIQuitServices = ('q' << 24) + ('u' << 16) + ('i' << 8) + 't';
	
	private static final String ABOUT_NAME = "About TuxGuitar";
	
	private boolean enabled;
	
	public MacMenu(){
		super();
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void hookApplicationMenu(Display display,final Shell shell){
		final Callback commandCallback= new Callback(this,"commandProc", 3);
		int commandProc= commandCallback.getAddress();
		if (commandProc == 0) {
			commandCallback.dispose();
			return; // give up
		}
		
		// Install event handler for commands
		int[] mask= new int[] { OS.kEventClassCommand, OS.kEventProcessCommand};
		OS.InstallEventHandler(OS.GetApplicationEventTarget(), commandProc,mask.length / 2, mask, 0, null);
		
		// create About ... menu command
		int[] outMenu= new int[1];
		short[] outIndex= new short[1];
		if (OS.GetIndMenuItemWithCommandID(0, kHICommandPreferences, 1, outMenu, outIndex) == OS.noErr && outMenu[0] != 0) {
			int menu= outMenu[0];
			
			char buffer[] = ABOUT_NAME.toCharArray();
			int length = ABOUT_NAME.length();
			int str= OS.CFStringCreateWithCharacters(OS.kCFAllocatorDefault, buffer, length);
			OS.InsertMenuItemTextWithCFString(menu, str, (short) 0, 0, kHICommandAbout);
			OS.CFRelease(str);
			
			// add separator between About & Preferences
			OS.InsertMenuItemTextWithCFString(menu, 0, (short) 1, OS.kMenuItemAttrSeparator, 0);
			
			// enable pref menu
			OS.EnableMenuCommand(menu, kHICommandPreferences);
			
			// disable services menu
			OS.DisableMenuCommand(menu, kHICommandServices);
		}
		
		// schedule disposal of callback object
		display.disposeExec(new Runnable() {
			public void run() {
				commandCallback.dispose();
			}
		});
	}
	
	public int commandProc(int nextHandler, int theEvent, int userData) {
		if (OS.GetEventKind(theEvent) == OS.kEventProcessCommand) {
			HICommand command= new HICommand();
			OS.GetEventParameter(theEvent,OS.kEventParamDirectObject, OS.typeHICommand, null,HICommand.sizeof, null, command);
			
			return handleCommand(command.commandID, theEvent);
		}
		return OS.eventNotHandledErr;
	}
	
	public int handleCommand(int command, int theEvent){
		if( this.isEnabled() ){
			switch (command) {
				case kHICommandPreferences:
					return handlePreferencesCommand();
				case kHICommandAbout:
					return handleAboutCommand();
				case kHIQuitServices:
					return handleQuitCommand();
				default:
					System.out.println(OS.GetEventKind(theEvent));
				break;
			}
			return OS.eventNotHandledErr;
		}
		return OS.noErr;
	}
	
	public int handleQuitCommand(){
		TuxGuitar.instance().getAction(ExitAction.NAME).process(null);
		return OS.noErr;
	}
	
	public int handleAboutCommand(){
		TuxGuitar.instance().getAction(ShowAboutAction.NAME).process(null);
		return OS.noErr;
	}
	
	public int handlePreferencesCommand(){
		TuxGuitar.instance().getAction(EditConfigAction.NAME).process(null);
		return OS.noErr;
	}
}
