EzLoaders: Making loaders quick and easy
===============================================

EzLoaders borrows heavily from [these posts written by Alex Lockwood](http://www.androiddesignpatterns.com/2012/07/loaders-and-loadermanager-background.html)
If you are not familiar with Loaders please read the [four posts Alex has written on them](http://www.androiddesignpatterns.com/2012/07/loaders-and-loadermanager-background.html)

Support
-------
EzLoaders is compatible with Android 1.6 - 4.1 thanks to the
Android Compatibility Library.

Usage
-----

### Adding the code
EzLoaders is available in source form and as a JAR in the downloads
section. Simply place the JAR in your Android projects libs folder or
import the source and add your target version of Android as an external 
JAR for the project.

### Using the code
When you want to make use of a Loader, your Activity or Fragment should
implement EzLoaderInterface<T>.

```java
@Override
public Loader<T> onCreateLoader(int arg0, Bundle args) {
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
public T loadInBackground() {
	//fetch and return your data here, this will be passed to your adapter
}

@Override
public void onReleaseResources(List<Thread> t) {
	//release any resources you may have used to query data in loadInBackground()
}
```


Dependencies
------------
This project depends on the Android Compatibility Library
(android-support-v4.jar)

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