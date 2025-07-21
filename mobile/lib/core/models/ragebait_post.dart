import 'package:freezed_annotation/freezed_annotation.dart';

part 'ragebait_post.freezed.dart';
part 'ragebait_post.g.dart';

@freezed
class RagebaitPost with _$RagebaitPost {
  const factory RagebaitPost({
    required int id,
    required String result,
  }) = _RagebaitPost;

  factory RagebaitPost.fromJson(Map<String, dynamic> json) =>
      _$RagebaitPostFromJson(json);
} 