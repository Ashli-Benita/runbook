import React, { useState } from 'react';
import { AlertTriangle, ShieldAlert, CheckCircle, XCircle } from 'lucide-react';
import { api } from '../services/api';

export default function ApprovalModal({ approval, onResumed }) {
  const [loading, setLoading] = useState(false);
  const [rejectOption, setRejectOption] = useState('STOP');

  if (!approval) return null;

  const handleApprove = async () => {
    setLoading(true);
    try {
      await api.approveStep(approval.executionId);
      onResumed();
    } catch (err) {
      alert('Error approving step: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleReject = async () => {
    setLoading(true);
    try {
      await api.rejectStep(approval.executionId, rejectOption);
      onResumed();
    } catch (err) {
      alert('Error rejecting step: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-card" style={{ borderColor: 'var(--accent-yellow)', boxShadow: '0 0 25px rgba(245, 158, 11, 0.3)' }}>
        <div className="modal-header" style={{ background: 'rgba(245, 158, 11, 0.1)', borderBottomColor: 'rgba(245, 158, 11, 0.3)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#f59e0b', fontWeight: 700, fontSize: '1.1rem' }}>
            <ShieldAlert size={22} />
            Human Approval Required
          </div>
          <span className="status-badge waiting">RISK LEVEL: {approval.riskLevel}</span>
        </div>

        <div className="modal-body">
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginBottom: '1.25rem' }}>
            The AI Agent encountered a potentially dangerous system operation and paused execution. Please review the proposed action:
          </p>

          <div style={{
            background: 'var(--bg-primary)',
            border: '1px solid var(--border-color)',
            borderRadius: '10px',
            padding: '1rem 1.25rem',
            marginBottom: '1.25rem'
          }}>
            <div style={{ fontSize: '0.75rem', color: 'var(--text-dim)', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
              Action Type
            </div>
            <div style={{ fontSize: '1.1rem', fontWeight: 700, color: '#60a5fa', marginBottom: '0.75rem', fontFamily: 'var(--font-mono)' }}>
              {approval.action}
            </div>

            <div style={{ fontSize: '0.75rem', color: 'var(--text-dim)', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
              Description & Purpose
            </div>
            <div style={{ fontSize: '0.95rem', color: 'var(--text-main)' }}>
              {approval.reason}
            </div>
          </div>

          <div style={{ background: 'rgba(59, 130, 246, 0.08)', border: '1px solid rgba(59, 130, 246, 0.2)', padding: '0.85rem 1rem', borderRadius: '8px', fontSize: '0.85rem', color: '#93c5fd' }}>
            <strong>Security Guarantee:</strong> This action will be executed exclusively through the controlled MCP tool wrapper after your explicit authorization.
          </div>
        </div>

        <div className="modal-footer" style={{ justifyContent: 'space-between' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <select
              value={rejectOption}
              onChange={(e) => setRejectOption(e.target.value)}
              style={{
                background: 'var(--bg-primary)',
                color: 'var(--text-main)',
                border: '1px solid var(--border-color)',
                padding: '0.5rem',
                borderRadius: '6px',
                fontSize: '0.8rem'
              }}
            >
              <option value="STOP">Reject & Stop Runbook</option>
              <option value="SKIP">Reject & Skip Step</option>
            </select>
            <button className="btn btn-danger" onClick={handleReject} disabled={loading}>
              <XCircle size={16} /> Reject
            </button>
          </div>

          <button className="btn btn-success" onClick={handleApprove} disabled={loading}>
            <CheckCircle size={16} /> {loading ? 'Executing...' : 'Approve & Execute'}
          </button>
        </div>
      </div>
    </div>
  );
}
