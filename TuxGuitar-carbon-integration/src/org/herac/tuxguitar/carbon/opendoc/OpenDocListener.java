package org.herac.tuxguitar.carbon.opendoc;

import org.eclipse.swt.internal.C;
import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.internal.carbon.AEDesc;
import org.eclipse.swt.internal.carbon.CFRange;
import org.eclipse.swt.internal.carbon.EventRecord;
import org.eclipse.swt.internal.carbon.OS;
/**
 * <p>
 * This registers a handler for opendoc events in OSX</o<
 *
 * $Id:$
 *
 * @author robin
 */
public class OpenDocListener implements OpenDocCallback{
	
	private static final int typeAEList = ('l' << 24) + ('i' << 16) + ('s' << 8) + 't';
	
	private static final int kCoreEventClass = ('a' << 24) + ('e' << 16) + ('v' << 8) + 't';
	
	private static final int kAEOpenDocuments = ('o' << 24) + ('d' << 16) + ('o' << 8) + 'c';
	
	private static final int kURLEventClass = ('G' << 24) + ('U' << 16) + ('R' << 8) + 'L';
	
	private static final int typeText = ('T' << 24) + ('E' << 16) + ('X' << 8) + 'T';
	
	private boolean enabled;
	
	public OpenDocListener() {
		super();
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void init() {
		Callback openDocCallback = new Callback(this, "openDocProc", 3);
		int openDocProc = openDocCallback.getAddress();
		if (openDocProc == 0) {
			openDocCallback.dispose();
			return;
		}
		if (OS.AEInstallEventHandler(kCoreEventClass, kAEOpenDocuments,openDocProc, 0, false) != OS.noErr) {
			return;
		}
		if (OS.AEInstallEventHandler(kURLEventClass, kURLEventClass,openDocProc, 0, false) != OS.noErr) {
			return;
		}
		int appTarget = OS.GetApplicationEventTarget();
		Callback appleEventCallback = new Callback(this, "appleEventProc", 3);
		int appleEventProc = appleEventCallback.getAddress();
		int[] mask3 = new int[] { OS.kEventClassAppleEvent, OS.kEventAppleEvent, kURLEventClass, };
		OS.InstallEventHandler(appTarget, appleEventProc,mask3.length / 2, mask3, 0, null);
	}
	
	public int appleEventProc(int nextHandler, int theEvent, int userData) {
		if(!this.isEnabled()){
			return OS.noErr;
		}
		int eventClass = OS.GetEventClass(theEvent);
		if (eventClass == OS.kEventClassAppleEvent) {
			int[] aeEventID = new int[1];
			if (OS.GetEventParameter(theEvent, OS.kEventParamAEEventID, OS.typeType, null, 4, null, aeEventID) != OS.noErr) {
				return OS.eventNotHandledErr;
			}
			
			if (aeEventID[0] != kAEOpenDocuments && aeEventID[0] != kURLEventClass) {
				return OS.eventNotHandledErr;
			}
			
			EventRecord eventRecord = new EventRecord();
			OS.ConvertEventRefToEventRecord(theEvent, eventRecord);
			OS.AEProcessAppleEvent(eventRecord);
			
			return OS.noErr;
		}
		
		return OS.eventNotHandledErr;
	}
	
	public int openDocProc(int theAppleEvent, int reply, int handlerRefcon) {
		if(!this.isEnabled()){
			return OS.noErr;
		}
		AEDesc aeDesc = new AEDesc();
		EventRecord eventRecord = new EventRecord();
		OS.ConvertEventRefToEventRecord(theAppleEvent, eventRecord);
		try {
			int result = OpenDocJNI.AEGetParamDesc(theAppleEvent,OS.kEventParamDirectObject, typeAEList, aeDesc);
			if (result != OS.noErr) {
				System.err.println("OSX: Could call AEGetParamDesc. Error: " + result);
				return OS.noErr;
			}
		} catch (java.lang.UnsatisfiedLinkError e) {
			System.err.println("OSX: AEGetParamDesc not available.  Can't open sent file");
			return OS.noErr;
		}
		int[] count = new int[1];
		OS.AECountItems(aeDesc, count);
		
		if (count[0] > 0) {
			String[] fileNames = new String[count[0]];
			int maximumSize = 80; // size of FSRef
			int dataPtr = OS.NewPtr(maximumSize);
			int[] aeKeyword = new int[1];
			int[] typeCode = new int[1];
			int[] actualSize = new int[1];
			for (int i = 0; i < count[0]; i++) {
				if (OS.AEGetNthPtr(aeDesc, i + 1, OS.typeFSRef, aeKeyword,typeCode, dataPtr, maximumSize, actualSize) == OS.noErr) {
					byte[] fsRef = new byte[actualSize[0]];
					C.memmove(fsRef, dataPtr, actualSize[0]);
					int dirUrl = OS.CFURLCreateFromFSRef(OS.kCFAllocatorDefault, fsRef);
					int dirString = OS.CFURLCopyFileSystemPath(dirUrl,OS.kCFURLPOSIXPathStyle);
					OS.CFRelease(dirUrl);
					int length = OS.CFStringGetLength(dirString);
					char[] buffer = new char[length];
					CFRange range = new CFRange();
					range.length = length;
					OS.CFStringGetCharacters(dirString, range, buffer);
					OS.CFRelease(dirString);
					fileNames[i] = new String(buffer);
				}
				
				if (OS.AEGetNthPtr(aeDesc, i + 1, typeText, aeKeyword,typeCode, dataPtr, maximumSize, actualSize) == OS.noErr) {
					byte[] urlRef = new byte[actualSize[0]];
					C.memmove(urlRef, dataPtr, actualSize[0]);
					fileNames[i] = new String(urlRef);
				}
			}
			
			if( fileNames.length > 0 ){
				OpenDocAction.saveAndOpen( fileNames[0] );
			}
		}
		
		return OS.noErr;
	}
}