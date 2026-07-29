import React, { useEffect, useState } from 'react';
import { Shield, RefreshCw } from 'lucide-react';
import { api } from '../services/api';

export default function AuditLogView() {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(false);

  const fetchAuditLogs = async () => {
    setLoading(true);
    try {
      const data = await api.getRecentAuditLogs();
      setLogs(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAuditLogs();
  }, []);

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 700 }}>Security Audit Logs</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
            Immutable security event trail & decision record
          </p>
        </div>

        <button className="btn btn-secondary" onClick={fetchAuditLogs} disabled={loading}>
          <RefreshCw size={16} className={loading ? 'spin' : ''} /> Refresh Logs
        </button>
      </div>

      <div className="terminal-box" style={{ maxHeight: '600px', padding: '1.25rem' }}>
        {logs.map((log) => (
          <div key={log.id} style={{ marginBottom: '0.5rem', paddingBottom: '0.5rem', borderBottom: '1px solid #1e293b', display: 'flex', gap: '1rem' }}>
            <span style={{ color: '#64748b' }}>[{new Date(log.timestamp).toLocaleTimeString()}]</span>
            <span style={{ color: log.eventType.includes('REJECT') || log.eventType.includes('FAIL') ? '#f87171' : log.eventType.includes('APPROV') ? '#fbbf24' : '#60a5fa', fontWeight: 600, width: '180px' }}>
              {log.eventType}
            </span>
            <span style={{ color: '#e2e8f0', flex: 1 }}>{log.message}</span>
          </div>
        ))}
      </div>
    </div>
  );
}
