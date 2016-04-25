//
//  ViewController.swift
//  NetService_iOS
//
//  Created by Jaehoon Lee on 2016. 4. 25..
//  Copyright © 2016년 Jaehoon Lee. All rights reserved.
//

import UIKit

extension NSData {
   // http://stackoverflow.com/questions/29294491/swift-obtaining-ip-address-from-socket-returns-weird-value
   func getHost() -> String {
      let addrP = UnsafePointer<sockaddr>(self.bytes)
      let addr = addrP.memory
      if addr.sa_family == UInt8(AF_INET) {
         var addr4 = UnsafePointer<sockaddr_in>(self.bytes).memory
         var buf : [CChar] = [CChar](count:16, repeatedValue:0)
         inet_ntop(AF_INET, &addr4.sin_addr, &buf, 16)
         if let addr = String(CString: buf, encoding:NSASCIIStringEncoding) {
            return addr
         }
         else {
            return "Address is not avaialbe"
         }
      }
      else {
         let addr6 = UnsafePointer<sockaddr_in6>(self.bytes).memory
         return "IP6 Address is not available"
      }

   }
}

class ViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, ServiceManagerDelegate {

   @IBOutlet weak var tableView: UITableView!
   
   @IBAction func findService(sender: AnyObject) {
      serviceManager.findService()
   }
   
   @IBAction func stopServiceFind(sender: AnyObject) {
      serviceManager.stopFindService()
   }
   
   func serviceListResolved() {
      tableView.reloadData()
   }
   
   func serviceInfoChanged(index: Int) {
      let indexPath = NSIndexPath(forRow: index, inSection: 0)
      tableView.reloadRowsAtIndexPaths([indexPath], withRowAnimation: .Automatic)
   }   
   
   @IBAction func makeNewService(sender: AnyObject) {
   }
   
   @IBAction func stopService(sender: AnyObject) {
   }
   
   func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
      return serviceManager.numberOfService()
   }
   
   func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
      let cell = tableView.dequeueReusableCellWithIdentifier("ServiceCell", forIndexPath: indexPath)
      
      let service : NSNetService = serviceManager.serviceAtIndex(indexPath.row)
      cell.textLabel?.text = service.name
      var addressStr = "not available"

      if service.addresses?.count > 0 {
         let data = service.addresses![0]
         addressStr = data.getHost()
      }
      cell.detailTextLabel?.text = addressStr
      
      return cell
      
   }
   
   var serviceManager : ServiceManager!
   
   override func viewDidLoad() {
      super.viewDidLoad()
      serviceManager = ServiceManager()
      serviceManager.delegate = self
   }

   override func didReceiveMemoryWarning() {
      super.didReceiveMemoryWarning()
      // Dispose of any resources that can be recreated.
   }


}

let SERVICE_TYPE = "_http._tcp."

protocol ServiceManagerDelegate {
   func serviceInfoChanged(index : Int)
   func serviceListResolved()
}

class ServiceManager : NSObject, NSNetServiceBrowserDelegate, NSNetServiceDelegate {

   var serviceBrowser : NSNetServiceBrowser!
   var isServiceSearching : Bool = false
   var delegate : ServiceManagerDelegate!
   
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

