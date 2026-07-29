import axios from 'axios';

const API_BASE = import.meta.env.VITE_API_URL || import.meta.env.VITE_API_BASE_URL || '/api';

export const api = {
  // Runbook endpoints
  getRunbooks: async () => {
    const res = await axios.get(`${API_BASE}/runbooks`);
    return res.data;
  },
  getRunbookById: async (id) => {
    const res = await axios.get(`${API_BASE}/runbooks/${id}`);
    return res.data;
  },
  getParsedRunbook: async (id) => {
    const res = await axios.get(`${API_BASE}/runbooks/${id}/parsed`);
    return res.data;
  },
  uploadRunbook: async (file) => {
    const formData = new FormData();
    formData.append('file', file);
    const res = await axios.post(`${API_BASE}/runbooks/upload`, formData);
    return res.data;
  },

  // Execution endpoints
  startExecution: async (runbookId) => {
    const res = await axios.post(`${API_BASE}/executions`, { runbookId });
    return res.data;
  },
  getExecutions: async () => {
    const res = await axios.get(`${API_BASE}/executions`);
    return res.data;
  },
  getExecutionById: async (id) => {
    const res = await axios.get(`${API_BASE}/executions/${id}`);
    return res.data;
  },
  getExecutionSteps: async (id) => {
    const res = await axios.get(`${API_BASE}/executions/${id}/steps`);
    return res.data;
  },
  getPendingApprovals: async () => {
    const res = await axios.get(`${API_BASE}/executions/pending-approvals`);
    return res.data;
  },
  approveStep: async (id) => {
    const res = await axios.post(`${API_BASE}/executions/${id}/approve`);
    return res.data;
  },
  rejectStep: async (id, userChoice = 'STOP') => {
    const res = await axios.post(`${API_BASE}/executions/${id}/reject`, { userChoice });
    return res.data;
  },
  cancelExecution: async (id) => {
    const res = await axios.post(`${API_BASE}/executions/${id}/cancel`);
    return res.data;
  },
  retryStep: async (executionId, stepId) => {
    const res = await axios.post(`${API_BASE}/executions/${executionId}/steps/${stepId}/retry`);
    return res.data;
  },
  skipStep: async (executionId, stepId) => {
    const res = await axios.post(`${API_BASE}/executions/${executionId}/steps/${stepId}/skip`);
    return res.data;
  },
  getExecutionReport: async (id) => {
    const res = await axios.get(`${API_BASE}/executions/${id}/report`);
    return res.data;
  },

  // Audit endpoints
  getAuditLogsByExecution: async (id) => {
    const res = await axios.get(`${API_BASE}/audit/execution/${id}`);
    return res.data;
  },
  getRecentAuditLogs: async () => {
    const res = await axios.get(`${API_BASE}/audit/recent`);
    return res.data;
  }
};
