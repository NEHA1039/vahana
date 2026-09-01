const API_BASE = "http://localhost:8080/api/vehicles";

export async function getVehicles() {
  const res = await fetch(API_BASE);
  if (!res.ok) throw new Error("Failed to fetch vehicles");
  return res.json();
}

export async function getHistory(vehicleId, limit = 50) {
  const res = await fetch(`${API_BASE}/${vehicleId}/history?limit=${limit}`);
  if (!res.ok) throw new Error("Failed to fetch history");
  return res.json();
}
