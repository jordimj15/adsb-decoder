# ✈️ ADS-B Decoder (Java 21)

A high-performance, single-thread ADS-B decoder implemented entirely in **Java 21**, designed to process raw Mode-S / ADS-B messages efficiently without external dependencies.

---

## 📌 Overview

This project decodes ADS-B messages typically collected from Mode-S / ADS-B receivers such as **Mode-S Beast**.

The decoder currently supports:

- **Airborne Position Messages**
- **Surface Position Messages**
- **Airborne Operational Status Messages**
- **Surface Operational Status Messages**
- **Airborne Velocity Messages** (Ground Speed / Air Speed)
- **Aircraft Identification Messages**

Aircraft identification data is preprocessed and automatically linked with all messages from the same aircraft, enriching the output messages.

Although the decoder is single-threaded, it is very optimized and can process large input files quickly, even on low-performance hardware.

---

## 🛠 Requirements

- **Java 21** or newer  
  (Compatible with Oracle JDK, OpenJDK, Temurin)
- **No external libraries needed**
- Works on Windows, macOS, and Linux

---

## 🚀 Installation

1. Install **Java 21** or newer.
2. Clone or download the project.
3. Compile and run:


## ▶️ Running the Main Script
A sample test file is included.
Running the main script typically produces output similar to:
```yaml
Airborne position count:     174693
Surface position count:      121
Airborne operational count:  34622
Surface operational count:   30
Ground speed count:          176293
Air speed count:             0
```
