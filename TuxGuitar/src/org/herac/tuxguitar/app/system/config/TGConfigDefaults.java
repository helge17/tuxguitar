/*
 * Created on 27-ene-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.system.config;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.view.component.tab.edit.EditorKit;
import org.herac.tuxguitar.app.view.dialog.fretboard.TGFretBoardConfig;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.util.properties.TGProperties;
import org.herac.tuxguitar.util.properties.TGPropertiesManager;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TGConfigDefaults{
	
	public static final String RESOURCE = "config-defaults";
	public static final String MODULE = "tuxguitar";
	
	private static final String DEFAULT_FONT_NAME = getDefaultFontName();
	
	public static TGProperties createDefaults(){
		TGPropertiesManager propertiesManager = TuxGuitar.getInstance().getPropertiesManager();
		TGProperties properties = propertiesManager.createProperties();
		TGConfigDefaults.loadProperties( properties );
		propertiesManager.readProperties(properties, RESOURCE, MODULE);
		
		return properties;
	}
	
	public static void loadProperties(TGProperties properties){
		loadProperty(properties, TGConfigKeys.SKIN,"Lavender");
		loadProperty(properties, TGConfigKeys.WINDOW_TITLE,"${appname} - ${filename}");
		loadProperty(properties, TGConfigKeys.SHOW_SPLASH,true);
		loadProperty(properties, TGConfigKeys.MAXIMIZED,false);
		loadProperty(properties, TGConfigKeys.WIDTH,800);
		loadProperty(properties, TGConfigKeys.HEIGHT,600);
		loadProperty(properties, TGConfigKeys.SHOW_INSTRUMENTS,false);
		loadProperty(properties, TGConfigKeys.SHOW_TRANSPORT,false);
		loadProperty(properties, TGConfigKeys.SHOW_FRETBOARD,false);
		loadProperty(properties, TGConfigKeys.SHOW_PIANO,false);
		loadProperty(properties, TGConfigKeys.SHOW_MARKERS,false);
		loadProperty(properties, TGConfigKeys.LAYOUT_MODE,TGLayout.MODE_VERTICAL);
		loadProperty(properties, TGConfigKeys.LAYOUT_STYLE,(TGLayout.DISPLAY_TABLATURE | TGLayout.DISPLAY_SCORE | TGLayout.DISPLAY_COMPACT | TGLayout.DISPLAY_CHORD_DIAGRAM));
		loadProperty(properties, TGConfigKeys.EDITOR_MOUSE_MODE,EditorKit.MOUSE_MODE_SELECTION);
		loadProperty(properties, TGConfigKeys.EDITOR_NATURAL_KEY_MODE,true);
		loadProperty(properties, TGConfigKeys.FONT_DEFAULT, (DEFAULT_FONT_NAME + ",6,0"));
		loadProperty(properties, TGConfigKeys.FONT_NOTE, (DEFAULT_FONT_NAME + ",7,0"));
		loadProperty(properties, TGConfigKeys.FONT_TIME_SIGNATURE, (DEFAULT_FONT_NAME + ",13,1"));
		loadProperty(properties, TGConfigKeys.FONT_LYRIC, (DEFAULT_FONT_NAME + ",7,0"));
		loadProperty(properties, TGConfigKeys.FONT_TEXT, (DEFAULT_FONT_NAME + ",8,0"));
		loadProperty(properties, TGConfigKeys.FONT_CHORD, (DEFAULT_FONT_NAME + ",7,0"));
		loadProperty(properties, TGConfigKeys.FONT_CHORD_FRET, (DEFAULT_FONT_NAME + ",5,0"));
		loadProperty(properties, TGConfigKeys.FONT_GRACE, (DEFAULT_FONT_NAME + ",6,0"));
		loadProperty(properties, TGConfigKeys.FONT_MARKER, (DEFAULT_FONT_NAME + ",7,0"));
		loadProperty(properties, TGConfigKeys.FONT_PRINTER_DEFAULT, (DEFAULT_FONT_NAME + ",6,0"));
		loadProperty(properties, TGConfigKeys.FONT_PRINTER_NOTE, (DEFAULT_FONT_NAME + ",6,0"));
		loadProperty(properties, TGConfigKeys.FONT_PRINTER_TIME_SIGNATURE, (DEFAULT_FONT_NAME + ",10,1"));
		loadProperty(properties, TGConfigKeys.FONT_PRINTER_LYRIC, (DEFAULT_FONT_NAME + ",7,0"));
		loadProperty(properties, TGConfigKeys.FONT_PRINTER_TEXT, (DEFAULT_FONT_NAME + ",8,0"));
		loadProperty(properties, TGConfigKeys.FONT_PRINTER_CHORD, (DEFAULT_FONT_NAME + ",7,0"));
		loadProperty(properties, TGConfigKeys.FONT_PRINTER_GRACE, (DEFAULT_FONT_NAME + ",6,0"));
		loadProperty(properties, TGConfigKeys.FONT_ABOUT_DIALOG_TITLE, (DEFAULT_FONT_NAME + ",36,3"));
		loadProperty(properties, TGConfigKeys.COLOR_BACKGROUND,"255,255,255");
		loadProperty(properties, TGConfigKeys.COLOR_LINE,"214,214,214");
		loadProperty(properties, TGConfigKeys.COLOR_SCORE_NOTE,"64,64,64");
		loadProperty(properties, TGConfigKeys.COLOR_TAB_NOTE,"64,64,64");
		loadProperty(properties, TGConfigKeys.COLOR_PLAY_NOTE,"255,0,0");
		loadProperty(properties, TGConfigKeys.COLOR_CARET_1,"127,127,127");
		loadProperty(properties, TGConfigKeys.COLOR_CARET_2,"165,42,42");
		loadProperty(properties, TGConfigKeys.COLOR_LOOP_S_MARKER,"42,165,42");
		loadProperty(properties, TGConfigKeys.COLOR_LOOP_E_MARKER,"165,42,42");
		loadProperty(properties, TGConfigKeys.MAX_HISTORY_FILES,10);
		loadProperty(properties, TGConfigKeys.MIN_SCORE_TABLATURE_SPACING,20);
		loadProperty(properties, TGConfigKeys.SCORE_LINE_SPACING,8);
		loadProperty(properties, TGConfigKeys.TAB_LINE_SPACING,10);
		loadProperty(properties, TGConfigKeys.FIRST_TRACK_SPACING,20);
		loadProperty(properties, TGConfigKeys.TRACK_SPACING,10);
		loadProperty(properties, TGConfigKeys.LANGUAGE,"");
		loadProperty(properties, TGConfigKeys.FRETBOARD_STRING_SPACING,20);
		loadProperty(properties, TGConfigKeys.FRETBOARD_STYLE ,TGFretBoardConfig.DISPLAY_TEXT_SCALE);
		loadProperty(properties, TGConfigKeys.FRETBOARD_DIRECTION ,TGFretBoardConfig.DIRECTION_RIGHT);
		loadProperty(properties, TGConfigKeys.FRETBOARD_FONT, (DEFAULT_FONT_NAME + ",8,1"));
		loadProperty(properties, TGConfigKeys.FRETBOARD_COLOR_BACKGROUND,"0,0,0");
		loadProperty(properties, TGConfigKeys.FRETBOARD_COLOR_STRING,"227,217,217");
		loadProperty(properties, TGConfigKeys.FRETBOARD_COLOR_FRET_POINT,"192,192,192");
		loadProperty(properties, TGConfigKeys.FRETBOARD_COLOR_NOTE,"42,85,128");
		loadProperty(properties, TGConfigKeys.FRETBOARD_COLOR_SCALE,"128,32,32");
		loadProperty(properties, TGConfigKeys.PIANO_COLOR_KEY_NATURAL,"255,255,255");
		loadProperty(properties, TGConfigKeys.PIANO_COLOR_KEY_NOT_NATURAL,"0,0,0");
		loadProperty(properties, TGConfigKeys.PIANO_COLOR_NOTE,"42,85,128");
		loadProperty(properties, TGConfigKeys.PIANO_COLOR_SCALE,"128,32,32");
		loadProperty(properties, TGConfigKeys.MATRIX_FONT, (DEFAULT_FONT_NAME + ",8,1"));
		loadProperty(properties, TGConfigKeys.MATRIX_COLOR_FOREGROUND,"190,190,190");
		loadProperty(properties, TGConfigKeys.MATRIX_COLOR_BORDER,"25,25,25");
		loadProperty(properties, TGConfigKeys.MATRIX_COLOR_POSITION,"190,190,190");
		loadProperty(properties, TGConfigKeys.MATRIX_COLOR_NOTE,"42,85,128");
		loadProperty(properties, TGConfigKeys.MATRIX_COLOR_PLAY_NOTE,"128,32,32");
		loadProperty(properties, TGConfigKeys.MATRIX_COLOR_LINE_1,"255,255,255");
		loadProperty(properties, TGConfigKeys.MATRIX_COLOR_LINE_2,"230,230,250");
		loadProperty(properties, TGConfigKeys.MATRIX_COLOR_LINE_3,"173,216,230");
		loadProperty(properties, TGConfigKeys.TABLE_AUTO_SIZE,true);
		loadProperty(properties, TGConfigKeys.BROWSER_LINES_VISIBLE,true);
	}
	
	private static void loadProperty(TGProperties properties, String key,String value){
		properties.setValue(key,value);
	}
	
	private static void loadProperty(TGProperties properties, String key,int value){
		properties.setValue(key,Integer.toString(value));
	}
	
	private static void loadProperty(TGProperties properties, String key,boolean value){
		properties.setValue(key,Boolean.toString(value));
	}
	
	private static String getDefaultFontName(){
		Font font = TuxGuitar.getInstance().getDisplay().getSystemFont();
		if( font != null ){
			FontData[] fd = font.getFontData();
			if(fd != null && fd.length > 0){
				return fd[0].getName();
			}
		}
		return new String();
	}
}
