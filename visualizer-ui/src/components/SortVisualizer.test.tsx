import { describe, it, expect, beforeEach, vi } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { SortVisualizer } from './SortVisualizer'
import type { SortVisualizationData, SortSnapshot } from '../types/sort'

describe('SortVisualizer', () => {
  const createMockData = (snapshots: SortSnapshot[]): SortVisualizationData => ({
    kind: 'sort',
    timestamp: Date.now(),
    metadata: {
      language: 'java',
      expression: 'arr',
      type: 'int[]'
    },
    data: {
      snapshots,
      algorithm: 'bubble'
    }
  })

  beforeEach(() => {
    // D3.js 테스트를 위한 SVG 모킹
    vi.clearAllMocks()
  })

  describe('Rendering', () => {
    it('should render visualizer container', () => {
      const data = createMockData([
        { array: [3, 1, 2], action: 'compare' }
      ])

      render(<SortVisualizer data={data} />)

      expect(screen.getByTestId('sort-visualizer')).toBeInTheDocument()
    })

    it('should render array bars for current snapshot', () => {
      const data = createMockData([
        { array: [5, 2, 8, 1], action: 'compare' }
      ])

      render(<SortVisualizer data={data} />)

      // SVG가 렌더링되는지 확인
      const svg = screen.getByTestId('sort-svg')
      expect(svg).toBeInTheDocument()
    })

    it('should display algorithm name', () => {
      const data = createMockData([
        { array: [3, 1, 2], action: 'compare' }
      ])

      render(<SortVisualizer data={data} />)

      expect(screen.getByText(/bubble/i)).toBeInTheDocument()
    })

    it('should display current step information', () => {
      const data = createMockData([
        { array: [3, 1, 2], action: 'compare' },
        { array: [1, 3, 2], action: 'swap' }
      ])

      render(<SortVisualizer data={data} />)

      expect(screen.getByText(/step.*1.*2/i)).toBeInTheDocument()
    })
  })

  describe('Animation Controls', () => {
    it('should render play/pause button', () => {
      const data = createMockData([
        { array: [3, 1, 2], action: 'compare' }
      ])

      render(<SortVisualizer data={data} />)

      const playButton = screen.getByRole('button', { name: /play|pause/i })
      expect(playButton).toBeInTheDocument()
    })

    it('should render next/previous buttons', () => {
      const data = createMockData([
        { array: [3, 1, 2], action: 'compare' },
        { array: [1, 3, 2], action: 'swap' }
      ])

      render(<SortVisualizer data={data} />)

      expect(screen.getByRole('button', { name: /next/i })).toBeInTheDocument()
      expect(screen.getByRole('button', { name: /previous/i })).toBeInTheDocument()
    })

    it('should advance to next snapshot when next button is clicked', () => {
      const data = createMockData([
        { array: [3, 1, 2], action: 'compare' },
        { array: [1, 3, 2], action: 'swap' }
      ])

      render(<SortVisualizer data={data} />)

      expect(screen.getByText(/step.*1.*2/i)).toBeInTheDocument()

      const nextButton = screen.getByRole('button', { name: /next/i })
      fireEvent.click(nextButton)

      expect(screen.getByText(/step.*2.*2/i)).toBeInTheDocument()
    })

    it('should go back to previous snapshot when previous button is clicked', () => {
      const data = createMockData([
        { array: [3, 1, 2], action: 'compare' },
        { array: [1, 3, 2], action: 'swap' }
      ])

      render(<SortVisualizer data={data} />)

      // 먼저 다음으로 이동
      const nextButton = screen.getByRole('button', { name: /next/i })
      fireEvent.click(nextButton)
      expect(screen.getByText(/step.*2.*2/i)).toBeInTheDocument()

      // 이전으로 돌아가기
      const prevButton = screen.getByRole('button', { name: /previous/i })
      fireEvent.click(prevButton)
      expect(screen.getByText(/step.*1.*2/i)).toBeInTheDocument()
    })

    it('should disable previous button on first step', () => {
      const data = createMockData([
        { array: [3, 1, 2], action: 'compare' },
        { array: [1, 3, 2], action: 'swap' }
      ])

      render(<SortVisualizer data={data} />)

      const prevButton = screen.getByRole('button', { name: /previous/i })
      expect(prevButton).toBeDisabled()
    })

    it('should disable next button on last step', () => {
      const data = createMockData([
        { array: [3, 1, 2], action: 'compare' },
        { array: [1, 3, 2], action: 'swap' }
      ])

      render(<SortVisualizer data={data} />)

      // 마지막 단계로 이동
      const nextButton = screen.getByRole('button', { name: /next/i })
      fireEvent.click(nextButton)

      expect(nextButton).toBeDisabled()
    })
  })

  describe('Snapshot Display', () => {
    it('should highlight comparing elements', () => {
      const data = createMockData([
        { array: [5, 2, 8], comparing: [0, 1], action: 'compare' }
      ])

      render(<SortVisualizer data={data} />)

      // D3로 렌더링된 rect 요소를 확인 (실제 구현 시)
      const svg = screen.getByTestId('sort-svg')
      expect(svg).toBeInTheDocument()
    })

    it('should highlight swapping elements', () => {
      const data = createMockData([
        { array: [2, 5, 8], swapping: [0, 1], action: 'swap' }
      ])

      render(<SortVisualizer data={data} />)

      const svg = screen.getByTestId('sort-svg')
      expect(svg).toBeInTheDocument()
    })

    it('should highlight sorted elements', () => {
      const data = createMockData([
        { array: [1, 2, 5, 8], sorted: [0, 1, 2, 3], action: 'sorted' }
      ])

      render(<SortVisualizer data={data} />)

      const svg = screen.getByTestId('sort-svg')
      expect(svg).toBeInTheDocument()
    })

    it('should display action description if provided', () => {
      const data = createMockData([
        {
          array: [5, 2, 8],
          comparing: [0, 1],
          action: 'compare',
          description: 'Comparing arr[0]=5 and arr[1]=2'
        }
      ])

      render(<SortVisualizer data={data} />)

      expect(screen.getByText(/comparing arr\[0\]=5 and arr\[1\]=2/i)).toBeInTheDocument()
    })
  })

  describe('Speed Control', () => {
    it('should render speed selector', () => {
      const data = createMockData([
        { array: [3, 1, 2], action: 'compare' }
      ])

      render(<SortVisualizer data={data} />)

      expect(screen.getByLabelText(/speed/i)).toBeInTheDocument()
    })

    it('should update speed when selector is changed', () => {
      const data = createMockData([
        { array: [3, 1, 2], action: 'compare' }
      ])

      render(<SortVisualizer data={data} />)

      const speedSelector = screen.getByLabelText(/speed/i)
      fireEvent.change(speedSelector, { target: { value: '2' } })

      expect(speedSelector).toHaveValue('2')
    })
  })

  describe('Edge Cases', () => {
    it('should handle empty snapshots array', () => {
      const data = createMockData([])

      render(<SortVisualizer data={data} />)

      expect(screen.getByText(/no data/i)).toBeInTheDocument()
    })

    it('should handle single element array', () => {
      const data = createMockData([
        { array: [42], sorted: [0], action: 'sorted' }
      ])

      render(<SortVisualizer data={data} />)

      expect(screen.getByTestId('sort-visualizer')).toBeInTheDocument()
    })

    it('should handle large arrays gracefully', () => {
      const largeArray = Array.from({ length: 100 }, (_, i) => i + 1)
      const data = createMockData([
        { array: largeArray, action: 'compare' }
      ])

      render(<SortVisualizer data={data} />)

      expect(screen.getByTestId('sort-visualizer')).toBeInTheDocument()
    })
  })

  describe('Auto-play', () => {
    it('should auto-play when autoPlay prop is true', async () => {
      const data = createMockData([
        { array: [3, 1, 2], action: 'compare' },
        { array: [1, 3, 2], action: 'swap' },
        { array: [1, 2, 3], action: 'sorted' }
      ])

      render(<SortVisualizer data={data} autoPlay={true} defaultSpeed={4} />)

      expect(screen.getByText(/step.*1.*3/i)).toBeInTheDocument()

      // 자동으로 다음 단계로 진행되는지 확인 (빠른 속도로)
      await waitFor(
        () => {
          expect(screen.queryByText(/step.*[23].*3/i)).toBeInTheDocument()
        },
        { timeout: 2000 }
      )
    })
  })
})
