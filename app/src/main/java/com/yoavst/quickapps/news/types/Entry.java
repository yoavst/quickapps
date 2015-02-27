package com.yoavst.quickapps.news.types;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yoav.
 */
public class Entry {

	@Expose
	private String title;
	@Expose
	private String id;
	@Expose
	private long crawled;
	@Expose
	private List<Alternate> alternate = new ArrayList<>();
	@Expose
	private boolean unread;
	@Expose
	private long published;
	@Expose
	private String author;
	@Expose
	private Origin origin;
	@Expose
	private int engagement;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getCrawled() {
		return crawled;
	}

	public void setCrawled(long crawled) {
		this.crawled = crawled;
	}

	public List<Alternate> getAlternate() {
		return alternate;
	}

	public void setAlternate(List<Alternate> alternate) {
		this.alternate = alternate;
	}

	public boolean isUnread() {
		return unread;
	}

	public void setUnread(boolean unread) {
		this.unread = unread;
	}

	public long getPublished() {
		return published;
	}

	public void setPublished(long published) {
		this.published = published;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Origin getOrigin() {
		return origin;
	}

	public void setOrigin(Origin origin) {
		this.origin = origin;
	}

	public int getEngagement() {
		return engagement;
	}

	public void setEngagement(int engagement) {
		this.engagement = engagement;
	}

	public static class Alternate {

		@Expose
		private String type;
		@Expose
		private String href;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getHref() {
			return href;
		}

		public void setHref(String href) {
			this.href = href;
		}

	}

	public static class Origin {

		@Expose
		private String title;
		@Expose
		private String htmlUrl;
		@Expose
		private String streamId;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getHtmlUrl() {
			return htmlUrl;
		}

		public void setHtmlUrl(String htmlUrl) {
			this.htmlUrl = htmlUrl;
		}

		public String getStreamId() {
			return streamId;
		}

		public void setStreamId(String streamId) {
			this.streamId = streamId;
		}

	}

}

