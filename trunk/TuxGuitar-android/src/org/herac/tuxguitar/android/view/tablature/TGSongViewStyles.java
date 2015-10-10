package org.herac.tuxguitar.android.view.tablature;

import org.herac.tuxguitar.graphics.TGColorModel;
import org.herac.tuxguitar.graphics.TGFontModel;
import org.herac.tuxguitar.graphics.control.TGLayoutStyles;

public class TGSongViewStyles {
	
	public TGSongViewStyles() {
		super();
	}
	
	public void configureStyles(TGLayoutStyles styles) {
		styles.setBufferEnabled(false);
		
		styles.setTrackSpacing(5);
		styles.setFirstTrackSpacing(15);
		styles.setFirstMeasureSpacing(5);
		
		styles.setStringSpacing( 10 );
		styles.setScoreLineSpacing( 8 );
		
		styles.setMinBufferSeparator(20);
		styles.setMinTopSpacing(10);
		styles.setMinScoreTabSpacing( 5 );
		
		styles.setChordFretIndexSpacing(8);
		styles.setChordStringSpacing(5);
		styles.setChordFretSpacing(6);
		styles.setChordNoteSize(4);
		styles.setRepeatEndingSpacing(20);
		styles.setTextSpacing(15);
		styles.setMarkerSpacing(15);
		styles.setLoopMarkerSpacing(5);
		styles.setDivisionTypeSpacing(10);
		styles.setEffectSpacing(8);
		
		styles.setDefaultFont( new TGFontModel("sans-serif", 8, false , false ) );
		styles.setNoteFont( new TGFontModel("sans-serif", 9 , true ,false )  );
		styles.setTimeSignatureFont( new TGFontModel("sans-serif", 18 , false , false )  );
		styles.setLyricFont( new TGFontModel("sans-serif", 8 , false , false )  );
		styles.setTextFont( new TGFontModel("sans-serif", 8 , false , false )  );
		styles.setMarkerFont( new TGFontModel("sans-serif", 8 , false , false )  );
		styles.setGraceFont( new TGFontModel("sans-serif", 6 , false , false )  );
		styles.setChordFont( new TGFontModel("sans-serif", 8 , false , false )  );
		styles.setChordFretFont( new TGFontModel("sans-serif", 8 , false , false )  );
		styles.setBackgroundColor( new TGColorModel(255, 255, 255 ));
		styles.setLineColor( new TGColorModel(200,200,200 ));
		styles.setScoreNoteColor( new TGColorModel(105, 105, 105 ) );
		styles.setTabNoteColor( new TGColorModel(105, 105, 105 ) );
		styles.setPlayNoteColor( new TGColorModel(255, 0, 0 ) );
		styles.setLoopSMarkerColor( new TGColorModel(0, 0, 0 ) );
		styles.setLoopEMarkerColor( new TGColorModel(0, 0, 0 ) );
	}
}
