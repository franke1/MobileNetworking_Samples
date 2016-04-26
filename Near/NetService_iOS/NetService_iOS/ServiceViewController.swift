//
//  ServiceViewController.swift
//  NetService_iOS
//
//  Created by Jaehoon Lee on 2016. 4. 26..
//  Copyright © 2016년 Jaehoon Lee. All rights reserved.
//

import UIKit

class ServiceViewController: UIViewController, NSNetServiceDelegate, OtherDelegate {
   
   @IBOutlet weak var userInput: UITextField!
   @IBOutlet weak var messageView: UITextView!
   var other : Other!
   
   var selectedService : NSNetService!
   
   override func viewDidLoad() {
      super.viewDidLoad()
   }

   
   override func viewDidAppear(animated: Bool) {
      if selectedService == nil {
         prepareServer()
      }
      else {
         connectToService()
      }
   }
   
   func prepareServer() {
      let alert = UIAlertController(title: "New Chat", message: "Input ChatRoom Name", preferredStyle: .Alert)
      let okAction = UIAlertAction(title: "OK", style: .Default) { (action : UIAlertAction) in
         if let textField = alert.textFields?[0] {
            let serviceName = textField.text!
            self.startService(serviceName)
         }
      }
      let cancelAction = UIAlertAction(title: "Cancel", style: .Cancel, handler:nil)
      alert.addAction(okAction)
      alert.addAction(cancelAction)
      alert.addTextFieldWithConfigurationHandler(nil)
      
      self.showViewController(alert, sender: nil)
   }
   
   var service : NSNetService!
   
   func startService(serviceName : String) {
      if service != nil {
         service.stop()
      }
      
      service = NSNetService(domain: "local", type: SERVICE_TYPE, name: serviceName, port: Int32(SERVICE_PORT))
      
      service.delegate = self
      service.publishWithOptions(NSNetServiceOptions.ListenForConnections)
   }
   
   @IBAction func sendMessage(sender: AnyObject) {
      let message = userInput.text
      if other != nil {
         messageView.text.appendContentsOf("\nMe >> \(message!)")
         other.sendMessage(message!)
         userInput.text = ""
      }
   }
      
   @IBAction func finishService(sender: AnyObject) {
      if other != nil {
         other.disconnect()
      }
      self.dismissViewControllerAnimated(true, completion: nil)
   }
   func connectToService() {
      var inputStream : NSInputStream?
      var outputStream : NSOutputStream?
      let ret = selectedService.getInputStream(&inputStream, outputStream: &outputStream)
      if ret && inputStream != nil && outputStream != nil {
         other = Other(inputStream: inputStream!, outputStream: outputStream!)
         other.messageHandler = { self.messageView.text.appendContentsOf("\nOther >> \($0)")}
      }
      else {
         messageView.text.appendContentsOf("Can not connect")
         print("Can not connect")
      }
   }
   
   override func didReceiveMemoryWarning() {
      super.didReceiveMemoryWarning()
      // Dispose of any resources that can be recreated.
   }

   func netServiceWillPublish(sender: NSNetService) {
      print("netServiceWillPublish")
   }
   
   func netServiceDidPublish(sender: NSNetService) {
      print("netServiceDidPublish")
      messageView.text.appendContentsOf("\nService Published")
   }

   func netServiceDidStop(sender: NSNetService) {
      print("netServiceDidStop")
      messageView.text.appendContentsOf("\nService Did Stop")
   }
   
   func netService(sender: NSNetService, didNotPublish errorDict: [String : NSNumber]) {
      print("didNotPublish")
   }
   
   func netService(sender: NSNetService, didAcceptConnectionWithInputStream inputStream: NSInputStream, outputStream: NSOutputStream) {
      print("didAcceptConnection")
      messageView.text.appendContentsOf("Client Connected")
      other = Other(inputStream: inputStream, outputStream: outputStream)
      other.messageHandler = { self.messageView.text.appendContentsOf("\nOther >> \($0)")}
   }
   
   func messageArrived(message: String) {
      messageView.text.appendContentsOf(message)
   }
}
