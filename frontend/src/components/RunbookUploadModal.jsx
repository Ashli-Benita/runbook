import React, { useState } from 'react';
import { Upload, X, FileText, Check } from 'lucide-react';
import { api } from '../services/api';

export default function RunbookUploadModal({ isOpen, onClose, onUploaded }) {
  const [file, setFile] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState(null);

  if (!isOpen) return null;

  const handleUpload = async () => {
    if (!file) return;
    setUploading(true);
    setError(null);
    try {
      await api.uploadRunbook(file);
      onUploaded();
      onClose();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to upload Markdown runbook');
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-card">
        <div className="modal-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontWeight: 600 }}>
            <Upload size={18} color="#3b82f6" />
            Upload New Markdown Runbook
          </div>
          <button className="nav-btn" onClick={onClose} style={{ padding: '0.25rem' }}>
            <X size={18} />
          </button>
        </div>

        <div className="modal-body">
          {error && (
            <div style={{
              background: 'rgba(239, 68, 68, 0.15)',
              border: '1px solid rgba(239, 68, 68, 0.3)',
              color: '#ef4444',
              padding: '0.75rem 1rem',
              borderRadius: '8px',
              marginBottom: '1rem',
              fontSize: '0.85rem'
            }}>
              {error}
            </div>
          )}

          <div style={{
            border: '2px dashed var(--border-color)',
            borderRadius: '12px',
            padding: '2.5rem 1.5rem',
            textAlign: 'center',
            background: 'var(--bg-primary)',
            cursor: 'pointer'
          }}>
            <FileText size={40} color="#9ca3af" style={{ marginBottom: '1rem' }} />
            <div style={{ fontWeight: 600, marginBottom: '0.25rem' }}>
              Select Markdown File (.md)
            </div>
            <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '1rem' }}>
              Upload runbooks containing numbered procedure steps
            </div>
            <input
              type="file"
              accept=".md,.markdown"
              onChange={(e) => setFile(e.target.files[0])}
              style={{ display: 'none' }}
              id="file-input"
            />
            <label htmlFor="file-input" className="btn btn-secondary">
              Browse Files
            </label>
            {file && (
              <div style={{ marginTop: '1rem', color: '#10b981', fontSize: '0.9rem', fontWeight: 500 }}>
                Selected: {file.name}
              </div>
            )}
          </div>
        </div>

        <div className="modal-footer">
          <button className="btn btn-secondary" onClick={onClose} disabled={uploading}>
            Cancel
          </button>
          <button className="btn btn-primary" onClick={handleUpload} disabled={!file || uploading}>
            {uploading ? 'Processing...' : 'Upload & Parse'}
          </button>
        </div>
      </div>
    </div>
  );
}
