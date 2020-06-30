//
//  SocketService.swift
//  DigitalBoardGameApp
//
//  Created by Jason Casiday on 11/2/19.
//  Copyright © 2019 Jason Casiday. All rights reserved.
//

import Foundation
import StompClientLib

class SocketService: StompClientLibDelegate {
    
    let socketClient = StompClientLib()
    
    let url = NSURL(string: Prop.SOCK)!
    
    var online: Bool = false
    
    var token: String = ""
    
    var dest: String = ""
    
    var cb: ((Dictionary<String, Any>) -> Void)?
    
    var concb: ((Bool) -> Void)?
    
    var reconnectInited: Bool = false
    
    var timer : Timer?
    
    func connect(token: String, dest: String, cb: @escaping (Dictionary<String, Any>) -> Void, concb: @escaping (Bool) -> Void) {
        self.token = token
        self.dest = dest
        self.cb = cb
        self.concb = concb
        openSocket()
    }
    
    func openSocket() {
//        print("Connecting... \(socketClient.isConnected())")
        socketClient.openSocketWithURLRequest(request: NSURLRequest(url: url as URL), delegate: self,
                                              connectionHeaders: ["Authorization": token])
    }
    
    func stompClient(client: StompClientLib!, didReceiveMessageWithJSONBody jsonBody: AnyObject?, akaStringBody stringBody: String?, withHeader header: [String : String]?, withDestination destination: String) {
//        print("Message received!")
//        print("Destination : \(destination)")
//        print("JSON Body : \(String(describing: jsonBody))")
//        print("String Body : \(stringBody ?? "nil")")
        cb!(jsonBody as! Dictionary<String, Any>)
    }
    
    func stompClientDidDisconnect(client: StompClientLib!) {
        online = false
        disconnect(intentional: false)
    }
    
    func disconnect(intentional: Bool) {
//        print("Disconnecting... \(socketClient.isConnected())")
        if (socketClient.isConnected()) {
            if intentional && self.timer != nil {
                timer?.invalidate()
                timer = nil
            }
            socketClient.disconnect()
            concb!(false)
        }
    }
    
    func stompClientDidConnect(client: StompClientLib!) {
        online = true
        subscribe(dest: self.dest)
        if !reconnectInited {
            self.timer = Timer.scheduledTimer(withTimeInterval: 4.0, repeats: true, block: { _ in
                if (!self.online) {
                  self.openSocket()
                }
            })
//            socketClient.reconnect(request: NSURLRequest(url: url as URL), delegate: self, connectionHeaders: ["Authorization": token], time: 4.0)
            reconnectInited = true
        }
        concb!(true)
    }
    
    func subscribe(dest: String) {
        socketClient.subscribeWithHeader(destination: dest, withHeader: [
                "Authorization": self.token,
                "id": dest
            ])
    }
    
    func sendMessage(dest: String, msg: String) {
        var headers = [String: String]()
        headers["content-type"] = "application/json"
        headers["Authorization"] = self.token
//        print("Sending to \(dest) message \(msg)")
        socketClient.sendMessage(message: msg, toDestination: dest, withHeaders: headers, withReceipt: nil)
    }
    
    func serverDidSendReceipt(client: StompClientLib!, withReceiptId receiptId: String) {
    }
    
    func serverDidSendError(client: StompClientLib!, withErrorMessage description: String, detailedErrorMessage message: String?) {
        // TODO surface error
        print("Socket Error: \(String(describing: description)) :: \(String(describing: message))")
        if (description.contains("Connection refused")) {
            socketClient.disconnect()
        }
    }
    
    func serverDidSendPing() {
        print("Server ping")
    }
    
}
