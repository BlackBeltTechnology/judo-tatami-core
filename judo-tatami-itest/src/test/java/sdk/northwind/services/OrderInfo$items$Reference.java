package sdk.northwind.services;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OrderInfo$items$Reference {
	private java.util.UUID __identifier;


    @java.beans.ConstructorProperties({"__identifier"})
    OrderInfo$items$Reference(java.util.UUID __identifier) {
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

	public static OrderInfo$items$Reference fromMap(Map<String, Object> map) {
		OrderInfo$items$ReferenceBuilder builder = orderInfo$items$ReferenceBuilder();
		if (map.containsKey("__identifier")) {
			builder.__identifier((java.util.UUID)map.get("__identifier"));
		}

	    return builder.build();
	}

	public String toString() {
	    return "OrderInfo$items$Reference(identifier=" + this.__identifier + ")";
	}

    // Builder
	public static OrderInfo$items$ReferenceBuilder orderInfo$items$ReferenceBuilder() {
		return new OrderInfo$items$ReferenceBuilder();
	}

	public static class OrderInfo$items$ReferenceBuilder {
		private java.util.UUID __identifier;

		OrderInfo$items$ReferenceBuilder() {
		}
		public OrderInfo$items$ReferenceBuilder __identifier(java.util.UUID __identifier) {
			this.__identifier = __identifier;
			return this;
		}


		public OrderInfo$items$Reference build() {
			return new OrderInfo$items$Reference(this.__identifier);
		}

		public String toString() {
    	    return "OrderInfo$items$ReferenceBuilder.OrderInfo$items$Reference(identifier=" + __identifier + ")";
		}
	}
}