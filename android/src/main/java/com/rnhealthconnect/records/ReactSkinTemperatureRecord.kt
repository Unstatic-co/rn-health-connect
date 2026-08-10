package com.rnhealthconnect.records

import androidx.health.connect.client.aggregate.AggregationResult
import androidx.health.connect.client.aggregate.AggregationResultGroupedByDuration
import androidx.health.connect.client.aggregate.AggregationResultGroupedByPeriod
import androidx.health.connect.client.records.SkinTemperatureRecord
import androidx.health.connect.client.request.AggregateGroupByDurationRequest
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.units.Temperature
import androidx.health.connect.client.units.TemperatureDelta
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.bridge.WritableNativeMap
import com.rnhealthconnect.utils.AggregationNotSupported
import com.rnhealthconnect.utils.InvalidTemperature
import com.rnhealthconnect.utils.convertMetadataFromJSMap
import com.rnhealthconnect.utils.convertMetadataToJSMap
import com.rnhealthconnect.utils.getSafeInt
import com.rnhealthconnect.utils.toMapList
import com.rnhealthconnect.utils.*
import java.time.Instant

class ReactSkinTemperatureRecord : ReactHealthRecordImpl<SkinTemperatureRecord> {

  override fun parseRecord(record: SkinTemperatureRecord): WritableNativeMap {
    return WritableNativeMap().apply {
      putString("startTime", record.startTime.toString())
      putMap("startZoneOffset", zoneOffsetToJsMap(record.startZoneOffset))
      putString("endTime", record.endTime.toString())
      putMap("endZoneOffset", zoneOffsetToJsMap(record.endZoneOffset))
      putMap("baseline", record.baseline?.let { temperatureToJsMap(it) })
      putInt("measurementLocation", record.measurementLocation)
      val array = WritableNativeArray().apply {
        record.deltas.map {
          val map = WritableNativeMap()
          map.putString("time", it.time.toString())
          map.putMap("delta", temperatureDeltaToJsMap(it.delta))
          this.pushMap(map)
        }
      }
      putArray("deltas", array)
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

  private fun getTemperatureFromJsMap(temperatureMap: ReadableMap?): Temperature {
    if (temperatureMap == null) {
      throw InvalidTemperature()
    }

    val value = temperatureMap.getDouble("value")
    return when (temperatureMap.getString("unit")) {
      "fahrenheit" -> Temperature.fahrenheit(value)
      "celsius" -> Temperature.celsius(value)
      else -> Temperature.celsius(value)
    }
  }

  private fun temperatureToJsMap(temperature: Temperature): WritableNativeMap {
    return WritableNativeMap().apply {
      putDouble("inFahrenheit", temperature.inFahrenheit)
      putDouble("inCelsius", temperature.inCelsius)
    }
  }

  private fun getTemperatureDeltaFromJsMap(temperatureDeltaMap: ReadableMap?): TemperatureDelta {
    if (temperatureDeltaMap == null) {
      throw InvalidTemperature()
    }

    val value = temperatureDeltaMap.getDouble("value")
    return when (temperatureDeltaMap.getString("unit")) {
      "fahrenheit" -> TemperatureDelta.fahrenheit(value)
      "celsius" -> TemperatureDelta.celsius(value)
      else -> TemperatureDelta.celsius(value)
    }
  }

  private fun temperatureDeltaToJsMap(temperatureDelta: TemperatureDelta): WritableNativeMap {
    return WritableNativeMap().apply {
      putDouble("inFahrenheit", temperatureDelta.inFahrenheit)
      putDouble("inCelsius", temperatureDelta.inCelsius)
    }
  }
}
