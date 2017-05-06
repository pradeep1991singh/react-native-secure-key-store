
# react-native-secure-key-store

## Getting started

`$ npm install react-native-secure-key-store --save`

### Mostly automatic installation

`$ react-native link react-native-secure-key-store`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-secure-key-store` and add `RNSecureKeyStore.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNSecureKeyStore.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNSecureKeyStorePackage;` to the imports at the top of the file
  - Add `new RNSecureKeyStorePackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-secure-key-store'
  	project(':react-native-secure-key-store').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-secure-key-store/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-secure-key-store')
  	```

#### Windows
[Read it! :D](https://github.com/ReactWindows/react-native)

1. In Visual Studio add the `RNSecureKeyStore.sln` in `node_modules/react-native-secure-key-store/windows/RNSecureKeyStore.sln` folder to their solution, reference from their app.
2. Open up your `MainPage.cs` app
  - Add `using Com.Reactlibrary.RNSecureKeyStore;` to the usings at the top of the file
  - Add `new RNSecureKeyStorePackage()` to the `List<IReactPackage>` returned by the `Packages` method


## Usage
```javascript
import RNSecureKeyStore from 'react-native-secure-key-store';

// TODO: What to do with the module?
RNSecureKeyStore;
```
  