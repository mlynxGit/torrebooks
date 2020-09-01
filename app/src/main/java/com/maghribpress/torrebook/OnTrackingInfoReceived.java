package com.maghribpress.torrebook;

import com.maghribpress.torrebook.db.entity.BookTracking;

public interface OnTrackingInfoReceived {
    void TrackingInfoReceived(BookTracking bookTracking);
}
