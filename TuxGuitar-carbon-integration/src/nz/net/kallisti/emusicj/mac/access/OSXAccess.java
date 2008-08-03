/*
 * Created on Jul 21, 2006 3:19:03 PM
 * Copyright (C) 2006 Aelitis, All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * AELITIS, SAS au capital de 46,603.30 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 */
package nz.net.kallisti.emusicj.mac.access;
 	
import org.eclipse.swt.internal.carbon.AEDesc;
 	
/**
 * @author TuxPaper
 * @created Jul 21, 2006
 *
 */
public class OSXAccess
{
	private static boolean bLoaded = false;

        static {
                try {
                        System.loadLibrary ("OSXAccess");
                        System.out.println("OSXAccess v" + getVersion() + " Load complete!");
                        bLoaded = true;
                } catch (UnsatisfiedLinkError e1) {
                        System.err.println("Could not find libOSXAccess.jnilib");
                }
        }

        public static final native int AEGetParamDesc(int theAppleEvent, int theAEKeyword, int desiredType, AEDesc result);

        public static final native String getVersion();

        // 1.02
        public static final native String getDocDir();
        
        // 1.03
        public static final native void memmove(byte[] dest, int src, int size);
       
        public static boolean isLoaded() {
                return bLoaded;
        }
}