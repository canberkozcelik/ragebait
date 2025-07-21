import 'package:dio/dio.dart';
import 'package:injectable/injectable.dart';
import 'package:logger/logger.dart';
import 'package:retrofit/retrofit.dart';
import '../models/ragebait_post.dart';

part 'api_client.g.dart';

@RestApi()
abstract class ApiClient {
  @factoryMethod
  factory ApiClient(Dio dio, {@Named('baseUrl') String baseUrl}) = _ApiClient;

  @POST('/api/v1/generate')
  Future<RagebaitPost> generateRagebait(@Body() Map<String, dynamic> body);
}

@module
abstract class NetworkModule {
  final _logger = Logger();

  @Named('baseUrl')
  String get baseUrl => 'http://127.0.0.1:8080';

  @singleton
  Dio dio() {
    final dio = Dio()
      ..options.connectTimeout = const Duration(seconds: 5)
      ..options.receiveTimeout = const Duration(seconds: 3)
      ..options.headers = {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      }
      ..interceptors.add(
        InterceptorsWrapper(
          onRequest: (options, handler) {
            _logger.i('Request: ${options.method} ${options.uri}');
            _logger.i('Request body: ${options.data}');
            return handler.next(options);
          },
          onResponse: (response, handler) {
            _logger.i('Response: ${response.statusCode}');
            _logger.i('Response body: ${response.data}');
            return handler.next(response);
          },
          onError: (error, handler) {
            _logger.e('Error: ${error.message}', error: error);
            return handler.next(error);
          },
        ),
      );

    return dio;
  }

  @lazySingleton
  ApiClient apiClient(Dio dio, @Named('baseUrl') String baseUrl) => ApiClient(dio, baseUrl: baseUrl);
} 