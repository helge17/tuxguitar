package org.herac.tuxguitar.app.view.main;

import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;

public class TGWindowLayout extends UITableLayout {
	
	private UIControl toolBar;
	private UIControl tabFolder;
	private UIControl divider;
	private UIControl table;
	private UIControl footer;
	
	public TGWindowLayout(UIControl toolBar, UIControl tabFolder, UIControl divider, UIControl table, UIControl footer) {
		this.toolBar = toolBar;
		this.tabFolder = tabFolder;
		this.divider = divider;
		this.table = table;
		this.footer = footer;
		this.configure();
	}
	
	public UISize computePackedSize(UILayoutContainer container) {
		return this.computePackedSize(container, true);
	}
	
	public UISize computePackedSize(UILayoutContainer container, boolean resetTableHeight) {
		if( resetTableHeight ) {
			this.set(this.table, MAXIMUM_PACKED_HEIGHT, null);
		}
		return super.computePackedSize(container);
	}
	
	public void setBounds(UILayoutContainer container, UIRectangle bounds) {
		UISize packedContentSize = container.getPackedContentSize();
		if( packedContentSize.getHeight() > bounds.getHeight() ) {
			UISize preferredSize = this.getPreferredSizeFor(this.table);
			this.set(this.table, MAXIMUM_PACKED_HEIGHT, (preferredSize.getHeight() - (packedContentSize.getHeight() - bounds.getHeight())));
			this.computePackedSize(container, false);
		}
		
		super.setBounds(container, bounds);
	}
	
	public void configure() {
		this.set(UITableLayout.IGNORE_INVISIBLE, true);
		this.set(this.toolBar, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, false, 1, 1, null, null, 0f);
		this.set(this.tabFolder, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f);
		this.set(this.tabFolder, UITableLayout.PACKED_HEIGHT, 0f);
		this.set(this.divider, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false, 1, 1, null, null, 0f);
		this.set(this.divider, UITableLayout.PACKED_HEIGHT, 4f);
		this.set(this.divider, UITableLayout.MARGIN, 0f);
		this.set(this.table, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, 1, null, null, 0f);
		this.set(this.footer, 5, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, 1, null, null, 0f);
	}
}
