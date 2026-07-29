import React from 'react';
import { Play, RotateCcw, SkipForward, XCircle, FileText, CheckCircle, Clock, ShieldAlert } from 'lucide-react';
import { api } from '../services/api';

export default function ExecutionMonitor({ execution, steps, onRefresh, onViewReport }) {
  if (!execution) {
    return (
      <div style={{ textAlign: 'center', padding: '4rem 2rem', color: 'var(--text-muted)' }}>
        <FileText size={48} style={{ marginBottom: '1rem', opacity: 0.5 }} />
        <h3>No Active Execution Selected</h3>
        <p style={{ fontSize: '0.9rem', marginTop: '0.25rem' }}>Select a runbook from the Runbooks tab to launch the AI Agent</p>
      </div>
    );
  }

  const handleRetry = async (stepId) => {
    try {
      await api.retryStep(execution.id, stepId);
      onRefresh();
    } catch (err) {
      alert(err.message);
    }
  };

  const handleSkip = async (stepId) => {
    try {
      await api.skipStep(execution.id, stepId);
      onRefresh();
    } catch (err) {
      alert(err.message);
    }
  };

  const completedCount = steps.filter(s => s.status === 'COMPLETED').length;
  const progressPercent = steps.length > 0 ? Math.round((completedCount / steps.length) * 100) : 0;

  return (
    <div>
      <div style={{ background: 'var(--bg-card)', border: '1px solid var(--border-color)', borderRadius: '16px', padding: '1.5rem', marginBottom: '2rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
          <div>
            <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>RUNBOOK AGENT WORKFLOW</div>
            <h2 style={{ fontSize: '1.5rem', fontWeight: 700 }}>{execution.runbookName}</h2>
          </div>

          <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center' }}>
            <span className={`status-badge ${execution.status === 'COMPLETED' ? 'live' : execution.status === 'WAITING_FOR_APPROVAL' ? 'waiting' : 'failed'}`}>
              {execution.status}
            </span>

            {execution.status === 'COMPLETED' && (
              <button className="btn btn-primary" onClick={() => onViewReport(execution.id)}>
                <FileText size={16} /> View Final Execution Report
              </button>
            )}
          </div>
        </div>

        {/* Progress Bar */}
        <div style={{ width: '100%', background: 'var(--bg-primary)', height: '10px', borderRadius: '999px', overflow: 'hidden', border: '1px solid var(--border-color)' }}>
          <div style={{
            width: `${progressPercent}%`,
            height: '100%',
            background: execution.status === 'FAILED' ? 'var(--accent-red)' : 'linear-gradient(to right, var(--accent-blue), var(--accent-green))',
            transition: 'width 0.4s ease'
          }}></div>
        </div>
        <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.8rem', color: 'var(--text-muted)', marginTop: '0.5rem' }}>
          <span>{completedCount} of {steps.length} steps completed</span>
          <span>{progressPercent}% Complete</span>
        </div>
      </div>

      {/* Execution Timeline */}
      <h3 style={{ fontSize: '1.25rem', fontWeight: 600, marginBottom: '1rem' }}>Live Execution Timeline</h3>
      <div className="timeline">
        {steps.map((step) => {
          const isDone = step.status === 'COMPLETED';
          const isWaiting = step.status === 'WAITING_FOR_APPROVAL';
          const isFailed = step.status === 'FAILED';

          return (
            <div key={step.id} className={`timeline-item ${isDone ? 'completed' : isWaiting ? 'waiting' : isFailed ? 'failed' : ''}`}>
              <div className="timeline-marker"></div>

              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.5rem' }}>
                <div>
                  <span style={{ fontSize: '0.8rem', color: '#60a5fa', fontWeight: 600, marginRight: '0.5rem' }}>
                    STEP {step.stepNumber}
                  </span>
                  <span style={{ fontWeight: 600, fontSize: '1rem' }}>{step.description}</span>
                </div>

                <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                  <span className={`status-badge ${step.riskLevel === 'HIGH' ? 'waiting' : 'live'}`} style={{ fontSize: '0.65rem' }}>
                    {step.action} [{step.riskLevel}]
                  </span>
                  <span className={`status-badge ${isDone ? 'live' : isWaiting ? 'waiting' : isFailed ? 'failed' : ''}`}>
                    {step.status}
                  </span>
                </div>
              </div>

              {step.output && (
                <div className="terminal-box" style={{ marginTop: '0.75rem' }}>
                  {step.output}
                </div>
              )}

              {isFailed && (
                <div style={{
                  marginTop: '0.75rem',
                  background: 'rgba(239, 68, 68, 0.1)',
                  border: '1px solid rgba(239, 68, 68, 0.3)',
                  padding: '1rem',
                  borderRadius: '8px'
                }}>
                  <div style={{ color: '#ef4444', fontWeight: 600, fontSize: '0.85rem', marginBottom: '0.25rem' }}>
                    Failure Reason: {step.error}
                  </div>
                  <div style={{ display: 'flex', gap: '0.5rem', marginTop: '0.75rem' }}>
                    <button className="btn btn-primary" style={{ padding: '0.4rem 0.8rem', fontSize: '0.8rem' }} onClick={() => handleRetry(step.id)}>
                      <RotateCcw size={14} /> Retry Step
                    </button>
                    <button className="btn btn-secondary" style={{ padding: '0.4rem 0.8rem', fontSize: '0.8rem' }} onClick={() => handleSkip(step.id)}>
                      <SkipForward size={14} /> Skip Step & Continue
                    </button>
                  </div>
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}
