import React from 'react';
import { Terminal, Shield, Activity, FileText, AlertTriangle } from 'lucide-react';

export default function Navbar({ activeTab, setActiveTab, pendingCount }) {
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

      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
        <div className="status-badge live">
          <span style={{ width: 8, height: 8, borderRadius: '50%', background: '#10b981', display: 'inline-block' }}></span>
          Ollama LLM Active
        </div>
        <div className="status-badge live" style={{ borderColor: 'rgba(59, 130, 246, 0.3)', color: '#60a5fa' }}>
          <Shield size={12} />
          MCP Guardrail Enabled
        </div>
      </div>
    </header>
  );
}
