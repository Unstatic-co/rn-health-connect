import { NativeModules, Platform } from 'react-native';
import type {
  AggregateRequest,
  AggregateResult,
  AggregateGroupByDurationRequest,
  AggregateGroupByPeriodRequest,
  AggregationGroupResult,
  AggregateResultRecordType,
  Permission,
  ReadRecordsOptions,
  RecordResult,
  RecordType,
  ReadRecordsResult,
  GetChangesRequest,
  GetChangesResults,
  ReadHealthDataHistoryPermission,
  BackgroundAccessPermission,
  RevokeAllPermissionsResponse,
} from './types';
import type { Location } from './types/base.types';

const LINKING_ERROR =
  `The package 'react-native-health-connect' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const PLATFORM_NOT_SUPPORTED_ERROR = `Platform not supported. This package only supports Android.`;

// @ts-expect-error
const isTurboModuleEnabled = global.__turboModuleProxy != null;

const moduleProxy = (message: string) =>
  new Proxy(
    {},
    {
      get() {
        throw new Error(message);
      },
    }
  );

const HealthConnectModule = Platform.select({
  android: isTurboModuleEnabled
    ? require('./NativeHealthConnect').default
    : NativeModules.HealthConnect,
  ios: moduleProxy(PLATFORM_NOT_SUPPORTED_ERROR),
  default: moduleProxy(PLATFORM_NOT_SUPPORTED_ERROR),
});

const HealthConnect = HealthConnectModule
  ? HealthConnectModule
  : moduleProxy(LINKING_ERROR);


/**
 * Gets the status of the Health Connect SDK
 * @param providerPackageName the package name of the Health Connect provider
 * @returns the status of the SDK - check SdkAvailabilityStatus constants
 */
export function getSdkStatus(
): Promise<number> {
  return HealthConnect.getSdkStatus();
}

/**
 * Initializes the Health Connect SDK
 * @param providerPackageName the package name of the Health Connect provider
 * @returns true if the SDK was initialized successfully
 */
export function initialize(
): Promise<boolean> {
  return HealthConnect.initialize();
}

/**
 * Opens Health Connect settings app
 */
export function openHealthConnectSettings(): void {
  return HealthConnect.openHealthConnectSettings();
}

/**
 * Opens Health Connect data management screen
 */
export function openHealthConnectDataManagement(
  providerPackageName?: string
): void {
  return HealthConnect.openHealthConnectDataManagement(providerPackageName);
}

/**
 * Request permissions to access Health Connect data
 * @param permissions list of permissions to request
 * @returns granted permissions, including special permissions like WriteExerciseRoutePermission and BackgroundAccessPermission
 */
export function requestPermission(
  permissions: (
    | Permission
    | BackgroundAccessPermission
    | ReadHealthDataHistoryPermission
  )[]
): Promise<
  (
    | Permission
    | ReadHealthDataHistoryPermission
    | BackgroundAccessPermission
  )[]
> {
  return HealthConnect.requestPermission(permissions);
}

export function requestExerciseRoute(recordId: string): Promise<Location[]> {
  return HealthConnect.requestExerciseRoute(recordId);
}

/**
 * Returns a set of all health permissions granted by the user to the calling app.
 * This includes regular permissions as well as special permissions like WriteExerciseRoutePermission and BackgroundAccessPermission.
 *
 * If your app called {@link revokeAllPermissions}, this keeps returning the revoked permissions
 * until the app process restarts. See {@link revokeAllPermissions} for details.
 *
 * @returns A promise that resolves to an array of granted permissions
 */
export function getGrantedPermissions(): Promise<
  (Permission | BackgroundAccessPermission)[]
> {
  return HealthConnect.getGrantedPermissions();
}

/**
 * Revokes all previously granted permissions by the user to the calling app.
 *
 * The revocation does not take effect until the app process restarts. This is a documented
 * Health Connect platform limitation, not a bug in this library. Until the restart:
 * - {@link getGrantedPermissions} keeps returning the revoked permissions.
 * - Reads and writes keep succeeding, and written data really is persisted.
 *
 * Because of this, do not use this as the mechanism behind an in-app "disconnect from Health
 * Connect" toggle. Prefer sending the user to Health Connect via {@link openHealthConnectSettings},
 * track the disconnected state in your own app and stop syncing yourself, and if you do call this,
 * tell the user the change takes effect after restarting the app.
 *
 * @see https://developer.android.com/health-and-fitness/health-connect/ui/permissions#sync-with-health-connect
 * @returns A promise that resolves to a RevokeAllPermissionsResponse object containing information about the revocation status,
 * or void for backward compatibility with older versions
 */
export function revokeAllPermissions(): Promise<RevokeAllPermissionsResponse | void> {
  return HealthConnect.revokeAllPermissions();
}

export function readRecords<T extends RecordType>(
  recordType: T,
  options: ReadRecordsOptions
): Promise<ReadRecordsResult<T>> {
  return HealthConnect.readRecords(recordType, options);
}

export function readRecord<T extends RecordType>(
  recordType: T,
  recordId: string
): Promise<RecordResult<T>> {
  return HealthConnect.readRecord(recordType, recordId);
}

export function aggregateRecord<T extends AggregateResultRecordType>(
  request: AggregateRequest<T>
): Promise<AggregateResult<T>> {
  return HealthConnect.aggregateRecord(request);
}

export function aggregateGroupByDuration<T extends AggregateResultRecordType>(
  request: AggregateGroupByDurationRequest<T>
): Promise<AggregationGroupResult<T>[]> {
  return HealthConnect.aggregateGroupByDuration(request);
}

export function aggregateGroupByPeriod<T extends AggregateResultRecordType>(
  request: AggregateGroupByPeriodRequest<T>
): Promise<AggregationGroupResult<T>[]> {
  return HealthConnect.aggregateGroupByPeriod(request);
}

export function getChanges(
  request: GetChangesRequest
): Promise<GetChangesResults> {
  return HealthConnect.getChanges(request);
}

export * from './constants';
export * from './types';
