# AI TourGuide 🧭🎧

AI TourGuide is a **hands-free, AI-powered tour guide application** designed for wearable headsets and mobile devices.  
It enables visitors to explore museums, heritage sites, and cultural landmarks using **camera-based text detection, AI-generated explanations, and multilingual text-to-speech**, without touching the screen.

---

## 🚀 Key Features

- 📷 **Camera-based text detection**
  - Uses ML Kit OCR to detect text from boards, plaques, and signs
- 🧠 **AI-generated historical explanations**
  - Powered by Google Gemini for concise, visitor-friendly responses
- 🌍 **Multilingual support**
  - English  
  - German  
  - Sinhala  
  - Chinese (Mandarin)  
- 🔊 **Backend-powered Text-to-Speech (TTS)**
  - Natural audio narration in the selected language
- 🎧 **Hands-free operation**
  - Controlled using physical headset or device buttons via Accessibility Service
- 📳 **Haptic feedback**
  - Single vibration for success
  - Double vibration for errors or failures
- 🔋 **Battery-optimized camera usage**
  - Optimized CameraX preview handling to reduce power drain
- 🧩 **Scalable architecture**
  - Ready for Bluetooth-based companion apps and wearable settings control

---

## 🧠 How It Works

1. User presses the **play button** on the headset
2. Camera opens and LED indicator turns ON
3. ML Kit detects and extracts visible text
4. Key location names are identified
5. The keyword is sent to **Gemini AI**
6. Gemini generates a short explanation in the selected language
7. The response is displayed and spoken via **TTS**
8. Camera and LED turn OFF automatically

---

## 🛠 Tech Stack

- **Language:** Kotlin  
- **UI:** Jetpack Compose  
- **Camera:** CameraX  
- **OCR:** ML Kit Text Recognition  
- **AI:** Google Gemini  
- **TTS:** Backend Text-to-Speech API  
- **Accessibility:** Android Accessibility Service  
- **Architecture:** State-driven, listener-based, wearable-friendly  

---

## 📱 Use Cases

- Museums and exhibitions
- UNESCO heritage sites
- Cultural and religious landmarks
- Smart wearable and headset-based guides
- Accessibility-focused guided experiences

---

## 🌐 Multilingual Support

The app dynamically switches languages at runtime.  
Language selection can be done via:
- On-screen selector
- Headset button controls
- (Planned) Bluetooth companion mobile app

---

## 📦 Project Status

🚧 **Active Development**

Planned improvements:
- Bluetooth companion mobile app for settings
- Offline caching of AI responses
- Auto language detection
- Text-to-Speech speed and voice controls
- Wearable OTA updates

---

## 🤝 Contributing

Contributions are welcome!  
Please:
1. Fork the repository
2. Create a feature branch
3. Submit a pull request with a clear description

---

## 👥 Community

This project is part of the **AiSee Community** initiative, focusing on  
**AI-powered accessibility, wearables, and hands-free experiences**.

---
