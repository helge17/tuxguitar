package org.herac.tuxguitar.app.view.dialog.channel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseListener;
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

public class TGScalePopup{
	
	private int type;
	private int value;
	private boolean inverted;
	private Shell shell;
	private Composite composite;
	private Button item;
	private Scale scale;
	private String text;
	
	private SelectionListener selectionListener;
	private MouseListener mouseListener;
	
	public TGScalePopup(Composite parent){
		this.init(parent);
	}
	
	public void init(Composite parent){
		this.value = -1;
		this.inverted =  true;
		this.item = new Button(parent, SWT.TOGGLE);
		this.item.addDisposeListener(createItemDisposeListener());
		this.item.addSelectionListener(createItemSelectionListener());
		this.item.getShell().addListener(SWT.Move, createMoveShellListener());
	}
	
	public void reset(){
		this.item.setSelection(false);
		this.hideShell();
	}
	
	public void addDefaultListeners(){
		this.scale.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				TGScalePopup.this.getValueFromScale();
				if( TGScalePopup.this.selectionListener != null ){
					TGScalePopup.this.selectionListener.widgetSelected(event);
				}
			}
		});
		if( this.mouseListener != null ){
			this.scale.addMouseListener(this.mouseListener);
		}
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
	
	public void getValueFromScale(){
		if(!isShellDisposed()){
			this.setValue( ( this.inverted ? 127 - this.scale.getSelection() : this.scale.getSelection() ) );
		}
	}
	
	public void setValueToScale(){
		if(!isShellDisposed()){
			this.scale.setSelection( ( this.inverted ? 127 - this.value : this.value ) );
		}
	}
	
	public void updateToolTipValue(){
		if( this.getText() != null ){
			this.item.setToolTipText( this.getText() + ": " + this.getValue() );
			if(!isShellDisposed()){
				this.scale.setToolTipText( this.text + ": " + this.getValue() );
			}
		}
	}
	
	public void setText(String text){
		this.text = text;
		if(this.getText() != null && this.getText().length() > 0){
			this.item.setText( this.getText().substring(0,1) );
		}
		this.updateToolTipValue();
	}
	
	public String getText(){
		return this.text;
	}
	
	public void showShell() {
		if( isShellDisposed() ){
			this.shell = new Shell( this.item.getShell(), SWT.NO_TRIM );
			this.shell.setVisible(false);
			this.shell.setLayout(getGridLayout());
			
			this.composite = new Composite(this.shell, SWT.BORDER);
			this.composite.setLayout(getGridLayout());
			this.composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			
			this.scale = new Scale(this.composite, SWT.VERTICAL );
			this.scale.setMaximum(127);
			this.scale.setMinimum(0);
			this.scale.setIncrement(1);
			this.scale.setPageIncrement(64);
			this.scale.setLayoutData(getScaleLayoutData());
			this.setValueToScale();
			this.addDefaultListeners();
			
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
		if( this.item.getSelection() ){
			showShell();
		}else{
			hideShell();
		}
	}
	
	public void moveShell() {
		if(!isShellDisposed()){
			Rectangle bounds = this.item.getBounds();
			Point location = this.item.getParent().toDisplay(new Point(bounds.x, bounds.y));
			
			this.shell.setLocation( (location.x + (bounds.width / 2)) - (this.shell.getSize().x / 2), location.y + bounds.height);
		}
	}
	
	public boolean isShellDisposed(){
		return ( this.shell == null || this.shell.isDisposed() );
	}
	
	public void setSelectionListener(SelectionListener selectionListener){
		this.selectionListener = selectionListener;
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
}
