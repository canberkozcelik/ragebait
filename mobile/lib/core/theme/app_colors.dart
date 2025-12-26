import 'package:flutter/material.dart';

class AppColors {
  // Backgrounds
  static const Color background = Color(0xFF0A0A0A); // Deep Black
  static const Color surface = Color(0xFF161616); // Slightly lighter for cards
  static const Color surfaceHighlight = Color(0xFF252525);

  // Primary Gradient (Rage/Energy)
  static const Color primaryStart = Color(0xFFFF3B30); // Vibrant Red
  static const Color primaryEnd = Color(0xFFFF9500); // Orange
  
  // Text
  static const Color textPrimary = Color(0xFFFFFFFF);
  static const Color textSecondary = Color(0xFFAAAAAA);
  static const Color textDim = Color(0xFF666666);

  // Status
  static const Color error = Color(0xFFFF453A);
  static const Color success = Color(0xFF32D74B);

  // Gradients
  static const LinearGradient primaryGradient = LinearGradient(
    colors: [primaryStart, primaryEnd],
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
  );
  
  static const LinearGradient glassGradient = LinearGradient(
    colors: [Color(0x1AFFFFFF), Color(0x0DFFFFFF)],
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
  );
}
