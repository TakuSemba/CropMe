## Release notes

### 2.0.2
 - Call requestLayout once a frame size is settled. This will set a image properly when a image is set before CropLayout is laid out. 

### 2.0.1
 - Refactor `HorizontalAnimatorImpl.kt`、`VerticalAnimatorImpl.kt` and `ScaleAnimatorImpl`.
 - Add more comments to classes.

### 2.0.0

 - Migrate to AndroidX. After this release, your app have to be migrated to AndroidX.
 - Support **Circle** frame by default. You can use a circle frame by setting `app:cropme_overlay_shape="circle"`
 - Add new attributes `cropme_overlay_shape`、`cropme_custom_shape_layout`.
 - Add samples for Rectangle / Circle / Custom frame. Check out the new [app](https://github.com/TakuSemba/CropMe/tree/master/app) module.
 - Upgrade compileSdkVersion and targetSdkVersion to 28.
 - Require Kotlin v1.3.50.

### 1.0.0

 - Initial Release