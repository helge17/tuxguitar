package app.tuxguitar.app.view.toolbar.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import app.tuxguitar.util.TGUserFileUtils;

public class TGMainToolBarConfigManager {

	// xml tags
	private static final String TAG_ROOT = "toolbar";
	private static final String TAG_MAINTOOLBAR = "maintoolbar";
	private static final String TAG_LEFT = "left";
	private static final String TAG_CENTER = "center";
	private static final String TAG_RIGHT = "right";
	private static final String TAG_ITEM = "item";
	private static final String ATTRIBUTE_NAME = "name";
	private List<TGMainToolBarConfig> configs;

	public TGMainToolBarConfigManager() {
		this.configs = new ArrayList<TGMainToolBarConfig>();

		// default
		this.configs.add(new TGMainToolBarConfig(""));

		Node configFileNode = null;
		// try to read configuration if available
		File file = new File(TGUserFileUtils.PATH_USER_TOOLBAR);
		if (TGUserFileUtils.isExistentAndReadable(file)) {
			try {
				InputStream stream = new FileInputStream(TGUserFileUtils.PATH_USER_TOOLBAR);
				if (stream != null) {
					Document doc = getDocument(stream);
					configFileNode = doc.getFirstChild();
					if (!configFileNode.getNodeName().equals(TAG_ROOT)) {
						return;
					}
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		if (configFileNode != null) {
			NodeList nodeList = configFileNode.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node child = nodeList.item(i);
				String nodeName = child.getNodeName();
				if (nodeName.equals(TAG_MAINTOOLBAR)) {
					TGMainToolBarConfig config = this.getConfig(child);
					if ((config != null) && !config.getName().equals("")
							&& !getConfigNames().contains(config.getName())) {
						this.configs.add(config);
					}
				}
			}
		}
	}

	public List<String> getConfigNames() {
		List<String> list = new ArrayList<String>();
		for (TGMainToolBarConfig config : this.configs) {
			list.add(config.getName());
		}
		return list;
	}

	public TGMainToolBarConfig getConfig(String toolBarName) {
		for (TGMainToolBarConfig config : this.configs) {
			if (config.getName().equals(toolBarName)) {
				return config;
			}
		}
		return null;
	}

	public TGMainToolBarConfig getDefaultConfig() {
		return getConfig("");
	}

	public void saveConfig(TGMainToolBarConfig configToSave) {
		TGMainToolBarConfig configToDelete = this.getConfig(configToSave.getName());
		if (configToDelete != null) {
			this.configs.remove(configToDelete);
		}
		this.configs.add(configToSave);
		this.save();
	}

	public void deleteConfig(TGMainToolBarConfig config) {
		TGMainToolBarConfig configToDelete = this.getConfig(config.getName());
		if (configToDelete != null) {
			this.configs.remove(configToDelete);
		}
		this.save();
	}

	private TGMainToolBarConfig getConfig(Node nodeToolBar) {
		if (nodeToolBar == null) {
			return null;
		}
		String name = nodeToolBar.getAttributes().getNamedItem(ATTRIBUTE_NAME).getTextContent();
		if (name.equals("")) {
			return null;
		}
		TGMainToolBarConfig config = new TGMainToolBarConfig(name);
		NodeList nodeList = nodeToolBar.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node child = nodeList.item(i);
			String nodeName = child.getNodeName();
			switch (nodeName) {
			case TAG_LEFT:
				config.setLeftAreaContent(this.readAreaContent(child));
				break;
			case TAG_CENTER:
				config.setCenterAreaContent(this.readAreaContent(child));
				break;
			case TAG_RIGHT:
				config.setRightAreaContent(this.readAreaContent(child));
				break;
			default:
				// do nothing
			}
		}
		return config;
	}

	private List<String> readAreaContent(Node node) {
		List<String> items = new ArrayList<String>();
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node child = nodeList.item(i);
			String nodeName = child.getNodeName();
			if (nodeName.equals(TAG_ITEM)) {
				items.add(child.getTextContent());
			}
		}
		return items;
	}

	private static Document getDocument(InputStream stream)
			throws ParserConfigurationException, SAXException, IOException {
		Document document = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// CVE-2020-14940
		try {
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			factory.setXIncludeAware(false);
		} catch (Throwable throwable) {
		}
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.parse(stream);

		return document;
	}

	private void save() {
		try {
			File file = new File(TGUserFileUtils.PATH_USER_TOOLBAR);
			OutputStream stream = new FileOutputStream(file);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			Node root = document.createElement(TAG_ROOT);
			this.write(root);
			document.appendChild(root);
			TransformerFactory xformFactory = TransformerFactory.newInstance();
			Transformer idTransform = xformFactory.newTransformer();
			Source input = new DOMSource(document);
			Result output = new StreamResult(stream);
			idTransform.setOutputProperty(OutputKeys.INDENT, "yes");
			idTransform.transform(input, output);
			stream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	// write xml file
	private void write(Node root) {
		Document document = root.getOwnerDocument();
		for (TGMainToolBarConfig config : this.configs) {
			if (!config.getName().equals("")) {
				Node nodeConfig = document.createElement(TAG_MAINTOOLBAR);
				Attr attrName = document.createAttribute(ATTRIBUTE_NAME);
				attrName.setNodeValue(config.getName());
				nodeConfig.getAttributes().setNamedItem(attrName);
				root.appendChild(nodeConfig);
				Node nodeLeft = document.createElement(TAG_LEFT);
				this.writeArea(nodeLeft, config.getAreaContent(TGMainToolBar.LEFT_AREA));
				nodeConfig.appendChild(nodeLeft);
				Node nodeCenter = document.createElement(TAG_CENTER);
				this.writeArea(nodeCenter, config.getAreaContent(TGMainToolBar.CENTER_AREA));
				nodeConfig.appendChild(nodeCenter);
				Node nodeRight = document.createElement(TAG_RIGHT);
				this.writeArea(nodeRight, config.getAreaContent(TGMainToolBar.RIGHT_AREA));
				nodeConfig.appendChild(nodeRight);
			}
		}
	}

	private void writeArea(Node root, List<String> itemNames) {
		Document document = root.getOwnerDocument();
		for (String itemName : itemNames) {
			Node nodeItem = document.createElement(TAG_ITEM);
			nodeItem.setTextContent(itemName);
			root.appendChild(nodeItem);
		}
	}
}
