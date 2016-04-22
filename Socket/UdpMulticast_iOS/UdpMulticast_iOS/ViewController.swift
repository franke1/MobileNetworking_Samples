//
//  ViewController.swift
//  UdpMulticast_iOS
//
//  Created by wannabewize on 2016. 4. 22..
//  Copyright © 2016년 VanillaStep. All rights reserved.
//

import UIKit
import CocoaAsyncSocket

class ViewController: UIViewController, AsyncUdpSocketDelegate, GCDAsyncUdpSocketDelegate {
   
   //  1024-49151
   // range 239.0.0.0 to 239.255.255.255 as this is reserved for private use.
   let MULTICAST_PORT : UInt16 = 49001
   let MULTICAST_ADDRESS = "239.255.255.255"
   
   @IBOutlet var messageView : UITextView!
   @IBOutlet weak var inputTextField: UITextField!
   
   @IBAction func sendMessage(sender: AnyObject) {
      let message = inputTextField.text
      if let data = message?.dataUsingEncoding(NSUTF8StringEncoding) {
         if socket2 != nil {
            socket2.sendData(data, toHost: MULTICAST_ADDRESS, port: MULTICAST_PORT, withTimeout: -1, tag: 0)
         }
         if socket != nil {
            socket.sendData(data, toHost: MULTICAST_ADDRESS, port: MULTICAST_PORT, withTimeout: -1, tag: 0)
         }
      }
   }
   
   var socket : AsyncUdpSocket!
   var socket2 : GCDAsyncUdpSocket!
   
   override func viewWillAppear(animated: Bool) {
      messageView.text.appendContentsOf("\nMulticast packet receiving from \(MULTICAST_ADDRESS)")
//      readyUdpSocket()
      readyGCDUdpSocket()
   }
   
   func readyGCDUdpSocket() {
      socket2 = GCDAsyncUdpSocket(delegate: self, delegateQueue: dispatch_get_main_queue())
      do {
         try socket2.bindToPort(MULTICAST_PORT)
         try socket2.joinMulticastGroup(MULTICAST_ADDRESS)
         try socket2.beginReceiving()
       
         messageView.text.appendContentsOf("\nLocal Address : \(socket2.localHost())")
      }
      catch let exception {
         print("Socket Exception : \(exception)")
      }
   }
   
   func udpSocket(sock: GCDAsyncUdpSocket!, didSendDataWithTag tag: Int) {
      print("didSendDataWithTag")
   }
   
   func udpSocket(sock: GCDAsyncUdpSocket!, didNotConnect error: NSError!) {
      print("didNotConnect")
   }
   
   func udpSocketDidClose(sock: GCDAsyncUdpSocket!, withError error: NSError!) {
      print("udpSocketDidClose : \(error)")
   }
   
   func udpSocket(sock: GCDAsyncUdpSocket!, didConnectToAddress address: NSData!) {
      print("didConnectToAddress")
   }
   
   func udpSocket(sock: GCDAsyncUdpSocket!, didNotSendDataWithTag tag: Int, dueToError error: NSError!) {
      print("didNotSendDataWithTag")
   }
   
   func udpSocket(sock: GCDAsyncUdpSocket!, didReceiveData data: NSData!, fromAddress address: NSData!, withFilterContext filterContext: AnyObject!) {
      print("didReceiveData data length : \(data.length)")
      
      if let message = String(data: data, encoding: NSUTF8StringEncoding) {
         let sender = GCDAsyncUdpSocket.hostFromAddress(address)
         messageView.text = "\(messageView.text)\n\(sender) >> \(message)"
      }
   }
   
   func readyUdpSocket() {
      socket = AsyncUdpSocket(delegate: self)
      do {
         try socket.bindToPort(MULTICAST_PORT)
         try socket.joinMulticastGroup(MULTICAST_ADDRESS)
         socket.receiveWithTimeout(-1, tag: 0)
         messageView.text.appendContentsOf("\nLocal Address : \(socket.connectedHost())")
      }
      catch let error as NSError {
         print("Socket Error : \(error)")
      }
   }
   
   func onUdpSocketDidClose(sock: AsyncUdpSocket!) {
      print("onUdpSocketDidClose")
   }
   
   func onUdpSocket(sock: AsyncUdpSocket!, didSendDataWithTag tag: Int) {
      print("didSendDataWithTag")
   }
   
   func onUdpSocket(sock: AsyncUdpSocket!, didNotSendDataWithTag tag: Int, dueToError error: NSError!) {
      print("didNotSendDataWithTag")
   }
   
   func onUdpSocket(sock: AsyncUdpSocket!, didNotReceiveDataWithTag tag: Int, dueToError error: NSError!) {
      print("didNotReceiveDataWithTag")
      
   }
   
   func onUdpSocket(sock: AsyncUdpSocket!, didReceiveData data: NSData!, withTag tag: Int, fromHost host: String!, port: UInt16) -> Bool {
      if let message = String(data: data, encoding: NSUTF8StringEncoding) {
         messageView.text = "\(messageView.text)\n\(host) >> \(message)"
      }
      return false
   }

   override func viewDidLoad() {
      super.viewDidLoad()
      // Do any additional setup after loading the view, typically from a nib.
   }

   override func didReceiveMemoryWarning() {
      super.didReceiveMemoryWarning()
      // Dispose of any resources that can be recreated.
   }


}

