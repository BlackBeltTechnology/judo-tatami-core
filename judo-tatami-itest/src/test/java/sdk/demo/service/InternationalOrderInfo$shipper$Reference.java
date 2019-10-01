package sdk.demo.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class InternationalOrderInfo$shipper$Reference {
	private java.util.UUID __identifier;

    @java.beans.ConstructorProperties({"__identifier"})
    InternationalOrderInfo$shipper$Reference(java.util.UUID __identifier) {
		this.__identifier = __identifier;
    }

	// Getters
	public java.util.UUID get__identifier() {
		return this.__identifier;
	}

	// Setters
	public void set__identifier(java.util.UUID __identifier) {
		this.__identifier = __identifier;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap();
		if (this.__identifier != null) {
			ret.put("__identifier", this.__identifier);
		}

		return ret;
	}

	public static InternationalOrderInfo$shipper$Reference fromMap(Map<String, Object> map) {
		InternationalOrderInfo$shipper$ReferenceBuilder builder = internationalOrderInfo$shipper$ReferenceBuilder();
		if (map.containsKey("__identifier")) {
			builder.__identifier((java.util.UUID)map.get("__identifier"));
		}
	    return builder.build();
	}

	public String toString() {
	    return "InternationalOrderInfo$shipper$Reference(identifier=" + this.__identifier + ")";
	}

    // Builder
	public static InternationalOrderInfo$shipper$ReferenceBuilder internationalOrderInfo$shipper$ReferenceBuilder() {
		return new InternationalOrderInfo$shipper$ReferenceBuilder();
	}

	public static class InternationalOrderInfo$shipper$ReferenceBuilder {
		private java.util.UUID __identifier;

		InternationalOrderInfo$shipper$ReferenceBuilder() {
		}

		public InternationalOrderInfo$shipper$ReferenceBuilder __identifier(java.util.UUID __identifier) {
			this.__identifier = __identifier;
			return this;
		}
		public InternationalOrderInfo$shipper$Reference build() {
			return new InternationalOrderInfo$shipper$Reference(this.__identifier);
		}

		public String toString() {
    	    return "InternationalOrderInfo$shipper$ReferenceBuilder.InternationalOrderInfo$shipper$Reference(identifier=" + __identifier + ")";
		}
	}
}
