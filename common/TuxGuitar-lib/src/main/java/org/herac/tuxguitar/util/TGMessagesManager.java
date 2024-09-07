package org.herac.tuxguitar.util;

// stores translated messages for modules common to desktop and Android apps

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.herac.tuxguitar.player.base.MidiInstrument;
import org.herac.tuxguitar.player.base.MidiPercussionKey;
import org.herac.tuxguitar.resource.TGResourceBundle;

public class TGMessagesManager {
	
	private static TGMessagesManager instance;
	private final static String INSTRUMENT_NAME_PREFIX = "midi.instrument-";
	private final static String PERCUSSION_NAME_PREFIX = "midi.percussion-key-";
	
	private TGResourceBundle resources;
	private List<MidiInstrument> instruments = null;
	private List<MidiPercussionKey> percussionKeys = null;
	
	public static TGMessagesManager getInstance() {
		synchronized (TGMessagesManager.class) {
			if (instance==null) {
				instance = new TGMessagesManager();
			}
		}
		return instance;
	}
	
	private TGMessagesManager() {
		super();
	}

	public void setResources(TGResourceBundle bundle) {
		this.resources = bundle;
		
		instruments = new ArrayList<MidiInstrument>();
		int i=0;
		String name = getProperty(INSTRUMENT_NAME_PREFIX + String.valueOf(i));
		while (!name.equals(INSTRUMENT_NAME_PREFIX + String.valueOf(i))) {
			instruments.add(new MidiInstrument(name));
			i++;
			name = getProperty(INSTRUMENT_NAME_PREFIX + String.valueOf(i));
		}
		
		percussionKeys = new ArrayList<MidiPercussionKey>();
		i=0;
		// search first percussion (not zero)
		name = getProperty(PERCUSSION_NAME_PREFIX + String.valueOf(i));
		while (i<100 && name.equals(PERCUSSION_NAME_PREFIX + String.valueOf(i))) {
			i++;
			name = getProperty(PERCUSSION_NAME_PREFIX + String.valueOf(i));
		}
		// fill list until last is found
		while (!name.equals(PERCUSSION_NAME_PREFIX + String.valueOf(i))) {
			percussionKeys.add(new MidiPercussionKey(i,name));
			i++;
			name = getProperty(PERCUSSION_NAME_PREFIX + String.valueOf(i));
		}
	}
	
	public static String getProperty(String key) {
		try {
			String property = getInstance().resources.getString(key);
			if (property == null) {
				// Some instrument and percussion names may not exist, but for all other messages a warning will be displayed if the
				// message is missing in both messages.properties and messages_LANG.properties.
				if (!key.startsWith(INSTRUMENT_NAME_PREFIX) && !key.startsWith(PERCUSSION_NAME_PREFIX)) {
					System.err.println("Message " + key + " not found");
				}
				return key;
			} else {
				return property;
			}
		}catch(Throwable throwable){
			return key;
		}
	}
	
	public static String getProperty(String key, Object[] arguments) {
		String property = getProperty(key);
		// guiv42 07/2024, don't use MessageFormat.format(), as property may include characters which have a specific meaning in this context
		// typ. apostrophe
		// see https://github.com/helge17/tuxguitar/issues/468
		if ((arguments == null) || (arguments.length == 0))
			return property;
		String newProperty = new String(property);
		try {
			for (int i=0; i<arguments.length; i++) {
				newProperty = newProperty.replace("{" + String.valueOf(i) + "}", String.valueOf(arguments[i]));
			}
		} catch (Throwable e) {
			return property;
		}
		return newProperty;
	}
	
	public static MidiInstrument getMidiInstrument(int index) {
		if (getInstance().instruments == null) return null;
		
		int len = getInstance().instruments.size();
		if (index<0 || index >= len) return null;

		return getInstance().instruments.get(index);
	}
	
	public static int getMidiInstrumentsNumber() {
		if (getInstance().instruments == null) return 0;
		return getInstance().instruments.size();
	}
	
	public static MidiInstrument[] getInstruments() {
		if (getInstance().instruments == null) return null;
		return (getInstance().instruments.toArray(new MidiInstrument[0]));
	}
	
	public static MidiPercussionKey[] getMidiPercussionKeys() {
		return (getInstance().percussionKeys.toArray(new MidiPercussionKey[0]));
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
	
	public Locale getLocale() {
		if (this.resources == null) return null;
		return this.resources.getLocale();
	}

}
