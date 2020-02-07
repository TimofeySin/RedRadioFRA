/**
 * CollectionViewModel.java
 * Implements the CollectionViewModel class
 * A CollectionViewModel stores the Collection of stations in a ViewModel
 *
 * This file is part of
 * TRANSISTOR - Radio App for Android
 *
 * Copyright (c) 2015-20 - Y20K.org
 * Licensed under the MIT-License
 * http://opensource.org/licenses/MIT
 */

package ru.rpw.radio.collection;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import ru.rpw.radio.core.Station;
import ru.rpw.radio.helpers.TransistorKeys;

/**
 * CollectionViewModel.class
 */
public class CollectionViewModel extends AndroidViewModel implements TransistorKeys {

    /* Define log tag */
    private static final String LOG_TAG = CollectionViewModel.class.getSimpleName();


    /* Main class variables */
    private final MutableLiveData<Station> mPlayerServiceStationLiveData;

    /* Constructor */
    public CollectionViewModel(Application application) {
        super(application);

        // initialize LiveData
        mPlayerServiceStationLiveData = new MutableLiveData<Station>();

        // set station from PlayerService to null
        mPlayerServiceStationLiveData.setValue(null);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
    }


    /* Getter for station from PlayerService */
    public MutableLiveData<Station> getPlayerServiceStation() {
        return mPlayerServiceStationLiveData;
    }
}
