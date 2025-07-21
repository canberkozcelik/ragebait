import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../bloc/ragebait_bloc.dart';

class RagebaitForm extends StatefulWidget {
  const RagebaitForm({super.key});

  @override
  State<RagebaitForm> createState() => _RagebaitFormState();
}

class _RagebaitFormState extends State<RagebaitForm> {
  final _formKey = GlobalKey<FormState>();
  final _topicController = TextEditingController();

  @override
  void dispose() {
    _topicController.dispose();
    super.dispose();
  }

  void _submitForm() {
    if (_formKey.currentState?.validate() ?? false) {
      context.read<RagebaitBloc>().add(
            RagebaitEvent.generate(_topicController.text.trim()),
          );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Form(
      key: _formKey,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          TextFormField(
            controller: _topicController,
            decoration: const InputDecoration(
              labelText: 'Topic',
              hintText: 'Enter a topic for ragebait content',
              border: OutlineInputBorder(),
            ),
            validator: (value) {
              if (value == null || value.trim().isEmpty) {
                return 'Please enter a topic';
              }
              return null;
            },
          ),
          const SizedBox(height: 16),
          ElevatedButton(
            onPressed: _submitForm,
            child: const Text('Generate Ragebait'),
          ),
        ],
      ),
    );
  }
} 