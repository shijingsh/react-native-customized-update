# react-native-customized-update
iOS/Android 
react-native app boudleJs update or apk update.
Support Chinese and English
## Result
<img width=200 title="update" src="https://github.com/liukefu2050/react-native-customized-update/blob/master/images/1.jpg">


Import library
```javascript
import ReactNativeCustomizedUpdate from 'react-native-customized-update';
```

#### check update

```javascript
  componentDidMount(){
      ReactNativeCustomizedUpdate.shouldApkUpdate({})
  }
```

#### clear app cache 
```javascript
    ReactNativeCustomizedUpdate.getAppCacheSize(function (value, unit) {
            //console.log(value)
            //console.log(unit)

            //that.setState({cacheSize:value+unit});
     })
```

```javascript
    ReactNativeCustomizedUpdate.clearAppCache(function () {

        //that.setState({cacheSize:''});
    })
```

#### get app version 
```javascript
    ReactNativeCustomizedUpdate.getAppVersion(function (version) {

        //that.setState({version:version});
    })
```

#### Request Object

| Property        | Type           | Description  |
| ------------- |:-------------:| :-----|
| isUpdateNow | bool (default false)      | Enable or disable check update for now |
| checkVersionUrl | String      | set checkVersionUrl  |

#### method

| name        | Description  |
| ------------- | :-----|
| shouldApkUpdate  | show apk update |

## Install

```
yarn add react-native-customized-update
```

###example 

https://github.com/shijingsh/react-native-customized-update-example

## Install android
- 1、import class 
- 2、MainActivity extends ReactNativeAppUpdateActivity
- 3、Override getCheckVersionUrl
   Check for updates
   ```javascript
   {
     "jsUrl": "js downloading url",
     "jsVersion": "1.0.0",
     "url": "apk downloading url",
     "version": "2.0.0"
   } 
   ```
- 4、Override getUpdateFrequency
    Decide how frequently to check for updates.
     *  EACH_TIME - each time the app starts
     *  DAILY     - maximum once per day
     *  WEEKLY    - maximum once per week
     *  MONTHLY   - maximum once per month
- 5、Override getShowProgress
    To show progress during the update process.(true/false)
- 6、add metadata file
    *  file name:metadata.android.json
    *  direct:root\android\app\src\main\assets
    *  content:
       ```javascript
        {
            "jsVersion": "1.0.0",
            "url": null
        }
       ```
       #### setting example
        ```java
        package com.exampleappupdate;
        
        import com.facebook.react.ReactActivity;
        import com.mg.appupdate.*;
        import com.mg.appupdate.ReactNativeAppUpdate.ReactNativeCustomizedUpdateFrequency;
        
        public class MainActivity extends ReactNativeAppUpdateActivity {
        
        
            @Override
            protected String getCheckVersionUrl() {
                /**
                 * value example:
                 * {"jsUrl":"http://192.168.15.67:10086/yourapp/your.js","jsVersion":"1.0","url":"http://192.168.15.67:10086/yourapp/your.apk","version":"2.0"}
                 */
                return "http://192.168.15.67:10086/jajayun/app/updateAndroid.json";
                //return "http://www.jajayun.com/app/updateAndroid.json";
            }
        
            /**
             *  Decide how frequently to check for updates.
             * Available options -
             *  EACH_TIME - each time the app starts
             *  DAILY     - maximum once per day
             *  WEEKLY    - maximum once per week
             * default value - EACH_TIME
             * */
            @Override
            protected ReactNativeCustomizedUpdateFrequency getUpdateFrequency() {
                return ReactNativeCustomizedUpdateFrequency.EACH_TIME;
            }
        
            /**
             *  To show progress during the update process.
             * */
            @Override
            protected boolean getShowProgress() {
                return true;
            }
        
            /**
             * Returns the name of the main component registered from JavaScript.
             * This is used to schedule rendering of the component.
             */
            @Override
            protected String getMainComponentName() {
                return "exampleAppUpdate";
            }
        }
        
        ```

## License
*MIT*
