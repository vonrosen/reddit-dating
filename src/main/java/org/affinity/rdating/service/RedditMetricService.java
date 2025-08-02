/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.service;

import jakarta.annotation.PreDestroy;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.affinity.rdating.entity.RedditMetricRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class RedditMetricService {
  private final ConcurrentHashMap<Integer, List<Instant>> hourlyCount = new ConcurrentHashMap<>();
  private final RedditMetricRepository redditMetricRepository;

  public RedditMetricService(RedditMetricRepository redditMetricRepository) {
    this.redditMetricRepository = redditMetricRepository;
  }

  @Async
  public void addCount() {
    Instant now = Instant.now();
    int hour = now.atZone(ZoneId.of("UTC")).getHour();
    hourlyCount.computeIfAbsent(hour, h -> new ArrayList<>()).add(now);
  }

  @Scheduled(cron = "0 0 * * * *") // every hour
  public void scheduledWrite() {
    writeAndClearAsync();
  }

  @PreDestroy
  public void onShutdown() {
    writeAndClearAsync();
  }

  public void writeAndClearAsync() {
    List<Integer> keys = new ArrayList<>(hourlyCount.keySet());
    for (Integer hour : keys) {
      persist(hour, hourlyCount.remove(hour));
    }
  }

  private void persist(int hour, List<Instant> hourCount) {
    Instant hourInstant =
        ZonedDateTime.ofInstant(hourCount.getFirst(), ZoneId.of("UTC"))
            .withHour(hour)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)
            .toInstant();
    redditMetricRepository.upsertByHour(hourInstant, hourlyCount.size());
  }
}
