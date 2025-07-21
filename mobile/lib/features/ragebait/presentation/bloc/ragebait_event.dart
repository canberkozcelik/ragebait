part of 'ragebait_bloc.dart';
 
@freezed
class RagebaitEvent with _$RagebaitEvent {
  const factory RagebaitEvent.generate(String topic) = _Generate;
} 