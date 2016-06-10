package org.herac.tuxguitar.ui.widget;

import org.herac.tuxguitar.ui.layout.UILayout;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;

public interface UILayoutContainer extends UIContainer {
	
	UISize getPackedContentSize();
	
	void setPackedContentSize(UISize packedContentSize);
	
	UIRectangle getChildArea();
	
	UILayout getLayout();
	
	void setLayout(UILayout layout);
	
	void layout(UIRectangle bounds);
	
	void layout();
	
	void pack();
}
