package gr.ntua.ivml.mint.util;

import gr.ntua.ivml.mint.mapping.model.Element;
import gr.ntua.ivml.mint.mapping.model.Mappings;
import gr.ntua.ivml.mint.persistent.Dataset;
import gr.ntua.ivml.mint.persistent.XmlSchema;
import gr.ntua.ivml.mint.persistent.XpathHolder;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JSTree {
	
	public JSTree() {
	}
	
	public static JSONArray getJSONObject(Dataset ds) {
		JSONArray result = new JSONArray();
		List<? extends TraversableI> children = ds.getRootHolder().getChildren();
		result = JSTree.getJSONObject(children);
		return result;
	}
	
	public static JSONArray getJSONObjectEnriched(Dataset ds) {
		JSONArray result = new JSONArray();
		List<? extends TraversableI> children = ds.getRootHolder().getChildren();
		Mappings schemaMap = ds.getSchema().getTemplate();
		result = JSTree.getJSONObject(children, schemaMap);
		return result;
	}

	public static JSONArray getJSONObject(List<? extends TraversableI> children, Mappings schemaMap) {
		JSONArray result = new JSONArray();
		for(TraversableI t : children) {
			XpathHolder xp = (XpathHolder) t;
			if(!xp.isTextNode()) {
				JSONObject child = JSTree.getJSONObject(xp, schemaMap);
				result.add(child);
			}
		}
		return result;
	}
	
	public static JSONArray getJSONObject(List<? extends TraversableI> children) {
		JSONArray result = new JSONArray();
		for(TraversableI t : children) {
			XpathHolder xp = (XpathHolder) t;
			if(!xp.isTextNode()) {
				JSONObject child = JSTree.getJSONObject(xp);
				result.add(child);
			}
		}
		return result;
	}
	
	public static JSONObject getJSONObject(XpathHolder xp) {
		JSONObject result = new JSONObject();
		JSONObject data = new JSONObject();
		data.element("title", xp.getNameWithPrefix(true));
		data.element("id", "schema-tree-" + xp.getDbID());
		if(xp.getTextNode() != null && xp.getTextNode().isUnique()) data.element("unique", "true");
		result.element("data", data);
		result.element("attr", data);
		result.element("metadata", new JSONObject()
			.element("distinct", xp.getDistinctCount())
			.element("count", xp.getCount())
			.element("depth", xp.getDepth())
			.element("xpath", xp.getXpathWithPrefix(true))
			.element("xpathHolderId", xp.getDbID()));
		List<? extends TraversableI> children = xp.getChildren();
		result.element("children", JSTree.getJSONObject(children));
		
		return result;
	}

	public static JSONObject getJSONObject(XpathHolder xp, Mappings schemaMap) {
		JSONObject result = new JSONObject();
		String xpath = xp.getXpathWithPrefix(true);
		Element el = schemaMap.findFirst(xpath);
		JSONObject data = new JSONObject();
		List<? extends TraversableI> children = xp.getChildren();
		JSONArray childrenArray = JSTree.getJSONObject(children, schemaMap);
		data.element("title", xp.getNameWithPrefix(true));
		data.element("id", "schema-tree-" + xp.getDbID());
		if(xp.getTextNode() != null && xp.getTextNode().isUnique()) data.element("unique", "true");
		result.element("data", data);
		result.element("attr", data);
		result.element("metadata", new JSONObject()
			.element("distinct", xp.getDistinctCount())
			.element("count", xp.getCount())
			.element("depth", xp.getDepth())
			.element("xpath", xpath)
			.element("xpathHolderId", xp.getDbID())
			.element("minOccurs", el.getMinOccurs())
			.element("maxOccurs", el.getMaxOccurs()));

		result.element("children",childrenArray);
		return result;
	}
}