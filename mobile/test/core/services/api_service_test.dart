import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';
import 'package:mockito/annotations.dart';
import 'package:dio/dio.dart';
import 'package:ragebait/core/services/api_service.dart';
import 'package:ragebait/core/services/auth_service.dart';

// Generate mocks
@GenerateMocks([AuthService, Dio])
import 'api_service_test.mocks.dart';

void main() {
  late ApiService apiService;
  late MockAuthService mockAuthService;
  late MockDio mockDio;

  setUp(() {
    mockAuthService = MockAuthService();
    mockDio = MockDio();
    apiService = ApiService(mockAuthService, dio: mockDio);
  });

  group('generateRagebait', () {
    const topic = 'slow walkers';
    const path = '/generate';

    test('returns result when API call is successful', () async {
      // Arrange
      final responseData = {'result': 'Mocked ragebait text'};
      when(mockDio.post(
        path,
        data: anyNamed('data'),
      )).thenAnswer((_) async => Response(
            data: responseData,
            statusCode: 200,
            requestOptions: RequestOptions(path: path),
          ));

      // Act
      final result = await apiService.generateRagebait(topic);

      // Assert
      expect(result, 'Mocked ragebait text');
      verify(mockDio.post(path, data: {'topic': topic})).called(1);
    });

    test('throws QuotaExceededException when 403 is returned', () async {
      // Arrange
      when(mockDio.post(
        path,
        data: anyNamed('data'),
      )).thenThrow(DioException(
        requestOptions: RequestOptions(path: path),
        response: Response(
          statusCode: 403,
          requestOptions: RequestOptions(path: path),
        ),
      ));

      // Act & Assert
      expect(
        () => apiService.generateRagebait(topic),
        throwsA(isA<QuotaExceededException>()),
      );
    });

    test('throws Exception on generic error', () async {
      // Arrange
      when(mockDio.post(
        path,
        data: anyNamed('data'),
      )).thenThrow(DioException(
        requestOptions: RequestOptions(path: path),
        response: Response(
          statusCode: 500,
          data: {'message': 'Internal Server Error'},
          requestOptions: RequestOptions(path: path),
        ),
      ));

      // Act & Assert
      expect(
        () => apiService.generateRagebait(topic),
        throwsA(isA<Exception>()),
      );
    });
  });

  group('syncPremiumStatus', () {
    const path = '/user/sync';

    test('calls correct endpoint', () async {
      // Arrange
      when(mockDio.post(path)).thenAnswer((_) async => Response(
            statusCode: 200,
            requestOptions: RequestOptions(path: path),
          ));

      // Act
      await apiService.syncPremiumStatus();

      // Assert
      verify(mockDio.post(path)).called(1);
    });

    test('suppresses error when API call fails', () async {
      // Arrange
      when(mockDio.post(path)).thenThrow(DioException(
        requestOptions: RequestOptions(path: path),
        error: 'Network Error',
      ));

      // Act
      // Should not throw
      await apiService.syncPremiumStatus();

      // Assert
      verify(mockDio.post(path)).called(1);
    });
  });
}
