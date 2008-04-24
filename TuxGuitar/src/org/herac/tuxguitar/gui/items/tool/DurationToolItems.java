/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.items.tool;

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
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.duration.ChangeDottedDurationAction;
import org.herac.tuxguitar.gui.actions.duration.ChangeDoubleDottedDurationAction;
import org.herac.tuxguitar.gui.actions.duration.ChangeTupletoDurationAction;
import org.herac.tuxguitar.gui.actions.duration.SetEighthDurationAction;
import org.herac.tuxguitar.gui.actions.duration.SetHalfDurationAction;
import org.herac.tuxguitar.gui.actions.duration.SetQuarterDurationAction;
import org.herac.tuxguitar.gui.actions.duration.SetSixteenthDurationAction;
import org.herac.tuxguitar.gui.actions.duration.SetSixtyFourthDurationAction;
import org.herac.tuxguitar.gui.actions.duration.SetThirtySecondDurationAction;
import org.herac.tuxguitar.gui.actions.duration.SetWholeDurationAction;
import org.herac.tuxguitar.gui.editors.TablatureEditor;
import org.herac.tuxguitar.gui.items.ToolItems;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGTupleto;

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
	private TupletoMenuItem tupletoItems;
	
	public DurationToolItems(){
		super(NAME);
	}
	
	public void showItems(ToolBar toolBar){
		this.toolBar = toolBar;
		this.durationItems = new ToolItem[7];
		
		this.durationItems[0] = new ToolItem(toolBar, SWT.RADIO);
		this.durationItems[0].setToolTipText("Redonda");
		this.durationItems[0].addSelectionListener(TuxGuitar.instance().getAction(SetWholeDurationAction.NAME));
		
		this.durationItems[1] = new ToolItem(toolBar, SWT.RADIO);
		this.durationItems[1].setToolTipText("Blanca");
		this.durationItems[1].addSelectionListener(TuxGuitar.instance().getAction(SetHalfDurationAction.NAME));
		
		this.durationItems[2] = new ToolItem(toolBar, SWT.RADIO);
		this.durationItems[2].setToolTipText("Negra");
		this.durationItems[2].setSelection(true);
		this.durationItems[2].addSelectionListener(TuxGuitar.instance().getAction(SetQuarterDurationAction.NAME));
		
		this.durationItems[3] = new ToolItem(toolBar, SWT.RADIO);
		this.durationItems[3].setToolTipText("Corchea");
		this.durationItems[3].addSelectionListener(TuxGuitar.instance().getAction(SetEighthDurationAction.NAME));
		
		this.durationItems[4] = new ToolItem(toolBar, SWT.RADIO);
		this.durationItems[4].setToolTipText("Semi-Corchea");
		this.durationItems[4].addSelectionListener(TuxGuitar.instance().getAction(SetSixteenthDurationAction.NAME));
		
		this.durationItems[5] = new ToolItem(toolBar, SWT.RADIO);
		this.durationItems[5].setToolTipText("Fusa");
		this.durationItems[5].addSelectionListener(TuxGuitar.instance().getAction(SetThirtySecondDurationAction.NAME));
		
		this.durationItems[6] = new ToolItem(toolBar, SWT.RADIO);
		this.durationItems[6].setToolTipText("Semi-Fusa");
		this.durationItems[6].addSelectionListener(TuxGuitar.instance().getAction(SetSixtyFourthDurationAction.NAME));
		
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		this.dotted = new ToolItem(toolBar, SWT.CHECK);
		this.dotted.setToolTipText("Puntillo");
		this.dotted.addSelectionListener(TuxGuitar.instance().getAction(ChangeDottedDurationAction.NAME));
		
		this.doubleDotted = new ToolItem(toolBar, SWT.CHECK);
		this.doubleDotted.setToolTipText("Doble Puntillo");
		this.doubleDotted.addSelectionListener(TuxGuitar.instance().getAction(ChangeDoubleDottedDurationAction.NAME));
		
		this.tupletoItems = new TupletoMenuItem();
		this.tupletoItems.addItems();
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		TGDuration duration = getEditor().getTablature().getCaret().getDuration();
		boolean running = TuxGuitar.instance().getPlayer().isRunning();
		int index = duration.getIndex();
		for(int i = 0;i < this.durationItems.length;i++){
			this.durationItems[i].setSelection( (i == index) );
			this.durationItems[i].setEnabled( !running );
		}
		this.dotted.setSelection(duration.isDotted());
		this.dotted.setEnabled( !running );
		this.doubleDotted.setSelection(duration.isDoubleDotted());
		this.doubleDotted.setEnabled( !running );
		
		this.tupletoItems.setEnabled( !running );
		this.tupletoItems.update();
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
		this.tupletoItems.setText(TuxGuitar.getProperty("duration.tupleto"));
	}
	
	public void loadIcons(){
		this.durationItems[0].setImage(TuxGuitar.instance().getIconManager().getDuration(TGDuration.WHOLE));
		this.durationItems[1].setImage(TuxGuitar.instance().getIconManager().getDuration(TGDuration.HALF));
		this.durationItems[2].setImage(TuxGuitar.instance().getIconManager().getDuration(TGDuration.QUARTER));
		this.durationItems[3].setImage(TuxGuitar.instance().getIconManager().getDuration(TGDuration.EIGHTH));
		this.durationItems[4].setImage(TuxGuitar.instance().getIconManager().getDuration(TGDuration.SIXTEENTH));
		this.durationItems[5].setImage(TuxGuitar.instance().getIconManager().getDuration(TGDuration.THIRTY_SECOND));
		this.durationItems[6].setImage(TuxGuitar.instance().getIconManager().getDuration(TGDuration.SIXTY_FOURTH));
		this.dotted.setImage(TuxGuitar.instance().getIconManager().getDurationDotted());
		this.doubleDotted.setImage(TuxGuitar.instance().getIconManager().getDurationDoubleDotted());
		this.tupletoItems.setImage(TuxGuitar.instance().getIconManager().getDurationTupleto());
	}
	
	protected TablatureEditor getEditor(){
		return super.getEditor();
	}
	
	private class TupletoMenuItem extends SelectionAdapter {
		private ToolItem tupleto;
		private Menu subMenu;
		private MenuItem[] subMenuItems;
		
		public TupletoMenuItem() {
			this.tupleto = new ToolItem(DurationToolItems.this.toolBar, SWT.DROP_DOWN);
			this.tupleto.addSelectionListener(this);
			this.tupleto.setData(newTupleto(3,2));
			this.subMenu = new Menu(this.tupleto.getParent().getShell());
		}
		
		public void setText(String text){
			this.tupleto.setToolTipText(text);
		}
		
		public void setImage(Image image){
			this.tupleto.setImage(image);
		}
		
		public void setEnabled(boolean enabled){
			this.tupleto.setEnabled(enabled);
		}
		
		public void addItems() {
			this.subMenuItems = new MenuItem[8];
			
			this.subMenuItems[0] = new MenuItem(this.subMenu, SWT.CHECK);
			this.subMenuItems[0].setText("3");
			this.subMenuItems[0].setData(newTupleto(3,2));
			this.subMenuItems[0].addSelectionListener(TuxGuitar.instance().getAction(ChangeTupletoDurationAction.NAME));
			
			this.subMenuItems[1] = new MenuItem(this.subMenu, SWT.CHECK);
			this.subMenuItems[1].setText("5");
			this.subMenuItems[1].setData(newTupleto(5,4));
			this.subMenuItems[1].addSelectionListener(TuxGuitar.instance().getAction(ChangeTupletoDurationAction.NAME));
			
			this.subMenuItems[2] = new MenuItem(this.subMenu, SWT.CHECK);
			this.subMenuItems[2].setText("6");
			this.subMenuItems[2].setData(newTupleto(6,4));
			this.subMenuItems[2].addSelectionListener(TuxGuitar.instance().getAction(ChangeTupletoDurationAction.NAME));
			
			this.subMenuItems[3] = new MenuItem(this.subMenu, SWT.CHECK);
			this.subMenuItems[3].setText("7");
			this.subMenuItems[3].setData(newTupleto(7,4));
			this.subMenuItems[3].addSelectionListener(TuxGuitar.instance().getAction(ChangeTupletoDurationAction.NAME));
			
			this.subMenuItems[4] = new MenuItem(this.subMenu, SWT.CHECK);
			this.subMenuItems[4].setText("9");
			this.subMenuItems[4].setData(newTupleto(9,8));
			this.subMenuItems[4].addSelectionListener(TuxGuitar.instance().getAction(ChangeTupletoDurationAction.NAME));
			
			this.subMenuItems[5] = new MenuItem(this.subMenu, SWT.CHECK);
			this.subMenuItems[5].setText("10");
			this.subMenuItems[5].setData(newTupleto(10,8));
			this.subMenuItems[5].addSelectionListener(TuxGuitar.instance().getAction(ChangeTupletoDurationAction.NAME));
			
			this.subMenuItems[6] = new MenuItem(this.subMenu, SWT.CHECK);
			this.subMenuItems[6].setText("11");
			this.subMenuItems[6].setData(newTupleto(11,8));
			this.subMenuItems[6].addSelectionListener(TuxGuitar.instance().getAction(ChangeTupletoDurationAction.NAME));
			
			this.subMenuItems[7] = new MenuItem(this.subMenu, SWT.CHECK);
			this.subMenuItems[7].setText("12");
			this.subMenuItems[7].setData(newTupleto(12,8));
			this.subMenuItems[7].addSelectionListener(TuxGuitar.instance().getAction(ChangeTupletoDurationAction.NAME));
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
				if(duration.getTupleto().isEqual(TGTupleto.NORMAL)){
					((TGTupleto)this.tupleto.getData()).setEnters(3);
					((TGTupleto)this.tupleto.getData()).setTimes(2);
				}else{
					((TGTupleto)this.tupleto.getData()).setEnters(1);
					((TGTupleto)this.tupleto.getData()).setTimes(1);
				}
				TuxGuitar.instance().getAction(ChangeTupletoDurationAction.NAME).process(event);
			}
		}
		
		public void update(){
			TGDuration duration = getEditor().getTablature().getCaret().getDuration();
			
			for(int i = 0;i < this.subMenuItems.length;i++){
				TGTupleto tupleto = (TGTupleto)this.subMenuItems[i].getData();
				this.subMenuItems[i].setSelection((tupleto.isEqual(duration.getTupleto())));
			}
		}
		
		private TGTupleto newTupleto(int enters,int times){
			TGTupleto tupleto = TuxGuitar.instance().getSongManager().getFactory().newTupleto();
			tupleto.setEnters(enters);
			tupleto.setTimes(times);
			return tupleto;
		}
	}
}
