/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All Rights Reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.pivotal.io/patents.
 *=========================================================================
 */
/**
 * 
 */
package com.gemstone.gemfire.internal.cache.persistence;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

import com.gemstone.gemfire.internal.DataSerializableFixedID;
import com.gemstone.gemfire.internal.Version;
import com.gemstone.gemfire.internal.cache.versions.VersionSource;

/**
 * A Unique ID for a disk store
 * @author dsmith
 *
 * TODO - RVV - this class is java serializable because apparently it is included in some
 * Exception that is serialized with java serialization back to a client as part
 * of a put all exception. See PutAllCSDUnitTest.testPartialKeyInPR. 
 */
public class DiskStoreID implements VersionSource<DiskStoreID>, Serializable {
  
  private static final long serialVersionUID = 1L;
  
  private long mostSig;
  private long leastSig;
  
  public DiskStoreID(UUID uuid) {
    this.mostSig = uuid.getMostSignificantBits();
    this.leastSig = uuid.getLeastSignificantBits();
  }

  public DiskStoreID(long diskStoreIdHigh, long diskStoreIdLow) {
    this.mostSig = diskStoreIdHigh;
    this.leastSig = diskStoreIdLow;
  }

  /** for deserialization */
  public DiskStoreID() {
  }

  @Override
  public void toData(DataOutput out) throws IOException {
    out.writeLong(mostSig);
    out.writeLong(leastSig);
  }
  
  @Override
  public void writeEssentialData(DataOutput out) throws IOException {
    out.writeLong(mostSig);
    out.writeLong(leastSig);
  }
  
  public static DiskStoreID readEssentialData(DataInput in) throws IOException {
    long mostSig = in.readLong();
    long leastSig = in.readLong();
    return new DiskStoreID(mostSig, leastSig);
  }

  @Override
  public void fromData(DataInput in) throws IOException, ClassNotFoundException {
    mostSig = in.readLong();
    leastSig = in.readLong();
  }

  @Override
  public int compareTo(DiskStoreID tagID) {
    if(tagID == null) {
      return 1;
    }
    int result = Long.signum(mostSig - tagID.mostSig);
    if(result != 0) {
      result = Long.signum(leastSig - tagID.leastSig);
    }
    return result;
  }

  public long getMostSignificantBits() {
    return mostSig;
  }

  public long getLeastSignificantBits() {
    return leastSig;
  }

  public static DiskStoreID random() {
    return new DiskStoreID(UUID.randomUUID());
  }

  @Override
  public int getDSFID() {
    return DISK_STORE_ID;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (leastSig ^ (leastSig >>> 32));
    result = prime * result + (int) (mostSig ^ (mostSig >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DiskStoreID other = (DiskStoreID) obj;
    if (leastSig != other.leastSig)
      return false;
    if (mostSig != other.mostSig)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return Long.toHexString(mostSig) + "-" + Long.toHexString(leastSig);
  }

  public UUID toUUID() {
    return new UUID(mostSig, leastSig);
  }

  @Override
  public Version[] getSerializationVersions() {
    return null;
  }
  
  public String abbrev() {
    return Long.toHexString(mostSig).substring(8);
  }
}
