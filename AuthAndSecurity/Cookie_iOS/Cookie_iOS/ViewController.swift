//
//  ViewController.swift
//  Cookie_iOS
//
//  Created by wannabewize on 2016. 3. 25..
//  Copyright © 2016년 VanillaStep. All rights reserved.
//

import UIKit

class ViewController: UIViewController {
   
   @IBOutlet weak var resultTextView: UITextView!
   let serverAddress = "http://192.168.0.6:3000/"
   
   @IBAction func sendRequest(sender: AnyObject) {
      if let url = NSURL(string: serverAddress),
         let data = NSData(contentsOfURL: url) {
            let resultStr = String(data: data, encoding: NSUTF8StringEncoding)!
            print("result : ", resultStr)
            self.resultTextView.text = "\(self.resultTextView.text)\n\(resultStr)"
      }
      else {
         print("URL Error")
      }
   }
   
   @IBAction func sendRequest2(sender: AnyObject) {
      if let url = NSURL(string: serverAddress) {
         let request = NSURLRequest(URL: url)
         let task = session.dataTaskWithRequest(request, completionHandler: { (data : NSData?, response : NSURLResponse?, error : NSError?) -> Void in
            if error != nil {
               print("Error : ", error)
               return
            }
            
            dispatch_async(dispatch_get_main_queue(), { () -> Void in
               let resultStr = String(data: data!, encoding: NSUTF8StringEncoding)!
               self.resultTextView.text = "\(self.resultTextView.text)\n\(resultStr)"
               print("Result : ", resultStr)
            })
         })
         task.resume()
         
      }
   }
   
   var session : NSURLSession!
   override func viewDidLoad() {
      super.viewDidLoad()
      let config = NSURLSessionConfiguration.defaultSessionConfiguration()
      session = NSURLSession(configuration: config)
      // Do any additional setup after loading the view, typically from a nib.
   }
   
   override func didReceiveMemoryWarning() {
      super.didReceiveMemoryWarning()
      // Dispose of any resources that can be recreated.
   }
   
   
}

