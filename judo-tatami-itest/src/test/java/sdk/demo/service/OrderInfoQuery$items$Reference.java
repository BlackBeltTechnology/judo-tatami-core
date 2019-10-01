package sdk.demo.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OrderInfoQuery$items$Reference {
	private java.util.UUID __identifier;

    @java.beans.ConstructorProperties({"__identifier"})
    OrderInfoQuery$items$Reference(java.util.UUID __identifier) {
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

	public static OrderInfoQuery$items$Reference fromMap(Map<String, Object> map) {
		OrderInfoQuery$items$ReferenceBuilder builder = orderInfoQuery$items$ReferenceBuilder();
		if (map.containsKey("__identifier")) {
			builder.__identifier((java.util.UUID)map.get("__identifier"));
		}
	    return builder.build();
	}

	public String toString() {
	    return "OrderInfoQuery$items$Reference(identifier=" + this.__identifier + ")";
	}

    // Builder
	public static OrderInfoQuery$items$ReferenceBuilder orderInfoQuery$items$ReferenceBuilder() {
		return new OrderInfoQuery$items$ReferenceBuilder();
	}

	public static class OrderInfoQuery$items$ReferenceBuilder {
		private java.util.UUID __identifier;

		OrderInfoQuery$items$ReferenceBuilder() {
		}

		public OrderInfoQuery$items$ReferenceBuilder __identifier(java.util.UUID __identifier) {
			this.__identifier = __identifier;
			return this;
		}
		public OrderInfoQuery$items$Reference build() {
			return new OrderInfoQuery$items$Reference(this.__identifier);
		}

		public String toString() {
    	    return "OrderInfoQuery$items$ReferenceBuilder.OrderInfoQuery$items$Reference(identifier=" + __identifier + ")";
		}
	}
}
