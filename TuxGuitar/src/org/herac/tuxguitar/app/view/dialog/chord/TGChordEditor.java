/*
 * Created on 28-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.view.dialog.chord;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.graphics.TGColorImpl;
import org.herac.tuxguitar.app.graphics.TGPainterImpl;
import org.herac.tuxguitar.app.system.color.TGColorManager;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.control.TGChordImpl;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVelocities;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UIMouseUpListener;
import org.herac.tuxguitar.ui.event.UIPaintEvent;
import org.herac.tuxguitar.ui.event.UIPaintListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIPosition;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.widget.UICanvas;
import org.herac.tuxguitar.ui.widget.UIContainer;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIScrollBar;
import org.herac.tuxguitar.ui.widget.UIScrollBarPanel;
import org.herac.tuxguitar.ui.widget.UISeparator;
import org.herac.tuxguitar.ui.widget.UITextField;
/**
 * @author julian
 * @author Nikola Kolarovic
 */
public class TGChordEditor {
	
	public static final int STRING_SPACING = 30;
	public static final int FRET_SPACING = 30;
	public static final short MIN_FRET = 1;
	public static final short MAX_FRET = 24;
	
	private TGChordDialog dialog;
	private UIPanel control;
	private UIScrollBarPanel scrollBarPanel;
	private UICanvas canvas;
	private UITextField chordName;
	private List<UIPosition> points;
	private boolean[] firstFrets;
	private int[] strings;
	private int[] frets;
	private short fret;
	private short maxStrings;
	private int width;
	private int height;
	private TGTrack currentTrack;
	
	public TGChordEditor(TGChordDialog dialog, UIContainer parent, short maxStrings) {		
		this.dialog = dialog;
		this.createControl(parent, maxStrings);
	}
	
	public void createControl(UIContainer parent, short maxStrings) {
		final UIFactory uiFactory = this.dialog.getUIFactory();
		UITableLayout layout = new UITableLayout(0f);
		
		this.control = uiFactory.createPanel(parent, true);
		this.control.setLayout(layout);
		
		this.fret = MIN_FRET;
		this.maxStrings = maxStrings;
		this.firstFrets = new boolean[this.maxStrings];
		this.strings = new int[this.maxStrings];
		this.frets = new int[TGChordImpl.MAX_FRETS];
		this.width = ((STRING_SPACING * this.maxStrings) - STRING_SPACING);
		this.height = ((FRET_SPACING * TGChordImpl.MAX_FRETS) - FRET_SPACING);
		this.points = new ArrayList<UIPosition>();
		
		for (int i = 0; i < this.firstFrets.length; i++) {
			this.firstFrets[i] = false;
		}
		
		for (int i = 0; i < this.strings.length; i++) {
			this.strings[i] = ((i + 1) * STRING_SPACING);
		}
		
		for (int i = 0; i < this.frets.length; i++) {
			this.frets[i] = ((i + 1) * FRET_SPACING);
		}
		
		UITableLayout compositeLayout = new UITableLayout();
		UIPanel composite = uiFactory.createPanel(this.control, false);
		composite.setLayout(compositeLayout);
		layout.set(composite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UITableLayout scrollBarLayout = new UITableLayout(0f);
		this.scrollBarPanel = uiFactory.createScrollBarPanel(composite, true, false, true);
		this.scrollBarPanel.setLayout(scrollBarLayout);
		compositeLayout.set(this.scrollBarPanel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.canvas = uiFactory.createCanvas(this.scrollBarPanel, false);
		
		float minimumWidth = (getWidth() + (STRING_SPACING * 2f));
		float minimumHeight = (getHeight() + (FRET_SPACING * 2f));
		scrollBarLayout.set(this.canvas, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, minimumWidth, minimumHeight, 0f);
		
		UITableLayout nameLayout = new UITableLayout(0f);
		UIPanel nameComposite = uiFactory.createPanel(composite, false);
		nameComposite.setLayout(nameLayout);
		compositeLayout.set(nameComposite, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_BOTTOM, true, true);
		
		UISeparator separator = uiFactory.createHorizontalSeparator(nameComposite);
		nameLayout.set(separator, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_BOTTOM, true, true);
		
		UILabel chordNameLabel = uiFactory.createLabel(nameComposite);
		chordNameLabel.setText(TuxGuitar.getProperty("chord.name"));
		nameLayout.set(chordNameLabel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_BOTTOM, true, false);
		
		this.chordName = uiFactory.createTextField(nameComposite);
		nameLayout.set(this.chordName, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_BOTTOM, true, false);
		
		this.canvas.setBgColor(this.dialog.getColor(TGColorManager.COLOR_WHITE));
		this.canvas.addPaintListener(new UIPaintListener() {
			public void onPaint(UIPaintEvent event) {
				paintEditor(new TGPainterImpl(uiFactory, event.getPainter()));
			}
		});
		
		this.canvas.addMouseUpListener(new UIMouseUpListener() {
			public void onMouseUp(UIMouseEvent event) {
				getComposite().setFocus();
				checkPoint(event.getPosition().getX(), event.getPosition().getY());
				redraw();
			}
		});
		
		final UIScrollBar uiScrollBar = this.scrollBarPanel.getVScroll();
		uiScrollBar.setIncrement(1);
		uiScrollBar.setMaximum(((MAX_FRET + MIN_FRET) - (TGChordImpl.MAX_FRETS - 1) + 1));
		uiScrollBar.setMinimum(MIN_FRET);
		uiScrollBar.setThumb(1);
		uiScrollBar.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				setFret((short) uiScrollBar.getValue(), false, true);
				redraw();
			}
		});
	}
	
	private void paintEditor(TGPainter painter) {
		int noteSize = (FRET_SPACING / 2);
		
		// fill background
		UIRectangle bounds = this.canvas.getBounds();
		painter.setBackground(new TGColorImpl(this.dialog.getColor(TGColorManager.COLOR_WHITE)));
		painter.initPath(TGPainter.PATH_FILL);
		painter.addRectangle(0, 0, bounds.getWidth(), bounds.getHeight());
		painter.closePath();
		
		painter.setForeground(new TGColorImpl(this.dialog.getColor(TGColorManager.COLOR_BLACK)));
		
		// dibujo el puente
		painter.initPath();
		painter.setAntialias(false);
		painter.moveTo((STRING_SPACING - 10), (FRET_SPACING - 10));
		painter.lineTo(STRING_SPACING + (this.width + 10), (FRET_SPACING - 10));
		painter.closePath();
		
		painter.drawString(Integer.toString(getFret()), FRET_SPACING - 25,STRING_SPACING);
		
		// dibujo las cuerdas
		painter.initPath();
		painter.setAntialias(false);
		for (int i = 0; i < this.strings.length; i++) {
			painter.moveTo(this.strings[i], FRET_SPACING);
			painter.lineTo(this.strings[i], FRET_SPACING + this.height);
		}
		painter.closePath();
		
		// dibujo las cegillas
		painter.initPath();
		painter.setAntialias(false);
		for (int i = 0; i < this.frets.length; i++) {
			painter.moveTo(STRING_SPACING, this.frets[i]);
			painter.lineTo(STRING_SPACING + this.width, this.frets[i]);
		}
		painter.closePath();
		
		// dibujo las notas
		painter.setBackground(new TGColorImpl(this.dialog.getColor(TGColorManager.COLOR_BLACK)));
		
		for(UIPosition point : this.points) {
			painter.initPath(TGPainter.PATH_FILL);
			painter.addOval(point.getX() - (noteSize / 2), point.getY() + (noteSize / 2),noteSize, noteSize);
			painter.closePath();
		}
		
		// dibujo las notas al aire
		for (int i = 0; i < this.firstFrets.length; i++) {
			if (!hasPoints(i)) {
				painter.initPath();
				if (this.firstFrets[i]) {
					int x = this.strings[i] - (noteSize / 2);
					int y = (FRET_SPACING - noteSize) - 11;
					painter.addOval(x, y, (noteSize - 1), (noteSize - 1));
				} else {
					int x = this.strings[i];
					int y = (FRET_SPACING - noteSize) - 4;
					painter.moveTo(x - ((noteSize / 2) - 1), y + ((noteSize / 2) - 1));
					painter.lineTo(x + ((noteSize / 2) - 1), y - ((noteSize / 2) - 1));
					painter.moveTo(x - ((noteSize / 2) - 1), y - ((noteSize / 2) - 1));
					painter.lineTo(x + ((noteSize / 2) - 1), y + ((noteSize / 2) - 1));
				}
				painter.closePath();
			}
		}
	}
	
	private void checkPoint(float x, float y) {
		int stringIndex = getStringIndex(x);
		int fretIndex = getFretIndex(y);
		
		if (y < FRET_SPACING) {
			this.firstFrets[stringIndex] = !this.firstFrets[stringIndex];
			this.removePointsAtStringLine(this.strings[stringIndex]);
		} else if (y < (FRET_SPACING * TGChordImpl.MAX_FRETS)) {
			UIPosition point = new UIPosition(this.strings[stringIndex], this.frets[fretIndex]);
			if (!this.removePoint(point)) {
				this.firstFrets[stringIndex] = false;
				this.removePointsAtStringLine(this.strings[stringIndex]);
				this.addPoint(point);
				this.orderPoints();
			}
		}
		else{
			return; // don't recognize it otherwise
		}
		
		// after changing a chord, recognize it
		this.dialog.getRecognizer().recognize(getChord(),true,false);
	}
	
	private boolean removePoint(UIPosition point) {
		for(UIPosition currPoint : this.points) {
			if( currPoint.getX() == point.getX() && currPoint.getY() == point.getY() ) {
				this.points.remove(point);
				return true;
			}
		}
		return false;
	}
	
	private void orderPoints() {
		for (int i = 0; i < this.points.size(); i++) {
			UIPosition minPoint = null;
			for (int noteIdx = i; noteIdx < this.points.size(); noteIdx++) {
				UIPosition point = this.points.get(noteIdx);
				if (minPoint == null || point.getX() < minPoint.getX()) {
					minPoint = point;
				}
			}
			this.points.remove(minPoint);
			this.points.add(i, minPoint);
		}
	}
	
	private void removePointsAtStringLine(float x) {
		for(UIPosition point : this.points) {
			if (point.getX() == x) {
				this.points.remove(point);
				break;
			}
		}
	}
	
	private void addPoint(UIPosition point) {
		this.points.add(point);
	}
	
	private int getStringIndex(float x) {
		int index = -1;
		for (int i = 0; i < this.strings.length; i++) {
			if (index < 0) {
				index = i;
			} else {
				float distanceX = Math.abs(x - this.strings[index]);
				float currDistanceX = Math.abs(x - this.strings[i]);
				if ( currDistanceX < distanceX) {
					index = i;
				}
			}
		}
		return index;
	}
	
	private int getFretIndex(float y) {
		int index = -1;
		for (int i = 0; i < this.frets.length; i++) {
			if (index < 0) {
				index = i;
			} else {
				float distanceX = Math.abs(y - (this.frets[index] + (FRET_SPACING / 2)));
				float currDistanceX = Math.abs(y - (this.frets[i] + (FRET_SPACING / 2)));
				if ( currDistanceX < distanceX) {
					index = i;
				}
			}
		}
		return index;
		
	}
	
	private boolean hasPoints(int stringIndex) {
		for(UIPosition point : this.points) {
			if (point.getX() == this.strings[stringIndex]) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isEmpty() {
		return this.points.isEmpty();
	}
	
	public int getValue(int string) {
		int value = -1;
		if (this.firstFrets[this.maxStrings - string]) {
			value = 0;
		}
		
		if (value < 0) {
			for(UIPosition point : this.points) {
				if (string == (this.maxStrings - getStringIndex(point.getX()))) {
					value = (getFretIndex(point.getY() + (FRET_SPACING / 2)) + 1);
					value += (getFret() - 1);
				}
			}
		}
		return value;
	}
	
	public void addValue(int value, int string) {
		int realValue = value;
		if (string >= 1 && string <= this.maxStrings) {
			this.firstFrets[this.maxStrings - string] = false;
			this.removePointsAtStringLine(this.strings[this.maxStrings - string]);
			if (realValue == 0) {
				this.firstFrets[this.maxStrings - string] = true;
			} else if (realValue >= 0) {
				realValue -= (getFret() - 1);
				if (realValue > 0 && realValue <= TGChordImpl.MAX_FRETS) {
					this.addPoint(new UIPosition(this.strings[this.maxStrings - string], this.frets[realValue - 1]));
				}
			}
		}
	}
	
	public short getFret() {
		return this.fret;
	}
	
	public void setFret(short fret) {
		setFret(fret, true, false);
	}
	
	private void setFret(short fret, boolean updateScroll, boolean recognize) {
		if (fret >= MIN_FRET && fret <= MAX_FRET) {
			this.fret = fret;
		}
		
		if (updateScroll) {
			this.scrollBarPanel.getVScroll().setValue(this.fret);
		}
		
		if(recognize){
			this.dialog.getRecognizer().recognize(getChord(), true,false);
		}
	}
	
	public TGChord getChord() {
		TGChord chord = TuxGuitar.getInstance().getSongManager().getFactory().newChord(this.strings.length);
		chord.setName(this.chordName.getText());
		chord.setFirstFret(this.fret);
		for (int i = 0; i < chord.getStrings().length; i++) {
			chord.addFretValue(i, getValue(i + 1));
		}
		return chord;
	}
	
	public void setChord(TGChord chord) {
		if (chord != null) {
			this.setFret((short)chord.getFirstFret());
			for (int i = 0; i < chord.getStrings().length; i++) {
				int fretValue = chord.getFretValue(i);
				this.addValue(fretValue, i + 1);
			}
			
			String name = chord.getName();
			
			this.dialog.getRecognizer().recognize(getChord(), (name == null), (name == null) );
			
			this.previewChord(chord);
			
			if(name != null){
				this.setChordName( name );
			}
			
			this.redraw();
		}
	}
	
	public short getMaxStrings() {
		return this.maxStrings;
	}
	
	public void setMaxStrings(short maxStrings) {
		this.maxStrings = maxStrings;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public UICanvas getComposite(){
		return this.canvas;
	}
	
	public UITextField getChordName() {
		return this.chordName;
	}
	
	public void setChordName(String chordName) {
		this.chordName.setText(chordName);
	}
	
	public void redraw() {
		this.canvas.redraw();
	}
	
	public void setCurrentTrack(TGTrack track) {
		this.currentTrack = track;
	}
	
	public TGTrack getCurrentTrack() {
		return this.currentTrack;
	}
	
	public UIPanel getControl() {
		return control;
	}
	
	public void previewChord(final TGChord chord) {
		
		new Thread(new Runnable() {
			public void run() {
				int playedStrings = 0;
				int stringCount = Math.min( getMaxStrings(), chord.countStrings() );
				for (int i = 0; i < stringCount; i++) {
					if (chord.getFretValue( i ) != -1) {
						playedStrings ++;
					}
				}
				int next = 0;
				int[][] beat = new int[playedStrings][2];
				for (int i = 0; i < stringCount; i++) {
					int string = (stringCount - i);
					int value = chord.getFretValue(string - 1);
					if (value != -1) {
						beat[next][0] = getCurrentTrack().getOffset() + getCurrentTrack().getString(string).getValue() + value;
						beat[next][1] = TGVelocities.DEFAULT;
						next ++;
					}
				}
				
				TGSong song = TuxGuitar.getInstance().getDocumentManager().getSong();
				TGChannel channel = TuxGuitar.getInstance().getSongManager().getChannel(song, getCurrentTrack().getChannelId());
				if( channel != null ){
					TuxGuitar.getInstance().getPlayer().playBeat(
						channel.getChannelId(),
						channel.getBank(),
						channel.getProgram(),
						channel.getVolume(),
						channel.getBalance(),
						channel.getChorus(),
						channel.getReverb(),
						channel.getPhaser(),
						channel.getTremolo(),
						beat,
						200,
						200
					);
				}
			}
		}).start();
	}
}
