package com.spreadtrum.iit.zpayapp.PullToRefreshLayoutTellH;

import android.content.Context;

/**
 * Created by SPREADTRUM\ting.long on 16-10-26.
 */

public class Utils {

    private Utils() {}

    public static int convertDpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

}
