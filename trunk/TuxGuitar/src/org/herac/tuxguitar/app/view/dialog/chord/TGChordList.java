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

import org.herac.tuxguitar.app.graphics.TGColorImpl;
import org.herac.tuxguitar.app.graphics.TGFontImpl;
import org.herac.tuxguitar.app.graphics.TGPainterImpl;
import org.herac.tuxguitar.app.system.color.TGColorManager;
import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.control.TGChordImpl;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.TGResourceBuffer;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UIMouseUpListener;
import org.herac.tuxguitar.ui.event.UIPaintEvent;
import org.herac.tuxguitar.ui.event.UIPaintListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIFontModel;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.widget.UICanvas;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UIScrollBar;
import org.herac.tuxguitar.ui.widget.UIScrollBarPanel;
/**
 * @author julian
 * 
 * Component that shows the list of (alternative) chords - bottom of the screen
 */
public class TGChordList {
	
	private static final int SCROLL_INCREMENT = 25;
	private static final float MIN_HEIGHT = 160f;
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
	private UIScrollBarPanel control;
	private UICanvas canvas;
	private UIFont font;
	
	public TGChordList(TGChordDialog dialog, UIContainer parent, TGBeat beat) {
		this.graphicChords = new ArrayList<TGChord>();
		this.resourceBuffer = new TGResourceBuffer();
		this.dialog = dialog;
		this.beat = beat;
		this.createControl(parent);
	}
	
	public void createControl(UIContainer parent) {
		final UIFactory uiFactory = this.dialog.getUIFactory();
		
		UITableLayout scrollBarLayout = new UITableLayout(0f);
		this.control = uiFactory.createScrollBarPanel(parent, true, false, true);
		this.control.setLayout(scrollBarLayout);
		
		this.canvas = uiFactory.createCanvas(this.control, false);
		this.canvas.setBgColor(this.dialog.getColor(TGColorManager.COLOR_WHITE));
		this.canvas.addPaintListener(new UIPaintListener() {
			public void onPaint(UIPaintEvent event) {
				paintChords(new TGPainterImpl(uiFactory, event.getPainter()));
			}
		});
		this.canvas.addMouseUpListener(new UIMouseUpListener() {
			public void onMouseUp(UIMouseEvent event) {
				getComposite().setFocus();
				getDialog().getEditor().setChord(getChord(event.getPosition().getX(), event.getPosition().getY(), true));
				redraw();
			}
		});
		scrollBarLayout.set(this.canvas, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, MIN_HEIGHT, 0f);
		
		final UIScrollBar uiScrollBar = this.control.getVScroll();
		uiScrollBar.setIncrement(SCROLL_INCREMENT);
		uiScrollBar.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				redraw();
			}
		});
		
		this.control.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				disposeChords();
				disposeFont();
			}
		});
	}
	
	public void redraw(){
		this.canvas.redraw();
	}
	
	private void fillBackground(TGPainter painter) {
		UIRectangle bounds = this.canvas.getBounds();
		painter.setBackground(new TGColorImpl(this.dialog.getColor(TGColorManager.COLOR_WHITE)));
		painter.initPath(TGPainter.PATH_FILL);
		painter.addRectangle(0, 0, bounds.getWidth(), bounds.getHeight());
		painter.closePath();
	}
	
	private void paintChords(TGPainter painter) {
		this.fillBackground(painter);
		
		float maxHeight = 0;
		float fromX = 15;
		float fromY = 10;
		float vScroll = this.control.getVScroll().getValue();
		Iterator<TGChord> it = this.graphicChords.iterator();
		while (it.hasNext()) {
			TGChordImpl chord = (TGChordImpl) it.next();
			
			TGColor color = new TGColorImpl(getChordColor(chord));
			chord.registerBuffer(this.resourceBuffer);
			chord.setBackgroundColor(new TGColorImpl(this.dialog.getColor(TGColorManager.COLOR_WHITE)));
			chord.setColor(color);
			chord.setNoteColor(color);
			chord.setTonicColor(new TGColorImpl(this.dialog.getColor(TGColorManager.COLOR_DARK_RED)));
			chord.setFirstFretSpacing(CHORD_FIRST_FRET_SPACING);
			chord.setStringSpacing(CHORD_STRING_SPACING);
			chord.setFretSpacing(CHORD_FRET_SPACING);
			chord.setNoteSize(CHORD_NOTE_SIZE);
			chord.setLineWidth(CHORD_LINE_WIDTH);
			chord.setFirstFretFont(new TGFontImpl(getFont()));
			chord.setStyle(TGLayout.DISPLAY_CHORD_DIAGRAM);
			chord.update(painter, this.resourceBuffer);
			if( fromX + chord.getWidth() >= ((this.control.getBounds().getX() + this.control.getBounds().getWidth()) - 20)){
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
	
	private UIColor getChordColor(TGChordImpl chord){
		if(this.selectedChord != null && this.selectedChord.equals(chord)){
			return this.dialog.getColor(TGColorManager.COLOR_BLUE);
		}
		return this.dialog.getColor(TGColorManager.COLOR_BLACK);
	}
	
	public void updateScroll() {
		UIRectangle bounds = this.canvas.getBounds();
		
		UIScrollBar uiScrollBar = this.control.getVScroll();
		uiScrollBar.setMaximum(Math.max(Math.round(this.height - bounds.getHeight()), 0));
		uiScrollBar.setThumb(Math.round(bounds.getHeight()));
	}
	
	private UIFont getFont(){
		if( this.font == null || this.font.isDisposed() ){
			UIFont font = this.control.getFont();
			UIFontModel model = new UIFontModel((font != null ? font.getName() : null), 7, true, false);
			
			this.font = this.dialog.getUIFactory().createFont(model);
		}
		return this.font;
	}
	
	private TGChordImpl getChord(float x, float y, boolean setAsSelected) {
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
			chord.setTonic( TGChordList.this.dialog.getSelector().getTonicList().getSelectedValue() );
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
	
	public UIScrollBarPanel getControl() {
		return control;
	}
	
	public UICanvas getComposite(){
		return this.canvas;
	}
	
	public TGChordDialog getDialog(){
		return this.dialog;
	}
}