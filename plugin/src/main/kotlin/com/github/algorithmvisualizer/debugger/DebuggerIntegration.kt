package com.github.algorithmvisualizer.debugger

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.XDebuggerManagerListener
import com.intellij.xdebugger.frame.XStackFrame

/**
 * 디버거 API 통합을 담당하는 서비스
 *
 * 이 클래스는 IntelliJ의 XDebugger API를 사용하여:
 * - 현재 디버깅 세션 감지
 * - 브레이크포인트에서 멈췄을 때 스택 프레임 접근
 * - 디버깅 상태 변경 감지
 */
@Service(Service.Level.PROJECT)
class DebuggerIntegration(private val project: Project) {

    private val logger = Logger.getInstance(DebuggerIntegration::class.java)
    private var currentSession: XDebugSession? = null
    private val listeners = mutableListOf<DebuggerStateListener>()

    init {
        setupDebuggerListener()
    }

    /**
     * 디버거 상태 변경을 감지하는 리스너 설정
     */
    private fun setupDebuggerListener() {
        val connection = project.messageBus.connect()

        connection.subscribe(XDebuggerManager.TOPIC, object : XDebuggerManagerListener {
            override fun processStarted(debugProcess: com.intellij.xdebugger.XDebugProcess) {
                logger.info("Debug process started: ${debugProcess.session.sessionName}")
                currentSession = debugProcess.session
                notifySessionStarted(debugProcess.session)
            }

            override fun processStopped(debugProcess: com.intellij.xdebugger.XDebugProcess) {
                logger.info("Debug process stopped: ${debugProcess.session.sessionName}")
                notifySessionStopped(debugProcess.session)
                currentSession = null
            }

            override fun currentSessionChanged(
                previousSession: XDebugSession?,
                currentSession: XDebugSession?
            ) {
                logger.info("Debug session changed: ${currentSession?.sessionName ?: "null"}")
                this@DebuggerIntegration.currentSession = currentSession
                notifySessionChanged(previousSession, currentSession)
            }
        })
    }

    /**
     * 현재 활성화된 디버그 세션 가져오기
     */
    fun getCurrentSession(): XDebugSession? {
        if (currentSession == null) {
            currentSession = XDebuggerManager.getInstance(project).currentSession
        }
        return currentSession
    }

    /**
     * 현재 스택 프레임 가져오기
     *
     * @return 현재 실행이 멈춘 지점의 스택 프레임, 디버깅 중이 아니면 null
     */
    fun getCurrentStackFrame(): XStackFrame? {
        val session = getCurrentSession() ?: run {
            logger.debug("No active debug session")
            return null
        }

        if (!session.isSuspended) {
            logger.debug("Debug session is not suspended")
            return null
        }

        return session.currentStackFrame
    }

    /**
     * 현재 디버깅 중인지 확인
     */
    fun isDebugging(): Boolean {
        return getCurrentSession() != null
    }

    /**
     * 현재 브레이크포인트에서 멈춰있는지 확인
     */
    fun isSuspended(): Boolean {
        return getCurrentSession()?.isSuspended ?: false
    }

    /**
     * 디버거 상태 변경 리스너 등록
     */
    fun addListener(listener: DebuggerStateListener) {
        listeners.add(listener)
    }

    /**
     * 디버거 상태 변경 리스너 제거
     */
    fun removeListener(listener: DebuggerStateListener) {
        listeners.remove(listener)
    }

    private fun notifySessionStarted(session: XDebugSession) {
        listeners.forEach { it.onSessionStarted(session) }
    }

    private fun notifySessionStopped(session: XDebugSession) {
        listeners.forEach { it.onSessionStopped(session) }
    }

    private fun notifySessionChanged(previous: XDebugSession?, current: XDebugSession?) {
        listeners.forEach { it.onSessionChanged(previous, current) }
    }

    companion object {
        /**
         * 프로젝트의 DebuggerIntegration 인스턴스 가져오기
         */
        fun getInstance(project: Project): DebuggerIntegration {
            return project.getService(DebuggerIntegration::class.java)
        }
    }
}

/**
 * 디버거 상태 변경을 감지하는 리스너 인터페이스
 */
interface DebuggerStateListener {
    /**
     * 디버그 세션이 시작되었을 때 호출
     */
    fun onSessionStarted(session: XDebugSession) {}

    /**
     * 디버그 세션이 종료되었을 때 호출
     */
    fun onSessionStopped(session: XDebugSession) {}

    /**
     * 현재 디버그 세션이 변경되었을 때 호출
     */
    fun onSessionChanged(previous: XDebugSession?, current: XDebugSession?) {}
}
