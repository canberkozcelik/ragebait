import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_animate/flutter_animate.dart';
import 'package:purchases_flutter/purchases_flutter.dart';
import '../../core/services/api_service.dart';
import '../../core/services/paywall_service.dart';
import '../../core/components/primary_button.dart';
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
      // Restore might fail silently but we still close if success?
      // Logic from before:
       if (success) {
         // handled in helper
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
        color: Colors.white,
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          const Text(
            'Unlock Unlimited Rage ⚡️',
            style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 16),
          _isLoading
              ? const CircularProgressIndicator()
              : _offering != null
                  ? Column(
                      children: [
                        ListTile(
                          title: Text(_offering!.storeProduct.title),
                          subtitle: Text(_offering!.storeProduct.description),
                          trailing: Text(_offering!.storeProduct.priceString),
                          onTap: _purchase,
                          shape: RoundedRectangleBorder(
                            side: const BorderSide(color: Colors.black12),
                            borderRadius: BorderRadius.circular(12),
                          ),
                        ),
                        const SizedBox(height: 16),
                        ElevatedButton(
                          onPressed: _isPurchasing ? null : _purchase,
                          style: ElevatedButton.styleFrom(
                            minimumSize: const Size(double.infinity, 50),
                            backgroundColor: Colors.black,
                            foregroundColor: Colors.white,
                          ),
                          child: _isPurchasing
                              ? const CircularProgressIndicator(color: Colors.white)
                              : const Text('Subscribe Now'),
                        ),
                        TextButton(
                          onPressed: _isPurchasing ? null : _restore,
                          child: const Text('Restore Purchases'),
                        ),
                      ],
                    )
                  : const Text('Failed to load offerings'),
          const SizedBox(height: 16),
        ],
      ),
    );
  }
}
