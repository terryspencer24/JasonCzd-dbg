//
//  DataService.swift
//  DigitalBoardGameApp
//
//  Created by Jason Casiday on 10/29/19.
//  Copyright © 2019 Jason Casiday. All rights reserved.
//
//  https://github.com/kishikawakatsumi/KeychainAccess

import Foundation
import KeychainAccess

class DataService {

    let defaults = UserDefaults.standard
    
    let keychain = Keychain(service: "com.dbg.tokens")
    
    let KEY_AUTHTOKEN = "authtoken"
    
    func getData(name: String) -> Any? {
        return defaults.object(forKey: name)
    }
    
    func setData(name: String, value: Any) {
        defaults.set(value, forKey: name)
    }
    
    func removeData(name: String) {
        defaults.removeObject(forKey: name)
    }
    
    func hasValidToken() -> Bool {
        // originally token had both value and creationdate, but that is no longer returned, so for now just return valid as having a token; the server will expire the token
        return getAuthToken() != nil
    }
    
    func getAuthTokenAge() -> Int {
        let dt:Date? = keychain[attributes:KEY_AUTHTOKEN]?.creationDate
        if (dt != nil) {
            return Calendar.current.dateComponents([.day], from: dt!, to:Date()).day!
        }
        return 9999
    }
    
    func getAuthToken() -> String? {
        return keychain[KEY_AUTHTOKEN]
    }
    
    func removeAuthToken() {
        keychain[KEY_AUTHTOKEN] = nil
    }
    
    func setAuthToken(token: String) {
        keychain[KEY_AUTHTOKEN] = token
    }

}

