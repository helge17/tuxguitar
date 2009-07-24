package org.herac.tuxguitar.gui.editors.tab;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Resource;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.TGPainter;
import org.herac.tuxguitar.gui.editors.tab.layout.ViewLayout;
import org.herac.tuxguitar.gui.editors.tab.painters.TGNotePainter;
import org.herac.tuxguitar.gui.editors.tab.painters.TGTempoPainter;
import org.herac.tuxguitar.gui.editors.tab.painters.TGTripletFeelPainter;
import org.herac.tuxguitar.gui.system.config.TGConfigKeys;
import org.herac.tuxguitar.gui.util.ImageUtils;
import org.herac.tuxguitar.song.models.TGDuration;

public class TGResources {
	
	private static final int SCORE_NOTE_EMPTY_NORMAL_MODE = 0;
	private static final int SCORE_NOTE_EMPTY_PLAY_MODE = 1;
	private static final int SCORE_NOTE_FULL_NORMAL_MODE = 2;
	private static final int SCORE_NOTE_FULL_PLAY_MODE = 3;
	
	private ViewLayout layout;
	private List resources;
	private Font defaultFont;
	private Font noteFont;
	private Font timeSignatureFont;
	private Font lyricFont;
	private Font textFont;
	private Font markerFont;
	private Font graceFont;
	private Font chordFont;
	private Font chordFretFont;
	private Font printerDefaultFont;
	private Font printerNoteFont;
	private Font printerTimeSignatureFont;
	private Font printerLyricFont;
	private Font printerTextFont;
	private Font printerGraceFont;
	private Font printerChordFont;
	private Color backgroundColor;
	private Color lineColor;
	private Color scoreNoteColor;
	private Color tabNoteColor;
	private Color playNoteColor;
	private Color colorWhite;
	private Color colorBlack;
	private Color colorRed;
	private Color caretColor1;
	private Color caretColor2;
	private Color loopSMarkerColor;
	private Color loopEMarkerColor;
	private Image[] scoreNotes;
	private Image[] harmonicNotes;
	private Image tempoImage;
	private Image tripletFeel8;
	private Image tripletFeelNone8;
	private Image tripletFeel16;
	private Image tripletFeelNone16;
	
	private int scoreNoteWidth;
	
	public TGResources(ViewLayout layout){
		this.layout = layout;
		this.resources = new ArrayList();
	}
	
	public void load(){
		this.dispose();
		this.initFonts();
		this.initColors();
		this.initImages();
	}
	
	public ViewLayout getLayout(){
		return this.layout;
	}
	
	public Font getDefaultFont() {
		return this.defaultFont;
	}
	
	public Font getNoteFont() {
		return this.noteFont;
	}
	
	public Font getTimeSignatureFont() {
		return this.timeSignatureFont;
	}
	
	public Font getLyricFont(){
		return this.lyricFont;
	}
	
	public Font getTextFont(){
		return this.textFont;
	}
	
	public Font getMarkerFont(){
		return this.markerFont;
	}
	
	public Font getChordFont(){
		return this.chordFont;
	}
	
	public Font getChordFretFont(){
		return this.chordFretFont;
	}
	
	public Font getGraceFont() {
		return this.graceFont;
	}
	
	public Font getPrinterDefaultFont() {
		return this.printerDefaultFont;
	}
	
	public Font getPrinterNoteFont() {
		return this.printerNoteFont;
	}
	
	public Font getPrinterTimeSignatureFont() {
		return this.printerTimeSignatureFont;
	}
	
	public Font getPrinterLyricFont() {
		return this.printerLyricFont;
	}
	
	public Font getPrinterTextFont() {
		return this.printerTextFont;
	}
	
	public Font getPrinterGraceFont() {
		return this.printerGraceFont;
	}
	
	public Font getPrinterChordFont() {
		return this.printerChordFont;
	}
	
	public Color getBackgroundColor() {
		return this.backgroundColor;
	}
	
	public Color getLineColor() {
		return this.lineColor;
	}
	
	public Color getPlayNoteColor() {
		return this.playNoteColor;
	}
	
	public Color getScoreNoteColor() {
		return this.scoreNoteColor;
	}
	
	public Color getTabNoteColor() {
		return this.tabNoteColor;
	}
	
	public Color getColorWhite() {
		return this.colorWhite;
	}
	
	public Color getColorBlack() {
		return this.colorBlack;
	}
	
	public Color getColorRed() {
		return this.colorRed;
	}
	
	public Color getCaretColor1() {
		return this.caretColor1;
	}
	
	public Color getCaretColor2() {
		return this.caretColor2;
	}
	
	public Color getLoopSMarkerColor() {
		return this.loopSMarkerColor;
	}
	
	public Color getLoopEMarkerColor() {
		return this.loopEMarkerColor;
	}
	
	public Image getTempoImage() {
		return this.tempoImage;
	}
	
	public Image getTripletFeel8(){
		return this.tripletFeel8;
	}
	
	public Image getTripletFeelNone8(){
		return this.tripletFeelNone8;
	}
	
	public Image getTripletFeel16(){
		return this.tripletFeel16;
	}
	
	public Image getTripletFeelNone16(){
		return this.tripletFeelNone16;
	}
	
	public Image getScoreNote(int value,boolean playing) {
		int index = 0;
		index += ((playing)?1:0);
		index += ((value >= TGDuration.QUARTER)?2:0);
		return this.scoreNotes[index];
	}
	
	public Image getHarmonicNote(int value,boolean playing) {
		int index = 0;
		index += ((playing)?1:0);
		index += ((value >= TGDuration.QUARTER)?2:0);
		return this.harmonicNotes[index];
	}
	
	public int getScoreNoteWidth(){
		return this.scoreNoteWidth;
	}
	
	private void initFonts(){
		float scale = this.layout.getFontScale() ;
		this.defaultFont = getFont(TGConfigKeys.FONT_DEFAULT, scale);
		this.noteFont = getFont(TGConfigKeys.FONT_NOTE, scale);
		this.timeSignatureFont = getFont(TGConfigKeys.FONT_TIME_SIGNATURE, scale);
		this.lyricFont = getFont(TGConfigKeys.FONT_LYRIC, scale);
		this.textFont =  getFont(TGConfigKeys.FONT_TEXT, scale);
		this.markerFont =  getFont(TGConfigKeys.FONT_MARKER, scale);
		this.graceFont = getFont(TGConfigKeys.FONT_GRACE, scale);
		this.chordFont = getFont(TGConfigKeys.FONT_CHORD, scale);
		this.chordFretFont = getFont(TGConfigKeys.FONT_CHORD_FRET, scale);
		this.printerDefaultFont = getFont(TGConfigKeys.FONT_PRINTER_DEFAULT, scale);
		this.printerNoteFont = getFont(TGConfigKeys.FONT_PRINTER_NOTE, scale);
		this.printerTimeSignatureFont = getFont(TGConfigKeys.FONT_PRINTER_TIME_SIGNATURE, scale);
		this.printerLyricFont = getFont(TGConfigKeys.FONT_PRINTER_LYRIC, scale);
		this.printerTextFont =  getFont(TGConfigKeys.FONT_PRINTER_TEXT, scale);
		this.printerGraceFont = getFont(TGConfigKeys.FONT_PRINTER_GRACE, scale);
		this.printerChordFont = getFont(TGConfigKeys.FONT_PRINTER_CHORD, scale);
	}
	
	private void initColors(){
		this.backgroundColor = getColor(TGConfigKeys.COLOR_BACKGROUND);
		this.lineColor = getColor(TGConfigKeys.COLOR_LINE);
		this.scoreNoteColor = getColor(TGConfigKeys.COLOR_SCORE_NOTE);
		this.tabNoteColor = getColor(TGConfigKeys.COLOR_TAB_NOTE);
		this.playNoteColor = getColor(TGConfigKeys.COLOR_PLAY_NOTE);
		this.caretColor1 = getColor(TGConfigKeys.COLOR_CARET_1);
		this.caretColor2 = getColor(TGConfigKeys.COLOR_CARET_2);
		this.loopSMarkerColor = getColor(TGConfigKeys.COLOR_LOOP_S_MARKER);
		this.loopEMarkerColor = getColor(TGConfigKeys.COLOR_LOOP_E_MARKER);
		// Static colors
		this.colorWhite = TuxGuitar.instance().getDisplay().getSystemColor(SWT.COLOR_WHITE);
		this.colorBlack = TuxGuitar.instance().getDisplay().getSystemColor(SWT.COLOR_BLACK);
		this.colorRed = TuxGuitar.instance().getDisplay().getSystemColor(SWT.COLOR_RED);
	}
	
	private void initImages(){
		this.scoreNotes = new Image[4];
		this.scoreNotes[SCORE_NOTE_EMPTY_NORMAL_MODE] = getScoreNoteImage( getScoreNoteColor(),false);
		this.scoreNotes[SCORE_NOTE_EMPTY_PLAY_MODE] = getScoreNoteImage( getPlayNoteColor(),false);
		this.scoreNotes[SCORE_NOTE_FULL_NORMAL_MODE] = getScoreNoteImage( getScoreNoteColor(),true);
		this.scoreNotes[SCORE_NOTE_FULL_PLAY_MODE] = getScoreNoteImage( getPlayNoteColor(),true);
		
		this.harmonicNotes = new Image[4];
		this.harmonicNotes[SCORE_NOTE_EMPTY_NORMAL_MODE] = getHarmonicImage( getScoreNoteColor(),false);
		this.harmonicNotes[SCORE_NOTE_EMPTY_PLAY_MODE] = getHarmonicImage( getPlayNoteColor(),false);
		this.harmonicNotes[SCORE_NOTE_FULL_NORMAL_MODE] = getHarmonicImage( getScoreNoteColor(),true);
		this.harmonicNotes[SCORE_NOTE_FULL_PLAY_MODE] = getHarmonicImage( getPlayNoteColor(),true);
		
		this.tempoImage = getTempoImage(this.getColorBlack());
		
		this.tripletFeel8 = getTripletFeel8(this.getColorBlack());
		this.tripletFeelNone8 = getTripletFeelNone8(this.getColorBlack());
		this.tripletFeel16 = getTripletFeel16(this.getColorBlack());
		this.tripletFeelNone16 = getTripletFeelNone16(this.getColorBlack());
	}
	
	private Font getFont(String key, float scale){
		FontData data = TuxGuitar.instance().getConfig().getFontDataConfigValue(key);
		if(data == null){
			data = new FontData();
		}
		float height = ( data.getHeight() * scale );
		
		data.setHeight( ( height > 1 ? Math.round(height) : 1 ) );
		
		Font font = new Font(TuxGuitar.instance().getDisplay(),data);
		this.resources.add( font );
		return font;
	}
	
	private Color getColor(String key){
		RGB rgb = TuxGuitar.instance().getConfig().getRGBConfigValue(key);
		if(rgb == null){
			rgb = new RGB(0,0,0);
		}
		Color color = new Color(TuxGuitar.instance().getDisplay(),rgb);
		this.resources.add( color );
		return color;
	}
	
	private Image getScoreNoteImage(Color color,boolean full) {
		float scale = (full ? getLayout().getScoreLineSpacing() + 1 : getLayout().getScoreLineSpacing() ) - 2;
		int width = Math.round(scale * 1.33f);
		int height = Math.round(scale * 1.0f);
		
		Image image = getImage(width + 1, height + 2);
		TGPainter painter = new TGPainter(new GC(image));
		painter.setBackground(color);
		painter.setForeground(color);
		painter.initPath( (full ? TGPainter.PATH_FILL : TGPainter.PATH_DRAW) );
		TGNotePainter.paintNote(painter,0,1, scale );
		painter.closePath();
		painter.dispose();
		
		this.scoreNoteWidth = width;
		
		return getImageMask(image, getBackgroundColor().getRGB(), color.getRGB());
	}
	
	private Image getHarmonicImage(Color color,boolean full) {
		int size = getLayout().getScoreLineSpacing();
		
		int x = 0;
		int y = 1;
		int width = getScoreNoteWidth() - 1;
		int height = size - 2;
		
		Image image = getImage(x + width + 2,y + height + 2);
		TGPainter painter = new TGPainter(new GC(image));
		painter.setForeground(color);
		painter.setBackground(color);
		painter.initPath( ( full ? TGPainter.PATH_DRAW | TGPainter.PATH_FILL : TGPainter.PATH_DRAW )  );
		TGNotePainter.paintHarmonic(painter, x, y, height);
		painter.closePath();
		painter.dispose();
		
		return getImageMask(image, getBackgroundColor().getRGB(), color.getRGB());
	}
	
	private Image getTempoImage(Color color) {
		float scale = 5f * getLayout().getScale();
		int width = Math.round(scale * 1.33f);
		int height = Math.round(scale * (1.0f + 2.5f));
		
		Image image = getImage(width + 1, height + 2);
		TGPainter painter = new TGPainter(new GC(image));
		painter.setBackground(color);
		painter.setForeground(color);
		TGTempoPainter.paintTempo(painter,0,0, scale);
		painter.dispose();
		
		return getImageMask(image, getBackgroundColor().getRGB(), color.getRGB());
	}
	
	private Image getTripletFeelNone8(Color color) {
		float scale = 5f * getLayout().getScale();
		
		float horizontalSpacing = (1.5f * scale);
		float verticalSpacing = (2.5f * scale);
		float ovalWidth = (1.33f * scale);
		float ovalHeight = (1.0f * scale);
		
		int width = Math.round( ovalWidth + horizontalSpacing );
		int height = Math.round( ovalHeight + verticalSpacing );
		
		Image image = getImage(width + 1, height + 2);
		TGPainter painter = new TGPainter(new GC(image));
		painter.setBackground(color);
		painter.setForeground(color);
		TGTripletFeelPainter.paintTripletFeelNone8(painter,0,0, scale);
		painter.dispose();
		
		return getImageMask(image, getBackgroundColor().getRGB(), color.getRGB());
	}
	
	private Image getTripletFeel8(Color color) {
		float scale = 5f * getLayout().getScale();
		float topSpacing = (1.0f * scale);
		float horizontalSpacing = (1.5f * scale);
		float verticalSpacing = (2.5f * scale);
		float ovalWidth = (1.33f * scale);
		float ovalHeight = (1.0f * scale);
		
		int width = Math.round( (ovalWidth * 2f) + horizontalSpacing );
		int height = Math.round((topSpacing + ovalHeight + verticalSpacing));
		
		Image image = getImage(width + 1, height + 2);
		TGPainter painter = new TGPainter(new GC(image));
		painter.setBackground(color);
		painter.setForeground(color);
		TGTripletFeelPainter.paintTripletFeel8(painter,0,0, scale);
		painter.dispose();
		
		return getImageMask(image, getBackgroundColor().getRGB(), color.getRGB());
	}	
	
	private Image getTripletFeelNone16(Color color) {
		float scale = 5f * getLayout().getScale();
		
		float horizontalSpacing = (1.5f * scale);
		float verticalSpacing = (2.5f * scale);
		float ovalWidth = (1.33f * scale);
		float ovalHeight = (1.0f * scale);
		
		int width = Math.round( ovalWidth + horizontalSpacing );
		int height = Math.round( ovalHeight + verticalSpacing );
		
		Image image = getImage(width + 1, height + 2);
		TGPainter painter = new TGPainter(new GC(image));
		painter.setBackground(color);
		painter.setForeground(color);
		TGTripletFeelPainter.paintTripletFeelNone16(painter,0,0, scale);
		painter.dispose();
		
		return getImageMask(image, getBackgroundColor().getRGB(), color.getRGB());
	}
	
	private Image getTripletFeel16(Color color) {
		float scale = 5f * getLayout().getScale();
		float topSpacing = (1.0f * scale);
		float horizontalSpacing = (1.5f * scale);
		float verticalSpacing = (2.5f * scale);
		float ovalWidth = (1.33f * scale);
		float ovalHeight = (1.0f * scale);
		
		int width = Math.round( (ovalWidth * 2f) + horizontalSpacing );
		int height = Math.round( topSpacing + ovalHeight + verticalSpacing );
		
		Image image = getImage(width + 1, height + 2);
		TGPainter painter = new TGPainter(new GC(image));
		painter.setBackground(color);
		painter.setForeground(color);
		TGTripletFeelPainter.paintTripletFeel16(painter,0,0, scale);
		painter.dispose();
		
		return getImageMask(image, getBackgroundColor().getRGB(), color.getRGB());
	}
	
	private Image getImage(int width, int height){
		Image image = new Image(getLayout().getTablature().getDisplay(),width, height);
		TGPainter painter = new TGPainter(new GC(image));
		painter.setBackground( getBackgroundColor());
		painter.initPath(TGPainter.PATH_FILL);
		painter.addRectangle(0,0,width, height);
		painter.closePath();
		painter.dispose();
		return image;
	}
	
	private Image getImageMask(Image src,RGB alpha,RGB none){
		ImageData srcData = src.getImageData();
		ImageData maskData = ImageUtils.applyMask(srcData, alpha , none);
		src.dispose();
		Image image = new Image(getLayout().getTablature().getDisplay(),srcData,maskData);
		this.resources.add(image);
		return image;
	}
	
	public void dispose(){
		Iterator it = this.resources.iterator();
		while( it.hasNext() ){
			Resource resource = (Resource)it.next();
			resource.dispose();
		}
		this.resources.clear();
	}
}
