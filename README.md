# CropMe

<img src="https://github.com/TakuSemba/CropMe/blob/master/arts/logo.png">

## Gradle

```groovy

dependencies {
    implementation 'com.github.takusemba:cropme:x.x.x'
}

```
<br/>

## Usage

<img src="https://github.com/TakuSemba/CropMe/blob/master/arts/crop.gif" align="right" width="30%">

![Platform](http://img.shields.io/badge/platform-android-green.svg?style=flat)
![Download](https://api.bintray.com/packages/takusemba/maven/cropme/images/download.svg)
![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)
![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)

This is an Android library for cropping images. Move images smoothly, and crop images precisely.

### Use CropView in your xml file.

```xml
  <com.takusemba.cropme.CropLayout
    android:id="@+id/crop_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:cropme_background_alpha="80%"
    app:cropme_frame_height_percent="80%"
    app:cropme_frame_width_percent="80%"
    app:cropme_max_scale="2.0"
    app:cropme_overlay_shape="rectangle"
    app:cropme_with_border="true"
    >
```

<br/>

### Set your image

```java
cropView.setUri(uri);
// or
cropView.setBitmap(bitmap);
// or
cropView.setDrawable(drawable);
```

### Crop it!

```java

cropView.isOffFrame(); // optionally check if the image is off of the frame.

cropView.crop(new OnCropListener() {
    @Override
    public void onSuccess(Bitmap bitmap) {
        // do something with Bitmap
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
| cropme_frame_width_percent | width of croppling frame | 80% |
| cropme_frame_height_percent | height of croppling frame | 80% |
| cropme_max_scale | maximum scale while cropping  | 2.0 |
| cropme_with_border | if borders are shown while cropping | true |
| cropme_background_alpha | background alpha out side of cropping area | 80% |
| cropme_overlay_shape | shape of croppling frame | rectangle / circle / custom |
| cropme_custom_shape_layout | custom layout for custom shape | @layout/custom_layout |

<br/>

## Custom Overlay

If you want to show a custom overlay, you can customize the Overlay by extending CropOverlay.

You can see more detail in [app](https://github.com/TakuSemba/CropMe/tree/master/app) module.

```java
class CustomCropOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    cropOverlayAttrs: AttributeSet? = attrs
) : CropOverlay(context, attrs, defStyleAttr, cropOverlayAttrs) {

  override fun drawBackground(canvas: Canvas, paint: Paint) {
    // draw background
  }

  override fun drawCrop(canvas: Canvas, paint: Paint) {
    // draw croppling frame
  }

  override fun drawBorder(canvas: Canvas, paint: Paint) {
    // draw borders
  }
}
```

## Sample
Clone this repo and check out the [app](https://github.com/TakuSemba/CropMe/tree/master/app) module.

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
