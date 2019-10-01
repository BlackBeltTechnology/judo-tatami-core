package sdk.demo.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class OrderInfo {
	private java.util.UUID __identifier;
	private String shipperName;
	private java.time.ZonedDateTime orderDate;
	private List<sdk.demo.service.OrderItem> items;
	private sdk.demo.service.ShipperInfo shipper;
	private List<sdk.demo.service.CategoryInfo> categories;
	private List<sdk.demo.service.Comment> comments;

    @java.beans.ConstructorProperties({"__identifier", "shipperName", "orderDate", "items", "shipper", "categories", "comments"})
    OrderInfo(java.util.UUID __identifier, String shipperName, java.time.ZonedDateTime orderDate, List<sdk.demo.service.OrderItem> items, sdk.demo.service.ShipperInfo shipper, List<sdk.demo.service.CategoryInfo> categories, List<sdk.demo.service.Comment> comments) {
		this.__identifier = __identifier;
        this.shipperName = shipperName;
        this.orderDate = orderDate;
        this.items = items;
        this.shipper = shipper;
        this.categories = categories;
        this.comments = comments;
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
	public List<sdk.demo.service.OrderItem> getItems() {
		return this.items;
	}
	public sdk.demo.service.ShipperInfo getShipper() {
		return this.shipper;
	}
	public List<sdk.demo.service.CategoryInfo> getCategories() {
		return this.categories;
	}
	public List<sdk.demo.service.Comment> getComments() {
		return this.comments;
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
	public void setItems(List<sdk.demo.service.OrderItem> items) {
		this.items = items;
	}
	public void setShipper(sdk.demo.service.ShipperInfo shipper) {
		this.shipper = shipper;
	}
	public void setCategories(List<sdk.demo.service.CategoryInfo> categories) {
		this.categories = categories;
	}
	public void setComments(List<sdk.demo.service.Comment> comments) {
		this.comments = comments;
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
	    if (shipper != null) {
		    ret.put("shipper", this.shipper.toMap());
		}
	    if (categories != null) {
		    ret.put("categories", this.categories.stream().map(i -> i.toMap()).collect(Collectors.toList()));
		}
	    if (comments != null) {
		    ret.put("comments", this.comments.stream().map(i -> i.toMap()).collect(Collectors.toList()));
		}
		return ret;
	}

	public static OrderInfo fromMap(Map<String, Object> map) {
		OrderInfoBuilder builder = orderInfoBuilder();
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
		    builder.items((List<sdk.demo.service.OrderItem>) ((List) map.get("items")).stream().map(i -> sdk.demo.service.OrderItem.fromMap((Map<String, Object>) i)).collect(Collectors.toList()));
	    }
	    if (map.containsKey("shipper")) {
		    builder.shipper(sdk.demo.service.ShipperInfo.fromMap((Map<String, Object>) map.get("shipper")));
	    }
	    if (map.containsKey("categories")) {
		    builder.categories((List<sdk.demo.service.CategoryInfo>) ((List) map.get("categories")).stream().map(i -> sdk.demo.service.CategoryInfo.fromMap((Map<String, Object>) i)).collect(Collectors.toList()));
	    }
	    if (map.containsKey("comments")) {
		    builder.comments((List<sdk.demo.service.Comment>) ((List) map.get("comments")).stream().map(i -> sdk.demo.service.Comment.fromMap((Map<String, Object>) i)).collect(Collectors.toList()));
	    }
	    return builder.build();
	}

	public String toString() {
	    return "OrderInfo(identifier=" + this.__identifier + ", shipperName=" + this.shipperName + ", orderDate=" + this.orderDate + ", items=" + this.items + ", shipper=" + this.shipper + ", categories=" + this.categories + ", comments=" + this.comments + ")";
	}

    // Builder
	public static OrderInfoBuilder orderInfoBuilder() {
		return new OrderInfoBuilder();
	}

	public static class OrderInfoBuilder {
		private java.util.UUID __identifier;
        private String shipperName;
        private java.time.ZonedDateTime orderDate;
        private List<sdk.demo.service.OrderItem> items;
        private sdk.demo.service.ShipperInfo shipper;
        private List<sdk.demo.service.CategoryInfo> categories;
        private List<sdk.demo.service.Comment> comments;

		OrderInfoBuilder() {
		}

		public OrderInfoBuilder __identifier(java.util.UUID __identifier) {
			this.__identifier = __identifier;
			return this;
		}
		public OrderInfoBuilder shipperName(String shipperName) {
			this.shipperName = shipperName;
			return this;
		}

		public OrderInfoBuilder orderDate(java.time.ZonedDateTime orderDate) {
			this.orderDate = orderDate;
			return this;
		}

		public OrderInfoBuilder items(List<sdk.demo.service.OrderItem> items) {
			this.items = items;
			return this;
		}

		public OrderInfoBuilder shipper(sdk.demo.service.ShipperInfo shipper) {
			this.shipper = shipper;
			return this;
		}

		public OrderInfoBuilder categories(List<sdk.demo.service.CategoryInfo> categories) {
			this.categories = categories;
			return this;
		}

		public OrderInfoBuilder comments(List<sdk.demo.service.Comment> comments) {
			this.comments = comments;
			return this;
		}

		public OrderInfo build() {
			return new OrderInfo(this.__identifier, this.shipperName, this.orderDate, this.items, this.shipper, this.categories, this.comments);
		}

		public String toString() {
    	    return "OrderInfoBuilder.OrderInfo(identifier=" + __identifier + ", shipperName=" + this.shipperName + ", orderDate=" + this.orderDate + ", items=" + this.items + ", shipper=" + this.shipper + ", categories=" + this.categories + ", comments=" + this.comments + ")";
		}
	}
}
