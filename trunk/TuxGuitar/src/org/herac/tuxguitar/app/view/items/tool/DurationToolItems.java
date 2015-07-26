/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.view.items.tool;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.items.ToolItems;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.duration.TGChangeDottedDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGChangeDoubleDottedDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetDivisionTypeDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetEighthDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetHalfDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetQuarterDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetSixteenthDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetSixtyFourthDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetThirtySecondDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetWholeDurationAction;
import org.herac.tuxguitar.song.models.TGDivisionType;
import org.herac.tuxguitar.song.models.TGDuration;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DurationToolItems  extends ToolItems{
	
	public static final String NAME = "duration.items";
	
	protected ToolBar toolBar;
	private ToolItem[] durationItems;
	private ToolItem dotted;
	private ToolItem doubleDotted;
	private DivisionTypeMenuItem divisionTypeItems;
	
	public DurationToolItems(){
		super(NAME);
	}
	
	public void showItems(ToolBar toolBar){
		this.toolBar = toolBar;
		this.durationItems = new ToolItem[7];
		
		this.durationItems[0] = new ToolItem(toolBar, SWT.RADIO);
		this.durationItems[0].addSelectionListener(this.createActionProcessor(TGSetWholeDurationAction.NAME));
		
		this.durationItems[1] = new ToolItem(toolBar, SWT.RADIO);
		this.durationItems[1].addSelectionListener(this.createActionProcessor(TGSetHalfDurationAction.NAME));
		
		this.durationItems[2] = new ToolItem(toolBar, SWT.RADIO);
		this.durationItems[2].setSelection(true);
		this.durationItems[2].addSelectionListener(this.createActionProcessor(TGSetQuarterDurationAction.NAME));
		
		this.durationItems[3] = new ToolItem(toolBar, SWT.RADIO);
		this.durationItems[3].addSelectionListener(this.createActionProcessor(TGSetEighthDurationAction.NAME));
		
		this.durationItems[4] = new ToolItem(toolBar, SWT.RADIO);
		this.durationItems[4].addSelectionListener(this.createActionProcessor(TGSetSixteenthDurationAction.NAME));
		
		this.durationItems[5] = new ToolItem(toolBar, SWT.RADIO);
		this.durationItems[5].addSelectionListener(this.createActionProcessor(TGSetThirtySecondDurationAction.NAME));
		
		this.durationItems[6] = new ToolItem(toolBar, SWT.RADIO);
		this.durationItems[6].addSelectionListener(this.createActionProcessor(TGSetSixtyFourthDurationAction.NAME));
		
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		this.dotted = new ToolItem(toolBar, SWT.CHECK);
		this.dotted.addSelectionListener(this.createActionProcessor(TGChangeDottedDurationAction.NAME));
		
		this.doubleDotted = new ToolItem(toolBar, SWT.CHECK);
		this.doubleDotted.addSelectionListener(this.createActionProcessor(TGChangeDoubleDottedDurationAction.NAME));
		
		this.divisionTypeItems = new DivisionTypeMenuItem();
		this.divisionTypeItems.addItems();
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		TGDuration duration = getEditor().getTablature().getCaret().getDuration();
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		int index = duration.getIndex();
		for(int i = 0;i < this.durationItems.length;i++){
			this.durationItems[i].setSelection( (i == index) );
			this.durationItems[i].setEnabled( !running );
		}
		this.dotted.setSelection(duration.isDotted());
		this.dotted.setEnabled( !running );
		this.doubleDotted.setSelection(duration.isDoubleDotted());
		this.doubleDotted.setEnabled( !running );
		
		this.divisionTypeItems.setEnabled( !running );
		this.divisionTypeItems.update();
	}
	
	public void loadProperties(){
		this.durationItems[0].setToolTipText(TuxGuitar.getProperty("duration.whole"));
		this.durationItems[1].setToolTipText(TuxGuitar.getProperty("duration.half"));
		this.durationItems[2].setToolTipText(TuxGuitar.getProperty("duration.quarter"));
		this.durationItems[3].setToolTipText(TuxGuitar.getProperty("duration.eighth"));
		this.durationItems[4].setToolTipText(TuxGuitar.getProperty("duration.sixteenth"));
		this.durationItems[5].setToolTipText(TuxGuitar.getProperty("duration.thirtysecond"));
		this.durationItems[6].setToolTipText(TuxGuitar.getProperty("duration.sixtyfourth"));
		this.dotted.setToolTipText(TuxGuitar.getProperty("duration.dotted"));
		this.doubleDotted.setToolTipText(TuxGuitar.getProperty("duration.doubledotted"));
		this.divisionTypeItems.setText(TuxGuitar.getProperty("duration.division-type"));
	}
	
	public void loadIcons(){
		this.durationItems[0].setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.WHOLE));
		this.durationItems[1].setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.HALF));
		this.durationItems[2].setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.QUARTER));
		this.durationItems[3].setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.EIGHTH));
		this.durationItems[4].setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.SIXTEENTH));
		this.durationItems[5].setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.THIRTY_SECOND));
		this.durationItems[6].setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.SIXTY_FOURTH));
		this.dotted.setImage(TuxGuitar.getInstance().getIconManager().getDurationDotted());
		this.doubleDotted.setImage(TuxGuitar.getInstance().getIconManager().getDurationDoubleDotted());
		this.divisionTypeItems.setImage(TuxGuitar.getInstance().getIconManager().getDivisionType());
	}
	
	protected TablatureEditor getEditor(){
		return super.getEditor();
	}
	
	private class DivisionTypeMenuItem extends SelectionAdapter {
		private TGDivisionType divisionType;
		private ToolItem divisionTypeItem;
		private Menu subMenu;
		private MenuItem[] subMenuItems;
		
		public DivisionTypeMenuItem() {
			this.divisionType = createDivisionType(TGDivisionType.TRIPLET);
			this.divisionTypeItem = new ToolItem(DurationToolItems.this.toolBar, SWT.DROP_DOWN);
			this.divisionTypeItem.addSelectionListener(this);
			this.subMenu = new Menu(this.divisionTypeItem.getParent().getShell());
		}
		
		public void setText(String text){
			this.divisionTypeItem.setToolTipText(text);
		}
		
		public void setImage(Image image){
			this.divisionTypeItem.setImage(image);
		}
		
		public void setEnabled(boolean enabled){
			this.divisionTypeItem.setEnabled(enabled);
		}
		
		public void addItems() {
			this.subMenuItems = new MenuItem[TGDivisionType.ALTERED_DIVISION_TYPES.length];
			
			for( int i = 0 ; i < TGDivisionType.ALTERED_DIVISION_TYPES.length ; i ++ ){
				this.subMenuItems[i] = new MenuItem(this.subMenu, SWT.CHECK);
				this.subMenuItems[i].setText(new Integer(TGDivisionType.ALTERED_DIVISION_TYPES[i].getEnters()).toString());
				this.subMenuItems[i].setData(createDivisionTypeActionData(TGDivisionType.ALTERED_DIVISION_TYPES[i]));
				this.subMenuItems[i].addSelectionListener(DurationToolItems.this.createActionProcessor(TGSetDivisionTypeDurationAction.NAME));
			}
		}
		
		public void widgetSelected(SelectionEvent event) {
			if (event.detail == SWT.ARROW) {
				ToolItem item = (ToolItem) event.widget;
				Rectangle rect = item.getBounds();
				Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
				this.subMenu.setLocation(pt.x, pt.y + rect.height);
				this.subMenu.setVisible(true);
			}else{
				TGDuration duration = getEditor().getTablature().getCaret().getDuration();
				if(duration.getDivision().isEqual(TGDivisionType.NORMAL)){
					this.divisionType.copyFrom(TGDivisionType.TRIPLET);
				}else{
					this.divisionType.copyFrom(TGDivisionType.NORMAL);
				}
				
				this.createDivisionTypeAction(this.divisionType).process();
			}
		}
		
		public void update(){
			TGDuration duration = getEditor().getTablature().getCaret().getDuration();
			
			for(int i = 0;i < this.subMenuItems.length;i++){
				Map<?, ?> actionData = (Map<?, ?>)this.subMenuItems[i].getData();
				TGDivisionType divisionType = (TGDivisionType)actionData.get(TGSetDivisionTypeDurationAction.PROPERTY_DIVISION_TYPE);
				
				this.subMenuItems[i].setSelection((divisionType.isEqual(duration.getDivision())));
			}
		}
		
		private TGDivisionType createDivisionType(TGDivisionType tgDivisionTypeSrc){
			TGDivisionType tgDivisionTypeDst = TuxGuitar.getInstance().getSongManager().getFactory().newDivisionType();
			tgDivisionTypeDst.copyFrom(tgDivisionTypeSrc);
			return tgDivisionTypeDst;
		}
		
		private Map<String, TGDivisionType> createDivisionTypeActionData(TGDivisionType tgDivisionType){
			Map<String, TGDivisionType> actionData = new HashMap<String, TGDivisionType>();
			actionData.put(TGSetDivisionTypeDurationAction.PROPERTY_DIVISION_TYPE, createDivisionType(tgDivisionType));
			return actionData;
		}
		
		private TGActionProcessor createDivisionTypeAction(TGDivisionType tgDivisionType){
			TGActionProcessor tgActionProcessor = new TGActionProcessor(TuxGuitar.getInstance().getContext(), TGSetDivisionTypeDurationAction.NAME);
			tgActionProcessor.setAttribute(TGSetDivisionTypeDurationAction.PROPERTY_DIVISION_TYPE, createDivisionType(tgDivisionType));
			return tgActionProcessor;
		}
	}
}
