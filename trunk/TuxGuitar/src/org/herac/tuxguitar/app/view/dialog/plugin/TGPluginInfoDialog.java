package org.herac.tuxguitar.app.view.dialog.plugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.util.plugin.TGPluginInfo;

public class TGPluginInfoDialog {
	
	public static String ATTRIBUTE_MODULE_ID = "moduleId";
	
	public void show(final TGViewContext context) {
		final String moduleId = context.getAttribute(ATTRIBUTE_MODULE_ID);
		
		final Shell parent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		TGPluginInfo pluginInfo = new TGPluginInfo(context.getContext(), moduleId);
		
		dialog.setLayout(new GridLayout());
		dialog.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		dialog.setText(TuxGuitar.getProperty("plugins"));
		
		Composite info = new Composite(dialog,SWT.NONE);
		info.setLayout(new GridLayout(2,false));
		
		showInfoString(info,TuxGuitar.getProperty("name") + ":", pluginInfo.getName());
		showInfoString(info,TuxGuitar.getProperty("version") + ":", pluginInfo.getVersion());
		showInfoString(info,TuxGuitar.getProperty("author") + ":", pluginInfo.getAuthor());
		showInfoString(info,TuxGuitar.getProperty("description") + ":", pluginInfo.getDescription());
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout());
		buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true));
		
		Button buttonExit = new Button(buttons, SWT.PUSH);
		buttonExit.setText(TuxGuitar.getProperty("exit"));
		buttonExit.setLayoutData(getButtonData());
		buttonExit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				dialog.dispose();
			}
		});
		
		dialog.setDefaultButton( buttonExit );
		
		DialogUtils.openDialog(dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}
	
	private void showInfoString(Composite parent,String key,String value){
		Label labelKey = new Label(parent,SWT.LEFT);
		Label labelValue = new Label(parent,SWT.LEFT | SWT.WRAP);
		labelKey.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false,true));
		labelValue.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,true));
		setBold(labelKey);
		labelKey.setText(key);
		labelValue.setText( (value != null && value.length() > 0)?value:TuxGuitar.getProperty("plugin.unknown-value"));
	}
	
	private void setBold(Label label){
		FontData[] fontDatas = label.getFont().getFontData();
		if(fontDatas.length > 0){
			final Font font = new Font(label.getDisplay(),fontDatas[0].getName(),(fontDatas[0].getHeight()),SWT.BOLD);
			label.setFont(font);
			label.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					font.dispose();
				}
			});
		}
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
}
