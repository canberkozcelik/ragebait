// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'ragebait_post.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
    'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#adding-getters-and-methods-to-our-models');

RagebaitPost _$RagebaitPostFromJson(Map<String, dynamic> json) {
  return _RagebaitPost.fromJson(json);
}

/// @nodoc
mixin _$RagebaitPost {
  int get id => throw _privateConstructorUsedError;
  String get result => throw _privateConstructorUsedError;

  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;
  @JsonKey(ignore: true)
  $RagebaitPostCopyWith<RagebaitPost> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $RagebaitPostCopyWith<$Res> {
  factory $RagebaitPostCopyWith(
          RagebaitPost value, $Res Function(RagebaitPost) then) =
      _$RagebaitPostCopyWithImpl<$Res, RagebaitPost>;
  @useResult
  $Res call({int id, String result});
}

/// @nodoc
class _$RagebaitPostCopyWithImpl<$Res, $Val extends RagebaitPost>
    implements $RagebaitPostCopyWith<$Res> {
  _$RagebaitPostCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? id = null,
    Object? result = null,
  }) {
    return _then(_value.copyWith(
      id: null == id
          ? _value.id
          : id // ignore: cast_nullable_to_non_nullable
              as int,
      result: null == result
          ? _value.result
          : result // ignore: cast_nullable_to_non_nullable
              as String,
    ) as $Val);
  }
}

/// @nodoc
abstract class _$$RagebaitPostImplCopyWith<$Res>
    implements $RagebaitPostCopyWith<$Res> {
  factory _$$RagebaitPostImplCopyWith(
          _$RagebaitPostImpl value, $Res Function(_$RagebaitPostImpl) then) =
      __$$RagebaitPostImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({int id, String result});
}

/// @nodoc
class __$$RagebaitPostImplCopyWithImpl<$Res>
    extends _$RagebaitPostCopyWithImpl<$Res, _$RagebaitPostImpl>
    implements _$$RagebaitPostImplCopyWith<$Res> {
  __$$RagebaitPostImplCopyWithImpl(
      _$RagebaitPostImpl _value, $Res Function(_$RagebaitPostImpl) _then)
      : super(_value, _then);

  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? id = null,
    Object? result = null,
  }) {
    return _then(_$RagebaitPostImpl(
      id: null == id
          ? _value.id
          : id // ignore: cast_nullable_to_non_nullable
              as int,
      result: null == result
          ? _value.result
          : result // ignore: cast_nullable_to_non_nullable
              as String,
    ));
  }
}

/// @nodoc
@JsonSerializable()
class _$RagebaitPostImpl implements _RagebaitPost {
  const _$RagebaitPostImpl({required this.id, required this.result});

  factory _$RagebaitPostImpl.fromJson(Map<String, dynamic> json) =>
      _$$RagebaitPostImplFromJson(json);

  @override
  final int id;
  @override
  final String result;

  @override
  String toString() {
    return 'RagebaitPost(id: $id, result: $result)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$RagebaitPostImpl &&
            (identical(other.id, id) || other.id == id) &&
            (identical(other.result, result) || other.result == result));
  }

  @JsonKey(ignore: true)
  @override
  int get hashCode => Object.hash(runtimeType, id, result);

  @JsonKey(ignore: true)
  @override
  @pragma('vm:prefer-inline')
  _$$RagebaitPostImplCopyWith<_$RagebaitPostImpl> get copyWith =>
      __$$RagebaitPostImplCopyWithImpl<_$RagebaitPostImpl>(this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$RagebaitPostImplToJson(
      this,
    );
  }
}

abstract class _RagebaitPost implements RagebaitPost {
  const factory _RagebaitPost(
      {required final int id,
      required final String result}) = _$RagebaitPostImpl;

  factory _RagebaitPost.fromJson(Map<String, dynamic> json) =
      _$RagebaitPostImpl.fromJson;

  @override
  int get id;
  @override
  String get result;
  @override
  @JsonKey(ignore: true)
  _$$RagebaitPostImplCopyWith<_$RagebaitPostImpl> get copyWith =>
      throw _privateConstructorUsedError;
}
