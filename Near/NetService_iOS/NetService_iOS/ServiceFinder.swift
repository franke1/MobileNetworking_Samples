import Foundation

let SERVICE_TYPE = "_http._tcp."
let SERVICE_PORT = 3000

protocol ServiceFindDelegate {
   func serviceInfoChanged(index : Int)
   func serviceListResolved()
}

class ServiceFindManager : NSObject, NSNetServiceBrowserDelegate, NSNetServiceDelegate {
   
   var serviceBrowser : NSNetServiceBrowser!
   var isServiceSearching : Bool = false
   var delegate : ServiceFindDelegate!
   
   override init() {
      super.init()
      serviceBrowser = NSNetServiceBrowser()
      serviceBrowser.delegate = self
   }
   
   func findService() {
      if isServiceSearching == false {
         services.removeAll()
         serviceBrowser.searchForServicesOfType(SERVICE_TYPE, inDomain: "local")
      }
   }
   
   func stopFindService() {
      if isServiceSearching {
         serviceBrowser.stop()
      }
   }
   
   func netServiceBrowserWillSearch(browser: NSNetServiceBrowser) {
      isServiceSearching = true
      print("netServiceBrowserWillSearch")
   }
   
   func netServiceBrowserDidStopSearch(browser: NSNetServiceBrowser) {
      isServiceSearching = false
      print("netServiceBrowserDidStopSearch")
   }
   
   func netServiceBrowser(browser: NSNetServiceBrowser, didNotSearch errorDict: [String : NSNumber]) {
      print("didNotSearch")
   }
   
   func netServiceBrowser(browser: NSNetServiceBrowser, didFindDomain domainString: String, moreComing: Bool) {
      print("didFindDomain")
   }
   func netServiceBrowser(browser: NSNetServiceBrowser, didFindService service: NSNetService, moreComing: Bool) {
      print("didFindService")
      if services.indexOf(service) == nil {
         services.append(service)
      }
      
      if service.addresses?.count == 0 {
         service.resolveWithTimeout(100)
         service.delegate = self
      }
      
      if moreComing == false {
         browser.stop()
         delegate.serviceListResolved()
      }
   }
   func netServiceBrowser(browser: NSNetServiceBrowser, didRemoveDomain domainString: String, moreComing: Bool) {
      print("didRemoveDomain")
   }
   func netServiceBrowser(browser: NSNetServiceBrowser, didRemoveService service: NSNetService, moreComing: Bool) {
      print("didRemoveService")
   }
   
   /*
    *  NetServiceDeleagte
    */
   func netServiceDidResolveAddress(sender: NSNetService) {
      print("netServiceDidResolveAddress")
      if let index = services.indexOf(sender) {
         print("service info chagned : \(index)")
         delegate.serviceInfoChanged(index)
      }
   }
   
   func netServiceWillResolve(sender: NSNetService) {
      print("netServiceWillResolve")
   }
   
   var services = [NSNetService]()
   func numberOfService() -> Int {
      return services.count
   }
   
   func serviceAtIndex(index : Int) -> NSNetService {
      return services[index]
   }
}