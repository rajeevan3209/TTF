import { useEffect, useState } from 'react'
import { api } from '../api/client.js'

export default function Simulator() {
  const [participants, setParticipants] = useState([])
  const [feeConfigs, setFeeConfigs] = useState([])
  const [acquirerId, setAcquirerId] = useState('')
  const [issuerId, setIssuerId] = useState('')
  const [amount, setAmount] = useState('100.00')
  const [result, setResult] = useState(null)
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    api.listParticipants().then(setParticipants).catch((e) => setError(e.message))
    api.listFeeConfig().then(setFeeConfigs).catch((e) => setError(e.message))
  }, [])

  const acquirer = participants.find((p) => String(p.id) === String(acquirerId))
  const issuer = participants.find((p) => String(p.id) === String(issuerId))
  const predictedOnUs = acquirer && issuer ? acquirer.schemeCode === issuer.schemeCode : null

  const standardFeeConfig = feeConfigs.find((f) => f.tier === 'STANDARD') ?? feeConfigs[0]
  const standardFeePercentage = Number(standardFeeConfig?.feePercentage ?? 0)
  const standardFeeCap = Number(standardFeeConfig?.feeCap ?? 100)
  const parsedAmount = parseFloat(amount)
  const hasValidPreview = predictedOnUs !== null && !Number.isNaN(parsedAmount) && parsedAmount > 0
  const previewFee = hasValidPreview
    ? predictedOnUs
      ? 0
      : Math.min(parsedAmount * standardFeePercentage, standardFeeCap)
    : null
  const previewFeeCapped = hasValidPreview && !predictedOnUs && parsedAmount * standardFeePercentage > standardFeeCap
  const previewTotal = hasValidPreview ? parsedAmount + previewFee : null

  async function handleSubmit(e) {
    e.preventDefault()
    setError(null)
    setResult(null)
    if (!acquirer || !issuer) {
      setError('Select both an acquirer and an issuer participant.')
      return
    }
    setLoading(true)
    try {
      const qrPayload = `${acquirer.schemeCode}|MERCHANT-${acquirer.id}-${Date.now()}`
      const response = await api.submitInboundTransaction({
        qrPayload,
        acquirerParticipantId: acquirer.id,
        issuerParticipantId: issuer.id,
        amount: parseFloat(amount),
      })
      setResult(response)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page">
      <h2>Simulate an inbound QR scan</h2>
      <p className="muted">
        Pick the merchant's acquirer (where the QR was presented) and the consumer's issuer
        (their payment app) to see how the SGQR+ pipeline identifies the scheme, validates both
        participants, routes the transaction as on-us or off-us, and translates the message for
        the destination acquirer switch.
      </p>

      <form className="card" onSubmit={handleSubmit}>
        <div className="form-row">
          <label>
            Acquirer participant (merchant side)
            <select value={acquirerId} onChange={(e) => setAcquirerId(e.target.value)} required>
              <option value="">Select acquirer&hellip;</option>
              {participants
                .filter((p) => p.participantType === 'ACQUIRER' || p.participantType === 'BOTH')
                .map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.name} ({p.schemeCode}) &mdash; {p.status}
                  </option>
                ))}
            </select>
          </label>

          <label>
            Issuer participant (consumer side)
            <select value={issuerId} onChange={(e) => setIssuerId(e.target.value)} required>
              <option value="">Select issuer&hellip;</option>
              {participants
                .filter((p) => p.participantType === 'ISSUER' || p.participantType === 'BOTH')
                .map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.name} ({p.schemeCode}) &mdash; {p.status}
                  </option>
                ))}
            </select>
          </label>

          <label>
            Amount (SGD)
            <input
              type="number"
              step="0.01"
              min="0.01"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              required
            />
          </label>
        </div>

        {predictedOnUs !== null && (
          <p className={`hint ${predictedOnUs ? 'on-us' : 'off-us'}`}>
            {predictedOnUs
              ? 'Same network on both sides → expected ON-US, no fee.'
              : `Different networks → expected OFF-US, ${(standardFeePercentage * 100).toFixed(2)}% fee applies.`}
          </p>
        )}

        {hasValidPreview && (
          <div className="cost-breakdown">
            <div className="cost-line">
              <span>Transaction amount</span>
              <span>SGD {parsedAmount.toFixed(2)}</span>
            </div>
            <div className="cost-line">
              <span>
                Transaction cost{' '}
                {!predictedOnUs && (
                  <span className="muted">
                    ({previewFeeCapped
                      ? `capped at SGD ${standardFeeCap.toFixed(2)}`
                      : `${(standardFeePercentage * 100).toFixed(2)}% off-us fee`})
                  </span>
                )}
              </span>
              <span>SGD {previewFee.toFixed(4)}</span>
            </div>
            <div className="cost-line total">
              <span>Total amount</span>
              <span>SGD {previewTotal.toFixed(4)}</span>
            </div>
          </div>
        )}

        <button type="submit" disabled={loading}>
          {loading ? 'Processing…' : 'Submit transaction'}
        </button>
      </form>

      {error && <div className="card error">{error}</div>}

      {result && (
        <div className={`card result ${result.onUs ? 'on-us' : 'off-us'}`}>
          <h3>{result.onUs ? 'ON-US transaction' : 'OFF-US transaction'}</h3>
          <dl>
            <dt>Transaction ID</dt>
            <dd>{result.id}</dd>
            <dt>Acquirer</dt>
            <dd>{result.acquirerParticipantName} ({result.acquirerSchemeCode})</dd>
            <dt>Issuer</dt>
            <dd>{result.issuerParticipantName} ({result.issuerSchemeCode})</dd>
            <dt>Amount</dt>
            <dd>SGD {Number(result.amount).toFixed(2)}</dd>
            <dt>Fee applied</dt>
            <dd>SGD {Number(result.feeAmount).toFixed(4)}</dd>
            <dt>Total amount</dt>
            <dd className="total-amount">
              SGD {(Number(result.amount) + Number(result.feeAmount)).toFixed(4)}
            </dd>
            <dt>Destination switch</dt>
            <dd>{result.destinationSchemeCode}</dd>
            <dt>Translated message</dt>
            <dd><code>{result.translatedMessage}</code></dd>
            <dt>Status</dt>
            <dd>{result.status}</dd>
          </dl>
        </div>
      )}
    </div>
  )
}
