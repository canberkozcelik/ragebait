import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:injectable/injectable.dart';
import 'package:logger/logger.dart';
import '../../domain/repositories/ragebait_repository.dart';
import '../../../../core/models/ragebait_post.dart';

part 'ragebait_event.dart';
part 'ragebait_state.dart';
part 'ragebait_bloc.freezed.dart';

@injectable
class RagebaitBloc extends Bloc<RagebaitEvent, RagebaitState> {
  final RagebaitRepository _repository;
  final _logger = Logger();

  RagebaitBloc(this._repository) : super(const RagebaitState.initial()) {
    on<RagebaitEvent>((event, emit) async {
      await event.map(
        generate: (e) => _onGenerate(e, emit),
      );
    });
  }

  Future<void> _onGenerate(_Generate event, Emitter<RagebaitState> emit) async {
    _logger.i('Generating ragebait for topic: ${event.topic}');
    emit(const RagebaitState.loading());
    
    final result = await _repository.generateRagebait(event.topic);
    
    result.fold(
      (failure) {
        _logger.e('Error generating ragebait', error: failure);
        emit(RagebaitState.error(failure.toString()));
      },
      (post) {
        _logger.i('Successfully generated ragebait: ${post.result}');
        emit(RagebaitState.success(post));
      },
    );
  }
} 