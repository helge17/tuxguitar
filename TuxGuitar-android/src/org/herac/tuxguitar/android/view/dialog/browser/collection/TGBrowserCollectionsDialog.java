package org.herac.tuxguitar.android.view.dialog.browser.collection;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.browser.TGBrowserCollection;
import org.herac.tuxguitar.android.browser.TGBrowserManager;
import org.herac.tuxguitar.android.browser.model.TGBrowserFactory;
import org.herac.tuxguitar.android.view.dialog.fragment.TGModalFragment;
import org.herac.tuxguitar.android.view.util.TGSelectableItem;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.song.models.TGChannel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TGBrowserCollectionsDialog extends TGModalFragment {

	private TGBrowserCollectionsEventListener eventListener;
	private TGBrowserCollectionsActionHandler actionHandler;
	
	public TGBrowserCollectionsDialog() {
		super(R.layout.view_browser_collections_dialog);
	}

	public TGChannel getChannel() {
		return this.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.createActionBar(false, false, R.string.browser_collections_dlg_title);
	}

	@SuppressLint("InflateParams")
	public void onPostInflateView() {
		this.actionHandler = new TGBrowserCollectionsActionHandler(this);
		this.eventListener = new TGBrowserCollectionsEventListener(this);
		
		this.fillFactories();
		this.fillAddButton();
		this.fillColletions();
	}

	@Override
	public void onShowView() {
		TGActionManager.getInstance(findContext()).addPostExecutionListener(this.eventListener);
	}

	@Override
	public void onHideView() {
		TGActionManager.getInstance(findContext()).removePostExecutionListener(this.eventListener);
	}

	public TGSelectableItem[] createFactoryValues() {
		List<TGSelectableItem> selectableItems = new ArrayList<TGSelectableItem>();
		
		Iterator<TGBrowserFactory> factories = TGBrowserManager.getInstance(this.findContext()).getFactories();
		while( factories.hasNext() ) {
			TGBrowserFactory factory = factories.next();
			selectableItems.add(new TGSelectableItem(factory, factory.getName()));
		}
		TGSelectableItem[] builtItems = new TGSelectableItem[selectableItems.size()];
		selectableItems.toArray(builtItems);
		return builtItems;
	}
	
	public void fillFactories() {
		ArrayAdapter<TGSelectableItem> arrayAdapter = new ArrayAdapter<TGSelectableItem>(getActivity(), android.R.layout.simple_spinner_item, createFactoryValues());
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.browser_collections_dlg_add_type);
		spinner.setAdapter(arrayAdapter);
	}
	
	public void fillAddButton() {
		ImageButton imageButton = (ImageButton) this.getView().findViewById(R.id.browser_collections_dlg_add_button);
		imageButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				createCollection(findSelectedFactory());
			}
		});
	}
	
	public void fillColletions() {
		ListView listView = (ListView) getView().findViewById(R.id.browser_collections_dlg_list);
		listView.setAdapter(new TGBrowserCollectionsAdapter(this, this.getView().getContext()));
		
		this.refreshListView();
	}
	
	public TGBrowserFactory findSelectedFactory() {
		Spinner spinner = (Spinner) this.getView().findViewById(R.id.browser_collections_dlg_add_type);
		
		return (TGBrowserFactory) ((TGSelectableItem) spinner.getSelectedItem()).getItem();
	}
	
	public void createCollection(TGBrowserFactory factory) {
		if( factory != null ) {
			factory.createSettings(new TGBrowserCollectionsSettingsHandler(this, factory.getType()));
		}
	}
	
	public void addCollection(TGBrowserCollection collection) {
		this.getActionHandler().createAddCollectionAction(collection).process();
	}
	
	public void removeCollection(TGBrowserCollection collection) {
		this.getActionHandler().createRemoveCollectionAction(collection).process();
	}
	
	public void refreshListView() {
		ListView listView = (ListView) this.getView().findViewById(R.id.browser_collections_dlg_list);
		TGBrowserCollectionsAdapter collectionsAdapter = (TGBrowserCollectionsAdapter) listView.getAdapter();
		
		collectionsAdapter.clearCollections();
		
		Iterator<TGBrowserCollection> collections = TGBrowserManager.getInstance(this.findContext()).getCollections();
		while( collections.hasNext() ) {
			collectionsAdapter.addCollection(collections.next());
		}
		
		collectionsAdapter.notifyDataSetChanged();
	}

	public TGBrowserCollectionsActionHandler getActionHandler() {
		return actionHandler;
	}
}
