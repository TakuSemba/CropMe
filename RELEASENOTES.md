Release notes
==========

Version 2.0.4 **(2019-11-8)**
----------------------------
 - Simplify animators.
 - Add test code

Version 2.0.3 **(2019-11-5)**
----------------------------
 - Set up CI environment.

Version 2.0.2 **(2019-11-4)**
----------------------------
 - Call requestLayout once a frame size is settled. This will set a image properly when a image is set before CropLayout is laid out. 

Version 2.0.1 **(2019-11-4)**
----------------------------
 - Refactor `HorizontalAnimatorImpl.kt`、`VerticalAnimatorImpl.kt` and `ScaleAnimatorImpl`.
 - Add more comments to classes.

Version 2.0.0 **(2019-11-4)**
----------------------------
 - Migrate to AndroidX. After this release, your app have to be migrated to AndroidX.
 - Support **Circle** frame by default. You can use a circle frame by setting `app:cropme_overlay_shape="circle"`
 - Add new attributes `cropme_overlay_shape`、`cropme_custom_shape_layout`.
 - Add samples for Rectangle / Circle / Custom frame. Check out the new [app](https://github.com/TakuSemba/CropMe/tree/master/app) module.
 - Upgrade compileSdkVersion and targetSdkVersion to 28.
 - Require Kotlin v1.3.50.

Version 1.0.0 **(2017-9-7)**
----------------------------
 - Initial Release