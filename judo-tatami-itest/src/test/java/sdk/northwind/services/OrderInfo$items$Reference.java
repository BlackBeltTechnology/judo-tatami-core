package sdk.northwind.services;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OrderInfo$items$Reference {
	private java.util.UUID identifier;


    @java.beans.ConstructorProperties({"identifier"})
    OrderInfo$items$Reference(java.util.UUID identifier) {
    }

	// Getters
	public java.util.UUID getIdentifier() {
		return this.identifier;
	}


	// Setters
	public void setIdentifier(java.util.UUID identifier) {
		this.identifier = identifier;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap();
		if (getIdentifier() != null) {
			ret.put("__identifier", this.identifier);
		}

		return ret;
	}

	public static OrderInfo$items$Reference fromMap(Map<String, Object> map) {
		OrderInfo$items$ReferenceBuilder builder = orderInfo$items$ReferenceBuilder();
		if (map.containsKey("__identifier")) {
			builder.identifier((java.util.UUID)map.get("identifier"));
		}

	    return builder.build();
	}

	public String toString() {
	    return "OrderInfo$items$Reference(identifier=" + this.identifier + ")";
	}

    // Builder
	public static OrderInfo$items$ReferenceBuilder orderInfo$items$ReferenceBuilder() {
		return new OrderInfo$items$ReferenceBuilder();
	}

	public static class OrderInfo$items$ReferenceBuilder {
		private java.util.UUID identifier;

		OrderInfo$items$ReferenceBuilder() {
		}
		public OrderInfo$items$ReferenceBuilder identifier(java.util.UUID identifier) {
			this.identifier = identifier;
			return this;
		}


		public OrderInfo$items$Reference build() {
			return new OrderInfo$items$Reference(this.identifier);
		}

		public String toString() {
    	    return "OrderInfo$items$ReferenceBuilder.OrderInfo$items$Reference(identifier=" + identifier + ")";
		}
	}
}