/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.tools;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.actions.ActionLock;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.undo.undoables.UndoableJoined;
import org.herac.tuxguitar.gui.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.gui.undo.undoables.track.UndoableTrackGeneric;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.gui.util.MessageDialog;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGSynchronizer;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TransposeAction extends Action{
	public static final String NAME = "action.tools.transpose";
	
	public TransposeAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		showDialog(getEditor().getTablature().getShell());
		return 0;
	}
	
	public void showDialog(Shell shell) {
		final int[] transpositions = new int[25];
		for( int i = 0 ; i < transpositions.length ; i ++ ){
			transpositions[ i ] = ( i - ( transpositions.length / 2 ) );
		}
		
		final Shell dialog = DialogUtils.newDialog(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("tools.transpose"));
		
		//-----------------TEMPO------------------------
		Group group = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout(2,false));
		group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		group.setText(TuxGuitar.getProperty("tools.transpose"));
		
		
		Label transpositionLabel = new Label(group, SWT.NULL);
		transpositionLabel.setText(TuxGuitar.getProperty("tools.transpose.semitones"));
		transpositionLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,true));
		
		final Combo transpositionCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY );
		transpositionCombo.setLayoutData( new GridData(SWT.FILL, SWT.FILL, true , true) );
		for( int i = 0 ; i < transpositions.length ; i ++ ){
			transpositionCombo.add( Integer.toString( transpositions[i]) );
		}
		transpositionCombo.select( ( transpositions.length / 2 ) );
		
		//------------------OPTIONS--------------------------
		Group options = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		options.setLayout(new GridLayout());
		options.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		options.setText(TuxGuitar.getProperty("options"));
		
		final Button applyToAllMeasuresButton = new Button(options, SWT.RADIO);
		applyToAllMeasuresButton.setText(TuxGuitar.getProperty("tools.transpose.apply-to-track"));
		applyToAllMeasuresButton.setSelection(true);
		
		final Button applyToCurrentMeasureButton = new Button(options, SWT.RADIO);
		applyToCurrentMeasureButton.setText(TuxGuitar.getProperty("tools.transpose.apply-to-measure"));
		
		final Button applyToAllTracksButton = new Button(options, SWT.CHECK);
		applyToAllTracksButton.setText(TuxGuitar.getProperty("tools.transpose.apply-to-all-tracks"));
		applyToAllTracksButton.setSelection(true);
		
		final Button applyToChordsButton = new Button(options, SWT.CHECK);
		applyToChordsButton.setText(TuxGuitar.getProperty("tools.transpose.apply-to-chords"));
		applyToChordsButton.setSelection(true);
		
		final Button tryKeepStringButton = new Button(options, SWT.CHECK);
		tryKeepStringButton.setText(TuxGuitar.getProperty("tools.transpose.try-keep-strings"));
		tryKeepStringButton.setSelection(true);
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				int transpositionIndex =  transpositionCombo.getSelectionIndex() ;
				if( transpositionIndex >= 0 && transpositionIndex < transpositions.length ){
					final int transposition = transpositions[ transpositionIndex ];
					final boolean tryKeepString = tryKeepStringButton.getSelection();
					final boolean applyToChords = applyToChordsButton.getSelection();
					final boolean applyToAllTracks = applyToAllTracksButton.getSelection();
					final boolean applyToAllMeasures = applyToAllMeasuresButton.getSelection();
					
					dialog.dispose();
					try {
						TGSynchronizer.instance().runLater(new TGSynchronizer.TGRunnable() {
							public void run() throws Throwable {
								ActionLock.lock();
								TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
								transpose(transposition, tryKeepString, applyToChords , applyToAllMeasures, applyToAllTracks );
								TuxGuitar.instance().updateCache( true );
								TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
								ActionLock.unlock();
							}
						});
					} catch (Throwable throwable) {
						MessageDialog.errorMessage(throwable);
					}
				} else {
					dialog.dispose();
				}
			}
		});
		
		Button buttonCancel = new Button(buttons, SWT.PUSH);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(getButtonData());
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				dialog.dispose();
			}
		});
		
		dialog.setDefaultButton( buttonOK );
		
		DialogUtils.openDialog(dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	public void transpose( int transposition , boolean tryKeepString , boolean applyToChords , boolean applyToAllMeasures , boolean applyToAllTracks) {
		//comienza el undoable
		UndoableJoined undoableJoined = new UndoableJoined();
		
		Caret caret = getEditor().getTablature().getCaret();
		if( applyToAllMeasures ){
			
			if( applyToAllTracks ){
				TGSong song = getSongManager().getSong();
				for( int i = 0 ; i < song.countTracks() ; i ++ ){
					transposeTrack( undoableJoined , song.getTrack( i ) , transposition , tryKeepString , applyToChords);
				}
			} else {
				transposeTrack( undoableJoined , caret.getTrack(), transposition , tryKeepString , applyToChords);
			}
			
			updateTablature();
		}else{
			if( applyToAllTracks ){
				TGSong song = getSongManager().getSong();
				for( int i = 0 ; i < song.countTracks() ; i ++ ){
					TGTrack track = song.getTrack( i );
					TGMeasure measure = getSongManager().getTrackManager().getMeasure(track, caret.getMeasure().getNumber() );
					if( measure != null ){
						transposeMeasure( undoableJoined, measure, transposition , tryKeepString , applyToChords);
					}
				}
			} else {
				transposeMeasure( undoableJoined , caret.getMeasure(), transposition , tryKeepString , applyToChords);
			}
			fireUpdate( caret.getMeasure().getNumber() );
		}
		
		//termia el undoable
		if( !undoableJoined.isEmpty() ){
			addUndoableEdit(undoableJoined.endUndo());
		}
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
	}
	
	public void transposeMeasure( UndoableJoined undoableJoined , TGMeasure measure, int transposition , boolean tryKeepString , boolean applyToChords ) {
		if( transposition != 0 && !measure.getTrack().isPercussionTrack() ){
			//comienza el undoable
			UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo( measure );
			
			getSongManager().getMeasureManager().transposeNotes( measure , transposition , tryKeepString , applyToChords , -1 );
			
			//termia el undoable
			undoableJoined.addUndoableEdit( undoable.endUndo( measure ) );
		}
	}
	
	public void transposeTrack( UndoableJoined undoableJoined , TGTrack track, int transposition , boolean tryKeepString , boolean applyToChords ) {
		if( transposition != 0 && !track.isPercussionTrack() ){
			//comienza el undoable
			UndoableTrackGeneric undoable = UndoableTrackGeneric.startUndo( track );
			
			getSongManager().getTrackManager().transposeNotes( track , transposition , tryKeepString , applyToChords, -1 );
			
			//termia el undoable
			undoableJoined.addUndoableEdit( undoable.endUndo( track ) );
		}
	}
}
