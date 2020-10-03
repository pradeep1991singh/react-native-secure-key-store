/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 * @lint-ignore-every XPLATJSCOPYRIGHT1
 */

import React, {Component} from 'react';
import {Platform, StyleSheet, Button, Text, TextInput, View} from 'react-native';

import RNSecureKeyStore, {ACCESSIBLE} from "react-native-secure-key-store";

type Props = {};
export default class App extends Component<Props> {
  state = {
    alias: 'hello',
    value: 'world'
  };

  getValue() {
    RNSecureKeyStore.get(this.state.alias)
      .then((value) => {
        this.setState({
          value,
        });
      })
      .catch(console.error);
  }

  setValue() {
    RNSecureKeyStore.set(this.state.alias, this.state.value, {})
      .then(() => this.getValue())
      .catch(console.error);
  }

  removeValue() {
    RNSecureKeyStore.remove(this.state.alias)
      .then(() => this.getValue())
      .catch(console.error);
  }

  render() {
    return (
      <View style={styles.container}>
        <View style={styles.row}>
          <Text>Alias:</Text>
          <TextInput
            style={styles.textInput}
            onChangeText={alias => this.setState({alias})}
            value={this.state.alias}
          />
        </View>

        <View style={styles.row}>
          <Text>Value:</Text>
          <TextInput
            style={styles.textInput}
            onChangeText={value => this.setState({value})}
            value={this.state.value}
          />
        </View>

        <View style={styles.row}>
          <View style={styles.button}>
            <Button
              onPress={() => this.getValue()}
              title='Get'
              />
          </View>
          <View style={styles.button}>
            <Button
              onPress={() => this.setValue()}
              title='Set'
              />
          </View>
          <View style={styles.button}>
            <Button
              onPress={() => this.removeValue()}
              title='Remove'
              />
          </View>
        </View>

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
  row: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: 20
  },
  textInput: {
    height: 40,
    width: 200,
    borderColor: 'gray',
    borderWidth: 1,
    marginLeft: 10
  },
  button: {
    marginLeft: 10
  }
});
