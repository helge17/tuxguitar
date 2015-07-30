package org.herac.tuxguitar.graphics.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGColorModel;
import org.herac.tuxguitar.graphics.TGFont;
import org.herac.tuxguitar.graphics.TGFontModel;
import org.herac.tuxguitar.graphics.TGResource;
import org.herac.tuxguitar.graphics.TGResourceFactory;

public class TGResources {
	
	private List<TGResource> resources;
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
	
	public TGResources(TGLayout layout){
		this.layout = layout;
		this.resources = new ArrayList<TGResource>();
	}
	
	public void load(TGLayoutStyles styles){
		this.dispose();
		this.initFonts(styles);
		this.initColors(styles);
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
	
	private TGResource addResource(TGResource resource){
		this.resources.add(resource);
		return resource;
	}
	
	public void dispose(){
		Iterator<TGResource> it = this.resources.iterator();
		while( it.hasNext() ){
			TGResource resource = (TGResource)it.next();
			resource.dispose();
		}
		this.resources.clear();
	}
}
