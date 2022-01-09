package com.company.android.myapplication;

import android.net.Uri;

public class Slider {
    private Uri url;

    public Slider()
    {

    }

    public Slider(android.net.Uri url) {
        this.url = url;
    }

    public Uri getUrl() {
        return url;
    }

    public void setUrl(Uri url) {
        this.url = url;
    }
}
