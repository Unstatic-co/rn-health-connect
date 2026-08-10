package com.rnhealthconnect.records

import androidx.health.connect.client.aggregate.AggregationResult
import androidx.health.connect.client.aggregate.AggregationResultGroupedByDuration
import androidx.health.connect.client.aggregate.AggregationResultGroupedByPeriod
import androidx.health.connect.client.records.Vo2MaxRecord
import androidx.health.connect.client.request.AggregateGroupByDurationRequest
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.AggregateRequest
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.bridge.WritableNativeMap
import com.rnhealthconnect.utils.AggregationNotSupported
import com.rnhealthconnect.utils.convertMetadataFromJSMap
import com.rnhealthconnect.utils.convertMetadataToJSMap
import com.rnhealthconnect.utils.getSafeInt
import com.rnhealthconnect.utils.toMapList
import com.rnhealthconnect.utils.*
import java.time.Instant

class ReactVo2MaxRecord : ReactHealthRecordImpl<Vo2MaxRecord> {

  override fun parseRecord(record: Vo2MaxRecord): WritableNativeMap {
    return WritableNativeMap().apply {
      putString("time", record.time.toString())
      putMap("zoneOffset", zoneOffsetToJsMap(record.zoneOffset))
      putDouble("vo2MillilitersPerMinuteKilogram", record.vo2MillilitersPerMinuteKilogram)
      putInt("measurementMethod", record.measurementMethod)
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

  override fun parseAggregationResult(record: AggregationResult): WritableNativeMap {
    throw AggregationNotSupported()
  }

  override fun parseAggregationResultGroupedByDuration(record: List<AggregationResultGroupedByDuration>): WritableNativeArray {
    throw AggregationNotSupported()
  }

  override fun parseAggregationResultGroupedByPeriod(record: List<AggregationResultGroupedByPeriod>): WritableNativeArray {
    throw AggregationNotSupported()
  }
}
