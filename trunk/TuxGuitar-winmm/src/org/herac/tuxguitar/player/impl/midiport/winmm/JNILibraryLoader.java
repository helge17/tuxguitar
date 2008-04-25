package org.herac.tuxguitar.player.impl.midiport.winmm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.herac.tuxguitar.util.TGClassLoader;

public class JNILibraryLoader {
	
	private static final String JNI_EXTENSION = ".jnilib";
	
	private static final String JNI_TMP_PATH = (System.getProperty( "java.io.tmpdir" ) + File.separator);
	
	public static void loadLibrary(String libname){
		if(!JNILibraryLoader.loadFromClassPath(libname + JNI_EXTENSION)){
			System.loadLibrary(libname);
		}
	}
	
	private static boolean loadFromClassPath(String filename){
		File file = new File(JNI_TMP_PATH + filename);
		try{
			if(!file.exists()){
				InputStream inputStream = TGClassLoader.instance().getClassLoader().getResourceAsStream(filename);
				if (inputStream != null) {
					OutputStream outputStream = new FileOutputStream(file);
					
					int read;
					byte [] buffer = new byte [4096];
					while ((read = inputStream.read (buffer)) != -1) {
						outputStream.write(buffer, 0, read);
					}
					outputStream.close();
					inputStream.close();
				}
			}
			if(file.exists()){
				System.load(file.getAbsolutePath());
				return true;
			}
		}catch(Throwable throwable){
			return false;
		}finally{
			if(file.exists()){
				file.delete();
			}
		}
		return false;
	}
}
