package org.herac.tuxguitar.android.view.tablature;

import org.herac.tuxguitar.graphics.TGColorModel;
import org.herac.tuxguitar.graphics.TGFontModel;
import org.herac.tuxguitar.graphics.control.TGLayoutStyles;

public class TGSongViewStyles extends TGLayoutStyles {
	
	public TGSongViewStyles() {
		this.setBufferEnabled(false);
		
		this.setTrackSpacing(5);
		this.setFirstTrackSpacing(15);
		this.setFirstMeasureSpacing(5);
		
		this.setStringSpacing( 10 );
		this.setScoreLineSpacing( 8 );
		
		this.setMinBufferSeparator(20);
		this.setMinTopSpacing(10);
		this.setMinScoreTabSpacing( 5 );
		
		this.setFirstNoteSpacing(10f);
		this.setMeasureLeftSpacing(15f);
		this.setMeasureRightSpacing(15f);
		this.setClefSpacing(30f);
		this.setKeySignatureSpacing(15f);
		this.setTimeSignatureSpacing(15f);
		
		this.setChordFretIndexSpacing(8);
		this.setChordStringSpacing(5);
		this.setChordFretSpacing(6);
		this.setChordNoteSize(4);
		this.setRepeatEndingSpacing(20);
		this.setTextSpacing(15);
		this.setMarkerSpacing(15);
		this.setLoopMarkerSpacing(5);
		this.setDivisionTypeSpacing(10);
		this.setEffectSpacing(8);
		
		this.setLineWidths(new float[] {0, 1, 2, 3, 4, 5});
		this.setDurationWidths(new float[] {30f, 25f, 21f, 20f,19f,18f});
		
		this.setDefaultFont( new TGFontModel("sans-serif", 8, false , false ) );
		this.setNoteFont( new TGFontModel("sans-serif", 9 , true ,false )  );
		this.setTimeSignatureFont( new TGFontModel("sans-serif", 18 , false , false )  );
		this.setLyricFont( new TGFontModel("sans-serif", 8 , false , false )  );
		this.setTextFont( new TGFontModel("sans-serif", 8 , false , false )  );
		this.setMarkerFont( new TGFontModel("sans-serif", 8 , false , false )  );
		this.setGraceFont( new TGFontModel("sans-serif", 6 , false , false )  );
		this.setChordFont( new TGFontModel("sans-serif", 8 , false , false )  );
		this.setChordFretFont( new TGFontModel("sans-serif", 8 , false , false )  );
		this.setBackgroundColor( new TGColorModel(255, 255, 255 ));
		this.setLineColor( new TGColorModel(200,200,200 ));
		this.setScoreNoteColor( new TGColorModel(105, 105, 105 ) );
		this.setTabNoteColor( new TGColorModel(105, 105, 105 ) );
		this.setPlayNoteColor( new TGColorModel(255, 0, 0 ) );
		this.setLoopSMarkerColor( new TGColorModel(0, 0, 0 ) );
		this.setLoopEMarkerColor( new TGColorModel(0, 0, 0 ) );
	}
}
