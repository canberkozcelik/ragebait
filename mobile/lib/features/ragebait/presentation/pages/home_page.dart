import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../bloc/ragebait_bloc.dart';
import '../widgets/ragebait_form.dart';
import '../widgets/ragebait_result.dart';

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Ragebait Generator'),
        centerTitle: true,
      ),
      body: BlocBuilder<RagebaitBloc, RagebaitState>(
        builder: (context, state) {
          return Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                const RagebaitForm(),
                const SizedBox(height: 24),
                Expanded(
                  child: state.map(
                    initial: (_) => const Center(
                      child: Text('Enter a topic to generate ragebait content'),
                    ),
                    loading: (_) => const Center(
                      child: CircularProgressIndicator(),
                    ),
                    success: (state) => RagebaitResult(post: state.post),
                    error: (state) => Center(
                      child: Text(
                        state.message,
                        style: const TextStyle(color: Colors.red),
                      ),
                    ),
                  ),
                ),
              ],
            ),
          );
        },
      ),
    );
  }
} 