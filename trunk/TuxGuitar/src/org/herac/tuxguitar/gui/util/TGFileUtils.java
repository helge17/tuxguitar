package org.herac.tuxguitar.gui.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

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
	private static final String TG_CLASS_PATH = "tuxguitar.class.path";
	private static final String TG_LIBRARY_PATH = "tuxguitar.library.path";
	private static final String TG_LIBRARY_PREFIX = "tuxguitar.library.prefix";
	private static final String TG_LIBRARY_EXTENSION = "tuxguitar.library.extension";
	
	public static final String PATH_USER_CONFIG = getUserConfigDir();
	public static final String PATH_USER_PLUGINS_CONFIG = getUserPluginsConfigDir();
	
	public static final String TG_STATIC_SHARED_PATH = System.getProperty(TG_SHARE_PATH);
	
	public static Image loadImage(String skin,String name){
		try{
			InputStream stream = getResourceAsStream("skins/" + skin + "/" + name);
			if(stream != null){			
				return new Image(TuxGuitar.instance().getDisplay(),new ImageData(stream));
			}
			System.err.println(name + ": not found");
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return new Image(TuxGuitar.instance().getDisplay(),16,16);
	}
	
	public static InputStream getResourceAsStream(String resource) {
		try {
			if(TG_STATIC_SHARED_PATH != null){
				File file = new File(TG_STATIC_SHARED_PATH + File.separator + resource);
				if( file.exists() ){
					return new FileInputStream( file );
				}
			}
			return TGClassLoader.instance().getClassLoader().getResourceAsStream(resource);
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return null;
	}
	
	public static URL getResourceUrl(String resource) {
		try {
			return TGClassLoader.instance().getClassLoader().getResource(resource);
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return null;
	}
	
	private static String getResourcePath(String resource) {
		try {
			String resourcePath = null;
			if(TG_STATIC_SHARED_PATH != null){
				resourcePath = (TG_STATIC_SHARED_PATH + File.separator + resource);
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
	
	public static void loadClasspath(){
		String plugins = getResourcePath("plugins");
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
	
	public static String[] getFileNames( String resource ){
		try {
			String path = getResourcePath(resource);
			if( path != null ){
				File file = new File( path );
				if(file.exists() && file.isDirectory()){
					return file.list();
				}
			}
			InputStream stream = getResourceAsStream(resource + "/list.properties" );
			if( stream != null ){
				BufferedReader reader = new BufferedReader( new InputStreamReader(stream) );
				List fileNameList = new ArrayList();
				String fileName = null;
				while( (fileName = reader.readLine()) != null ){
					fileNameList.add( fileName );
				}
				String[] fileNames = new String[ fileNameList.size() ];
				for (int i = 0 ; i < fileNames.length ; i ++ ){
					fileNames[ i ] = (String)fileNameList.get( i );
				}
				return fileNames;
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return null;
	}
	
	public static Image loadImage(String name){
		return loadImage(TuxGuitar.instance().getConfig().getStringConfigValue(TGConfigKeys.SKIN),name);
	}
}
