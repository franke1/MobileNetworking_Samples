//
//  ViewController.swift
//  PhotoUpload_iOS
//
//  Created by wannabewize on 2016. 3. 25..
//  Copyright © 2016년 VanillaStep. All rights reserved.
//

import UIKit
import Alamofire
import SafariServices

class ViewController: UIViewController, UIImagePickerControllerDelegate, UINavigationControllerDelegate {

   @IBOutlet weak var serverAddress: UITextField!
   @IBOutlet weak var imageView: UIImageView!
   @IBOutlet weak var titleTextField: UITextField!
   
   @IBAction func showResult(sender: AnyObject) {
      let urlStr = serverAddress.text!
      if let url = NSURL(string: urlStr) {
         let safari = SFSafariViewController(URL: url)
         self.presentViewController(safari, animated: true, completion: nil)
      }
   }
   
   @IBAction func selectImage(sender: AnyObject) {
      let picker = UIImagePickerController()
      picker.delegate = self
      self.presentViewController(picker, animated: true, completion: nil)
   }
   
   func imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : AnyObject]) {
      if let image = info[UIImagePickerControllerOriginalImage] as? UIImage {
         imageView.image = image
      }
      picker.dismissViewControllerAnimated(true, completion: nil)
   }
   
   @IBAction func uploadInfo(sender: AnyObject) {
      let urlStr = serverAddress.text!
      let title = titleTextField.text!
      
      Alamofire.upload(.POST, urlStr, multipartFormData: { (formData : MultipartFormData) in
         let imageData = UIImagePNGRepresentation(self.imageView.image!)!
         formData.appendBodyPart(data: imageData, name: "poster", fileName: "file.jpg", mimeType: "image/jpg")
         print("image data : ", imageData.length)
         
         let titleData = title.dataUsingEncoding(NSUTF8StringEncoding)!
         formData.appendBodyPart(data: titleData, name: "title")
         
         
         }, encodingCompletion: { (encodingResult : Manager.MultipartFormDataEncodingResult) in
            print("encoding complete : ", encodingResult)
         }
      )
   }
   
   var session : NSURLSession!
   
   override func viewDidLoad() {
      super.viewDidLoad()
      
      let config = NSURLSessionConfiguration.defaultSessionConfiguration()
      session = NSURLSession(configuration: config)
      
   }

   override func didReceiveMemoryWarning() {
      super.didReceiveMemoryWarning()
      // Dispose of any resources that can be recreated.
   }


}

