package sdk.demo.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OrderInfoQuery {
	private java.util.UUID __identifier;

	private java.lang.String shipperName;
	private java.time.ZonedDateTime orderDate;
	private List<sdk.demo.service.OrdermItemQuery> items;
	private List<sdk.demo.service.CategoryInfo> categories;
	private sdk.demo.service.ShipperInfo shipper;

    @java.beans.ConstructorProperties({"__identifier", "shipperName", "orderDate", "items", "categories", "shipper"})
    OrderInfoQuery(java.util.UUID __identifier, java.lang.String shipperName, java.time.ZonedDateTime orderDate, List<sdk.demo.service.OrdermItemQuery> items, List<sdk.demo.service.CategoryInfo> categories, sdk.demo.service.ShipperInfo shipper) {
		this.__identifier = __identifier;
        this.shipperName = shipperName;
        this.orderDate = orderDate;
        this.items = items;
        this.categories = categories;
        this.shipper = shipper;
    }

	// Getters
	public java.util.UUID get__identifier() {
		return this.__identifier;
	}

	public java.lang.String getShipperName() {
		return this.shipperName;
	}
	public java.time.ZonedDateTime getOrderDate() {
		return this.orderDate;
	}
	public List<sdk.demo.service.OrdermItemQuery> getItems() {
		return this.items;
	}
	public List<sdk.demo.service.CategoryInfo> getCategories() {
		return this.categories;
	}
	public sdk.demo.service.ShipperInfo getShipper() {
		return this.shipper;
	}

	// Setters
	public void set__identifier(java.util.UUID __identifier) {
		this.__identifier = __identifier;
	}

	public void setShipperName(java.lang.String shipperName) {
		this.shipperName = shipperName;
	}
	public void setOrderDate(java.time.ZonedDateTime orderDate) {
		this.orderDate = orderDate;
	}
	public void setItems(List<sdk.demo.service.OrdermItemQuery> items) {
		this.items = items;
	}
	public void setCategories(List<sdk.demo.service.CategoryInfo> categories) {
		this.categories = categories;
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
	    if (orderDate != null) {
		    ret.put("orderDate", this.orderDate);
	    }
	    if (items != null) {
		    ret.put("items", this.items.stream().map(i -> i.toMap()).collect(Collectors.toList()));
		}
	    if (categories != null) {
		    ret.put("categories", this.categories.stream().map(i -> i.toMap()).collect(Collectors.toList()));
		}
	    if (shipper != null) {
		    ret.put("shipper", this.shipper.toMap());
		}
		return ret;
	}

	public static OrderInfoQuery fromMap(Map<String, Object> map) {
		OrderInfoQueryBuilder builder = orderInfoQueryBuilder();
		if (map.containsKey("__identifier")) {
			builder.__identifier((java.util.UUID)map.get("__identifier"));
		}
	    if (map.containsKey("shipperName")) {
			builder.shipperName((java.lang.String) map.get("shipperName"));
		}
	    if (map.containsKey("orderDate")) {
			builder.orderDate((java.time.ZonedDateTime) map.get("orderDate"));
		}
	    if (map.containsKey("items")) {
		    builder.items((List<sdk.demo.service.OrdermItemQuery>) ((List) map.get("items")).stream().map(i -> sdk.demo.service.OrdermItemQuery.fromMap((Map<String, Object>) i)).collect(Collectors.toList()));
	    }
	    if (map.containsKey("categories")) {
		    builder.categories((List<sdk.demo.service.CategoryInfo>) ((List) map.get("categories")).stream().map(i -> sdk.demo.service.CategoryInfo.fromMap((Map<String, Object>) i)).collect(Collectors.toList()));
	    }
	    if (map.containsKey("shipper")) {
		    builder.shipper(sdk.demo.service.ShipperInfo.fromMap((Map<String, Object>) map.get("shipper")));
	    }

	    return builder.build();
	}

	public String toString() {
	    return "OrderInfoQuery(identifier=" + this.__identifier + ", shipperName=" + this.shipperName + ", orderDate=" + this.orderDate + ", items=" + this.items + ", categories=" + this.categories + ", shipper=" + this.shipper + ")";
	}

    // Builder
	public static OrderInfoQueryBuilder orderInfoQueryBuilder() {
		return new OrderInfoQueryBuilder();
	}

	public static class OrderInfoQueryBuilder {
		private java.util.UUID __identifier;
        private java.lang.String shipperName;
        private java.time.ZonedDateTime orderDate;
        private List<sdk.demo.service.OrdermItemQuery> items;
        private List<sdk.demo.service.CategoryInfo> categories;
        private sdk.demo.service.ShipperInfo shipper;

		OrderInfoQueryBuilder() {
		}
		public OrderInfoQueryBuilder __identifier(java.util.UUID __identifier) {
			this.__identifier = __identifier;
			return this;
		}

		public OrderInfoQueryBuilder shipperName(java.lang.String shipperName) {
			this.shipperName = shipperName;
			return this;
		}
		public OrderInfoQueryBuilder orderDate(java.time.ZonedDateTime orderDate) {
			this.orderDate = orderDate;
			return this;
		}
		public OrderInfoQueryBuilder items(List<sdk.demo.service.OrdermItemQuery> items) {
			this.items = items;
			return this;
		}
		public OrderInfoQueryBuilder categories(List<sdk.demo.service.CategoryInfo> categories) {
			this.categories = categories;
			return this;
		}
		public OrderInfoQueryBuilder shipper(sdk.demo.service.ShipperInfo shipper) {
			this.shipper = shipper;
			return this;
		}

		public OrderInfoQuery build() {
			return new OrderInfoQuery(this.__identifier, this.shipperName, this.orderDate, this.items, this.categories, this.shipper);
		}

		public String toString() {
    	    return "OrderInfoQueryBuilder.OrderInfoQuery(identifier=" + __identifier + ", shipperName=" + this.shipperName + ", orderDate=" + this.orderDate + ", items=" + this.items + ", categories=" + this.categories + ", shipper=" + this.shipper + ")";
		}
	}
}