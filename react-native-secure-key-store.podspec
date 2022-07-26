require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = package['name']
  s.version      = package['version']
  s.summary      = package['description']
  s.description  = package['description']
  s.homepage     = package['homepage']
  s.license      = package['license']
  s.authors      = package['author']
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/pradeep1991singh/react-native-secure-key-store", :tag => "master" }
  s.source_files = "ios/**/*.{h,m}"
  s.requires_arc = true


  s.dependency "React-Core"
  #s.dependency "others"

end

  
