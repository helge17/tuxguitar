/*
 * Created on 09-ene-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package app.tuxguitar.app.system.language;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.tuxguitar.app.util.TGFileUtils;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.event.TGEventManager;
import app.tuxguitar.resource.TGResourceBundle;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGMessagesManager;

/**
 * @author julian
 *
 */
public class TGLanguageManager {

	public static final String PACKAGE = "lang";
	public static final String PREFIX = "messages";
	public static final String EXTENSION = ".properties";

	private TGContext context;
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
			TGMessagesManager.getInstance().setResources(TGResourceBundle.getBundle(this.context, baseName, locale));
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
	public String[] getLanguages() {
		return this.languages;
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
			this.languages[i] = availableList.get( i );
		}
	}
}
