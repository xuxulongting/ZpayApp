package com.spreadtrum.iit.zpayapp.network;

import android.os.AsyncTask;

/**
 * Created by SPREADTRUM\ting.long on 16-11-4.
 */

public class DownAsyncTask extends AsyncTask<Void,Integer,Boolean> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            while (true) {
                int downloadPercent = 10;//doDownload();
                publishProgress(downloadPercent);
                if (downloadPercent >= 100) {
                    break;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }
}
