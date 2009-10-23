package org.herac.tuxguitar.gui.editors.tab;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;

import org.herac.tuxguitar.gui.editors.TGPainter;
import org.herac.tuxguitar.gui.editors.tab.layout.ViewLayout;
import org.herac.tuxguitar.gui.editors.tab.painters.TGNotePainter;
import org.herac.tuxguitar.gui.editors.tab.painters.TGTempoPainter;
import org.herac.tuxguitar.gui.editors.tab.painters.TGTripletFeelPainter;
import org.herac.tuxguitar.gui.system.config.TGConfig;
import org.herac.tuxguitar.song.models.TGDuration;

public class TGResources {
	
	private static final int SCORE_NOTE_EMPTY_NORMAL_MODE = 0;
	private static final int SCORE_NOTE_EMPTY_PLAY_MODE = 1;
	private static final int SCORE_NOTE_FULL_NORMAL_MODE = 2;
	private static final int SCORE_NOTE_FULL_PLAY_MODE = 3;
	
	private ViewLayout layout;
	private Font defaultFont;
	private Font noteFont;
	private Font timeSignatureFont;
	private Font lyricFont;
	private Font textFont;
	private Font graceFont;
	private Font chordFont;
	private Font chordFretFont;
	private Font markerFont;
	private Color backgroundColor;
	private Color lineColor;
	private Color scoreNoteColor;
	private Color tabNoteColor;
	private Color playNoteColor;
	private Color colorWhite;
	private Color colorBlack;
	private Color colorRed;
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
	
	public Font getChordFont(){
		return this.chordFont;
	}
	
	public Font getChordFretFont(){
		return this.chordFretFont;
	}
	
	public Font getGraceFont() {
		return this.graceFont;
	}
	
	public Font getMarkerFont(){
		return this.markerFont;
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
		this.defaultFont = TGConfig.FONT_DEFAULT;
		this.noteFont = TGConfig.FONT_NOTE;
		this.timeSignatureFont = TGConfig.FONT_TIME_SIGNATURE;
		this.lyricFont = TGConfig.FONT_LYRIC;
		this.textFont =  TGConfig.FONT_TEXT;
		this.graceFont = TGConfig.FONT_GRACE;
		this.chordFont = TGConfig.FONT_CHORD;
		this.chordFretFont = TGConfig.FONT_CHORD_FRET;
		this.markerFont = TGConfig.FONT_MARKER;
	}
	
	private void initColors(){
		this.backgroundColor = TGConfig.COLOR_BACKGROUND;
		this.lineColor = TGConfig.COLOR_LINE;
		this.scoreNoteColor = TGConfig.COLOR_SCORE_NOTE;
		this.tabNoteColor = TGConfig.COLOR_TAB_NOTE;
		this.playNoteColor = TGConfig.COLOR_PLAY_NOTE;
		this.loopSMarkerColor = TGConfig.COLOR_LOOP_S_MARKER;
		this.loopEMarkerColor = TGConfig.COLOR_LOOP_E_MARKER;
		// Static colors
		this.colorWhite = Color.WHITE;
		this.colorBlack = Color.BLACK;
		this.colorRed = Color.RED;
	}
	
	private void initImages(){
		this.scoreNotes = new Image[4];
		this.scoreNotes[SCORE_NOTE_EMPTY_NORMAL_MODE] = getScoreNoteImage( getScoreNoteColor(),false);
		this.scoreNotes[SCORE_NOTE_EMPTY_PLAY_MODE] = getScoreNoteImage( getPlayNoteColor(),false);
		this.scoreNotes[SCORE_NOTE_FULL_NORMAL_MODE] = getScoreNoteImage( getScoreNoteColor(),true);
		this.scoreNotes[SCORE_NOTE_FULL_PLAY_MODE] = getScoreNoteImage( getPlayNoteColor(),true);
		
		this.harmonicNotes = new Image[4];
		this.harmonicNotes[SCORE_NOTE_EMPTY_NORMAL_MODE] = getArmonicImage( getScoreNoteColor(),false);
		this.harmonicNotes[SCORE_NOTE_EMPTY_PLAY_MODE] = getArmonicImage( getPlayNoteColor(),false);
		this.harmonicNotes[SCORE_NOTE_FULL_NORMAL_MODE] = getArmonicImage( getScoreNoteColor(),true);
		this.harmonicNotes[SCORE_NOTE_FULL_PLAY_MODE] = getArmonicImage( getPlayNoteColor(),true);
		
		this.tempoImage = getTempoImage(this.getColorBlack());
		
		this.tripletFeel8 = getTripletFeel8(this.getColorBlack());
		this.tripletFeelNone8 = getTripletFeelNone8(this.getColorBlack());
		this.tripletFeel16 = getTripletFeel16(this.getColorBlack());
		this.tripletFeelNone16 = getTripletFeelNone16(this.getColorBlack());
	}
	
	private Image getScoreNoteImage(Color color,boolean full) {
		float scale = (full ? getLayout().getScoreLineSpacing() + 1 : getLayout().getScoreLineSpacing() ) - 2;
		int width = Math.round(scale * 1.33f);
		int height = Math.round(scale * 1.0f);
		
		Image image = getImage(width + 1, height + 2);
		TGPainter painter = new TGPainter(image);
		painter.setBackground(color);
		painter.setForeground(color);
		painter.initPath( (full ? TGPainter.PATH_FILL : TGPainter.PATH_DRAW) );
		TGNotePainter.paintNote(painter,0,1, scale );
		painter.closePath();
		painter.dispose();
		
		this.scoreNoteWidth = width;

		return getTransparentImage(image,getBackgroundColor());
	}
	
	private Image getArmonicImage(Color color,boolean full) {
		int size = getLayout().getScoreLineSpacing();
		
		int x = 0;
		int y = 1;
		int width = getScoreNoteWidth() - 1;
		int height = size - 2;
		
		Image image = getImage(x + width + 2,y + height + 2);
		TGPainter painter = new TGPainter(image);
		painter.setForeground(color);
		painter.setBackground(color);
		painter.initPath( ( full ? TGPainter.PATH_DRAW | TGPainter.PATH_FILL : TGPainter.PATH_DRAW )  );
		TGNotePainter.paintHarmonic(painter, x, y, height);
		painter.closePath();
		painter.dispose();
		
		return getTransparentImage(image,getBackgroundColor());
	}
	
	private Image getTempoImage(Color color) {
		float scale = 5f * getLayout().getScale();
		int width = Math.round(scale * 1.33f);
		int height = Math.round(scale * (1.0f + 2.5f));
		
		Image image = getImage(width + 1, height + 2);
		TGPainter painter = new TGPainter(image);
		painter.setBackground(color);
		painter.setForeground(color);
		TGTempoPainter.paintTempo(painter,0,0, scale);
		painter.dispose();
		
		return getTransparentImage(image,getBackgroundColor());
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
		TGPainter painter = new TGPainter(image);
		painter.setBackground(color);
		painter.setForeground(color);
		TGTripletFeelPainter.paintTripletFeelNone8(painter,0,0, scale);
		painter.dispose();
		
		return getTransparentImage(image,getBackgroundColor());
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
		TGPainter painter = new TGPainter(image);
		painter.setBackground(color);
		painter.setForeground(color);
		TGTripletFeelPainter.paintTripletFeel8(painter,0,0, scale);
		painter.dispose();
		
		return getTransparentImage(image,getBackgroundColor());
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
		TGPainter painter = new TGPainter(image);
		painter.setBackground(color);
		painter.setForeground(color);
		TGTripletFeelPainter.paintTripletFeelNone16(painter,0,0, scale);
		painter.dispose();
		
		return getTransparentImage(image,getBackgroundColor());
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
		TGPainter painter = new TGPainter(image);
		painter.setBackground(color);
		painter.setForeground(color);
		TGTripletFeelPainter.paintTripletFeel16(painter,0,0, scale);
		painter.dispose();
		
		return getTransparentImage(image,getBackgroundColor());
	}
	
	private Image getImage(int width, int height){
		Image image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		TGPainter painter = new TGPainter(image);
		painter.setBackground( getBackgroundColor());
		painter.initPath(TGPainter.PATH_FILL);
		painter.addRectangle(0,0,width, height);
		painter.closePath();
		painter.dispose();
		return image;
	}
	
	public Image getTransparentImage(Image image, final Color color) {
		ImageFilter filter = new RGBImageFilter() {
			public int markerRGB = (color.getRGB() | 0xFF000000);
			
			public final int filterRGB(int x, int y, int rgb) {
				if ( ( rgb | 0xFF000000 ) == markerRGB ) {
					return 0x00FFFFFF & rgb;
				}
				return rgb;
			}
		};
		return Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), filter));
	}
	
	public void dispose(){
		// nothing to do
	}
}
