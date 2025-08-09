/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.model;

import org.affinity.rdating.enums.ListingKind;

public record PostId(String id) {

  public String fullName() {
    return ListingKind.POST.getKind() + "_" + id;
  }
}
