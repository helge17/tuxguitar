package app.tuxguitar.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class TGUserFileUtils {

	private static final String TG_HOME_PATH = "tuxguitar.home.path";
	private static final String TG_USER_SHARE_PATH = "tuxguitar.user-share.path";
	private static final String TG_CONFIG_PATH = "tuxguitar.config.path";

	public static final String PATH_HOME = getHomePath();
	public static final String PATH_USER_DIR = getDefaultUserAppDir();
	public static final String PATH_USER_CONFIG = getUserConfigDir();
	public static final String PATH_USER_PLUGINS_CONFIG = getUserPluginsConfigDir();
	public static final String PATH_USER_SHARE_PATH = getUserSharedPath();
	public static final String PATH_USER_TEMPLATE = getUserTemplatePath();
	public static final String PATH_USER_TUNINGS = getUserTuningsPath();
	public static final String PATH_USER_TOOLBAR = getUserToolBarPath();

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

	private static String getUserTemplatePath() {
		String path = getUserConfigDir() + File.separator + "templates";
		File directory = new File(path);
		if (!isDirectoryAndReadable(directory)) {
			tryCreateDirectory(directory);
		}
		return path + File.separator + "user-template.tg";
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

	private static String getDefaultUserAppDir(){
		String home = System.getProperty("user.home");
		String folderName = "tuxguitar";
		String os = System.getProperty("os.name");
		if (os.equals("Mac OS X")) {
			return joinPath(home, "Library", "Application Support", folderName);
		}
		if (os.contains("Windows")) {
			return joinPath(home, "AppData", "Roaming", folderName);
		}
		return joinPath(home, ".config", folderName);
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


	public static boolean isExistentAndReadable( File file ){
		try{
			return file.exists();
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

	private static String joinPath(String base, String ...entries) {
		File file = new File(base);
		for (String entry : entries) {
			file = new File(file, entry);
		}
		return file.toString();
	}

	public static boolean isDirectoryAndReadable( File file ){
		try{
			return file.isDirectory();
		}catch(SecurityException se){
			return false;
		}
	}

	public static boolean isUserTemplateReadable()  {
		return isExistentAndReadable(new File(PATH_USER_TEMPLATE));
	}

	public static boolean setUserTemplate(File srcFile) {
		try {
			Path src = srcFile.toPath();
			Path dst = new File(PATH_USER_TEMPLATE).toPath();
			Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
			return true;
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean deleteUserTemplate() {
		return new File(PATH_USER_TEMPLATE).delete();
	}

	private static String getUserTuningsPath() {
		return getUserConfigDir() + File.separator + "tunings.xml";
	}

	private static String getUserToolBarPath() {
		return getUserConfigDir() + File.separator + "toolbar.xml";
	}
}
