/* (C)2025 */
package org.affinity.rdating.enums;

public enum ListingKind {
  POST("t3"),
  COMMENT("t1");

  private final String kind;

  ListingKind(String kind) {
    this.kind = kind;
  }

  public static ListingKind fromKind(String kind) {
    for (ListingKind listingKind : values()) {
      if (listingKind.kind.equals(kind)) {
        return listingKind;
      }
    }
    throw new IllegalArgumentException("Unknown kind: " + kind);
  }
}
