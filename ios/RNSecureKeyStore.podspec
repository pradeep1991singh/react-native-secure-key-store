
Pod::Spec.new do |s|
  s.name         = "RNSecureKeyStore"
  s.version      = "1.0.0"
  s.summary      = "A package for secure storage on android and ios"
  s.description  = "A package for secure storage on android and ios. Stores using the keystore on Android devices, and the keychain on iOS devices."
  s.homepage     = "https://github.com/pradeep1991singh/react-native-secure-key-store#readme"
  s.license      = "MIT"
  # s.license    = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author       = { "author" => "pradeep1991singh" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/pradeep1991singh/react-native-secure-key-store", :tag => "master" }
  s.source_files = "**/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  #s.dependency "others"

end

  
