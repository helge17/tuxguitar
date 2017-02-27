package org.herac.tuxguitar.io.pdf;

import org.herac.tuxguitar.graphics.TGColorModel;
import org.herac.tuxguitar.graphics.TGFontModel;
import org.herac.tuxguitar.graphics.control.TGLayoutStyles;

import com.itextpdf.text.pdf.BaseFont;

public class PDFLayoutStyles extends TGLayoutStyles {
	
	public PDFLayoutStyles() {
		this.setBufferEnabled(false);
		this.setFirstMeasureSpacing(15);
		this.setMinBufferSeparator(15);
		this.setMinTopSpacing(20);
		this.setMinScoreTabSpacing(15);
		this.setScoreLineSpacing(7);
		this.setFirstTrackSpacing(5);
		this.setTrackSpacing(5);
		this.setStringSpacing(8);
		this.setChordFretIndexSpacing(8);
		this.setChordStringSpacing(4);
		this.setChordFretSpacing(5);
		this.setChordNoteSize(3);
		this.setChordLineWidth(1);
		this.setRepeatEndingSpacing(20);
		this.setTextSpacing(15);
		this.setMarkerSpacing(15);
		this.setDivisionTypeSpacing(10);
		this.setEffectSpacing(8);
		this.setFirstNoteSpacing(10f);
		this.setMeasureLeftSpacing(15f);
		this.setMeasureRightSpacing(15f);
		this.setClefSpacing(30f);
		this.setKeySignatureSpacing(15f);
		this.setTimeSignatureSpacing(15f);
		
		this.setLineWidths(new float[] {0, 1, 2, 3, 4, 5});
		this.setDurationWidths(new float[] {18f, 18f, 16f, 15f, 14f});
		this.setDefaultFont(new TGFontModel(BaseFont.TIMES_ROMAN, 8, false, false));
		this.setNoteFont(new TGFontModel(BaseFont.TIMES_BOLD, 9, true, false));
		this.setTimeSignatureFont(new TGFontModel(BaseFont.TIMES_BOLD, 15, true, false));
		this.setLyricFont(new TGFontModel(BaseFont.TIMES_ROMAN, 8, false, false));
		this.setTextFont(new TGFontModel(BaseFont.TIMES_ROMAN, 8, false, false));
		this.setGraceFont(new TGFontModel(BaseFont.TIMES_ROMAN, 8, false, false));
		this.setChordFont(new TGFontModel(BaseFont.TIMES_ROMAN, 8, false, false));
		this.setChordFretFont(new TGFontModel(BaseFont.TIMES_ROMAN, 8, false, false));
		this.setMarkerFont(new TGFontModel(BaseFont.TIMES_ROMAN, 8, false, false));
		this.setBackgroundColor(new TGColorModel(0xff,0xff,0xff));
		this.setLineColor(new TGColorModel(0x00,0x00,0x00));
		this.setScoreNoteColor(new TGColorModel(0x00,0x00,0x00));
		this.setTabNoteColor(new TGColorModel(0x00,0x00,0x00));
		this.setPlayNoteColor(new TGColorModel(0x00,0x00,0x00));
		this.setLoopSMarkerColor(new TGColorModel(0x00,0x00,0x00));
		this.setLoopEMarkerColor(new TGColorModel(0x00,0x00,0x00));
	}
}
