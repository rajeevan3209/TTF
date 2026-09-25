const BASE_URL = 'http://localhost:8080/api'

async function request(path, options = {}) {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })
  const isJson = res.headers.get('content-type')?.includes('application/json')
  const body = isJson ? await res.json() : null
  if (!res.ok) {
    const message = body?.message || `Request failed with status ${res.status}`
    const error = new Error(message)
    error.status = res.status
    error.body = body
    throw error
  }
  return body
}

export const api = {
  listSchemes: () => request('/schemes'),
  listFeeConfig: () => request('/fee-config'),
  listParticipants: () => request('/participants'),
  createParticipant: (participant) =>
    request('/participants', { method: 'POST', body: JSON.stringify(participant) }),
  listTransactions: () => request('/transactions'),
  submitInboundTransaction: (payload) =>
    request('/transactions/inbound', { method: 'POST', body: JSON.stringify(payload) }),
}
