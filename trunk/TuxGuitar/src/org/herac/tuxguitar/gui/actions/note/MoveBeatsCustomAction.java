package org.herac.tuxguitar.gui.actions.note;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.actions.ActionLock;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.undo.undoables.track.UndoableTrackGeneric;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.gui.util.MessageDialog;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGSynchronizer;

public class MoveBeatsCustomAction extends Action{
	public static final String NAME = "action.beat.general.move-custom";
	
	private static final ComboItem[] MOVE_DIRECTIONS = new ComboItem[]{
		new ComboItem( TuxGuitar.getProperty("beat.move-custom.dialog.direction.right"), new Integer(1) ),
		new ComboItem( TuxGuitar.getProperty("beat.move-custom.dialog.direction.left") , new Integer(-1) ),
	};
	
	private static final ComboItem[] MOVE_DURATIONS = new ComboItem[]{
		new ComboItem( TuxGuitar.getProperty("duration.whole") , new Integer(TGDuration.WHOLE) ),
		new ComboItem( TuxGuitar.getProperty("duration.half") , new Integer(TGDuration.HALF) ),
		new ComboItem( TuxGuitar.getProperty("duration.quarter") , new Integer(TGDuration.QUARTER) ),
		new ComboItem( TuxGuitar.getProperty("duration.eighth") , new Integer(TGDuration.EIGHTH) ),
		new ComboItem( TuxGuitar.getProperty("duration.sixteenth") , new Integer(TGDuration.SIXTEENTH) ),
		new ComboItem( TuxGuitar.getProperty("duration.thirtysecond") , new Integer(TGDuration.THIRTY_SECOND) ),
		new ComboItem( TuxGuitar.getProperty("duration.sixtyfourth") , new Integer(TGDuration.SIXTY_FOURTH) ),
	};
	
	private static final ComboItem[] MOVE_DURATION_TYPES = new ComboItem[]{
		new ComboItem( TuxGuitar.getProperty("beat.move-custom.dialog.duration.type.normal") , new boolean[]{ false, false} ),
		new ComboItem( TuxGuitar.getProperty("duration.dotted") , new boolean[]{ true, false} ),
		new ComboItem( TuxGuitar.getProperty("duration.doubledotted") , new boolean[]{ false, true} ),
	};
	
	private static final ComboItem[] MOVE_DURATION_DIVISIONS = new ComboItem[]{
		new ComboItem( TuxGuitar.getProperty("beat.move-custom.dialog.duration.division-type.normal") , new int[] { 1  , 1} ),
		new ComboItem( "3"    , new int[] { 3  , 2} ),
		new ComboItem( "5"    , new int[] { 5  , 4} ),
		new ComboItem( "6"    , new int[] { 6  , 4} ),
		new ComboItem( "7"    , new int[] { 7  , 4} ),
		new ComboItem( "9"    , new int[] { 9  , 8} ),
		new ComboItem( "10"   , new int[] { 10 , 8} ),
		new ComboItem( "11"   , new int[] { 11 , 8} ),
		new ComboItem( "12"   , new int[] { 12 , 8} ),
	};
	
	public MoveBeatsCustomAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		this.showDialog(getEditor().getTablature().getShell());
		return 0;
	}
	
	protected void showDialog(Shell shell){
		final Shell dialog = DialogUtils.newDialog(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("beat.move-custom.dialog.title"));
		
		//-------direction-------------------------------------
		Group direction = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		direction.setLayout(new GridLayout(2,false));
		direction.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		direction.setText(TuxGuitar.getProperty("beat.move-custom.dialog.direction-tip"));
		
		Label directionLabel = new Label(direction, SWT.RIGHT);
		directionLabel.setText(TuxGuitar.getProperty("beat.move-custom.dialog.direction") + ":");
		
		final Combo directionCombo = new Combo(direction, SWT.DROP_DOWN | SWT.READ_ONLY);
		for( int i = 0 ; i < MOVE_DIRECTIONS.length ; i ++ ){
			directionCombo.add( MOVE_DIRECTIONS[i].getLabel() );
		}
		directionCombo.select( 0 );
		directionCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		//-------move 1------------------------------------------
		final List move1Controls = new ArrayList();
		
		Group move1 = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		move1.setLayout(new GridLayout(2,false));
		move1.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		move1.setText(TuxGuitar.getProperty("beat.move-custom.dialog.move-1.tip"));
		
		Label count1Label = new Label(move1, SWT.RIGHT);
		count1Label.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,true,true));
		count1Label.setText(TuxGuitar.getProperty("beat.move-custom.dialog.count") + ":");
		
		final Spinner count1Spinner = new Spinner(move1 , SWT.BORDER );
		count1Spinner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		count1Spinner.setMinimum(0);
		count1Spinner.setMaximum(100);
		count1Spinner.setIncrement(1);
		count1Spinner.setSelection(0);
		count1Spinner.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateControls( count1Spinner.getSelection(), move1Controls );
			}
		});
		
		Label duration1Label = new Label(move1, SWT.RIGHT);
		duration1Label.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,true,true));
		duration1Label.setText(TuxGuitar.getProperty("beat.move-custom.dialog.duration") + ":");
		move1Controls.add( duration1Label );
		
		final Combo duration1Combo = new Combo(move1, SWT.DROP_DOWN | SWT.READ_ONLY);
		duration1Combo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		for( int i = 0 ; i < MOVE_DURATIONS.length ; i ++ ){
			duration1Combo.add( MOVE_DURATIONS[i].getLabel() );
		}
		duration1Combo.select( 0 );
		move1Controls.add( duration1Combo );
		
		updateControls( 0, move1Controls );
		//-------move 2------------------------------------------
		final List move2Controls = new ArrayList();
		
		Group move2 = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		move2.setLayout(new GridLayout(2,false));
		move2.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		move2.setText(TuxGuitar.getProperty("beat.move-custom.dialog.move-2.tip"));
		
		Label count2Label = new Label(move2, SWT.RIGHT);
		count2Label.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,true,true));
		count2Label.setText(TuxGuitar.getProperty("beat.move-custom.dialog.count") + ":");
		
		final Spinner count2Spinner = new Spinner(move2 , SWT.BORDER );
		count2Spinner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		count2Spinner.setSelection(0);
		count2Spinner.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateControls( count2Spinner.getSelection(), move2Controls );
			}
		});
		
		Label duration2Label = new Label(move2, SWT.RIGHT);
		duration2Label.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,true,true));
		duration2Label.setText(TuxGuitar.getProperty("beat.move-custom.dialog.duration") + ":");
		move2Controls.add( duration2Label );
		
		final Combo duration2Combo = new Combo(move2, SWT.DROP_DOWN | SWT.READ_ONLY);
		duration2Combo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		for( int i = 0 ; i < MOVE_DURATIONS.length ; i ++ ){
			duration2Combo.add( MOVE_DURATIONS[i].getLabel() );
		}
		duration2Combo.select( 2 );
		move2Controls.add( duration2Combo );
		
		Label type2Label = new Label(move2, SWT.RIGHT);
		type2Label.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,true,true));
		type2Label.setText(TuxGuitar.getProperty("beat.move-custom.dialog.duration.type") + ":");
		move2Controls.add( type2Label );
		
		final Combo type2Combo = new Combo(move2, SWT.DROP_DOWN | SWT.READ_ONLY);
		type2Combo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		for( int i = 0 ; i < MOVE_DURATION_TYPES.length ; i ++ ){
			type2Combo.add( MOVE_DURATION_TYPES[i].getLabel() );
		}
		type2Combo.select( 0 );
		move2Controls.add( type2Combo );
		
		Label division2Label = new Label(move2, SWT.RIGHT);
		division2Label.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,true,true));
		division2Label.setText(TuxGuitar.getProperty("beat.move-custom.dialog.duration.division-type") + ":");
		move2Controls.add( division2Label );
		
		final Combo division2Combo = new Combo(move2, SWT.DROP_DOWN | SWT.READ_ONLY);
		division2Combo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		for( int i = 0 ; i < MOVE_DURATION_DIVISIONS.length ; i ++ ){
			division2Combo.add( MOVE_DURATION_DIVISIONS[i].getLabel() );
		}
		division2Combo.select( 0 );
		move2Controls.add( division2Combo );
		
		updateControls( 0, move2Controls );
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true));
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				final int directionValue = getDirection( directionCombo.getSelectionIndex() );
				final long duration1 = getDuration1(duration1Combo.getSelectionIndex(), count1Spinner.getSelection());
				final long duration2 = getDuration2(duration2Combo.getSelectionIndex(),type2Combo.getSelectionIndex(),division2Combo.getSelectionIndex(), count2Spinner.getSelection());
				final long duration = ( ( duration1 + duration2 ) * directionValue );
				
				dialog.dispose();
				try {
					TGSynchronizer.instance().runLater(new TGSynchronizer.TGRunnable() {
						public void run() throws Throwable {
							ActionLock.lock();
							TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
							moveBeats(duration);
							TuxGuitar.instance().updateCache( true );
							TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
							ActionLock.unlock();
						}
					});
				} catch (Throwable throwable) {
					MessageDialog.errorMessage(throwable);
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
	
	protected GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	protected int getDirection( int index ){
		if( index >= 0 && index < MOVE_DIRECTIONS.length ){
			return ((Integer)MOVE_DIRECTIONS[ index ].getValue()).intValue();
		}
		return 0;
	}
	
	protected long getDuration1( int index , int count ){
		if( count > 0 && index >= 0 && index < MOVE_DURATIONS.length ){
			TGDuration duration = getSongManager().getFactory().newDuration();
			duration.setValue( ((Integer)MOVE_DURATIONS[ index ].getValue()).intValue() );
			duration.setDotted( false );
			duration.setDoubleDotted( false );
			duration.getDivision().setTimes( 1 );
			duration.getDivision().setEnters( 1 );
			return ( duration.getTime() * count );
		}
		return 0;
	}
	
	protected long getDuration2( int index , int indexType , int indexDivision, int count ){
		if( count > 0 && index >= 0 && index < MOVE_DURATIONS.length ){
			if( indexType >= 0 && indexType < MOVE_DURATION_TYPES.length && indexDivision >= 0 && indexDivision < MOVE_DURATION_DIVISIONS.length ){
				TGDuration duration = getSongManager().getFactory().newDuration();
				duration.setValue( ((Integer)MOVE_DURATIONS[ index ].getValue()).intValue() );
				duration.setDotted(((boolean[])MOVE_DURATION_TYPES[ indexType ].getValue())[0]);
				duration.setDoubleDotted(((boolean[])MOVE_DURATION_TYPES[ indexType ].getValue())[1]);
				duration.getDivision().setEnters(((int[])MOVE_DURATION_DIVISIONS[ indexDivision ].getValue())[0]);
				duration.getDivision().setTimes(((int[])MOVE_DURATION_DIVISIONS[ indexDivision ].getValue())[1]);
				return ( duration.getTime() * count );
			}
		}
		return 0;
	}
	
	protected void updateControls(int count, List controls){
		Iterator it = controls.iterator();
		while( it.hasNext() ){
			Control control = (Control) it.next();
			control.setEnabled( count > 0 );
		}
	}
	
	protected int moveBeats( long theMove ){
		Caret caret = getEditor().getTablature().getCaret();		
		TGBeat beat = caret.getSelectedBeat();
		TGMeasure measure = caret.getMeasure();
		TGTrack track = caret.getTrack();
		if(beat != null && measure != null && track != null){
			//comienza el undoable
			UndoableTrackGeneric undoable = UndoableTrackGeneric.startUndo(track);
			
			getSongManager().getTrackManager().moveTrackBeats(track, measure.getStart(), beat.getStart(), theMove );
			
			//termia el undoable
			addUndoableEdit(undoable.endUndo(track));
			
			TuxGuitar.instance().getFileHistory().setUnsavedFile();
			
			updateTablature();
		}
		return 0;
	}
	
	protected static class ComboItem {		
		private String label;
		private Object value;
		
		public ComboItem(String label, Object value){
			this.label = label;
			this.value = value;
		}
		
		public String getLabel() {
			return this.label;
		}
		
		public Object getValue() {
			return this.value;
		}
	}
}
