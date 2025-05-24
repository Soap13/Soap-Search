package com.soap.search.util;

import java.util.BitSet;

public class DocIdBitSet{
  private static final int NO_MORE_DOCS = Integer.MAX_VALUE;
  private int docId;
  private BitSet bitSet;

  DocIdBitSet(BitSet bitSet) {
    this.bitSet = bitSet;
    this.docId = -1;

//    for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
//      docId = i;
//    }
  }

  /** @deprecated use {@link #docID()} instead. */
  public int doc() {
    assert docId != -1;
    return docId;
  }

  public int docID() {
    return docId;
  }

  /** @deprecated use {@link #nextDoc()} instead. */
  public boolean next() {
    // (docId + 1) on next line requires -1 initial value for docNr:
    return nextDoc() != NO_MORE_DOCS;
  }

  public int nextDoc() {
    // (docId + 1) on next line requires -1 initial value for docNr:
    int d = bitSet.nextSetBit(docId + 1);
    // -1 returned by BitSet.nextSetBit() when exhausted
    docId = d == -1 ? NO_MORE_DOCS : d;
    return docId;
  }

  /** @deprecated use {@link #advance(int)} instead. */
  public boolean skipTo(int skipDocNr) {
    return advance(skipDocNr) != NO_MORE_DOCS;
  }

  public int advance(int target) {
    int d = bitSet.nextSetBit(target);
    // -1 returned by BitSet.nextSetBit() when exhausted
    docId = d == -1 ? NO_MORE_DOCS : d;
    return docId;
  }

}