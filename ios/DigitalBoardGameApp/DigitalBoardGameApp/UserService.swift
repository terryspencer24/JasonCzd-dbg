//
//  UserService.swift
//  DigitalBoardGameApp
//
//  Created by Jason Casiday on 11/4/19.
//  Copyright © 2019 Jason Casiday. All rights reserved.
//

import Foundation

class UserService {
    
    let data = DataService()
    
    let api = ApiService()
    
    func load(cb: @escaping (HTTPURLResponse, Array<Dictionary<String, Any>>) -> Void, ecb: @escaping (Data?, URLResponse?, Error?) -> Void) {
        api.getauth(url: "\(Prop.URL)/api/users", method: "GET", token: data.getAuthToken()!, obj: nil, cb: {(res, json) in
                if (res.statusCode == 200) {
                    let userid = json[0]["id"] as! Int
                    let username = json[0]["username"] as! String
                    self.data.setData(name: Prop.DATA_USERID, value: userid)
                    self.data.setData(name: Prop.DATA_USERNAME, value: username)
                } else {
                    self.clear(full: res.statusCode != 401)
                }
                cb(res, json)
        }, ecb: {(data, response, error) in
            ecb(data, response, error)
        })
    }
    
    func clear(full: Bool) {
        self.data.removeData(name: Prop.DATA_USERID)
        self.data.removeData(name: Prop.DATA_USERNAME)
        if (full) {
            self.api.getauth(url: "\(Prop.URL)/api/users/logout", method: "GET", token: self.data.getAuthToken()!, obj: nil, cb: {(res, json) in
                self.data.removeAuthToken()
            }, ecb: {(data, repsonse, error) in
                self.data.removeAuthToken()
            })
        }
    }
    
    func id() -> Int? {
        return data.getData(name: Prop.DATA_USERID) as? Int
    }
    
    func username() -> String? {
        return data.getData(name: Prop.DATA_USERNAME) as? String
    }
    
}
