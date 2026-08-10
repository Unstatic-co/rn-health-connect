package com.rnhealthconnect.records

import androidx.health.connect.client.aggregate.AggregationResult
import androidx.health.connect.client.aggregate.AggregationResultGroupedByDuration
import androidx.health.connect.client.aggregate.AggregationResultGroupedByPeriod
import androidx.health.connect.client.records.BloodGlucoseRecord
import androidx.health.connect.client.records.MealType.MEAL_TYPE_UNKNOWN
import androidx.health.connect.client.request.AggregateGroupByDurationRequest
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.units.BloodGlucose
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.bridge.WritableNativeMap
import com.rnhealthconnect.utils.AggregationNotSupported
import com.rnhealthconnect.utils.InvalidBloodGlucoseLevel
import com.rnhealthconnect.utils.convertMetadataFromJSMap
import com.rnhealthconnect.utils.convertMetadataToJSMap
import com.rnhealthconnect.utils.getSafeInt
import com.rnhealthconnect.utils.toMapList
import com.rnhealthconnect.utils.*
import java.time.Instant

class ReactBloodGlucoseRecord : ReactHealthRecordImpl<BloodGlucoseRecord> {

  override fun parseRecord(record: BloodGlucoseRecord): WritableNativeMap {
    return WritableNativeMap().apply {
      putString("time", record.time.toString())
      putMap("zoneOffset", zoneOffsetToJsMap(record.zoneOffset))
      putMap("level", bloodGlucoseToJsMap(record.level))
      putInt("specimenSource", record.specimenSource)
      putInt("mealType", record.mealType)
      putInt("relationToMeal", record.relationToMeal)
      putMap("metadata", convertMetadataToJSMap(record.metadata))
    }
  }

  override fun getAggregateRequest(record: ReadableMap): AggregateRequest {
    throw AggregationNotSupported()
  }

  override fun getAggregateGroupByDurationRequest(record: ReadableMap): AggregateGroupByDurationRequest {
    throw AggregationNotSupported()
  }

  override fun getAggregateGroupByPeriodRequest(record: ReadableMap): AggregateGroupByPeriodRequest {
    throw AggregationNotSupported()
  }

  override fun parseAggregationResultGroupedByDuration(record: List<AggregationResultGroupedByDuration>): WritableNativeArray {
    throw AggregationNotSupported()
  }

  override fun parseAggregationResult(record: AggregationResult): WritableNativeMap {
    throw AggregationNotSupported()
  }

  override fun parseAggregationResultGroupedByPeriod(record: List<AggregationResultGroupedByPeriod>): WritableNativeArray {
    throw AggregationNotSupported()
  }

  private fun bloodGlucoseToJsMap(bloodGlucose: BloodGlucose): WritableNativeMap {
    return WritableNativeMap().apply {
      putDouble("inMillimolesPerLiter", bloodGlucose.inMillimolesPerLiter)
      putDouble("inMilligramsPerDeciliter", bloodGlucose.inMilligramsPerDeciliter)
    }
  }

  private fun getBloodGlucoseFromJsMap(bloodGlucoseMap: ReadableMap?): BloodGlucose {
    if (bloodGlucoseMap == null) {
      throw InvalidBloodGlucoseLevel()
    }

    val value = bloodGlucoseMap.getDouble("value")
    return when (bloodGlucoseMap.getString("unit")) {
      "milligramsPerDeciliter" -> BloodGlucose.milligramsPerDeciliter(value)
      "millimolesPerLiter" -> BloodGlucose.millimolesPerLiter(value)
      else -> BloodGlucose.milligramsPerDeciliter(value)
    }
  }
}
