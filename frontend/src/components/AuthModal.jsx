import React, { useState } from 'react';
import { api } from '../services/api';
import { X, Lock, User, Mail, ShieldAlert, CheckCircle2 } from 'lucide-react';

export default function AuthModal({ isOpen, onClose, onAuthSuccess }) {
  const [isLogin, setIsLogin] = useState(true);
  const [formData, setFormData] = useState({ username: '', email: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  if (!isOpen) return null;

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setError(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccess(null);

    try {
      if (isLogin) {
        const res = await api.login({ username: formData.username, password: formData.password });
        localStorage.setItem('jwt_token', res.token);
        localStorage.setItem('user_info', JSON.stringify({ username: res.username, email: res.email, role: res.role }));
        setSuccess('Login successful!');
        setTimeout(() => {
          onAuthSuccess(res);
          onClose();
        }, 500);
      } else {
        const res = await api.register(formData);
        localStorage.setItem('jwt_token', res.token);
        localStorage.setItem('user_info', JSON.stringify({ username: res.username, email: res.email, role: res.role }));
        setSuccess('Registration successful!');
        setTimeout(() => {
          onAuthSuccess(res);
          onClose();
        }, 500);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Authentication failed. Please check your details.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-container auth-modal" style={{ maxWidth: '440px' }}>
        <div className="modal-header">
          <div className="flex items-center gap-2">
            <Lock className="text-cyan" size={20} />
            <h2>{isLogin ? 'DevOps Agent Sign In' : 'Register Operator Account'}</h2>
          </div>
          <button className="btn-icon" onClick={onClose}>
            <X size={18} />
          </button>
        </div>

        <div className="modal-body">
          {error && (
            <div className="alert-box alert-error mb-4" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <ShieldAlert size={18} />
              <span>{error}</span>
            </div>
          )}

          {success && (
            <div className="alert-box alert-success mb-4" style={{ display: 'flex', alignItems: 'center', gap: '8px', background: 'rgba(34, 197, 94, 0.15)', color: '#4ade80', border: '1px solid rgba(34, 197, 94, 0.3)', padding: '10px 14px', borderRadius: '6px' }}>
              <CheckCircle2 size={18} />
              <span>{success}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="auth-form" style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
            <div className="form-group">
              <label className="form-label" style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '13px', marginBottom: '6px', color: '#94a3b8' }}>
                <User size={14} /> Username
              </label>
              <input
                type="text"
                name="username"
                className="form-input"
                placeholder="e.g. sre_operator"
                value={formData.username}
                onChange={handleChange}
                required
                style={{ width: '100%', padding: '10px 12px', background: '#0f172a', border: '1px solid #334155', borderRadius: '6px', color: '#f8fafc' }}
              />
            </div>

            {!isLogin && (
              <div className="form-group">
                <label className="form-label" style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '13px', marginBottom: '6px', color: '#94a3b8' }}>
                  <Mail size={14} /> Email Address
                </label>
                <input
                  type="email"
                  name="email"
                  className="form-input"
                  placeholder="sre@company.com"
                  value={formData.email}
                  onChange={handleChange}
                  required
                  style={{ width: '100%', padding: '10px 12px', background: '#0f172a', border: '1px solid #334155', borderRadius: '6px', color: '#f8fafc' }}
                />
              </div>
            )}

            <div className="form-group">
              <label className="form-label" style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '13px', marginBottom: '6px', color: '#94a3b8' }}>
                <Lock size={14} /> Password
              </label>
              <input
                type="password"
                name="password"
                className="form-input"
                placeholder="••••••••"
                value={formData.password}
                onChange={handleChange}
                required
                style={{ width: '100%', padding: '10px 12px', background: '#0f172a', border: '1px solid #334155', borderRadius: '6px', color: '#f8fafc' }}
              />
            </div>

            <button
              type="submit"
              className="btn btn-primary mt-2"
              disabled={loading}
              style={{ width: '100%', padding: '11px', marginTop: '10px', display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '8px' }}
            >
              {loading ? (
                <span>Authenticating...</span>
              ) : isLogin ? (
                <span>Sign In to Platform</span>
              ) : (
                <span>Create Account</span>
              )}
            </button>
          </form>

          <div style={{ textAlign: 'center', marginTop: '16px', fontSize: '13px', color: '#94a3b8' }}>
            {isLogin ? "Don't have an account? " : "Already have an account? "}
            <button
              type="button"
              style={{ background: 'none', border: 'none', color: '#38bdf8', cursor: 'pointer', textDecoration: 'underline', fontWeight: 500 }}
              onClick={() => { setIsLogin(!isLogin); setError(null); setSuccess(null); }}
            >
              {isLogin ? 'Register now' : 'Sign in'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
