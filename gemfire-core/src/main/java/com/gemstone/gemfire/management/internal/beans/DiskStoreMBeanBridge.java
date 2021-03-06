/*
 *  =========================================================================
 *  Copyright (c) 2002-2014 Pivotal Software, Inc. All Rights Reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * more patents listed at http://www.pivotal.io/patents.
 *  ========================================================================
 */
package com.gemstone.gemfire.management.internal.beans;

import java.io.File;

import com.gemstone.gemfire.cache.DiskStore;
import com.gemstone.gemfire.internal.cache.DirectoryHolder;
import com.gemstone.gemfire.internal.cache.DiskStoreImpl;
import com.gemstone.gemfire.internal.cache.DiskStoreStats;
import com.gemstone.gemfire.management.internal.ManagementStrings;
import com.gemstone.gemfire.management.internal.beans.stats.MBeanStatsMonitor;
import com.gemstone.gemfire.management.internal.beans.stats.StatType;
import com.gemstone.gemfire.management.internal.beans.stats.StatsAverageLatency;
import com.gemstone.gemfire.management.internal.beans.stats.StatsKey;
import com.gemstone.gemfire.management.internal.beans.stats.StatsRate;

/**
 * Bridge class to act as an interface between JMX layer and GemFire DiskStores
 * 
 * @author rishim
 * 
 */
public class DiskStoreMBeanBridge {

  private DiskStoreImpl diskStore;

  private int compactionThreshold;

  private String[] diskDirectories;

  private String name;

  private long timeInterval;

  private int writeBufferSize;

  private long maxOpLogSize;

  private boolean isAutoCompact;

  private boolean isForceCompactionAllowed;

  private int queueSize;
  
  private MBeanStatsMonitor monitor;
  
  private StatsRate diskReadsRate;
  
  private StatsRate diskWritesRate;  
  
  private StatsAverageLatency diskReadsAvgLatency;
  
  private StatsAverageLatency diskWritesAvgLatency;
  
  private StatsAverageLatency diskFlushTimeAvgLatency;
  
  
  private DiskStoreStats diskStoreStats;
  
  private DirectoryHolder[] directoryHolders;

  public DiskStoreMBeanBridge(DiskStore ds) {
    this.diskStore = (DiskStoreImpl)ds;
    initDiskData();
    this.monitor = new MBeanStatsMonitor(ManagementStrings.DISKSTORE_MONITOR
        .toLocalizedString());

    this.diskStoreStats = diskStore.getStats();
    
    addDiskStoreStats(diskStoreStats);
    initializeStats();
  }

  private void initDiskData() {
    this.name = diskStore.getName();
    this.compactionThreshold = diskStore.getCompactionThreshold();
    this.timeInterval = diskStore.getTimeInterval();
    this.writeBufferSize = diskStore.getWriteBufferSize();
    this.maxOpLogSize = diskStore.getMaxOplogSize();
    this.queueSize = diskStore.getQueueSize();
    this.isAutoCompact = diskStore.getAutoCompact();
    this.isForceCompactionAllowed = diskStore.getAllowForceCompaction();
    this.directoryHolders = diskStore.getDirectoryHolders();

    File[] diskDirs = diskStore.getDiskDirs();
    String[] diskDirStr = new String[diskDirs.length];
    for (int i = 0; i < diskDirs.length; i++) {
      diskDirStr[i] = diskDirs[i].getAbsolutePath();
    }
    this.diskDirectories = diskDirStr;

  }
  
  
  public void stopMonitor(){
    monitor.stopListener();
  }

  // ** Operations On DiskStores **//

  /**
   * Allows a disk compaction to be forced on this disk store. The compaction is
   * done even if automatic compaction is not configured. If the current active
   * oplog has had data written to it and it is compactable then an implicit
   * call to forceRoll will be made so that the active oplog can be compacted.
   * This method will block until the compaction completes. return true if one
   * or more oplogs were compacted; False indicates that no oplogs were ready to
   * be compacted or that a compaction was already in progress.
   */
  public boolean forceCompaction() {
    return diskStore.forceCompaction();
  }

  /**
   * Asks the disk store to start writing to a new oplog. The old oplog will be
   * asynchronously compressed if compaction is set to true. The new oplog will
   * be created in the next available directory with free space. If there is no
   * directory with free space available and compaction is set to false, then a
   * DiskAccessException saying that the disk is full will be thrown. If
   * compaction is true then the application will wait for the other oplogs to
   * be compacted and more space to be created
   */
  public void forceRoll() {
    diskStore.forceRoll();
  }

  /**
   * Causes any data that is currently in the asynchronous queue to be written
   * to disk. Does not return until the flush is complete.
   */
  public void flush() {
    diskStore.flush();
  }

  /** DiskStore Config Data **/

  public int getCompactionThreshold() {
    return compactionThreshold;
  }

  public String[] getDiskDirectories() {
    return diskDirectories;
  }

  public long getMaxOpLogSize() {
    return maxOpLogSize;
  }

  public String getName() {
    return name;
  }

  public long getTimeInterval() {
    return timeInterval;
  }

  public int getWriteBufferSize() {
    return writeBufferSize;
  }

  public boolean isAutoCompact() {
    return isAutoCompact;
  }

  public boolean isForceCompactionAllowed() {
    return isForceCompactionAllowed;
  }

  public int getQueueSize() {
    return queueSize;
  }

  /** Statistics **/
  
  public DiskStoreMBeanBridge() {
    this.monitor = new MBeanStatsMonitor(ManagementStrings.DISKSTORE_MONITOR
        .toLocalizedString());
    initializeStats();
  }
  
  public void addDiskStoreStats(DiskStoreStats stats){
    monitor.addStatisticsToMonitor(stats.getStats());
  }
  
  private void initializeStats(){
    
    String[] diskReads = new String[] { StatsKey.DISK_READ_BYTES, StatsKey.DISK_RECOVERED_BYTES };
    diskReadsRate = new StatsRate(diskReads, StatType.LONG_TYPE, monitor);
    
    diskWritesRate =  new StatsRate(
        StatsKey.DISK_WRITEN_BYTES, StatType.LONG_TYPE, monitor);
    
    diskFlushTimeAvgLatency = new StatsAverageLatency(
        StatsKey.NUM_FLUSHES, StatType.LONG_TYPE,
        StatsKey.TOTAL_FLUSH_TIME, monitor);
    
    diskReadsAvgLatency = new StatsAverageLatency(
        StatsKey.DISK_READ_BYTES, StatType.LONG_TYPE,
        StatsKey.DISK_READS_TIME, monitor);
    
    diskWritesAvgLatency = new StatsAverageLatency(
        StatsKey.DISK_WRITEN_BYTES, StatType.LONG_TYPE,
        StatsKey.DISK_WRITES_TIME, monitor);
  }
  

  public long getDiskReadsAvgLatency() {
    return diskReadsAvgLatency.getAverageLatency();
  }

  public float getDiskReadsRate() {
    return diskReadsRate.getRate();
  }

  public long getDiskWritesAvgLatency() {
    return diskWritesAvgLatency.getAverageLatency();
  }

  public float getDiskWritesRate() {
    return diskWritesRate.getRate();
  }

  public long getFlushTimeAvgLatency() {
    return diskFlushTimeAvgLatency.getAverageLatency();
  }

  public int getTotalBackupInProgress() {
    return getDiskStoreStatistic(StatsKey.BACKUPS_IN_PROGRESS).intValue();
  }
  
  public int getTotalBackupCompleted(){
    return getDiskStoreStatistic(StatsKey.BACKUPS_COMPLETED).intValue();
  }

  public long getTotalBytesOnDisk() {
    long diskSpace = 0;
    for (DirectoryHolder dr : this.directoryHolders) {
      diskSpace += dr.getDiskDirectoryStats().getDiskSpace();
    }
    return diskSpace;
  }

  public int getTotalQueueSize() {
    return getDiskStoreStatistic(StatsKey.DISK_QUEUE_SIZE).intValue();
  }
  
  public int getTotalRecoveriesInProgress() {
    return getDiskStoreStatistic(StatsKey.RECOVERIES_IN_PROGRESS).intValue();
  }
   
  public Number getDiskStoreStatistic(String statName) {
    if(diskStoreStats != null){
      return diskStoreStats.getStats().get(statName);  
    }
    return 0;
  }
  
  public float getDiskUsageWarningPercentage() {
    return diskStore.getDiskUsageWarningPercentage();
  }

  public float getDiskUsageCriticalPercentage() {
    return diskStore.getDiskUsageCriticalPercentage();
  }
  
  public void setDiskUsageWarningPercentage(float warningPercent) {
    diskStore.setDiskUsageWarningPercentage(warningPercent);
  }
  
  public void setDiskUsageCriticalPercentage(float criticalPercent) {
    diskStore.setDiskUsageCriticalPercentage(criticalPercent);
  }
}
