package org.affinity.rdating.service;

import jakarta.annotation.PreDestroy;
import org.affinity.rdating.entity.RedditMetricRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RedditMetricService {
    private final ConcurrentHashMap<Integer, List<Instant>> instantMap = new ConcurrentHashMap<>();
    private final RedditMetricRepository redditMetricRepository;

    public RedditMetricService(RedditMetricRepository redditMetricRepository) {
        this.redditMetricRepository = redditMetricRepository;
    }

    public void addCount(Instant instant) {
        int hour = instant.atZone(ZoneId.of("UTC")).getHour();
        instantMap.computeIfAbsent(hour, _ -> new ArrayList<>()).add(instant);
    }

    @Scheduled(cron = "0 0 * * * *") // every hour
    public void scheduledWrite() {
        writeAndClearAsync();
    }

    @PreDestroy
    public void onShutdown() {
        writeAndClearAsync();
    }

    @Async
    public void writeAndClearAsync() {
        for (Integer hour : instantMap.keySet()) {
            int count = instantMap.get(hour).size();
            Instant hourInstant = instantMap.get(hour).getFirst();
            redditMetricRepository.upsertByHour(hourInstant, count);
        }
        instantMap.clear();
    }
}

