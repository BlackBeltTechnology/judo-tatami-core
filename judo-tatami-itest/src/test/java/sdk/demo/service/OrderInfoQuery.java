package sdk.demo.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OrderInfoQuery {
	private java.util.UUID __identifier;
	private String shipperName;
	private java.time.ZonedDateTime orderDate;
	private List<sdk.demo.service.OrderItemQuery> items;
	private List<sdk.demo.service.CategoryInfo> categories;

    @java.beans.ConstructorProperties({"__identifier", "shipperName", "orderDate", "items", "categories"})
    OrderInfoQuery(java.util.UUID __identifier, String shipperName, java.time.ZonedDateTime orderDate, List<sdk.demo.service.OrderItemQuery> items, List<sdk.demo.service.CategoryInfo> categories) {
		this.__identifier = __identifier;
        this.shipperName = shipperName;
        this.orderDate = orderDate;
        this.items = items;
        this.categories = categories;
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
	public List<sdk.demo.service.OrderItemQuery> getItems() {
		return this.items;
	}
	public List<sdk.demo.service.CategoryInfo> getCategories() {
		return this.categories;
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
	public void setItems(List<sdk.demo.service.OrderItemQuery> items) {
		this.items = items;
	}
	public void setCategories(List<sdk.demo.service.CategoryInfo> categories) {
		this.categories = categories;
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
		return ret;
	}

	public static OrderInfoQuery fromMap(Map<String, Object> map) {
		OrderInfoQueryBuilder builder = orderInfoQueryBuilder();
		if (map.containsKey("__identifier")) {
			builder.__identifier((java.util.UUID)map.get("__identifier"));
		}
	    if (map.containsKey("shipperName")) {
			builder.shipperName((String) map.get("shipperName"));
		}
	    if (map.containsKey("orderDate")) {
			builder.orderDate((java.time.ZonedDateTime) map.get("orderDate"));
		}
	    if (map.containsKey("items")) {
		    builder.items((List<sdk.demo.service.OrderItemQuery>) ((List) map.get("items")).stream().map(i -> sdk.demo.service.OrderItemQuery.fromMap((Map<String, Object>) i)).collect(Collectors.toList()));
	    }
	    if (map.containsKey("categories")) {
		    builder.categories((List<sdk.demo.service.CategoryInfo>) ((List) map.get("categories")).stream().map(i -> sdk.demo.service.CategoryInfo.fromMap((Map<String, Object>) i)).collect(Collectors.toList()));
	    }
	    return builder.build();
	}

	public String toString() {
	    return "OrderInfoQuery(identifier=" + this.__identifier + ", shipperName=" + this.shipperName + ", orderDate=" + this.orderDate + ", items=" + this.items + ", categories=" + this.categories + ")";
	}

    // Builder
	public static OrderInfoQueryBuilder orderInfoQueryBuilder() {
		return new OrderInfoQueryBuilder();
	}

	public static class OrderInfoQueryBuilder {
		private java.util.UUID __identifier;
        private String shipperName;
        private java.time.ZonedDateTime orderDate;
        private List<sdk.demo.service.OrderItemQuery> items;
        private List<sdk.demo.service.CategoryInfo> categories;

		OrderInfoQueryBuilder() {
		}

		public OrderInfoQueryBuilder __identifier(java.util.UUID __identifier) {
			this.__identifier = __identifier;
			return this;
		}
		public OrderInfoQueryBuilder shipperName(String shipperName) {
			this.shipperName = shipperName;
			return this;
		}

		public OrderInfoQueryBuilder orderDate(java.time.ZonedDateTime orderDate) {
			this.orderDate = orderDate;
			return this;
		}

		public OrderInfoQueryBuilder items(List<sdk.demo.service.OrderItemQuery> items) {
			this.items = items;
			return this;
		}

		public OrderInfoQueryBuilder categories(List<sdk.demo.service.CategoryInfo> categories) {
			this.categories = categories;
			return this;
		}

		public OrderInfoQuery build() {
			return new OrderInfoQuery(this.__identifier, this.shipperName, this.orderDate, this.items, this.categories);
		}

		public String toString() {
    	    return "OrderInfoQueryBuilder.OrderInfoQuery(identifier=" + __identifier + ", shipperName=" + this.shipperName + ", orderDate=" + this.orderDate + ", items=" + this.items + ", categories=" + this.categories + ")";
		}
	}
}
