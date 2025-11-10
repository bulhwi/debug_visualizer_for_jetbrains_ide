import { describe, it, expect, beforeEach, vi } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import App from './App'

describe('App', () => {
  beforeEach(() => {
    // window.visualizerAPI 초기화
    delete (window as any).visualizerAPI
  })

  it('should render the app title', () => {
    render(<App />)
    expect(screen.getByText(/Algorithm Debug Visualizer/i)).toBeInTheDocument()
  })

  it('should show empty state initially', () => {
    render(<App />)
    expect(screen.getByText(/데이터 준비 중/i)).toBeInTheDocument()
    expect(screen.getByText(/📊/)).toBeInTheDocument()
  })

  it('should register visualizerAPI on mount', async () => {
    render(<App />)

    await waitFor(() => {
      expect((window as any).visualizerAPI).toBeDefined()
      expect((window as any).visualizerAPI.showData).toBeInstanceOf(Function)
      expect((window as any).visualizerAPI.clear).toBeInstanceOf(Function)
    })
  })

  it('should display data when showData is called', async () => {
    render(<App />)

    await waitFor(() => {
      expect((window as any).visualizerAPI).toBeDefined()
    })

    // Java에서 데이터 전송 시뮬레이션
    const testData = {
      expression: 'myArray',
      value: '[1, 2, 3, 4, 5]',
      type: 'int[]',
      timestamp: Date.now(),
    }

    ;(window as any).visualizerAPI.showData(JSON.stringify(testData))

    await waitFor(() => {
      expect(screen.getByText('myArray')).toBeInTheDocument()
      expect(screen.getByText('[1, 2, 3, 4, 5]')).toBeInTheDocument()
      expect(screen.getByText('int[]')).toBeInTheDocument()
    })
  })

  it('should handle JSON string data', async () => {
    render(<App />)

    await waitFor(() => {
      expect((window as any).visualizerAPI).toBeDefined()
    })

    const jsonString = JSON.stringify({
      expression: 'sum',
      value: '42',
      timestamp: Date.now(),
    })

    ;(window as any).visualizerAPI.showData(jsonString)

    await waitFor(() => {
      expect(screen.getByText('sum')).toBeInTheDocument()
      expect(screen.getByText('42')).toBeInTheDocument()
    })
  })

  it('should handle already parsed object', async () => {
    render(<App />)

    await waitFor(() => {
      expect((window as any).visualizerAPI).toBeDefined()
    })

    const dataObject = {
      expression: 'chars',
      value: "['1', '2', '3']",
      timestamp: Date.now(),
    }

    ;(window as any).visualizerAPI.showData(dataObject)

    await waitFor(() => {
      expect(screen.getByText('chars')).toBeInTheDocument()
      expect(screen.getByText("['1', '2', '3']")).toBeInTheDocument()
    })
  })

  it('should handle invalid JSON gracefully', async () => {
    const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {})

    render(<App />)

    await waitFor(() => {
      expect((window as any).visualizerAPI).toBeDefined()
    })

    ;(window as any).visualizerAPI.showData('invalid json {')

    await waitFor(() => {
      expect(screen.getByText('invalid json {')).toBeInTheDocument()
      expect(screen.getByText('error')).toBeInTheDocument()
    })

    expect(consoleErrorSpy).toHaveBeenCalledWith(
      'Failed to parse data:',
      expect.any(Error)
    )

    consoleErrorSpy.mockRestore()
  })

  it('should clear data when clear is called', async () => {
    render(<App />)

    await waitFor(() => {
      expect((window as any).visualizerAPI).toBeDefined()
    })

    // 먼저 데이터 표시
    const testData = {
      expression: 'test',
      value: 'value',
      timestamp: Date.now(),
    }

    ;(window as any).visualizerAPI.showData(JSON.stringify(testData))

    await waitFor(() => {
      expect(screen.getByText('test')).toBeInTheDocument()
    })

    // 클리어 호출
    ;(window as any).visualizerAPI.clear()

    await waitFor(() => {
      expect(screen.queryByText('test')).not.toBeInTheDocument()
      expect(screen.getByText(/데이터 준비 중/i)).toBeInTheDocument()
    })
  })

  it('should display timestamp correctly', async () => {
    render(<App />)

    await waitFor(() => {
      expect((window as any).visualizerAPI).toBeDefined()
    })

    const now = Date.now()
    const testData = {
      expression: 'test',
      value: 'value',
      timestamp: now,
    }

    ;(window as any).visualizerAPI.showData(JSON.stringify(testData))

    await waitFor(() => {
      const expectedTime = new Date(now).toLocaleTimeString()
      expect(screen.getByText(/평가 시각:/)).toBeInTheDocument()
      expect(screen.getByText(new RegExp(expectedTime))).toBeInTheDocument()
    })
  })

  it('should only show type section when type is provided', async () => {
    render(<App />)

    await waitFor(() => {
      expect((window as any).visualizerAPI).toBeDefined()
    })

    // 타입 없이 데이터 전송
    const dataWithoutType = {
      expression: 'test',
      value: 'value',
      timestamp: Date.now(),
    }

    ;(window as any).visualizerAPI.showData(JSON.stringify(dataWithoutType))

    await waitFor(() => {
      expect(screen.getByText('표현식')).toBeInTheDocument()
      expect(screen.getByText('값')).toBeInTheDocument()
      expect(screen.queryByText('타입')).not.toBeInTheDocument()
    })

    // 클리어 후 타입과 함께 데이터 전송
    ;(window as any).visualizerAPI.clear()

    const dataWithType = {
      expression: 'test',
      value: 'value',
      type: 'String',
      timestamp: Date.now(),
    }

    ;(window as any).visualizerAPI.showData(JSON.stringify(dataWithType))

    await waitFor(() => {
      expect(screen.getByText('타입')).toBeInTheDocument()
      expect(screen.getByText('String')).toBeInTheDocument()
    })
  })
})
