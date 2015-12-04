package org.herac.tuxguitar.android.view.browser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserRefreshAction;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.application.TGApplicationUtil;
import org.herac.tuxguitar.android.browser.TGBrowserCollection;
import org.herac.tuxguitar.android.browser.TGBrowserManager;
import org.herac.tuxguitar.android.browser.assets.TGAssetBrowserFactory;
import org.herac.tuxguitar.android.browser.filesystem.TGFsBrowserFactory;
import org.herac.tuxguitar.android.browser.model.TGBrowserElement;
import org.herac.tuxguitar.android.browser.model.TGBrowserException;
import org.herac.tuxguitar.android.browser.model.TGBrowserSession;
import org.herac.tuxguitar.android.view.browser.filesystem.TGBrowserSettingsFactoryImpl;
import org.herac.tuxguitar.android.view.util.TGSelectableAdapter;
import org.herac.tuxguitar.android.view.util.TGSelectableItem;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGOutputStreamBase;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.error.TGErrorManager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

public class TGBrowserView extends RelativeLayout {
	
	private TGBrowserActionHandler actionHandler;
	
	public TGBrowserView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.actionHandler = new TGBrowserActionHandler(this);
	}

	public void onFinishInflate() {
		try {
			this.addBrowserDefaults();
			this.fillFormats();
			this.fillListView();
			this.addListeners();
			this.refresh();
		} catch (TGBrowserException e) {
			TGErrorManager.getInstance(findContext()).handleError(e);
		}
	}

	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		this.post(new Runnable() {
			public void run() {
				TGBrowserView.this.updateSavePanel();
			}
		});
	}
	
	public TGSelectableItem[] createCollectionValues() {
		List<TGSelectableItem> selectableItems = new ArrayList<TGSelectableItem>();
		selectableItems.add(new TGSelectableItem(null, getContext().getString(R.string.global_spinner_select_option)));
		
		Iterator<TGBrowserCollection> collections = TGBrowserManager.getInstance(this.findContext()).getCollections();
		while( collections.hasNext() ) {
			TGBrowserCollection collection = collections.next();
			selectableItems.add(new TGSelectableItem(collection, collection.getSettings().getTitle()));
		}
		TGSelectableItem[] builtItems = new TGSelectableItem[selectableItems.size()];
		selectableItems.toArray(builtItems);
		return builtItems;
	}
	
	public void refreshCollections() {
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(findActivity(), R.layout.view_browser_spinner_item, createCollectionValues());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		TGSelectableItem selectedItem = new TGSelectableItem(findCurrentCollection(), null);
		
		Spinner spinner = (Spinner) this.findViewById(R.id.browser_collections);
		OnItemSelectedListener listener = spinner.getOnItemSelectedListener();
		spinner.setOnItemSelectedListener(null);
		spinner.setAdapter(arrayAdapter);
		spinner.setSelection(arrayAdapter.getPosition(selectedItem), false);
		spinner.setOnItemSelectedListener(listener);
	}
	
	public TGBrowserCollection selectCollection() {
		Spinner spinner = (Spinner) this.findViewById(R.id.browser_collections);
		TGSelectableItem selectableItem = (TGSelectableItem) spinner.getSelectedItem();
		return (selectableItem != null ? (TGBrowserCollection) selectableItem.getItem() : null);
	}
	
	public TGBrowserCollection findSelectedCollection() {
		Spinner spinner = (Spinner) this.findViewById(R.id.browser_collections);
		TGSelectableItem selectableItem = (TGSelectableItem) spinner.getSelectedItem();
		return (selectableItem != null ? (TGBrowserCollection) selectableItem.getItem() : null);
	}
	
	public TGBrowserCollection findCurrentCollection() {
		TGBrowserSession session = TGBrowserManager.getInstance(this.findContext()).getSession();
		return (session.getCollection());
	}
	
	public String createFormatLabel(TGFileFormat format) {
		return createExtension(format, format.getName());
	}
	
	public String createFormatDropDownLabel(TGFileFormat format) {
		return format.getName();
	}
	
	public List<TGSelectableItem> createFormatValues() {
		List<TGSelectableItem> selectableItems = new ArrayList<TGSelectableItem>();
		
		Iterator<?> outputStreams = TGFileFormatManager.getInstance(findContext()).getOutputStreams();
		while( outputStreams.hasNext() ) {
			TGOutputStreamBase outputStream = (TGOutputStreamBase) outputStreams.next();
			TGFileFormat format = outputStream.getFileFormat();
			selectableItems.add(new TGSelectableItem(format, createFormatLabel(format), createFormatDropDownLabel(format)));
		}
		
		return selectableItems;
	}
	
	public void fillListView() {
		ListView listView = (ListView) findViewById(R.id.browser_elements);
		listView.setAdapter(new TGBrowserListAdapter(getContext()));
		listView.setOnItemClickListener(new TGBrowserItemListener(this));
	}
	
	public void fillFormats() {
		TGSelectableAdapter selectableAdapter = new TGSelectableAdapter(findActivity(), R.layout.view_browser_spinner_item, createFormatValues());
		selectableAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner = (Spinner) this.findViewById(R.id.browser_save_format);
		spinner.setAdapter(selectableAdapter);
	}
	
	public TGFileFormat findSelectedFormat() {
		Spinner spinner = (Spinner) this.findViewById(R.id.browser_save_format);
		TGSelectableItem selectableItem = (TGSelectableItem) spinner.getSelectedItem();
		return (selectableItem != null ? (TGFileFormat) selectableItem.getItem() : null);
	}
	
	public void addBrowserDefaults() throws TGBrowserException {
		TGBrowserManager browserManager = TGBrowserManager.getInstance(findContext());
		browserManager.addFactory(new TGAssetBrowserFactory(this.findContext()));
		browserManager.addFactory(new TGFsBrowserFactory(new TGBrowserSettingsFactoryImpl(findContext(), findActivity())));
		browserManager.restoreCollections();
		
		TGBrowserCollection defaultCollection = browserManager.getDefaultCollection();
		if( defaultCollection != null ) {
			this.getActionHandler().createOpenSessionAction((defaultCollection)).process();
		}
	}
	
	public void requestRefresh() {
		this.getActionHandler().createBrowserAction(TGBrowserRefreshAction.NAME).process();
	}

	public void updateSavePanel() {
		TGBrowserSession session = TGBrowserManager.getInstance(this.findContext()).getSession();
		
		View view = findViewById(R.id.browser_save_panel);
		view.setVisibility(session.getSessionType() == TGBrowserSession.WRITE_MODE ? View.VISIBLE : View.GONE);
		
		EditText editText = (EditText) findViewById(R.id.browser_save_element_name);
		editText.setText(findActivity().getString(R.string.browser_file_default_name));
	}
	
	public void addListeners() {
		findViewById(R.id.browser_save_button).setOnClickListener(createSaveButtonListener());
		
		((Spinner) this.findViewById(R.id.browser_collections)).setOnItemSelectedListener(createCollectionsSpinnerListener());
		
		TGActionManager.getInstance(this.findContext()).addPostExecutionListener(new TGBrowserEventListener(this));
	}
	
	public void updateItems() throws TGBrowserException{
		TGBrowserSession session = TGBrowserManager.getInstance(this.findContext()).getSession();
		boolean writable = session.getBrowser() != null && session.getBrowser().isWritable();
		
		this.findViewById(R.id.browser_save_element_name).setEnabled(writable);
		this.findViewById(R.id.browser_save_format).setEnabled(writable);
		this.findViewById(R.id.browser_save_button).setEnabled(writable);
	}
	
	public OnItemSelectedListener createCollectionsSpinnerListener() {
		return new OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		    	processSelectedCollection();
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    	processSelectedCollection();
		    }
		};
	}
	
	public OnClickListener createSaveButtonListener() {
		return new OnClickListener() {
			public void onClick(View v) {
				processSaveButton();
			}
		};
	}
	
	public void processSelectedCollection() {
		this.getActionHandler().createOpenSessionAction(findSelectedCollection()).process();
	}
	
	public void processSaveButton() {
		try {
			TGBrowserSession session = TGBrowserManager.getInstance(this.findContext()).getSession();
			if( session.getBrowser() != null ) {
				TGFileFormat format = findSelectedFormat();
				
				EditText editText = (EditText) findViewById(R.id.browser_save_element_name);
				InputMethodManager inputMethodManager = (InputMethodManager) findActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);
				
				String elementName = editText.getText().toString() + createExtension(format, TGFileFormatManager.DEFAULT_EXTENSION);
				
				TGBrowserElement element = findElement(elementName);
				if( element != null && element.isWritable() ) {
					TGActionProcessor actionProcessor = this.getActionHandler().createBrowserSaveElementAction(element, format);
					this.getActionHandler().processConfirmableAction(actionProcessor, this.findActivity().getString(R.string.browser_file_overwrite_question));
				} 
				else {
					if( session.getBrowser().isWritable() ) {
						element = session.getBrowser().createElement(elementName);
						if( element != null && element.isWritable() ) {
							this.getActionHandler().createBrowserSaveElementAction(element, format).process();
						}
					}
				}
			}
		} catch (TGBrowserException e) {
			TGErrorManager.getInstance(findContext()).handleError(e);
		}
	}
	
	public String createExtension(TGFileFormat format, String defaultValue) {
		String[] supportedFormats = format.getSupportedFormats();
		if( supportedFormats != null && supportedFormats.length > 0 ) {
			return this.createExtension(supportedFormats[0]);
		}
		return defaultValue;
	}
	
	public String createExtension(String supportedFormat) {
		return ("." + supportedFormat);
	}
	
	public String findExtension(String text, String defaultValue) {
		int index = text.lastIndexOf(".");
		if( index >= 0 ) {
			return text.substring(index);
		}
		return defaultValue;
	}
	
	public TGFileFormat findFormatByElementName(TGBrowserElement element) throws TGBrowserException {
		TGFileFormat defaultFormat = null;
		String extension = findExtension(element.getName(), null);
		
		Iterator<?> outputStreams = TGFileFormatManager.getInstance(findContext()).getOutputStreams();
		while( outputStreams.hasNext() ) {
			TGOutputStreamBase outputStream = (TGOutputStreamBase) outputStreams.next();
			TGFileFormat format = outputStream.getFileFormat();
			
			if( extension != null ) {
				for(String supportedFormat : format.getSupportedFormats()) {
					if( extension.toLowerCase().equals(this.createExtension(supportedFormat).toLowerCase())) {
						return format;
					}
				}
			}
			
			if( defaultFormat == null ) {
				defaultFormat = format;
			}
		}
		
		return defaultFormat;
	}
	
	public TGBrowserElement findElement(String name) throws TGBrowserException {
		TGBrowserSession tgBrowserSession = TGBrowserManager.getInstance(this.findContext()).getSession();
		if( tgBrowserSession.getCurrentElements() != null ) {
			for(TGBrowserElement tgBrowserElement : tgBrowserSession.getCurrentElements()){
				if( tgBrowserElement.getName().equals(name) ) {
					return tgBrowserElement;
				}
			}
		}
		return null;
	}
	
	public void refreshListView() {
		ListView listView = (ListView) findViewById(R.id.browser_elements);

		TGBrowserListAdapter tgBrowserElementAdapter = (TGBrowserListAdapter) listView.getAdapter();
		TGBrowserSession tgBrowserSession = TGBrowserManager.getInstance(this.findContext()).getSession();
		if (tgBrowserSession.getCurrentElements() == null) {
			tgBrowserElementAdapter.clearElements();
		} else {
			tgBrowserElementAdapter.fillElements(tgBrowserSession.getCurrentElements());
		}
		
		tgBrowserElementAdapter.notifyDataSetChanged();
	}
	
	public void refresh() throws TGBrowserException {
		this.refreshListView();
		this.refreshCollections();
		this.updateItems();
	}
	
	public TGContext findContext() {
		return TGApplicationUtil.findContext(this);
	}

	public TGActivity findActivity() {
		return (TGActivity) getContext();
	}
	
	public TGBrowserActionHandler getActionHandler() {
		return this.actionHandler;
	}
}
