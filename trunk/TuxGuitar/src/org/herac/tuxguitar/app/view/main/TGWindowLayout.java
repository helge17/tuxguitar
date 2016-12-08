package org.herac.tuxguitar.app.view.main;

import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;

public class TGWindowLayout extends UITableLayout {
	
	private UIControl top;
	private UIControl topContainer;
	private UIControl divider;
	private UIControl bottomContainer;
	private UIControl bottom;
	
	public TGWindowLayout(UIControl top, UIControl topContainer, UIControl divider, UIControl bottomContainer, UIControl bottom) {
		this.top = top;
		this.topContainer = topContainer;
		this.divider = divider;
		this.bottomContainer = bottomContainer;
		this.bottom = bottom;
		this.configure();
	}
	
	public UISize computePackedSize(UILayoutContainer container) {
		return this.computePackedSize(container, true);
	}
	
	public UISize computePackedSize(UILayoutContainer container, boolean resetTableHeight) {
		if( resetTableHeight ) {
			this.set(this.bottomContainer, MAXIMUM_PACKED_HEIGHT, null);
		}
		return super.computePackedSize(container);
	}
	
	public void setBounds(UILayoutContainer container, UIRectangle bounds) {
		UISize packedContentSize = container.getPackedContentSize();
		if( packedContentSize.getHeight() > bounds.getHeight() ) {
			UISize preferredSize = this.getPreferredSizeFor(this.bottomContainer);
			this.set(this.bottomContainer, MAXIMUM_PACKED_HEIGHT, (preferredSize.getHeight() - (packedContentSize.getHeight() - bounds.getHeight())));
			this.computePackedSize(container, false);
		}
		
		super.setBounds(container, bounds);
	}
	
	public void configure() {
		this.set(UITableLayout.IGNORE_INVISIBLE, true);
		this.set(this.top, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, false, 1, 1, null, null, 0f);
		this.set(this.topContainer, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, null, null, 0f);
		this.set(this.topContainer, UITableLayout.PACKED_HEIGHT, 0f);
		this.set(this.divider, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false, 1, 1, null, null, 0f);
		this.set(this.divider, UITableLayout.PACKED_HEIGHT, 4f);
		this.set(this.divider, UITableLayout.MARGIN, 0f);
		this.set(this.bottomContainer, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, 1, null, null, 0f);
		this.set(this.bottom, 5, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, 1, null, null, 0f);
	}
}
