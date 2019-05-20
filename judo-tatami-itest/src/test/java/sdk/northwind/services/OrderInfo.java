package sdk.northwind.services;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OrderInfo {
	private java.util.UUID identifier;

	private java.time.ZonedDateTime orderDate;
	private java.lang.String shipperName;
	private List<sdk.northwind.services.OrderItem> items;
	private List<sdk.northwind.services.CategoryInfo> categories;

    @java.beans.ConstructorProperties({"identifier", "orderDate", "shipperName", "items", "categories"})
    OrderInfo(java.util.UUID identifier, java.time.ZonedDateTime orderDate, java.lang.String shipperName, List<sdk.northwind.services.OrderItem> items, List<sdk.northwind.services.CategoryInfo> categories) {
        this.orderDate = orderDate;
        this.shipperName = shipperName;
        this.items = items;
        this.categories = categories;
    }

	// Getters
	public java.util.UUID getIdentifier() {
		return this.identifier;
	}

	public java.time.ZonedDateTime getOrderDate() {
		return this.orderDate;
	}
	public java.lang.String getShipperName() {
		return this.shipperName;
	}
	public List<sdk.northwind.services.OrderItem> getItems() {
		return this.items;
	}
	public List<sdk.northwind.services.CategoryInfo> getCategories() {
		return this.categories;
	}

	// Setters
	public void setIdentifier(java.util.UUID identifier) {
		this.identifier = identifier;
	}
	public void setOrderDate(java.time.ZonedDateTime orderDate) {
		this.orderDate = orderDate;
	}
	public void setShipperName(java.lang.String shipperName) {
		this.shipperName = shipperName;
	}
	public void setItems(List<sdk.northwind.services.OrderItem> items) {
		this.items = items;
	}
	public void setCategories(List<sdk.northwind.services.CategoryInfo> categories) {
		this.categories = categories;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap();
		if (getIdentifier() != null) {
			ret.put("__identifier", this.identifier);
		}

	    if (getOrderDate() != null) {
		    ret.put("orderDate", this.orderDate);
	    }
	    if (getShipperName() != null) {
		    ret.put("shipperName", this.shipperName);
	    }
	    if (getItems() != null) {
		    ret.put("items", getItems().stream().map(i -> i.toMap()).collect(Collectors.toList()));
		}
	    if (getCategories() != null) {
		    ret.put("categories", getCategories().stream().map(i -> i.toMap()).collect(Collectors.toList()));
		}
		return ret;
	}

	public static OrderInfo fromMap(Map<String, Object> map) {
		OrderInfoBuilder builder = orderInfoBuilder();
		if (map.containsKey("__identifier")) {
			builder.identifier((java.util.UUID)map.get("identifier"));
		}
	    if (map.containsKey("orderDate")) {
			builder.orderDate((java.time.ZonedDateTime) map.get("orderDate"));
		}
	    if (map.containsKey("shipperName")) {
			builder.shipperName((java.lang.String) map.get("shipperName"));
		}
	    if (map.containsKey("items")) {
		    builder.items((List<sdk.northwind.services.OrderItem>) ((List) map.get("items")).stream().map(i -> sdk.northwind.services.OrderItem.fromMap((Map<String, Object>) i)).collect(Collectors.toList()));
	    }
	    if (map.containsKey("categories")) {
		    builder.categories((List<sdk.northwind.services.CategoryInfo>) ((List) map.get("categories")).stream().map(i -> sdk.northwind.services.CategoryInfo.fromMap((Map<String, Object>) i)).collect(Collectors.toList()));
	    }

	    return builder.build();
	}

	public String toString() {
	    return "OrderInfo(identifier=" + this.identifier + ", orderDate=" + this.orderDate + ", shipperName=" + this.shipperName + ", items=" + this.items + ", categories=" + this.categories + ")";
	}

    // Builder
	public static OrderInfoBuilder orderInfoBuilder() {
		return new OrderInfoBuilder();
	}

	public static class OrderInfoBuilder {
		private java.util.UUID identifier;
        private java.time.ZonedDateTime orderDate;
        private java.lang.String shipperName;
        private List<sdk.northwind.services.OrderItem> items;
        private List<sdk.northwind.services.CategoryInfo> categories;

		OrderInfoBuilder() {
		}
		public OrderInfoBuilder identifier(java.util.UUID identifier) {
			this.identifier = identifier;
			return this;
		}

		public OrderInfoBuilder orderDate(java.time.ZonedDateTime orderDate) {
			this.orderDate = orderDate;
			return this;
		}
		public OrderInfoBuilder shipperName(java.lang.String shipperName) {
			this.shipperName = shipperName;
			return this;
		}
		public OrderInfoBuilder items(List<sdk.northwind.services.OrderItem> items) {
			this.items = items;
			return this;
		}
		public OrderInfoBuilder categories(List<sdk.northwind.services.CategoryInfo> categories) {
			this.categories = categories;
			return this;
		}

		public OrderInfo build() {
			return new OrderInfo(this.identifier, this.orderDate, this.shipperName, this.items, this.categories);
		}

		public String toString() {
    	    return "OrderInfoBuilder.OrderInfo(identifier=" + identifier + ", orderDate=" + this.orderDate + ", shipperName=" + this.shipperName + ", items=" + this.items + ", categories=" + this.categories + ")";
		}
	}
}