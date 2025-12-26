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

    if (dio == null) {
      _setupInterceptors();
    }
  }

  void _setupInterceptors() {
    _dio.interceptors.add(InterceptorsWrapper(
      onRequest: (options, handler) async {
        final token = await _authService.getIdToken();
        if (token != null) {
          options.headers['Authorization'] = 'Bearer $token';
        } else {
        }
        return handler.next(options);
      },
      onError: (DioException e, handler) {
        return handler.next(e); // Continue to the catch block below
      },
    ));

    
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
      final response = await _dio.post('/generate', data: {
        'topic': topic,
      });
      
      return response.data['result'] ?? 'Error generating text';
    } on DioException catch (e) {
      
      if (e.response?.statusCode == 403) {
        throw QuotaExceededException();
      }
      if (e.response?.statusCode == 429) {
        throw Exception("Too many requests. Please try again later.");
      }
      throw Exception(e.response?.data['message'] ?? 'Failed to connect: ${e.message}');
    } catch (e) {
      rethrow;
    }
  }

  Future<void> syncPremiumStatus() async {
    try {
      await _dio.post('/user/sync');
    } catch (e) {
      // Intentionally silent
    }
  }
}

class QuotaExceededException implements Exception {}
