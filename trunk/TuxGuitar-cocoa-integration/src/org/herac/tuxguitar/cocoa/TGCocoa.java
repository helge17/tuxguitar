package org.herac.tuxguitar.cocoa;

import java.lang.reflect.Method;

import org.eclipse.swt.internal.C;
import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.internal.cocoa.NSButton;
import org.eclipse.swt.internal.cocoa.NSControl;
import org.eclipse.swt.internal.cocoa.NSMenu;
import org.eclipse.swt.internal.cocoa.NSMenuItem;
import org.eclipse.swt.internal.cocoa.NSString;
import org.eclipse.swt.internal.cocoa.NSWindow;
import org.eclipse.swt.internal.cocoa.OS;
import org.herac.tuxguitar.cocoa.toolbar.MacToolbarDelegate;

public class TGCocoa {
	
	public static final int noErr = OS.noErr;
	
	public static final long sel_registerName(String selectorName){
		try {
			return longValue(invokeMethod(OS.class, "sel_registerName", new Object[] { selectorName }));
		}catch (Throwable throwable){
			throwable.printStackTrace();
		}
		return 0;
	}
	
	public static final long objc_lookUpClass(String classname) throws Throwable{
		return longValue(invokeMethod(OS.class, "objc_lookUpClass", new Object[] { classname }));
	}
	
	public static final long objc_allocateClassPair(String name, long extraBytes) throws Throwable{
		return longValue(invokeMethod(OS.class, "objc_allocateClassPair", new Object[] { OS.class.getField("class_NSObject").get(OS.class), name, osType(extraBytes) }));
	}
	
	public static final boolean class_addIvar(long cls, byte[] name, long size, byte alignment, byte[] types) throws Throwable{
		return boolValue(invokeMethod(OS.class, "class_addIvar", new Object[] { osType(cls), name, osType(size), new Byte(alignment), types }));
	}
	
	public static final long object_setInstanceVariable(Object idValue, byte[] name, long value) throws Throwable{
		return longValue(invokeMethod(OS.class, "object_setInstanceVariable", new Object[] { idValue , name, osType(value) }));
	}
	
	public static final boolean class_addMethod(long cls, long name, long imp, String types) throws Throwable{
		return boolValue(invokeMethod(OS.class,"class_addMethod", new Object[] { osType(cls), osType(name), osType(imp), types }));
	}
	
	public static final void objc_registerClassPair(long cls) throws Throwable{
		invokeMethod(OS.class, "objc_registerClassPair", new Object[] { osType(cls) });
	}
	
	public static final void setControlAction(NSControl control, long aSelector) throws Throwable{
		invokeMethod(NSControl.class, control, "setAction", new Object[] { osType(aSelector) });
	}
	
	public static final void setControlAction(NSMenuItem control, long aSelector) throws Throwable{
		invokeMethod(NSMenuItem.class, control, "setAction", new Object[] { osType(aSelector) });
	}
	
	public static final NSMenuItem getMenuItemAtIndex(NSMenu menu, long index) throws Throwable{
		return (NSMenuItem)invokeMethod(NSMenu.class, menu, "itemAtIndex", new Object[] { osType(index) });
	}
	
	public static final NSButton getStandardWindowButton(NSWindow nsWindow, long index) throws Throwable{
		return (NSButton)invokeMethod(NSWindow.class, nsWindow, "standardWindowButton", new Object[] { osType(index) });
	}
	
	public static final String getNSStringValue( long pointer ) throws Throwable {
		NSString nsString = new NSString();
		NSString.class.getField("id").set(nsString, osType(pointer) );
		return nsString.getString();
	}
	
	public static final Callback newCallback(Object object, String method64, String method32, int argCount) throws Throwable {
		return new Callback( object, ( C.PTR_SIZEOF == 8 ? method64 : method32 ) , argCount );
	}
	
	public static final long getCallbackAddress( Callback callback ) throws Throwable {
		return longValue(invokeMethod(Callback.class, callback , "getAddress", new Object[] {}));
	}
	
	public static final long getMenuNumberOfItems( NSMenu menu ) throws Throwable {
		return longValue(invokeMethod(NSMenu.class, menu , "numberOfItems", new Object[] {}));
	}
	
	public static final long NewGlobalRef( Object object ) throws Throwable{
		Method method = OS.class.getMethod("NewGlobalRef", new Class[] { Object.class });
		return longValue( method.invoke(OS.class, new Object[] { object }) ) ;
	}
	
	public static final void DeleteGlobalRef( long ref ) throws Throwable{
		invokeMethod(OS.class, "DeleteGlobalRef", new Object[] { osType(ref) } );
	}
	
	public static final MacToolbarDelegate newMacToolbarDelegate() throws Throwable{
		return (MacToolbarDelegate)Class.forName( MacToolbarDelegate.class.getName() ).newInstance();
	}
	
	private static Object invokeMethod(Class clazz, String methodName, Object[] args) throws Throwable {
		return invokeMethod(clazz, null, methodName, args);
	}
	
	private static Object invokeMethod(Class clazz, Object target, String methodName, Object[] args) throws Throwable {
		Class[] signature = new Class[args.length];
		for (int i = 0; i < args.length; i++) {
			Class thisClass = args[i].getClass();
			if (thisClass == Integer.class){
				signature[i] = int.class;
			}else if (thisClass == Long.class){
				signature[i] = long.class;
			}else if (thisClass == Byte.class){
				signature[i] = byte.class;
			}else{
				signature[i] = thisClass;
			}
		}
		Method method = clazz.getMethod(methodName, signature);
		return method.invoke(target, args);
	}
	
	private static Object osType( long value ) {
		return ( C.PTR_SIZEOF == 8 ? ((Object)new Long(value)) : ((Object)new Integer((int)value)) );
	}
	
	private static long longValue(Object object) {
		if (object instanceof Integer) {
			return ((Integer) object).longValue();
		}
		if (object instanceof Long) {
			return ((Long) object).longValue();
		}
		return 0;
	}
	
	private static boolean boolValue(Object object) {
		if (object instanceof Boolean) {
			return ((Boolean) object).booleanValue();
		}
		return false;
	}
}
