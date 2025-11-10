import { useEffect, useRef } from 'react'
import * as d3 from 'd3'

interface ArrayVisualizerProps {
  data: string | number[]
  expression: string
}

/**
 * D3.js를 사용한 배열 시각화 컴포넌트
 * 배열 데이터를 막대 그래프로 표현
 */
export function ArrayVisualizer({ data, expression }: ArrayVisualizerProps) {
  const svgRef = useRef<SVGSVGElement>(null)

  useEffect(() => {
    if (!svgRef.current) return

    // 데이터 파싱
    const parsedData = parseArrayData(data)
    if (!parsedData || parsedData.length === 0) return

    // SVG 설정
    const svg = d3.select(svgRef.current)
    const width = 800
    const height = 400
    const margin = { top: 40, right: 40, bottom: 60, left: 60 }
    const chartWidth = width - margin.left - margin.right
    const chartHeight = height - margin.top - margin.bottom

    // 기존 내용 제거
    svg.selectAll('*').remove()

    // 메인 그룹 생성
    const g = svg
      .attr('width', width)
      .attr('height', height)
      .append('g')
      .attr('transform', `translate(${margin.left},${margin.top})`)

    // 스케일 설정
    const xScale = d3
      .scaleBand()
      .domain(parsedData.map((_, i) => i.toString()))
      .range([0, chartWidth])
      .padding(0.2)

    const maxValue = d3.max(parsedData, d => Math.abs(d.value)) || 1
    const yScale = d3
      .scaleLinear()
      .domain([Math.min(0, -maxValue), maxValue])
      .range([chartHeight, 0])

    // 색상 스케일
    const colorScale = d3
      .scaleSequential()
      .domain([0, parsedData.length - 1])
      .interpolator(d3.interpolateViridis)

    // X축
    g.append('g')
      .attr('class', 'x-axis')
      .attr('transform', `translate(0,${chartHeight / 2})`)
      .call(d3.axisBottom(xScale))
      .selectAll('text')
      .style('font-size', '12px')

    // Y축
    g.append('g')
      .attr('class', 'y-axis')
      .call(d3.axisLeft(yScale))
      .selectAll('text')
      .style('font-size', '12px')

    // 막대 그래프
    g.selectAll('.bar')
      .data(parsedData)
      .enter()
      .append('rect')
      .attr('class', 'bar')
      .attr('x', (_, i) => xScale(i.toString()) || 0)
      .attr('y', d => (d.value >= 0 ? yScale(d.value) : yScale(0)))
      .attr('width', xScale.bandwidth())
      .attr('height', d => Math.abs(yScale(d.value) - yScale(0)))
      .attr('fill', (_, i) => colorScale(i))
      .attr('rx', 4)
      .style('cursor', 'pointer')
      .on('mouseover', function (_event, d) {
        d3.select(this)
          .transition()
          .duration(200)
          .attr('opacity', 0.7)

        // 툴팁 표시
        g.append('text')
          .attr('class', 'tooltip')
          .attr('x', parseFloat(d3.select(this).attr('x')) + xScale.bandwidth() / 2)
          .attr('y', parseFloat(d3.select(this).attr('y')) - 10)
          .attr('text-anchor', 'middle')
          .style('font-size', '14px')
          .style('font-weight', 'bold')
          .style('fill', '#333')
          .text(`${d.display} (idx: ${d.index})`)
      })
      .on('mouseout', function () {
        d3.select(this)
          .transition()
          .duration(200)
          .attr('opacity', 1)

        g.selectAll('.tooltip').remove()
      })

    // 값 레이블
    g.selectAll('.value-label')
      .data(parsedData)
      .enter()
      .append('text')
      .attr('class', 'value-label')
      .attr('x', (_, i) => (xScale(i.toString()) || 0) + xScale.bandwidth() / 2)
      .attr('y', d => {
        const barY = d.value >= 0 ? yScale(d.value) : yScale(0)
        return d.value >= 0 ? barY - 5 : barY + 15
      })
      .attr('text-anchor', 'middle')
      .style('font-size', '11px')
      .style('fill', '#555')
      .text(d => d.display)

    // 제목
    svg
      .append('text')
      .attr('x', width / 2)
      .attr('y', 20)
      .attr('text-anchor', 'middle')
      .style('font-size', '16px')
      .style('font-weight', 'bold')
      .style('fill', '#333')
      .text(`${expression} (length: ${parsedData.length})`)
  }, [data, expression])

  return (
    <div className="array-visualizer">
      <svg ref={svgRef}></svg>
    </div>
  )
}

/**
 * 배열 데이터 파싱
 */
function parseArrayData(data: string | number[]): { index: number; value: number; display: string }[] | null {
  try {
    let array: any[]

    if (Array.isArray(data)) {
      array = data
    } else if (typeof data === 'string') {
      // "[1, 2, 3]" 형식
      if (data.startsWith('[') && data.endsWith(']')) {
        const content = data.slice(1, -1).trim()
        if (!content) return []

        array = content.split(',').map(item => {
          const trimmed = item.trim()
          // "['1', '2']" 같은 char 배열 처리
          if (trimmed.startsWith("'") && trimmed.endsWith("'")) {
            return trimmed.slice(1, -1)
          }
          return trimmed
        })
      } else {
        return null
      }
    } else {
      return null
    }

    // 숫자 변환
    return array.map((item, index) => {
      let value: number
      let display: string

      if (typeof item === 'number') {
        value = item
        display = item.toString()
      } else if (typeof item === 'string') {
        // char인 경우 ASCII 코드로 변환
        if (item.length === 1) {
          value = item.charCodeAt(0)
          display = `'${item}'`
        } else {
          value = parseFloat(item) || 0
          display = item
        }
      } else {
        value = 0
        display = String(item)
      }

      return { index, value, display }
    })
  } catch (e) {
    console.error('Failed to parse array data:', e)
    return null
  }
}
