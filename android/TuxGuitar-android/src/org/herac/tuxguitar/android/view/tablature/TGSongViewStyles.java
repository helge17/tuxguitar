package org.herac.tuxguitar.android.view.tablature;

import org.herac.tuxguitar.graphics.control.TGLayoutStyles;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.ui.resource.UIFontModel;

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
		this.setChordLineWidth(0);
		this.setRepeatEndingSpacing(20);
		this.setTextSpacing(15);
		this.setMarkerSpacing(15);
		this.setLoopMarkerSpacing(5);
		this.setDivisionTypeSpacing(10);
		this.setEffectSpacing(8);
		
		this.setLineWidths(new float[] {0, 1, 2, 3, 4, 5});
		this.setDurationWidths(new float[] {30f, 25f, 21f, 20f,19f,18f});
		
		this.setDefaultFont( new UIFontModel("sans-serif", 8, false , false ) );
		this.setNoteFont( new UIFontModel("sans-serif", 9 , true ,false )  );
		this.setLyricFont( new UIFontModel("sans-serif", 8 , false , false )  );
		this.setTextFont( new UIFontModel("sans-serif", 8 , false , false )  );
		this.setMarkerFont( new UIFontModel("sans-serif", 8 , false , false )  );
		this.setGraceFont( new UIFontModel("sans-serif", 6 , false , false )  );
		this.setChordFont( new UIFontModel("sans-serif", 8 , false , false )  );
		this.setChordFretFont( new UIFontModel("sans-serif", 8 , false , false )  );
		this.setForegroundColor( new UIColorModel(0, 0, 0 ));
		this.setBackgroundColor( new UIColorModel(255, 255, 255 ));
		this.setLineColor( new UIColorModel(200,200,200 ));
		this.setScoreNoteColor( new UIColorModel(105, 105, 105 ) );
		this.setTabNoteColor( new UIColorModel(105, 105, 105 ) );
		this.setPlayNoteColor( new UIColorModel(255, 0, 0 ) );
		this.setLoopSMarkerColor( new UIColorModel(0, 0, 0 ) );
		this.setLoopEMarkerColor( new UIColorModel(0, 0, 0 ) );
		this.setMeasureNumberColor( new UIColorModel(255, 0, 0 ) );
	}
}
