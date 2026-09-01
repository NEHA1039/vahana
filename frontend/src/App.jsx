import { useEffect, useState } from "react";
import { getVehicles, getHistory } from "./api";

const POLL_INTERVAL_MS = 4000;

export default function App() {
  const [vehicles, setVehicles] = useState([]);
  const [selectedId, setSelectedId] = useState(null);
  const [history, setHistory] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    let cancelled = false;

    async function poll() {
      try {
        const data = await getVehicles();
        if (!cancelled) {
          setVehicles(data);
          setError(null);
        }
      } catch (err) {
        if (!cancelled) setError(err.message);
      }
    }

    poll();
    const interval = setInterval(poll, POLL_INTERVAL_MS);
    return () => {
      cancelled = true;
      clearInterval(interval);
    };
  }, []);

  useEffect(() => {
    if (selectedId == null) return;
    getHistory(selectedId, 20).then(setHistory).catch((err) => setError(err.message));
  }, [selectedId, vehicles]);

  return (
    <div style={{ fontFamily: "sans-serif", padding: "24px", maxWidth: 900, margin: "0 auto" }}>
      <h1>Vahana — Live Vehicle Tracking</h1>
      {error && <p style={{ color: "red" }}>Error: {error}</p>}

      <table border="1" cellPadding="8" style={{ borderCollapse: "collapse", width: "100%" }}>
        <thead>
          <tr>
            <th>Registration</th>
            <th>Driver</th>
            <th>Type</th>
            <th>Status</th>
            <th>Latitude</th>
            <th>Longitude</th>
            <th>Last Updated</th>
          </tr>
        </thead>
        <tbody>
          {vehicles.map((v) => (
            <tr
              key={v.id}
              onClick={() => setSelectedId(v.id)}
              style={{ cursor: "pointer", background: v.id === selectedId ? "#eef" : "white" }}
            >
              <td>{v.registrationNumber}</td>
              <td>{v.driverName}</td>
              <td>{v.vehicleType}</td>
              <td>{v.status}</td>
              <td>{v.latitude ?? "—"}</td>
              <td>{v.longitude ?? "—"}</td>
              <td>{v.lastUpdated ? new Date(v.lastUpdated).toLocaleTimeString() : "—"}</td>
            </tr>
          ))}
        </tbody>
      </table>

      {selectedId != null && (
        <div style={{ marginTop: 24 }}>
          <h2>Recent history — vehicle #{selectedId}</h2>
          <ul>
            {history.map((p) => (
              <li key={p.id}>
                {new Date(p.timestamp).toLocaleTimeString()} — ({p.latitude}, {p.longitude})
                {p.speed != null ? ` @ ${p.speed} km/h` : ""}
              </li>
            ))}
          </ul>
        </div>
      )}

      <p style={{ color: "#888", marginTop: 24 }}>
        Click a row to see its recent location history. Table refreshes every {POLL_INTERVAL_MS / 1000}s.
      </p>
    </div>
  );
}
