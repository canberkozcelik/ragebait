import 'package:dio/dio.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:ragebait/core/network/api_client.dart';

void main() {
  late NetworkModule networkModule;

  setUp(() {
    networkModule = _TestNetworkModule();
  });

  test('baseUrl returns the correct value', () {
    expect(networkModule.baseUrl, 'http://127.0.0.1:8080');
  });

  test('dio() returns Dio with correct timeouts and headers', () {
    final dio = networkModule.dio();
    expect(dio.options.connectTimeout, const Duration(seconds: 5));
    expect(dio.options.receiveTimeout, const Duration(seconds: 3));
    expect(dio.options.headers['Content-Type'], 'application/json');
    expect(dio.options.headers['Accept'], 'application/json');
    expect(dio.interceptors, isNotEmpty);
  });

  test('apiClient() returns an ApiClient instance', () {
    final dio = networkModule.dio();
    final apiClient = networkModule.apiClient(dio, networkModule.baseUrl);
    expect(apiClient, isA<ApiClient>());
  });
}

// Helper to instantiate the abstract NetworkModule for testing
class _TestNetworkModule extends NetworkModule {} 