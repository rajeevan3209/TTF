import { useEffect, useState } from 'react'
import { api } from '../api/client.js'

const PARTICIPANT_TYPES = ['ACQUIRER', 'ISSUER', 'BOTH']

export default function Participants() {
  const [participants, setParticipants] = useState([])
  const [schemes, setSchemes] = useState([])
  const [error, setError] = useState(null)
  const [form, setForm] = useState({ name: '', schemeCode: '', participantType: 'BOTH' })
  const [submitting, setSubmitting] = useState(false)

  function load() {
    Promise.all([api.listParticipants(), api.listSchemes()])
      .then(([p, s]) => {
        setParticipants(p)
        setSchemes(s)
        setForm((f) => ({ ...f, schemeCode: f.schemeCode || s[0]?.code || '' }))
      })
      .catch((e) => setError(e.message))
  }

  useEffect(() => {
    load()
  }, [])

  async function handleSubmit(e) {
    e.preventDefault()
    setError(null)
    setSubmitting(true)
    try {
      await api.createParticipant(form)
      setForm((f) => ({ ...f, name: '' }))
      load()
    } catch (err) {
      setError(err.message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="page">
      <h2>Participants</h2>
      <p className="muted">
        Schemes and participants registered with the SGQR+ national switch. New participants are
        onboarded as ACTIVE by default.
      </p>

      {error && <div className="card error">{error}</div>}

      <form className="card inline-form" onSubmit={handleSubmit}>
        <input
          placeholder="Participant name"
          value={form.name}
          onChange={(e) => setForm({ ...form, name: e.target.value })}
          required
        />
        <select
          value={form.schemeCode}
          onChange={(e) => setForm({ ...form, schemeCode: e.target.value })}
        >
          {schemes.map((s) => (
            <option key={s.code} value={s.code}>
              {s.name}
            </option>
          ))}
        </select>
        <select
          value={form.participantType}
          onChange={(e) => setForm({ ...form, participantType: e.target.value })}
        >
          {PARTICIPANT_TYPES.map((t) => (
            <option key={t} value={t}>
              {t}
            </option>
          ))}
        </select>
        <button type="submit" disabled={submitting}>
          {submitting ? 'Adding…' : 'Add participant'}
        </button>
      </form>

      <table className="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Scheme</th>
            <th>Type</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          {participants.map((p) => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>{p.name}</td>
              <td>{p.schemeName}</td>
              <td>{p.participantType}</td>
              <td>
                <span className={`badge ${p.status === 'ACTIVE' ? 'on-us' : 'off-us'}`}>
                  {p.status}
                </span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
