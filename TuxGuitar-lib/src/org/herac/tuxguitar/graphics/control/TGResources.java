package org.herac.tuxguitar.graphics.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGColorModel;
import org.herac.tuxguitar.graphics.TGFont;
import org.herac.tuxguitar.graphics.TGFontModel;
import org.herac.tuxguitar.graphics.TGImage;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGResource;
import org.herac.tuxguitar.graphics.TGResourceFactory;
import org.herac.tuxguitar.graphics.control.painters.TGNotePainter;
import org.herac.tuxguitar.graphics.control.painters.TGTempoPainter;
import org.herac.tuxguitar.graphics.control.painters.TGTripletFeelPainter;
import org.herac.tuxguitar.song.models.TGDuration;

public class TGResources {
	
	private static final int SCORE_NOTE_EMPTY_NORMAL_MODE = 0;
	private static final int SCORE_NOTE_EMPTY_PLAY_MODE = 1;
	private static final int SCORE_NOTE_FULL_NORMAL_MODE = 2;
	private static final int SCORE_NOTE_FULL_PLAY_MODE = 3;
	
	private List resources;
	private TGLayout layout;
	
	private TGFont defaultFont;
	private TGFont noteFont;
	private TGFont timeSignatureFont;
	private TGFont lyricFont;
	private TGFont textFont;
	private TGFont markerFont;
	private TGFont graceFont;
	private TGFont chordFont;
	private TGFont chordFretFont;
	private TGColor backgroundColor;
	private TGColor lineColor;
	private TGColor scoreNoteColor;
	private TGColor tabNoteColor;
	private TGColor playNoteColor;
	private TGColor colorWhite;
	private TGColor colorBlack;
	private TGColor colorRed;
	private TGColor loopSMarkerColor;
	private TGColor loopEMarkerColor;
	private TGImage[] scoreNotes;
	private TGImage[] harmonicNotes;
	private TGImage tempoImage;
	private TGImage tripletFeel8;
	private TGImage tripletFeelNone8;
	private TGImage tripletFeel16;
	private TGImage tripletFeelNone16;
	
	private int scoreNoteWidth;
	
	public TGResources(TGLayout layout){
		this.layout = layout;
		this.resources = new ArrayList();
	}
	
	public void load(TGLayoutStyles styles){
		this.dispose();
		this.initFonts(styles);
		this.initColors(styles);
		this.initImages();
	}
	
	public TGLayout getLayout(){
		return this.layout;
	}
	
	public TGFont getDefaultFont() {
		return this.defaultFont;
	}
	
	public TGFont getNoteFont() {
		return this.noteFont;
	}
	
	public TGFont getTimeSignatureFont() {
		return this.timeSignatureFont;
	}
	
	public TGFont getLyricFont(){
		return this.lyricFont;
	}
	
	public TGFont getTextFont(){
		return this.textFont;
	}
	
	public TGFont getMarkerFont(){
		return this.markerFont;
	}
	
	public TGFont getChordFont(){
		return this.chordFont;
	}
	
	public TGFont getChordFretFont(){
		return this.chordFretFont;
	}
	
	public TGFont getGraceFont() {
		return this.graceFont;
	}
	
	public TGColor getBackgroundColor() {
		return this.backgroundColor;
	}
	
	public TGColor getLineColor() {
		return this.lineColor;
	}
	
	public TGColor getPlayNoteColor() {
		return this.playNoteColor;
	}
	
	public TGColor getScoreNoteColor() {
		return this.scoreNoteColor;
	}
	
	public TGColor getTabNoteColor() {
		return this.tabNoteColor;
	}
	
	public TGColor getColorWhite() {
		return this.colorWhite;
	}
	
	public TGColor getColorBlack() {
		return this.colorBlack;
	}
	
	public TGColor getColorRed() {
		return this.colorRed;
	}
	
	public TGColor getLoopSMarkerColor() {
		return this.loopSMarkerColor;
	}
	
	public TGColor getLoopEMarkerColor() {
		return this.loopEMarkerColor;
	}
	
	public TGImage getTempoImage() {
		return this.tempoImage;
	}
	
	public TGImage getTripletFeel8(){
		return this.tripletFeel8;
	}
	
	public TGImage getTripletFeelNone8(){
		return this.tripletFeelNone8;
	}
	
	public TGImage getTripletFeel16(){
		return this.tripletFeel16;
	}
	
	public TGImage getTripletFeelNone16(){
		return this.tripletFeelNone16;
	}
	
	public TGImage getScoreNote(int value,boolean playing) {
		int index = 0;
		index += ((playing)?1:0);
		index += ((value >= TGDuration.QUARTER)?2:0);
		return this.scoreNotes[index];
	}
	
	public TGImage getHarmonicNote(int value,boolean playing) {
		int index = 0;
		index += ((playing)?1:0);
		index += ((value >= TGDuration.QUARTER)?2:0);
		return this.harmonicNotes[index];
	}
	
	public int getScoreNoteWidth(){
		return this.scoreNoteWidth;
	}
	
	private void initFonts(TGLayoutStyles style){
		float scale = this.layout.getFontScale() ;
		this.defaultFont = getFont(style.getDefaultFont(),scale);
		this.noteFont = getFont(style.getNoteFont(), scale);
		this.timeSignatureFont = getFont(style.getTimeSignatureFont(), scale);
		this.lyricFont = getFont(style.getLyricFont(), scale);
		this.textFont =  getFont(style.getTextFont(), scale);
		this.markerFont =  getFont(style.getMarkerFont(), scale);
		this.graceFont = getFont(style.getGraceFont(), scale);
		this.chordFont = getFont(style.getChordFont(), scale);
		this.chordFretFont = getFont(style.getChordFretFont(), scale);
	}
	
	private void initColors(TGLayoutStyles style){
		this.backgroundColor = getColor(style.getBackgroundColor());
		this.lineColor = getColor(style.getLineColor());
		this.scoreNoteColor = getColor(style.getScoreNoteColor());
		this.tabNoteColor = getColor(style.getTabNoteColor());
		this.playNoteColor = getColor(style.getPlayNoteColor());
		this.loopSMarkerColor = getColor(style.getLoopSMarkerColor());
		this.loopEMarkerColor = getColor(style.getLoopEMarkerColor());
		// Static colors
		this.colorWhite = getColor(new TGColorModel(0xff,0xff,0xff));
		this.colorBlack = getColor(new TGColorModel(0x00,0x00,0x00));
		this.colorRed = getColor(new TGColorModel(0xff,0x00,0x00));
	}
	
	private void initImages(){
		this.scoreNotes = new TGImage[4];
		this.scoreNotes[SCORE_NOTE_EMPTY_NORMAL_MODE] = getScoreNoteImage( getScoreNoteColor(),false);
		this.scoreNotes[SCORE_NOTE_EMPTY_PLAY_MODE] = getScoreNoteImage( getPlayNoteColor(),false);
		this.scoreNotes[SCORE_NOTE_FULL_NORMAL_MODE] = getScoreNoteImage( getScoreNoteColor(),true);
		this.scoreNotes[SCORE_NOTE_FULL_PLAY_MODE] = getScoreNoteImage( getPlayNoteColor(),true);
		
		this.harmonicNotes = new TGImage[4];
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
	
	private TGFont getFont(TGFontModel model, float scale){
		TGResourceFactory factory = getLayout().getComponent().getResourceFactory();
		TGFontModel fm = new TGFontModel();
		if(model != null){
			float height = ( model.getHeight() * scale );
			
			fm.setHeight( ( height > 1 ? Math.round(height) : 1 ) );
			fm.setName( model.getName() );
			fm.setBold( model.isBold() );
			fm.setItalic( model.isItalic() );
		}
		
		return (TGFont)addResource(factory.createFont(fm));
	}
	
	private TGColor getColor(TGColorModel model){
		TGResourceFactory factory = getLayout().getComponent().getResourceFactory();
		TGColorModel cm = (model != null ? model : new TGColorModel() );
		
		return (TGColor)addResource(factory.createColor(cm));
	}
	
	private TGImage getScoreNoteImage(TGColor color,boolean full) {
		float scale = (full ? getLayout().getScoreLineSpacing() + 1 : getLayout().getScoreLineSpacing() ) - 2;
		int width = Math.round(scale * 1.33f);
		int height = Math.round(scale * 1.0f);
		
		TGImage image = getImage(width + 1, height + 2);
		TGPainter painter = image.createPainter();
		painter.setBackground(color);
		painter.setForeground(color);
		painter.initPath( (full ? TGPainter.PATH_FILL : TGPainter.PATH_DRAW) );
		TGNotePainter.paintNote(painter,0,1, scale );
		painter.closePath();
		painter.dispose();
		image.applyTransparency(getBackgroundColor());
		
		this.scoreNoteWidth = width;
		
		return ((TGImage)addResource(image));
	}
	
	private TGImage getHarmonicImage(TGColor color,boolean full) {
		float size = getLayout().getScoreLineSpacing();
		
		float x = 0;
		float y = 1;
		float width = getScoreNoteWidth() - 1;
		float height = size - 2;
		
		TGImage image = getImage(x + width + 2,y + height + 2);
		TGPainter painter = image.createPainter();
		painter.setForeground(color);
		painter.setBackground(color);
		painter.initPath( ( full ? TGPainter.PATH_DRAW | TGPainter.PATH_FILL : TGPainter.PATH_DRAW )  );
		TGNotePainter.paintHarmonic(painter, x, y, height);
		painter.closePath();
		painter.dispose();
		image.applyTransparency(getBackgroundColor());
		
		return ((TGImage)addResource(image));
	}
	
	private TGImage getTempoImage(TGColor color) {
		float scale = 5f * getLayout().getScale();
		int width = Math.round(scale * 1.33f);
		int height = Math.round(scale * (1.0f + 2.5f));
		
		TGImage image = getImage(width + 1, height + 2);
		TGPainter painter = image.createPainter();
		painter.setBackground(color);
		painter.setForeground(color);
		TGTempoPainter.paintTempo(painter,0,0, scale);
		painter.dispose();
		image.applyTransparency(getBackgroundColor());
		
		return ((TGImage)addResource(image));
	}
	
	private TGImage getTripletFeelNone8(TGColor color) {
		float scale = 5f * getLayout().getScale();
		
		float horizontalSpacing = (1.5f * scale);
		float verticalSpacing = (2.5f * scale);
		float ovalWidth = (1.33f * scale);
		float ovalHeight = (1.0f * scale);
		
		int width = Math.round( ovalWidth + horizontalSpacing );
		int height = Math.round( ovalHeight + verticalSpacing );
		
		TGImage image = getImage(width + 1, height + 2);
		TGPainter painter = image.createPainter();
		painter.setBackground(color);
		painter.setForeground(color);
		TGTripletFeelPainter.paintTripletFeelNone8(painter,0,0, scale);
		painter.dispose();
		image.applyTransparency(getBackgroundColor());
		
		return ((TGImage)addResource(image));
	}
	
	private TGImage getTripletFeel8(TGColor color) {
		float scale = 5f * getLayout().getScale();
		float topSpacing = (1.0f * scale);
		float horizontalSpacing = (1.5f * scale);
		float verticalSpacing = (2.5f * scale);
		float ovalWidth = (1.33f * scale);
		float ovalHeight = (1.0f * scale);
		
		int width = Math.round( (ovalWidth * 2f) + horizontalSpacing );
		int height = Math.round((topSpacing + ovalHeight + verticalSpacing));
		
		TGImage image = getImage(width + 1, height + 2);
		TGPainter painter = image.createPainter();
		painter.setBackground(color);
		painter.setForeground(color);
		TGTripletFeelPainter.paintTripletFeel8(painter,0,0, scale);
		painter.dispose();
		image.applyTransparency(getBackgroundColor());
		
		return ((TGImage)addResource(image));
	}	
	
	private TGImage getTripletFeelNone16(TGColor color) {
		float scale = 5f * getLayout().getScale();
		
		float horizontalSpacing = (1.5f * scale);
		float verticalSpacing = (2.5f * scale);
		float ovalWidth = (1.33f * scale);
		float ovalHeight = (1.0f * scale);
		
		int width = Math.round( ovalWidth + horizontalSpacing );
		int height = Math.round( ovalHeight + verticalSpacing );
		
		TGImage image = getImage(width + 1, height + 2);
		TGPainter painter = image.createPainter();
		painter.setBackground(color);
		painter.setForeground(color);
		TGTripletFeelPainter.paintTripletFeelNone16(painter,0,0, scale);
		painter.dispose();
		image.applyTransparency(getBackgroundColor());
		
		return ((TGImage)addResource(image));
	}
	
	private TGImage getTripletFeel16(TGColor color) {
		float scale = 5f * getLayout().getScale();
		float topSpacing = (1.0f * scale);
		float horizontalSpacing = (1.5f * scale);
		float verticalSpacing = (2.5f * scale);
		float ovalWidth = (1.33f * scale);
		float ovalHeight = (1.0f * scale);
		
		int width = Math.round( (ovalWidth * 2f) + horizontalSpacing );
		int height = Math.round( topSpacing + ovalHeight + verticalSpacing );
		
		TGImage image = getImage(width + 1, height + 2);
		TGPainter painter = image.createPainter();
		painter.setBackground(color);
		painter.setForeground(color);
		TGTripletFeelPainter.paintTripletFeel16(painter,0,0, scale);
		painter.dispose();
		image.applyTransparency(getBackgroundColor());
		
		return ((TGImage)addResource(image));
	}
	
	private TGImage getImage(float width, float height){
		TGResourceFactory factory = getLayout().getComponent().getResourceFactory();
		TGImage image = factory.createImage(width, height);
		TGPainter painter = image.createPainter();
		painter.setBackground( getBackgroundColor());
		painter.initPath(TGPainter.PATH_FILL);
		painter.addRectangle(0,0,width, height);
		painter.closePath();
		painter.dispose();
		return image;
	}
	
	private TGResource addResource(TGResource resource){
		this.resources.add(resource);
		return resource;
	}
	
	public void dispose(){
		Iterator it = this.resources.iterator();
		while( it.hasNext() ){
			TGResource resource = (TGResource)it.next();
			resource.dispose();
		}
		this.resources.clear();
	}
}
