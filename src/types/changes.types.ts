import type { RecordType } from './records.types';
import type { HealthConnectRecordResult } from './results.types';

export interface GetChangesRequest {
  changesToken?: string;
  pageSize?: number;
  recordTypes?: RecordType[];
  dataOriginFilter?: string[];
}

export interface GetChangesResults {
  upsertionChanges: Array<{ record: HealthConnectRecordResult }>;
  deletionChanges: Array<{ recordId: string }>;
  nextChangesToken: string;
  changesTokenExpired: boolean;
  hasMore: boolean;
}
