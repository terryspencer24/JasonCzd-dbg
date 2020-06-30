//
//  GenericSocketController.swift
//  DigitalBoardGameApp
//
//  Created by Jason Casiday on 11/16/19.
//  Copyright © 2019 Jason Casiday. All rights reserved.
//

import UIKit

class GenericSocketController: GenericGameController {
    
    var sock = SocketService()
    
    var data = DataService()
    
    override func viewDidLoad() {
        connectToSocket()
        NotificationCenter.default.addObserver(self, selector: #selector(willResignActive), name: UIApplication.didEnterBackgroundNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(willEnterForeground), name: UIApplication.willEnterForegroundNotification, object: nil)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        sock.disconnect(intentional: true)
        AppDelegate.AppUtility.clearLock()
    }
    
    @objc func willResignActive(_ notification: Notification) {
        sock.disconnect(intentional: true)
        AppDelegate.AppUtility.clearLock()
    }
    
    @objc func willEnterForeground(_ notification: Notification) {
        connectToSocket()
    }
    
    func connectToSocket() {
        if !self.sock.online {
            self.sock.connect(token: self.data.getAuthToken()!, dest: getDestReceive(), cb: getMsgHandler(), concb: getErrHandler())
        }
    }
    
    func getDestReceive() -> String {
        preconditionFailure("This method must be overridden")
    }
    
    func getMsgHandler() -> (Dictionary<String, Any>) -> Void {
        preconditionFailure("This method must be overridden")
    }
    
    func getErrHandler() -> (Bool) -> Void {
        preconditionFailure("This method must be overridden")
    }
    
    func send(obj: Dictionary<String, Any>) {
        print("sending \(obj)")
        if let theJSONData = try?  JSONSerialization.data(withJSONObject: obj),
            let theJSONText = String(data: theJSONData, encoding: String.Encoding.ascii) {
//                print("JSON string = \n\(theJSONText)")
                self.sock.sendMessage(dest: getDestSend(), msg: theJSONText)
            }
    }
    
    func getDestSend() -> String {
        preconditionFailure("This method must be overridden")
    }
    
}
