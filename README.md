# MQTT Data Hub
Extensible, real-time MQTT visualization studio built with thread-safe Java messaging architecture.

> **Project History:** Developed during my sophomore year (2021–2022) for the UIUC Advanced Power Lab. Published as-is to preserve the original real-time multi-threading architecture and functional proof-of-concept.

<img width="90%" alt="image" src="https://github.com/user-attachments/assets/56073fa9-1cae-4b8d-9070-58c2b5467748" />



A Java-based Graphical User Interface (GUI) application for publishing, subscribing, and visualizing real-time MQTT message streams.

---

## Prerequisites

* **Java Development Kit (JDK):** Version 11 or higher installed on your system.
* **Eclipse IDE:** Any modern version of Eclipse IDE for Java Developers.
* **MQTT Broker:** A running broker is required to publish and subscribe to topics.
  * **macOS:** Follow this step-by-step guide to [Install Mosquitto MQTT Broker on macOS](https://brittanyho.com/installing-a-mqtt-broker-on-macos/) for quick plug-and-play local setup (`127.0.0.1:1883`).
  * **Other OS / Remote:** You can use any standard MQTT broker (e.g., Mosquitto, HiveMQ). Ensure your broker's host IP and port match the configuration in `mqtt.properties` or your router setup.

---

## Getting Started (Eclipse)

Follow these steps to import and run the application in Eclipse IDE:

### 1. Import the Project
1. Open **Eclipse IDE**.
2. Go to **File** $\rightarrow$ **Import...**
3. Select **General** $\rightarrow$ **Existing Projects into Workspace** and click **Next**.
4. Click **Browse...** next to *Select root directory*, select the **`mqtt-gui`** folder inside this repository, and click **Finish**.

> **Note:** All required library dependencies (`jfreechart` and `paho-mqtt`) are pre-configured in the project's `lib/` folder.

### 2. Run the Application
1. In the **Package Explorer** panel, navigate to:
   `mqtt-gui` $\rightarrow$ `src` $\rightarrow$ `gui.model` $\rightarrow$ `MQTTFrame.java`
2. Right-click **`MQTTFrame.java`**.
3. Select **Run As** $\rightarrow$ **Java Application**.

#### Mode 1: High-Speed View
<img width="80%" alt="High-Speed View Mode" src="https://github.com/user-attachments/assets/6a037f4e-1bb3-453f-b284-d922806c2e64" />

#### Mode 2: Multi-Topic Grid
<img width="80%" alt="Multi-Topic Grid Mode" src="https://github.com/user-attachments/assets/7f7fd82a-c819-4351-82e6-ce5117bbe72a" />

### 3. Configure Broker

1. Use the default local broker setting (`127.0.0.1:1883`) or enter your target Broker IP address and port (see [Prerequisites](https://github.com/brittho/mqtt-data-hub/edit/main/README.md#prerequisites)).
2. Click `Connect`.
  * **Yellow Light:** the application is actively attempting to connect.
  * **Green Light:** a successful connection to the MQTT broker.
  * **Red Light + Error Popup:** the connection fails, an exception window will display detailing the issue.

Quick Demo!

---

## Helper Scripts (Optional)
If you don't have active live telemetry streams, the `scripts/` folder includes shell utilities for testing:
* **`pub_stream.sh`**: Generates and publishes custom simulated real-time telemetry data to a single MQTT topic at a configurable interval.
* **`launch_all.sh`**: Spawns and manages 6 concurrent `pub_stream.sh` background streams with a unified `Ctrl+C` shutdown trap.

#### Pre-Configured Test Streams (`launch_all.sh`)

The `launch_all.sh` script comes pre-tested with 6 parallel streams. You can modify the topic names (`-t`), value ranges (`-1`, `-2`), or target brokers (`-H`, `-p`) directly inside the script as needed:

* `sensor/node01/ambient` — (Node 1) ambient telemetry
* `sensor/node02/ambient` — (Node 2) ambient telemetry
* `sensor/node02/ambient/pct` — (Node 2, scaled percentage) ambient telemetry stream (`-1 100 -2 100`)
* `sensor/node03/grid_substation` — (Node 3) substation monitoring stream
* `sensor/node04/inverter` — (Node 4) primary inverter output stream
* `sensor/node04/inverter/pct` — (Node 4, scaled percentage) inverter output stream (`-1 100 -2 100`)

To make them executable on macOS/Linux:
```bash
chmod +x scripts/*.sh
./scripts/launch_all.sh
```
