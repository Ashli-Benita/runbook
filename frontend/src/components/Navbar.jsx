import React from 'react';
import { Terminal, Shield, Activity, FileText, User, LogOut, Lock } from 'lucide-react';

export default function Navbar({ activeTab, setActiveTab, pendingCount, user, onOpenAuth, onLogout }) {
  return (
    <header className="navbar">
      <div className="brand">
        <div className="brand-icon">
          <Terminal size={22} />
        </div>
        <div>
          <div className="brand-title">AI Runbook Agent</div>
          <div className="brand-subtitle">DevOps / SRE Autonomous Operations</div>
        </div>
      </div>

      <nav className="nav-links">
        <button
          className={`nav-btn ${activeTab === 'dashboard' ? 'active' : ''}`}
          onClick={() => setActiveTab('dashboard')}
        >
          <Activity size={16} />
          Dashboard
        </button>

        <button
          className={`nav-btn ${activeTab === 'executions' ? 'active' : ''}`}
          onClick={() => setActiveTab('executions')}
        >
          <Terminal size={16} />
          Executions
          {pendingCount > 0 && (
            <span style={{
              background: '#ef4444',
              color: 'white',
              fontSize: '0.7rem',
              padding: '0.1rem 0.4rem',
              borderRadius: '999px',
              fontWeight: 700
            }}>
              {pendingCount}
            </span>
          )}
        </button>

        <button
          className={`nav-btn ${activeTab === 'runbooks' ? 'active' : ''}`}
          onClick={() => setActiveTab('runbooks')}
        >
          <FileText size={16} />
          Runbooks
        </button>

        <button
          className={`nav-btn ${activeTab === 'audit' ? 'active' : ''}`}
          onClick={() => setActiveTab('audit')}
        >
          <Shield size={16} />
          Audit Logs
        </button>
      </nav>

      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
        <div className="status-badge live" style={{ borderColor: 'rgba(59, 130, 246, 0.3)', color: '#60a5fa' }}>
          <Shield size={12} />
          MCP Guardrail Enabled
        </div>

        {user ? (
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', background: '#0f172a', border: '1px solid #334155', borderRadius: '6px', padding: '4px 10px' }}>
            <User size={14} className="text-cyan" />
            <span style={{ fontSize: '13px', fontWeight: 600, color: '#f8fafc' }}>{user.username}</span>
            <button
              onClick={onLogout}
              title="Sign Out"
              style={{ background: 'none', border: 'none', color: '#94a3b8', cursor: 'pointer', display: 'flex', alignItems: 'center', padding: '2px', marginLeft: '4px' }}
            >
              <LogOut size={14} />
            </button>
          </div>
        ) : (
          <button
            onClick={onOpenAuth}
            className="btn btn-secondary"
            style={{ display: 'flex', alignItems: 'center', gap: '6px', padding: '6px 12px', fontSize: '13px' }}
          >
            <Lock size={14} />
            Sign In
          </button>
        )}
      </div>
    </header>
  );
}
