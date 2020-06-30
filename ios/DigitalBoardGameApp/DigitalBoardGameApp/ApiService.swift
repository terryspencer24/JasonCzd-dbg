//
//  ApiService.swift
//  DigitalBoardGameApp
//
//  Created by Jason Casiday on 10/29/19.
//  Copyright © 2019 Jason Casiday. All rights reserved.
//

import Foundation

class ApiService {
    
    static let sharedInstance = ApiService()
    
    func getauth(url: String, method: String?, token: String?, obj: Any?, cb: @escaping (HTTPURLResponse, Array<Dictionary<String, Any>>) -> Void, ecb: @escaping (Data?, URLResponse?, Error?) -> Void) {
        
        var request = URLRequest(url: URL(string: url)!, cachePolicy: .useProtocolCachePolicy, timeoutInterval: 10.0)
        request.httpMethod = "GET"
        if (method != nil) {
            request.httpMethod = method
        }
        if (token != nil) {
            request.addValue("Bearer " + token!, forHTTPHeaderField: "Authorization")
        }
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        if (obj != nil) {
            do {
                request.httpBody = try JSONSerialization.data(withJSONObject: obj!, options: .prettyPrinted)
            } catch let error {
                // TODO how to send this up..
                print(error.localizedDescription)
            }
        }
        
        let config = URLSessionConfiguration.default
        let session = URLSession(configuration: config)
        let task = session.dataTask(with: request, completionHandler: {(data, response, error) in
            if error != nil {
                ecb(data, response, error)
            } else {
                let res : HTTPURLResponse = response as! HTTPURLResponse
                var json = try? JSONSerialization.jsonObject(with: data!, options: []) as? Array<Dictionary<String, Any>>
                if json == nil {
                    json = Array<Dictionary<String, Any>>()
                    do {
                        let singlejson = try JSONSerialization.jsonObject(with: data!, options: []) as? [String : Any]
                        if singlejson != nil {
                            json?.append(singlejson!)
                        }
                    } catch _ {}
                }
                cb(res, json!)
            }
        })
        task.resume()
    }
    
}
