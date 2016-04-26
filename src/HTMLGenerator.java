import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/*
 *   This class parses JSON file and converts it to HTML Tree structure and writes it to
 *   output file.
 */
public class HTMLGenerator {

	String inputFile;
	String outputFile;

	HTMLNode tree;

	Map<String, String> elementMap = new HashMap<String, String>();

	public HTMLGenerator(String inputFile, String outFile) {
		this.inputFile = inputFile;
		this.outputFile = outFile;
		tree = new HTMLNode();

		elementMap.put("link", "a");
		elementMap.put("paragraph", "p");
		elementMap.put("horizontal_rule", "hr");
	}

	/*
	 * Generates html file for input JSON file.
	 */
	public void generate() throws IOException {
		String fileData;

		// Step 1: read input file
		fileData = readInputFile(inputFile);
		// Step 2: generate HTML node
		generateHTMLNode(fileData);
		// Step 3: write generated HTML to file.
		writeToFile(tree.toString());
		System.out.println(tree.toString());
	}

	/*
	 * helper method to convert input data to HTML node
	 */
	private void generateHTMLNode(String fileData) {
		JSONObject root = new JSONObject(fileData);
		tree.setType("html");
		// check if metadata is present
		if (root.has("metadata")) {
			JSONObject metadata = root.getJSONObject("metadata");
			HTMLNode metaNode = processMetadata(metadata);
			if (metadata != null)
				tree.addChild(metaNode);
		}

		HTMLNode bodyNode = new HTMLNode();
		bodyNode.setType("body");
		tree.addChild(bodyNode);
		// check if header is present
		if (root.has("header")) {
			JSONObject header = root.getJSONObject("header");
			HTMLNode headerNode = processNode(header);
			headerNode.setType("header");
			bodyNode.addChild(headerNode);
		}
		// check if main is present
		if (root.has("main")) {
			JSONArray main = root.getJSONArray("main");
			processMain(main, bodyNode);
		}
		// check if footer is present
		if (root.has("footer")) {
			JSONObject footer = root.getJSONObject("footer");
			HTMLNode footerNode = processFooter(footer);
			tree.addChild(footerNode);
		}
	}

	/*
	 * processes main node and generate navigation and section nodes.
	 */
	private void processMain(JSONArray main, HTMLNode body) {
		Iterator<Object> iterator = main.iterator();
		while (iterator.hasNext()) {
			JSONObject object = (JSONObject) iterator.next();
			if (object.has("type")) {
				if (object.getString("type").equals("menu")) {
					HTMLNode menuNode = processNode(object);
					menuNode.setType("nav");
					body.addChild(menuNode);
				} else if (object.getString("type").equals("content")) {
					HTMLNode contentNode = processNode(object);
					contentNode.setType("section");

					body.addChild(contentNode);
				}
			}
		}

	}

	/*
	 * Processes metadata information and generate head node.
	 */
	private HTMLNode processMetadata(JSONObject metadata) {
		HTMLNode metanode = new HTMLNode();
		metanode.setType("head");
		Iterator<String> keys = metadata.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			if (key.equals("title")) {
				HTMLNode titleNode = new HTMLNode();
				titleNode.setType("title");
				titleNode.setText(metadata.getString("title"));
				metanode.addChild(titleNode);
			} else {
				HTMLNode node = new HTMLNode();
				node.setType("meta");
				node.addAttribute(createProperty("name", key));
				node.addAttribute(createProperty("content", metadata.getString(key)));
				metanode.addChild(node);
			}
		}
		return metanode;
	}

	/*
	 * processes footer node
	 */
	private HTMLNode processFooter(JSONObject footer) {

		HTMLNode footerNode = processNode(footer);
		footerNode.setType("footer");
		footerNode.addStyle(createProperty("clear", "both"));
		return footerNode;
	}

	/*
	 * helper method to process JSONNode and generate HTMLNode
	 */
	private HTMLNode processNode(JSONObject node) {
		List<CSSProperty> styleBuilder = new ArrayList<CSSProperty>();
		CSSMapper mapper = new CSSMapper();
		HTMLNode htmlNode = new HTMLNode();

		Iterator<String> keys = node.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			if (key.equals("elements")) {

				JSONArray elementArray = node.getJSONArray("elements");
				Iterator<Object> iterator = elementArray.iterator();
				while (iterator.hasNext()) {
					JSONObject element = (JSONObject) iterator.next();
					HTMLNode elementNode = processElement(element);
					htmlNode.addChild(elementNode);
				}

			} else {
				styleBuilder.addAll(mapper.mapCSSProperty(key, node.get(key)));
			}
		}
		htmlNode.setStyle(styleBuilder);
		return htmlNode;
	}

	/*
	 * helper method to process element node.
	 */
	private HTMLNode processElement(JSONObject element) {
		List<CSSProperty> elementStyle = new ArrayList<CSSProperty>();
		CSSMapper mapper = new CSSMapper();
		HTMLNode elementNode = new HTMLNode();

		Iterator<String> elementKeys = element.keys();
		while (elementKeys.hasNext()) {
			String elementKey = elementKeys.next();
			if (elementKey.equals("type")) {
				String val = element.getString(elementKey);
				if (elementMap.containsKey(val)) {
					elementNode.setType(elementMap.get(val));
				} else {
					elementNode.setType("div");
				}
			} else if (elementKey.equals("text")) {
				elementNode.setText(element.getString(elementKey));
			} else if (elementKey.equals("url")) {
				CSSProperty property = new CSSProperty();
				property.setName("href");
				property.setValue(element.getString(elementKey));
				elementNode.addAttribute(property);
			} else {
				elementStyle.addAll(mapper.mapCSSProperty(elementKey, element.get(elementKey)));
			}
		}
		elementNode.setStyle(elementStyle);
		return elementNode;
	}

	/*
	 * helper method to create name,value pair
	 */
	private CSSProperty createProperty(String name, String val) {
		CSSProperty property = new CSSProperty();
		property.setName(name);
		property.setValue(val);
		return property;
	}

	/*
	 * reads input file and returns the content
	 */
	private String readInputFile(String filename) throws IOException {
		File inputFile = new File(filename);
		FileInputStream inputStream = new FileInputStream(inputFile);
		byte[] data = new byte[(int) inputFile.length()];
		inputStream.read(data);
		inputStream.close();
		String str = new String(data, "UTF-8");
		return str;
	}

	/*
	 * writes content to file
	 */
	private void writeToFile(String data) throws IOException {
		FileWriter fileWriter = new FileWriter(outputFile);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		bufferedWriter.write(data);
		bufferedWriter.close();
	}
}
