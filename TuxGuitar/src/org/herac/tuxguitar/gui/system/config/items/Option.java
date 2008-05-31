package org.herac.tuxguitar.gui.system.config.items;

import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.TablatureEditor;
import org.herac.tuxguitar.gui.system.config.TGConfigEditor;
import org.herac.tuxguitar.gui.system.config.TGConfigManager;

public abstract class Option extends SelectionAdapter{
	
	protected static final int DEFAULT_INDENT = 20;
	
	private TGConfigEditor configEditor ;
	private ToolBar toolBar;
	private Group group;
	private Composite composite;
	private ToolItem toolItem;
	
	public Option(TGConfigEditor configEditor,ToolBar toolBar,final Composite parent,String text, int horizontalAlignment,int verticalAlignment){
		this.configEditor = configEditor;
		this.toolBar = toolBar;
		this.toolItem = new ToolItem(toolBar, SWT.RADIO);
		this.group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		this.group.setLayout(new GridLayout());
		this.group.setLayoutData(getGroupData());
		this.group.setText(text);
		this.composite = new Composite(this.group, SWT.SHADOW_ETCHED_IN);
		this.composite.setLayout(new GridLayout());
		this.composite.setLayoutData(new GridData(horizontalAlignment,verticalAlignment,true ,true));
	}
	
	public Option(TGConfigEditor configEditor,ToolBar toolBar, Composite parent,String text){
		this(configEditor, toolBar, parent, text, SWT.FILL,SWT.TOP);
	}
	
	public abstract void createOption();
	
	public abstract void updateConfig();
	
	public abstract void updateDefaults();
	
	public abstract void applyConfig(boolean force);
	
	public void setVisible(boolean visible){
		this.toolItem.setSelection(visible);
		this.group.setVisible(visible);
		this.group.setFocus();
		this.group.redraw();
	}
	
	public void dispose(){
		//Override me
	}
	
	protected Label showLabel(Composite parent,int labelStyle,int fontStyle,int fontScale,String text){
		return showLabel(parent,SWT.FILL,SWT.CENTER, labelStyle, fontStyle, fontScale, text);
	}
	
	protected Label showLabel(Composite parent,int hAlign,int vAlign,int labelStyle,int fontStyle,int fontScale,String text){
		return showLabel(parent, hAlign, vAlign, true, true, labelStyle, fontStyle, fontScale, text);
	}
	
	protected Label showLabel(Composite parent,int hAlign,int vAlign,boolean grabExcessHSpace,boolean grabExcessVSpace,int labelStyle,int fontStyle,int fontScale,String text){
		Label label = new Label(parent,labelStyle);
		label.setText(text);
		label.setLayoutData(new GridData(hAlign,vAlign,grabExcessHSpace,grabExcessVSpace));
		
		FontData[] fontDatas = label.getFont().getFontData();
		if(fontDatas.length > 0){
			final Font font = new Font(label.getDisplay(),fontDatas[0].getName(),(fontDatas[0].getHeight() + fontScale),fontStyle);
			label.setFont(font);
			label.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent arg0) {
					font.dispose();
				}
			});
		}
		return label;
	}
	
	protected Label showImageLabel(Composite parent,int labelStyle,Image image){
		Label label = new Label(parent,labelStyle);
		label.setImage(image);
		return label;
	}
	
	protected FormData getGroupData(){
		FormData data = new FormData();
		data.top = new FormAttachment(0,0);
		data.bottom = new FormAttachment(100,0);
		data.left = new FormAttachment(0,0);
		data.right = new FormAttachment(100,0);
		return data;
	}
	
	public void widgetSelected(SelectionEvent e) {
		this.configEditor.select(this);
	}
	
	public Composite getComposite(){
		return this.composite;
	}
	
	public ToolItem getToolItem(){
		return this.toolItem;
	}
	
	public Point computeSize(){
		return this.computeSize(SWT.DEFAULT,SWT.DEFAULT);
	}
	
	protected Point computeSize(int wHint,int hHint){
		return this.group.computeSize(wHint, hHint);
	}
	
	public TGConfigManager getConfig(){
		return this.configEditor.getConfig();
	}
	
	public Properties getDefaults(){
		return this.configEditor.getDefaults();
	}
	
	public TablatureEditor getEditor(){
		return this.configEditor.getEditor();
	}
	
	public Display getDisplay(){
		return this.toolBar.getDisplay();
	}
	
	public Shell getShell(){
		return this.toolBar.getShell();
	}
	
	protected boolean isDisposed(){
		return (this.toolBar.isDisposed() || this.toolBar.getShell().isDisposed());
	}
	
	public  GridData makeGridData(int with,int height,int minWith,int minHeight){
		return this.configEditor.makeGridData(with, height, minWith, minHeight);
	}
	
	protected GridData getTabbedData(){
		return getTabbedData(DEFAULT_INDENT,SWT.FILL,SWT.CENTER);
	}
	
	protected GridData getTabbedData(int horizontalAlignment,int verticalAlignment, boolean grabExcessHorizontalSpace, boolean grabExcessVerticalSpace){
		return getTabbedData(DEFAULT_INDENT,horizontalAlignment,verticalAlignment,grabExcessHorizontalSpace,grabExcessVerticalSpace);
	}
	
	protected GridData getTabbedData(int horizontalAlignment,int verticalAlignment){
		return getTabbedData(DEFAULT_INDENT,horizontalAlignment,verticalAlignment);
	}
	
	protected GridData getTabbedData(int indent,int horizontalAlignment,int verticalAlignment){
		return getTabbedData(indent, horizontalAlignment, verticalAlignment, true, true);
	}
	
	protected GridData getTabbedData(int indent,int horizontalAlignment,int verticalAlignment, boolean grabExcessHorizontalSpace, boolean grabExcessVerticalSpace){
		GridData data = new GridData();
		data.horizontalAlignment = horizontalAlignment;
		data.grabExcessHorizontalSpace = grabExcessHorizontalSpace;
		data.verticalAlignment = verticalAlignment;
		data.grabExcessVerticalSpace = grabExcessVerticalSpace;
		data.horizontalIndent = indent;
		return data;
	}
	
	public GridData makeGridData(int widthHint,
								 int heightHint,
								 int minimumWidth,
								 int minimumHeight,
								 int horizontalAlignment,
								 int verticalAlignment,
								 boolean grabExcessHorizontalSpace,
								 boolean grabExcessVerticalSpace,
								 int horizontalSpan,
								 int verticalSpan){
		GridData data = new GridData();
		data.widthHint = widthHint;
		data.heightHint = heightHint;
		data.minimumWidth = minimumWidth;
		data.minimumHeight = minimumHeight;
		data.horizontalAlignment = horizontalAlignment;
		data.verticalAlignment = verticalAlignment;
		data.grabExcessHorizontalSpace = grabExcessHorizontalSpace;
		data.grabExcessVerticalSpace = grabExcessVerticalSpace;
		data.horizontalSpan = horizontalSpan;
		data.verticalSpan = verticalSpan;
		
		return data;
	}
	
	public void pack(){
		this.configEditor.pack();
	}
	
	public void loadCursor(int style){
		TuxGuitar.instance().loadCursor(this.configEditor.getDialog(),style);
	}
	
	protected void addSyncThread(Runnable runnable){
		this.configEditor.addSyncThread(runnable);
	}
	
}