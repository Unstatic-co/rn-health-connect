import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';
import type {
  Permission,
  ReadHealthDataHistoryPermission,
} from './types';
import type { Location } from './types/base.types';

type ReadRecordsOptions = {
  startTime: string;
  endTime: string;
  dataOriginFilter?: string[];
  ascendingOrder?: boolean;
  pageSize?: number;
  pageToken?: string;
};

//@TODO: Fix and refactor types when codegen starts supporting type imports and generics
export interface Spec extends TurboModule {
  getSdkStatus(): Promise<number>;
  initialize(): Promise<boolean>;
  openHealthConnectSettings: () => void;
  openHealthConnectDataManagement: (providerPackageName?: string) => void;
  requestPermission(
    permissions: (
      | Permission
      | ReadHealthDataHistoryPermission
    )[]
  ): Promise<Permission[]>;
  requestExerciseRoute(recordId: string): Promise<Location[]>;
  getGrantedPermissions(): Promise<Permission[]>;
  revokeAllPermissions(): Promise<void>;
  readRecords(recordType: string, options: ReadRecordsOptions): Promise<{}>;
  readRecord(recordType: string, recordId: string): Promise<{}>;
  aggregateRecord(record: {
    recordType: string;
    startTime: string;
    endTime: string;
  }): Promise<{}>;
  aggregateGroupByDuration(record: {
    recordType: string;
    startTime: string;
    endTime: string;
    timeRangeSlicer: Object;
  }): Promise<[]>;
  aggregateGroupByPeriod(record: {
    recordType: string;
    startTime: string;
    endTime: string;
    timeRangeSlicer: Object;
  }): Promise<[]>;

  getChanges(request: {
    changesToken?: string;
    pageSize?: number;
    recordTypes?: string[];
    dataOriginFilters?: string[];
  }): Promise<{}>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('RnHealthConnect');
