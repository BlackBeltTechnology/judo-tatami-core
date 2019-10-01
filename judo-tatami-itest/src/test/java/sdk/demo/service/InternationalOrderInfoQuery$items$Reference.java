package sdk.demo.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class InternationalOrderInfoQuery$items$Reference {
	private java.util.UUID __identifier;

    @java.beans.ConstructorProperties({"__identifier"})
    InternationalOrderInfoQuery$items$Reference(java.util.UUID __identifier) {
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

	public static InternationalOrderInfoQuery$items$Reference fromMap(Map<String, Object> map) {
		InternationalOrderInfoQuery$items$ReferenceBuilder builder = internationalOrderInfoQuery$items$ReferenceBuilder();
		if (map.containsKey("__identifier")) {
			builder.__identifier((java.util.UUID)map.get("__identifier"));
		}
	    return builder.build();
	}

	public String toString() {
	    return "InternationalOrderInfoQuery$items$Reference(identifier=" + this.__identifier + ")";
	}

    // Builder
	public static InternationalOrderInfoQuery$items$ReferenceBuilder internationalOrderInfoQuery$items$ReferenceBuilder() {
		return new InternationalOrderInfoQuery$items$ReferenceBuilder();
	}

	public static class InternationalOrderInfoQuery$items$ReferenceBuilder {
		private java.util.UUID __identifier;

		InternationalOrderInfoQuery$items$ReferenceBuilder() {
		}

		public InternationalOrderInfoQuery$items$ReferenceBuilder __identifier(java.util.UUID __identifier) {
			this.__identifier = __identifier;
			return this;
		}
		public InternationalOrderInfoQuery$items$Reference build() {
			return new InternationalOrderInfoQuery$items$Reference(this.__identifier);
		}

		public String toString() {
    	    return "InternationalOrderInfoQuery$items$ReferenceBuilder.InternationalOrderInfoQuery$items$Reference(identifier=" + __identifier + ")";
		}
	}
}
