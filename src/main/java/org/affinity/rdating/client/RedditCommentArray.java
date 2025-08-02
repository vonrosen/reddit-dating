/* (C)2025 */
package org.affinity.rdating.client;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RedditCommentArray {
  public static class Listing {
    public String kind;
    public ListingData data;
  }

  public static class ListingData {
    public String after;
    public Integer dist;
    public String modhash;

    @SerializedName("geo_filter")
    public String geoFilter;

    public List<Child> children;
    public String before;
  }

  public static class Child {
    public String kind;
    public JsonObject data;
  }

  // For kind == t3 (post)
  public static class PostData {
    public String title;
    public String selftext;
    public String author;
    public String name;
    public String permalink;
    public String url;
    // Add more fields as needed
  }

  // For kind == t1 (comment)
  public static class CommentData {
    public String body;
    public String author;
    public String name;
    public String parent_id;
    public String permalink;
    // Add more fields as needed
  }

  // For kind == more
  public static class MoreData {
    public int count;
    public String name;
    public String id;
    public String parent_id;
    public int depth;
    public List<String> children;
  }

  // Helper: parse array
  public static List<Listing> fromJson(String json, Gson gson) {
    return gson.fromJson(json, new com.google.gson.reflect.TypeToken<List<Listing>>() {}.getType());
  }

  // Example usage:
  // Gson gson = new Gson();
  // String json = ...; // your JSON string
  // List<RedditCommentArray.Listing> listings = RedditCommentArray.fromJson(json, gson);
  // RedditCommentArray.Listing firstListing = listings.get(0);
  // RedditCommentArray.ListingData data = firstListing.data;
  // RedditCommentArray.Child firstChild = data.children.get(0);
  // String kind = firstChild.kind;
  // RedditCommentArray.PostData postData = gson.fromJson(firstChild.data,
  // RedditCommentArray.PostData.class);
  // System.out.println(postData.title);
}
