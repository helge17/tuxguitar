package org.herac.tuxguitar.gui.util;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.system.config.TGConfigKeys;
import org.herac.tuxguitar.util.TGClassLoader;
import org.herac.tuxguitar.util.TGLibraryLoader;
import org.herac.tuxguitar.util.TGVersion;

public class TGFileUtils {		
	
	private static final String TG_CONFIG_PATH = "tuxguitar.config.path";	
	private static final String TG_SHARE_PATH = "tuxguitar.share.path";	
	private static final String TG_DOC_PATH = "tuxguitar.doc.path";
	private static final String TG_CLASS_PATH = "tuxguitar.class.path";
	private static final String TG_LIBRARY_PATH = "tuxguitar.library.path";
	private static final String TG_LIBRARY_PREFIX = "tuxguitar.library.prefix";
	private static final String TG_LIBRARY_EXTENSION = "tuxguitar.library.extension";
	
	public static final String PATH_USER_CONFIG = getUserConfigDir();
	public static final String PATH_USER_PLUGINS_CONFIG = getUserPluginsConfigDir();
	public static final String PATH_SKINS = getSharePath("skins");
	public static final String PATH_LANGUAGE = getSharePath("lang");
	public static final String PATH_SCALES = getSharePath("scales");
	public static final String PATH_HELP = getDocPath("help");
	
	public static Image loadImage(String name){
		return loadImage(TuxGuitar.instance().getConfig().getStringConfigValue(TGConfigKeys.SKIN),name);
	}
	
	public static Image loadImage(String skin,String name){
		if(PATH_SKINS != null){			
			String path = (PATH_SKINS + skin + File.separator + name);
			try{
				return new Image(TuxGuitar.instance().getDisplay(),new ImageData(path));
			}catch(Exception e){
				System.err.println(path + ": not found");
			}
		}
		return new Image(TuxGuitar.instance().getDisplay(),16,16);
	}		
			
	public static InputStream getResourceAsStream(String resource) {        
		return TGClassLoader.instance().getClassLoader().getResourceAsStream(resource);
	}
	
	public static void loadClasspath(){
		String plugins = getSharePath("plugins");
		if(plugins != null){
			TGClassLoader.instance().addPaths(new File(plugins));
		}
		
		String custompath = System.getProperty(TG_CLASS_PATH);
		if(custompath != null){
			String[] paths = custompath.split(File.pathSeparator);
			for(int i = 0; i < paths.length; i++){
				TGClassLoader.instance().addPaths(new File(paths[i]));
			}
		}
	}
	
	public static void loadLibraries(){
		String libraryPath = System.getProperty(TG_LIBRARY_PATH);
		if(libraryPath != null){
			String[] libraryPaths = libraryPath.split(File.pathSeparator);
			String libraryPrefix = System.getProperty(TG_LIBRARY_PREFIX);
			String libraryExtension = System.getProperty(TG_LIBRARY_EXTENSION);
			for(int i = 0; i < libraryPaths.length; i++){
				TGLibraryLoader.instance().loadLibraries(new File(libraryPaths[i]),libraryPrefix,libraryExtension);
			}
		}
	}	

    private static String getUserConfigDir(){   
    	// Look for the system property
    	String configPath = System.getProperty(TG_CONFIG_PATH);

    	// Default System User Home
    	if(configPath == null){
    		configPath = ( (System.getProperty("user.home") + File.separator + ".tuxguitar-" + TGVersion.CURRENT.getVersion()) ) ;
    	}

    	// Check if the path exists
		File file = new File(configPath);
		if(!file.exists()){
			file.mkdirs();
		}
    	return configPath;
    }
    
    private static String getUserPluginsConfigDir(){   
    	String configPluginsPath = (getUserConfigDir() + File.separator + "plugins");
    	
    	//Check if the path exists
		File file = new File(configPluginsPath);
		if(!file.exists()){
			file.mkdirs();
		}
    	
    	return configPluginsPath;
    }    
    
	private static String getSharePath(String resource){
		return getResourcePath(resource, System.getProperty(TG_SHARE_PATH));
	}

	private static String getDocPath(String resource){
		return getResourcePath(resource, System.getProperty(TG_DOC_PATH));
	}

	private static String getResourcePath(String resource,String path) {
		try {
			String resourcePath = null;
			if(path != null){
				resourcePath = (path + File.separator + resource);
			}else{
				URL url = TGClassLoader.instance().getClassLoader().getResource(resource);
				if(url != null){
					resourcePath = URLDecoder.decode(url.getPath(), "UTF-8");
				}
			}
			if(resourcePath != null){
				return new File(resourcePath).getAbsolutePath() + File.separator;
			}        
		}catch(Throwable throwable){
			throwable.printStackTrace();	
		}		
        return null;
	}

}
