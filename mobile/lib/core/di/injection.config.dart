// GENERATED CODE - DO NOT MODIFY BY HAND

// **************************************************************************
// InjectableConfigGenerator
// **************************************************************************

// ignore_for_file: type=lint
// coverage:ignore-file

// ignore_for_file: no_leading_underscores_for_library_prefixes
import 'package:dio/dio.dart' as _i3;
import 'package:get_it/get_it.dart' as _i1;
import 'package:injectable/injectable.dart' as _i2;

import '../../features/ragebait/data/repositories/ragebait_repository_impl.dart'
    as _i6;
import '../../features/ragebait/domain/repositories/ragebait_repository.dart'
    as _i5;
import '../../features/ragebait/presentation/bloc/ragebait_bloc.dart' as _i7;
import '../network/api_client.dart' as _i4;

extension GetItInjectableX on _i1.GetIt {
// initializes the registration of main-scope dependencies inside of GetIt
  _i1.GetIt init({
    String? environment,
    _i2.EnvironmentFilter? environmentFilter,
  }) {
    final gh = _i2.GetItHelper(
      this,
      environment,
      environmentFilter,
    );
    final networkModule = _$NetworkModule();
    gh.singleton<_i3.Dio>(() => networkModule.dio());
    gh.factory<String>(
      () => networkModule.baseUrl,
      instanceName: 'baseUrl',
    );
    gh.lazySingleton<_i4.ApiClient>(() => networkModule.apiClient(
          gh<_i3.Dio>(),
          gh<String>(instanceName: 'baseUrl'),
        ));
    gh.factory<_i5.RagebaitRepository>(
        () => _i6.RagebaitRepositoryImpl(gh<_i4.ApiClient>()));
    gh.factory<_i7.RagebaitBloc>(
        () => _i7.RagebaitBloc(gh<_i5.RagebaitRepository>()));
    return this;
  }
}

class _$NetworkModule extends _i4.NetworkModule {}
