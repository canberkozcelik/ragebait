import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:mockito/mockito.dart';
import 'package:mockito/annotations.dart';
import 'package:purchases_flutter/purchases_flutter.dart';
import 'package:ragebait/core/services/api_service.dart';
import 'package:ragebait/core/services/paywall_service.dart';
import 'package:ragebait/features/paywall/paywall_sheet.dart';

// Generate Mocks
@GenerateMocks([PaywallService, ApiService, Package, StoreProduct])
import 'paywall_sheet_test.mocks.dart';

void main() {
  late MockPaywallService mockPaywallService;
  late MockApiService mockApiService;
  late MockPackage mockPackage;
  late MockStoreProduct mockStoreProduct;

  setUp(() {
    mockPaywallService = MockPaywallService();
    mockApiService = MockApiService();
    mockPackage = MockPackage();
    mockStoreProduct = MockStoreProduct();

    // Default Stubs
    // Setup Mock Package chain: package.storeProduct -> mockStoreProduct
    when(mockPackage.storeProduct).thenReturn(mockStoreProduct);
    when(mockStoreProduct.title).thenReturn('Monthly Access');
    when(mockStoreProduct.description).thenReturn('Unlock everything');
    when(mockStoreProduct.priceString).thenReturn('\$2.99');
  });

  Widget createSubject() {
    return ProviderScope(
      overrides: [
        paywallServiceProvider.overrideWithValue(mockPaywallService),
        apiServiceProvider.overrideWithValue(mockApiService),
      ],
      child: const MaterialApp(
        home: Scaffold(
          body: PaywallSheet(),
        ),
      ),
    );
  }

  group('PaywallSheet', () {
    testWidgets('shows loading indicator initially', (tester) async {
      // Arrange
      // Delay the response to verify loading state
      when(mockPaywallService.getCurrentOfferings()).thenAnswer(
        (_) async {
            await Future.delayed(const Duration(milliseconds: 100));
            return [];
        }
      );

      // Act
      await tester.pumpWidget(createSubject());

      // Assert
      expect(find.byType(CircularProgressIndicator), findsOneWidget);
      await tester.pumpAndSettle(); // Finish the future
    });

    testWidgets('shows offerings when loaded', (tester) async {
      // Arrange
      when(mockPaywallService.getCurrentOfferings()).thenAnswer((_) async => [mockPackage]);

      // Act
      await tester.pumpWidget(createSubject());
      await tester.pumpAndSettle();

      // Assert
      expect(find.text('Monthly Access'), findsOneWidget);
      expect(find.text('Unlock everything'), findsOneWidget);
      expect(find.text('\$2.99'), findsOneWidget);
      expect(find.text('Subscribe Now'), findsOneWidget);
    });

    testWidgets('calls purchase and sync on subscribe tap', (tester) async {
       // Arrange
      when(mockPaywallService.getCurrentOfferings()).thenAnswer((_) async => [mockPackage]);
      when(mockPaywallService.purchasePackage(mockPackage)).thenAnswer((_) async => true);
      when(mockApiService.syncPremiumStatus()).thenAnswer((_) async {}); // Future<void>

      // Act
      await tester.pumpWidget(createSubject());
      await tester.pumpAndSettle();

      // Find 'Subscribe Now' button. It might be in a ElevatedButton or similar.
      // Based on code: ElevatedButton > Text('Subscribe Now')
      await tester.tap(find.text('Subscribe Now'));
      await tester.pump(); // Start purchase (set isPurchasing=true)
      
      // We might see a loading spinner on the button now
      // await tester.pump(const Duration(seconds: 1)); // Wait for purchase
      await tester.pumpAndSettle(); // Wait for navigation pop

      // Assert
      verify(mockPaywallService.purchasePackage(mockPackage)).called(1);
      verify(mockApiService.syncPremiumStatus()).called(1);
      expect(find.byType(PaywallSheet), findsNothing); // Should be popped
    });
  });
}
