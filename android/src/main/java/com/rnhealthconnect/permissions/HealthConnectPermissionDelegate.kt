package com.rnhealthconnect.permissions

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.contracts.ExerciseRouteRequestContract
import androidx.health.connect.client.records.ExerciseRoute
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object HealthConnectPermissionDelegate {
  private class Launchers(
    val requestPermission: ActivityResultLauncher<Set<String>>,
    val requestRoutePermission: ActivityResultLauncher<String>
  )

  private val coroutineScope = CoroutineScope(Dispatchers.IO)
  private val permissionsChannel = Channel<Set<String>>()
  private val exerciseRouteChannel = Channel<ExerciseRoute?>()

  // One entry per live activity, most recently created last. An activity's launchers are
  // unregistered when it is destroyed, so requests must fall back to the previous activity.
  // Only accessed on the main thread.
  private val registeredLaunchers = ArrayDeque<Launchers>()

  fun setPermissionDelegate(
    activity: ComponentActivity,
    providerPackageName: String = "com.google.android.apps.healthdata"
  ) {
    val contract = PermissionController.createRequestPermissionResultContract(providerPackageName)
    val exerciseRouteRequestContract = ExerciseRouteRequestContract()

    val launchers = Launchers(
      requestPermission = activity.registerForActivityResult(contract) {
        coroutineScope.launch {
          permissionsChannel.send(it)
          coroutineContext.cancel()
        }
      },
      requestRoutePermission = activity.registerForActivityResult(exerciseRouteRequestContract) {
        coroutineScope.launch {
          exerciseRouteChannel.send(it)
          coroutineContext.cancel()
        }
      }
    )

    registeredLaunchers.addLast(launchers)
    activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
      override fun onDestroy(owner: LifecycleOwner) {
        registeredLaunchers.remove(launchers)
      }
    })
  }

  /** Returns an empty set, meaning nothing was granted, if no activity can show the dialog. */
  suspend fun launchPermissionsDialog(permissions: Set<String>): Set<String> {
    val launched = withContext(Dispatchers.Main.immediate) {
      val launcher = registeredLaunchers.lastOrNull()?.requestPermission
      launcher?.launch(permissions)
      launcher != null
    }
    return if (launched) permissionsChannel.receive() else emptySet()
  }

  /** Returns null if no activity can show the dialog. */
  suspend fun launchExerciseRouteAccessRequestDialog(recordId: String): ExerciseRoute? {
    val launched = withContext(Dispatchers.Main.immediate) {
      val launcher = registeredLaunchers.lastOrNull()?.requestRoutePermission
      launcher?.launch(recordId)
      launcher != null
    }
    return if (launched) exerciseRouteChannel.receive() else null
  }
}
