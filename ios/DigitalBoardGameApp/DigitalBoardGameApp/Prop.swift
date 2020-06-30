//
//  Prop.swift
//  DigitalBoardGameApp
//
//  Created by Jason Casiday on 10/31/19.
//  Copyright © 2019 Jason Casiday. All rights reserved.
//

import Foundation

struct Prop {
    
    // static let HOST = "localhost:8080"
    // static let HOST = "tabletopster.com/game"
    
    //static let URL = "https://" + HOST
    static let URL = Bundle.main.object(forInfoDictionaryKey: "API_URL") as! String
    
    //static let SOCK = "wss://" + HOST + "/sock"
    static let SOCK = Bundle.main.object(forInfoDictionaryKey: "SOCK_URL") as! String
    
    static let DATA_USERID = "user.id"
    
    static let DATA_USERNAME = "user.username"
    
}
