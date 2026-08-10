package com.rnhealthconnect.records

import androidx.health.connect.client.aggregate.AggregationResult
import androidx.health.connect.client.aggregate.AggregationResultGroupedByDuration
import androidx.health.connect.client.aggregate.AggregationResultGroupedByPeriod
import androidx.health.connect.client.records.BasalMetabolicRateRecord
import androidx.health.connect.client.request.AggregateGroupByDurationRequest
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.units.Power
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.bridge.WritableNativeMap
import com.rnhealthconnect.utils.InvalidPower
import com.rnhealthconnect.utils.convertDataOriginsToJsArray
import com.rnhealthconnect.utils.convertJsToDataOriginSet
import com.rnhealthconnect.utils.convertMetadataFromJSMap
import com.rnhealthconnect.utils.convertMetadataToJSMap
import com.rnhealthconnect.utils.getTimeRangeFilter
import com.rnhealthconnect.utils.mapJsDurationToDuration
import com.rnhealthconnect.utils.mapJsPeriodToPeriod
import com.rnhealthconnect.utils.toMapList
import com.rnhealthconnect.utils.*
import java.time.Instant

class ReactBasalMetabolicRateRecord : ReactHealthRecordImpl<BasalMetabolicRateRecord> {
  private val aggregateMetrics = setOf(BasalMetabolicRateRecord.BASAL_CALORIES_TOTAL)

  override fun parseRecord(record: BasalMetabolicRateRecord): WritableNativeMap {
    return WritableNativeMap().apply {
      putString("time", record.time.toString())
      putMap("zoneOffset", zoneOffsetToJsMap(record.zoneOffset))
      putMap("basalMetabolicRate", powerToJsMap(record.basalMetabolicRate))
      putMap("metadata", convertMetadataToJSMap(record.metadata))
    }
  }

  override fun getAggregateRequest(record: ReadableMap): AggregateRequest {
    return AggregateRequest(
      metrics = aggregateMetrics,
      timeRangeFilter = record.getTimeRangeFilter("timeRangeFilter"),
      dataOriginFilter = convertJsToDataOriginSet(record.getArray("dataOriginFilter"))
    )
  }

  override fun getAggregateGroupByDurationRequest(record: ReadableMap): AggregateGroupByDurationRequest {
    return AggregateGroupByDurationRequest(
      metrics = aggregateMetrics,
      timeRangeFilter = record.getTimeRangeFilter("timeRangeFilter"),
      timeRangeSlicer = mapJsDurationToDuration(record.getMap("timeRangeSlicer")),
      dataOriginFilter = convertJsToDataOriginSet(record.getArray("dataOriginFilter"))
    )
  }

  override fun getAggregateGroupByPeriodRequest(record: ReadableMap): AggregateGroupByPeriodRequest {
    return AggregateGroupByPeriodRequest(
      metrics = aggregateMetrics,
      timeRangeFilter = record.getTimeRangeFilterLocal("timeRangeFilter"),
      timeRangeSlicer = mapJsPeriodToPeriod(record.getMap("timeRangeSlicer")),
      dataOriginFilter = convertJsToDataOriginSet(record.getArray("dataOriginFilter"))
    )
  }

  override fun parseAggregationResultGroupedByDuration(record: List<AggregationResultGroupedByDuration>): WritableNativeArray {
    return WritableNativeArray().apply {
      record.forEach {
        val map = WritableNativeMap().apply {
          putMap("result", parseAggregationResult(it.result))
          putString("startTime", it.startTime.toString())
          putString("endTime", it.endTime.toString())
          putString("zoneOffset", it.zoneOffset.toString())
        }
        pushMap(map)
      }
    }
  }

  override fun parseAggregationResultGroupedByPeriod(record: List<AggregationResultGroupedByPeriod>): WritableNativeArray {
    return WritableNativeArray().apply {
      record.forEach {
        val map = WritableNativeMap().apply {
          putMap("result", parseAggregationResult(it.result))
          putString("startTime", it.startTime.toString())
          putString("endTime", it.endTime.toString())
        }
        pushMap(map)
      }
    }
  }

  override fun parseAggregationResult(record: AggregationResult): WritableNativeMap {
    return WritableNativeMap().apply {
      val map = WritableNativeMap().apply {
        putDouble(
          "inCalories",
          record[BasalMetabolicRateRecord.BASAL_CALORIES_TOTAL]?.inCalories ?: 0.0
        )
        putDouble(
          "inKilojoules",
          record[BasalMetabolicRateRecord.BASAL_CALORIES_TOTAL]?.inKilojoules ?: 0.0
        )
        putDouble(
          "inKilocalories",
          record[BasalMetabolicRateRecord.BASAL_CALORIES_TOTAL]?.inKilocalories ?: 0.0
        )
        putDouble(
          "inJoules",
          record[BasalMetabolicRateRecord.BASAL_CALORIES_TOTAL]?.inJoules ?: 0.0
        )
      }
      putMap("BASAL_CALORIES_TOTAL", map)
      putArray("dataOrigins", convertDataOriginsToJsArray(record.dataOrigins))
    }
  }

  private fun powerToJsMap(power: Power): WritableNativeMap {
    return WritableNativeMap().apply {
      putDouble("inKilocaloriesPerDay", power.inKilocaloriesPerDay)
      putDouble("inWatts", power.inWatts)
    }
  }

  private fun getPowerFromJsMap(powerMap: ReadableMap?): Power {
    if (powerMap == null) {
      throw InvalidPower()
    }

    val value = powerMap.getDouble("value")
    return when (powerMap.getString("unit")) {
      "kilocaloriesPerDay" -> Power.kilocaloriesPerDay(value)
      "watts" -> Power.watts(value)
      else -> Power.kilocaloriesPerDay(value)
    }
  }
}
