package org.herac.tuxguitar.jws;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.util.TGClassLoader;

public class TuxGuitarJWS {

	private static final String PATH_TMP = ( System.getProperty( "java.io.tmpdir" ) + File.separator + "tuxguitar-jws" );
	private static final String PATH_SHARE = (PATH_TMP + File.separator + "share");
	
	public static void main(String[] s){
		extractFiles("share.jar",PATH_TMP);
		
		TGClassLoader.instance().addPath(PATH_SHARE);
		
		TuxGuitar.instance().displayGUI(s);
		
		deleteFiles(new File(PATH_TMP));
		
		System.exit(0);
	}
	
    public static void extractFiles(String filename,String path){
        try{
            int offset = 0;
        	byte[] buf = new byte[1024];

        	JarInputStream in = new JarInputStream(TuxGuitarJWS.class.getClassLoader().getResourceAsStream(filename));
            JarEntry entry = null;
            while( (entry = in.getNextJarEntry()) != null){
            	if(!entry.isDirectory()){
                	File file = new File(path + File.separator + entry.getName());
                	if(!file.getParentFile().exists()){
                		file.getParentFile().mkdirs();
                	}
                	
                	OutputStream out = new FileOutputStream(file);
                	while ((offset = in.read(buf, 0, 1024)) > -1){
                		out.write(buf, 0, offset);
                	}
                	out.close();
                }
                in.closeEntry();
            }
            in.close();
        }
        catch (Throwable throwable){
        	throwable.printStackTrace();
        }
    }

    public static boolean deleteFiles(File file) {
    	try{
    		if (file.isDirectory()) {
    			String[] children = file.list();
    			for (int i=0; i<children.length; i++) {
    				boolean success = deleteFiles(new File(file, children[i]));
    				if (!success) {
    					return false;
    				}
    			}
    		}
    		return file.delete();
    	}catch (Throwable throwable){
        	throwable.printStackTrace();
        }
        return false;
    }    
}