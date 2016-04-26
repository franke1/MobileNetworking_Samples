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

class ViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, ServiceFindDelegate {

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
   
   override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
      if "SERVICE_CONNECT_SEGUE" == segue.identifier {
         let cell = sender as! UITableViewCell
         let index = tableView.indexPathForCell(cell)!
         let service = serviceManager.serviceAtIndex(index.row)
         
         let serviceVC = segue.destinationViewController as! ServiceViewController
         serviceVC.selectedService = service
      }
   }
   
   var serviceManager : ServiceFindManager!
   
   override func viewDidLoad() {
      super.viewDidLoad()
      serviceManager = ServiceFindManager()
      serviceManager.delegate = self
   }

   override func didReceiveMemoryWarning() {
      super.didReceiveMemoryWarning()
      // Dispose of any resources that can be recreated.
   }
}



