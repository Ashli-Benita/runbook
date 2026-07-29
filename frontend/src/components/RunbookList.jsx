import React, { useState } from 'react';
import { FileText, Play, Eye, Plus, CheckCircle2 } from 'lucide-react';
import RunbookUploadModal from './RunbookUploadModal';

export default function RunbookList({ runbooks, onSelectRunbook, onStartExecution, onRefresh }) {
  const [isUploadOpen, setIsUploadOpen] = useState(false);
  const [previewRunbook, setPreviewRunbook] = useState(null);

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 700 }}>Runbook Procedures</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
            Structured SRE recovery playbooks available for execution
          </p>
        </div>

        <button className="btn btn-primary" onClick={() => setIsUploadOpen(true)}>
          <Plus size={16} /> Upload Runbook
        </button>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: '1.5rem' }}>
        {runbooks.map((rb) => (
          <div key={rb.id} className="stat-card" style={{ flexDirection: 'column', alignItems: 'flex-start', gap: '1rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', width: '100%', alignItems: 'flex-start' }}>
              <div className="stat-icon" style={{ background: 'rgba(59, 130, 246, 0.15)', color: '#3b82f6' }}>
                <FileText size={22} />
              </div>
              <span className="status-badge live" style={{ fontSize: '0.7rem' }}>{rb.fileName}</span>
            </div>

            <div>
              <h3 style={{ fontSize: '1.1rem', fontWeight: 600, marginBottom: '0.375rem' }}>{rb.name}</h3>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', lineClamp: 2, display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>
                {rb.description}
              </p>
            </div>

            <div style={{ display: 'flex', gap: '0.5rem', width: '100%', marginTop: 'auto' }}>
              <button
                className="btn btn-secondary"
                style={{ flex: 1, fontSize: '0.8rem' }}
                onClick={() => setPreviewRunbook(rb)}
              >
                <Eye size={14} /> Preview
              </button>
              <button
                className="btn btn-primary"
                style={{ flex: 1, fontSize: '0.8rem' }}
                onClick={() => onStartExecution(rb.id)}
              >
                <Play size={14} /> Execute
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Preview Modal */}
      {previewRunbook && (
        <div className="modal-overlay">
          <div className="modal-card" style={{ maxWidth: '700px' }}>
            <div className="modal-header">
              <div style={{ fontWeight: 600 }}>{previewRunbook.name}</div>
              <button className="nav-btn" onClick={() => setPreviewRunbook(null)}>X</button>
            </div>
            <div className="modal-body">
              <div className="terminal-box" style={{ maxHeight: '400px' }}>
                {previewRunbook.content}
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={() => setPreviewRunbook(null)}>Close</button>
              <button className="btn btn-primary" onClick={() => {
                const id = previewRunbook.id;
                setPreviewRunbook(null);
                onStartExecution(id);
              }}>
                <Play size={14} /> Start Runbook Agent
              </button>
            </div>
          </div>
        </div>
      )}

      <RunbookUploadModal
        isOpen={isUploadOpen}
        onClose={() => setIsUploadOpen(false)}
        onUploaded={onRefresh}
      />
    </div>
  );
}
