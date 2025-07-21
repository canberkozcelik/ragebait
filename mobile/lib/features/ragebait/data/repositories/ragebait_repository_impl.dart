import 'package:dartz/dartz.dart';
import 'package:injectable/injectable.dart';
import 'package:logger/logger.dart';
import '../../../../core/models/ragebait_post.dart';
import '../../../../core/network/api_client.dart';
import '../../domain/repositories/ragebait_repository.dart';

@Injectable(as: RagebaitRepository)
class RagebaitRepositoryImpl implements RagebaitRepository {
  final ApiClient _apiClient;
  final _logger = Logger();

  RagebaitRepositoryImpl(this._apiClient);

  @override
  Future<Either<Exception, RagebaitPost>> generateRagebait(String topic) async {
    try {
      _logger.i('Generating ragebait for topic: $topic');
      final response = await _apiClient.generateRagebait({'topic': topic});
      _logger.i('Received response: ${response.result}');
      return Right(response);
    } on Exception catch (e) {
      _logger.e('Error generating ragebait', error: e);
      return Left(e);
    }
  }
} 