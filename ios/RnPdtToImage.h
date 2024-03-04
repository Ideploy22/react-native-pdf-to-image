
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNRnPdtToImageSpec.h"

@interface RnPdtToImage : NSObject <NativeRnPdtToImageSpec>
#else
#import <React/RCTBridgeModule.h>

@interface RnPdtToImage : NSObject <RCTBridgeModule>
#endif

@end
