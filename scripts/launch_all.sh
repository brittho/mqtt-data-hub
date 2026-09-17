#!/usr/bin/env bash

# Function to handle clean exit when pressing CTRL+C
cleanup() {
  echo -e "\nStopping all MQTT publishers..."
  kill $(jobs -p) 2>/dev/null
  exit 0
}

trap cleanup SIGINT SIGTERM

echo "Starting 6 MQTT streams in parallel..."

# Run each publisher in the background (&)
./pub_stream.sh -t sensor/node01/ambient &
./pub_stream.sh -t sensor/node02/ambient &
./pub_stream.sh -t sensor/node02/ambient/pct -1 100 -2 100 &
./pub_stream.sh -t sensor/node03/grid_substation &
./pub_stream.sh -t sensor/node04/inverter &
./pub_stream.sh -t sensor/node04/inverter/pct -1 100 -2 100 &

echo "--------------------------------------------------------"
echo "All 6 publishers are running simultaneously!"
echo "Press [CTRL+C] in this terminal window to stop all of them."
echo "--------------------------------------------------------"

# Keep script active so SIGINT trap catches CTRL+C
wait
