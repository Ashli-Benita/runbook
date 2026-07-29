import React from 'react';
import { CheckCircle, XCircle, ShieldAlert, Clock, ArrowLeft, Download, FileText } from 'lucide-react';

export default function ExecutionReportView({ report, onBack }) {
  if (!report) return null;

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <button className="btn btn-secondary" onClick={onBack}>
          <ArrowLeft size={16} /> Back to Dashboard
        </button>

        <button className="btn btn-primary" onClick={() => window.print()}>
          <Download size={16} /> Download Report Summary
        </button>
      </div>

      <div style={{ background: 'var(--bg-card)', border: '1px solid var(--border-color)', borderRadius: '16px', padding: '2rem' }}>
        {/* Header Summary */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', borderBottom: '1px solid var(--border-color)', pb: '1.5rem', marginBottom: '1.5rem' }}>
          <div>
            <span className="status-badge live" style={{ marginBottom: '0.5rem' }}>INCIDENT RESOLUTION REPORT</span>
            <h1 style={{ fontSize: '1.75rem', fontWeight: 700, margin: '0.25rem 0' }}>{report.runbookName}</h1>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
              Execution #{report.executionId} • Started: {new Date(report.startedAt).toLocaleString()}
            </p>
          </div>

          <div className={`status-badge ${report.finalStatus === 'COMPLETED' ? 'live' : report.finalStatus === 'WAITING_FOR_APPROVAL' ? 'waiting' : 'failed'}`} style={{ fontSize: '1rem', padding: '0.5rem 1rem' }}>
            {report.finalStatus}
          </div>
        </div>

        {/* Key Metrics Grid */}
        <div className="grid-4" style={{ marginBottom: '2rem' }}>
          <div style={{ background: 'var(--bg-primary)', padding: '1rem', borderRadius: '10px', border: '1px solid var(--border-color)' }}>
            <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Total Steps</div>
            <div style={{ fontSize: '1.5rem', fontWeight: 700 }}>{report.totalSteps}</div>
          </div>
          <div style={{ background: 'var(--bg-primary)', padding: '1rem', borderRadius: '10px', border: '1px solid var(--border-color)' }}>
            <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Successful</div>
            <div style={{ fontSize: '1.5rem', fontWeight: 700, color: '#10b981' }}>{report.successfulSteps}</div>
          </div>
          <div style={{ background: 'var(--bg-primary)', padding: '1rem', borderRadius: '10px', border: '1px solid var(--border-color)' }}>
            <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Human Approvals</div>
            <div style={{ fontSize: '1.5rem', fontWeight: 700, color: '#f59e0b' }}>{report.humanApprovalsCount}</div>
          </div>
          <div style={{ background: 'var(--bg-primary)', padding: '1rem', borderRadius: '10px', border: '1px solid var(--border-color)' }}>
            <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Duration</div>
            <div style={{ fontSize: '1.5rem', fontWeight: 700 }}>{report.totalDurationSeconds}s</div>
          </div>
        </div>

        {/* Steps Breakdown */}
        <h3 style={{ fontSize: '1.2rem', fontWeight: 600, marginBottom: '1rem' }}>Executed Steps Detail</h3>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', marginBottom: '2rem' }}>
          {report.steps.map((step) => (
            <div key={step.id} style={{
              background: 'var(--bg-primary)',
              border: '1px solid var(--border-color)',
              borderRadius: '10px',
              padding: '1rem 1.25rem'
            }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                <span style={{ fontWeight: 600, fontSize: '0.95rem' }}>
                  Step {step.stepNumber}: {step.description}
                </span>
                <span className={`status-badge ${step.status === 'COMPLETED' ? 'live' : 'failed'}`}>
                  {step.status}
                </span>
              </div>
              <div style={{ fontFamily: 'var(--font-mono)', fontSize: '0.8rem', color: '#93c5fd' }}>
                Action: {step.action} [{step.riskLevel}]
              </div>
              {step.output && (
                <div className="terminal-box" style={{ marginTop: '0.5rem', maxHeight: '100px' }}>
                  {step.output}
                </div>
              )}
            </div>
          ))}
        </div>

        {/* Audit Log Trail */}
        <h3 style={{ fontSize: '1.2rem', fontWeight: 600, marginBottom: '1rem' }}>Audit Log Trail</h3>
        <div className="terminal-box" style={{ maxHeight: '250px' }}>
          {report.auditSummary?.map((line, idx) => (
            <div key={idx}>{line}</div>
          ))}
        </div>
      </div>
    </div>
  );
}
