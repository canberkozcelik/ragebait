import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:purchases_flutter/purchases_flutter.dart';
import '../config/environment.dart';

final paywallServiceProvider = Provider<PaywallService>((ref) {
  return PaywallService();
});

class PaywallService {
  static const _apiKey = Environment.revenueCatSdkKey;
  static const _entitlementId = 'Ragebait Pro'; // Ensure this matches your RevenueCat Entitlement Name

  Future<void> init() async {
    await Purchases.setLogLevel(LogLevel.debug);
    
    PurchasesConfiguration configuration = PurchasesConfiguration(_apiKey);
    await Purchases.configure(configuration);
  }

  Future<CustomerInfo> getCustomerInfo() async {
    return await Purchases.getCustomerInfo();
  }

  Future<bool> isUserPremium() async {
    try {
      CustomerInfo customerInfo = await Purchases.getCustomerInfo();
      return customerInfo.entitlements.all[_entitlementId]?.isActive ?? false;
    } on PlatformException catch (_) {
      // print("Error fetching customer info: $e");
      return false;
    }
  }

  Future<bool> purchasePackage(Package package) async {
    try {
      CustomerInfo customerInfo = await Purchases.purchasePackage(package);
      return customerInfo.entitlements.all[_entitlementId]?.isActive ?? false;
    } on PlatformException catch (e) {
      var errorCode = PurchasesErrorHelper.getErrorCode(e);
      if (errorCode != PurchasesErrorCode.purchaseCancelledError) {
         // print("Error purchasing: $e");
      }
      return false;
    }
  }

  Future<bool> restorePurchases() async {
    try {
      CustomerInfo customerInfo = await Purchases.restorePurchases();
      return customerInfo.entitlements.all[_entitlementId]?.isActive ?? false;
    } on PlatformException catch (_) {
       // print("Error restoring: $e");
      return false;
    }
  }

  Future<List<Package>> getCurrentOfferings() async {
    try {
      Offerings offerings = await Purchases.getOfferings();
      if (offerings.current != null && offerings.current!.availablePackages.isNotEmpty) {
        return offerings.current!.availablePackages;
      }
    } on PlatformException catch (_) {
       // print("Error fetching offerings: $e");
    }
    return [];
  }
}
