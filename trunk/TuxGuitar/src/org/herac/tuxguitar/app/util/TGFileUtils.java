package org.herac.tuxguitar.app.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.resource.TGResourceManager;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGLibraryLoader;
import org.herac.tuxguitar.util.TGVersion;

public class TGFileUtils {
	
	private static final String FILE_SCHEME = "file";
	
	private static final String TG_HOME_PATH = "tuxguitar.home.path";
	private static final String TG_CONFIG_PATH = "tuxguitar.config.path";
	private static final String TG_SHARE_PATH = "tuxguitar.share.path";
	private static final String TG_USER_SHARE_PATH = "tuxguitar.user-share.path";
	private static final String TG_CLASS_PATH = "tuxguitar.class.path";
	private static final String TG_LIBRARY_PATH = "tuxguitar.library.path";
	private static final String TG_LIBRARY_PREFIX = "tuxguitar.library.prefix";
	private static final String TG_LIBRARY_EXTENSION = "tuxguitar.library.extension";
	
	public static final String PATH_HOME = getHomePath();
	public static final String PATH_USER_DIR = getDefaultUserAppDir();
	public static final String PATH_USER_CONFIG = getUserConfigDir();
	public static final String PATH_USER_PLUGINS_CONFIG = getUserPluginsConfigDir();
	public static final String PATH_USER_SHARE_PATH = getUserSharedPath();
	
	//writable
	public static final String[] TG_STATIC_SHARED_PATHS = getStaticSharedPaths();
	
	public static void loadClasspath(TGContext context){
		try {
			TGClassLoader.getInstance(context).setFilePaths(TG_STATIC_SHARED_PATHS);
			
			Enumeration<URL> plugins = TGResourceManager.getInstance(context).getResources("plugins");
			while( plugins.hasMoreElements() ){
				URL url = (URL)plugins.nextElement();
				TGClassLoader.getInstance(context).addPaths(new File(getUrlPath(url)));
			}
			
			String custompath = System.getProperty(TG_CLASS_PATH);
			if(custompath != null){
				String[] paths = custompath.split(File.pathSeparator);
				for(int i = 0; i < paths.length; i++){
					TGClassLoader.getInstance(context).addPaths(new File(paths[i]));
				}
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
	}
	
	public static void loadLibraries(TGContext context){
		String libraryPath = System.getProperty(TG_LIBRARY_PATH);
		if(libraryPath != null){
			String[] libraryPaths = libraryPath.split(File.pathSeparator);
			String libraryPrefix = System.getProperty(TG_LIBRARY_PREFIX);
			String libraryExtension = System.getProperty(TG_LIBRARY_EXTENSION);
			for(int i = 0; i < libraryPaths.length; i++){
				TGLibraryLoader.getInstance(context).loadLibraries(new File(libraryPaths[i]),libraryPrefix,libraryExtension);
			}
		}
	}
	
	private static String getResourcePath(TGContext context, String resource) {
		try {
			URL url = TGResourceManager.getInstance(context).getResource(resource);
			if(url != null){
				return getUrlPath(url);
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return null;
	}
	
	public static String[] getFileNames(TGContext context, String resource ){
		try {
			String path = getResourcePath(context, resource);
			if( path != null ){
				File file = new File( path );
				if( isExistentAndReadable( file ) && isDirectoryAndReadable( file )){
					return file.list();
				}
			}
			
			InputStream stream = TGResourceManager.getInstance(context).getResourceAsStream(resource + "/list.properties");
			if( stream != null ){
				BufferedReader reader = new BufferedReader( new InputStreamReader(stream) );
				List<String> fileNameList = new ArrayList<String>();
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
	
	public static UIImage loadImage2(TGContext context, String skin, String name){
		UIFactory uiFactory = TGApplication.getInstance(context).getFactory();
		try{
			InputStream stream = TGResourceManager.getInstance(context).getResourceAsStream("skins/" + skin + "/" + name);
			if( stream != null ){
				return uiFactory.createImage(stream);
			}
			System.err.println(name + ": not found");
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return uiFactory.createImage(16, 16);
	}
	
	public static boolean isLocalFile(URL url){
		try {
			if( url.getProtocol().equals( new File(url.getFile()).toURI().toURL().getProtocol() ) ){
				return true;
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return false;
	}
	
	public static boolean isLocalFile(URI uri){
		try {
			if( uri.isAbsolute() && !uri.isOpaque() && FILE_SCHEME.equals(uri.getScheme())) {
				return true;
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return false;
	}
	
	public static String getDecodedFileName(URI uri) {
		return (!uri.isOpaque() ? getDecodedFileName(uri.getPath()) : null);
	}
	
	public static String getDecodedFileName(URL url) {
		return getDecodedFileName(url.getFile());
	}
	
	public static String getDecodedFileName(String fileName) {
		try {
			return URLDecoder.decode(new File(fileName).getName(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static String getHomePath(){
		// Look for the system property
		String homePath = System.getProperty(TG_HOME_PATH);
		if( homePath != null ) {
			File file = new File(homePath).getAbsoluteFile();
			if( isExistentAndReadable( file ) && isDirectoryAndReadable( file )){
				return file.getAbsolutePath();
			}
		}
		
		// Default user dir
		homePath = System.getProperty("user.dir");
		if( homePath != null ) {
			File file = new File(homePath).getAbsoluteFile();
			if( isExistentAndReadable( file ) && isDirectoryAndReadable( file )){
				return file.getAbsolutePath();
			}
		}
		
		return new File(".").getAbsolutePath();
	}
	
	private static String getDefaultUserAppDir(){
		return ((System.getProperty("user.home") + File.separator + ".tuxguitar-" + TGVersion.CURRENT.getVersion()));
	}
	
	private static String getUserConfigDir(){
		// Look for the system property
		String configPath = System.getProperty(TG_CONFIG_PATH);
		
		// Default System User Home
		if( configPath == null ){
			configPath = (getDefaultUserAppDir() + File.separator + "config");
		}
		
		// Check if the path exists
		File file = new File(configPath);
		if(!isExistentAndReadable( file )){
			tryCreateDirectory( file );
		}
		return configPath;
	}
	
	private static String getUserPluginsConfigDir(){
		String configPluginsPath = (getUserConfigDir() + File.separator + "plugins");
		
		//Check if the path exists
		File file = new File(configPluginsPath);
		if(!isExistentAndReadable( file )){
			tryCreateDirectory( file );
		}
		
		return configPluginsPath;
	}
	
	private static String getUserSharedPath(){
		// Look for the system property
		String userSharePath = System.getProperty(TG_USER_SHARE_PATH);
		
		// Use configuration path as default.
		if( userSharePath == null){
			userSharePath = (getDefaultUserAppDir() + File.separator + "cache");
		}
		
		// Check if the path exists
		File file = new File(userSharePath);
		if(!isExistentAndReadable( file )){
			tryCreateDirectory( file );
		}
		return userSharePath;
	}
	
	private static String[] getStaticSharedPaths(){
		String staticSharedPaths = new String(PATH_USER_SHARE_PATH);
		String staticSharedPathsProperty = System.getProperty(TG_SHARE_PATH);
		if( staticSharedPathsProperty != null ){
			staticSharedPaths += (File.pathSeparator + staticSharedPathsProperty);
		}
		return staticSharedPaths.split(File.pathSeparator);
	}
	
	private static String getUrlPath( URL url ) throws UnsupportedEncodingException{
		return (new File(URLDecoder.decode(url.getPath(), "UTF-8")).getAbsolutePath() + File.separator);
	}
	
	public static boolean isExistentAndReadable( File file ){
		try{
			return file.exists();
		}catch(SecurityException se){
			return false;
		}
	}
	
	public static boolean isDirectoryAndReadable( File file ){
		try{
			return file.isDirectory();
		}catch(SecurityException se){
			return false;
		}
	}
	
	public static boolean tryCreateDirectory( File file ){
		try{
			return file.mkdirs();
		}catch(SecurityException se){
			return false;
		}
	}
}
