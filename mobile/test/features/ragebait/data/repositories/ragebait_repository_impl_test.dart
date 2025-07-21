import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';
import 'package:mockito/annotations.dart';
import 'package:ragebait/core/network/api_client.dart';
import 'package:ragebait/core/models/ragebait_post.dart';
import 'package:ragebait/features/ragebait/data/repositories/ragebait_repository_impl.dart';
import 'package:dartz/dartz.dart';
import 'ragebait_repository_impl_test.mocks.dart';

@GenerateMocks([ApiClient])
void main() {
  late RagebaitRepositoryImpl repository;
  late MockApiClient mockApiClient;

  setUp(() {
    mockApiClient = MockApiClient();
    repository = RagebaitRepositoryImpl(mockApiClient);
  });

  group('RagebaitRepositoryImpl', () {
    test('should return RagebaitPost when API call is successful', () async {
      // Arrange
      const topic = 'test topic';
      const expectedPost = RagebaitPost(id: 1, result: 'Test result');
      when(mockApiClient.generateRagebait({'topic': topic}))
          .thenAnswer((_) async => expectedPost);

      // Act
      final result = await repository.generateRagebait(topic);

      // Assert
      expect(result, Right(expectedPost));
      verify(mockApiClient.generateRagebait({'topic': topic})).called(1);
    });

    test('should return Exception when API call fails', () async {
      // Arrange
      const topic = 'test topic';
      when(mockApiClient.generateRagebait({'topic': topic}))
          .thenThrow(Exception('API Error'));

      // Act
      final result = await repository.generateRagebait(topic);

      // Assert
      expect(result.isLeft(), isTrue);
      result.fold(
        (e) => expect(e.toString(), contains('API Error')),
        (_) => fail('Should not be a Right'),
      );
      verify(mockApiClient.generateRagebait({'topic': topic})).called(1);
    });
  });
}
 