const path = require("path");

const packagePath = path.resolve(__dirname, "../");

module.exports = {
  resolver: {
    extraNodeModules: {
      "react-native-secure-key-store": packagePath,
    },
  },
  watchFolders: [packagePath],
};
