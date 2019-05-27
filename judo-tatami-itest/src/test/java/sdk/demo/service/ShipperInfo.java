package sdk.demo.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class ShipperInfo {
	private java.util.UUID __identifier;

	private java.lang.String companyName;

    @java.beans.ConstructorProperties({"__identifier", "companyName"})
    ShipperInfo(java.util.UUID __identifier, java.lang.String companyName) {
		this.__identifier = __identifier;
        this.companyName = companyName;
    }

	// Getters
	public java.util.UUID get__identifier() {
		return this.__identifier;
	}

	public java.lang.String getCompanyName() {
		return this.companyName;
	}

	// Setters
	public void set__identifier(java.util.UUID __identifier) {
		this.__identifier = __identifier;
	}

	public void setCompanyName(java.lang.String companyName) {
		this.companyName = companyName;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap(); 
		if (this.__identifier != null) {
			ret.put("__identifier", this.__identifier);
		}

	    if (companyName != null) {
		    ret.put("companyName", this.companyName);
	    }
		return ret;
	}

	public static ShipperInfo fromMap(Map<String, Object> map) {
		ShipperInfoBuilder builder = shipperInfoBuilder();
		if (map.containsKey("__identifier")) {
			builder.__identifier((java.util.UUID)map.get("__identifier"));
		}
	    if (map.containsKey("companyName")) {
			builder.companyName((java.lang.String) map.get("companyName"));
		}

	    return builder.build();
	}

	public String toString() {
	    return "ShipperInfo(identifier=" + this.__identifier + ", companyName=" + this.companyName + ")";
	}

    // Builder
	public static ShipperInfoBuilder shipperInfoBuilder() {
		return new ShipperInfoBuilder();
	}

	public static class ShipperInfoBuilder {
		private java.util.UUID __identifier;
        private java.lang.String companyName;

		ShipperInfoBuilder() {
		}
		public ShipperInfoBuilder __identifier(java.util.UUID __identifier) {
			this.__identifier = __identifier;
			return this;
		}

		public ShipperInfoBuilder companyName(java.lang.String companyName) {
			this.companyName = companyName;
			return this;
		}

		public ShipperInfo build() {
			return new ShipperInfo(this.__identifier, this.companyName);
		}

		public String toString() {
    	    return "ShipperInfoBuilder.ShipperInfo(identifier=" + __identifier + ", companyName=" + this.companyName + ")";
		}
	}
}