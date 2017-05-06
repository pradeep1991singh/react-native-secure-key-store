/**
 * React Native Secure Key Store
 * Store keys securely in iOS KeyChain
 * Ref: https://useyourloaf.com/blog/simple-iphone-keychain-access/
 */

#import "React/RCTUtils.h"
#import "RNSecureKeyStore.h"

@implementation RNSecureKeyStore

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}
RCT_EXPORT_MODULE()

static NSString *serviceName = @"RNSecureKeyStoreKeyChain";

- (NSMutableDictionary *)newSearchDictionary:(NSString *)identifier {
    NSMutableDictionary *searchDictionary = [[NSMutableDictionary alloc] init];

    [searchDictionary setObject:(id)kSecClassGenericPassword forKey:(id)kSecClass];

    NSData *encodedIdentifier = [identifier dataUsingEncoding:NSUTF8StringEncoding];
    [searchDictionary setObject:encodedIdentifier forKey:(id)kSecAttrGeneric];
    [searchDictionary setObject:encodedIdentifier forKey:(id)kSecAttrAccount];
    [searchDictionary setObject:serviceName forKey:(id)kSecAttrService];

    return searchDictionary;
}

- (NSString *)searchKeychainCopyMatching:(NSString *)identifier {
    NSMutableDictionary *searchDictionary = [self newSearchDictionary:identifier];

    // Add search attributes
    [searchDictionary setObject:(id)kSecMatchLimitOne forKey:(id)kSecMatchLimit];

    // Add search return types
    [searchDictionary setObject:(id)kCFBooleanTrue forKey:(id)kSecReturnData];

    NSDictionary *found = nil;
    CFTypeRef result = NULL;
    OSStatus status = SecItemCopyMatching((CFDictionaryRef)searchDictionary,
                                          (CFTypeRef *)&result);

    NSString *value = nil;
    found = (__bridge NSDictionary*)(result);
    if (found) {
        value = [[NSString alloc] initWithData:found encoding:NSUTF8StringEncoding];
    }
    return value;
}

- (BOOL)createKeychainValue:(NSString *)value forIdentifier:(NSString *)identifier {
    NSMutableDictionary *dictionary = [self newSearchDictionary:identifier];

    NSData *valueData = [value dataUsingEncoding:NSUTF8StringEncoding];
    [dictionary setObject:valueData forKey:(id)kSecValueData];

    OSStatus status = SecItemAdd((CFDictionaryRef)dictionary, NULL);

    if (status == errSecSuccess) {
        return YES;
    }
    return NO;
}

- (BOOL)updateKeychainValue:(NSString *)password forIdentifier:(NSString *)identifier {

    NSMutableDictionary *searchDictionary = [self newSearchDictionary:identifier];
    NSMutableDictionary *updateDictionary = [[NSMutableDictionary alloc] init];
    NSData *passwordData = [password dataUsingEncoding:NSUTF8StringEncoding];
    [updateDictionary setObject:passwordData forKey:(id)kSecValueData];

    OSStatus status = SecItemUpdate((CFDictionaryRef)searchDictionary,
                                    (CFDictionaryRef)updateDictionary);

    if (status == errSecSuccess) {
        return YES;
    }
    return NO;
}

- (void)deleteKeychainValue:(NSString *)identifier {
    NSMutableDictionary *searchDictionary = [self newSearchDictionary:identifier];
    SecItemDelete((CFDictionaryRef)searchDictionary);
}

NSError * secureKeyStoreError(NSString *errMsg)
{
    NSError *error = [NSError errorWithDomain:serviceName code:200 userInfo:@{@"Error reason": errMsg}];
    return error;
}

RCT_EXPORT_METHOD(set:(NSString *)key value:(NSString *)value
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        BOOL status = [self createKeychainValue: value forIdentifier: key];
        if (status) {
            resolve(@"key stored successfully");
        } else {
            BOOL status = [self updateKeychainValue: value forIdentifier: key];
            if (status) {
                resolve(@"key updated successfully");
            } else {
                reject(@"no_events", @"Not able to save key", secureKeyStoreError(@"Not able to save key"));
            }
        }
    }
    @catch (NSException *exception) {
        reject(@"no_events", @"Not able to save key", secureKeyStoreError(exception.reason));
    }
}

RCT_EXPORT_METHOD(get:(NSString *)key
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        NSString *value = [self searchKeychainCopyMatching:key];
        if (value == nil) {
            reject(@"no_events", @"Not able to find key", secureKeyStoreError(@"Not able to find key"));
        } else {
            resolve(value);
        }
    }
    @catch (NSException *exception) {
        reject(@"no_events", @"Not able to find key", secureKeyStoreError(exception.reason));
    }
}

RCT_EXPORT_METHOD(remove:(NSString *)key
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    @try {
        [self deleteKeychainValue:key];
        resolve(@"key removed successfully");
    }
    @catch(NSException *exception) {
        reject(@"no_events", @"Could not remove key from keychain", secureKeyStoreError(exception.reason));
    }
}

@end
