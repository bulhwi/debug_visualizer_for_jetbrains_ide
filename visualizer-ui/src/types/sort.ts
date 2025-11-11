/**
 * 정렬 알고리즘 시각화 타입 정의
 * @see docs/sort-visualization-schema.md
 */

export interface SortVisualizationData {
  kind: 'sort'
  timestamp: number
  metadata: {
    language: string
    expression: string
    type: string
    lineNumber?: number
    fileName?: string
  }
  data: {
    snapshots: SortSnapshot[]
    algorithm?: SortAlgorithm
  }
  config?: {
    animation?: boolean
    speed?: number
  }
}

export interface SortSnapshot {
  array: number[]
  comparing?: number[]
  swapping?: number[]
  sorted?: number[]
  pivot?: number
  partitions?: Partition[]
  action?: SortAction
  description?: string
}

export interface Partition {
  start: number
  end: number
  label?: string
  color?: string
}

export type SortAlgorithm = 'bubble' | 'quick' | 'merge' | 'insertion' | 'selection' | 'heap'

export type SortAction = 'compare' | 'swap' | 'merge' | 'partition' | 'sorted'

export enum SortVisualizationColor {
  DEFAULT = '#4ecdc4',
  COMPARING = '#ff922b',
  SWAPPING = '#ff6b6b',
  SORTED = '#51cf66',
  PIVOT = '#9775fa',
  PARTITION_LEFT = '#ffd43b',
  PARTITION_RIGHT = '#74c0fc',
  MERGING = '#ff8787'
}
