package sdk.demo.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OrderItemQuery$category$Reference {
	private java.util.UUID __identifier;

    @java.beans.ConstructorProperties({"__identifier"})
    OrderItemQuery$category$Reference(java.util.UUID __identifier) {
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

	public static OrderItemQuery$category$Reference fromMap(Map<String, Object> map) {
		OrderItemQuery$category$ReferenceBuilder builder = orderItemQuery$category$ReferenceBuilder();
		if (map.containsKey("__identifier")) {
			builder.__identifier((java.util.UUID)map.get("__identifier"));
		}
	    return builder.build();
	}

	public String toString() {
	    return "OrderItemQuery$category$Reference(identifier=" + this.__identifier + ")";
	}

    // Builder
	public static OrderItemQuery$category$ReferenceBuilder orderItemQuery$category$ReferenceBuilder() {
		return new OrderItemQuery$category$ReferenceBuilder();
	}

	public static class OrderItemQuery$category$ReferenceBuilder {
		private java.util.UUID __identifier;

		OrderItemQuery$category$ReferenceBuilder() {
		}

		public OrderItemQuery$category$ReferenceBuilder __identifier(java.util.UUID __identifier) {
			this.__identifier = __identifier;
			return this;
		}
		public OrderItemQuery$category$Reference build() {
			return new OrderItemQuery$category$Reference(this.__identifier);
		}

		public String toString() {
    	    return "OrderItemQuery$category$ReferenceBuilder.OrderItemQuery$category$Reference(identifier=" + __identifier + ")";
		}
	}
}
