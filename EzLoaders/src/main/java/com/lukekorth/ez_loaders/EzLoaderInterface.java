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

import android.app.LoaderManager;

/**
 * This interface should be implemented by the Activity or Fragment making use
 * of Loaders.
 */
public interface EzLoaderInterface<T> extends LoaderManager.LoaderCallbacks<T> {

    /**
     * Called on a background thread to fetch data from a database or network request.
     * 
     * @return T
     */
    public T loadInBackground(int id);

    /**
     * Helper method to take care of releasing resources associated with an
     * actively loaded data set. For a simple List, there is nothing to do. For
     * something like a Cursor, we would close it in this method. All resources
     * associated with the Loader should be released here.
     */
    public void onReleaseResources(T t);
}
