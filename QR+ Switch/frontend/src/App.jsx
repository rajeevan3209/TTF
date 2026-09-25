import { useState } from 'react'
import Dashboard from './pages/Dashboard.jsx'
import Participants from './pages/Participants.jsx'
import Simulator from './pages/Simulator.jsx'

const TABS = [
  { id: 'simulator', label: 'Transaction Simulator' },
  { id: 'dashboard', label: 'Dashboard' },
  { id: 'participants', label: 'Participants' },
]

export default function App() {
  const [activeTab, setActiveTab] = useState('simulator')

  return (
    <div className="app-shell">
      <header className="app-header">
        <h1>SGQR+ National Switch</h1>
        <p className="subtitle">Common inbound QR routing layer &middot; scheme identification &middot; participant validation &middot; on-us/off-us routing &middot; message translation</p>
      </header>

      <nav className="tab-bar">
        {TABS.map((tab) => (
          <button
            key={tab.id}
            className={`tab-button ${activeTab === tab.id ? 'active' : ''}`}
            onClick={() => setActiveTab(tab.id)}
          >
            {tab.label}
          </button>
        ))}
      </nav>

      <main className="app-main">
        {activeTab === 'simulator' && <Simulator />}
        {activeTab === 'dashboard' && <Dashboard />}
        {activeTab === 'participants' && <Participants />}
      </main>
    </div>
  )
}
