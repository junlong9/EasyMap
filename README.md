# EasyMap - AI-Powered Location Search for Tourists in China

EasyMap is an Android application designed to help foreign tourists in China find locations easily by using natural language search with AI translation and AMap integration.

## Features

- **Natural Language Search**: Search for places using English descriptions (e.g., "dumplings", "coffee shops")
- **AI Translation**: Uses DeepSeek AI to translate and understand search queries
- **Real-time Location**: Uses AMap location services to find nearby places
- **Interactive Map**: Displays search results on an interactive AMap
- **Results List**: Shows detailed information about found locations
- **Monetization Ready**: Framework for promoting local businesses

## Prerequisites

1. **AMap API Key**: You need to obtain an API key from [AMap Developer Console](https://lbs.amap.com/)
2. **Python Backend**: The AI chatbot backend should be running (as described in your development log)
3. **Android Studio**: Latest version with Android SDK 35
4. **Device/Emulator**: Android device or emulator with Google Play Services

## Setup Instructions

### 1. Get AMap API Key

1. Go to [AMap Developer Console](https://lbs.amap.com/)
2. Create an account and register your application
3. Get your API key for Android platform
4. Enable the following services:
   - Maps SDK
   - Location SDK
   - Search SDK

### 2. Configure API Key

1. Open `app/src/main/res/values/amap_config.xml`
2. Replace `YOUR_AMAP_API_KEY_HERE` with your actual AMap API key:

```xml
<string name="amap_api_key">your_actual_api_key_here</string>
```

### 3. Configure Backend URL

1. Open `app/src/main/java/com/example/easymap/MainActivity.java`
2. Update the `BACKEND_URL` constant:
   - For Android Emulator: `http://10.0.2.2:8000/chat`
   - For physical device: `http://your_computer_ip:8000/chat`

### 4. Build and Run

1. Open the project in Android Studio
2. Sync Gradle files
3. Build the project
4. Run on device or emulator

## Project Structure

```
app/src/main/java/com/example/easymap/
├── MainActivity.java          # Main activity with map and search functionality
├── LocationHelper.java        # Handles AMap location services
├── PlaceResult.java          # Data model for search results
└── ResultsAdapter.java       # RecyclerView adapter for results list

app/src/main/res/
├── layout/
│   └── activity_main.xml     # Main UI layout
├── values/
│   ├── strings.xml           # String resources
│   └── amap_config.xml       # AMap API key configuration
└── AndroidManifest.xml       # App permissions and configuration
```

## How It Works

1. **User Input**: User enters a natural language query (e.g., "Find dumplings near me")
2. **AI Processing**: Query is sent to your Python backend with DeepSeek AI
3. **Translation**: AI translates and extracts relevant Chinese keywords
4. **Location Search**: App searches AMap using the translated keywords
5. **Results Display**: Found locations are displayed on the map and in a list
6. **Interaction**: Users can tap on results to focus the map on that location

## Permissions

The app requires the following permissions:
- `ACCESS_FINE_LOCATION`: For precise location services
- `ACCESS_COARSE_LOCATION`: For approximate location services
- `INTERNET`: For API calls to backend and AMap services
- `ACCESS_NETWORK_STATE`: For network connectivity checks

## Monetization Features

The app is designed to support business promotion:
- `PlaceResult` class has an `isPromoted` field
- Promoted businesses can be prioritized in search results
- Framework ready for business owner partnerships

## Troubleshooting

### Common Issues

1. **Map not loading**: Check your AMap API key configuration
2. **Location not working**: Ensure location permissions are granted
3. **Backend connection failed**: Verify the backend URL and that your Python server is running
4. **No search results**: Check if you're in China or using a VPN that works with AMap

### Debug Tips

- Check Logcat for detailed error messages
- Verify API key is correctly set in `amap_config.xml`
- Test backend connectivity separately
- Ensure device has internet connection

## Next Steps

1. **Complete RecyclerView**: Add custom layout for search results
2. **Business Promotion**: Implement promoted business logic
3. **Offline Support**: Add offline map capabilities
4. **User Preferences**: Add search history and favorites
5. **Multi-language**: Support for other languages beyond English

## Contributing

This project is designed for tourists in China. Contributions are welcome, especially:
- UI/UX improvements
- Additional language support
- Better AI response parsing
- Performance optimizations

## License

This project is for educational and commercial use. Please respect AMap's terms of service and API usage limits. 