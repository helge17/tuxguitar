/*
 * Created on 28-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.editors.chord;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.TGPainter;
import org.herac.tuxguitar.gui.editors.tab.TGChordImpl;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVelocities;
/**
 * @author julian
 * @author Nikola Kolarovic
 */
public class ChordEditor extends Composite {
	
	public static final int STRING_SPACING = 30;
	public static final int FRET_SPACING = 30;
	public static final short MIN_FRET = 1;
	public static final short MAX_FRET = 24;
	
	private ChordDialog dialog;
	private Composite composite;
	private Text chordName;
	private List points;
	private boolean[] firstFrets;
	private int[] strings;
	private int[] frets;
	private short fret;
	private short maxStrings;
	private int width;
	private int height;
	private TGTrack currentTrack = null;
	
	public ChordEditor(Composite parent, int style) {
		super(parent, style);
	}
	
	public ChordEditor(ChordDialog dialog, Composite parent,int style, short maxStrings) {
		this(parent, style);
		this.dialog = dialog;
		this.setLayout(dialog.gridLayout(1, false, 0, 0));
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.init(maxStrings);
	}
	
	public void init(short maxStrings) {
		this.fret = MIN_FRET;
		this.maxStrings = maxStrings;
		this.firstFrets = new boolean[this.maxStrings];
		this.strings = new int[this.maxStrings];
		this.frets = new int[TGChordImpl.MAX_FRETS];
		this.width = ((STRING_SPACING * this.maxStrings) - STRING_SPACING);
		this.height = ((FRET_SPACING * TGChordImpl.MAX_FRETS) - FRET_SPACING);
		this.points = new ArrayList();
		
		for (int i = 0; i < this.firstFrets.length; i++) {
			this.firstFrets[i] = false;
		}
		
		for (int i = 0; i < this.strings.length; i++) {
			this.strings[i] = ((i + 1) * STRING_SPACING);
		}
		
		for (int i = 0; i < this.frets.length; i++) {
			this.frets[i] = ((i + 1) * FRET_SPACING);
		}
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		this.composite = new Composite(composite, SWT.BORDER | SWT.V_SCROLL | SWT.DOUBLE_BUFFERED);
		
		Composite nameComposite = new Composite(composite, SWT.NONE);
		nameComposite.setLayout(this.dialog.gridLayout(1, true, 0, 0));
		nameComposite.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true,true));
		
		Label formulaLabel = new Label(nameComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		formulaLabel.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true,true));
		
		Label chordNameLabel = new Label(nameComposite, SWT.LEFT);
		chordNameLabel.setText(TuxGuitar.getProperty("chord.name"));
		chordNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true,false));
		
		this.chordName = new Text(nameComposite, SWT.SINGLE | SWT.BORDER);
		this.chordName.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true,false));
		
		this.composite.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		this.composite.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				TGPainter painter = new TGPainter(e.gc);
				paintEditor(painter);
			}
		});
		
		this.composite.addMouseListener(new MouseAdapter() {
			public void mouseUp(org.eclipse.swt.events.MouseEvent e) {
				getComposite().setFocus();
				checkPoint(e.x, e.y);
				redraw();
			}
		});
		
		this.composite.getVerticalBar().setIncrement(1);
		this.composite.getVerticalBar().setMaximum( ((MAX_FRET + MIN_FRET) - (TGChordImpl.MAX_FRETS - 1) + 1));
		this.composite.getVerticalBar().setMinimum(MIN_FRET);
		this.composite.getVerticalBar().setThumb(1);
		this.composite.getVerticalBar().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setFret((short) getComposite().getVerticalBar().getSelection(), false, true);
				redraw();
			}
		});
		
		this.composite.setLayoutData(makeCompositeData());
	}
	
	private GridData makeCompositeData() {
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = (getWidth() + (STRING_SPACING * 2) + this.composite.getVerticalBar().getSize().x);
		data.minimumHeight = (getHeight() + (FRET_SPACING * 2));
		return data;
	}
	
	protected void paintEditor(TGPainter painter) {
		int noteSize = (FRET_SPACING / 2);
		
		painter.setForeground(this.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		
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
		painter.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		Iterator it = this.points.iterator();
		while (it.hasNext()) {
			Point point = (Point) it.next();
			painter.initPath(TGPainter.PATH_FILL);
			painter.addOval(point.x - (noteSize / 2), point.y + (noteSize / 2),noteSize, noteSize);
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
	
	protected void checkPoint(int x, int y) {
		int stringIndex = getStringIndex(x);
		int fretIndex = getFretIndex(y);
		
		if (y < FRET_SPACING) {
			this.firstFrets[stringIndex] = !this.firstFrets[stringIndex];
			this.removePointsAtStringLine(this.strings[stringIndex]);
		} else if (y < (FRET_SPACING * TGChordImpl.MAX_FRETS)) {
			Point point = new Point(this.strings[stringIndex],this.frets[fretIndex]);
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
	
	private boolean removePoint(Point point) {
		Iterator it = this.points.iterator();
		while (it.hasNext()) {
			Point currPoint = (Point) it.next();
			if (currPoint.x == point.x && currPoint.y == point.y) {
				this.points.remove(point);
				return true;
			}
		}
		return false;
	}
	
	private void orderPoints() {
		for (int i = 0; i < this.points.size(); i++) {
			Point minPoint = null;
			for (int noteIdx = i; noteIdx < this.points.size(); noteIdx++) {
				Point point = (Point) this.points.get(noteIdx);
				if (minPoint == null || point.x < minPoint.x) {
					minPoint = point;
				}
			}
			this.points.remove(minPoint);
			this.points.add(i, minPoint);
		}
	}
	
	private void removePointsAtStringLine(int x) {
		Iterator it = this.points.iterator();
		while (it.hasNext()) {
			Point point = (Point) it.next();
			if (point.x == x) {
				this.points.remove(point);
				break;
			}
		}
	}
	
	private void addPoint(Point point) {
		this.points.add(point);
	}
	
	private int getStringIndex(int x) {
		int index = -1;
		for (int i = 0; i < this.strings.length; i++) {
			if (index < 0) {
				index = i;
			} else {
				int distanceX = Math.abs(x - this.strings[index]);
				int currDistanceX = Math.abs(x - this.strings[i]);
				if (currDistanceX < distanceX) {
					index = i;
				}
			}
		}
		return index;
	}
	
	private int getFretIndex(int y) {
		int index = -1;
		for (int i = 0; i < this.frets.length; i++) {
			if (index < 0) {
				index = i;
			} else {
				int distanceX = Math.abs(y - (this.frets[index] + (FRET_SPACING / 2)));
				int currDistanceX = Math.abs(y - (this.frets[i] + (FRET_SPACING / 2)));
				if (currDistanceX < distanceX) {
					index = i;
				}
			}
		}
		return index;
		
	}
	
	private boolean hasPoints(int stringIndex) {
		Iterator it = this.points.iterator();
		while (it.hasNext()) {
			Point point = (Point) it.next();
			if (point.x == this.strings[stringIndex]) {
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
			Iterator it = this.points.iterator();
			while (it.hasNext()) {
				Point point = (Point) it.next();
				if (string == (this.maxStrings - getStringIndex(point.x))) {
					value = (getFretIndex(point.y + (FRET_SPACING / 2)) + 1);
					value += (getFret() - 1);
				}
			}
		}
		return value;
	}
	
	public void addValue(int value, int string/*, boolean redecorate*/) {
		int realValue = value;
		if (string >= 1 && string <= this.maxStrings) {
			this.firstFrets[this.maxStrings - string] = false;
			this.removePointsAtStringLine(this.strings[this.maxStrings - string]);
			if (realValue == 0) {
				this.firstFrets[this.maxStrings - string] = true;
			} else if (realValue >= 0) {
				realValue -= (getFret() - 1);
				if (realValue > 0 && realValue <= TGChordImpl.MAX_FRETS) {
					this.addPoint(new Point(this.strings[this.maxStrings - string], this.frets[realValue - 1]));
				}
			}
			//INNECESARY CODE
			//this method is called allways from "setChord(c)" 
			//but it is called some times, as Strings has the chord.
			//So i moved it to "setChord" to call "recognize" only one time.
			
			// after adding a value, recognize the current chord
			//this.chordName.setText(this.dialog.getRecognizer().recognize(getChord(), redecorate));
		}
	}
	
	public short getFret() {
		return this.fret;
	}
	
	public void setFret(short fret) {
		setFret(fret, true, false);
	}
	
	protected void setFret(short fret, boolean updateScroll, boolean recognize) {
		if (fret >= MIN_FRET && fret <= MAX_FRET) {
			this.fret = fret;
		}
		
		if (updateScroll) {
			this.composite.getVerticalBar().setSelection(this.fret);
		}
		
		if(recognize){
			this.dialog.getRecognizer().recognize(getChord(), true,false);
		}
	}
	
	public TGChord getChord() {
		TGChord chord = TuxGuitar.instance().getSongManager().getFactory().newChord(this.strings.length);
		chord.setName(this.chordName.getText());
		chord.setFirstFret(this.fret);
		for (int i = 0; i < chord.getStrings().length; i++) {
			chord.addFretValue(i, getValue(i + 1));
			//chord.setName(this.chordName.getText());
		}
		return chord;
	}
	
	public void setChord(TGChord chord) {
		if (chord != null) {
			this.setFret((short)chord.getFirstFret());
			for (int i = 0; i < chord.getStrings().length; i++) {
				int fretValue = chord.getFretValue(i);
				this.addValue(fretValue, i + 1/*, false*/);
			}
			
			//SEE Comment on addValue.
			//this.getChordName().setText(chord.getName() != null ? chord.getName() : this.dialog.getRecognizer().recognize(getChord(),true) );
			
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
	
	protected Composite getComposite(){
		return this.composite;
	}
	
	public Text getChordName() {
		return this.chordName;
	}
	
	public void setChordName(String chordName) {
		this.chordName.setText(chordName);
	}
	
	public void redraw() {
		super.redraw();
		this.composite.redraw();
	}
	
	public void setCurrentTrack(TGTrack track) {
		this.currentTrack = track;
	}
	
	public TGTrack getCurrentTrack() {
		return this.currentTrack;
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
				
				TGChannel ch = getCurrentTrack().getChannel(); 
				TuxGuitar.instance().getPlayer().playBeat(ch.getChannel(),
				                                          ch.getInstrument(),
				                                          ch.getVolume(),
				                                          ch.getBalance(),
				                                          ch.getChorus(),
				                                          ch.getReverb(),
				                                          ch.getPhaser(),
				                                          ch.getTremolo(),
				                                          beat,
				                                          200,
				                                          200 );
			}
		}).start();
	}
}
