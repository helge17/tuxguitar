/*
 * Created on 09-ene-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.system.language;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.herac.tuxguitar.gui.util.TGFileUtils;

/**
 * @author julian
 * 
 */
public class LanguageManager {
	
	public static final String PACKAGE = "lang";
	public static final String PREFIX = "messages";
	public static final String EXTENSION = ".properties";
	
	private TGResourceBundle resources;
	private String[] languages;
	private List loaders;
	
	public LanguageManager() {
		this.loaders = new ArrayList();
		this.loadLanguages();
	}
	
	public void addLoader(LanguageLoader loader){
		if( !this.loaders.contains( loader )){
			this.loaders.add(loader);
		}
	}
	
	public void removeLoader(LanguageLoader loader){
		if( this.loaders.contains( loader )){
			this.loaders.remove(loader);
		}
	}
	
	private void fireChanges(){
		Iterator it = this.loaders.iterator();
		while(it.hasNext()){
			LanguageLoader loader = (LanguageLoader)it.next();
			loader.loadProperties();
		}
	}
	
	public void setLanguage(String lang) {
		try {
			String baseName = (PACKAGE + "." + PREFIX);
			Locale locale = getLocale(lang);
			this.resources = TGResourceBundle.getBundle(baseName, locale);
			this.fireChanges();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Locale getLocale(String lang){
		if(this.isSupportedLanguage(lang)){
			String[] locale = lang.split("_");
			String language = (locale.length > 0 ? locale[0] : "" );
			String country =  (locale.length > 1 ? locale[1] : "" );
			String variant =  (locale.length > 2 ? locale[2] : "" );
			return new Locale(language, country, variant);
		}
		return Locale.getDefault();
	}
	
	private boolean isSupportedLanguage(String lang){
		if(lang != null && lang.length() > 0 && this.languages != null){
			for(int i = 0 ; i < this.languages.length; i ++){
				if(this.languages[i].equals(lang)){
					return true;
				}
			}
		}
		return false;
	}
	
	public String getProperty(String key,String value) {
		try {
			String property = this.resources.getString(key);
			return (property == null ? value : property );
		}catch(Throwable throwable){
			return value;
		}
	}
	
	public String getProperty(String key) {
		return this.getProperty(key,key);
	}
	
	public String getProperty(String key, Object[] arguments) {
		return getProperty(key,key,arguments);
	}
	
	public String getProperty(String key,String value, Object[] arguments) {
		String property = this.getProperty(key,value);
		return ( arguments != null ? MessageFormat.format(property, arguments) : property );
	}
	
	public String[] getLanguages() {
		return this.languages;
	}
	
	public String getLanguage() {
		if(this.resources != null){
			Locale locale = this.resources.getLocale();
			boolean language = (locale.getLanguage() != null && locale.getLanguage().length() > 0);
			boolean country = (locale.getCountry() != null && locale.getCountry().length() > 0);
			boolean variant = (locale.getVariant() != null && locale.getVariant().length() > 0);
			
			String localeId = new String();
			if( language ){
				localeId += locale.getLanguage();
			}
			if( country ){
				localeId += "_" + locale.getCountry();
			}
			if( variant ){
				localeId += "_" + ( country ? locale.getVariant() : ("_" + locale.getVariant()) );
			}
			return localeId;
		}
		return null;
	}
	
	/**
	 * Load language files from lang folder
	 *
	 */
	private void loadLanguages(){
		List availableList = new ArrayList();
		String[] fileNames = TGFileUtils.getFileNames("lang");
		if( fileNames != null ){
			// now iterate over them
			for(int i = 0;i < fileNames.length;i++){
				if (fileNames[i].indexOf("messages_") == 0){
					int prefixIndex = fileNames[i].indexOf(PREFIX + "_");
					int extensionIndex = fileNames[i].indexOf(EXTENSION);
					if(prefixIndex == 0 && extensionIndex > (PREFIX + "_").length()){
						availableList.add( fileNames[i].substring( (PREFIX + "_").length() , extensionIndex ) );
					}
				}
			}
		}
		this.languages = new String[availableList.size()];
		for(int i = 0; i < this.languages.length; i++){
			this.languages[i] = (String) availableList.get( i );
		}
	}
}