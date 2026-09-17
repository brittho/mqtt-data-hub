#!/usr/bin/env bash

# Default Values
BROKER="127.0.0.1"
PORT="1883"
TOPIC="sensor/node01/ambient"
VAL1_MAX=255
VAL2_MAX=100
INTERVAL=0.05

# Display help message
usage() {
  echo "Usage: $0 [FLAGS]"
  echo "Flags:"
  echo "  -t  MQTT Topic Name        (default: $TOPIC)"
  echo "  -1  Line 1 Max Value       (default: $VAL1_MAX)"
  echo "  -2  Line 2 Max Value       (default: $VAL2_MAX)"
  echo "  -H  Broker Host IP         (default: $BROKER)"
  echo "  -p  Broker Port            (default: $PORT)"
  echo "  -i  Publish Interval (sec) (default: $INTERVAL)"
  echo "  -h  Show this help message"
  exit 1
}

# Parse command-line flags
while getopts "t:1:2:h:p:i:?" opt; do
  case "$opt" in
    t) TOPIC="$OPTARG" ;;
    1) VAL1_MAX="$OPTARG" ;;
    2) VAL2_MAX="$OPTARG" ;;
    h) BROKER="$OPTARG" ;;
    p) PORT="$OPTARG" ;;
    i) INTERVAL="$OPTARG" ;;
    ?) usage ;;
  esac
done

# Initial starting points (centered in range)
val1=$(( VAL1_MAX / 2 ))
val2=$(( VAL2_MAX / 2 ))

echo "--------------------------------------------------------"
echo "Starting MQTT Publisher with Configuration:"
echo " Broker    : $BROKER:$PORT"
echo " Topic     : $TOPIC"
echo " Line 1 Max: $VAL1_MAX"
echo " Line 2 Max: $VAL2_MAX"
echo " Interval  : ${INTERVAL}s"
echo "--------------------------------------------------------"
echo "Press [CTRL+C] to stop."

while true; do
  # Generate smooth random steps
  step1=$(( (RANDOM % 11) - 5 ))
  step2=$(( (RANDOM % 7) - 3 ))

  # Apply steps
  val1=$(( val1 + step1 ))
  val2=$(( val2 + step2 ))

  # Clamp values dynamically
  (( val1 < 0 )) && val1=0
  (( val1 > VAL1_MAX )) && val1=$VAL1_MAX

  (( val2 < 0 )) && val2=0
  (( val2 > VAL2_MAX )) && val2=$VAL2_MAX

  # Format into 8-character uppercase Hex strings
  hex1=$(printf "%08X" "$val1")
  hex2=$(printf "%08X" "$val2")

  # Construct 20-character payload
  payload="0x${hex1}1x${hex2}"

  # Publish message
  mosquitto_pub -h "$BROKER" -p "$PORT" -t "$TOPIC" -m "$payload"

  echo "Published to '$TOPIC': $payload | Val1: $val1 | Val2: $val2"

  sleep "$INTERVAL"
done
