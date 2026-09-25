import { useEffect, useMemo, useState } from 'react'
import { api } from '../api/client.js'

export default function Dashboard() {
  const [transactions, setTransactions] = useState([])
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(true)

  function load() {
    setLoading(true)
    api
      .listTransactions()
      .then(setTransactions)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    load()
  }, [])

  const stats = useMemo(() => {
    const onUs = transactions.filter((t) => t.onUs)
    const offUs = transactions.filter((t) => !t.onUs)
    const totalFees = offUs.reduce((sum, t) => sum + Number(t.feeAmount), 0)
    const totalVolume = transactions.reduce((sum, t) => sum + Number(t.amount), 0)
    return {
      count: transactions.length,
      onUsCount: onUs.length,
      offUsCount: offUs.length,
      totalFees,
      totalVolume,
    }
  }, [transactions])

  return (
    <div className="page">
      <div className="page-header-row">
        <h2>Switch Dashboard</h2>
        <button onClick={load} disabled={loading}>
          {loading ? 'Refreshing…' : 'Refresh'}
        </button>
      </div>

      {error && <div className="card error">{error}</div>}

      <div className="stat-grid">
        <div className="stat-card">
          <span className="stat-value">{stats.count}</span>
          <span className="stat-label">Total transactions</span>
        </div>
        <div className="stat-card">
          <span className="stat-value">{stats.onUsCount}</span>
          <span className="stat-label">On-us</span>
        </div>
        <div className="stat-card">
          <span className="stat-value">{stats.offUsCount}</span>
          <span className="stat-label">Off-us</span>
        </div>
        <div className="stat-card">
          <span className="stat-value">SGD {stats.totalVolume.toFixed(2)}</span>
          <span className="stat-label">Total volume</span>
        </div>
        <div className="stat-card">
          <span className="stat-value">SGD {stats.totalFees.toFixed(4)}</span>
          <span className="stat-label">Fees collected (off-us)</span>
        </div>
      </div>

      <table className="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Acquirer</th>
            <th>Issuer</th>
            <th>Amount</th>
            <th>Type</th>
            <th>Fee</th>
            <th>Destination</th>
            <th>Status</th>
            <th>Created</th>
          </tr>
        </thead>
        <tbody>
          {transactions.map((t) => (
            <tr key={t.id}>
              <td>{t.id}</td>
              <td>{t.acquirerParticipantName} <span className="muted">({t.acquirerSchemeCode})</span></td>
              <td>{t.issuerParticipantName} <span className="muted">({t.issuerSchemeCode})</span></td>
              <td>SGD {Number(t.amount).toFixed(2)}</td>
              <td>
                <span className={`badge ${t.onUs ? 'on-us' : 'off-us'}`}>
                  {t.onUs ? 'ON-US' : 'OFF-US'}
                </span>
              </td>
              <td>SGD {Number(t.feeAmount).toFixed(4)}</td>
              <td>{t.destinationSchemeCode}</td>
              <td>{t.status}</td>
              <td>{new Date(t.createdAt).toLocaleString()}</td>
            </tr>
          ))}
          {transactions.length === 0 && !loading && (
            <tr>
              <td colSpan={9} className="muted">No transactions yet. Try the Simulator tab.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  )
}
