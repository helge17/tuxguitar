package org.herac.tuxguitar.app.view.dialog.beat;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.note.TGMoveBeatsAction;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDivisionType;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UISpinner;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class TGBeatMoveDialog {
	
	public TGBeatMoveDialog() {
		super();
	}
	
	public void show(final TGViewContext context){
		final TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		final TGMeasure measure = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		final TGBeat beat = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
		
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT2);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("beat.move-custom.dialog.title"));
		
		//-------direction-------------------------------------
		UITableLayout directionLayout = new UITableLayout();
		UILegendPanel direction = uiFactory.createLegendPanel(dialog);
		direction.setLayout(directionLayout);
		direction.setText(TuxGuitar.getProperty("beat.move-custom.dialog.direction-tip"));
		dialogLayout.set(direction, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UILabel directionLabel = uiFactory.createLabel(direction);
		directionLabel.setText(TuxGuitar.getProperty("beat.move-custom.dialog.direction") + ":");
		directionLayout.set(directionLabel, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, true, true);
		
		final UISelectItem<Integer>[] moveDirections = this.createMoveDirections();
		final UIDropDownSelect<Integer> directionCombo = uiFactory.createDropDownSelect(direction);
		for( int i = 0 ; i < moveDirections.length ; i ++ ){
			directionCombo.addItem(moveDirections[i]);
		}
		directionCombo.setSelectedItem(moveDirections[0]);
		directionLayout.set(directionCombo, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		//-------move 1------------------------------------------
		final List<UIControl> move1Controls = new ArrayList<UIControl>();
		
		UITableLayout move1Layout = new UITableLayout();
		UILegendPanel move1 = uiFactory.createLegendPanel(dialog);
		move1.setLayout(move1Layout);
		move1.setText(TuxGuitar.getProperty("beat.move-custom.dialog.move-1.tip"));
		dialogLayout.set(move1, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UILabel count1Label = uiFactory.createLabel(move1);
		count1Label.setText(TuxGuitar.getProperty("beat.move-custom.dialog.count") + ":");
		move1Layout.set(count1Label, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, true, true);
		
		final UISpinner count1Spinner = uiFactory.createSpinner(move1);
		count1Spinner.setMinimum(0);
		count1Spinner.setMaximum(100);
		count1Spinner.setIncrement(1);
		count1Spinner.setValue(0);
		count1Spinner.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				updateControls(count1Spinner.getValue(), move1Controls);
			}
		});
		move1Layout.set(count1Spinner, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UILabel duration1Label = uiFactory.createLabel(move1);
		duration1Label.setText(TuxGuitar.getProperty("beat.move-custom.dialog.duration") + ":");
		move1Layout.set(duration1Label, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, true, true);
		move1Controls.add( duration1Label );
		
		final UISelectItem<Integer>[] moveDurations = this.createMoveDurations();
		final UIDropDownSelect<Integer> duration1Combo = uiFactory.createDropDownSelect(move1);
		for( int i = 0 ; i < moveDurations.length ; i ++ ){
			duration1Combo.addItem( moveDurations[i] );
		}
		duration1Combo.setSelectedValue(TGDuration.WHOLE);
		move1Layout.set(duration1Combo, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		move1Controls.add( duration1Combo );
		
		updateControls(0, move1Controls);
		
		//-------move 2------------------------------------------
		final List<UIControl> move2Controls = new ArrayList<UIControl>();
		
		UITableLayout move2Layout = new UITableLayout();
		UILegendPanel move2 = uiFactory.createLegendPanel(dialog);
		move2.setLayout(move2Layout);
		move2.setText(TuxGuitar.getProperty("beat.move-custom.dialog.move-2.tip"));
		dialogLayout.set(move2, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UILabel count2Label = uiFactory.createLabel(move2);
		count2Label.setText(TuxGuitar.getProperty("beat.move-custom.dialog.count") + ":");
		move2Layout.set(count2Label, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, true, true);
		
		final UISpinner count2Spinner = uiFactory.createSpinner(move2);
		count2Spinner.setValue(0);
		count2Spinner.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				updateControls( count2Spinner.getValue(), move2Controls );
			}
		});
		move2Layout.set(count2Spinner, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UILabel duration2Label = uiFactory.createLabel(move2);
		duration2Label.setText(TuxGuitar.getProperty("beat.move-custom.dialog.duration") + ":");
		move2Layout.set(duration2Label, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, true, true);
		move2Controls.add( duration2Label );
		
		final UIDropDownSelect<Integer> duration2Combo = uiFactory.createDropDownSelect(move2);
		for( int i = 0 ; i < moveDurations.length ; i ++ ){
			duration2Combo.addItem( moveDurations[i] );
		}
		duration2Combo.setSelectedValue(TGDuration.QUARTER);
		move2Layout.set(duration2Combo, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		move2Controls.add( duration2Combo );
		
		UILabel type2Label = uiFactory.createLabel(move2);
		type2Label.setText(TuxGuitar.getProperty("beat.move-custom.dialog.duration.type") + ":");
		move2Layout.set(type2Label, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, true, true);
		move2Controls.add( type2Label );
		
		final UISelectItem<boolean[]>[] moveDurationTypes = this.createMoveDurationTypes();
		final UIDropDownSelect<boolean[]> type2Combo = uiFactory.createDropDownSelect(move2);
		for( int i = 0 ; i < moveDurationTypes.length ; i ++ ){
			type2Combo.addItem( moveDurationTypes[i] );
		}
		type2Combo.setSelectedItem(moveDurationTypes[0]);
		move2Layout.set(type2Combo, 3, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		move2Controls.add( type2Combo );
		
		UILabel division2Label = uiFactory.createLabel(move2);
		division2Label.setText(TuxGuitar.getProperty("beat.move-custom.dialog.duration.division-type") + ":");
		move2Layout.set(division2Label, 4, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, true, true);
		move2Controls.add( division2Label );
		
		final UISelectItem<int[]>[] moveDivisionTypes = this.createDivisionTypes();
		final UIDropDownSelect<int[]> division2Combo = uiFactory.createDropDownSelect(move2);
		for( int i = 0 ; i < moveDivisionTypes.length ; i ++ ){
			division2Combo.addItem( moveDivisionTypes[i] );
		}
		division2Combo.setSelectedItem(moveDivisionTypes[0]);
		move2Layout.set(division2Combo, 4, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		move2Controls.add( division2Combo );
		
		updateControls( 0, move2Controls );
		
		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 4, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		final UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				final int directionValue = getDirection(directionCombo);
				final long duration1 = getDuration1(duration1Combo, count1Spinner.getValue());
				final long duration2 = getDuration2(duration2Combo, type2Combo, division2Combo, count2Spinner.getValue());
				final long duration = ( ( duration1 + duration2 ) * directionValue );
				
				dialog.dispose();
				moveBeats(context.getContext(), track, measure, beat, duration);
			}
		});
		buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		
		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);
		
		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	@SuppressWarnings("unchecked")
	public UISelectItem<Integer>[] createMoveDirections() {
		return new UISelectItem[] {		
			new UISelectItem<Integer>(TuxGuitar.getProperty("beat.move-custom.dialog.direction.right"), new Integer(1)),
			new UISelectItem<Integer>(TuxGuitar.getProperty("beat.move-custom.dialog.direction.left") , new Integer(-1)),
		};
	}
	
	@SuppressWarnings("unchecked")
	public UISelectItem<Integer>[] createMoveDurations() {
		return new UISelectItem[] {		
			new UISelectItem<Integer>(TuxGuitar.getProperty("duration.whole") , new Integer(TGDuration.WHOLE)),
			new UISelectItem<Integer>(TuxGuitar.getProperty("duration.half") , new Integer(TGDuration.HALF)),
			new UISelectItem<Integer>(TuxGuitar.getProperty("duration.quarter") , new Integer(TGDuration.QUARTER)),
			new UISelectItem<Integer>(TuxGuitar.getProperty("duration.eighth") , new Integer(TGDuration.EIGHTH)),
			new UISelectItem<Integer>(TuxGuitar.getProperty("duration.sixteenth") , new Integer(TGDuration.SIXTEENTH)),
			new UISelectItem<Integer>(TuxGuitar.getProperty("duration.thirtysecond") , new Integer(TGDuration.THIRTY_SECOND)),
			new UISelectItem<Integer>(TuxGuitar.getProperty("duration.sixtyfourth") , new Integer(TGDuration.SIXTY_FOURTH)),
		};
	}
	
	@SuppressWarnings("unchecked")
	public UISelectItem<boolean[]>[] createMoveDurationTypes() {
		return new UISelectItem[] {		
			new UISelectItem<boolean[]>(TuxGuitar.getProperty("beat.move-custom.dialog.duration.type.normal") , new boolean[]{ false, false}),
			new UISelectItem<boolean[]>(TuxGuitar.getProperty("duration.dotted") , new boolean[]{ true, false}),
			new UISelectItem<boolean[]>(TuxGuitar.getProperty("duration.doubledotted") , new boolean[]{ false, true}),
		};
	}
	
	@SuppressWarnings("unchecked")
	public UISelectItem<int[]>[] createDivisionTypes(){
		TGDivisionType[] types = TGDivisionType.ALTERED_DIVISION_TYPES;
		
		UISelectItem<int[]>[] comboItems = new UISelectItem[ types.length + 1 ];
		comboItems[0] = new UISelectItem<int[]>( TuxGuitar.getProperty("beat.move-custom.dialog.duration.division-type.normal") , new int[] { 1  , 1} );
		for( int i = 0 ; i < types.length ; i ++ ){ 
			comboItems[i + 1] = new UISelectItem<int[]>(new Integer(types[i].getEnters()).toString(),new int[]{types[i].getEnters(),types[i].getTimes()});
		}
		
		return comboItems;
	}
	
	public int getDirection(UIDropDownSelect<Integer> directionCombo){
		Integer value = directionCombo.getSelectedValue();
		
		return (value != null ? value : 0);
	}
	
	public long getDuration1(UIDropDownSelect<Integer> durationCombo, int count){
		Integer value = durationCombo.getSelectedValue(); 
		if( count > 0 && value != null ){
			TGDuration duration = new TGFactory().newDuration();
			duration.setValue(value);
			duration.setDotted(false);
			duration.setDoubleDotted(false);
			duration.getDivision().setTimes(1);
			duration.getDivision().setEnters(1);
			return (duration.getTime() * count);
		}
		return 0;
	}
	
	public long getDuration2(UIDropDownSelect<Integer> durationCombo, UIDropDownSelect<boolean[]> typeCombo , UIDropDownSelect<int[]> divisionCombo, int count){
		Integer value = durationCombo.getSelectedValue();
		boolean[] type = typeCombo.getSelectedValue();
		int[] division = divisionCombo.getSelectedValue();
		
		if( count > 0 && value != null && type != null && division != null ){
			TGDuration duration = new TGFactory().newDuration();
			duration.setValue(value);
			duration.setDotted(type[0]);
			duration.setDoubleDotted(type[1]);
			duration.getDivision().setEnters(division[0]);
			duration.getDivision().setTimes(division[1]);
			return ( duration.getTime() * count );
		}
		return 0;
	}
	
	public void updateControls(int count, List<UIControl> controls){
		for(UIControl control : controls){
			control.setEnabled( count > 0 );
		}
	}
	
	public void moveBeats(TGContext context, TGTrack track, TGMeasure measure, TGBeat beat, Long theMove) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGMoveBeatsAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		tgActionProcessor.setAttribute(TGMoveBeatsAction.ATTRIBUTE_MOVE, theMove);
		tgActionProcessor.process();
	}
}
