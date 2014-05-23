/*
 * Copyright 2014 Luke Korth
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lukekorth.ez_loaders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.LocalBroadcastManager;

public class EzLoader<T> extends AsyncTaskLoader<T> {

    private BroadcastReceiver mObserver;
    private String mBroadcastString;
    private boolean mUseLocalBroadcastManager;

    private EzLoaderInterface<T> mLoaderInterface;

    private T mT;

    /**
     * @param context
     * @param broadcastString The broadcast intent that the observer should
     *            listen for. Any time you are modifying data that is used by a
     *            loader somewhere in your application you should send a
     *            broadcast when it is updated so Loaders can refresh and your
     *            UI can update
     * @param loaderInterface The Activity or Fragment that implements
     *            EzLoaderInterface
     */
    public EzLoader(Context context, String broadcastString, EzLoaderInterface<T> loaderInterface) {
        super(context);
        mBroadcastString = broadcastString;
        mUseLocalBroadcastManager = false;
        mLoaderInterface = loaderInterface;
    }

    /**
     * @param context
     * @param broadcastString The broadcast intent that the observer should
     *            listen for. Any time you are modifying data that is used by a
     *            loader somewhere in your application you should send a
     *            broadcast when it is updated so Loaders can refresh and your
     *            UI can update
     * @param useLocalBroadcastManager A boolean indicating if EzLoader should
     *            use the LocalBroadcastManager
     * @param loaderInterface The Activity or Fragment that implements
     *            EzLoaderInterface
     */
    public EzLoader(Context context, String broadcastString, boolean useLocalBroadcastManager,
            EzLoaderInterface<T> loaderInterface) {
        super(context);
        mBroadcastString = broadcastString;
        mUseLocalBroadcastManager = useLocalBroadcastManager;
        mLoaderInterface = loaderInterface;
    }

    /**
     * This method is called on a background thread and should fetch your data
     * 
     * @return T
     */
    @Override
    public T loadInBackground() {
        return mLoaderInterface.loadInBackground(getId());
    }

    /**
     * Called when there is new data to deliver to the client. The superclass
     * will deliver it to the registered listener (i.e. the LoaderManager),
     * which will forward the results to the client through a call to
     * onLoadFinished.
     */
    @Override
    public void deliverResult(T t) {
        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the
            // data. This can happen when the Loader is reset while an
            // asynchronous query is working in the background. That is, when
            // the background thread finishes its work and attempts to deliver
            // the results to the client, it will see here that the Loader has
            // been reset and discard any resources associated with the new data
            // as necessary.
            if (t != null) {
                onReleaseResources(t);
                return;
            }
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // The old data may still be in use (i.e. bound to an adapter, etc.), so
        // we must protect it until the new data has been delivered.
        T oldT = mT;
        mT = t;

        if (isStarted()) {
            // If the Loader is in a started state, have the superclass deliver
            // the results to the client.
            super.deliverResult(t);
        }

        // Invalidate the old data as we don't need it any more.
        if (oldT != null && oldT != t) {
            onReleaseResources(oldT);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mT != null) {
            // Deliver any previously loaded data immediately.
            deliverResult(mT);
        }

        // Register the observers that will notify the Loader when changes are
        // made.
        if (mObserver == null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(mBroadcastString);

            mObserver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    EzLoader.this.onContentChanged();
                }
            };

            if (mUseLocalBroadcastManager)
                LocalBroadcastManager.getInstance(getContext()).registerReceiver(mObserver, filter);
            else
                getContext().registerReceiver(mObserver, filter);
        }

        if (takeContentChanged()) {
            // When the observer detects new data, it will call
            // onContentChanged() on the Loader, which will cause the next call
            // to takeContentChanged() to return true. If this is ever the case
            // (or if the current data is null), we force a new load.
            forceLoad();
        } else if (mT == null) {
            // If the current data is null... then we should make it non-null
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // The Loader has been put in a stopped state, so we should attempt to
        // cancel the current load (if there is one).
        cancelLoad();

        // Note that we leave the observer as is; Loaders in a stopped state
        // should still monitor the data source for changes so that the Loader
        // will know to force a new load if it is ever started again.
    }

    @Override
    protected void onReset() {
        // Ensure the loader is stopped.
        onStopLoading();

        // At this point we can release the resources
        if (mT != null) {
            onReleaseResources(mT);
            mT = null;
        }

        // The Loader is being reset, so we should stop monitoring for changes.
        if (mObserver != null) {
            if (mUseLocalBroadcastManager)
                LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mObserver);
            else
                getContext().unregisterReceiver(mObserver);
            mObserver = null;
        }
    }

    @Override
    public void onCanceled(T t) {
        // Attempt to cancel the current asynchronous load.
        super.onCanceled(t);

        // The load has been canceled, so we should release the resources
        // associated with 'mApps'.
        onReleaseResources(t);
    }

    @Override
    public void forceLoad() {
        super.forceLoad();
    }

    /**
     * Helper method to take care of releasing resources associated with an
     * actively loaded data set. For a simple List, there is nothing to do. For
     * something like a Cursor, we would close it in this method. All resources
     * associated with the Loader should be released here.
     */
    protected void onReleaseResources(T t) {
        mLoaderInterface.onReleaseResources(t);
    }

}
