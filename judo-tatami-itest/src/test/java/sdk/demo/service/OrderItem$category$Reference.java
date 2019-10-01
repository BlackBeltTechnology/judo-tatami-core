package sdk.demo.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OrderItem$category$Reference {
	private java.util.UUID __identifier;

    @java.beans.ConstructorProperties({"__identifier"})
    OrderItem$category$Reference(java.util.UUID __identifier) {
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

	public static OrderItem$category$Reference fromMap(Map<String, Object> map) {
		OrderItem$category$ReferenceBuilder builder = orderItem$category$ReferenceBuilder();
		if (map.containsKey("__identifier")) {
			builder.__identifier((java.util.UUID)map.get("__identifier"));
		}
	    return builder.build();
	}

	public String toString() {
	    return "OrderItem$category$Reference(identifier=" + this.__identifier + ")";
	}

    // Builder
	public static OrderItem$category$ReferenceBuilder orderItem$category$ReferenceBuilder() {
		return new OrderItem$category$ReferenceBuilder();
	}

	public static class OrderItem$category$ReferenceBuilder {
		private java.util.UUID __identifier;

		OrderItem$category$ReferenceBuilder() {
		}

		public OrderItem$category$ReferenceBuilder __identifier(java.util.UUID __identifier) {
			this.__identifier = __identifier;
			return this;
		}
		public OrderItem$category$Reference build() {
			return new OrderItem$category$Reference(this.__identifier);
		}

		public String toString() {
    	    return "OrderItem$category$ReferenceBuilder.OrderItem$category$Reference(identifier=" + __identifier + ")";
		}
	}
}
