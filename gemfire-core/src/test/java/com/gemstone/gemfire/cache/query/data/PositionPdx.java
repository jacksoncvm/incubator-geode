/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All Rights Reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.pivotal.io/patents.
 *=========================================================================
 */
package com.gemstone.gemfire.cache.query.data;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.gemstone.gemfire.internal.cache.GemFireCacheImpl;
import com.gemstone.gemfire.pdx.PdxReader;
import com.gemstone.gemfire.pdx.PdxSerializable;
import com.gemstone.gemfire.pdx.PdxWriter;


public class PositionPdx implements Serializable, PdxSerializable, Comparable {
  private long avg20DaysVol=0;
  private String bondRating;
  private double convRatio;
  private String country;
  private double delta;
  private long industry;
  private long issuer;
  private double mktValue;
  private double qty;
  public String secId;
  public String secIdIndexed;
  private String secLinks;
  public String secType;
  private double sharesOutstanding;
  public String underlyer;
  private long volatility;
  private int pid;
  public static int cnt = 0;
  public int portfolioId = 0;
  
  public static int numInstance = 0;
  
  /* public no-arg constructor required for DataSerializable */  
  public PositionPdx() {
    this.numInstance++;
    //GemFireCacheImpl.getInstance().getLoggerI18n().fine(new Exception("DEBUG"));
  }

  public PositionPdx(String id, double out) {
    secId = id;
    secIdIndexed = secId;
    sharesOutstanding = out;
    secType = "a";
    pid = cnt++;
    this.mktValue = cnt;
    this.numInstance++;
    //GemFireCacheImpl.getInstance().getLoggerI18n().fine(new Exception("DEBUG" + this.secId));
  }
  
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof PositionPdx)) return false;
    return this.secId.equals(((PositionPdx)o).secId);
  }
  
  @Override
  public int hashCode() {
    return this.secId.hashCode();
  }
  
  
  public static void resetCounter() {
    cnt = 0;
  }
  
  public double getMktValue() {
    return this.mktValue;
  }
  
  public String getSecId(){
    return secId;
  }
  
  public int getId(){
    return pid;
  }
  
  public double getSharesOutstanding(){
    return sharesOutstanding;
  }
  
  public String toString(){
    return "PositionPdx [secId=" + this.secId + " out=" + this.sharesOutstanding
           + " type=" + this.secType + " id=" + this.pid + " mktValue="
           + this.mktValue + "]";
  }
  
  public Set getSet(int size){
    Set set = new HashSet();
    for(int i=0;i<size;i++){
      set.add(""+i);
    }
    return set;
  }
  
  public Set getCol(){
    Set set = new HashSet();
    for(int i=0;i<2;i++){
      set.add(""+i);
    }
    return set;
  }
  
  public int getPid() {
    return pid;
  }
  
  public void fromData(PdxReader in) {
    this.avg20DaysVol = in.readLong("avg20DaysVol");
    this.bondRating = in.readString("bondRating");
    this.convRatio = in.readDouble("convRatio");
    this.country = in.readString("country");
    this.delta = in.readDouble("delta");
    this.industry = in.readLong("industry");
    this.issuer = in.readLong("issuer");
    this.mktValue = in.readDouble("mktValue");
    this.qty = in.readDouble("qty");
    this.secId = in.readString("secId");
    this.secIdIndexed = in.readString("secIdIndexed");
    this.secLinks = in.readString("secLinks");
    this.sharesOutstanding = in.readDouble("sharesOutstanding");
    this.underlyer = in.readString("underlyer");
    this.volatility = in.readLong("volatility");
    this.pid = in.readInt("pid");
    this.portfolioId = in.readInt("portfolioId");
    //GemFireCacheImpl.getInstance().getLoggerI18n().fine(new Exception("DEBUG fromData() " + this.secId));    
  }
  
  public void toData(PdxWriter out) {
    out.writeLong("avg20DaysVol",this.avg20DaysVol);
    out.writeString("bondRating", this.bondRating);
    out.writeDouble("convRatio",this.convRatio);
    out.writeString("country", this.country);
    out.writeDouble("delta", this.delta);
    out.writeLong("industry", this.industry);
    out.writeLong("issuer", this.issuer);
    out.writeDouble("mktValue", this.mktValue);
    out.writeDouble("qty", this.qty);
    out.writeString("secId", this.secId);
    out.writeString("secIdIndexed", this.secIdIndexed);
    out.writeString("secLinks", this.secLinks);
    out.writeDouble("sharesOutstanding", this.sharesOutstanding);
    out.writeString("underlyer", this.underlyer);
    out.writeLong("volatility", this.volatility);
    out.writeInt("pid", this.pid);
    out.writeInt("portfolioId", this.portfolioId);
    // Identity Field.
    out.markIdentityField("secId");
 }


  public int compareTo(Object o)
  {
    if( o == this || ((PositionPdx)o).secId.equals(this.secId)) {
      return 0;
    } else {
      return this.pid < ((PositionPdx)o).pid ? -1:1;
    }
     
  } 
  
}
