/*


 * Created on 02-ene-2006


 *


 * TODO To change the template for this generated file go to


 * Window - Preferences - Java - Code Style - Code Templates


 */

package org.herac.tuxguitar.app.view.dialog.chord;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.herac.tuxguitar.app.graphics.TGColorImpl;
import org.herac.tuxguitar.app.graphics.TGFontImpl;
import org.herac.tuxguitar.app.graphics.TGPainterImpl;
import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.control.TGChordImpl;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.TGResourceBuffer;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGString;
/**
 * @author julian
 * 
 * Component that shows the list of (alternative) chords - bottom of the screen
 */
public class TGChordList extends Composite {
	
	private static final int MIN_HEIGHT = 160;
	private static final int SCROLL_INCREMENT = 25;
	private static final float CHORD_FIRST_FRET_SPACING = 12;
	private static final float CHORD_STRING_SPACING = 8;
	private static final float CHORD_FRET_SPACING = 10;
	private static final float CHORD_NOTE_SIZE = 6;
	private static final float CHORD_LINE_WIDTH = 1f;
	
	private TGChordDialog dialog;
	private TGBeat beat;
	private TGResourceBuffer resourceBuffer;
	private List<TGChord> graphicChords;
	private float height;
	private TGChordImpl selectedChord;
	private Composite composite;
	private Font font;
	
	public TGChordList(TGChordDialog dialog,Composite parent,TGBeat beat) {
		super(parent, SWT.NONE);
		this.setLayout(dialog.gridLayout(1,false,0,0));
		this.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.graphicChords = new ArrayList<TGChord>();
		this.resourceBuffer = new TGResourceBuffer();
		this.dialog = dialog;
		this.beat = beat;
		this.init();
	}
	
	private void init(){
		this.composite = new Composite(this,SWT.BORDER | SWT.V_SCROLL | SWT.DOUBLE_BUFFERED);
		this.composite.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		this.composite.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				TGPainterImpl painter = new TGPainterImpl(e.gc);
				paintChords(painter);
			}
		});
		this.composite.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				getComposite().setFocus();
				getDialog().getEditor().setChord(getChord(e.x, e.y,true));
				redraw();
			}
		});
		
		final Point origin = new Point(0, 0);
		final ScrollBar vBar = this.composite.getVerticalBar();
		vBar.setIncrement(SCROLL_INCREMENT);
		vBar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				int vSelection = vBar.getSelection();
				int destY = -vSelection - origin.y;
				Rectangle rect = getComposite().getBounds();
				getShell().scroll(0, destY, 0, 0, rect.width, rect.height, false);
				origin.y = -vSelection;
				redraw();
			}
		});
		
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumHeight = MIN_HEIGHT;
		this.composite.setLayoutData(data);
		this.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				disposeChords();
				disposeFont();
			}
		});
	}
	
	public void redraw(){
		super.redraw();
		this.composite.redraw();
	}
	
	protected void paintChords(TGPainterImpl painter) {
		float maxHeight = 0;
		float fromX = 15;
		float fromY = 10;
		float vScroll = this.composite.getVerticalBar().getSelection();
		Iterator<TGChord> it = this.graphicChords.iterator();
		while (it.hasNext()) {
			TGChordImpl chord = (TGChordImpl) it.next();
			
			TGColor color = new TGColorImpl(getChordColor(chord));
			chord.registerBuffer(this.resourceBuffer);
			chord.setBackgroundColor(new TGColorImpl(this.composite.getBackground()));
			chord.setColor(color);
			chord.setNoteColor(color);
			chord.setTonicColor(new TGColorImpl(getDisplay().getSystemColor(SWT.COLOR_DARK_RED)));
			chord.setFirstFretSpacing(CHORD_FIRST_FRET_SPACING);
			chord.setStringSpacing(CHORD_STRING_SPACING);
			chord.setFretSpacing(CHORD_FRET_SPACING);
			chord.setNoteSize(CHORD_NOTE_SIZE);
			chord.setLineWidth(CHORD_LINE_WIDTH);
			chord.setFirstFretFont(new TGFontImpl(getFont(painter.getGC())));
			chord.setStyle(TGLayout.DISPLAY_CHORD_DIAGRAM);
			chord.update(painter, this.resourceBuffer);
			if(fromX + chord.getWidth() >= ((getBounds().x + getBounds().width) - 20)){
				fromX = 15;
				fromY += chord.getHeight() + 10;
			}
			chord.setEditing(true);
			chord.setPosX( fromX );
			chord.setPosY( fromY - vScroll);
			chord.paint(painter,(chord.getWidth() / 2),0);
			
			fromX += chord.getWidth() + 10;
			maxHeight = Math.max(maxHeight,chord.getHeight());
		}
		this.height = (fromY + maxHeight + 10);
		this.updateScroll();
	}
	
	private Color getChordColor(TGChordImpl chord){
		if(this.selectedChord != null && this.selectedChord.equals(chord)){
			return getDisplay().getSystemColor(SWT.COLOR_BLUE);
		}
		return getDisplay().getSystemColor(SWT.COLOR_BLACK);
	}
	
	public void updateScroll(){
		Rectangle rect = this.composite.getBounds();
		Rectangle client = this.composite.getClientArea();
		ScrollBar vBar = this.composite.getVerticalBar();
		vBar.setMaximum(Math.round(this.height));
		vBar.setThumb(Math.min(rect.height, client.height));
	}
	
	protected int getTrackString(int number){
		TGString string = TGChordList.this.beat.getMeasure().getTrack().getString(number);
		return string.getValue();
	}
	
	protected Font getFont(GC painter){
		if(this.font == null || this.font.isDisposed()){ 
			Font available = painter.getFont();
			if(available == null || available.isDisposed()){
				available = getDisplay().getSystemFont();
			}
			FontData[] datas = available.getFontData();
			if(datas.length > 0){
				this.font = new Font(getDisplay(),datas[0].getName(),Math.min(7,datas[0].getHeight()),SWT.BOLD);
			}
		}
		return this.font;
	}
	
	protected TGChordImpl getChord(int x, int y,boolean setAsSelected) {
		Iterator<TGChord> it = this.graphicChords.iterator();
		while (it.hasNext()) {
			TGChordImpl chord = (TGChordImpl) it.next();
			float x1 = chord.getPosX();
			float x2 = x1 + chord.getWidth();
			float y1 = chord.getPosY();
			float y2 = y1 + chord.getHeight();
			if (x > x1 && x < x2 && y > y1 && y < y2) {
				if( setAsSelected ){
					if(this.selectedChord != null){
						this.selectedChord.dispose();
					}
					this.selectedChord = chord;
					chord.dispose();
				}
				return chord;
			}
		}
		return null;
	}
	
	public void setChords(List<TGChord> chords) {
		this.disposeChords();
		this.selectedChord = null;
		
		Iterator<TGChord> it = chords.iterator();
		while (it.hasNext()) {
			TGChordImpl chord = (TGChordImpl) it.next();
			chord.setTonic( TGChordList.this.dialog.getSelector().getTonicList().getSelectionIndex() );
			chord.setBeat(TGChordList.this.beat);
			this.graphicChords.add(chord);
		}
		this.redraw();
	}
	
	public void disposeFont(){
		if(this.font != null){
			this.font.dispose();
		}
	}
	
	public void disposeChords(){
		this.graphicChords.clear();
		this.resourceBuffer.disposeAllResources();
	}
	
	protected Composite getComposite(){
		return this.composite;
	}
	
	protected TGChordDialog getDialog(){
		return this.dialog;
	}
}