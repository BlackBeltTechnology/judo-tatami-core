package sdk.demo.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class InternationalOrderInfo$items$Reference {
	private java.util.UUID __identifier;

    @java.beans.ConstructorProperties({"__identifier"})
    InternationalOrderInfo$items$Reference(java.util.UUID __identifier) {
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

	public static InternationalOrderInfo$items$Reference fromMap(Map<String, Object> map) {
		InternationalOrderInfo$items$ReferenceBuilder builder = internationalOrderInfo$items$ReferenceBuilder();
		if (map.containsKey("__identifier")) {
			builder.__identifier((java.util.UUID)map.get("__identifier"));
		}
	    return builder.build();
	}

	public String toString() {
	    return "InternationalOrderInfo$items$Reference(identifier=" + this.__identifier + ")";
	}

    // Builder
	public static InternationalOrderInfo$items$ReferenceBuilder internationalOrderInfo$items$ReferenceBuilder() {
		return new InternationalOrderInfo$items$ReferenceBuilder();
	}

	public static class InternationalOrderInfo$items$ReferenceBuilder {
		private java.util.UUID __identifier;

		InternationalOrderInfo$items$ReferenceBuilder() {
		}

		public InternationalOrderInfo$items$ReferenceBuilder __identifier(java.util.UUID __identifier) {
			this.__identifier = __identifier;
			return this;
		}
		public InternationalOrderInfo$items$Reference build() {
			return new InternationalOrderInfo$items$Reference(this.__identifier);
		}

		public String toString() {
    	    return "InternationalOrderInfo$items$ReferenceBuilder.InternationalOrderInfo$items$Reference(identifier=" + __identifier + ")";
		}
	}
}
