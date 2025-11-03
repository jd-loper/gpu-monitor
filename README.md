# GPU Resource Monitor
A lightweight Java application that displays real-time GPU usage statistics with a clean GUI.

## Features
- Real-time GPU usage monitoring
- Multi-GPU support
- Key metrics:
  - Memory usage
  - Fan speed
  - Temperature
  - Utilization

## Requirements
- Java JDK
- NVIDIA GPU
  - NVIDIA drivers with nvidia-smi utility

## How It Works
The application uses a Java SwingWorker to poll the GPU usage statistics every second using the nvidia-smi utility.
The SwingWorker runs in a background thread, executing the nvidia-smi command to fetch GPU information and updating the GUI with the latest data.
It parses the CSV output, formats it into a string, and updates the GUI text field on the main Swing Event Dispatch Thread.