//
//  ViewController.swift
//  LocalAuth_iOS
//
//  Created by wannabewize on 2016. 3. 26..
//  Copyright © 2016년 VanillaStep. All rights reserved.
//

import UIKit

class ViewController: UIViewController, NSURLSessionDelegate {
   
   let serverAddress = "http://192.168.0.6:3000"
   
   var session : NSURLSession!
   
   func URLSession(session: NSURLSession, didReceiveChallenge challenge: NSURLAuthenticationChallenge, completionHandler: (NSURLSessionAuthChallengeDisposition, NSURLCredential?) -> Void) {
      print("didReceiveChallenge")
   }
   

   @IBAction func postTalk(sender: AnyObject) {
      let urlStr = "\(serverAddress)/talks"
      print("Trying to post new Talk. : ", urlStr)

      if let url = NSURL(string: urlStr) {
         let request = NSMutableURLRequest(URL: url)
         request.HTTPMethod = "post"
         let dataStr = "talk=newTalk"
         let data = dataStr.dataUsingEncoding(NSUTF8StringEncoding)!
         
         
//         let task = session.uploadTaskWithRequest(request, fromData: data)
         
         let task = session.uploadTaskWithRequest(request, fromData: data, completionHandler: { (data : NSData?, response : NSURLResponse?, error : NSError?) -> Void in
            if error != nil {
               print("Error : ", error)
               return
            }
            let dataStr = String(data:data!, encoding:NSUTF8StringEncoding)
            print("Data : ", dataStr)
         })
         task.resume()
      } else {
         print("URL Error : ", urlStr)
      }
   }
   
   @IBAction func login(sender: AnyObject) {
   }
   
   @IBAction func resolveTalks(sender: AnyObject) {
      let urlStr = "\(serverAddress)/talks"
      if let url = NSURL(string: urlStr) {

      }
   }
   
   override func viewDidLoad() {
      super.viewDidLoad()
      let config = NSURLSessionConfiguration.defaultSessionConfiguration()
//      session = NSURLSession(configuration: config)
      session = NSURLSession(configuration: config, delegate: self, delegateQueue: NSOperationQueue.mainQueue())
   }

   override func didReceiveMemoryWarning() {
      super.didReceiveMemoryWarning()
      // Dispose of any resources that can be recreated.
   }


}

