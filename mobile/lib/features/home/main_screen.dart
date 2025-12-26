import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../core/components/glass_card.dart';
import '../../core/components/primary_button.dart';
import '../../core/components/rage_input.dart';
import '../../core/services/api_service.dart';
import '../../core/services/auth_service.dart';
import '../../core/services/paywall_service.dart';
import '../../core/theme/app_colors.dart';
import '../paywall/paywall_sheet.dart';

class MainScreen extends ConsumerStatefulWidget {
  const MainScreen({super.key});

  @override
  ConsumerState<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends ConsumerState<MainScreen> {
  final TextEditingController _topicController = TextEditingController();
  String? _generatedResult;
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _initApp();
  }

  Future<void> _initApp() async {
    // 1. Silent Login
    final user = ref.read(authServiceProvider).currentUser;
    if (user == null) {
      await ref.read(authServiceProvider).signInAnonymously();
    }
    
    // 2. Init RevenueCat
    await ref.read(paywallServiceProvider).init();
  }

  void _generateRagebait() async {
    final topic = _topicController.text.trim();
    if (topic.isEmpty) return;

    // Dismiss keyboard
    FocusScope.of(context).unfocus();

    setState(() {
      _isLoading = true;
      _generatedResult = null;
    });

    try {
      final result = await ref.read(apiServiceProvider).generateRagebait(topic);
      setState(() {
        _generatedResult = result;
      });
    } on QuotaExceededException {
      if (mounted) {
        _showPaywall();
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text("Error: ${e.toString()}")),
        );
      }
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  void _showPaywall() async {
    final success = await showModalBottomSheet<bool>(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (context) => const PaywallSheet(),
    );

    if (success == true) {
       if (!mounted) return;
       // Retry generation automatically if they purchased? 
       // For now, just let them tap the button again.
       ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("You are now UNLIMITED! 🔥")),
        );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: const BoxDecoration(
          color: AppColors.background,
        ),
        child: SafeArea(
          child: ListView(
            padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 40),
            children: [
              // HEADER
              const Text(
                "RAGEBAIT",
                style: TextStyle(
                  fontSize: 27,
                  fontFamily: 'Impact',
                  fontWeight: FontWeight.w900,
                  height: 0.9,
                  letterSpacing: -1,
                  color: Colors.white,
                ),
              ).animate().fadeIn(duration: 600.ms).slideY(begin: -0.2, end: 0),
              
              const SizedBox(height: 10),
              Text(
                "Create viral engagement with one tap.",
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: AppColors.textSecondary,
                ),
              ).animate().fadeIn(delay: 200.ms),

              const SizedBox(height: 60),

              // INPUT CARD
              GlassCard(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      "WHAT'S THE TOPIC?",
                      style: Theme.of(context).textTheme.labelSmall?.copyWith(
                        color: AppColors.textDim,
                        fontWeight: FontWeight.bold,
                        letterSpacing: 1.2,
                      ),
                    ),
                    const SizedBox(height: 10),
                    RageInput(
                      controller: _topicController,
                      hintText: "e.g. Pineapple Pizza, Remote Work...",
                      maxLines: 2,
                      onSubmit: _generateRagebait,
                    ),
                  ],
                ),
              ).animate().fadeIn(delay: 400.ms).slideY(begin: 0.2, end: 0),

              const SizedBox(height: 30),

              // ACTION BUTTON
              PrimaryButton(
                text: "GENERATE",
                isLoading: _isLoading,
                onPressed: _generateRagebait,
              ).animate().fadeIn(delay: 600.ms).scale(),

              const SizedBox(height: 40),

              // RESULT AREA
              if (_generatedResult != null)
                GlassCard(
                  padding: const EdgeInsets.symmetric(vertical: 30, horizontal: 24),
                  child: Column(
                    children: [
                      Text(
                        _generatedResult!,
                        textAlign: TextAlign.center,
                        style: const TextStyle(
                          fontSize: 22,
                          fontWeight: FontWeight.bold,
                          height: 1.4,
                          color: AppColors.textPrimary,
                        ),
                      ),
                      const SizedBox(height: 20),
                      TextButton.icon(
                        onPressed: () {
                           // Clipboard logic would go here
                        },
                        icon: const Icon(Icons.copy, size: 18),
                        label: const Text("Copy Text"),
                        style: TextButton.styleFrom(
                          foregroundColor: AppColors.textSecondary,
                        ),
                      )
                    ],
                  ),
                ).animate().fadeIn().shimmer(duration: 1.seconds),
            ],
          ),
        ),
      ),
    );
  }
}
