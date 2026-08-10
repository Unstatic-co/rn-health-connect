package com.rnhealthconnect

import com.facebook.react.bridge.ReactApplicationContext

class RnHealthConnectModule(reactContext: ReactApplicationContext) :
  NativeRnHealthConnectSpec(reactContext) {

  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }

  companion object {
    const val NAME = NativeRnHealthConnectSpec.NAME
  }
}
