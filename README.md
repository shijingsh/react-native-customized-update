# react-native-customized-update
iOS/Android 
react-native app boudleJs update or apk update

## Result
<img width=200 title="update" src="https://github.com/liukefu2050/react-native-customized-update/blob/master/images/1.jpg">


Import library
```javascript
import ReactNativeCustomizedUpdate from 'react-native-customized-update';
```

#### check update

```javascript
  componentDidMount(){
      ReactNativeCustomizedUpdate.shouldApkUpdate()
  }
```

#### Request Object

| Property        | Type           | Description  |
| ------------- |:-------------:| :-----|
| cropping | bool (default false)      | Enable or disable cropping |
| width       | number(default 200) | Width of result image when used with `cropping` option |
| height      | number(default 200) | Height of result image when used with `cropping` option |
| multiple | bool (default false) | Enable or disable multiple image selection |
| isCamera | bool (default false) | Enable or disable camera selection |
| openCameraOnStart | bool (default false) | Enable or disable turn on the camera when it starts |
| maxSize  | number (default 9) | set image count |
| includeBase64 | bool (default false) | Enable or disable includeBase64 |
| compressQuality  | number([0-100]) | Picture compression ratio |
#### Response Object

| Property        | Type           | Description  |
| ------------- |:-------------:| :-----|
| path          | string | Selected image location |
| width      | number      | Selected image width |
| height | number      | Selected image height |
| mime | string | Selected image MIME type (image/jpeg, image/png) |
| size | number | Selected image size in bytes |
| data | base64 | Optional base64 selected file representation |

## Install

```
npm i react-native-customized-update --save
react-native link react-native-customized-update
```


## License
*MIT*
