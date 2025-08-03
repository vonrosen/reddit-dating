/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.client;

import java.util.List;
import java.util.Map;

public class RedditUpvoteListing {
  public String kind;
  public RedditListingData data;

  public static class RedditListingData {
    public String after;
    public Integer dist;
    public String modhash;
    public String geo_filter;
    public List<RedditChild> children;
    public String before;
  }

  public static class RedditChild {
    public String kind;
    public RedditPostData data;
  }

  public static class RedditPostData {
    public Object approved_at_utc;
    public String subreddit;
    public String selftext;
    public String author_fullname;
    public boolean saved;
    public Object mod_reason_title;
    public int gilded;
    public boolean clicked;
    public String title;
    public List<Object> link_flair_richtext;
    public String subreddit_name_prefixed;
    public boolean hidden;
    public Object pwls;
    public String link_flair_css_class;
    public int downs;
    public Integer thumbnail_height;
    public Object top_awarded_type;
    public boolean hide_score;
    public String name;
    public boolean quarantine;
    public String link_flair_text_color;
    public double upvote_ratio;
    public Object author_flair_background_color;
    public int ups;
    public int total_awards_received;
    public Map<String, Object> media_embed;
    public Integer thumbnail_width;
    public Object author_flair_template_id;
    public boolean is_original_content;
    public List<Object> user_reports;
    public Object secure_media;
    public boolean is_reddit_media_domain;
    public boolean is_meta;
    public Object category;
    public Map<String, Object> secure_media_embed;
    public String link_flair_text;
    public boolean can_mod_post;
    public int score;
    public Object approved_by;
    public boolean is_created_from_ads_ui;
    public boolean author_premium;
    public String thumbnail;
    public boolean edited;
    public Object author_flair_css_class;
    public List<Object> author_flair_richtext;
    public Map<String, Object> gildings;
    public String post_hint;
    public Object content_categories;
    public boolean is_self;
    public String subreddit_type;
    public double created;
    public String link_flair_type;
    public Object wls;
    public Object removed_by_category;
    public Object banned_by;
    public String author_flair_type;
    public String domain;
    public boolean allow_live_comments;
    public String selftext_html;
    public Boolean likes;
    public Object suggested_sort;
    public Object banned_at_utc;
    public String url_overridden_by_dest;
    public Object view_count;
    public boolean archived;
    public boolean no_follow;
    public boolean is_crosspostable;
    public boolean pinned;
    public boolean over_18;
    public RedditPreview preview;
    public List<Object> all_awardings;
    public List<Object> awarders;
    public boolean media_only;
    public String link_flair_template_id;
    public boolean can_gild;
    public boolean spoiler;
    public boolean locked;
    public Object author_flair_text;
    public List<Object> treatment_tags;
    public String rte_mode;
    public boolean visited;
    public Object removed_by;
    public Object mod_note;
    public Object distinguished;
    public String subreddit_id;
    public boolean author_is_blocked;
    public Object mod_reason_by;
    public Object num_reports;
    public Object removal_reason;
    public String link_flair_background_color;
    public String id;
    public boolean is_robot_indexable;
    public Object report_reasons;
    public String author;
    public Object discussion_type;
    public int num_comments;
    public boolean send_replies;
    public boolean contest_mode;
    public List<Object> mod_reports;
    public boolean author_patreon_flair;
    public Object author_flair_text_color;
    public String permalink;
    public boolean stickied;
    public String url;
    public int subreddit_subscribers;
    public double created_utc;
    public int num_crossposts;
    public Object media;
    public boolean is_video;
  }

  public static class RedditPreview {
    public List<RedditImage> images;
    public boolean enabled;
  }

  public static class RedditImage {
    public RedditImageSource source;
    public List<RedditImageResolution> resolutions;
    public Map<String, Object> variants;
    public String id;
  }

  public static class RedditImageSource {
    public String url;
    public int width;
    public int height;
  }

  public static class RedditImageResolution {
    public String url;
    public int width;
    public int height;
  }
}
