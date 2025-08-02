/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.client;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class RedditPostListing {
  public String kind;
  public ListingData data;

  public static class ListingData {
    public String after;
    public int dist;
    public String modhash;

    @SerializedName("geo_filter")
    public String geoFilter;

    public List<Child> children;
    public String before;
  }

  public static class Child {
    public String kind;
    public PostData data;
  }

  public static class PostData {
    public String subreddit;
    public String selftext;
    public String title;
    public String author;
    public String name;
    public String url;
    public String permalink;
    public String domain;
    public boolean is_gallery;
    public Map<String, MediaMetadata> media_metadata;
    public GalleryData gallery_data;
    public Preview preview;
    public String id;
  }

  public static class MediaMetadata {
    public String status;
    public String e;
    public String m;
    public List<MediaPreview> p;
    public MediaSource s;
    public String id;
  }

  public static class MediaPreview {
    public int y;
    public int x;
    public String u;
  }

  public static class MediaSource {
    public int y;
    public int x;
    public String u;
  }

  public static class GalleryData {
    public List<GalleryItem> items;
  }

  public static class GalleryItem {
    public String media_id;
    public long id;
  }

  public static class Preview {
    public List<PreviewImage> images;
    public boolean enabled;
  }

  public static class PreviewImage {
    public Source source;
    public List<Resolution> resolutions;
    public String id;
    public Map<String, Object> variants;
  }

  public static class Source {
    public String url;
    public int width;
    public int height;
  }

  public static class Resolution {
    public String url;
    public int width;
    public int height;
  }
}
