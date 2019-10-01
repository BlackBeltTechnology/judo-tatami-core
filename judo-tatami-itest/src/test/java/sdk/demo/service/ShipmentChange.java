package sdk.demo.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class ShipmentChange {
	private java.util.UUID __identifier;
	private String shipperName;
	private java.time.ZonedDateTime orderDate;

    @java.beans.ConstructorProperties({"__identifier", "shipperName", "orderDate"})
    ShipmentChange(java.util.UUID __identifier, String shipperName, java.time.ZonedDateTime orderDate) {
		this.__identifier = __identifier;
        this.shipperName = shipperName;
        this.orderDate = orderDate;
    }

	// Getters
	public java.util.UUID get__identifier() {
		return this.__identifier;
	}
	public String getShipperName() {
		return this.shipperName;
	}
	public java.time.ZonedDateTime getOrderDate() {
		return this.orderDate;
	}

	// Setters
	public void set__identifier(java.util.UUID __identifier) {
		this.__identifier = __identifier;
	}
	public void setShipperName(String shipperName) {
		this.shipperName = shipperName;
	}
	public void setOrderDate(java.time.ZonedDateTime orderDate) {
		this.orderDate = orderDate;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap();
		if (this.__identifier != null) {
			ret.put("__identifier", this.__identifier);
		}

	    if (shipperName != null) {
		    ret.put("shipperName", this.shipperName);
	    }
	    if (orderDate != null) {
		    ret.put("orderDate", this.orderDate);
	    }
		return ret;
	}

	public static ShipmentChange fromMap(Map<String, Object> map) {
		ShipmentChangeBuilder builder = shipmentChangeBuilder();
		if (map.containsKey("__identifier")) {
			builder.__identifier((java.util.UUID)map.get("__identifier"));
		}
	    if (map.containsKey("shipperName")) {
			builder.shipperName((String) map.get("shipperName"));
		}
	    if (map.containsKey("orderDate")) {
			builder.orderDate((java.time.ZonedDateTime) map.get("orderDate"));
		}
	    return builder.build();
	}

	public String toString() {
	    return "ShipmentChange(identifier=" + this.__identifier + ", shipperName=" + this.shipperName + ", orderDate=" + this.orderDate + ")";
	}

    // Builder
	public static ShipmentChangeBuilder shipmentChangeBuilder() {
		return new ShipmentChangeBuilder();
	}

	public static class ShipmentChangeBuilder {
		private java.util.UUID __identifier;
        private String shipperName;
        private java.time.ZonedDateTime orderDate;

		ShipmentChangeBuilder() {
		}

		public ShipmentChangeBuilder __identifier(java.util.UUID __identifier) {
			this.__identifier = __identifier;
			return this;
		}
		public ShipmentChangeBuilder shipperName(String shipperName) {
			this.shipperName = shipperName;
			return this;
		}

		public ShipmentChangeBuilder orderDate(java.time.ZonedDateTime orderDate) {
			this.orderDate = orderDate;
			return this;
		}

		public ShipmentChange build() {
			return new ShipmentChange(this.__identifier, this.shipperName, this.orderDate);
		}

		public String toString() {
    	    return "ShipmentChangeBuilder.ShipmentChange(identifier=" + __identifier + ", shipperName=" + this.shipperName + ", orderDate=" + this.orderDate + ")";
		}
	}
}
