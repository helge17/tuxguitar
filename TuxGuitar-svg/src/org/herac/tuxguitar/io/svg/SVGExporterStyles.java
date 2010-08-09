package org.herac.tuxguitar.io.svg;

import org.herac.tuxguitar.graphics.TGColorModel;
import org.herac.tuxguitar.graphics.TGFontModel;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.TGLayoutStyles;

public class SVGExporterStyles {
	
	private int layoutFlags;
	private TGLayoutStyles layoutStyles;
	
	private boolean configured;
	
	public SVGExporterStyles(){
		this.layoutFlags = 0;
		this.layoutStyles = new TGLayoutStyles();
		this.configured = false;
	}
	
	public boolean isConfigured() {
		return this.configured;
	}
	
	public void setConfigured(boolean configured) {
		this.configured = configured;
	}
	
	public void setLayoutFlags(int layoutFlags){
		this.layoutFlags = layoutFlags;
	}
	
	public int getLayoutFlags(){
		return this.layoutFlags;
	}
	
	public TGLayoutStyles getLayoutStyles(){
		return this.layoutStyles;
	}
	
	public void configure() {
		this.configureWithDefaults();
	}
	
	public void configureWithDefaults(){
		this.setLayoutFlags( 0 );
		this.setLayoutFlags( this.getLayoutFlags() | TGLayout.DISPLAY_SCORE );
		this.setLayoutFlags( this.getLayoutFlags() | TGLayout.DISPLAY_TABLATURE );
		this.setLayoutFlags( this.getLayoutFlags() | TGLayout.DISPLAY_MULTITRACK );
		this.setLayoutFlags( this.getLayoutFlags() | TGLayout.DISPLAY_COMPACT );
		this.setLayoutFlags( this.getLayoutFlags() | TGLayout.DISPLAY_CHORD_NAME );
		this.setLayoutFlags( this.getLayoutFlags() | TGLayout.DISPLAY_CHORD_DIAGRAM );
		
		this.getLayoutStyles().setBufferEnabled(true);
		this.getLayoutStyles().setStringSpacing(10);
		this.getLayoutStyles().setScoreLineSpacing(8);
		this.getLayoutStyles().setFirstMeasureSpacing(20);
		this.getLayoutStyles().setMinBufferSeparator(20);
		this.getLayoutStyles().setMinTopSpacing(30);
		this.getLayoutStyles().setMinScoreTabSpacing(20);
		this.getLayoutStyles().setFirstTrackSpacing(20);
		this.getLayoutStyles().setTrackSpacing(10);
		this.getLayoutStyles().setChordFretIndexSpacing(8);
		this.getLayoutStyles().setChordStringSpacing(5);
		this.getLayoutStyles().setChordFretSpacing(6);
		this.getLayoutStyles().setChordNoteSize(4);
		this.getLayoutStyles().setRepeatEndingSpacing(20);
		this.getLayoutStyles().setTextSpacing(15);
		this.getLayoutStyles().setMarkerSpacing(15);
		this.getLayoutStyles().setLoopMarkerSpacing(5);
		this.getLayoutStyles().setDivisionTypeSpacing(10);
		this.getLayoutStyles().setEffectSpacing(8);
		
		this.getLayoutStyles().setDefaultFont(new TGFontModel("Default", 10, false, false) );
		this.getLayoutStyles().setNoteFont(new TGFontModel("Default", 10, false, false) );
		this.getLayoutStyles().setTimeSignatureFont(new TGFontModel("Default", 14, true, false) );
		this.getLayoutStyles().setLyricFont(new TGFontModel("Default", 10, false, false) );
		this.getLayoutStyles().setTextFont(new TGFontModel("Default", 10, false, false));
		this.getLayoutStyles().setMarkerFont(new TGFontModel("Default", 10, false, false));
		this.getLayoutStyles().setGraceFont(new TGFontModel("Default", 8, false, false));
		this.getLayoutStyles().setChordFont(new TGFontModel("Default", 10, false, false));
		this.getLayoutStyles().setChordFretFont(new TGFontModel("Default", 8, false, false));
		
		this.getLayoutStyles().setBackgroundColor( new TGColorModel(255,255,255) );
		this.getLayoutStyles().setLineColor(  new TGColorModel(214,214,214)  );
		this.getLayoutStyles().setScoreNoteColor( new TGColorModel(64,64,64));
		this.getLayoutStyles().setTabNoteColor(new TGColorModel(64,64,64));
		this.getLayoutStyles().setPlayNoteColor(new TGColorModel(64,64,64));
		this.getLayoutStyles().setLoopSMarkerColor(new TGColorModel(0,0,0));
		this.getLayoutStyles().setLoopEMarkerColor(new TGColorModel(0,0,0));
		
		this.setConfigured( true );
	}
}
