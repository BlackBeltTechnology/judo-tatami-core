package sdk.demo.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class Comment {
	private java.util.UUID __identifier;
	private String note;
	private String author;
	private java.time.ZonedDateTime timestamp;

    @java.beans.ConstructorProperties({"__identifier", "note", "author", "timestamp"})
    Comment(java.util.UUID __identifier, String note, String author, java.time.ZonedDateTime timestamp) {
		this.__identifier = __identifier;
        this.note = note;
        this.author = author;
        this.timestamp = timestamp;
    }

	// Getters
	public java.util.UUID get__identifier() {
		return this.__identifier;
	}
	public String getNote() {
		return this.note;
	}
	public String getAuthor() {
		return this.author;
	}
	public java.time.ZonedDateTime getTimestamp() {
		return this.timestamp;
	}

	// Setters
	public void set__identifier(java.util.UUID __identifier) {
		this.__identifier = __identifier;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public void setTimestamp(java.time.ZonedDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap();
		if (this.__identifier != null) {
			ret.put("__identifier", this.__identifier);
		}

	    if (note != null) {
		    ret.put("note", this.note);
	    }
	    if (author != null) {
		    ret.put("author", this.author);
	    }
	    if (timestamp != null) {
		    ret.put("timestamp", this.timestamp);
	    }
		return ret;
	}

	public static Comment fromMap(Map<String, Object> map) {
		CommentBuilder builder = commentBuilder();
		if (map.containsKey("__identifier")) {
			builder.__identifier((java.util.UUID)map.get("__identifier"));
		}
	    if (map.containsKey("note")) {
			builder.note((String) map.get("note"));
		}
	    if (map.containsKey("author")) {
			builder.author((String) map.get("author"));
		}
	    if (map.containsKey("timestamp")) {
			builder.timestamp((java.time.ZonedDateTime) map.get("timestamp"));
		}
	    return builder.build();
	}

	public String toString() {
	    return "Comment(identifier=" + this.__identifier + ", note=" + this.note + ", author=" + this.author + ", timestamp=" + this.timestamp + ")";
	}

    // Builder
	public static CommentBuilder commentBuilder() {
		return new CommentBuilder();
	}

	public static class CommentBuilder {
		private java.util.UUID __identifier;
        private String note;
        private String author;
        private java.time.ZonedDateTime timestamp;

		CommentBuilder() {
		}

		public CommentBuilder __identifier(java.util.UUID __identifier) {
			this.__identifier = __identifier;
			return this;
		}
		public CommentBuilder note(String note) {
			this.note = note;
			return this;
		}

		public CommentBuilder author(String author) {
			this.author = author;
			return this;
		}

		public CommentBuilder timestamp(java.time.ZonedDateTime timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public Comment build() {
			return new Comment(this.__identifier, this.note, this.author, this.timestamp);
		}

		public String toString() {
    	    return "CommentBuilder.Comment(identifier=" + __identifier + ", note=" + this.note + ", author=" + this.author + ", timestamp=" + this.timestamp + ")";
		}
	}
}
