package org.herac.tuxguitar.io.svg;

import org.herac.tuxguitar.graphics.TGColorModel;
import org.herac.tuxguitar.graphics.TGFontModel;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.TGLayoutStyles;

public class SVGStyles {
	
	private int track;
	private int flags;
	private TGLayoutStyles styles;
	
	public SVGStyles(){
		this.track = -1;
		this.flags = 0;
		this.styles = new TGLayoutStyles();
	}
	
	public int getTrack() {
		return this.track;
	}
	
	public void setTrack(int track) {
		this.track = track;
	}
	
	public void setFlags(int flags){
		this.flags = flags;
	}
	
	public int getFlags(){
		return this.flags;
	}
	
	public TGLayoutStyles getStyles(){
		return this.styles;
	}
	
	public void configure() {
		this.configureWithDefaults();
	}
	
	public void configureWithDefaults(){
		this.setTrack( -1 );
		
		this.setFlags( 0 );
		this.setFlags( this.getFlags() | TGLayout.DISPLAY_SCORE );
		this.setFlags( this.getFlags() | TGLayout.DISPLAY_TABLATURE );
		this.setFlags( this.getFlags() | TGLayout.DISPLAY_MULTITRACK );
		this.setFlags( this.getFlags() | TGLayout.DISPLAY_COMPACT );
		this.setFlags( this.getFlags() | TGLayout.DISPLAY_CHORD_NAME );
		this.setFlags( this.getFlags() | TGLayout.DISPLAY_CHORD_DIAGRAM );
		
		this.getStyles().setBufferEnabled(true);
		this.getStyles().setStringSpacing(10);
		this.getStyles().setScoreLineSpacing(8);
		this.getStyles().setFirstMeasureSpacing(20);
		this.getStyles().setMinBufferSeparator(20);
		this.getStyles().setMinTopSpacing(30);
		this.getStyles().setMinScoreTabSpacing(20);
		this.getStyles().setFirstTrackSpacing(20);
		this.getStyles().setTrackSpacing(10);
		this.getStyles().setFirstNoteSpacing(10f);
		this.getStyles().setMeasureLeftSpacing(15f);
		this.getStyles().setMeasureRightSpacing(15f);
		this.getStyles().setClefSpacing(30f);
		this.getStyles().setKeySignatureSpacing(15f);
		this.getStyles().setTimeSignatureSpacing(15f);
		this.getStyles().setChordFretIndexSpacing(8);
		this.getStyles().setChordStringSpacing(5);
		this.getStyles().setChordFretSpacing(6);
		this.getStyles().setChordNoteSize(4);
		this.getStyles().setChordLineWidth(1);
		this.getStyles().setRepeatEndingSpacing(20);
		this.getStyles().setTextSpacing(15);
		this.getStyles().setMarkerSpacing(15);
		this.getStyles().setLoopMarkerSpacing(5);
		this.getStyles().setDivisionTypeSpacing(10);
		this.getStyles().setEffectSpacing(8);
		this.getStyles().setLineWidths(new float[] {0, 1, 2, 3, 4, 5});
		this.getStyles().setDurationWidths(new float[] {30f, 25f, 21f, 20f,19f,18f});
		
		this.getStyles().setDefaultFont(new TGFontModel("Default", 10, false, false) );
		this.getStyles().setNoteFont(new TGFontModel("Default", 10, false, false) );
		this.getStyles().setTimeSignatureFont(new TGFontModel("Default", 14, true, false) );
		this.getStyles().setLyricFont(new TGFontModel("Default", 10, false, false) );
		this.getStyles().setTextFont(new TGFontModel("Default", 10, false, false));
		this.getStyles().setMarkerFont(new TGFontModel("Default", 10, false, false));
		this.getStyles().setGraceFont(new TGFontModel("Default", 8, false, false));
		this.getStyles().setChordFont(new TGFontModel("Default", 10, false, false));
		this.getStyles().setChordFretFont(new TGFontModel("Default", 8, false, false));
		
		this.getStyles().setBackgroundColor( new TGColorModel(255,255,255) );
		this.getStyles().setLineColor(  new TGColorModel(214,214,214)  );
		this.getStyles().setScoreNoteColor( new TGColorModel(64,64,64));
		this.getStyles().setTabNoteColor(new TGColorModel(64,64,64));
		this.getStyles().setPlayNoteColor(new TGColorModel(64,64,64));
		this.getStyles().setLoopSMarkerColor(new TGColorModel(0,0,0));
		this.getStyles().setLoopEMarkerColor(new TGColorModel(0,0,0));
	}
}
