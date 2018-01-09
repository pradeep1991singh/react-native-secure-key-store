
# react-native-secure-key-store

React Native Library for securely storing keys to iOS and Android devices in KeyChain and KeyStore respectively

## Getting started

```sh
$ npm install react-native-secure-key-store --save
```
or

```sh
$ yarn add react-native-secure-key-store
```

### Mostly automatic installation

```sh
$ react-native link react-native-secure-key-store
```

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-secure-key-store` and add `RNSecureKeyStore.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNSecureKeyStore.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.securekeystore.RNSecureKeyStorePackage;` to the imports at the top of the file
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

## Usage

```javascript
import RNSecureKeyStore from 'react-native-secure-key-store';

// For storing key
RNSecureKeyStore.set("key1", "value1")
	.then((res) => {
		console.log(res);
	}, (err) => {
		console.log(err);
	});

// For retrieving key
RNSecureKeyStore.get("key1")
	.then((res) => {
		console.log(res);
	}, (err) => {
		console.log(err);
	});

// For removing key
RNSecureKeyStore.remove("key1")
	.then((res) => {
		console.log(res);
	}, (err) => {
		console.log(err);
	});		
```
- For demo app, checkout example directory.

## Testing

For Testing using Jest, add RNSecureKeyStoreMock implementation under your __test__/__mocks__ folder.
This mock implementation makes easy for you to make testing that dependes on react-native-secure-key-store

## License

ISC License (ISC)
Copyright (c) 2016 pradeep singh

Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
  
