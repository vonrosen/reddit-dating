/* (C)2025 */
package org.affinity.rdating.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "reddit_metric")
public class RedditMetricEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "hour", nullable = false)
  private Instant hour;

  @Column(name = "count", nullable = false)
  private Long count;

  public RedditMetricEntity() {}

  public RedditMetricEntity(Instant hour, Long count) {
    this.hour = hour;
    this.count = count;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Instant getHour() {
    return hour;
  }

  public void setHour(Instant hour) {
    this.hour = hour;
  }

  public Long getCount() {
    return count;
  }

  public void setCount(Long count) {
    this.count = count;
  }
}
