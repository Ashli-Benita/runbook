import apiClient from '../api/axiosConfig';

export const api = {
  // Auth endpoints
  register: async (data) => {
    const res = await apiClient.post('/auth/register', data);
    return res.data;
  },
  login: async (credentials) => {
    const res = await apiClient.post('/auth/login', credentials);
    return res.data;
  },
  getCurrentUser: async () => {
    const res = await apiClient.get('/auth/me');
    return res.data;
  },

  // Runbook endpoints
  getRunbooks: async () => {
    const res = await apiClient.get('/runbooks');
    return res.data;
  },
  getRunbookById: async (id) => {
    const res = await apiClient.get(`/runbooks/${id}`);
    return res.data;
  },
  getParsedRunbook: async (id) => {
    const res = await apiClient.get(`/runbooks/${id}/parsed`);
    return res.data;
  },
  uploadRunbook: async (file) => {
    const formData = new FormData();
    formData.append('file', file);
    const res = await apiClient.post('/runbooks/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    return res.data;
  },

  // Execution endpoints
  startExecution: async (runbookId) => {
    const res = await apiClient.post('/executions', { runbookId });
    return res.data;
  },
  getExecutions: async () => {
    const res = await apiClient.get('/executions');
    return res.data;
  },
  getExecutionById: async (id) => {
    const res = await apiClient.get(`/executions/${id}`);
    return res.data;
  },
  getExecutionSteps: async (id) => {
    const res = await apiClient.get(`/executions/${id}/steps`);
    return res.data;
  },
  getPendingApprovals: async () => {
    const res = await apiClient.get('/executions/pending-approvals');
    return res.data;
  },
  approveStep: async (id) => {
    const res = await apiClient.post(`/executions/${id}/approve`);
    return res.data;
  },
  rejectStep: async (id, userChoice = 'STOP') => {
    const res = await apiClient.post(`/executions/${id}/reject`, { userChoice });
    return res.data;
  },
  cancelExecution: async (id) => {
    const res = await apiClient.post(`/executions/${id}/cancel`);
    return res.data;
  },
  retryStep: async (executionId, stepId) => {
    const res = await apiClient.post(`/executions/${executionId}/steps/${stepId}/retry`);
    return res.data;
  },
  skipStep: async (executionId, stepId) => {
    const res = await apiClient.post(`/executions/${executionId}/steps/${stepId}/skip`);
    return res.data;
  },
  getExecutionReport: async (id) => {
    const res = await apiClient.get(`/executions/${id}/report`);
    return res.data;
  },

  // Audit endpoints
  getAuditLogsByExecution: async (id) => {
    const res = await apiClient.get(`/audit/execution/${id}`);
    return res.data;
  },
  getRecentAuditLogs: async () => {
    const res = await apiClient.get('/audit/recent');
    return res.data;
  }
};
