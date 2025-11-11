import { useState, useEffect, useRef } from 'react'
import * as d3 from 'd3'
import type { SortVisualizationData, SortSnapshot } from '../types/sort'
import { SortVisualizationColor } from '../types/sort'

interface SortVisualizerProps {
  data: SortVisualizationData
  width?: number
  height?: number
  autoPlay?: boolean
  defaultSpeed?: number
  onStepChange?: (step: number) => void
}

/**
 * 정렬 알고리즘 시각화 컴포넌트
 * D3.js를 사용하여 정렬 과정을 애니메이션으로 표현
 */
export function SortVisualizer({
  data,
  width = 800,
  height = 400,
  autoPlay = false,
  defaultSpeed = 1.0,
  onStepChange
}: SortVisualizerProps) {
  const svgRef = useRef<SVGSVGElement>(null)
  const [currentStep, setCurrentStep] = useState(0)
  const [isPlaying, setIsPlaying] = useState(autoPlay)
  const [speed, setSpeed] = useState(defaultSpeed)

  const snapshots = data.data.snapshots
  const totalSteps = snapshots.length
  const currentSnapshot = snapshots[currentStep] || null

  // 빈 데이터 처리
  if (snapshots.length === 0) {
    return (
      <div data-testid="sort-visualizer">
        <p>No data to visualize</p>
      </div>
    )
  }

  // 단계 변경 핸들러
  const handleNext = () => {
    if (currentStep < totalSteps - 1) {
      const nextStep = currentStep + 1
      setCurrentStep(nextStep)
      onStepChange?.(nextStep)
    }
  }

  const handlePrevious = () => {
    if (currentStep > 0) {
      const prevStep = currentStep - 1
      setCurrentStep(prevStep)
      onStepChange?.(prevStep)
    }
  }

  const handlePlayPause = () => {
    setIsPlaying(!isPlaying)
  }

  const handleSpeedChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setSpeed(parseFloat(event.target.value))
  }

  // 자동 재생
  useEffect(() => {
    if (!isPlaying || currentStep >= totalSteps - 1) {
      return
    }

    const intervalMs = 1000 / speed
    const timer = setInterval(() => {
      setCurrentStep(prev => {
        if (prev >= totalSteps - 1) {
          setIsPlaying(false)
          return prev
        }
        const nextStep = prev + 1
        onStepChange?.(nextStep)
        return nextStep
      })
    }, intervalMs)

    return () => clearInterval(timer)
  }, [isPlaying, currentStep, totalSteps, speed, onStepChange])

  // D3 시각화
  useEffect(() => {
    if (!svgRef.current || !currentSnapshot) return

    const svg = d3.select(svgRef.current)
    const margin = { top: 60, right: 40, bottom: 80, left: 60 }
    const chartWidth = width - margin.left - margin.right
    const chartHeight = height - margin.top - margin.bottom

    // 기존 내용 제거
    svg.selectAll('*').remove()

    // SVG 설정
    svg.attr('width', width).attr('height', height)

    const g = svg.append('g').attr('transform', `translate(${margin.left},${margin.top})`)

    // 스케일
    const xScale = d3
      .scaleBand()
      .domain(currentSnapshot.array.map((_, i) => i.toString()))
      .range([0, chartWidth])
      .padding(0.2)

    const maxValue = d3.max(currentSnapshot.array) || 1
    const yScale = d3.scaleLinear().domain([0, maxValue]).range([chartHeight, 0])

    // 축
    g.append('g')
      .attr('class', 'x-axis')
      .attr('transform', `translate(0,${chartHeight})`)
      .call(d3.axisBottom(xScale))

    g.append('g').attr('class', 'y-axis').call(d3.axisLeft(yScale))

    // 막대 그래프
    g.selectAll('.bar')
      .data(currentSnapshot.array)
      .enter()
      .append('rect')
      .attr('class', 'bar')
      .attr('x', (_, i) => xScale(i.toString()) || 0)
      .attr('y', d => yScale(d))
      .attr('width', xScale.bandwidth())
      .attr('height', d => chartHeight - yScale(d))
      .attr('fill', (_, i) => getBarColor(i, currentSnapshot))
      .attr('rx', 4)

    // 값 레이블
    g.selectAll('.value-label')
      .data(currentSnapshot.array)
      .enter()
      .append('text')
      .attr('class', 'value-label')
      .attr('x', (_, i) => (xScale(i.toString()) || 0) + xScale.bandwidth() / 2)
      .attr('y', d => yScale(d) - 5)
      .attr('text-anchor', 'middle')
      .style('font-size', '12px')
      .style('fill', '#333')
      .text(d => d)

    // 제목
    svg
      .append('text')
      .attr('x', width / 2)
      .attr('y', 25)
      .attr('text-anchor', 'middle')
      .style('font-size', '18px')
      .style('font-weight', 'bold')
      .text(`${data.data.algorithm || 'Sort'} Sort`)

    // 설명
    if (currentSnapshot.description) {
      svg
        .append('text')
        .attr('x', width / 2)
        .attr('y', height - 20)
        .attr('text-anchor', 'middle')
        .style('font-size', '14px')
        .style('fill', '#555')
        .text(currentSnapshot.description)
    }
  }, [currentSnapshot, width, height, data.data.algorithm])

  return (
    <div data-testid="sort-visualizer" style={{ padding: '20px' }}>
      <svg ref={svgRef} data-testid="sort-svg"></svg>

      <div style={{ marginTop: '20px', textAlign: 'center' }}>
        <div style={{ marginBottom: '15px' }}>
          <button
            onClick={handlePrevious}
            disabled={currentStep === 0}
            aria-label="Previous"
            style={{ marginRight: '10px', padding: '8px 16px' }}
          >
            ◀◀ Previous
          </button>

          <button onClick={handlePlayPause} aria-label={isPlaying ? 'Pause' : 'Play'} style={{ marginRight: '10px', padding: '8px 16px' }}>
            {isPlaying ? '⏸ Pause' : '▶ Play'}
          </button>

          <button
            onClick={handleNext}
            disabled={currentStep === totalSteps - 1}
            aria-label="Next"
            style={{ marginRight: '10px', padding: '8px 16px' }}
          >
            Next ▶▶
          </button>
        </div>

        <div style={{ marginBottom: '10px' }}>
          <span>
            Step {currentStep + 1} / {totalSteps}
          </span>
        </div>

        <div>
          <label htmlFor="speed-selector">
            Speed:
            <select id="speed-selector" value={speed} onChange={handleSpeedChange} style={{ marginLeft: '10px', padding: '4px' }}>
              <option value="0.5">0.5x</option>
              <option value="1">1x</option>
              <option value="2">2x</option>
              <option value="4">4x</option>
            </select>
          </label>
        </div>
      </div>
    </div>
  )
}

/**
 * 인덱스에 따른 막대 색상 결정
 */
function getBarColor(index: number, snapshot: SortSnapshot): string {
  // 정렬 완료
  if (snapshot.sorted?.includes(index)) {
    return SortVisualizationColor.SORTED
  }

  // 스왑 중
  if (snapshot.swapping?.includes(index)) {
    return SortVisualizationColor.SWAPPING
  }

  // 비교 중
  if (snapshot.comparing?.includes(index)) {
    return SortVisualizationColor.COMPARING
  }

  // 피벗
  if (snapshot.pivot === index) {
    return SortVisualizationColor.PIVOT
  }

  // 기본
  return SortVisualizationColor.DEFAULT
}
