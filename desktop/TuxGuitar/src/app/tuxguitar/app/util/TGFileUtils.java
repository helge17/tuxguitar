package app.tuxguitar.app.util;

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

import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.resource.TGResourceManager;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGLibraryLoader;
import app.tuxguitar.util.TGUserFileUtils;

public class TGFileUtils {

	private static final String FILE_SCHEME = "file";

	private static final String TG_SHARE_PATH = "tuxguitar.share.path";
	private static final String TG_CLASS_PATH = "tuxguitar.class.path";
	private static final String TG_LIBRARY_PATH = "tuxguitar.library.path";
	private static final String TG_LIBRARY_PREFIX = "tuxguitar.library.prefix";
	private static final String TG_LIBRARY_EXTENSION = "tuxguitar.library.extension";

	public static final String PATH_HOME = TGUserFileUtils.PATH_HOME;
	public static final String PATH_USER_DIR = TGUserFileUtils.PATH_USER_DIR;
	public static final String PATH_USER_CONFIG = TGUserFileUtils.PATH_USER_CONFIG;
	public static final String PATH_USER_PLUGINS_CONFIG = TGUserFileUtils.PATH_USER_PLUGINS_CONFIG;
	public static final String PATH_USER_SHARE_PATH = TGUserFileUtils.PATH_USER_SHARE_PATH;

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

	public static UIImage loadImage(TGContext context, String skin, String name){
		UIFactory uiFactory = TGApplication.getInstance(context).getFactory();
		try{
			InputStream stream = TGResourceManager.getInstance(context).getResourceAsStream("skins/" + skin + "/" + name);
			if( stream != null ){
				return uiFactory.createImage(stream);
			}
			System.err.println("Icon " + name + " not found in skin " + skin);
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
		return getDecodedPath(new File(fileName).getName());
	}

	public static String getDecodedPath(String path) {
		try {
			return URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
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
		return TGUserFileUtils.isExistentAndReadable(file);
	}

	public static boolean isDirectoryAndReadable( File file ){
		return TGUserFileUtils.isDirectoryAndReadable(file);
	}

	public static boolean tryCreateDirectory( File file ){
		return TGUserFileUtils.tryCreateDirectory(file);
	}
}
