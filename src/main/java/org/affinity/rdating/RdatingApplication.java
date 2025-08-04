/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RdatingApplication {

  public static void main(String[] args) {
    SpringApplication.run(RdatingApplication.class, args);
  }
}
