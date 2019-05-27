package sdk.demo.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OrderItem$product$Reference {
	private java.util.UUID __identifier;


    @java.beans.ConstructorProperties({"__identifier"})
    OrderItem$product$Reference(java.util.UUID __identifier) {
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

	public static OrderItem$product$Reference fromMap(Map<String, Object> map) {
		OrderItem$product$ReferenceBuilder builder = orderItem$product$ReferenceBuilder();
		if (map.containsKey("__identifier")) {
			builder.__identifier((java.util.UUID)map.get("__identifier"));
		}

	    return builder.build();
	}

	public String toString() {
	    return "OrderItem$product$Reference(identifier=" + this.__identifier + ")";
	}

    // Builder
	public static OrderItem$product$ReferenceBuilder orderItem$product$ReferenceBuilder() {
		return new OrderItem$product$ReferenceBuilder();
	}

	public static class OrderItem$product$ReferenceBuilder {
		private java.util.UUID __identifier;

		OrderItem$product$ReferenceBuilder() {
		}
		public OrderItem$product$ReferenceBuilder __identifier(java.util.UUID __identifier) {
			this.__identifier = __identifier;
			return this;
		}


		public OrderItem$product$Reference build() {
			return new OrderItem$product$Reference(this.__identifier);
		}

		public String toString() {
    	    return "OrderItem$product$ReferenceBuilder.OrderItem$product$Reference(identifier=" + __identifier + ")";
		}
	}
}