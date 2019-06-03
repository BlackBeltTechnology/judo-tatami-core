package sdk.demo.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OrderInfo$shipper$Reference {
	private java.util.UUID __identifier;


    @java.beans.ConstructorProperties({"__identifier"})
    OrderInfo$shipper$Reference(java.util.UUID __identifier) {
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

	public static OrderInfo$shipper$Reference fromMap(Map<String, Object> map) {
		OrderInfo$shipper$ReferenceBuilder builder = orderInfo$shipper$ReferenceBuilder();
		if (map.containsKey("__identifier")) {
			builder.__identifier((java.util.UUID)map.get("__identifier"));
		}

	    return builder.build();
	}

	public String toString() {
	    return "OrderInfo$shipper$Reference(identifier=" + this.__identifier + ")";
	}

    // Builder
	public static OrderInfo$shipper$ReferenceBuilder orderInfo$shipper$ReferenceBuilder() {
		return new OrderInfo$shipper$ReferenceBuilder();
	}

	public static class OrderInfo$shipper$ReferenceBuilder {
		private java.util.UUID __identifier;

		OrderInfo$shipper$ReferenceBuilder() {
		}
		public OrderInfo$shipper$ReferenceBuilder __identifier(java.util.UUID __identifier) {
			this.__identifier = __identifier;
			return this;
		}


		public OrderInfo$shipper$Reference build() {
			return new OrderInfo$shipper$Reference(this.__identifier);
		}

		public String toString() {
    	    return "OrderInfo$shipper$ReferenceBuilder.OrderInfo$shipper$Reference(identifier=" + __identifier + ")";
		}
	}
}