package app.tuxguitar.app.view.dialog.chord;

import java.util.Iterator;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.icons.TGColorManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.util.TGCursorController;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.note.TGInsertChordAction;
import app.tuxguitar.editor.action.note.TGRemoveChordAction;
import app.tuxguitar.graphics.control.TGChordImpl;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChord;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UIDisposeEvent;
import app.tuxguitar.ui.event.UIDisposeListener;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UICursor;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIWindow;

public class TGChordDialog {

	private UIWindow dialog;
	private TGViewContext context;
	private TGChordEditor editor;
	private TGChordSelector selector;
	private TGChordList list;
	private TGChordRecognizer recognizer;
	private TGCursorController cursorController;

	public TGChordDialog(TGViewContext context) {
		this.context = context;
	}

	public void show() {
		final TGTrack track = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		final TGMeasure measure = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		final TGBeat beat = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
		final TGVoice voice = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE);

		final UIFactory uiFactory = this.getUIFactory();
		final UIWindow uiParent = this.context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();

		TGChordStyleAdapter.appendColors(this.context.getContext());

		this.dialog = uiFactory.createWindow(uiParent, true, false);
		this.dialog.setLayout(dialogLayout);
		this.dialog.setText(TuxGuitar.getProperty("chord.editor"));
		this.dialog.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				TuxGuitar.getInstance().getCustomChordManager().write();
			}
		});

		UITableLayout topLayout = new UITableLayout();
		UIPanel topComposite = uiFactory.createPanel(this.dialog, false);
		topComposite.setLayout(topLayout);
		dialogLayout.set(topComposite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		UITableLayout bottomLayout = new UITableLayout();
		UIPanel bottomComposite = uiFactory.createPanel(this.dialog, false);
		bottomComposite.setLayout(bottomLayout);
		dialogLayout.set(bottomComposite, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		int[] tuning = findCurrentTuning(measure.getTrack());

		//---------------SELECTOR--------------------------------
		this.selector = new TGChordSelector(this, topComposite, tuning, measure.getKeySignature()<=7);
		topLayout.set(this.selector.getControl(), 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		//---------------EDITOR--------------------------------
		this.editor = new TGChordEditor(this, topComposite, (short)tuning.length);
		this.editor.setCurrentTrack(measure.getTrack());
		topLayout.set(this.editor.getControl(), 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		//---------------RECOGNIZER------------------------------------
		this.recognizer = new TGChordRecognizer(this, topComposite);
		topLayout.set(this.recognizer.getControl(), 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 180f, null, null);

		//---------------CUSTOM CHORDS---------------------------------
		TGChordCustomList customList = new TGChordCustomList(this, topComposite);
		topLayout.set(customList.getControl(), 1, 4, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		//---------------LIST--------------------------------
		this.list = new TGChordList(this, bottomComposite, beat);
		bottomLayout.set(this.list.getControl(), 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout();
		UIPanel buttons = uiFactory.createPanel(this.dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);

		final UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				insertChord(track, measure, beat, voice, getEditor().getChord());
				getWindow().dispose();
			}
		});

		UIButton buttonClean = uiFactory.createButton(buttons);
		buttonClean.setText(TuxGuitar.getProperty("clean"));
		buttonClean.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				removeChord(measure, beat);
				getWindow().dispose();
			}
		});

		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				getWindow().dispose();
			}
		});

		buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonClean, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonCancel, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);

		// load the current chord
		this.editor.setChord(findCurrentChord(measure, beat.getStart()));

		TGDialogUtil.openDialog(this.dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
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

	public UIWindow getWindow(){
		return this.dialog;
	}

	public void loadCursor(UICursor cursor) {
		if(!this.dialog.isDisposed()) {
			if( this.cursorController == null || !this.cursorController.isControlling(this.dialog) ) {
				this.cursorController = new TGCursorController(this.getContext().getContext(), this.dialog);
			}
			this.cursorController.loadCursor(cursor);
		}
	}

	public UIColor getColor(String colorId) {
		return TGColorManager.getInstance(this.context.getContext()).getColor(colorId);
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
					if( maxValue < 0 || maxValue < note.getValue()){
						maxValue = note.getValue();
					}
					if( note.getValue() > 0 && (minValue < 0 || minValue > note.getValue())){
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

	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context.getContext()).getFactory();
	}

	public TGViewContext getContext() {
		return this.context;
	}
}