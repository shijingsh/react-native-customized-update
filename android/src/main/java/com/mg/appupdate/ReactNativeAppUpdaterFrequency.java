package com.mg.appupdate;

/**
 *  Decide how frequently to check for updates.
 * Available options -
 *  EACH_TIME - each time the app starts
 *  DAILY     - maximum once per day
 *  WEEKLY    - maximum once per week
 *  MONTHLY   - maximum once per month
 * default value - EACH_TIME
 * */
public enum ReactNativeAppUpdaterFrequency {
    EACH_TIME, DAILY, WEEKLY,MONTHLY
}
