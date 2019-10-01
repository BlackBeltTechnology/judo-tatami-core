package sdk.demo.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OrderItemQuery$product$Reference {
	private java.util.UUID __identifier;

    @java.beans.ConstructorProperties({"__identifier"})
    OrderItemQuery$product$Reference(java.util.UUID __identifier) {
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

	public static OrderItemQuery$product$Reference fromMap(Map<String, Object> map) {
		OrderItemQuery$product$ReferenceBuilder builder = orderItemQuery$product$ReferenceBuilder();
		if (map.containsKey("__identifier")) {
			builder.__identifier((java.util.UUID)map.get("__identifier"));
		}
	    return builder.build();
	}

	public String toString() {
	    return "OrderItemQuery$product$Reference(identifier=" + this.__identifier + ")";
	}

    // Builder
	public static OrderItemQuery$product$ReferenceBuilder orderItemQuery$product$ReferenceBuilder() {
		return new OrderItemQuery$product$ReferenceBuilder();
	}

	public static class OrderItemQuery$product$ReferenceBuilder {
		private java.util.UUID __identifier;

		OrderItemQuery$product$ReferenceBuilder() {
		}

		public OrderItemQuery$product$ReferenceBuilder __identifier(java.util.UUID __identifier) {
			this.__identifier = __identifier;
			return this;
		}
		public OrderItemQuery$product$Reference build() {
			return new OrderItemQuery$product$Reference(this.__identifier);
		}

		public String toString() {
    	    return "OrderItemQuery$product$ReferenceBuilder.OrderItemQuery$product$Reference(identifier=" + __identifier + ")";
		}
	}
}
