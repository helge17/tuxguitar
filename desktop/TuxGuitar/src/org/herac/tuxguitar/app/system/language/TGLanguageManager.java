/*
 * Created on 09-ene-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.system.language;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.herac.tuxguitar.app.util.TGFileUtils;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.util.TGContext;

/**
 * @author julian
 * 
 */
public class TGLanguageManager {
	
	public static final String PACKAGE = "lang";
	public static final String PREFIX = "messages";
	public static final String EXTENSION = ".properties";
	
	private TGContext context;
	private TGResourceBundle resources;
	private String[] languages;
	
	public TGLanguageManager(TGContext context) {
		this.context = context;
		this.loadLanguages();
	}
	
	public void addLoader(TGEventListener listener){
		TGEventManager.getInstance(this.context).addListener(TGLanguageEvent.EVENT_TYPE, listener);
	}
	
	public void removeLoader(TGEventListener listener){
		TGEventManager.getInstance(this.context).removeListener(TGLanguageEvent.EVENT_TYPE, listener);
	}
	
	private void fireChanges(){
		TGEventManager.getInstance(this.context).fireEvent(new TGLanguageEvent());
	}
	
	public void setLanguage(String lang) {
		try {
			String baseName = (PACKAGE + "." + PREFIX);
			Locale locale = getLocale(lang);
			this.resources = TGResourceBundle.getBundle(this.context, baseName, locale);
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
		List<String> availableList = new ArrayList<String>();
		String[] fileNames = TGFileUtils.getFileNames(this.context, "lang");
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