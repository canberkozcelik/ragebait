class Environment {
  static const String revenueCatSdkKey = String.fromEnvironment(
    'REVENUECAT_SDK_KEY',
    defaultValue: 'test_hKcJaGawHVukbmanfqISHyyIthU', // Default for dev convenience if needed, or remove for strictness
  );

  static const String apiBaseUrl = String.fromEnvironment(
    'API_BASE_URL',
    defaultValue: 'https://ragebait-backend-1077799612961.europe-west3.run.app/api/v1',
  );
}
