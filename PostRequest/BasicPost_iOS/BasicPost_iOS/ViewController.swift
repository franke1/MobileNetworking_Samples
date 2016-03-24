//
//  ViewController.swift
//  BasicPost_iOS
//
//  Created by wannabewize on 2016. 3. 24..
//  Copyright © 2016년 VanillaStep. All rights reserved.
//

import UIKit

class ViewController: UIViewController, UIWebViewDelegate {

   @IBOutlet weak var webView: UIWebView!
   @IBOutlet weak var serverAddress: UITextField!
   @IBOutlet weak var movieTitle: UITextField!
   @IBOutlet weak var movieDirector: UITextField!
   
   var session : NSURLSession!
   
   @IBAction func postMovieInfo(sender: AnyObject) {
      if let title = movieTitle.text,
         let director = movieDirector.text,
         let address = serverAddress.text,
         let url = NSURL(string: address) {
         print("Post title : \(title), director : \(director)")
         
         let request = NSMutableURLRequest(URL: url)
         request.HTTPMethod = "POST"
         let data = "title=\(title)&director=\(director)".dataUsingEncoding(NSUTF8StringEncoding)!
         
         let task = session.uploadTaskWithRequest(request, fromData: data, completionHandler: { (data : NSData?, response : NSURLResponse?, error : NSError?) in
            if error != nil {
               print("Error : ", error)
               return
            }
            print("Response : ", response, " Data : ", data)
            self.refreshInfo()
         })
         task.resume()
      }
      
   }
   @IBAction func refreshInfo() {
      let address = serverAddress.text!
      if let url = NSURL(string: address) {
         print("Refresh : ", address)
         let request = NSURLRequest(URL: url)
         webView.loadRequest(request)
      }
      else {
         print("Can not make url : ", address)
      }
   }
   
   override func viewDidLoad() {
      super.viewDidLoad()
      let config = NSURLSessionConfiguration.defaultSessionConfiguration()
      session = NSURLSession(configuration: config)
   }
   
   func webView(webView: UIWebView, didFailLoadWithError error: NSError?) {
      print("Error : ", error)
   }
   
   func webViewDidStartLoad(webView: UIWebView) {
      print("Webview Start Loading")
   }

   override func didReceiveMemoryWarning() {
      super.didReceiveMemoryWarning()
      // Dispose of any resources that can be recreated.
   }


}

