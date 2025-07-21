part of 'ragebait_bloc.dart';

@freezed
class RagebaitState with _$RagebaitState {
  const factory RagebaitState.initial() = _Initial;
  const factory RagebaitState.loading() = _Loading;
  const factory RagebaitState.success(RagebaitPost post) = _Success;
  const factory RagebaitState.error(String message) = _Error;
} 