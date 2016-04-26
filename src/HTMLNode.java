import java.util.ArrayList;
import java.util.List;
/*
 *  Class to represent HTML in form of tree.It has type, style attributes and child elements
 */

public class HTMLNode {
	  private String type;
	  private List<CSSProperty> style;
	  private List<CSSProperty> attributes;
	    private String text;
		  private List<HTMLNode> children;;
	
	public HTMLNode(){
		attributes = new ArrayList<CSSProperty>();
		children = new ArrayList<HTMLNode>();
	}
		  
    public void addAttribute(CSSProperty property){
	    attributes.add(property);
	}
    public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<CSSProperty> getStyle() {
		return style;
	}
	public void setStyle(List<CSSProperty> style) {
		this.style = style;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public void addChild(HTMLNode node){
		children.add(node);
	}
	public void addStyle(CSSProperty property){
		style.add(property);
	}
	
	/*
	 * generates string format of tree
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("<" + type + " ");	
		if( attributes != null && ! attributes.isEmpty()){
		    for(CSSProperty property: attributes){
		    	builder.append(property.getName());
		    	builder.append("=\"");
		    	builder.append(property.getValue());
		    	builder.append("\" ");
		    }
		}
		if(style != null && ! style.isEmpty()){
			builder.append("style=\"");
			for(CSSProperty property: style){
				builder.append(property.getName());
				builder.append(":");
				builder.append(property.getValue());
				builder.append(";");
			}
			builder.append("\"");
		}
		builder.append("> \n");
		if(text != null){
			builder.append(text);
		}
		if(!children.isEmpty()){
			for(HTMLNode node: children){
				if(node != null)
				builder.append(node.toString());
			}
			builder.append("\n\n");
		}
		builder.append("</" + type + ">  \n");
		return builder.toString();
	}
  }
