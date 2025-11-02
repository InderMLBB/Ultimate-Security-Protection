# 🛡️ PerisaiChat - Ultimate Anti-Virtex & Anti-Ban Protection

**Version**: 1.0.0  
**Platform**: Android (API 24-34)  
**Language**: Kotlin  
**Database**: Room SQLite  

## 📱 Download APK

### Metode 1: Download dari GitHub Releases (Recommended)

1. Push code ini ke GitHub repository Anda
2. GitHub Actions akan otomatis build APK
3. Download APK dari **Releases** section di GitHub
4. Install APK di Android device

**Link Releases**: `https://github.com/USERNAME/REPO/releases`

### Metode 2: Build Manual di Android Studio

Jika Anda ingin build sendiri:

1. Download semua file project ini
2. Extract dan buka di Android Studio
3. Jalankan: `./gradlew assembleDebug`
4. APK ada di: `app/build/outputs/apk/debug/app-debug.apk`

## 🔧 Cara Install APK di Android

1. **Download APK** dari GitHub Releases atau build sendiri
2. **Transfer APK** ke Android device (via USB/email/drive)
3. **Buka Settings** → Security → Enable "Install from Unknown Sources"
4. **Tap file APK** dan klik Install
5. **Buka PerisaiChat** dan berikan izin Notification Access

## 🎯 Fitur Utama

**Perlindungan Real-Time:**
- 🛡️ Deteksi virtex/kenon otomatis
- 🎣 Blokir pesan phishing
- 📧 Filter spam Unicode
- 🚫 Cegah flood attacks

**Anti-Ban Support:**
- 📊 Koleksi bukti ancaman masuk
- 📝 Template surat banding otomatis
- 📦 Export evidence (CSV + ZIP)
- ⚠️ Deteksi WhatsApp modded

**Teknologi:**
- AI Heuristic Analyzer (0-100 scoring)
- Room Database untuk logging
- Material 3 Design
- NotificationListenerService API

## 🏗️ Struktur Project

```
PerisaiChat/
├── app/
│   ├── src/main/
│   │   ├── java/com/perisaichat/
│   │   │   ├── MainActivity.kt                    # Main UI dengan shield toggle
│   │   │   ├── ShieldNotificationService.kt       # Real-time notification monitor
│   │   │   ├── VirusDetector.kt                   # AI heuristic analyzer
│   │   │   ├── data/
│   │   │   │   ├── ThreatLog.kt                   # Room entity
│   │   │   │   ├── ThreatLogDao.kt                # Database DAO
│   │   │   │   └── AppDatabase.kt                 # Room database
│   │   │   ├── ui/
│   │   │   │   ├── LogActivity.kt                 # View threat logs
│   │   │   │   ├── ExportActivity.kt              # Export evidence
│   │   │   │   └── SettingsActivity.kt            # Settings & anti-ban tips
│   │   │   └── util/
│   │   │       └── FileUtils.kt                   # CSV, ZIP, SHA256
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml              # Main UI layout
│   │   │   │   ├── activity_log.xml               # Logs RecyclerView
│   │   │   │   ├── activity_export.xml            # Export UI
│   │   │   │   ├── activity_settings.xml          # Settings UI
│   │   │   │   ├── item_log.xml                   # Log item layout
│   │   │   │   └── dialog_log_detail.xml          # Detail dialog
│   │   │   ├── values/
│   │   │   │   ├── strings.xml                    # String resources
│   │   │   │   └── themes.xml                     # Material 3 theme
│   │   │   └── xml/
│   │   │       └── file_paths.xml                 # FileProvider paths
│   │   └── AndroidManifest.xml
│   ├── src/test/
│   │   └── java/com/perisaichat/
│   │       └── VirusDetectorTest.kt               # Unit tests
│   └── build.gradle
├── testdata/
│   ├── virtex.txt                                 # Sample virtex
│   ├── phishing.txt                               # Sample phishing
│   └── normal.txt                                 # Normal messages
├── build.gradle
├── settings.gradle
└── README.md
```

## 🚀 Build & Installation

### Prerequisites

1. **Android Studio** (Arctic Fox or later)
2. **JDK 17** or higher
3. **Android SDK** (API Level 24-34)
4. **Gradle 8.x**

### Build Steps

#### 1. Clone Project

```bash
# If in Replit, files are already here
# If cloning from Git:
git clone <repository-url>
cd PerisaiChat
```

#### 2. Open in Android Studio

```bash
# Open Android Studio
File → Open → Select PerisaiChat folder
```

#### 3. Sync Gradle

Android Studio will automatically sync Gradle dependencies.
Wait for completion (may take 2-5 minutes first time).

#### 4. Build APK

**Via Android Studio:**
```
Build → Build Bundle(s) / APK(s) → Build APK(s)
```

**Via Command Line:**
```bash
./gradlew assembleDebug
```

#### 5. Locate APK

After successful build:
```
app/build/outputs/apk/debug/app-debug.apk
```

#### 6. Install to Device

**Via ADB:**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

**Via Manual Transfer:**
1. Copy APK to device
2. Open file and tap Install
3. Enable "Install from Unknown Sources" if prompted

## 📋 Permissions Required

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
```

**Special Permission:**
- Notification Listener Access (requested at runtime)

## 🧪 Testing

### Unit Tests
```bash
./gradlew test
```

### Test Coverage
- Virtex detection patterns
- Phishing URL detection
- Normal message validation
- False positive handling

### Test Data Files
- `testdata/virtex.txt` - Virtex samples
- `testdata/phishing.txt` - Phishing samples  
- `testdata/normal.txt` - Safe messages

## 🔒 Privacy & Security

**Data Collection:**
- All data stored locally on device
- No cloud sync or external transmission
- SHA256 hashing for evidence integrity

**Permissions Usage:**
- Notification access: Monitor incoming messages
- File access: Export evidence and logs

## 📖 User Guide

### First Time Setup

1. **Install & Open** PerisaiChat
2. **Grant Permission** when prompted for Notification Access
3. **Enable Shield** toggle on main screen
4. **You're Protected!** App will now monitor notifications

### Viewing Threat Logs

1. Tap **"View Logs"** button
2. See all blocked threats with details
3. Tap any log for full information
4. Mark false positives if needed

### Exporting Evidence

1. Go to **Export** section
2. Choose export format:
   - **CSV** - Spreadsheet format
   - **ZIP** - Complete evidence package
   - **Template** - Appeal letter template
3. Share exported file via email/messaging

### Anti-Ban Tips

1. Open **Settings**
2. Read **Anti-Ban Advisor**
3. Check if using modded WhatsApp
4. Follow prevention guidelines

## 🆘 Troubleshooting

**Shield Not Working?**
- Check notification permission is granted
- Ensure shield toggle is ON
- Restart app if needed

**False Positives?**
- Mark message as false positive in logs
- System will learn and improve

**Export Not Working?**
- Check storage permissions
- Ensure there are logs to export

## ⚖️ Legal & Ethics

**Defensive Use Only:**

✅ **Allowed:**
- Personal device protection
- Evidence collection for appeals
- Educational purposes

❌ **Prohibited:**
- Privacy violations
- Revenge attacks
- Data exploitation

**Disclaimer:** This app provides defensive protection only. Users are responsible for their own actions and data.

## 📞 Support

For issues or questions:
1. Check this README
2. Review `replit.md` documentation
3. Check GitHub Issues

## 📄 License

[Add your license here]

## 👨‍💻 Development

Built with:
- Kotlin 1.9.20
- Android Gradle Plugin 8.2.0
- Room Database 2.6.1
- Material 3 Components
- Coroutines for async operations

---

**Made with 🛡️ by PerisaiChat Team**