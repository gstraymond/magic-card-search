package fr.gstraymond.android

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.magic.card.search.commons.log.Log

class LogActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
    private val log = Log(javaClass)
    override fun onActivityPaused(activity: Activity) =
            log.d("onActivityPaused: $activity")

    override fun onActivityStarted(activity: Activity) =
            log.d("onActivityStarted: $activity")

    override fun onActivityDestroyed(activity: Activity) =
            log.d("onActivityDestroyed: $activity")

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) =
            log.d("onActivitySaveInstanceState: $activity / $bundle")

    override fun onActivityStopped(activity: Activity) =
            log.d("onActivityStopped: $activity")

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) =
            log.d("onActivityCreated: $activity / $bundle")

    override fun onActivityResumed(activity: Activity) =
            log.d("onActivityResumed: $activity")
}