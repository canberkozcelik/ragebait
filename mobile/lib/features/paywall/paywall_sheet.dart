import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:purchases_flutter/purchases_flutter.dart';
import '../../core/services/api_service.dart';
import '../../core/services/paywall_service.dart';
import '../../core/theme/app_colors.dart';

 class PaywallSheet extends ConsumerStatefulWidget {
  const PaywallSheet({super.key});

  @override
  ConsumerState<PaywallSheet> createState() => _PaywallSheetState();
}

class _PaywallSheetState extends ConsumerState<PaywallSheet> {
  Package? _offering;
  bool _isLoading = true;
  bool _isPurchasing = false;

  @override
  void initState() {
    super.initState();
    _fetchOfferings();
  }

  Future<void> _fetchOfferings() async {
    final packages = await ref.read(paywallServiceProvider).getCurrentOfferings();
    if (mounted) {
      setState(() {
        _offering = packages.isNotEmpty ? packages.first : null;
        _isLoading = false;
      });
    }
  }

  Future<void> _purchase() async {
    if (_offering == null) return;
    
    setState(() => _isPurchasing = true);
    
    final success = await ref.read(paywallServiceProvider).purchasePackage(_offering!);
    
    if (mounted) {
      if (success) {
        await _handleSuccessfulPurchase();
      }
      
      setState(() => _isPurchasing = false);
    }
  }

  Future<void> _restore() async {
    setState(() => _isPurchasing = true);
    final success = await ref.read(paywallServiceProvider).restorePurchases();

    if (mounted) {
      if (success) {
        await _handleSuccessfulPurchase();
      }

       setState(() => _isPurchasing = false);
      
       if (!mounted) return;

       if (success) {
         // success
       } else {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('No active subscriptions found')),
        );
      }
    }
  }

  // Extracted Success Logic
  Future<void> _handleSuccessfulPurchase() async {
     await ref.read(apiServiceProvider).syncPremiumStatus();
     
     if (mounted) {
        Navigator.pop(context, true);
     }
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(24.0),
      decoration: const BoxDecoration(
        color: AppColors.surface, // Dark background
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          const Text(
            'Unlock Unlimited Rage ⚡️',
            style: TextStyle(
              fontSize: 24, 
              fontWeight: FontWeight.bold,
              color: AppColors.textPrimary, // White text
            ),
          ),
          const SizedBox(height: 16),
          _isLoading
              ? const CircularProgressIndicator(color: AppColors.textPrimary)
              : _offering != null
                  ? Column(
                      children: [
                        ListTile(
                          title: Text(
                            _offering!.storeProduct.title,
                            style: const TextStyle(color: AppColors.textPrimary, fontWeight: FontWeight.bold),
                          ),
                          subtitle: Text(
                            _offering!.storeProduct.description,
                            style: const TextStyle(color: AppColors.textSecondary),
                          ),
                          trailing: Text(
                            _offering!.storeProduct.priceString,
                            style: const TextStyle(color: AppColors.textPrimary, fontSize: 16, fontWeight: FontWeight.bold),
                          ),
                          onTap: _purchase,
                          shape: RoundedRectangleBorder(
                            side: const BorderSide(color: Colors.white12), // Subtle border
                            borderRadius: BorderRadius.circular(12),
                          ),
                          tileColor: Colors.white10, // Slight glass effect
                        ),
                        const SizedBox(height: 24),
                        
                        // Subscribe Button
                        Container(
                          decoration: BoxDecoration(
                            gradient: AppColors.primaryGradient,
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: ElevatedButton(
                            onPressed: _isPurchasing ? null : _purchase,
                            style: ElevatedButton.styleFrom(
                              minimumSize: const Size(double.infinity, 50),
                              backgroundColor: Colors.transparent, // Use container gradient
                              shadowColor: Colors.transparent,
                              foregroundColor: Colors.white,
                              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                            ),
                            child: _isPurchasing
                                ? const CircularProgressIndicator(color: Colors.white)
                                : const Text(
                                    'Subscribe Now',
                                    style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                                  ),
                          ),
                        ),
                        
                        const SizedBox(height: 12),
                        TextButton(
                          onPressed: _isPurchasing ? null : _restore,
                          child: const Text(
                            'Restore Purchases',
                            style: TextStyle(color: AppColors.textDim),
                          ),
                        ),
                      ],
                    )
                  : const Text(
                      'Failed to load offerings',
                      style: TextStyle(color: AppColors.error),
                    ),
          const SizedBox(height: 16),
        ],
      ),
    );
  }
}
