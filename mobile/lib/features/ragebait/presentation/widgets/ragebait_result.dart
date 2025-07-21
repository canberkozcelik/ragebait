import 'package:flutter/material.dart';
import '../../../../core/models/ragebait_post.dart';

class RagebaitResult extends StatelessWidget {
  final RagebaitPost post;

  const RagebaitResult({
    super.key,
    required this.post,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Generated Content:',
              style: Theme.of(context).textTheme.titleMedium,
            ),
            const SizedBox(height: 16),
            Text(
              post.result,
              style: Theme.of(context).textTheme.bodyLarge,
            ),
          ],
        ),
      ),
    );
  }
} 