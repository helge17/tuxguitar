package app.tuxguitar.graphics.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UIColorModel;
import app.tuxguitar.ui.resource.UIFont;
import app.tuxguitar.ui.resource.UIFontAlignment;
import app.tuxguitar.ui.resource.UIFontModel;
import app.tuxguitar.ui.resource.UIResource;
import app.tuxguitar.ui.resource.UIResourceFactory;

public class TGResources {

	private List<UIResource> resources;
	private TGLayout layout;

	private UIFont defaultFont;
	private UIFont noteFont;
	private UIFont lyricFont;
	private UIFont textFont;
	private UIFont markerFont;
	private UIFont graceFont;
	private UIFont chordFont;
	private UIFont chordFretFont;
	private UIColor foregroundColor;
	private UIColor backgroundColor;
	private UIColor backgroundColorPlaying;
	private UIColor lineColor;
	private UIColor lineColorInvalid;
	private UIColor scoreNoteColor;
	private UIColor tabNoteColor;
	private UIColor playNoteColor;
	private UIColor selectionColor;
	private UIColor colorWhite;
	private UIColor colorBlack;
	private UIColor loopSMarkerColor;
	private UIColor loopEMarkerColor;
	private UIColor measureNumberColor;

	public TGResources(TGLayout layout){
		this.layout = layout;
		this.resources = new ArrayList<UIResource>();
	}

	public void load(TGLayoutStyles styles){
		this.dispose();
		this.initFonts(styles);
		this.initColors(styles);
	}

	public TGLayout getLayout(){
		return this.layout;
	}

	public UIFont getDefaultFont() {
		return this.defaultFont;
	}

	public UIFont getNoteFont() {
		return this.noteFont;
	}

	public UIFont getLyricFont(){
		return this.lyricFont;
	}

	public UIFont getTextFont(){
		return this.textFont;
	}

	public UIFont getMarkerFont(){
		return this.markerFont;
	}

	public UIFont getChordFont(){
		return this.chordFont;
	}

	public UIFont getChordFretFont(){
		return this.chordFretFont;
	}

	public UIFont getGraceFont() {
		return this.graceFont;
	}

	public UIColor getForegroundColor() {
		return foregroundColor;
	}

	public UIColor getBackgroundColor() {
		return this.backgroundColor;
	}

	public UIColor getBackgroundColorPlaying() {
		return this.backgroundColorPlaying;
	}

	public UIColor getLineColor() {
		return this.lineColor;
	}

	public UIColor getLineColorInvalid() {
		return this.lineColorInvalid;
	}
	

	public UIColor getPlayNoteColor() {
		return this.playNoteColor;
	}

	public UIColor getScoreNoteColor() {
		return this.scoreNoteColor;
	}

	public UIColor getTabNoteColor() {
		return this.tabNoteColor;
	}

	public UIColor getSelectionColor() {
	    return this.selectionColor;
    }

	public UIColor getColorWhite() {
		return this.colorWhite;
	}

	public UIColor getColorBlack() {
		return this.colorBlack;
	}

	public UIColor getLoopSMarkerColor() {
		return this.loopSMarkerColor;
	}

	public UIColor getLoopEMarkerColor() {
		return this.loopEMarkerColor;
	}

	public UIColor getMeasureNumberColor() {
		return this.measureNumberColor;
	}

	private void initFonts(TGLayoutStyles style){
		float scale = this.layout.getFontScale() ;
		this.defaultFont = getFont(style.getDefaultFont(),scale);
		this.noteFont = getFont(style.getNoteFont(), scale);
		this.lyricFont = getFont(style.getLyricFont(), scale);
		this.textFont =  getFont(style.getTextFont(), scale);
		this.markerFont =  getFont(style.getMarkerFont(), scale);
		this.graceFont = getFont(style.getGraceFont(), scale);
		this.chordFont = getFont(style.getChordFont(), scale);
		this.chordFretFont = getFont(style.getChordFretFont(), scale);
	}

	private void initColors(TGLayoutStyles style){
		this.foregroundColor = getColor(style.getForegroundColor());
		this.backgroundColor = getColor(style.getBackgroundColor());
		this.backgroundColorPlaying = getColor(style.getBackgroundColorPlaying());
		this.lineColor = getColor(style.getLineColor());
		this.lineColorInvalid = getColor(style.getLineColorInvalid());
		this.scoreNoteColor = getColor(style.getScoreNoteColor());
		this.tabNoteColor = getColor(style.getTabNoteColor());
		this.playNoteColor = getColor(style.getPlayNoteColor());
		this.selectionColor = getColor(style.getSelectionColor());
		this.loopSMarkerColor = getColor(style.getLoopSMarkerColor());
		this.loopEMarkerColor = getColor(style.getLoopEMarkerColor());
		this.measureNumberColor = getColor(style.getMeasureNumberColor());

		// Static colors
		this.colorWhite = getColor(new UIColorModel(0xff,0xff,0xff));
		this.colorBlack = getColor(new UIColorModel(0x00,0x00,0x00));
	}

	private UIFont getFont(UIFontModel model, float scale){
		UIResourceFactory factory = getLayout().getComponent().getResourceFactory();
		UIFontModel fm = new UIFontModel();
		if(model != null){
			float height = (model.getHeight() * scale);

			fm.setHeight((height > 1 ? Math.round(height) : 1));
			fm.setName(model.getName());
			fm.setBold(model.isBold());
			fm.setItalic(model.isItalic());
			if( model.getAlignment() != null ) {
				fm.setAlignment(new UIFontAlignment());
				fm.getAlignment().setTop(model.getAlignment().getTop() * scale);
				fm.getAlignment().setMiddle(model.getAlignment().getMiddle() * scale);
				fm.getAlignment().setBottom(model.getAlignment().getBottom() * scale);
			}
		}

		return (UIFont) addResource(factory.createFont(fm));
	}

	private UIColor getColor(UIColorModel model){
		UIResourceFactory factory = getLayout().getComponent().getResourceFactory();
		UIColorModel cm = (model != null ? model : new UIColorModel() );

		return (UIColor) addResource(factory.createColor(cm));
	}

	private UIResource addResource(UIResource resource){
		this.resources.add(resource);
		return resource;
	}

	public void dispose(){
		Iterator<UIResource> it = this.resources.iterator();
		while( it.hasNext() ){
			UIResource resource = (UIResource)it.next();
			resource.dispose();
		}
		this.resources.clear();
	}
}
