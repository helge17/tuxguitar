/*
 * Created on 27-ene-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.system.config;

import java.io.InputStream;
import java.util.Properties;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.fretboard.FretBoardConfig;
import org.herac.tuxguitar.gui.editors.tab.edit.EditorKit;
import org.herac.tuxguitar.gui.editors.tab.layout.ViewLayout;
import org.herac.tuxguitar.gui.util.TGFileUtils;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TGConfigDefaults{
	
	private static final String DEFAULT_FILE = "config.dist";
	
	private static final String DEFAULT_FONT_NAME = getDefaultFontName();
	
	private Properties properties;
	
	public TGConfigDefaults(){
		this.properties = new Properties();
		this.loadDefaults();
		this.loadDefaultFile();
	}
	
	public void loadDefaultFile() {
		try {
			InputStream is = TGFileUtils.getResourceAsStream(DEFAULT_FILE);
			if(is != null){
				this.properties.load(is);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadDefaults(){
		this.loadProperty(TGConfigKeys.SKIN,"Lavender");
		this.loadProperty(TGConfigKeys.WINDOW_TITLE,"${appname} - ${filename}");
		this.loadProperty(TGConfigKeys.SHOW_SPLASH,true);
		this.loadProperty(TGConfigKeys.MAXIMIZED,false);
		this.loadProperty(TGConfigKeys.WIDTH,800);
		this.loadProperty(TGConfigKeys.HEIGHT,600);
		this.loadProperty(TGConfigKeys.SHOW_MIXER,false);
		this.loadProperty(TGConfigKeys.SHOW_TRANSPORT,false);
		this.loadProperty(TGConfigKeys.SHOW_FRETBOARD,false);
		this.loadProperty(TGConfigKeys.SHOW_PIANO,false);
		this.loadProperty(TGConfigKeys.SHOW_MARKERS,false);
		this.loadProperty(TGConfigKeys.LAYOUT_MODE,ViewLayout.MODE_PAGE);
		this.loadProperty(TGConfigKeys.LAYOUT_STYLE,(ViewLayout.DISPLAY_TABLATURE | ViewLayout.DISPLAY_SCORE | ViewLayout.DISPLAY_COMPACT | ViewLayout.DISPLAY_CHORD_DIAGRAM));
		this.loadProperty(TGConfigKeys.EDITOR_MOUSE_MODE,EditorKit.MOUSE_MODE_EDITION);
		this.loadProperty(TGConfigKeys.EDITOR_NATURAL_KEY_MODE,true);
		this.loadProperty(TGConfigKeys.FONT_DEFAULT, (DEFAULT_FONT_NAME + ",6,0"));
		this.loadProperty(TGConfigKeys.FONT_NOTE, (DEFAULT_FONT_NAME + ",7,0"));
		this.loadProperty(TGConfigKeys.FONT_TIME_SIGNATURE, (DEFAULT_FONT_NAME + ",13,1"));
		this.loadProperty(TGConfigKeys.FONT_LYRIC, (DEFAULT_FONT_NAME + ",7,0"));
		this.loadProperty(TGConfigKeys.FONT_TEXT, (DEFAULT_FONT_NAME + ",8,0"));
		this.loadProperty(TGConfigKeys.FONT_CHORD, (DEFAULT_FONT_NAME + ",7,0"));
		this.loadProperty(TGConfigKeys.FONT_CHORD_FRET, (DEFAULT_FONT_NAME + ",5,0"));
		this.loadProperty(TGConfigKeys.FONT_GRACE, (DEFAULT_FONT_NAME + ",6,0"));
		this.loadProperty(TGConfigKeys.FONT_MARKER, (DEFAULT_FONT_NAME + ",7,0"));
		this.loadProperty(TGConfigKeys.FONT_PRINTER_DEFAULT, (DEFAULT_FONT_NAME + ",6,0"));
		this.loadProperty(TGConfigKeys.FONT_PRINTER_NOTE, (DEFAULT_FONT_NAME + ",6,0"));
		this.loadProperty(TGConfigKeys.FONT_PRINTER_TIME_SIGNATURE, (DEFAULT_FONT_NAME + ",10,1"));
		this.loadProperty(TGConfigKeys.FONT_PRINTER_LYRIC, (DEFAULT_FONT_NAME + ",7,0"));
		this.loadProperty(TGConfigKeys.FONT_PRINTER_TEXT, (DEFAULT_FONT_NAME + ",8,0"));
		this.loadProperty(TGConfigKeys.FONT_PRINTER_CHORD, (DEFAULT_FONT_NAME + ",7,0"));
		this.loadProperty(TGConfigKeys.FONT_PRINTER_GRACE, (DEFAULT_FONT_NAME + ",6,0"));
		this.loadProperty(TGConfigKeys.FONT_ABOUT_DIALOG_TITLE, (DEFAULT_FONT_NAME + ",36,3"));
		this.loadProperty(TGConfigKeys.COLOR_BACKGROUND,"255,255,255");
		this.loadProperty(TGConfigKeys.COLOR_LINE,"214,214,214");
		this.loadProperty(TGConfigKeys.COLOR_SCORE_NOTE,"64,64,64");
		this.loadProperty(TGConfigKeys.COLOR_TAB_NOTE,"64,64,64");
		this.loadProperty(TGConfigKeys.COLOR_PLAY_NOTE,"255,0,0");
		this.loadProperty(TGConfigKeys.COLOR_CARET_1,"127,127,127");
		this.loadProperty(TGConfigKeys.COLOR_CARET_2,"165,42,42");
		this.loadProperty(TGConfigKeys.COLOR_LOOP_S_MARKER,"42,165,42");
		this.loadProperty(TGConfigKeys.COLOR_LOOP_E_MARKER,"165,42,42");
		this.loadProperty(TGConfigKeys.MAX_HISTORY_FILES,10);
		this.loadProperty(TGConfigKeys.MIN_SCORE_TABLATURE_SPACING,20);
		this.loadProperty(TGConfigKeys.SCORE_LINE_SPACING,8);
		this.loadProperty(TGConfigKeys.TAB_LINE_SPACING,10);
		this.loadProperty(TGConfigKeys.FIRST_TRACK_SPACING,20);
		this.loadProperty(TGConfigKeys.TRACK_SPACING,10);
		this.loadProperty(TGConfigKeys.LANGUAGE,"");
		this.loadProperty(TGConfigKeys.FRETBOARD_STRING_SPACING,20);
		this.loadProperty(TGConfigKeys.FRETBOARD_STYLE ,FretBoardConfig.DISPLAY_TEXT_SCALE);
		this.loadProperty(TGConfigKeys.FRETBOARD_DIRECTION ,FretBoardConfig.DIRECTION_RIGHT);
		this.loadProperty(TGConfigKeys.FRETBOARD_FONT, (DEFAULT_FONT_NAME + ",8,1"));
		this.loadProperty(TGConfigKeys.FRETBOARD_COLOR_BACKGROUND,"0,0,0");
		this.loadProperty(TGConfigKeys.FRETBOARD_COLOR_STRING,"227,217,217");
		this.loadProperty(TGConfigKeys.FRETBOARD_COLOR_FRET_POINT,"192,192,192");
		this.loadProperty(TGConfigKeys.FRETBOARD_COLOR_NOTE,"42,85,128");
		this.loadProperty(TGConfigKeys.FRETBOARD_COLOR_SCALE,"128,32,32");
		this.loadProperty(TGConfigKeys.PIANO_COLOR_KEY_NATURAL,"255,255,255");
		this.loadProperty(TGConfigKeys.PIANO_COLOR_KEY_NOT_NATURAL,"0,0,0");
		this.loadProperty(TGConfigKeys.PIANO_COLOR_NOTE,"42,85,128");
		this.loadProperty(TGConfigKeys.PIANO_COLOR_SCALE,"128,32,32");
		this.loadProperty(TGConfigKeys.MATRIX_FONT, (DEFAULT_FONT_NAME + ",8,1"));
		this.loadProperty(TGConfigKeys.MATRIX_COLOR_FOREGROUND,"190,190,190");
		this.loadProperty(TGConfigKeys.MATRIX_COLOR_BORDER,"25,25,25");
		this.loadProperty(TGConfigKeys.MATRIX_COLOR_POSITION,"190,190,190");
		this.loadProperty(TGConfigKeys.MATRIX_COLOR_NOTE,"42,85,128");
		this.loadProperty(TGConfigKeys.MATRIX_COLOR_PLAY_NOTE,"128,32,32");
		this.loadProperty(TGConfigKeys.MATRIX_COLOR_LINE_1,"255,255,255");
		this.loadProperty(TGConfigKeys.MATRIX_COLOR_LINE_2,"230,230,250");
		this.loadProperty(TGConfigKeys.MATRIX_COLOR_LINE_3,"173,216,230");
		this.loadProperty(TGConfigKeys.TABLE_AUTO_SIZE,true);
		this.loadProperty(TGConfigKeys.BROWSER_LINES_VISIBLE,true);
	}
	
	public Properties getProperties() {
		return this.properties;
	}
	
	private void loadProperty(String key,String value){
		this.properties.setProperty(key,value);
	}
	
	private void loadProperty(String key,int value){
		this.properties.setProperty(key,Integer.toString(value));
	}
	
	private void loadProperty(String key,boolean value){
		this.properties.setProperty(key,Boolean.toString(value));
	}
	
	private static String getDefaultFontName(){
		Font font = TuxGuitar.instance().getDisplay().getSystemFont();
		if( font != null ){
			FontData[] fd = font.getFontData();
			if(fd != null && fd.length > 0){
				return fd[0].getName();
			}
		}
		return new String();
	}
	
}
