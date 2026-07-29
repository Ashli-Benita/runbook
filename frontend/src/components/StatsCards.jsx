import React from 'react';
import { FileText, Play, CheckCircle, AlertTriangle, ShieldAlert } from 'lucide-react';

export default function StatsCards({ runbooksCount, executionsCount, successCount, pendingApprovalsCount }) {
  return (
    <div className="grid-4">
      <div className="stat-card">
        <div>
          <div className="stat-val">{runbooksCount}</div>
          <div className="stat-label">Available Runbooks</div>
        </div>
        <div className="stat-icon" style={{ background: 'rgba(59, 130, 246, 0.15)', color: '#3b82f6' }}>
          <FileText size={24} />
        </div>
      </div>

      <div className="stat-card">
        <div>
          <div className="stat-val">{executionsCount}</div>
          <div className="stat-label">Total Executions</div>
        </div>
        <div className="stat-icon" style={{ background: 'rgba(139, 92, 246, 0.15)', color: '#8b5cf6' }}>
          <Play size={24} />
        </div>
      </div>

      <div className="stat-card">
        <div>
          <div className="stat-val">{successCount}</div>
          <div className="stat-label">Successful Recoveries</div>
        </div>
        <div className="stat-icon" style={{ background: 'rgba(16, 185, 129, 0.15)', color: '#10b981' }}>
          <CheckCircle size={24} />
        </div>
      </div>

      <div className="stat-card" style={{ borderColor: pendingApprovalsCount > 0 ? '#f59e0b' : 'var(--border-color)' }}>
        <div>
          <div className="stat-val" style={{ color: pendingApprovalsCount > 0 ? '#f59e0b' : 'inherit' }}>
            {pendingApprovalsCount}
          </div>
          <div className="stat-label">Pending Human Approvals</div>
        </div>
        <div className="stat-icon" style={{ background: 'rgba(245, 158, 11, 0.15)', color: '#f59e0b' }}>
          <ShieldAlert size={24} />
        </div>
      </div>
    </div>
  );
}
