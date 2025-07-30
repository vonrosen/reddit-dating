package org.affinity.rdating.service;

import jakarta.annotation.PreDestroy;
import org.affinity.rdating.entity.RedditMetricRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    @Async
    public synchronized void addCount() {
        Instant now = Instant.now();
        int hour = now.atZone(ZoneId.of("UTC")).getHour();
        instantMap.computeIfAbsent(hour, _ -> new ArrayList<>()).add(now);
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
    public synchronized void writeAndClearAsync() {
        for (Integer hour : instantMap.keySet()) {
            int count = instantMap.get(hour).size();
            Instant hourInstant = ZonedDateTime
                    .ofInstant(instantMap.get(hour).getFirst(),
                        ZoneId.of("UTC"))
                    .withHour(hour)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0)
                    .toInstant();
            redditMetricRepository.upsertByHour(hourInstant, count);
        }
        instantMap.clear();
    }
}

