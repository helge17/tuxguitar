package org.herac.tuxguitar.app.system.config;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.view.component.tab.edit.EditorKit;
import org.herac.tuxguitar.app.view.dialog.fretboard.TGFretBoardConfig;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.ui.resource.UIFontModel;
import org.herac.tuxguitar.util.properties.TGProperties;
import org.herac.tuxguitar.util.properties.TGPropertiesManager;
import org.herac.tuxguitar.util.properties.TGPropertiesUtil;

public class TGConfigDefaults{
	
	public static final String RESOURCE = "config-defaults";
	public static final String MODULE = "tuxguitar";
	
	private static final String DEFAULT_FONT_NAME = UIFontModel.DEFAULT_NAME;
	
	public static TGProperties createDefaults(){
		TGPropertiesManager propertiesManager = TuxGuitar.getInstance().getPropertiesManager();
		TGProperties properties = propertiesManager.createProperties();
		TGConfigDefaults.loadProperties( properties );
		propertiesManager.readProperties(properties, RESOURCE, MODULE);
		
		return properties;
	}
	
	public static void loadProperties(TGProperties properties){
		loadProperty(properties, TGConfigKeys.SKIN, "Lavender");
		loadProperty(properties, TGConfigKeys.WINDOW_TITLE, "${appname} - ${filename}");
		loadProperty(properties, TGConfigKeys.SHOW_SPLASH, true);
		loadProperty(properties, TGConfigKeys.MAXIMIZED, false);
		loadProperty(properties, TGConfigKeys.WIDTH, 960);
		loadProperty(properties, TGConfigKeys.HEIGHT, 600);
		loadProperty(properties, TGConfigKeys.SHOW_MAIN_TOOLBAR, true);
		loadProperty(properties, TGConfigKeys.SHOW_EDIT_TOOLBAR, true);
		loadProperty(properties, TGConfigKeys.SHOW_INSTRUMENTS, false);
		loadProperty(properties, TGConfigKeys.SHOW_TRANSPORT, false);
		loadProperty(properties, TGConfigKeys.SHOW_FRETBOARD, false);
		loadProperty(properties, TGConfigKeys.SHOW_PIANO, false);
		loadProperty(properties, TGConfigKeys.SHOW_MARKERS, false);
		loadProperty(properties, TGConfigKeys.LAYOUT_MODE, TGLayout.MODE_VERTICAL);
		loadProperty(properties, TGConfigKeys.LAYOUT_STYLE, (TGLayout.DISPLAY_TABLATURE | TGLayout.DISPLAY_SCORE | TGLayout.DISPLAY_COMPACT | TGLayout.DISPLAY_CHORD_DIAGRAM));
		loadProperty(properties, TGConfigKeys.EDITOR_MOUSE_MODE, EditorKit.MOUSE_MODE_SELECTION);
		loadProperty(properties, TGConfigKeys.EDITOR_NATURAL_KEY_MODE,true);
		loadProperty(properties, TGConfigKeys.FONT_DEFAULT, (DEFAULT_FONT_NAME + ",6,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_NOTE, (DEFAULT_FONT_NAME + ",7,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_TIME_SIGNATURE, (DEFAULT_FONT_NAME + ",13,true,false"));
		loadProperty(properties, TGConfigKeys.FONT_LYRIC, (DEFAULT_FONT_NAME + ",7,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_TEXT, (DEFAULT_FONT_NAME + ",8,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_CHORD, (DEFAULT_FONT_NAME + ",7,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_CHORD_FRET, (DEFAULT_FONT_NAME + ",5,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_GRACE, (DEFAULT_FONT_NAME + ",6,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_MARKER, (DEFAULT_FONT_NAME + ",7,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_PRINTER_DEFAULT, (DEFAULT_FONT_NAME + ",6,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_PRINTER_NOTE, (DEFAULT_FONT_NAME + ",6,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_PRINTER_TIME_SIGNATURE, (DEFAULT_FONT_NAME + ",10,true,false"));
		loadProperty(properties, TGConfigKeys.FONT_PRINTER_LYRIC, (DEFAULT_FONT_NAME + ",7,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_PRINTER_TEXT, (DEFAULT_FONT_NAME + ",8,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_PRINTER_CHORD, (DEFAULT_FONT_NAME + ",7,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_PRINTER_GRACE, (DEFAULT_FONT_NAME + ",6,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_ABOUT_DIALOG_TITLE, (DEFAULT_FONT_NAME + ",36,true,true"));
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
		loadProperty(properties, TGConfigKeys.LANGUAGE,"");
		loadProperty(properties, TGConfigKeys.FRETBOARD_STRING_SPACING,20);
		loadProperty(properties, TGConfigKeys.FRETBOARD_STYLE ,TGFretBoardConfig.DISPLAY_TEXT_SCALE);
		loadProperty(properties, TGConfigKeys.FRETBOARD_DIRECTION ,TGFretBoardConfig.DIRECTION_RIGHT);
		loadProperty(properties, TGConfigKeys.FRETBOARD_FONT, (DEFAULT_FONT_NAME + ",8,true,false"));
		loadProperty(properties, TGConfigKeys.FRETBOARD_COLOR_BACKGROUND,"0,0,0");
		loadProperty(properties, TGConfigKeys.FRETBOARD_COLOR_STRING,"227,217,217");
		loadProperty(properties, TGConfigKeys.FRETBOARD_COLOR_FRET_POINT,"192,192,192");
		loadProperty(properties, TGConfigKeys.FRETBOARD_COLOR_NOTE,"42,85,128");
		loadProperty(properties, TGConfigKeys.FRETBOARD_COLOR_SCALE,"128,32,32");
		loadProperty(properties, TGConfigKeys.PIANO_COLOR_KEY_NATURAL,"255,255,255");
		loadProperty(properties, TGConfigKeys.PIANO_COLOR_KEY_NOT_NATURAL,"0,0,0");
		loadProperty(properties, TGConfigKeys.PIANO_COLOR_NOTE,"42,85,128");
		loadProperty(properties, TGConfigKeys.PIANO_COLOR_SCALE,"128,32,32");
		loadProperty(properties, TGConfigKeys.MATRIX_FONT, (DEFAULT_FONT_NAME + ",8,true,false"));
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
		
		loadProperty(properties, TGConfigKeys.STYLE_MIN_BUFFER_SEPARATOR, 20);
		loadProperty(properties, TGConfigKeys.STYLE_MIN_TOP_SPACING, 30);
		loadProperty(properties, TGConfigKeys.STYLE_MIN_SCORE_TAB_SPACING, 20);
		loadProperty(properties, TGConfigKeys.STYLE_STRING_SPACING, 10);
		loadProperty(properties, TGConfigKeys.STYLE_SCORE_LINE_SPACING, 8);
		loadProperty(properties, TGConfigKeys.STYLE_TRACK_SPACING, 10);
		loadProperty(properties, TGConfigKeys.STYLE_FIRST_TRACK_SPACING, 20);
		loadProperty(properties, TGConfigKeys.STYLE_FIRST_MEASURE_SPACING, 20);
		loadProperty(properties, TGConfigKeys.STYLE_FIRST_NOTE_SPACING, 10);
		loadProperty(properties, TGConfigKeys.STYLE_MEASURE_LEFT_SPACING, 15);
		loadProperty(properties, TGConfigKeys.STYLE_MEASURE_RIGHT_SPACING, 15);
		loadProperty(properties, TGConfigKeys.STYLE_CLEF_SPACING, 30);
		loadProperty(properties, TGConfigKeys.STYLE_KEY_SIGNATURE_SPACING, 15);
		loadProperty(properties, TGConfigKeys.STYLE_TIME_SIGNATURE_SPACING, 15);
		loadProperty(properties, TGConfigKeys.STYLE_CHORD_FRET_INDEX_SPACING, 8);
		loadProperty(properties, TGConfigKeys.STYLE_CHORD_STRING_SPACING, 5);
		loadProperty(properties, TGConfigKeys.STYLE_CHORD_FRET_SPACING, 6);
		loadProperty(properties, TGConfigKeys.STYLE_CHORD_NOTE_SIZE, 4);
		loadProperty(properties, TGConfigKeys.STYLE_CHORD_LINE_WIDTH, 1);
		loadProperty(properties, TGConfigKeys.STYLE_REPEAT_ENDING_SPACING, 20);
		loadProperty(properties, TGConfigKeys.STYLE_EFFECT_SPACING, 8);
		loadProperty(properties, TGConfigKeys.STYLE_DIVISION_TYPE_SPACING, 10);
		loadProperty(properties, TGConfigKeys.STYLE_TEXT_SPACING, 15);
		loadProperty(properties, TGConfigKeys.STYLE_MARKER_SPACING, 15);
		loadProperty(properties, TGConfigKeys.STYLE_LOOP_MARKER_SPACING, 5);
		loadProperty(properties, TGConfigKeys.STYLE_LINE_WIDTHS, new float[] {0f, 1f, 2f, 3f, 4f, 5f});
		loadProperty(properties, TGConfigKeys.STYLE_DURATION_WIDTHS, new float[] {30f, 25f, 21f, 20f, 19f,18f});
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
	
	private static void loadProperty(TGProperties properties, String key, float[] value) {
		TGPropertiesUtil.setValue(properties, key, value);
	}
}
