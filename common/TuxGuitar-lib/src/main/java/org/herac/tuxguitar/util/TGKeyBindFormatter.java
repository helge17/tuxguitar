package org.herac.tuxguitar.util;

import java.util.List;

public class TGKeyBindFormatter {

    public static final String DEFAULT_MASK_SEPARATOR = "+";

    private TGKeyTranslator translator;
    private static TGKeyBindFormatter instance = null;

    public interface TGKeyTranslator {
        String translate(List<String> keyCombination);
    }

    private TGKeyBindFormatter() { super(); }

    public void setTranslator(TGKeyTranslator translator) {
        this.translator = translator;
    }

    public static String defaultTranslate(List<String> keyCombination) {
        StringBuffer fullMask = new StringBuffer();
        for(String key : keyCombination){
            if( fullMask.length() > 0 ) {
                fullMask.append(DEFAULT_MASK_SEPARATOR);
            }
            fullMask.append(key);
        }
        return fullMask.toString();
    }

    public String format(List<String> keyCombination) {
        if (translator != null) {
            return translator.translate(keyCombination);
        }
        return defaultTranslate(keyCombination);
    }

    public static TGKeyBindFormatter getInstance() {
        synchronized (TGKeyBindFormatter.class) {
            if( instance == null ) {
                instance = new TGKeyBindFormatter();
            }
            return instance;
        }
    }
}
