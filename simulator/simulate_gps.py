"""
Vahana GPS ping simulator.

Simulates a handful of vehicles moving along a straight-line route between two
points, sending a location ping to the backend every few seconds. Its only job
is to make the "real-time tracking" demo look alive.

Usage:
    pip install requests
    python simulate_gps.py
"""

import time
import random
from datetime import datetime, timezone

import requests

API_BASE = "http://localhost:8080/api/vehicles"
PING_INTERVAL_SECONDS = 4
STEPS = 40  # how many pings per vehicle before the script stops

# Each vehicle: (registrationNumber, start_lat, start_lon, end_lat, end_lon)
# Route roughly spans central Pune as a plausible-looking path.
ROUTES = [
    {"reg": "MH12AB1234", "start": (18.5204, 73.8567), "end": (18.5679, 73.9143)},
    {"reg": "MH14CD5678", "start": (18.5089, 73.8258), "end": (18.5314, 73.8446)},
    {"reg": "MH04EF9012", "start": (18.4633, 73.8283), "end": (18.5018, 73.8636)},
]


def get_or_register_vehicle(reg_number, driver_name, vehicle_type):
    resp = requests.get(API_BASE)
    resp.raise_for_status()
    for v in resp.json():
        if v["registrationNumber"] == reg_number:
            return v["id"]

    resp = requests.post(API_BASE, json={
        "registrationNumber": reg_number,
        "driverName": driver_name,
        "vehicleType": vehicle_type,
    })
    resp.raise_for_status()
    return resp.json()["id"]


def interpolate(start, end, fraction):
    lat = start[0] + (end[0] - start[0]) * fraction
    lon = start[1] + (end[1] - start[1]) * fraction
    return lat, lon


def run():
    vehicle_ids = {}
    drivers = ["Ramesh Kulkarni", "Sunita Pawar", "Amit Deshmukh"]
    types = ["TRUCK", "VAN", "CAR"]

    for i, route in enumerate(ROUTES):
        vid = get_or_register_vehicle(route["reg"], drivers[i], types[i])
        vehicle_ids[route["reg"]] = vid
        print(f"Vehicle {route['reg']} -> id {vid}")

    print(f"\nSimulating {len(ROUTES)} vehicles, {STEPS} pings each, "
          f"every {PING_INTERVAL_SECONDS}s. Ctrl+C to stop.\n")

    for step in range(STEPS):
        fraction = step / (STEPS - 1)
        for route in ROUTES:
            vid = vehicle_ids[route["reg"]]
            lat, lon = interpolate(route["start"], route["end"], fraction)
            # small jitter so it doesn't look perfectly linear
            lat += random.uniform(-0.0005, 0.0005)
            lon += random.uniform(-0.0005, 0.0005)
            speed = round(random.uniform(20, 60), 1)

            payload = {
                "latitude": round(lat, 6),
                "longitude": round(lon, 6),
                "timestamp": datetime.now(timezone.utc).isoformat(),
                "speed": speed,
            }
            try:
                resp = requests.post(f"{API_BASE}/{vid}/location", json=payload)
                resp.raise_for_status()
                print(f"[{route['reg']}] step {step+1}/{STEPS} -> "
                      f"({payload['latitude']}, {payload['longitude']}) speed={speed}")
            except requests.RequestException as e:
                print(f"[{route['reg']}] FAILED to send ping: {e}")

        time.sleep(PING_INTERVAL_SECONDS)

    print("\nSimulation complete.")


if __name__ == "__main__":
    run()
