declare module 'react-native-secure-key-store' {
  export enum ACCESSIBLE {
    AFTER_FIRST_UNLOCK = 'AccessibleAfterFirstUnlock',
    AFTER_FIRST_UNLOCK_THIS_DEVICE_ONLY =
      'AccessibleAfterFirstUnlockThisDeviceOnly',
    ALWAYS = 'AccessibleAlways',
    ALWAYS_THIS_DEVICE_ONLY = 'AccessibleAlwaysThisDeviceOnly',
    WHEN_PASSCODE_SET_THIS_DEVICE_ONLY =
      'AccessibleWhenPasscodeSetThisDeviceOnly',
    WHEN_UNLOCKED = 'AccessibleWhenUnlocked',
    WHEN_UNLOCKED_THIS_DEVICE_ONLY = 'AccessibleWhenUnlockedThisDeviceOnly',
  }

  interface RNSecureKeyStore {
    get: (key: string) => Promise<any>
    set: (key: string, value: string, options?: { accessible?: ACCESSIBLE }) => Promise<any>
    remove: (key: string) => Promise<any>
    setResetOnAppUninstallTo: (enabled: boolean) => boolean
  }
  const secureKeystore: RNSecureKeyStore

  export default secureKeystore
}
