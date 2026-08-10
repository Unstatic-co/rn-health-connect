package com.rnhealthconnect

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap

class RnHealthConnectModule(reactContext: ReactApplicationContext) :
  NativeRnHealthConnectSpec(reactContext) {
  private val clientManager = HealthConnectClientManager(reactContext)
  override fun getSdkStatus(
    promise: Promise
  ) {
  return clientManager.getSdkStatus( promise)
  }

  override fun initialize(
    promise: Promise
  ) {
    clientManager.initialize(promise)
  }

  override fun openHealthConnectSettings() {
    clientManager.openHealthConnectSettings()
  }

  override fun openHealthConnectDataManagement(providerPackageName: String?) {
    clientManager.openHealthConnectDataManagement(providerPackageName)
  }

  override fun requestPermission(
    permissions: ReadableArray,
    promise: Promise
  ) {
    clientManager.requestPermission(permissions, promise)

  }

  override fun requestExerciseRoute(
    recordId: String,
    promise: Promise
  ) {
    clientManager.requestExerciseRoute(recordId,promise)
  }

  override fun getGrantedPermissions(promise: Promise) {
    clientManager.getGrantedPermissions(promise)
  }

  override fun revokeAllPermissions(promise: Promise) {
    clientManager.revokeAllPermissions(promise)
  }

  override fun readRecords(
    recordType: String,
    options: ReadableMap,
    promise: Promise
  ) {
    clientManager.readRecords(recordType, options, promise)
  }

  override fun readRecord(
    recordType: String,
    recordId: String,
    promise: Promise
  ) {
    clientManager.readRecord(recordType, recordId, promise)
  }

  override fun aggregateRecord(
    record: ReadableMap,
    promise: Promise
  ) {
    clientManager.aggregateRecord(record, promise)
  }

  override fun aggregateGroupByDuration(
    record: ReadableMap,
    promise: Promise
  ) {
    clientManager.aggregateGroupByDuration(record, promise)
  }

  override fun aggregateGroupByPeriod(
    record: ReadableMap,
    promise: Promise
  ) {
    clientManager.aggregateGroupByPeriod(record, promise)
  }

  override fun getChanges(
    request: ReadableMap,
    promise: Promise
  ) {
    clientManager.getChanges(request, promise)
  }

  companion object {
    const val NAME = NativeRnHealthConnectSpec.NAME
  }
}
