# PerisaiChat Project Documentation

## Overview

**PerisaiChat** is a comprehensive anti-virtex protection system with two components:

1. **Android Native App** - Kotlin-based Android app with real-time notification monitoring
2. **Web Demo** - Browser-based interactive demo for testing and visualization

**Version**: 1.0.0  
**Platform**: Android (API 24-34) + Web (Node.js)  
**Language**: Kotlin (Android) + JavaScript (Web)  
**Database**: Room SQLite (Android) + SQLite (Web)  

## Purpose

This defensive Android application protects users from:
- Virus text (virtex/kenon) attacks
- Phishing messages with suspicious URLs
- Spam with excessive Unicode characters
- Mass messaging and flood attacks

Additionally, it helps users who face WhatsApp account bans by:
- Collecting evidence of harmful incoming messages
- Auto-generating appeal letter templates
- Exporting complete evidence packages (ZIP)
- Providing anti-ban prevention tips

## Project Status

✅ **Completed Features:**
1. Real-time notification monitoring service (ShieldNotificationService.kt)
2. AI heuristic threat analyzer with 0-100 scoring (VirusDetector.kt)
3. Room database for threat logging (ThreatLog, ThreatLogDao, AppDatabase)
4. Evidence export system (CSV, ZIP, appeal templates)
5. Anti-ban advisor with modded app detection
6. False positive handling and whitelist system
7. Material 3 UI with multiple activities
8. Complete unit test suite (VirusDetectorTest.kt)
9. Test data files (virtex, phishing, normal samples)
10. Comprehensive README with build and appeal instructions

## Architecture

### Core Components

**Data Layer:**
- `ThreatLog.kt` - Room entity for threat records
- `ThreatLogDao.kt` - Database access object
- `AppDatabase.kt` - Room database singleton

**Logic Layer:**
- `VirusDetector.kt` - AI heuristic analyzer (0-100 threat scoring)
- `ShieldNotificationService.kt` - NotificationListenerService implementation

**UI Layer:**
- `MainActivity.kt` - Main dashboard with shield toggle
- `LogActivity.kt` - Threat logs viewer with RecyclerView
- `ExportActivity.kt` - Evidence export functionality
- `SettingsActivity.kt` - Settings and anti-ban advisor

**Utilities:**
- `FileUtils.kt` - CSV export, ZIP creation, SHA256 hashing

### Detection Algorithms

**Virtex Detection (Score: 0-100):**
- Length check: > 300 chars = +25 points
- Non-ASCII ratio: > 40% = +30 points
- Invisible characters: > 5 = +35 points
- Emoji spam: > 20 emojis = +20 points
- Character repetition: pattern match = +30 points
- Low diversity: < 10% unique = +25 points

**Phishing Detection:**
- Suspicious URLs: bit.ly, tinyurl, freegift, etc. = +40 points
- Phishing keywords + URL combo = +25 points
- Multiple phishing keywords = +15 points

**Threat Threshold:**
- Score >= 50 → isThreat = true → Block notification

## Build Requirements

⚠️ **Important**: This is an Android project and requires specific tools:

### Required Tools
1. Android Studio (Arctic Fox or later)
2. JDK 17 or higher
3. Android SDK (API Level 24-34)
4. Gradle 8.x

### Build Commands

**Debug APK:**
```bash
./gradlew assembleDebug
```

**Release APK:**
```bash
./gradlew assembleRelease
```

**Run Tests:**
```bash
./gradlew test
```

**Output Location:**
```
app/build/outputs/apk/debug/app-debug.apk
```

## Recent Changes

- **2025-11-02**: Web Demo Added
  - Full-featured web application with Node.js/Express backend
  - Interactive threat detection testing
  - Real-time dashboard with statistics
  - Database viewer with search/filter capabilities
  - Export evidence functionality (CSV download)
  - Material Design UI with animations
  - JavaScript port of VirusDetector algorithm

- **2025-01-02**: Initial project creation
  - Complete Android project structure
  - All core features implemented
  - Unit tests created
  - Documentation completed

## User Preferences

This project follows defensive-only principles:
- No offensive features (auto-reply, spam-back, etc.)
- 100% local data storage
- No external server uploads
- Open for security review
- Privacy-focused design

## Project Architecture Decisions

### Why NotificationListenerService?
- Allows real-time monitoring of incoming notifications
- Can intercept and cancel harmful notifications before user sees them
- Android's official API for notification access

### Why Room Database?
- Type-safe SQL queries
- Compile-time verification
- LiveData integration for reactive UI
- Supports complex queries and relations

### Why Heuristic Analysis?
- No internet required (fully offline)
- Fast analysis (< 100ms per message)
- Adaptable to new patterns
- Privacy-preserving (no data sent externally)

### Why Material 3?
- Modern Android design language
- Consistent user experience
- Accessibility support built-in
- Dark/light theme support

## Legal & Ethics

**This application is DEFENSIVE ONLY.**

✅ **Allowed Uses:**
- Personal device protection
- Evidence collection for appeals
- Educational purposes

❌ **Prohibited Uses:**
- Hacking or privacy violations
- Revenge or counter-attacks
- Data exploitation
- Exposing others

**Disclaimer**: Application does not guarantee 100% security. Users are responsible for their own data and actions.

## Build Notes for Replit Environment

⚠️ **Limitation**: Replit does not have Android SDK installed by default.

To build this project, you need to:
1. Download the project files
2. Open in Android Studio on a local machine
3. Sync Gradle dependencies
4. Build APK using `./gradlew assembleDebug`

**Alternative**: Use GitHub Actions or CI/CD pipeline with Android build environment.

## Testing

### Unit Tests
- Located: `app/src/test/java/com/perisaichat/VirusDetectorTest.kt`
- Coverage: Virtex, phishing, normal message detection
- Run with: `./gradlew test`

### Test Data
- `testdata/virtex.txt` - Sample virtex patterns
- `testdata/phishing.txt` - Sample phishing messages
- `testdata/normal.txt` - Normal safe messages

### Manual Testing
1. Build and install APK on Android device
2. Grant notification access permission
3. Enable shield toggle
4. Send test messages via WhatsApp
5. Verify threats are blocked and logged

## Support & Maintenance

### Common Issues

**Q: Shield not working?**
A: Check if notification access is granted in Settings

**Q: False positives?**
A: Mark log as false positive to improve learning

**Q: Export fails?**
A: Check storage permissions

**Q: WhatsApp banned?**
A: Use Export Evidence → ZIP → Submit to WhatsApp Support

### Future Enhancements (Not in MVP)

- Screenshot capture using MediaProjection API
- VirusTotal API integration
- Advanced machine learning patterns
- Dark/light theme toggle
- Multi-language support

## File Structure Summary

```
Total Files: 30+
- Kotlin Source: 10 files
- XML Layouts: 6 files
- Gradle Config: 4 files
- Test Files: 4 files
- Documentation: 3 files
```

## Dependencies

Key libraries:
- AndroidX Core KTX
- Material Components
- Room Database
- Coroutines
- RecyclerView
- CardView
- JUnit (testing)

All dependencies managed via Gradle.

---

## Web Demo Component

### Overview

The Web Demo provides a browser-based interface to test and visualize the PerisaiChat threat detection system without requiring an Android device.

**Running URL**: http://localhost:5000  
**Tech Stack**: Node.js, Express, SQLite, Vanilla JavaScript  
**Port**: 5000

### Features

1. **Dashboard** (`/`)
   - Real-time threat statistics
   - Shield activation toggle
   - Recent threats preview
   - Animated counters and status indicators

2. **Database Viewer** (`/database.html`)
   - Complete threat logs table
   - Search by message content, app source, or sender
   - Filter by threat type (virtex, phishing, spam, false positive)
   - Detailed modal view with full metadata
   - Mark threats as false positives
   - Delete individual or all logs

3. **Test Detection** (`/test.html`)
   - Interactive message analyzer
   - Real-time threat scoring (0-100)
   - Sample message templates
   - Visual score indicator with color coding
   - Detailed analysis breakdown
   - Save detected threats to database

4. **Export Evidence** (`/export.html`)
   - CSV export for all logged threats
   - Export statistics summary
   - WhatsApp appeal letter template
   - Usage instructions for ban appeals

### Web API Endpoints

**Statistics:**
- `GET /api/stats` - Get threat count summary

**Threat Logs:**
- `GET /api/logs` - Get all threat logs
- `POST /api/logs` - Create new threat log
- `PUT /api/logs/:id` - Update log (mark false positive)
- `DELETE /api/logs/:id` - Delete specific log
- `DELETE /api/logs` - Delete all logs

**Settings:**
- `GET /api/settings/:key` - Get setting value
- `PUT /api/settings/:key` - Update setting value

**Export:**
- `GET /api/export/csv` - Download CSV file

### File Structure (Web)

```
server.js                  # Express server + API endpoints
perisaichat.db            # SQLite database
public/
  index.html              # Dashboard page
  database.html           # Database viewer page
  test.html               # Test detection page
  export.html             # Export evidence page
  css/
    style.css             # Material Design styling
  js/
    detector.js           # JavaScript port of VirusDetector
    app.js                # Dashboard logic
    database.js           # Database viewer logic
    test.js               # Test detection logic
    export.js             # Export page logic
```

### Running the Web Demo

**Start Server:**
```bash
npm start
```

**Access:**
- Dashboard: http://localhost:5000
- Database: http://localhost:5000/database.html
- Test: http://localhost:5000/test.html
- Export: http://localhost:5000/export.html

### Web Detection Algorithm

The JavaScript implementation mirrors the Android Kotlin version:

**Same Features:**
- 0-100 threat scoring system
- Virtex detection (length, non-ASCII, emoji spam, repetition)
- Phishing detection (suspicious URLs, keywords)
- Spam detection (Unicode patterns, mass messaging)
- SHA256 hashing for message fingerprinting

**Differences:**
- Uses Web Crypto API instead of Java crypto
- Browser-based instead of NotificationListenerService
- Manual testing instead of real-time monitoring

### Database Schema (Web)

**threat_logs table:**
```sql
- id: INTEGER PRIMARY KEY
- timestamp: TEXT (ISO 8601)
- appSource: TEXT
- senderId: TEXT
- messageSnippet: TEXT
- hash: TEXT (SHA-256)
- reason: TEXT (virtex|phishing|spam)
- score: INTEGER (0-100)
- isFalsePositive: INTEGER (0|1)
```

**settings table:**
```sql
- key: TEXT PRIMARY KEY
- value: TEXT
```

### Development Notes

**Dependencies:**
- express: Web framework
- better-sqlite3: SQLite database driver
- Built-in Node.js modules (crypto, fs, path)

**No Build Step Required:**
- Vanilla JavaScript (no bundler needed)
- Direct HTML/CSS/JS serving
- Hot reload via nodemon in development
