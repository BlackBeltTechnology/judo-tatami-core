package hu.blackbelt.judo.tatami.itest.restbeans;

import java.util.LinkedHashMap;
import java.util.Map;

public class Comment {
	private java.lang.String note;
	private java.lang.String author;
	private java.time.ZonedDateTime timestamp;

    @java.beans.ConstructorProperties({"note", "author", "timestamp"})
    Comment(java.lang.String note, java.lang.String author, java.time.ZonedDateTime timestamp) {
        this.note = note;
        this.author = author;
        this.timestamp = timestamp;
    }

	// Getters
	public java.lang.String getNote() {
		return this.note;
	}
	public java.lang.String getAuthor() {
		return this.author;
	}
	public java.time.ZonedDateTime getTimestamp() {
		return this.timestamp;
	}

	// Setters
	public void setNote(java.lang.String note) {
		this.note = note;
	}
	public void setAuthor(java.lang.String author) {
		this.author = author;
	}
	public void setTimestamp(java.time.ZonedDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public Map<String, Object> toMap() {
		Map ret = new LinkedHashMap(); 
	    if (getNote() != null) {
		    ret.put("note", this.note);
	    }
	    if (getAuthor() != null) {
		    ret.put("author", this.author);
	    }
	    if (getTimestamp() != null) {
		    ret.put("timestamp", this.timestamp);
	    }
		return ret;
	}

	public static Comment fromMap(Map<String, Object> map) {
		CommentBuilder builder = commentBuilder();
	    if (map.containsKey("note")) {
			builder.note((java.lang.String) map.get("note"));
		}
	    if (map.containsKey("author")) {
			builder.author((java.lang.String) map.get("author"));
		}
	    if (map.containsKey("timestamp")) {
			builder.timestamp((java.time.ZonedDateTime) map.get("timestamp"));
		}

	    return builder.build();
	}

	public String toString() {
	    return "Comment(note=" + this.note + ", author=" + this.author + ", timestamp=" + this.timestamp + ")";
	}

    // Builder
	public static CommentBuilder commentBuilder() {
		return new CommentBuilder();
	}

	public static class CommentBuilder {
        private java.lang.String note;
        private java.lang.String author;
        private java.time.ZonedDateTime timestamp;

		CommentBuilder() {
		}

		public CommentBuilder note(java.lang.String note) {
			this.note = note;
			return this;
		}
		public CommentBuilder author(java.lang.String author) {
			this.author = author;
			return this;
		}
		public CommentBuilder timestamp(java.time.ZonedDateTime timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public Comment build() {
			return new Comment(this.note, this.author, this.timestamp);
		}

		public String toString() {
    	    return "CommentBuilder.Comment(note=" + this.note + ", author=" + this.author + ", timestamp=" + this.timestamp + ")";
		}
	}
}