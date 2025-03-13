package app.tuxguitar.ui.qt;

public class QTEnvironment {

	private static final String QT_STYLE = "app.tuxguitar.ui.qt.style";

	public QTEnvironment() {
		super();
	}

	public String findStyle() {
		return System.getProperty(QT_STYLE);
	}
}
