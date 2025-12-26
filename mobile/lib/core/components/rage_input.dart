import 'package:flutter/material.dart';
import '../theme/app_colors.dart';

class RageInput extends StatelessWidget {
  final TextEditingController controller;
  final String hintText;
  final int maxLines;
  final VoidCallback? onSubmit;

  const RageInput({
    super.key,
    required this.controller,
    required this.hintText,
    this.maxLines = 1,
    this.onSubmit,
  });

  @override
  Widget build(BuildContext context) {
    return TextField(
      controller: controller, // Expects controller to be managed by parent
      maxLines: maxLines,
      style: const TextStyle(
        fontSize: 18,
        color: AppColors.textPrimary,
        height: 1.5,
      ),
      cursorColor: AppColors.primaryStart,
      decoration: InputDecoration(
        hintText: hintText,
        alignLabelWithHint: true,
      ),
      onSubmitted: (_) => onSubmit?.call(),
    );
  }
}
