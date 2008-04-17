/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.items.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.measure.AddMeasureAction;
import org.herac.tuxguitar.gui.actions.measure.CleanMeasureAction;
import org.herac.tuxguitar.gui.actions.measure.CopyMeasureAction;
import org.herac.tuxguitar.gui.actions.measure.GoFirstMeasureAction;
import org.herac.tuxguitar.gui.actions.measure.GoLastMeasureAction;
import org.herac.tuxguitar.gui.actions.measure.GoNextMeasureAction;
import org.herac.tuxguitar.gui.actions.measure.GoPreviousMeasureAction;
import org.herac.tuxguitar.gui.actions.measure.PasteMeasureAction;
import org.herac.tuxguitar.gui.actions.measure.RemoveMeasureAction;
import org.herac.tuxguitar.gui.editors.tab.TGMeasureImpl;
import org.herac.tuxguitar.gui.items.MenuItems;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MeasureMenuItem implements MenuItems{

    private MenuItem measureMenuItem;
    private Menu menu; 
    private MenuItem first;
    private MenuItem last;
    private MenuItem next;
    private MenuItem previous;
    private MenuItem addMeasure;
    private MenuItem cleanMeasure;
    private MenuItem removeMeasure;
    private MenuItem copyMeasure;
    private MenuItem pasteMeasure;
    
    public MeasureMenuItem(Shell shell,Menu parent, int style) {
        this.measureMenuItem = new MenuItem(parent, style);
        this.menu = new Menu(shell, SWT.DROP_DOWN);
    }

    
    public void showItems(){  
        //--first--
        this.first = new MenuItem(this.menu, SWT.PUSH);
        this.first.addSelectionListener(TuxGuitar.instance().getAction(GoFirstMeasureAction.NAME));        
        //--previous--
        this.previous = new MenuItem(this.menu, SWT.PUSH); 
        this.previous.addSelectionListener(TuxGuitar.instance().getAction(GoPreviousMeasureAction.NAME));        
        //--next--
        this.next = new MenuItem(this.menu, SWT.PUSH);
        this.next.addSelectionListener(TuxGuitar.instance().getAction(GoNextMeasureAction.NAME));        
        //--last--
        this.last = new MenuItem(this.menu, SWT.PUSH);          
        this.last.addSelectionListener(TuxGuitar.instance().getAction(GoLastMeasureAction.NAME));        

        //--SEPARATOR
        new MenuItem(this.menu, SWT.SEPARATOR);          
        //--add--
        this.addMeasure = new MenuItem(this.menu, SWT.PUSH);       
        this.addMeasure.addSelectionListener(TuxGuitar.instance().getAction(AddMeasureAction.NAME));
        //--clean--
        this.cleanMeasure = new MenuItem(this.menu, SWT.PUSH);   
        this.cleanMeasure.addSelectionListener(TuxGuitar.instance().getAction(CleanMeasureAction.NAME));         
        //--remove--
        this.removeMeasure = new MenuItem(this.menu, SWT.PUSH);   
        this.removeMeasure.addSelectionListener(TuxGuitar.instance().getAction(RemoveMeasureAction.NAME)); 
                
        //--SEPARATOR
        new MenuItem(this.menu, SWT.SEPARATOR);          
        //--copy--
        this.copyMeasure = new MenuItem(this.menu, SWT.PUSH);      
        this.copyMeasure.addSelectionListener(TuxGuitar.instance().getAction(CopyMeasureAction.NAME)); 
        //--paste--
        this.pasteMeasure = new MenuItem(this.menu, SWT.PUSH);
   
        this.pasteMeasure.addSelectionListener(TuxGuitar.instance().getAction(PasteMeasureAction.NAME)); 
                   
        this.measureMenuItem.setMenu(this.menu);     
        
        this.loadIcons();
        this.loadProperties();
    }

    public void update(){
        TGMeasureImpl measure = TuxGuitar.instance().getTablatureEditor().getTablature().getCaret().getMeasure();
        boolean running = TuxGuitar.instance().getPlayer().isRunning();
        boolean isFirst = (measure.getNumber() == 1);
        boolean isLast = (measure.getNumber() == measure.getTrack().countMeasures());
        this.first.setEnabled(!isFirst);
        this.previous.setEnabled(!isFirst);
        this.next.setEnabled(!isLast);
        this.last.setEnabled(!isLast);
        this.addMeasure.setEnabled(!running);
        this.cleanMeasure.setEnabled(!running);
        this.removeMeasure.setEnabled(!running);
        this.copyMeasure.setEnabled(!running);        
        this.pasteMeasure.setEnabled(!running && !TuxGuitar.instance().getTablatureEditor().getClipBoard().isEmpty());
    }
    
    public void loadProperties(){
        this.measureMenuItem.setText(TuxGuitar.getProperty("measure"));        
        this.first.setText(TuxGuitar.getProperty("measure.first"));
        this.last.setText(TuxGuitar.getProperty("measure.last"));
        this.previous.setText(TuxGuitar.getProperty("measure.previous"));
        this.next.setText(TuxGuitar.getProperty("measure.next"));        
        this.addMeasure.setText(TuxGuitar.getProperty("measure.add"));
        this.cleanMeasure.setText(TuxGuitar.getProperty("measure.clean"));
        this.removeMeasure.setText(TuxGuitar.getProperty("measure.remove"));
        this.copyMeasure.setText(TuxGuitar.getProperty("measure.copy"));
        this.pasteMeasure.setText(TuxGuitar.getProperty("measure.paste"));        
    }         
    
    public void loadIcons(){
    	//Nothing to do
    }
}
