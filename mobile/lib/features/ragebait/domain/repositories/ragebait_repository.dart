import 'package:dartz/dartz.dart';
import '../../../../core/models/ragebait_post.dart';
 
abstract class RagebaitRepository {
  Future<Either<Exception, RagebaitPost>> generateRagebait(String topic);
} 