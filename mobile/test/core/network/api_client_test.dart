import 'package:dio/dio.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/annotations.dart';
import 'package:mockito/mockito.dart';
import 'package:ragebait/core/models/ragebait_post.dart';
import 'package:ragebait/core/network/api_client.dart';
import 'dart:convert';

import 'api_client_test.mocks.dart';

@GenerateMocks([Dio])
void main() {
  late MockDio mockDio;
  late ApiClient apiClient;

  setUp(() {
    mockDio = MockDio();
    // Stub the options getter
    when(mockDio.options).thenReturn(BaseOptions());
    apiClient = ApiClient(mockDio, baseUrl: 'http://127.0.0.1:8080');
  });

  group('ApiClient', () {
    test('generateRagebait returns RagebaitPost on success', () async {
      final responsePayload = {
        'id': 1,
        'result': 'test result',
      };
      final response = Response(
        data: responsePayload,
        statusCode: 200,
        requestOptions: RequestOptions(path: '/api/v1/generate'),
      );
      when(mockDio.fetch(any)).thenAnswer((_) async => response);

      final result = await apiClient.generateRagebait({'topic': 'test'});
      expect(result, isA<RagebaitPost>());
      expect(result.id, 1);
      expect(result.result, 'test result');
    });

    test('generateRagebait throws DioError on failure', () async {
      final dioError = DioError(
        requestOptions: RequestOptions(path: '/api/v1/generate'),
        response: Response(
          data: 'error',
          statusCode: 500,
          requestOptions: RequestOptions(path: '/api/v1/generate'),
        ),
        type: DioErrorType.badResponse,
      );
      when(mockDio.fetch(any)).thenThrow(dioError);

      expect(
        () => apiClient.generateRagebait({'topic': 'test'}),
        throwsA(isA<DioError>()),
      );
    });
  });
} 