package org.herac.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.swt.event.SWTSelectionListenerManager;
import org.herac.tuxguitar.ui.widget.UIKnob;

public class SWTKnob extends SWTControl<Button> implements UIKnob {
	
	private int maximum;
	private int minimum;
	private int increment;
	private int type;
	private int value;
	private String toolTipText;
	private boolean inverted;
	private Shell shell;
	private Composite composite;
	private Scale scale;
	
	private SWTSelectionListenerManager selectionListener;
	
	public SWTKnob(SWTContainer<? extends Composite> parent) {
		super(new Button(parent.getControl(), SWT.TOGGLE), parent);
		
		this.value = -1;
		this.inverted =  true;
		this.getControl().addDisposeListener(createItemDisposeListener());
		this.getControl().addSelectionListener(createItemSelectionListener());
		this.getControl().getShell().addListener(SWT.Move, createMoveShellListener());
		this.selectionListener = new SWTSelectionListenerManager(this);
	}
	
	public int getType(){
		return this.type;
	}
	
	public int getValue(){
		if( this.value < 0 ){
			this.getValueFromScale();
		}
		return this.value;
	}
	
	public void setValue(int value){
		if( value != this.value ){
			this.value = value;
			this.setValueToScale();
			this.updateToolTipValue();
		}
	}
	
	public int getMaximum() {
		return maximum;
	}

	public void setMaximum(int maximum) {
		this.maximum = maximum;
		this.setMaximumToScale();
	}

	public int getMinimum() {
		return minimum;
	}

	public void setMinimum(int minimum) {
		this.minimum = minimum;
		this.setMinimumToScale();
	}

	public int getIncrement() {
		return increment;
	}

	public void setIncrement(int increment) {
		this.increment = increment;
		this.setIncrementToScale();
	}

	
	public void getValueFromScale(){
		if(!this.isShellDisposed()){
			this.setValue( ( this.inverted ? 127 - this.scale.getSelection() : this.scale.getSelection() ) );
		}
	}
	
	public void setValueToScale(){
		if(!this.isShellDisposed()){
			this.scale.setSelection( ( this.inverted ? 127 - this.value : this.value ) );
		}
	}
	
	public void setIncrementToScale(){
		if(!this.isShellDisposed()){
			this.scale.setIncrement(this.increment);
		}
	}
	
	public void setMaximumToScale(){
		if(!this.isShellDisposed()){
			this.scale.setMaximum(this.maximum);
		}
	}
	
	public void setMinimumToScale(){
		if(!this.isShellDisposed()){
			this.scale.setMinimum(this.minimum);
		}
	}
	
	public void updateToolTipValue(){
		if( this.getToolTipText() != null ){
			this.getControl().setToolTipText( this.getToolTipText() + ": " + this.getValue() );
			if(!this.isShellDisposed()){
				this.scale.setToolTipText( this.getToolTipText() + ": " + this.getValue() );
			}
		}
	}
	
	public void setToolTipText(String text){
		this.toolTipText = text;
		if( this.getToolTipText() != null && this.getToolTipText().length() > 0){
			this.getControl().setText(this.getToolTipText().substring(0, 1));
		}
		this.updateToolTipValue();
	}
	
	public String getToolTipText(){
		return this.toolTipText;
	}
	
	private GridLayout getGridLayout(){
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginHeight = 0;
		return layout;
	}
	
	private GridData getScaleLayoutData(){
		GridData data = new GridData(SWT.CENTER,SWT.FILL,false,true);
		data.heightHint = 65;
		return data;
	}
	
	public void showShell() {
		if( isShellDisposed() ){
			this.shell = new Shell(this.getControl().getShell(), SWT.NO_TRIM );
			this.shell.setVisible(false);
			this.shell.setLayout(getGridLayout());
			
			this.composite = new Composite(this.shell, SWT.BORDER);
			this.composite.setLayout(getGridLayout());
			this.composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			
			this.scale = new Scale(this.composite, SWT.VERTICAL );
			this.scale.setMaximum(this.maximum);
			this.scale.setMinimum(this.minimum);
			this.scale.setIncrement(this.increment);
			this.scale.setPageIncrement(this.increment * 3);
			this.scale.setLayoutData(getScaleLayoutData());
			this.setValueToScale();
			
			this.scale.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					SWTKnob.this.getValueFromScale();
					SWTKnob.this.selectionListener.widgetSelected(event);
				}
			});
			
			this.shell.pack();
			this.moveShell();
			this.shell.setVisible(true);
		}
	}
	
	public void hideShell() {
		if(!isShellDisposed()){
			this.shell.dispose();
			this.shell = null;
		}
	}
	
	public void showHideShell() {
		if( this.getControl().getSelection() ){
			showShell();
		}else{
			hideShell();
		}
	}
	
	public void moveShell() {
		if(!isShellDisposed()){
			Rectangle bounds = this.getControl().getBounds();
			Point location = this.getControl().getParent().toDisplay(new Point(bounds.x, bounds.y));
			
			this.shell.setLocation( (location.x + (bounds.width / 2)) - (this.shell.getSize().x / 2), location.y + bounds.height);
		}
	}
	
	public boolean isShellDisposed(){
		return ( this.shell == null || this.shell.isDisposed() );
	}
	
	private DisposeListener createItemDisposeListener(){
		return new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				hideShell();
			}
		};
	}
	
	private SelectionListener createItemSelectionListener(){
		return new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				showHideShell();
			}
		};
	}
	
	private Listener createMoveShellListener(){
		return new Listener() {
			public void handleEvent(Event event) {
				moveShell();
			}
		};
	}
	
	public void addSelectionListener(UISelectionListener listener) {
		this.selectionListener.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionListener.removeListener(listener);
	}
}
