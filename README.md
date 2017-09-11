# CropMe

<img src="https://github.com/TakuSemba/CropMe/blob/master/arts/logo.png">

## Gradle

```groovy

dependencies {
    compile 'com.github.takusemba:cropme:1.0.2'
}

```
<br/>

<img src="https://github.com/TakuSemba/CropMe/blob/master/arts/crop.gif" align="right" width="300">

## Features

![Platform](http://img.shields.io/badge/platform-android-green.svg?style=flat)
![Download](https://api.bintray.com/packages/takusemba/maven/multisnaprecyclerview/images/download.svg)
![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)
![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)

This is an Android library for cropping images.
Move images smoothly, and crop images precisely.
Enjoy cropping!
<br/>
- [x] fling animation.
- [x] moveing and scaleing.
- [x] interface to crop image.
- [x] smaple code.
- [ ] rotation and tilting
- [ ] test code

<br/>
<br/>
<br/>
<br/>

<img src="https://github.com/TakuSemba/CropMe/blob/master/arts/snap_count.gif" align="left" width="300">

## Usage
##### Use CropView in your xml file.

```xml
<com.takusemba.cropme.CropView
    android:id="@+id/crop_view"
    android:layout_width="match_parent"
    android:layout_height="0dp"
    android:layout_weight="2"
    app:cropme_background_alpha="80%"
    app:cropme_max_scale="3"
    app:cropme_result_height="80%"
    app:cropme_result_width="80%"
    app:cropme_with_border="true" />

```

##### Set your image

```java
cropView.setUri(uri);
// or
cropView.setBitmap(bitmap);
```

##### Crop it!

```java
cropView.crop(new OnCropListener() {
    @Override
    public void onSuccess(Bitmap bitmap) {
        // do something
    }

    @Override
    public void onFailure() {
        
    }
});
```

<br/>

## Attributes

| attribute | description | default |
|:---|:---|:---|
| cropme_result_width | width of propping area in CropView | 80% |
| cropme_result_height | height propping area in CropView | 80% |
| cropme_max_scale | max scale | 2 |
| cropme_with_border | true if white borders are shown while cropping | true |
| cropme_background_alpha | background alpha out side of propping area | 80% |

<br/>

## Sample
Clone this repo and check out the [app](https://github.com/TakuSemba/MultiSnapRecyclerView/tree/master/app) module.

## Change Log

### Version: 1.0.0

  * Initial Build


## Author

* **Taku Semba**
    * **Github** - (https://github.com/takusemba)
    * **Twitter** - (https://twitter.com/takusemba)
    * **Facebook** - (https://www.facebook.com/takusemba)

## Licence
```
Copyright 2017 Taku Semba.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
