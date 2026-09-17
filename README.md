# MQTT Data Hub
Extensible, real-time MQTT visualization studio built with thread-safe Java messaging architecture.

<img width="1796" height="1132" alt="image" src="https://github.com/user-attachments/assets/56073fa9-1cae-4b8d-9070-58c2b5467748" />



A Java-based Graphical User Interface (GUI) application for publishing, subscribing, and visualizing real-time MQTT message streams.

---

## Prerequisites

* **Java Development Kit (JDK):** Version 11 or higher installed on your system.
* **Eclipse IDE:** Any modern version of Eclipse IDE for Java Developers.

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

---

## Helper Scripts (Optional)

The `scripts/` folder includes shell utilities for testing:
* **`pub_stream.sh`**: Simulates publishing MQTT stream data to test the GUI.
* **`launch_all.sh`**: Launches automated setup/test processes.

To make them executable on macOS/Linux:
```bash
chmod +x scripts/*.sh
./scripts/pub_stream.sh
```
