package app.tuxguitar.ui.widget;

import app.tuxguitar.ui.layout.UILayout;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.ui.resource.UISize;

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
