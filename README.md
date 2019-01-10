ConnectableActivity
=========
[![GitHub license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/titanium-codes/ConnectableActivity/blob/master/LICENSE)
[_![Download](https://api.bintray.com/packages/titanium-codes/Android/connectableactivity/images/download.svg) ](https://bintray.com/titanium-codes/Android/connectableactivity/_latestVersion)

Overview
--------
Extension function for attaching activity to any context.

Install
-------
#### Gradle

**Step 1.** Add it in your root build.gradle at the end of repositories:
```
allprojects {
    repositories {
        jcenter()
    }
}
```  
**Step 2.** Add the dependency

```
dependencies {
    implementation 'codes.titanium:connectableactivity:1.0.3'
}
```  

When I should use it?
---

* You want to ask permission , but you have only app context
* You want to launch activity for result , but you have only app context
* You want to show dialog, but ... you have only app context
* You want to wrap calls to onActivityResult and onPermissionsResult with RxJava 

How it can help me?
---

Some business case. You need to show same dialog for every network error, instead of creating base activity with same logic, 
you can handle it somewhere in http layer, in one place for all network errors using only app context, without any risk of memory leaks.
Same with asking permissions. Everywhere where you need activity - you can safely launch ConnectableActivity, get some profit from activity context and shut it down
without any changes in default behavior(in fact this activity is invisible to user), but with much more control(extending on activity result can be painful, especially if logic should be reused)

Example
---

```
//Function that accepts activity and requesting permission from it
val permRequest: (Activity) -> Unit = { it.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1) }
//As soon as request permission result will be called -> you will be here
val permResult: (Boolean) -> Unit = { isGranted ->
    if (isGranted) {
        //do some logic on permissions granted
    } else {
        //do some logic on permissions declined
    }
}
//launching connectable activity from app context
appContext.launchConnectableActivity(permRequest, onRequestPermissionsResult = permResult)
```

Notes
---
1. As soon as onActivityResult or onRequestPermissionResult was called - activity will be finished with no UI using finishWithNoAnimation method defined in ConnectableActivity,
so in case if you want to do another logic with this activity - you will still need to finish it, i suggest using finishWithNoAnimation for consistency, but feel free to do it in any way. 
2. You can also add onDeAttach function(by default doing nothing) that is called as soon as connectableActivity is destroyed

Release notes
-------------

### 1.0.3
> * Fixed result code
> * Fixed invisible theme
> * Util method to finish ConnectableActivity with no animation

### 1.0.0
> * Initial release
