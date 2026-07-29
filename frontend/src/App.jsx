import React, { useEffect, useState } from 'react';
import Navbar from './components/Navbar';
import StatsCards from './components/StatsCards';
import RunbookList from './components/RunbookList';
import ExecutionMonitor from './components/ExecutionMonitor';
import ApprovalModal from './components/ApprovalModal';
import ExecutionReportView from './components/ExecutionReportView';
import AuditLogView from './components/AuditLogView';
import { api } from './services/api';
import { Play, ArrowRight, Activity, ShieldAlert } from 'lucide-react';

export default function App() {
  const [activeTab, setActiveTab] = useState('dashboard');
  const [runbooks, setRunbooks] = useState([]);
  const [executions, setExecutions] = useState([]);
  const [selectedExecution, setSelectedExecution] = useState(null);
  const [executionSteps, setExecutionSteps] = useState([]);
  const [pendingApprovals, setPendingApprovals] = useState([]);
  const [report, setReport] = useState(null);

  const loadData = async () => {
    try {
      const [rbs, execs, approvals] = await Promise.all([
        api.getRunbooks(),
        api.getExecutions(),
        api.getPendingApprovals()
      ]);
      setRunbooks(rbs);
      setExecutions(execs);
      setPendingApprovals(approvals);

      if (selectedExecution) {
        const updatedExec = execs.find(e => e.id === selectedExecution.id);
        if (updatedExec) {
          setSelectedExecution(updatedExec);
          const steps = await api.getExecutionSteps(updatedExec.id);
          setExecutionSteps(steps);
        }
      }
    } catch (err) {
      console.error("Error loading application data:", err);
    }
  };

  useEffect(() => {
    loadData();
    const interval = setInterval(loadData, 2000);
    return () => clearInterval(interval);
  }, [selectedExecution?.id]);

  const handleStartExecution = async (runbookId) => {
    try {
      const exec = await api.startExecution(runbookId);
      setSelectedExecution(exec);
      const steps = await api.getExecutionSteps(exec.id);
      setExecutionSteps(steps);
      setActiveTab('executions');
      loadData();
    } catch (err) {
      alert("Error starting runbook execution: " + err.message);
    }
  };

  const handleViewReport = async (executionId) => {
    try {
      const rpt = await api.getExecutionReport(executionId);
      setReport(rpt);
      setActiveTab('report');
    } catch (err) {
      alert("Error generating report: " + err.message);
    }
  };

  const successCount = executions.filter(e => e.status === 'COMPLETED').length;

  return (
    <div className="app-container">
      <Navbar
        activeTab={activeTab}
        setActiveTab={(tab) => {
          if (tab !== 'report') setReport(null);
          setActiveTab(tab);
        }}
        pendingCount={pendingApprovals.length}
      />

      <main className="main-content">
        <StatsCards
          runbooksCount={runbooks.length}
          executionsCount={executions.length}
          successCount={successCount}
          pendingApprovalsCount={pendingApprovals.length}
        />

        {activeTab === 'dashboard' && (
          <div>
            <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '1.5rem', marginBottom: '2rem' }}>
              <div>
                <h3 style={{ fontSize: '1.25rem', fontWeight: 600, marginBottom: '1rem' }}>Available Runbooks</h3>
                <RunbookList
                  runbooks={runbooks}
                  onSelectRunbook={(id) => {}}
                  onStartExecution={handleStartExecution}
                  onRefresh={loadData}
                />
              </div>

              <div>
                <h3 style={{ fontSize: '1.25rem', fontWeight: 600, marginBottom: '1rem' }}>Recent Executions</h3>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                  {executions.map((exec) => (
                    <div
                      key={exec.id}
                      className="stat-card"
                      style={{ cursor: 'pointer', padding: '1rem' }}
                      onClick={async () => {
                        setSelectedExecution(exec);
                        const steps = await api.getExecutionSteps(exec.id);
                        setExecutionSteps(steps);
                        setActiveTab('executions');
                      }}
                    >
                      <div>
                        <div style={{ fontWeight: 600, fontSize: '0.95rem' }}>{exec.runbookName}</div>
                        <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                          #{exec.id} • {new Date(exec.startedAt).toLocaleTimeString()}
                        </div>
                      </div>
                      <span className={`status-badge ${exec.status === 'COMPLETED' ? 'live' : exec.status === 'WAITING_FOR_APPROVAL' ? 'waiting' : 'failed'}`}>
                        {exec.status}
                      </span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'executions' && (
          <ExecutionMonitor
            execution={selectedExecution}
            steps={executionSteps}
            onRefresh={loadData}
            onViewReport={handleViewReport}
          />
        )}

        {activeTab === 'runbooks' && (
          <RunbookList
            runbooks={runbooks}
            onSelectRunbook={(id) => {}}
            onStartExecution={handleStartExecution}
            onRefresh={loadData}
          />
        )}

        {activeTab === 'audit' && <AuditLogView />}

        {activeTab === 'report' && (
          <ExecutionReportView
            report={report}
            onBack={() => setActiveTab('dashboard')}
          />
        )}
      </main>

      {/* Human-in-the-Loop Approval Modal */}
      {pendingApprovals.length > 0 && (
        <ApprovalModal
          approval={pendingApprovals[0]}
          onResumed={loadData}
        />
      )}
    </div>
  );
}
