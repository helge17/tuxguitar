package org.herac.tuxguitar.carbon.opendoc;
 	
import org.eclipse.swt.internal.carbon.AEDesc;
 	
/**
 * @author TuxPaper
 * @created Jul 21, 2006
 *
 */
public class OpenDocJNI {
	
	private static final String JNI_LIBRARY_NAME = new String("tuxguitar-carbon-integration");
	
	static {
		System.loadLibrary (JNI_LIBRARY_NAME);
	}
	
	public static final native int AEGetParamDesc(int theAppleEvent, int theAEKeyword, int desiredType, AEDesc result);
	
}