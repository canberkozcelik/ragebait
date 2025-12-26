import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'auth_service.dart';
import '../config/environment.dart';

final apiServiceProvider = Provider<ApiService>((ref) {
  return ApiService(ref.read(authServiceProvider));
});

class ApiService {
  final AuthService _authService;
  late final Dio _dio;

  static const String _baseUrl = Environment.apiBaseUrl;

  ApiService(this._authService, {Dio? dio}) {
    _dio = dio ?? Dio(BaseOptions(
      baseUrl: _baseUrl,
      connectTimeout: const Duration(seconds: 30),
      receiveTimeout: const Duration(seconds: 60),
    ));

    // Only add interceptors if using the internal Dio (production)
    // Or add them always? For unit tests involving MockDio, we usually stub methods directly.
    // If we pass a MockDio, we probably don't want real interceptors attaching to it.
    if (dio == null) {
      _setupInterceptors();
    }
  }

  void _setupInterceptors() {
    // add auth interceptor
    _dio.interceptors.add(InterceptorsWrapper(
      onRequest: (options, handler) async {
        final token = await _authService.getIdToken();
        if (token != null) {
          options.headers['Authorization'] = 'Bearer $token';
          // print("🔑 Auth Token injected: ${token.substring(0, 10)}...");
        } else {
          // print("⚠️ NO Auth Token available!");
        }
        return handler.next(options);
      },
      onError: (DioException e, handler) {
        return handler.next(e); // Continue to the catch block below
      },
    ));

    // Add Logger
    _dio.interceptors.add(LogInterceptor(
      request: true,
      requestHeader: true,
      requestBody: true,
      responseHeader: true,
      responseBody: true,
      error: true,
    ));
  }

  Future<String> generateRagebait(String topic) async {
    try {
      // print("🚀 Sending request for: $topic to $_baseUrl/generate");
      final response = await _dio.post('/generate', data: {
        'topic': topic,
      });
      // print("✅ Response received: ${response.data}");
      
      return response.data['result'] ?? 'Error generating text';
    } on DioException catch (e) {
      // print("❌ DioError: ${e.message}");
      // print("❌ Response: ${e.response?.data}");
      // print("❌ Headers: ${e.requestOptions.headers}");
      
      if (e.response?.statusCode == 403) {
        throw QuotaExceededException();
      }
      if (e.response?.statusCode == 429) {
        throw Exception("Too many requests. Please try again later.");
      }
      throw Exception(e.response?.data['message'] ?? 'Failed to connect: ${e.message}');
    } catch (e) {
      // print("❌ Unknown Error: $e");
      // print(stack);
      rethrow;
    }
  }
  Future<void> syncPremiumStatus() async {
    try {
      await _dio.post('/user/sync');
    } catch (e) {
      // print("❌ [ApiService] Failed to sync premium status: $e");
      // We don't rethrow here because the purchase itself was successful.
      // Failing to sync shouldn't block the user from using the app immediately locally,
      // but might cause quota issues later. 
      // ideally we might want to retry implicitly.
    }
  }
}

class QuotaExceededException implements Exception {}
