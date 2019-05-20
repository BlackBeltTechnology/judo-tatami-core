package sdk.northwind.services;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OrderItem$product$Reference {
	private java.util.UUID identifier;


    @java.beans.ConstructorProperties({"identifier"})
    OrderItem$product$Reference(java.util.UUID identifier) {
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

	public static OrderItem$product$Reference fromMap(Map<String, Object> map) {
		OrderItem$product$ReferenceBuilder builder = orderItem$product$ReferenceBuilder();
		if (map.containsKey("__identifier")) {
			builder.identifier((java.util.UUID)map.get("identifier"));
		}

	    return builder.build();
	}

	public String toString() {
	    return "OrderItem$product$Reference(identifier=" + this.identifier + ")";
	}

    // Builder
	public static OrderItem$product$ReferenceBuilder orderItem$product$ReferenceBuilder() {
		return new OrderItem$product$ReferenceBuilder();
	}

	public static class OrderItem$product$ReferenceBuilder {
		private java.util.UUID identifier;

		OrderItem$product$ReferenceBuilder() {
		}
		public OrderItem$product$ReferenceBuilder identifier(java.util.UUID identifier) {
			this.identifier = identifier;
			return this;
		}


		public OrderItem$product$Reference build() {
			return new OrderItem$product$Reference(this.identifier);
		}

		public String toString() {
    	    return "OrderItem$product$ReferenceBuilder.OrderItem$product$Reference(identifier=" + identifier + ")";
		}
	}
}