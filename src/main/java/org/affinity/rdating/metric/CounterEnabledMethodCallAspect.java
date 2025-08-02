/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.metric;

import org.affinity.rdating.service.RedditMetricService;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CounterEnabledMethodCallAspect {

  private final RedditMetricService redditMetricService;

  public CounterEnabledMethodCallAspect(RedditMetricService redditMetricService) {
    this.redditMetricService = redditMetricService;
  }

  @Pointcut("@annotation(org.affinity.rdating.metric.CounterEnabled)")
  public void countedMethodsPointCut() {}

  @After("countedMethodsPointCut()")
  public void logCounter() {
    redditMetricService.addCount();
  }
}
