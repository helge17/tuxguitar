package org.herac.tuxguitar.app.view.dialog.chord;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.note.TGInsertChordAction;
import org.herac.tuxguitar.editor.action.note.TGRemoveChordAction;
import org.herac.tuxguitar.graphics.control.TGChordImpl;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVoice;

public class TGChordDialog {
	
	private static final int DEFAULT_STYLE = SWT.BORDER;
	
	private Shell dialog;
	private TGViewContext context;
	private TGChordEditor editor;
	private TGChordSelector selector;
	private TGChordList list;
	private TGChordRecognizer recognizer;
	
	public TGChordDialog(TGViewContext context) {
		this.context = context;
	}
	
	public void show() {
		final TGTrack track = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		final TGMeasure measure = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		final TGBeat beat = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
		final TGVoice voice= this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE);
		
		final Shell parent = this.context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		
		this.dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.dialog.setLayout(new GridLayout());
		this.dialog.setText(TuxGuitar.getProperty("chord.editor"));
		this.dialog.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				TuxGuitar.getInstance().getCustomChordManager().write();
			}
		});
		
		Composite topComposite = new Composite(this.dialog, SWT.NONE);
		topComposite.setLayout(new GridLayout(4,false));
		topComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		Composite bottomComposite = new Composite(this.dialog, SWT.NONE);  
		bottomComposite.setLayout(new GridLayout());
		bottomComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		int[] tuning = findCurrentTuning(measure.getTrack());
		
		//---------------SELECTOR--------------------------------
		this.selector = new TGChordSelector(this,topComposite,DEFAULT_STYLE, tuning);
		this.selector.pack();
		
		//---------------EDITOR--------------------------------
		this.editor = new TGChordEditor(this, topComposite, DEFAULT_STYLE,(short)tuning.length);
		this.editor.pack();
		
		this.editor.setCurrentTrack(measure.getTrack());
		
		//---------------RECOGNIZER------------------------------------
		this.recognizer = new TGChordRecognizer(this, topComposite, DEFAULT_STYLE);
		
		//---------------CUSTOM CHORDS---------------------------------
		new TGChordCustomList(this, topComposite, DEFAULT_STYLE,Math.max(this.selector.getBounds().height,this.editor.getBounds().height));
		
		//---------------LIST--------------------------------
		Composite listComposite = new Composite(bottomComposite, SWT.NONE);
		listComposite.setLayout(gridLayout(1,false,0,0));
		listComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.list = new TGChordList(this,listComposite,beat);
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(this.dialog, SWT.NONE);
		buttons.setLayout(gridLayout(3,false,0,0));
		buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true));
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				insertChord(track, measure, beat, voice, getEditor().getChord());
				getDialog().dispose();
			}
		});
		
		Button buttonClean = new Button(buttons, SWT.PUSH);
		buttonClean.setText(TuxGuitar.getProperty("clean"));
		buttonClean.setLayoutData(getButtonData());
		buttonClean.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				removeChord(measure, beat);
				getDialog().dispose();
			}
		});
		
		Button buttonCancel = new Button(buttons, SWT.PUSH);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(getButtonData());
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				getDialog().dispose();
			}
		});
		
		// load the current chord
		this.editor.setChord(findCurrentChord(measure, beat.getStart()));
		
		this.dialog.setDefaultButton( buttonOK );
		
		DialogUtils.openDialog(this.dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}
	
	public TGChordEditor getEditor() {
		return this.editor;
	}
	
	public TGChordSelector getSelector() {
		return this.selector;
	}
	
	public TGChordList getList() {
		return this.list;
	}
	
	public TGChordRecognizer getRecognizer() {
		return this.recognizer;
	}
	
	public boolean isDisposed(){
		return this.dialog.isDisposed();
	}
	
	public Shell getDialog(){
		return this.dialog;
	}
	
	public GridLayout gridLayout(int numColumns,boolean makeColumnsEqualWidth,int marginWidth,int marginHeight){
		GridLayout layout = new GridLayout();
		layout.numColumns = numColumns;
		layout.makeColumnsEqualWidth = makeColumnsEqualWidth;
		layout.marginWidth = (marginWidth >= 0)?marginWidth:layout.marginWidth;
		layout.marginHeight = (marginHeight >= 0)?marginHeight:layout.marginHeight;
		return layout;
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	private int[] findCurrentTuning(TGTrack track){
		int[] tuning = new int[track.stringCount()];
		Iterator<TGString> it = track.getStrings().iterator();
		while(it.hasNext()){
			TGString string = (TGString)it.next();
			tuning[(tuning.length - string.getNumber())] = string.getValue();
		}
		return tuning;
	}
	
	protected TGChord findCurrentChord(TGMeasure measure, long start){
		TGSongManager manager = TuxGuitar.getInstance().getSongManager();
		TGChord chord = manager.getMeasureManager().getChord(measure, start);
		if( chord == null ){
			chord = manager.getFactory().newChord(measure.getTrack().stringCount());
			chord.setFirstFret(1);
			List<TGNote> notes = manager.getMeasureManager().getNotes(measure, start);
			if(!notes.isEmpty()){
				int maxValue = -1;
				int minValue = -1;
				
				//verifico el first fret
				Iterator<TGNote> it = notes.iterator();
				while(it.hasNext()){
					TGNote note = (TGNote)it.next(); 
					if(maxValue < 0 || maxValue < note.getValue()){
						maxValue = note.getValue();
					}
					if(note.getValue() > 0 && (minValue < 0 || minValue > note.getValue())){
						minValue = note.getValue();
					}
				}
				if(maxValue > TGChordImpl.MAX_FRETS  && minValue > 0){
					chord.setFirstFret((short)(minValue));
				}
				
				//agrego los valores
				it = notes.iterator();
				while(it.hasNext()){
					TGNote note = (TGNote)it.next();
					chord.addFretValue( ( note.getString() - 1) , note.getValue());
				}
			}
		}
		return chord;
	}
	
	public void insertChord(TGTrack track, TGMeasure measure, TGBeat beat, TGVoice voice, TGChord chord) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(), TGInsertChordAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE, voice);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHORD, chord);
		tgActionProcessor.processOnNewThread();
	}
	
	public void removeChord(TGMeasure measure, TGBeat beat) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(), TGRemoveChordAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		tgActionProcessor.processOnNewThread();
	}
	
	public TGViewContext getContext() {
		return this.context;
	}
}