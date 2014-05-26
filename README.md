EzLoaders
===============================================
Making loaders quick and easy.

EzLoaders borrows heavily from [these posts written by Alex Lockwood](http://www.androiddesignpatterns.com/2012/07/loaders-and-loadermanager-background.html)
If you are not familiar with Loaders please read the [four posts Alex has written on them](http://www.androiddesignpatterns.com/2012/07/loaders-and-loadermanager-background.html)

Additional information about Loaders can be found in the [Android documentation](http://developer.android.com/guide/components/loaders.html)

Support
-------
EzLoaders is compatible with Android 1.6 - 4.2 thanks to the
Android Compatibility Library.

Usage
-----

### Adding the code

EzLoaders is available on maven central. 

For those using gradle, simply add the following to your build.gradle

```groovy
dependencies {
    compile 'com.lukekorth:ez-loaders:1.2'
}
```

Users of maven can add the following to their pom

```xml
<dependency>
    <groupId>com.lukekorth</groupId>
    <artifactId>ez-loaders</artifactId>
    <version>1.2</version>
</dependency>
```

### Using the code
When you want to make use of a Loader, your Activity or Fragment should
implement EzLoaderInterface<T>.

To start the loader, in `onCreate` your Activity should call `initLoader`.

Your `LOADER_ID` can be any value, it is returned to you in `onCreateLoader`
and `loadInBackground` as `id` and `onLoadFinished` and `onLoaderReset` as
`loader.getId()`. `LOADER_ID` is mainly used to branch on when multiple loaders
are used in one Activity, you will not need to worry about it if you are only
using a single loader.

You can pass a `Bundle` of args to `initLoader` which can be used in `onCreateLoader`.

```java
// on API 11+
getLoaderManager().initLoader(LOADER_ID, null, this)

// on API < 11
getSupportLoaderManager().initLoader(LOADER_ID, null, this)
```

```java
@Override
public Loader<T> onCreateLoader(int id, Bundle args) {
    // com.lukekorth.REFRESH is the broadcast action that will cause
    // the loader to refresh it's data
    return new EzLoader<T>(this, "com.lukekorth.REFRESH", this);
}

@Override
public void onLoadFinished(Loader<T> loader, T data) {
    mAdapter.setData(data);
}

@Override
public void onLoaderReset(Loader<T> loader) {
    mAdapter.setData(null);
}

@Override
public T loadInBackground(int id) {
    //fetch and return your data here, this will be passed to your adapter
}

@Override
public void onReleaseResources(List<Thread> t) {
    //release any resources you may have used to query data in loadInBackground()
}
```


Dependencies
------------
This project depends on the Android Compatibility Library included via gradle

Demo
----
Coming soon!

License
-------
The code in this project is licensed under the Apache
Software License 2.0, for details see the included LICENSE
file.

Issues
---------
If you have encountered a bug, please post an [issue](https://github.com/lkorth/ez-loaders/issues).
