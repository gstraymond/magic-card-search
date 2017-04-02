package fr.gstraymond.biz

import android.os.AsyncTask
import fr.gstraymond.android.SplashScreenActivity
import fr.gstraymond.models.search.response.SearchResult
import fr.gstraymond.utils.app

class SplashProcessor(private val activity: SplashScreenActivity,
                      private val options: SearchOptions) : AsyncTask<Void, Int, SearchResult>() {

    override fun doInBackground(vararg params: Void) =
            activity.app().elasticSearchClient.process(options)

    override fun onPostExecute(result: SearchResult) {
        activity.startNextActivity(result)
    }
}
