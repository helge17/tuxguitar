package app.tuxguitar.app.system.config;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.view.component.tab.edit.EditorKit;
import app.tuxguitar.app.view.dialog.fretboard.TGFretBoardConfig;
import app.tuxguitar.graphics.control.TGLayout;
import app.tuxguitar.ui.resource.UIFontModel;
import app.tuxguitar.util.properties.TGProperties;
import app.tuxguitar.util.properties.TGPropertiesManager;
import app.tuxguitar.util.properties.TGPropertiesUtil;

public class TGConfigDefaults{

	public static final String RESOURCE = "config-defaults";
	public static final String MODULE = "tuxguitar";

	private static List<String> keys = new ArrayList<String>();

	public static final String DEFAULT_SKIN = "Oxygen";
	private static final String DEFAULT_FONT_NAME = UIFontModel.DEFAULT_NAME;

	public static TGProperties createDefaults(){
		TGPropertiesManager propertiesManager = TuxGuitar.getInstance().getPropertiesManager();
		TGProperties properties = propertiesManager.createProperties();
		TGConfigDefaults.loadProperties( properties );
		propertiesManager.readProperties(properties, RESOURCE, MODULE);

		return properties;
	}

	public static void loadProperties(TGProperties properties){
		loadProperty(properties, TGConfigKeys.SKIN, DEFAULT_SKIN);
		loadProperty(properties, TGConfigKeys.WINDOW_TITLE, "${appname} - ${filename}");
		loadProperty(properties, TGConfigKeys.SHOW_SPLASH, true);
		loadProperty(properties, TGConfigKeys.MAXIMIZED, false);
		loadProperty(properties, TGConfigKeys.WIDTH, 1200);
		loadProperty(properties, TGConfigKeys.HEIGHT, 675);
		loadProperty(properties, TGConfigKeys.SHOW_INSTRUMENTS, false);
		loadProperty(properties, TGConfigKeys.SHOW_TRANSPORT, false);
		loadProperty(properties, TGConfigKeys.SHOW_FRETBOARD, false);
		loadProperty(properties, TGConfigKeys.SHOW_MATRIX, false);
		loadProperty(properties, TGConfigKeys.SHOW_PIANO, false);
		loadProperty(properties, TGConfigKeys.SHOW_MARKERS, false);
		loadProperty(properties, TGConfigKeys.SHOW_MAIN_TOOLBAR, true);
		loadProperty(properties, TGConfigKeys.SHOW_EDIT_TOOLBAR, true);
		loadProperty(properties, TGConfigKeys.MAIN_TOOLBAR_NAME, "");
		loadProperty(properties, TGConfigKeys.SHOW_TRACKS, true);
		loadProperty(properties, TGConfigKeys.LAYOUT_MODE, TGLayout.MODE_VERTICAL);
		loadProperty(properties, TGConfigKeys.LAYOUT_STYLE, (TGLayout.DISPLAY_TABLATURE | TGLayout.DISPLAY_SCORE | TGLayout.DISPLAY_COMPACT | TGLayout.DISPLAY_CHORD_DIAGRAM | TGLayout.HIGHLIGHT_PLAYED_BEAT));
		loadProperty(properties, TGConfigKeys.LANGUAGE, "");
		loadProperty(properties, TGConfigKeys.EDITOR_MOUSE_MODE, EditorKit.MOUSE_MODE_SELECTION);
		loadProperty(properties, TGConfigKeys.EDITOR_NATURAL_KEY_MODE, true);
		loadProperty(properties, TGConfigKeys.FONT_DEFAULT, (DEFAULT_FONT_NAME + ",6,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_NOTE, (DEFAULT_FONT_NAME + ",7,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_LYRIC, (DEFAULT_FONT_NAME + ",7,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_TEXT, (DEFAULT_FONT_NAME + ",8,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_CHORD, (DEFAULT_FONT_NAME + ",7,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_CHORD_FRET, (DEFAULT_FONT_NAME + ",5,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_GRACE, (DEFAULT_FONT_NAME + ",6,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_MARKER, (DEFAULT_FONT_NAME + ",7,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_PRINTER_DEFAULT, (DEFAULT_FONT_NAME + ",6,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_PRINTER_NOTE, (DEFAULT_FONT_NAME + ",6,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_PRINTER_LYRIC, (DEFAULT_FONT_NAME + ",7,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_PRINTER_TEXT, (DEFAULT_FONT_NAME + ",8,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_PRINTER_CHORD, (DEFAULT_FONT_NAME + ",7,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_PRINTER_GRACE, (DEFAULT_FONT_NAME + ",6,false,false"));
		loadProperty(properties, TGConfigKeys.FONT_ABOUT_DIALOG_TITLE, (DEFAULT_FONT_NAME + ",36,true,true"));
		loadProperty(properties, TGConfigKeys.FONT_MAINTOOLBAR_TIMESTAMP, ("Monospace,16.0,false,false"));
		loadProperty(properties, TGConfigKeys.COLOR_FOREGROUND, "0,0,0");
		loadProperty(properties, TGConfigKeys.COLOR_BACKGROUND, "255,255,255");
		loadProperty(properties, TGConfigKeys.COLOR_BACKGROUND_PLAYING, "255,242,242");
		loadProperty(properties, TGConfigKeys.COLOR_LINE, "214,214,214");
		loadProperty(properties, TGConfigKeys.COLOR_LINE_INVALID, "205,0,0");
		loadProperty(properties, TGConfigKeys.COLOR_SCORE_NOTE, "64,64,64");
		loadProperty(properties, TGConfigKeys.COLOR_TAB_NOTE, "64,64,64");
		loadProperty(properties, TGConfigKeys.COLOR_PLAY_NOTE, "255,0,0");
		loadProperty(properties, TGConfigKeys.COLOR_SELECTION, "116,152,208");
		loadProperty(properties, TGConfigKeys.COLOR_CARET_CURRENT_VOICE, "5,5,5");
		loadProperty(properties, TGConfigKeys.COLOR_CARET_OTHER_VOICE, "200,10,10");
		loadProperty(properties, TGConfigKeys.COLOR_CARET_ALPHA, "12");
		loadProperty(properties, TGConfigKeys.COLOR_LOOP_S_MARKER, "42,165,42");
		loadProperty(properties, TGConfigKeys.COLOR_LOOP_E_MARKER, "165,42,42");
		loadProperty(properties, TGConfigKeys.COLOR_MEASURE_NUMBER, "128,0,0");
		loadProperty(properties, TGConfigKeys.MIDI_PORT, "tuxguitar-synth.port");
		loadProperty(properties, TGConfigKeys.MIDI_SEQUENCER, "tuxguitar.sequencer");
		loadProperty(properties, TGConfigKeys.MAX_HISTORY_FILES, 10);
		loadProperty(properties, TGConfigKeys.FRETBOARD_STRING_SPACING, 20);
		loadProperty(properties, TGConfigKeys.FRETBOARD_STYLE, TGFretBoardConfig.DISPLAY_TEXT_SCALE);
		loadProperty(properties, TGConfigKeys.FRETBOARD_FONT, (DEFAULT_FONT_NAME + ",8,true,false"));
		loadProperty(properties, TGConfigKeys.FRETBOARD_COLOR_BACKGROUND, "0,0,0");
		loadProperty(properties, TGConfigKeys.FRETBOARD_COLOR_STRING, "227,217,217");
		loadProperty(properties, TGConfigKeys.FRETBOARD_COLOR_FRET_POINT, "192,192,192");
		loadProperty(properties, TGConfigKeys.FRETBOARD_COLOR_NOTE, "42,85,128");
		loadProperty(properties, TGConfigKeys.FRETBOARD_COLOR_SCALE, "128,32,32");
		loadProperty(properties, TGConfigKeys.FRETBOARD_DIRECTION, TGFretBoardConfig.DIRECTION_RIGHT);
		loadProperty(properties, TGConfigKeys.PIANO_COLOR_KEY_NATURAL, "255,255,255");
		loadProperty(properties, TGConfigKeys.PIANO_COLOR_KEY_NOT_NATURAL, "0,0,0");
		loadProperty(properties, TGConfigKeys.PIANO_COLOR_NOTE, "42,85,128");
		loadProperty(properties, TGConfigKeys.PIANO_COLOR_SCALE, "128,32,32");
		loadProperty(properties, TGConfigKeys.MATRIX_GRIDS, 2);
		loadProperty(properties, TGConfigKeys.MATRIX_FONT, (DEFAULT_FONT_NAME + ",8,true,false"));
		loadProperty(properties, TGConfigKeys.MATRIX_COLOR_FOREGROUND, "0,0,0");
		loadProperty(properties, TGConfigKeys.MATRIX_COLOR_LINE_1, "255,255,255");
		loadProperty(properties, TGConfigKeys.MATRIX_COLOR_LINE_2, "230,230,250");
		loadProperty(properties, TGConfigKeys.MATRIX_COLOR_LINE_3, "173,216,230");
		loadProperty(properties, TGConfigKeys.MATRIX_COLOR_BORDER, "214,214,214");
		loadProperty(properties, TGConfigKeys.MATRIX_COLOR_POSITION, "0,0,0");
		loadProperty(properties, TGConfigKeys.MATRIX_COLOR_NOTE, "42,85,128");
		loadProperty(properties, TGConfigKeys.MATRIX_COLOR_PLAY_NOTE, "128,32,32");
		loadProperty(properties, TGConfigKeys.TABLE_AUTO_SIZE, true);

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
		loadProperty(properties, TGConfigKeys.STYLE_CHORD_LINE_WIDTH, 0);
		loadProperty(properties, TGConfigKeys.STYLE_REPEAT_ENDING_SPACING, 20);
		loadProperty(properties, TGConfigKeys.STYLE_EFFECT_SPACING, 8);
		loadProperty(properties, TGConfigKeys.STYLE_DIVISION_TYPE_SPACING, 10);
		loadProperty(properties, TGConfigKeys.STYLE_PICK_STROKE_SPACING, 8);
		loadProperty(properties, TGConfigKeys.STYLE_BEND_SPACING, 8);
		loadProperty(properties, TGConfigKeys.STYLE_TEXT_SPACING, 15);
		loadProperty(properties, TGConfigKeys.STYLE_MARKER_SPACING, 15);
		loadProperty(properties, TGConfigKeys.STYLE_LOOP_MARKER_SPACING, 5);
		loadProperty(properties, TGConfigKeys.STYLE_LINE_WIDTHS, new float[] {0f, 1f, 2f, 3f, 4f, 5f});
		loadProperty(properties, TGConfigKeys.STYLE_DURATION_WIDTHS, new float[] {30f, 25f, 21f, 20f, 19f,18f});

		loadProperty(properties, TGConfigKeys.HOMEPAGE_URL, "https://tuxguitar.app");
		loadProperty(properties, TGConfigKeys.CONFIG_APP_VERSION, "");
	}

	public static List<String> getKeys() {
		if (keys.isEmpty()) {
			loadProperties(null);
		}
		return keys;
	}

	private static void loadProperty(TGProperties properties, String key,String value){
		if (properties != null) {
			properties.setValue(key,value);
		}
		keys.add(key);
	}

	private static void loadProperty(TGProperties properties, String key,int value){
		if (properties != null) {
			properties.setValue(key,Integer.toString(value));
		}
		keys.add(key);
	}

	private static void loadProperty(TGProperties properties, String key,boolean value){
		if (properties != null) {
			properties.setValue(key,Boolean.toString(value));
		}
		keys.add(key);
	}

	private static void loadProperty(TGProperties properties, String key, float[] value) {
		if (properties != null) {
			TGPropertiesUtil.setValue(properties, key, value);
		}
		keys.add(key);
	}

}
