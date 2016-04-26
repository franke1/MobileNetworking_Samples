//
//  Other.swift
//  NetService_iOS
//
//  Created by Jaehoon Lee on 2016. 4. 27..
//  Copyright © 2016년 Jaehoon Lee. All rights reserved.
//

import Foundation

protocol OtherDelegate {
   func messageArrived(message : String)
}

class Other : NSObject, NSStreamDelegate {
   
   var messageHandler : ((String)->Void)!
   var outputStream : NSOutputStream!
   var inputStream : NSInputStream!
   
   init(inputStream : NSInputStream, outputStream : NSOutputStream) {
      super.init()
      
      self.inputStream = inputStream
      inputStream.delegate = self
      inputStream.scheduleInRunLoop(NSRunLoop.currentRunLoop(), forMode: NSDefaultRunLoopMode)
      inputStream.open()
      
      self.outputStream = outputStream
      outputStream.delegate = self
      outputStream.scheduleInRunLoop(NSRunLoop.currentRunLoop(), forMode: NSDefaultRunLoopMode)
      outputStream.open()
   }
   
   func disconnect() {
      if inputStream != nil {
         inputStream.close()
      }
      
      if outputStream != nil {
         outputStream.close()
      }
   }
   
   func sendMessage(message : String) -> Void {
      if let data = message.dataUsingEncoding(NSUTF8StringEncoding) {
         let buf = UnsafePointer<UInt8>(data.bytes)
         outputStream.write(buf, maxLength: data.length)
      }
   }
   
   func stream(aStream: NSStream, handleEvent eventCode: NSStreamEvent) {
      switch eventCode {
      case NSStreamEvent.None:
         print("None")
      case NSStreamEvent.OpenCompleted:
         print("OpenCompleted")
         if messageHandler != nil {
            messageHandler("Connected!")
         }
      case NSStreamEvent.HasBytesAvailable:
         print("HasBytesAvailable")
         readData(aStream as! NSInputStream)
      case NSStreamEvent.HasSpaceAvailable:
         print("HasSpaceAvailable")
      case NSStreamEvent.ErrorOccurred:
         print("ErrorOccurred")
      case NSStreamEvent.EndEncountered:
         print("EndEncountered")
      default:
         print("What the hell")
      }
   }
   
   let MAX_BUFFER_LENGTH = 1024 * 10
   func readData(stream : NSInputStream) {
      var buffer = Array<UInt8>(count: MAX_BUFFER_LENGTH, repeatedValue: 0)
      let length = stream.read(&buffer, maxLength: MAX_BUFFER_LENGTH)
      if length > 0 && messageHandler != nil {
         if let message = String(bytes: buffer, encoding: NSUTF8StringEncoding) {
            messageHandler(message)
         }
         else {
            print("Can not decode message")
         }
      }
   }
}