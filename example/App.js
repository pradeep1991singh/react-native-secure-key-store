/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 * @lint-ignore-every XPLATJSCOPYRIGHT1
 */

import React, {Component} from 'react';
import {Platform, StyleSheet, Text, View} from 'react-native';

import RNSecureKeyStore, {ACCESSIBLE} from "react-native-secure-key-store";

const instructions = Platform.select({
  ios: 'Press Cmd+R to reload,\n' + 'Cmd+D or shake for dev menu',
  android:
    'Double tap R on your keyboard to reload,\n' +
    'Shake or press menu button for dev menu',
});

type Props = {};
export default class App extends Component<Props> {
  render() {

    RNSecureKeyStore.set("key1", "value1", {accessible: ACCESSIBLE.ALWAYS_THIS_DEVICE_ONLY})
      .then((res) => {
        console.log(res);
      }, (err) => {
        console.log(err);
      });

    RNSecureKeyStore.set("key2", "value2", {accessible: ACCESSIBLE.ALWAYS_THIS_DEVICE_ONLY})
      .then((res) => {
        console.log(res);
      }, (err) => {
        console.log(err);
      });

    RNSecureKeyStore.get("key1")
      .then((res) => {
        console.log(res);
      }, (err) => {
        console.log(err);
      });

    RNSecureKeyStore.get("key2")
      .then((res) => {
        console.log(res);
      }, (err) => {
        console.log(err);
      });

    RNSecureKeyStore.remove("key1")
      .then((res) => {
        console.log(res);
      }, (err) => {
        console.log(err);
      });

    RNSecureKeyStore.remove("key2")
      .then((res) => {
        console.log(res);
      }, (err) => {
        console.log(err);
      });

    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>Welcome to React Native!</Text>
        <Text style={styles.instructions}>To get started, edit App.js</Text>
        <Text style={styles.instructions}>{instructions}</Text>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});
