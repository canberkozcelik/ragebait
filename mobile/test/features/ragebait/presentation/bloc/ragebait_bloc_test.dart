import 'package:bloc_test/bloc_test.dart';
import 'package:dartz/dartz.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/annotations.dart';
import 'package:mockito/mockito.dart';
import 'package:ragebait/core/models/ragebait_post.dart';
import 'package:ragebait/features/ragebait/domain/repositories/ragebait_repository.dart';
import 'package:ragebait/features/ragebait/presentation/bloc/ragebait_bloc.dart';

@GenerateMocks([RagebaitRepository])
import 'ragebait_bloc_test.mocks.dart';

void main() {
  late MockRagebaitRepository mockRepository;
  late RagebaitBloc bloc;

  setUp(() {
    mockRepository = MockRagebaitRepository();
    bloc = RagebaitBloc(mockRepository);
  });

  const testTopic = 'test topic';
  final testPost = RagebaitPost(id: 1, result: 'test result');
  final testException = Exception('error');

  test('initial state is RagebaitState.initial()', () {
    expect(bloc.state, const RagebaitState.initial());
  });

  blocTest<RagebaitBloc, RagebaitState>(
    'emits [loading, success] when repository returns post',
    build: () {
      when(mockRepository.generateRagebait(testTopic))
          .thenAnswer((_) async => Right(testPost));
      return bloc;
    },
    act: (bloc) => bloc.add(const RagebaitEvent.generate(testTopic)),
    expect: () => [
      const RagebaitState.loading(),
      RagebaitState.success(testPost),
    ],
    verify: (_) {
      verify(mockRepository.generateRagebait(testTopic)).called(1);
    },
  );

  blocTest<RagebaitBloc, RagebaitState>(
    'emits [loading, error] when repository returns failure',
    build: () {
      when(mockRepository.generateRagebait(testTopic))
          .thenAnswer((_) async => Left(testException));
      return bloc;
    },
    act: (bloc) => bloc.add(const RagebaitEvent.generate(testTopic)),
    expect: () => [
      const RagebaitState.loading(),
      RagebaitState.error(testException.toString()),
    ],
    verify: (_) {
      verify(mockRepository.generateRagebait(testTopic)).called(1);
    },
  );
} 