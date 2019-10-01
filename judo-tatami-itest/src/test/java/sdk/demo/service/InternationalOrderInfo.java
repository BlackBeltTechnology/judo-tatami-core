package sdk.demo.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class InternationalOrderInfo {
	private java.util.UUID __identifier;
	private String shipperName;
	private String customsDescription;
	private Double exciseTax;
	private java.time.ZonedDateTime orderDate;
	private List<sdk.demo.service.OrderItem> items;
	private sdk.demo.service.ShipperInfo shipper;

    @java.beans.ConstructorProperties({"__identifier", "shipperName", "customsDescription", "exciseTax", "orderDate", "items", "shipper"})
    InternationalOrderInfo(java.util.UUID __identifier, String shipperName, String customsDescription, Double exciseTax, java.time.ZonedDateTime orderDate, List<sdk.demo.service.OrderItem> items, sdk.demo.service.ShipperInfo shipper) {
		this.__identifier = __identifier;
        this.shipperName = shipperName;
        this.customsDescription = customsDescription;
        this.exciseTax = exciseTax;
        this.orderDate = orderDate;
        this.items = items;
        this.shipper = shipper;
    }

	// Getters
	public java.util.UUID get__identifier() {
		return this.__identifier;
	}
	public String getShipperName() {
		return this.shipperName;
	}
	public String getCustomsDescription() {
		return this.customsDescription;
	}
	public Double getExciseTax() {
		return this.exciseTax;
	}
	public java.time.ZonedDateTime getOrderDate() {
		return this.orderDate;
	}
	public List<sdk.demo.service.OrderItem> getItems() {
		return this.items;
	}
	public sdk.demo.service.ShipperInfo getShipper() {
		return this.shipper;
	}

	// Setters
	public void set__identifier(java.util.UUID __identifier) {
		this.__identifier = __identifier;
	}
	public void setShipperName(String shipperName) {
		this.shipperName = shipperName;
	}
	public void setCustomsDescription(String customsDescription) {
		this.customsDescription = customsDescription;
	}
	public void setExciseTax(Double exciseTax) {
		this.exciseTax = exciseTax;
	}
	public void setOrderDate(java.time.ZonedDateTime orderDate) {
		this.orderDate = orderDate;
	}
	public void setItems(List<sdk.demo.service.OrderItem> items) {
		this.items = items;
	}
	public void setShipper(sdk.demo.service.ShipperInfo shipper) {
		this.shipper = shipper;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap();
		if (this.__identifier != null) {
			ret.put("__identifier", this.__identifier);
		}

	    if (shipperName != null) {
		    ret.put("shipperName", this.shipperName);
	    }
	    if (customsDescription != null) {
		    ret.put("customsDescription", this.customsDescription);
	    }
	    if (exciseTax != null) {
		    ret.put("exciseTax", this.exciseTax);
	    }
	    if (orderDate != null) {
		    ret.put("orderDate", this.orderDate);
	    }
	    if (items != null) {
		    ret.put("items", this.items.stream().map(i -> i.toMap()).collect(Collectors.toList()));
		}
	    if (shipper != null) {
		    ret.put("shipper", this.shipper.toMap());
		}
		return ret;
	}

	public static InternationalOrderInfo fromMap(Map<String, Object> map) {
		InternationalOrderInfoBuilder builder = internationalOrderInfoBuilder();
		if (map.containsKey("__identifier")) {
			builder.__identifier((java.util.UUID)map.get("__identifier"));
		}
	    if (map.containsKey("shipperName")) {
			builder.shipperName((String) map.get("shipperName"));
		}
	    if (map.containsKey("customsDescription")) {
			builder.customsDescription((String) map.get("customsDescription"));
		}
	    if (map.containsKey("exciseTax")) {
			builder.exciseTax((Double) map.get("exciseTax"));
		}
	    if (map.containsKey("orderDate")) {
			builder.orderDate((java.time.ZonedDateTime) map.get("orderDate"));
		}
	    if (map.containsKey("items")) {
		    builder.items((List<sdk.demo.service.OrderItem>) ((List) map.get("items")).stream().map(i -> sdk.demo.service.OrderItem.fromMap((Map<String, Object>) i)).collect(Collectors.toList()));
	    }
	    if (map.containsKey("shipper")) {
		    builder.shipper(sdk.demo.service.ShipperInfo.fromMap((Map<String, Object>) map.get("shipper")));
	    }
	    return builder.build();
	}

	public String toString() {
	    return "InternationalOrderInfo(identifier=" + this.__identifier + ", shipperName=" + this.shipperName + ", customsDescription=" + this.customsDescription + ", exciseTax=" + this.exciseTax + ", orderDate=" + this.orderDate + ", items=" + this.items + ", shipper=" + this.shipper + ")";
	}

    // Builder
	public static InternationalOrderInfoBuilder internationalOrderInfoBuilder() {
		return new InternationalOrderInfoBuilder();
	}

	public static class InternationalOrderInfoBuilder {
		private java.util.UUID __identifier;
        private String shipperName;
        private String customsDescription;
        private Double exciseTax;
        private java.time.ZonedDateTime orderDate;
        private List<sdk.demo.service.OrderItem> items;
        private sdk.demo.service.ShipperInfo shipper;

		InternationalOrderInfoBuilder() {
		}

		public InternationalOrderInfoBuilder __identifier(java.util.UUID __identifier) {
			this.__identifier = __identifier;
			return this;
		}
		public InternationalOrderInfoBuilder shipperName(String shipperName) {
			this.shipperName = shipperName;
			return this;
		}

		public InternationalOrderInfoBuilder customsDescription(String customsDescription) {
			this.customsDescription = customsDescription;
			return this;
		}

		public InternationalOrderInfoBuilder exciseTax(Double exciseTax) {
			this.exciseTax = exciseTax;
			return this;
		}

		public InternationalOrderInfoBuilder orderDate(java.time.ZonedDateTime orderDate) {
			this.orderDate = orderDate;
			return this;
		}

		public InternationalOrderInfoBuilder items(List<sdk.demo.service.OrderItem> items) {
			this.items = items;
			return this;
		}

		public InternationalOrderInfoBuilder shipper(sdk.demo.service.ShipperInfo shipper) {
			this.shipper = shipper;
			return this;
		}

		public InternationalOrderInfo build() {
			return new InternationalOrderInfo(this.__identifier, this.shipperName, this.customsDescription, this.exciseTax, this.orderDate, this.items, this.shipper);
		}

		public String toString() {
    	    return "InternationalOrderInfoBuilder.InternationalOrderInfo(identifier=" + __identifier + ", shipperName=" + this.shipperName + ", customsDescription=" + this.customsDescription + ", exciseTax=" + this.exciseTax + ", orderDate=" + this.orderDate + ", items=" + this.items + ", shipper=" + this.shipper + ")";
		}
	}
}
