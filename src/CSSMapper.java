import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

  /*
   * This class provides CSS mapping of the JSON properties
   */
public class CSSMapper {
    
	 
		/*
		 * map attribute to appropriate CSS attribute
		 */
		public List<CSSProperty> mapCSSProperty(String elementKey, Object object) {
			List<CSSProperty> properties = new ArrayList<CSSProperty>();

			switch (elementKey) {
			case "alignment":
				properties.add(createProperty("text-align", (String) object));
				break;
			case "margin":
				properties.addAll(mapMargin((JSONObject) object));
				break;
			case "size":
				properties.addAll(mapCSSSize((JSONObject) object));
				break;
			case "position":
				properties.add(createProperty("float", "left"));

				break;

			case "padding":
				properties.addAll(mapCSSPadding((JSONObject) object));

				break;
			default:
				properties.add(createProperty(elementKey, (String) object));
			}
			return properties;
		}

		/*
		 * helper methods to map JSONObject to CSS attributes
		 */
		private List<CSSProperty> mapCSSPadding(JSONObject paddingNode) {
			List<CSSProperty> properties = new ArrayList<CSSProperty>();
			Iterator<String> values = paddingNode.keys();
			while (values.hasNext()) {
				String value = values.next();
				if (value.equals("all")) {
					properties.add(createProperty("padding", paddingNode.getString(value)));
				} else {
					properties.add(createProperty("padding-", paddingNode.getString(value)));
				}
			}
			return properties;
		}

		private List<CSSProperty> mapCSSSize(JSONObject sizeNode) {
			List<CSSProperty> properties = new ArrayList<CSSProperty>();
			if (sizeNode.has("x")) {

				properties.add(createProperty("width", sizeNode.getString("x")));
			}
			if (sizeNode.has("y")) {
				properties.add(createProperty("height", sizeNode.getString("y")));
			}
			return properties;
		}

		private List<CSSProperty> mapMargin(JSONObject valueNode) {
			List<CSSProperty> properties = new ArrayList<CSSProperty>();
			Iterator<String> values = valueNode.keys();
			while (values.hasNext()) {
				String value = values.next();
				if (value.equals("all")) {
					properties.add(createProperty("margin", valueNode.getString("all")));

				} else {
					properties.add(createProperty("margin-" + value, valueNode.getString(value)));
				}
			}
			return properties;
		}

		private CSSProperty createProperty(String name, String val) {
			CSSProperty property = new CSSProperty();
			property.setName(name);
			property.setValue(val);
			return property;
		}
  }
