//
//  ReactNativeCustomizedUpdate.h
//  ReactNativeCustomizedUpdate
//
//  Created by Rahul Jiresal on 11/23/15.
//  Copyright © 2015 Rahul Jiresal. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef NS_ENUM(NSUInteger, ReactNativeCustomizedUpdateUpdateType)
{
  // Versions are in format Major.Minor.Patch
  ReactNativeCustomizedUpdateMajorUpdate = 1,   // Updates are only applied if the major version number changes
  ReactNativeCustomizedUpdateMinorUpdate,       // (DEFAULT) Updates are applied if major or minor version changes
  ReactNativeCustomizedUpdatePatchUpdate,       // Updates are applied if major, minor or patch version changes
};

@class ReactNativeCustomizedUpdate;

@protocol ReactNativeCustomizedUpdateDelegate <NSObject>

- (void)ReactNativeCustomizedUpdate:(ReactNativeCustomizedUpdate *)reactiveNativeAutoUpdater updateDownloadedToURL:(NSURL*)url currentVersion:(NSString *)currentVersion;
- (void)ReactNativeCustomizedUpdate:(ReactNativeCustomizedUpdate *)reactiveNativeAutoUpdater updateDownloadFailed:(NSError *)error;

@end

@interface ReactNativeCustomizedUpdate : NSObject

@property (weak) id<ReactNativeCustomizedUpdateDelegate> delegate;

/**
 *  Returns the singleton instance of ReactNativeCustomizedUpdate
 *
 *  @return instance of ReactNativeCustomizedUpdate
 */
+ (id)sharedInstance;

/**
 *  Initializes the singleton instance with the metadata URL and default JS code location
 *
 *  @param url                          Metadata URL where information of updates can be downloaded
 *  @param defaultJSCodeLocation        default JS code location in case there are no updates
 *  @param defaultMetadataFileLocation  location of the metadata file that described the JS code shipped with the app
 */
- (void)initializeWithUpdateMetadataUrl:(NSURL*)url defaultJSCodeLocation:(NSURL*)defaultJSCodeLocation defaultMetadataFileLocation:(NSURL*)metadataFileLocation;

/**
 *  Returns the location of the latest JS code that has been downloaded so far.
 *
 *  @return NSURL object of the JS code location
 */
- (NSURL*)latestJSCodeLocation;

/**
 *  Call this to enable/disable visual progress of the download
 *
 *  @param progress BOOL value to enable/disable visual progress
 */
- (void)showProgress: (BOOL)progress;

/**
 *  Check for updates right now
 */
- (void)checkUpdate;

/**
 *  Check for updates if not checked in the last 24 hours
 */
- (void)checkUpdateDaily;

/**
 *  Check for updates if not checked in the last 7 days
 */
- (void)checkUpdateWeekly;

/**
 *  Enable/disable cellular data use for downloading updates
 *
 *  @param cellular BOOL value to enable/disable cellular data use
 */
- (void)allowCellularDataUse: (BOOL)cellular;

/**
 *  Decide what type of updates to download - All, Major or Minor
 *  Decided using the version information provided in the metadata JSON file
 *
 *  @param type type of udpates to download
 */
- (void)downloadUpdatesForType:(ReactNativeCustomizedUpdateUpdateType)type;

/**
 *  When the JSON file has a relative URL for downloading the JS Bundle,
 *  set the hostname for relative downloads
 *
 *  @param hostname
 */
- (void)setHostnameForRelativeDownloadURLs:(NSString*)hostname;

@end
